package rcub.zinfo.barcodescanner.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Build;
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
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.math.BigDecimal;
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
    View mProgressView;

    ZinfoPaket paket;

    private static final String METHOD_GET_LIST = "getNumByPaket";
    private static final String METHOD_DELETE = "brisiNumeraciju";
    private static final String METHOD_SAVE = "sacuvajNumeraciju";

    //target namespace
    private static final String NAMESPACE = "http://webservices.paketi/";
    //address location u WDSL
    private static final String URL = "http://172.16.2.131:8993/WebServices/NumeracijaServiceSoapHttpPort";

    //action
    private static final String SOAP_ACTION_GET_LIST = NAMESPACE + "/" + METHOD_GET_LIST;
    private static final String SOAP_ACTION_DELETE = NAMESPACE + "/" + METHOD_DELETE;
    private static final String SOAP_ACTION_SAVE = NAMESPACE + "/" + METHOD_SAVE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        View view = findViewById(R.id.detail_header);

        proizvod = (TextView) findViewById(R.id.detail_paket_proizvod);
        numOd = (TextView) findViewById(R.id.detail_paket_num_od);
        numDo = (TextView) findViewById(R.id.detail_paket_num_do);
        paketBroj = (TextView) findViewById(R.id.detail_paket_broj);

        paket = getIntent().getExtras().getParcelable(KEY_PAKET);


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
        mProgressView = findViewById(R.id.list_progress);
        numeracijeList = (RecyclerView) findViewById(R.id.numeracijeListView);
        ZinfoNumeracijeRecyclerAdapter adapter = new ZinfoNumeracijeRecyclerAdapter(this);
        adapter.setOnItemClickListener(new ZinfoNumeracijeRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(final ZinfoNeispravnaNumeracija entity) {
                deleteNumeracija(entity);
            }
        });
        numeracijeList.setAdapter(adapter);
        numeracijeList.setLayoutManager(new LinearLayoutManager(this));

        WebserviceLoadListCall call = new WebserviceLoadListCall(this);
        call.execute(paket);
    }

    public void showInputDialog(){
        fabAddButton.setVisibility(View.GONE);
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle(getString(R.string.NeispravnaNumeracija));

        // Set an EditText view to get user input
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setText(paket.getNumOd());
        alert.setView(input);

        alert.setPositiveButton(getString(R.string.OK), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                BigDecimal num = new BigDecimal(input.getText().toString());
                if (num.compareTo(new BigDecimal(numDo.getText().toString())) > 0
                        || num.compareTo(new BigDecimal(numOd.getText().toString())) < 0) {
                    Crouton.showText(DetailActivity.this, getString(R.string.NumeracijaVanOpsega), Style.ALERT);
                } else {
                    ZinfoNeispravnaNumeracija numeracija =
                            new ZinfoNeispravnaNumeracija(null,input.getText().toString(), null, paket.getIdPaket());
                    saveMissingNumeration(numeracija);
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

    public void saveMissingNumeration(final ZinfoNeispravnaNumeracija num){
        showProgress(true);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                WebserviceInsertNumCall call = new WebserviceInsertNumCall(DetailActivity.this);
                call.execute(num);
            }
        });
    }

    private class WebserviceLoadListCall extends AsyncTask<ZinfoPaket, Void, List<ZinfoNeispravnaNumeracija>> {

        Context context;

        public WebserviceLoadListCall(Context ctx) {
            this.context = ctx;
        }

        protected List<ZinfoNeispravnaNumeracija> doInBackground(ZinfoPaket... paket) {
            return getNeispravneNumeracije(paket[0]);
        }

        protected void onPostExecute(List<ZinfoNeispravnaNumeracija> result) {
            populateList(result);
        }
    }

    private List<ZinfoNeispravnaNumeracija> getNeispravneNumeracije(ZinfoPaket paket){
        SoapObject request = new SoapObject(NAMESPACE, METHOD_GET_LIST);

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
            ht.call(SOAP_ACTION_GET_LIST, envelope);

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

    private class WebserviceDeleteNumCall extends AsyncTask<ZinfoNeispravnaNumeracija, Void, Long> {

        Context context;
        Long succes = 0L;

        public WebserviceDeleteNumCall(Context ctx) {
            this.context = ctx;
        }

        protected Long doInBackground(ZinfoNeispravnaNumeracija... num) {
            succes = deletZinfoNeispravnaNumeracija(num[0]);
            try{
                List<ZinfoNeispravnaNumeracija> res = getNeispravneNumeracije(paket);

                if(res!=null){
                    populateList(res);
                }
            }
            finally{
                return succes != null ? succes : 0L;
            }
        }

        protected void onPostExecute(Long result) {
            showProgress(false);
            if (result < 1L) {
                Crouton.showText(DetailActivity.this, "Neuspelo brisanje", Style.ALERT);
            }
            numeracijeList.getAdapter().notifyDataSetChanged();
        }
    }

    private Long deletZinfoNeispravnaNumeracija(ZinfoNeispravnaNumeracija num) {
        Long ret = 0L;

        SoapObject request = new SoapObject(NAMESPACE, METHOD_DELETE);

        Log.e("INFO", "Usao u brisanje");

        PropertyInfo pi = new PropertyInfo();
        pi.setName("numId");
        pi.setValue(num.getId());
        pi.setType(Long.class);
        request.addProperty(pi);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);

        envelope.setOutputSoapObject(request);

        HttpTransportSE ht = new HttpTransportSE(URL);
        SoapObject response = null;
        try {
            ht.call(SOAP_ACTION_DELETE, envelope);

            try {
                response = (SoapObject) envelope.getResponse();
            } catch (ClassCastException e) {
                response = (SoapObject) envelope.bodyIn;
                Log.e("INFO", "Los odgovor!");
            }

            return Long.parseLong(((SoapPrimitive) response.getProperty(0)).toString());

        } catch (Exception e) {
            Log.e("ERROR", "Ne moze da parsira paket!");
            return ret;
        }
    }

    public void populateList(final List<ZinfoNeispravnaNumeracija> result){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((ZinfoNumeracijeRecyclerAdapter)numeracijeList.getAdapter()).setData(result);
                numeracijeList.getAdapter().notifyDataSetChanged();
            }
        });
    }

    public void deleteNumeracija(final ZinfoNeispravnaNumeracija num) {
        showProgress(true);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                WebserviceDeleteNumCall call = new WebserviceDeleteNumCall(DetailActivity.this);
                call.execute(num);
            }
        });
    }

    private class WebserviceInsertNumCall extends AsyncTask<ZinfoNeispravnaNumeracija, Void, Long> {

        Context context;
        Long succes = 0L;

        public WebserviceInsertNumCall(Context ctx) {
            this.context = ctx;
        }

        protected Long doInBackground(ZinfoNeispravnaNumeracija... num) {
            succes = insertZinfoNeispravnaNumeracija(num[0]);
            try{
                List<ZinfoNeispravnaNumeracija> res = getNeispravneNumeracije(paket);

                if(res!=null){
                    populateList(res);
                }
            }
            finally{
                return succes != null ? succes : 0L;
            }
        }

        protected void onPostExecute(Long result) {
            showProgress(false);
            if (result < 1L) {
                Crouton.showText(DetailActivity.this, "Neuspelo cuvanje", Style.ALERT);
            }
            numeracijeList.getAdapter().notifyDataSetChanged();
        }
    }

    private Long insertZinfoNeispravnaNumeracija(ZinfoNeispravnaNumeracija num) {
        Long ret = 0L;

        SoapObject request = new SoapObject(NAMESPACE, METHOD_SAVE);

        Log.e("INFO", "Usao u cuvanje");

        PropertyInfo pi = new PropertyInfo();
        pi.setName("idPaket");
        pi.setValue(num.getIdPaket());
        pi.setType(Long.class);
        request.addProperty(pi);

        PropertyInfo pi2 = new PropertyInfo();
        pi2.setName("numeracija");
        pi2.setValue(num.getNumeracija());
        pi2.setType(String.class);
        request.addProperty(pi2);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);

        envelope.setOutputSoapObject(request);

        HttpTransportSE ht = new HttpTransportSE(URL);
        SoapObject response = null;
        try {
            ht.call(SOAP_ACTION_DELETE, envelope);

            try {
                response = (SoapObject) envelope.getResponse();
            } catch (ClassCastException e) {
                response = (SoapObject) envelope.bodyIn;
                Log.e("INFO", "Los odgovor!");
            }

            return Long.parseLong(((SoapPrimitive) response.getProperty(0)).toString());

        } catch (Exception e) {
            Log.e("ERROR", "Ne moze da parsira paket!");
            return null;
        }
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            numeracijeList.setVisibility(show ? View.GONE : View.VISIBLE);
            numeracijeList.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    numeracijeList.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            numeracijeList.setVisibility(show ? View.GONE : View.VISIBLE);
        }
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
