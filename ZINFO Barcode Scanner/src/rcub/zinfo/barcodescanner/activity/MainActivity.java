package rcub.zinfo.barcodescanner.activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
//    ImageButton fabCam;

    //WSDL operation name
    private static final String METHOD_NAME = "getPaket";
    //target namespace
    private static final String NAMESPACE = "http://webservice.paketi/";
    //address location u WDSL
    private static final String URL = "http://172.16.2.131:8993/ZINFO8-WS/PaketiSoapHttpPort";
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.capture_barcode:
                openScanActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    };

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

            ArrayList<ZinfoPaket> list = new ArrayList<ZinfoPaket>();

            if (result == null) result = new ZinfoPaket();

            list.add(result);
            lista = list;

            populateList(list, context);
        }
    }

    /**
     * Pomocni metod koji se iz poziva zasebne niti i koji na osnovu bar-koda dohvata odgovor web-servisa.
     * @param barKod procitani ili uneti bar-kod
     * @return odgovor servisa za zadati @barKod
     */
    private PaketKSOAP2Parser dohvatiRezultat(Long barKod) {
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
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