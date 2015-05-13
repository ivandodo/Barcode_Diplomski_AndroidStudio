package rcub.zinfo.barcodescanner.view;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import rcub.zinfo.barcodescanner.R;
import rcub.zinfo.barcodescanner.ZinfoPaket;

/**
 * Klasa sluzi za prilagodjavanje klase ZinfoPaket za prikazivanje u listama i slicnim strukturama.
 */
public class ZinfoPaketAdapter extends ArrayAdapter<ZinfoPaket> {
    Context context;
    int layoutResourceId;
    ZinfoPaket data[] = null;

    /**
     * Konstruktor adaptera
     * @param context kontekst u kome se vrsi popunjavanje
     * @param layoutResourceId identifikator resursa (vizuelnog elmenta) koji se popunjava
     * @param data lista podataka koju treba vizuelno predstaviti
     */
    public ZinfoPaketAdapter(Context context, int layoutResourceId, ZinfoPaket[] data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    /**
     * Osnovni metod za popunjavanje vizuelnog elementa podacima
     * @param position pozicija elementa u listi
     * @param convertView vizuelni element koji se popunjava clanom liste na poziciji @position
     * @param parent deo korisnickoj interfejsa koji je direktni sadrzalac ovog elementa
     * @return
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View row = convertView;
        ZinfoPaketHolder holder = null;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new ZinfoPaketHolder();
            holder.proizvod = (TextView) row.findViewById(R.id.paket_proizvod);
            holder.numOd = (TextView) row.findViewById(R.id.paket_num_od);
            holder.numDo = (TextView) row.findViewById(R.id.paket_num_do);
            holder.kolicina = (TextView) row.findViewById(R.id.paket_broj);

            row.setTag(holder);
        } else {
            holder = (ZinfoPaketHolder) row.getTag();
        }

        ZinfoPaket paket = data[position];
        if (paket != null) {
            holder.proizvod.setText(paket.getProizvod());
            holder.numOd.setText(paket.getNumOd());
            holder.numDo.setText(paket.getNumDo());
            holder.kolicina.setText(paket.getBrojPaketa() != null ? paket.getBrojPaketa().toString() : "");
        }

        return row;
    }

    /**
     * Pomocna klasa koja opisuje iz kojih prostijih vizuelnih elmenata se sastoje elementi
     * strukture koja se popunjava
     */
    class ZinfoPaketHolder {
        TextView proizvod;
        TextView numOd;
        TextView numDo;
        TextView kolicina;
    }

}
