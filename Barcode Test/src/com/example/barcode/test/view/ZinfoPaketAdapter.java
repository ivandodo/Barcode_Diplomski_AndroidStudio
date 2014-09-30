package com.example.barcode.test.view;

import com.example.barcode.test.R;
import com.example.barcode.test.ZinfoPaket;
//import com.example.barcode.test.R.id;
//import com.example.barcode.test.R.layout;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ZinfoPaketAdapter extends ArrayAdapter<ZinfoPaket> {
    Context context; 
    int layoutResourceId;    
    ZinfoPaket data[] = null;
    
    public ZinfoPaketAdapter(Context context, int layoutResourceId, ZinfoPaket[] data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
    	
        View row = convertView;
        ZinfoPaketHolder holder = null;
        
        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            
            holder = new ZinfoPaketHolder();
            holder.proizvod = (TextView)row.findViewById(R.id.paket_proizvod);
            holder.numOd = (TextView)row.findViewById(R.id.paket_num_od);
            holder.numDo = (TextView)row.findViewById(R.id.paket_num_do);
            holder.kolicina = (TextView)row.findViewById(R.id.paket_broj);
            
            row.setTag(holder);
        }
        else
        {
            holder = (ZinfoPaketHolder)row.getTag();
        }
        
        ZinfoPaket paket = data[position];
        if (paket != null) {
            holder.proizvod.setText(paket.getProizvod());
            holder.numOd.setText(paket.getNumOd());
            holder.numDo.setText(paket.getNumDo());
            holder.kolicina.setText(paket.getBrojPaketa()!= null? paket.getBrojPaketa().toString() : "");
        }
        
        return row;
    }
    
    class ZinfoPaketHolder{
    	TextView proizvod;
    	TextView numOd;
    	TextView numDo;
    	TextView kolicina;
    }

}
