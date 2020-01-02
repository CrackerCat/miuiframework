package android.service.autofill;

import android.annotation.SystemApi;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.os.RemoteCallback;
import android.os.RemoteException;
import android.service.autofill.IAutofillFieldClassificationService.Stub;
import android.util.Log;
import android.view.autofill.AutofillValue;
import com.android.internal.util.function.pooled.PooledLambda;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@SystemApi
public abstract class AutofillFieldClassificationService extends Service {
    public static final String EXTRA_SCORES = "scores";
    public static final String REQUIRED_ALGORITHM_EDIT_DISTANCE = "EDIT_DISTANCE";
    public static final String REQUIRED_ALGORITHM_EXACT_MATCH = "EXACT_MATCH";
    public static final String SERVICE_INTERFACE = "android.service.autofill.AutofillFieldClassificationService";
    public static final String SERVICE_META_DATA_KEY_AVAILABLE_ALGORITHMS = "android.autofill.field_classification.available_algorithms";
    public static final String SERVICE_META_DATA_KEY_DEFAULT_ALGORITHM = "android.autofill.field_classification.default_algorithm";
    private static final String TAG = "AutofillFieldClassificationService";
    private final Handler mHandler = new Handler(Looper.getMainLooper(), null, true);
    private AutofillFieldClassificationServiceWrapper mWrapper;

    private final class AutofillFieldClassificationServiceWrapper extends Stub {
        private AutofillFieldClassificationServiceWrapper() {
        }

        public void calculateScores(RemoteCallback callback, List<AutofillValue> actualValues, String[] userDataValues, String[] categoryIds, String defaultAlgorithm, Bundle defaultArgs, Map algorithms, Map args) throws RemoteException {
            AutofillFieldClassificationService.this.mHandler.sendMessage(PooledLambda.obtainMessage(-$$Lambda$AutofillFieldClassificationService$AutofillFieldClassificationServiceWrapper$mUalgFt87R5lup2LhB9vW49Xixs.INSTANCE, AutofillFieldClassificationService.this, callback, actualValues, userDataValues, categoryIds, defaultAlgorithm, defaultArgs, algorithms, args));
        }
    }

    public static final class Scores implements Parcelable {
        public static final Creator<Scores> CREATOR = new Creator<Scores>() {
            public Scores createFromParcel(Parcel parcel) {
                return new Scores(parcel, null);
            }

            public Scores[] newArray(int size) {
                return new Scores[size];
            }
        };
        public final float[][] scores;

        private Scores(Parcel parcel) {
            int size1 = parcel.readInt();
            int size2 = parcel.readInt();
            this.scores = (float[][]) Array.newInstance(float.class, new int[]{size1, size2});
            for (int i = 0; i < size1; i++) {
                for (int j = 0; j < size2; j++) {
                    this.scores[i][j] = parcel.readFloat();
                }
            }
        }

        private Scores(float[][] scores) {
            this.scores = scores;
        }

        public String toString() {
            float[][] fArr = this.scores;
            int size1 = fArr.length;
            int i = 0;
            if (size1 > 0) {
                i = fArr[0].length;
            }
            int size2 = i;
            StringBuilder builder = new StringBuilder("Scores [");
            builder.append(size1);
            builder.append("x");
            builder.append(size2);
            builder = builder.append("] ");
            for (int i2 = 0; i2 < size1; i2++) {
                builder.append(i2);
                builder.append(": ");
                builder.append(Arrays.toString(this.scores[i2]));
                builder.append(' ');
            }
            return builder.toString();
        }

        public int describeContents() {
            return 0;
        }

        public void writeToParcel(Parcel parcel, int flags) {
            float[][] fArr = this.scores;
            int size1 = fArr.length;
            int size2 = fArr[0].length;
            parcel.writeInt(size1);
            parcel.writeInt(size2);
            for (int i = 0; i < size1; i++) {
                for (int j = 0; j < size2; j++) {
                    parcel.writeFloat(this.scores[i][j]);
                }
            }
        }
    }

    private void calculateScores(RemoteCallback callback, List<AutofillValue> actualValues, String[] userDataValues, String[] categoryIds, String defaultAlgorithm, Bundle defaultArgs, Map algorithms, Map args) {
        Bundle data = new Bundle();
        float[][] scores = onCalculateScores(actualValues, Arrays.asList(userDataValues), Arrays.asList(categoryIds), defaultAlgorithm, defaultArgs, algorithms, args);
        if (scores != null) {
            data.putParcelable(EXTRA_SCORES, new Scores(scores, null));
        }
        RemoteCallback remoteCallback = callback;
        callback.sendResult(data);
    }

    public void onCreate() {
        super.onCreate();
        this.mWrapper = new AutofillFieldClassificationServiceWrapper();
    }

    public IBinder onBind(Intent intent) {
        return this.mWrapper;
    }

    @SystemApi
    @Deprecated
    public float[][] onGetScores(String algorithm, Bundle algorithmOptions, List<AutofillValue> list, List<String> list2) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("service implementation (");
        stringBuilder.append(getClass());
        stringBuilder.append(" does not implement onGetScores()");
        Log.e(TAG, stringBuilder.toString());
        return null;
    }

    @SystemApi
    public float[][] onCalculateScores(List<AutofillValue> list, List<String> list2, List<String> list3, String defaultAlgorithm, Bundle defaultArgs, Map algorithms, Map args) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("service implementation (");
        stringBuilder.append(getClass());
        stringBuilder.append(" does not implement onCalculateScore()");
        Log.e(TAG, stringBuilder.toString());
        return null;
    }
}
