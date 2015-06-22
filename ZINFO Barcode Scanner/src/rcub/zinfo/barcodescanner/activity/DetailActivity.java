package rcub.zinfo.barcodescanner.activity;

import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewCompat;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import rcub.zinfo.barcodescanner.R;
import rcub.zinfo.barcodescanner.ZinfoPaket;

public class DetailActivity extends BarcodeScannerBaseActivity {

    public static String KEY_PAKET = "DetailPaket";

    TextView proizvod;
    TextView numOd;
    TextView numDo;
    TextView paketBroj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        View view = findViewById(R.id.detail_header);

        proizvod = (TextView) findViewById(R.id.detail_paket_proizvod);
        numOd = (TextView) findViewById(R.id.detail_paket_num_od);
        numDo = (TextView) findViewById(R.id.detail_paket_num_do);
        paketBroj = (TextView) findViewById(R.id.detail_paket_broj);

        ZinfoPaket paket = getIntent().getExtras().getParcelable(KEY_PAKET);


        ViewCompat.setTransitionName(view, getString(R.string.detail_transition));

        proizvod.setText(paket.getProizvod());
        numOd.setText(paket.getNumOd());
        numDo.setText(paket.getNumDo());
        paketBroj.setText(paket.getBrojPaketa().toString());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int id = menuItem.getItemId();
        if (id == android.R.id.home) {
            ActivityCompat.finishAfterTransition(this);
            return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }

}
