package com.android.internal.app;

import android.app.ActivityManager;
import android.app.AppGlobals;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.IPackageManager;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.RemoteException;
import android.util.Log;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.app.ResolverActivity.DisplayResolveInfo;
import com.android.internal.app.ResolverActivity.ResolvedComponentInfo;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class ResolverListController {
    private static final boolean DEBUG = false;
    private static final String TAG = "ResolverListController";
    private boolean isComputed;
    private final Context mContext;
    private final int mLaunchedFromUid;
    private final String mReferrerPackage;
    private AbstractResolverComparator mResolverComparator;
    private final Intent mTargetIntent;
    private final PackageManager mpm;

    private class ComputeCallback implements AfterCompute {
        private CountDownLatch mFinishComputeSignal;

        public ComputeCallback(CountDownLatch finishComputeSignal) {
            this.mFinishComputeSignal = finishComputeSignal;
        }

        public void afterCompute() {
            this.mFinishComputeSignal.countDown();
        }
    }

    public ResolverListController(Context context, PackageManager pm, Intent targetIntent, String referrerPackage, int launchedFromUid) {
        this(context, pm, targetIntent, referrerPackage, launchedFromUid, new ResolverRankerServiceResolverComparator(context, targetIntent, referrerPackage, null));
    }

    public ResolverListController(Context context, PackageManager pm, Intent targetIntent, String referrerPackage, int launchedFromUid, AbstractResolverComparator resolverComparator) {
        this.isComputed = false;
        this.mContext = context;
        this.mpm = pm;
        this.mLaunchedFromUid = launchedFromUid;
        this.mTargetIntent = targetIntent;
        this.mReferrerPackage = referrerPackage;
        this.mResolverComparator = resolverComparator;
    }

    @VisibleForTesting
    public ResolveInfo getLastChosen() throws RemoteException {
        IPackageManager packageManager = AppGlobals.getPackageManager();
        Intent intent = this.mTargetIntent;
        return packageManager.getLastChosenActivity(intent, intent.resolveTypeIfNeeded(this.mContext.getContentResolver()), 65536);
    }

    @VisibleForTesting
    public void setLastChosen(Intent intent, IntentFilter filter, int match) throws RemoteException {
        AppGlobals.getPackageManager().setLastChosenActivity(intent, intent.resolveType(this.mContext.getContentResolver()), 65536, filter, match, intent.getComponent());
    }

    @VisibleForTesting
    public List<ResolvedComponentInfo> getResolversForIntent(boolean shouldGetResolvedFilter, boolean shouldGetActivityMetadata, List<Intent> intents) {
        List<ResolvedComponentInfo> resolvedComponents = null;
        int N = intents.size();
        for (int i = 0; i < N; i++) {
            Intent intent = (Intent) intents.get(i);
            int i2 = 0;
            int flags = 65536 | (shouldGetResolvedFilter ? 64 : 0);
            if (shouldGetActivityMetadata) {
                i2 = 128;
            }
            flags |= i2;
            if (intent.isWebIntent() || (intent.getFlags() & 2048) != 0) {
                flags |= 8388608;
            }
            List<ResolveInfo> infos = this.mpm.queryIntentActivities(intent, flags);
            for (int j = infos.size() - 1; j >= 0; j--) {
                ResolveInfo info = (ResolveInfo) infos.get(j);
                if (!(info.activityInfo == null || info.activityInfo.exported)) {
                    infos.remove(j);
                }
            }
            if (resolvedComponents == null) {
                resolvedComponents = new ArrayList();
            }
            addResolveListDedupe(resolvedComponents, intent, infos);
        }
        return resolvedComponents;
    }

    @VisibleForTesting
    public void addResolveListDedupe(List<ResolvedComponentInfo> into, Intent intent, List<ResolveInfo> from) {
        int fromCount = from.size();
        int intoCount = into.size();
        for (int i = 0; i < fromCount; i++) {
            ResolveInfo newInfo = (ResolveInfo) from.get(i);
            boolean found = false;
            for (int j = 0; j < intoCount; j++) {
                ResolvedComponentInfo rci = (ResolvedComponentInfo) into.get(j);
                if (isSameResolvedComponent(newInfo, rci)) {
                    found = true;
                    rci.add(intent, newInfo);
                    break;
                }
            }
            if (!found) {
                into.add(new ResolvedComponentInfo(new ComponentName(newInfo.activityInfo.packageName, newInfo.activityInfo.name), intent, newInfo));
            }
        }
    }

    @VisibleForTesting
    public ArrayList<ResolvedComponentInfo> filterIneligibleActivities(List<ResolvedComponentInfo> inputList, boolean returnCopyOfOriginalListIfModified) {
        ArrayList<ResolvedComponentInfo> listToReturn = null;
        for (int i = inputList.size() - 1; i >= 0; i--) {
            ActivityInfo ai = ((ResolvedComponentInfo) inputList.get(i)).getResolveInfoAt(0).activityInfo;
            if (ActivityManager.checkComponentPermission(ai.permission, this.mLaunchedFromUid, ai.applicationInfo.uid, ai.exported) != 0 || isComponentFiltered(ai.getComponentName())) {
                if (returnCopyOfOriginalListIfModified && listToReturn == null) {
                    listToReturn = new ArrayList(inputList);
                }
                inputList.remove(i);
            }
        }
        return listToReturn;
    }

    @VisibleForTesting
    public ArrayList<ResolvedComponentInfo> filterLowPriority(List<ResolvedComponentInfo> inputList, boolean returnCopyOfOriginalListIfModified) {
        ArrayList<ResolvedComponentInfo> listToReturn = null;
        ResolveInfo r0 = ((ResolvedComponentInfo) inputList.get(0)).getResolveInfoAt(0);
        int N = inputList.size();
        for (int i = 1; i < N; i++) {
            ResolveInfo ri = ((ResolvedComponentInfo) inputList.get(i)).getResolveInfoAt(0);
            if (r0.priority != ri.priority || r0.isDefault != ri.isDefault) {
                while (i < N) {
                    if (returnCopyOfOriginalListIfModified && listToReturn == null) {
                        listToReturn = new ArrayList(inputList);
                    }
                    inputList.remove(i);
                    N--;
                }
            }
        }
        return listToReturn;
    }

    @VisibleForTesting
    public void sort(List<ResolvedComponentInfo> inputList) {
        AbstractResolverComparator abstractResolverComparator = this.mResolverComparator;
        String str = TAG;
        if (abstractResolverComparator == null) {
            Log.d(str, "Comparator has already been destroyed; skipped.");
            return;
        }
        try {
            long beforeRank = System.currentTimeMillis();
            if (!this.isComputed) {
                CountDownLatch finishComputeSignal = new CountDownLatch(1);
                this.mResolverComparator.setCallBack(new ComputeCallback(finishComputeSignal));
                this.mResolverComparator.compute(inputList);
                finishComputeSignal.await();
                this.isComputed = true;
            }
            Collections.sort(inputList, this.mResolverComparator);
            System.currentTimeMillis();
        } catch (InterruptedException e) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Compute & Sort was interrupted: ");
            stringBuilder.append(e);
            Log.e(str, stringBuilder.toString());
        }
    }

    private static boolean isSameResolvedComponent(ResolveInfo a, ResolvedComponentInfo b) {
        ActivityInfo ai = a.activityInfo;
        return ai.packageName.equals(b.name.getPackageName()) && ai.name.equals(b.name.getClassName());
    }

    /* Access modifiers changed, original: 0000 */
    public boolean isComponentFiltered(ComponentName componentName) {
        return false;
    }

    @VisibleForTesting
    public float getScore(DisplayResolveInfo target) {
        return this.mResolverComparator.getScore(target.getResolvedComponentName());
    }

    public void updateModel(ComponentName componentName) {
        this.mResolverComparator.updateModel(componentName);
    }

    public void updateChooserCounts(String packageName, int userId, String action) {
        this.mResolverComparator.updateChooserCounts(packageName, userId, action);
    }

    public void destroy() {
        this.mResolverComparator.destroy();
    }
}
