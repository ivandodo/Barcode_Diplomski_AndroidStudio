package rcub.zinfo.barcodescanner.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import rcub.zinfo.barcodescanner.R;
import rcub.zinfo.barcodescanner.ZinfoNeispravnaNumeracija;

/**
 * Created by ivan.radojevic on 23.06.2015..
 */
public class ZinfoNumeracijeRecyclerAdapter extends AbstractListAdapter<ZinfoNeispravnaNumeracija, ZinfoNumeracijeRecyclerAdapter.ViewHolder> {

    private final Context mContext;
    private final LayoutInflater mInflater;
    private       OnItemClickListener mOnItemClickListener;

    public ZinfoNumeracijeRecyclerAdapter(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ViewHolder(
                mInflater.inflate(R.layout.numeracije_pregled_row, viewGroup, false)
        );
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        viewHolder.bind(mData.get(i));
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView mNumView;
        private final Button mDeleteButton;
        private ZinfoNeispravnaNumeracija mEntity;

        public ViewHolder(View v) {
            super(v);

            mNumView = (TextView) v.findViewById(R.id.numeracije_num);
            mDeleteButton = (Button) v.findViewById(R.id.numeracije_delete);

            View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(mEntity);
                    }
                }
            };

            mDeleteButton.setOnClickListener(listener);
        }

        public void bind(ZinfoNeispravnaNumeracija entity) {
            mEntity = entity;
            mNumView.setText(entity.getNumeracija());
        }

        @Override
        public String toString() {
            return "ViewHolder{" + mNumView.getText() + "}";
        }
    }

    public static interface OnItemClickListener {
        public void onItemClick(ZinfoNeispravnaNumeracija entity);
    }
}
