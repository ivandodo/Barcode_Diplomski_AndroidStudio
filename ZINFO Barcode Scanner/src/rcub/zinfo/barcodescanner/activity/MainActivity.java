package rcub.zinfo.barcodescanner.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gc.materialdesign.views.ButtonFloat;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import rcub.zinfo.barcodescanner.R;
import rcub.zinfo.barcodescanner.ScanActivity;
import rcub.zinfo.barcodescanner.ZinfoPaket;
import rcub.zinfo.barcodescanner.view.ZinfoPaketAdapter;
import rcub.zinfo.barcodescanner.webservice.entity.PaketKSOAP2Parser;

/**
 * Glavna klasa. Sluzi za pokretanje skeniranja, obradu i prikaz rezultata istog.
 */
public class MainActivity extends BarcodeScannerBaseActivity {
    /**
     * Identifikator sacuvane liste podataka u slucaju promene konfiguracije.
     */
    public static final String PAKET = "PAKET";
    /**
     * Polje za unost teksta
     */
    EditText inputBarcode;
    /**
     * lista za prikaz paketa
     */
    ListView paketList;

    /**
     * Promenljiva u kojoj se cuva lista paketa dobijenih skeniranjem.
     */
    ArrayList<ZinfoPaket> lista;
    /**
     * Promenljiva kojom se obezbedjuje logout
     */
    boolean doubleBackToExitPressedOnce;

    com.gc.materialdesign.views.ButtonFloat fabCameraButton;


    View mMainFormView;
    View mProgressView;

    //WSDL operation name
    private static final String METHOD_NAME = "getPaket";
    //target namespace
    private static final String NAMESPACE = "http://webservices.paketi/";
    //address location u WDSL
    private static final String URL = "http://172.16.2.131:8993/WebServices/PaketiServiceSoapHttpPort";
//    private static final String URL = "http://pegasus.soneco.co.rs:8888/ZINFO8-WS/PaketiSoapHttpPort";

    //action
    private static final String SOAP_ACTION = NAMESPACE + "/" + METHOD_NAME;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        doubleBackToExitPressedOnce = false;

        inputBarcode = (EditText) findViewById(R.id.editBarcode);
//        fabCam = (ImageButton) findViewById(R.id.fab);
//        fabCam.setOnClickListener(new ImageButton.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                openScanActivity();
//            }
//        });

        if (lista == null) lista = new ArrayList<ZinfoPaket>();
        populateList(lista, this);

