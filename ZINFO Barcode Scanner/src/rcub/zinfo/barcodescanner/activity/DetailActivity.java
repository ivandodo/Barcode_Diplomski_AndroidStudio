package rcub.zinfo.barcodescanner.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.gc.materialdesign.views.ButtonFloat;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;
import java.util.List;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import rcub.zinfo.barcodescanner.R;
import rcub.zinfo.barcodescanner.ZinfoNeispravnaNumeracija;
import rcub.zinfo.barcodescanner.ZinfoPaket;
import rcub.zinfo.barcodescanner.view.ZinfoNumeracijeRecyclerAdapter;
import rcub.zinfo.barcodescanner.webservice.entity.NumeracijaKSOAP2Parser;

public class DetailActivity extends BarcodeScannerBaseActivity {

    public static String KEY_PAKET = "DetailPaket";

    TextView proizvod;
    TextView numOd;
    TextView numDo;
    TextView paketBroj;

    ButtonFloat fabAddButton;
    RecyclerView numeracijeList;

    private static final String METHOD_NAME = "getNumByPaket";

    //target namespace
    private static final String NAMESPACE = "http://webservices.paketi/";
    //address location u WDSL
    private static final String URL = "http://172.16.2.131:8993/WebServices/NumeracijaServiceSoapHttpPort";

    //action
    private static final String SOAP_ACTION2 = NAMESPACE + "/" + METHOD_NAME;

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

        fabAddButton = (ButtonFloat) findViewById(R.id.buttonFloatAdd);
        fabAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInputDialog();
            }
        });

        WebserviceCall call = new WebserviceCall(this);
        call.execute(paket);
    }

    public void showInputDialog(){
        fabAddButton.setVisibility(View.GONE);
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle(getString(R.string.NeispravnaNumeracija));

        // Set an EditText view to get user input
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        alert.setView(input);

        alert.setPositiveButton(getString(R.string.OK), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Long num = Long.parseLong(input.getText().toString());
                if (num < Long.parseLong(numDo.getText().toString())
                        || num < Long.parseLong(numOd.getText().toString())) {
                    Crouton.showText(DetailActivity.this, getString(R.string.NumeracijaVanOpsega), Style.ALERT);
                } else {
                    saveMissingNumber(num);
                }
                fabAddButton.setVisibility(View.VISIBLE);
            }
        });

        alert.setNegativeButton(getString(R.string.CANCEL), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                fabAddButton.setVisibility(View.VISIBLE);
            }
        });

        alert.show();
    }

    public void saveMissingNumber(Long value){

    }

    private class WebserviceCall extends AsyncTask<ZinfoPaket, Void, List<ZinfoNeispravnaNumeracija>> {

        Context context;

        public WebserviceCall(Context ctx) {
            this.context = ctx;
        }

        protected List<ZinfoNeispravnaNumeracija> doInBackground(ZinfoPaket... paket) {
            return getNeispravneNumeracije(paket[0]);
        }

        protected void onPostExecute(List<ZinfoNeispravnaNumeracija> result) {
//            showProgress(false);
            populateList(result);
        }
    }

    private List<ZinfoNeispravnaNumeracija> getNeispravneNumeracije(ZinfoPaket paket){
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

        Log.e("INFO", "Usao u dohvatanje");

        PropertyInfo pi = new PropertyInfo();
        pi.setName("idPaket");
        pi.setValue(paket.getIdPaket());
        pi.setType(Long.class);
        request.addProperty(pi);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);

        envelope.setOutputSoapObject(request);

        HttpTransportSE ht = new HttpTransportSE(URL);
        KvmSerializable response = null;
        List<ZinfoNeispravnaNumeracija> lista = new ArrayList<ZinfoNeispravnaNumeracija>();
        try {
            ht.call(SOAP_ACTION2, envelope);

            try {
                response = (SoapObject) envelope.getResponse();
            } catch (ClassCastException e) {
                response = (SoapObject) envelope.bodyIn;
                Log.e("INFO", "Los odgovor!");
            }

            for(int i=0;i<response.getPropertyCount();i++){
                lista.add((new NumeracijaKSOAP2Parser((SoapObject)response.getProperty(i))).getNumeracija());
            }

            return lista;

        } catch (Exception e) {
            Log.e("ERROR", "Ne moze da parsira paket!");
            return lista;
        }
    }

    public void populateList(List<ZinfoNeispravnaNumeracija> result){
        numeracijeList = (RecyclerView) findViewById(R.id.numeracijeListView);
        numeracijeList.setLayoutManager(new LinearLayoutManager(this));
        ZinfoNumeracijeRecyclerAdapter adapter = new ZinfoNumeracijeRecyclerAdapter(this);
        adapter.setOnItemClickListener(new ZinfoNumeracijeRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(ZinfoNeispravnaNumeracija entity) {
                deleteNumeracija(entity);
            }
        });
        adapter.setData(result);
        numeracijeList.swapAdapter(adapter, false);
    }

    public void deleteNumeracija(ZinfoNeispravnaNumeracija num) {
        Crouton.showText(DetailActivity.this, "RADI", Style.ALERT);
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
