package android.widget;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.Iterator;

public class RemoteViewsListAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<RemoteViews> mRemoteViewsList;
    private int mViewTypeCount;
    private ArrayList<Integer> mViewTypes = new ArrayList();

    public RemoteViewsListAdapter(Context context, ArrayList<RemoteViews> remoteViews, int viewTypeCount) {
        this.mContext = context;
        this.mRemoteViewsList = remoteViews;
        this.mViewTypeCount = viewTypeCount;
        init();
    }

    public void setViewsList(ArrayList<RemoteViews> remoteViews) {
        this.mRemoteViewsList = remoteViews;
        init();
        notifyDataSetChanged();
    }

    private void init() {
        if (this.mRemoteViewsList != null) {
            this.mViewTypes.clear();
            Iterator it = this.mRemoteViewsList.iterator();
            while (it.hasNext()) {
                RemoteViews rv = (RemoteViews) it.next();
                if (!this.mViewTypes.contains(Integer.valueOf(rv.getLayoutId()))) {
                    this.mViewTypes.add(Integer.valueOf(rv.getLayoutId()));
                }
            }
            int size = this.mViewTypes.size();
            int i = this.mViewTypeCount;
            if (size > i || i < 1) {
                throw new RuntimeException("Invalid view type count -- view type count must be >= 1and must be as large as the total number of distinct view types");
            }
        }
    }

    public int getCount() {
        ArrayList arrayList = this.mRemoteViewsList;
        if (arrayList != null) {
            return arrayList.size();
        }
        return 0;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return (long) position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (position >= getCount()) {
            return null;
        }
        View v;
        RemoteViews rv = (RemoteViews) this.mRemoteViewsList.get(position);
        rv.addFlags(2);
        if (convertView == null || convertView.getId() != rv.getLayoutId()) {
            v = rv.apply(this.mContext, parent);
        } else {
            v = convertView;
            rv.reapply(this.mContext, v);
        }
        return v;
    }

    public int getItemViewType(int position) {
        if (position >= getCount()) {
            return 0;
        }
        return this.mViewTypes.indexOf(Integer.valueOf(((RemoteViews) this.mRemoteViewsList.get(position)).getLayoutId()));
    }

    public int getViewTypeCount() {
        return this.mViewTypeCount;
    }

    public boolean hasStableIds() {
        return false;
    }
}