        fabCameraButton = (ButtonFloat) findViewById(R.id.buttonFloat);
        fabCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openScanActivity();
            }
        });

        inputBarcode.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String barKod = inputBarcode.getText().toString();
                    if (barKod != null && barKod.length() > 0) {
                        decodeBarcode(barKod);

                        InputMethodManager imm =
                                (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

                        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

                        populateList(new ArrayList<ZinfoPaket>(), MainActivity.this);
                    } else {
                        Crouton.showText(MainActivity.this, R.string.NoInputData, Style.ALERT);

                        populateList(new ArrayList<ZinfoPaket>(), MainActivity.this);
                    }
                    return true;
                }
                return false;
            }
        });

        paketList.setEmptyView(findViewById(R.id.emptyListView));

        paketList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                openScanActivity();
            }
        });

        mMainFormView = findViewById(R.id.paketListView);
        mProgressView = findViewById(R.id.search_progress);
    }

    //Menu
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.activity_main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(android.view.MenuItem item) {
//        // Handle presses on the action bar items
//        switch (item.getItemId()) {
//            case R.id.capture_barcode:
//                openScanActivity();
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    };

    /**
     * Poziv @Activity u kojoj se vrsi skeniranje bar-koda
     */
    private void openScanActivity() {
        Intent intent = new Intent(this, ScanActivity.class);
        intent.putExtra("SCAN_MODE", "EAN_8");
        intent.putExtra("SCAN_MODE", "EAN_16");
        intent.putExtra("SCAN_MODE", "UPC_A");
        intent.putExtra("SCAN_MODE", "UPC_E");
        startActivityForResult(intent, BarcodeScannerBaseActivity.ACTIVITY_SCAN);
    }

    /**
     * Pomiocni metod koji proverava da li je dobijeni kod ispravan i koji prosledjuje poziv
     * web servisu
     * @param barcode bar-kod koji se dekodira
     */
    private void decodeBarcode(String barcode) {
        //Za dohvacen rezultat poziva web servis
        showProgress(true);
        WebserviceCall call = new WebserviceCall(this);
        try {
            Long nfeCheck = Long.parseLong(barcode);
            call.execute(nfeCheck);
        }
        catch (Throwable nfe) {

        }

    }

    /**
     * Callback metod kojim se vracaju rezultati aktivnosi koje su pokrenute sa tom namerom
     * @param requestCode definise @Activity ciji se callback obradjije
     * @param resultCode definise status dobijenog rezultata
     * @param intent kolekcija u kojoj se nalaze rezultati koje je proizvela druga aktivnost
     */
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == ACTIVITY_SCAN) {

            // Handle successful scan
            if (resultCode == RESULT_OK) {

                //Dohvata rezultat skena
                String barKod = intent.getStringExtra("SCAN_RESULT");

                if (barKod != null) {
                    inputBarcode.setText(barKod);
                    decodeBarcode(barKod);
                } else {
                    Crouton.showText(this, R.string.NoResultFound, Style.ALERT);
                }

            } else if (resultCode == RESULT_CANCELED) {
                Crouton.showText(this, R.string.ScanInterrupted, Style.INFO);
            }
        }
    }

    //popunjavanje liste
    private void populateList(ArrayList<ZinfoPaket> lista, Context ctx) {

        ZinfoPaketAdapter adapter = new ZinfoPaketAdapter(ctx,
                R.layout.paket_pregled_row, lista.toArray(new ZinfoPaket[lista.size()]));

        paketList = (ListView) findViewById(R.id.paketListView);

        if (paketList.getHeaderViewsCount() == 0) {
            View header = (View) getLayoutInflater().inflate(R.layout.paket_pregled_header, null);
            paketList.addHeaderView(header);
        }

        paketList.setAdapter(adapter);
    }

    /**
     * Unutrasnja klasa koja sluzi za asinhrono dohvatanje podataka o paketu na osnovu bar-koda.
     */
    private class WebserviceCall extends AsyncTask<Long, Void, ZinfoPaket> {

        Context context;

        public WebserviceCall(Context ctx) {
            this.context = ctx;
        }

        protected ZinfoPaket doInBackground(Long... barKod) {

            ZinfoPaket response = null;

            try {
                PaketKSOAP2Parser paket = dohvatiRezultat(barKod[0]);

                //formiranje paketa na osnovu odgovora
                response =
                        new ZinfoPaket(paket.proizvod,
                                paket.broj,
                                paket.numeracija_od,
                                paket.numeracija_do);
            } catch (Throwable t) {
                response = new ZinfoPaket();
            }
            return response;
        }

        protected void onPostExecute(ZinfoPaket result) {

            showProgress(false);
            ArrayList<ZinfoPaket> list = new ArrayList<ZinfoPaket>();

            if (result == null) result = new ZinfoPaket();

            list.add(result);
            lista = list;

            populateList(list, context);
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

            mMainFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mMainFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mMainFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mMainFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Pomocni metod koji se iz poziva zasebne niti i koji na osnovu bar-koda dohvata odgovor web-servisa.
     * @param barKod procitani ili uneti bar-kod
     * @return odgovor servisa za zadati @barKod
     */
    private PaketKSOAP2Parser dohvatiRezultat(Long barKod) {
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

        Log.e("INFO", "Usao u dohvatanje");

        PropertyInfo pi = new PropertyInfo();
        pi.setName("id");
        try {
            pi.setValue(barKod);
        } catch (NumberFormatException nfe) {
            return null;
        }
        pi.setType(Long.class);
        request.addProperty(pi);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);

        envelope.setOutputSoapObject(request);

        HttpTransportSE ht = new HttpTransportSE(URL);
        SoapObject response = null;
        try {
            ht.call(SOAP_ACTION, envelope);

            try {
                response = (SoapObject) envelope.getResponse();
            } catch (ClassCastException e) {
                response = (SoapObject) envelope.bodyIn;
                Log.e("INFO", "Los odgovor!");
            }

            return new PaketKSOAP2Parser(response);

        } catch (Exception e) {
            Log.e("ERROR", "Ne moze da parsira paket!");
            return null;
        }
    }

    /**
     * Citanje sacuvanih parametara u slicaju promene konfiguracije
     * @param savedInstanceState @Bundle u kome se cuvaju parametri
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Read values from the "savedInstanceState"-object
        ArrayList<ZinfoPaket> list = savedInstanceState.getParcelableArrayList(PAKET);
        lista = list;
        populateList(list, this);
    }

    /**
     * Cuvanje parametara u slicaju promene konfiguracije
     * @param outState @Bundle u kome se cuvaju parametri
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // Save the values you need
        outState.putParcelableArrayList(PAKET, lista);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            BarcodeScannerBaseActivity.setPassword(null);
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, getString(R.string.pressBackTwiceToLogout), Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }
}