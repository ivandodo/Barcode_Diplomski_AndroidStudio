package rcub.zinfo.barcodescanner.view;

/**
 * Created by ivan.radojevic on 22.06.2015..
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import rcub.zinfo.barcodescanner.R;
import rcub.zinfo.barcodescanner.ZinfoPaket;

/**
 * Created by per-erik on 15/11/14.
 */
public class ZinfoPaketRecyclerAdapter extends AbstractListAdapter<ZinfoPaket, ZinfoPaketRecyclerAdapter.ViewHolder> {

    private final Context             mContext;
    private final LayoutInflater      mInflater;
    private       OnItemClickListener mOnItemClickListener;

    public ZinfoPaketRecyclerAdapter(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ViewHolder(
                mInflater.inflate(R.layout.paket_pregled_row, viewGroup, false)
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
        private final TextView mProizvodView;
        private final TextView mNumOdView;
        private final TextView mNumDoView;
        private final TextView mPaketView;
        private ZinfoPaket mEntity;

        public ViewHolder(View v) {
            super(v);

            mProizvodView = (TextView) v.findViewById(R.id.paket_proizvod);
            mNumOdView = (TextView) v.findViewById(R.id.paket_num_od);
            mNumDoView = (TextView) v.findViewById(R.id.paket_num_do);
            mPaketView = (TextView) v.findViewById(R.id.paket_broj);

            View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(mEntity);
                    }
                }
            };

            mProizvodView.setOnClickListener(listener);
            mNumOdView.setOnClickListener(listener);
            mNumDoView.setOnClickListener(listener);
            mPaketView.setOnClickListener(listener);
        }

        public void bind(ZinfoPaket entity) {
            mEntity = entity;
            mProizvodView.setText(entity.getProizvod());
            mNumOdView.setText(entity.getNumOd());
            mNumDoView.setText(entity.getNumDo());
            mPaketView.setText(entity.getBrojPaketa().toString());
        }

        @Override
        public String toString() {
            return "ViewHolder{" + mProizvodView.getText() + " " + mPaketView.getText() + " " +
                    mNumOdView.getText() + " - " + mNumDoView.getText() + "}";
        }

        public TextView getmProizvodView() {
            return mProizvodView;
        }

        public TextView getmNumOdView() {
            return mNumOdView;
        }

        public TextView getmNumDoView() {
            return mNumDoView;
        }

        public TextView getmPaketView() {
            return mPaketView;
        }
    }

    public static interface OnItemClickListener {
        public void onItemClick(ZinfoPaket entity);
    }
}