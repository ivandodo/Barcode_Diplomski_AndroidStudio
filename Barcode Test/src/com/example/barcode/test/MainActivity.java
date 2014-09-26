package com.example.barcode.test;

import java.util.ArrayList;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.barcode.test.view.ZinfoPaketAdapter;
import com.example.barcode.test.webservice.entity.BarcodeReaderException;
import com.example.barcode.test.webservice.entity.PaketKSOAP2Parser;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class MainActivity extends ActionBarActivity {

    private static final int ACTIVITY_SCAN = 0;
    public static final String PAKET = "PAKET";

    EditText inputBarcode;
    ListView listView1;
//	EmptyLayout emptyLayout;

    ZinfoPaket paket;
    ArrayList<ZinfoPaket> lista;
    
    //WSDL operation name
    private static final String METHOD_NAME = "getDefaultPaket";
    //target namespace
    private static final String NAMESPACE = "http://webservice.paketi/";
    //address location u WDSL
//    private static final String URL = "http://172.16.2.130:8991/ZINFO8-WS/PaketiSoapHttpPort";
    private static final String URL = "http://pegasus.local.rcub.bg.ac.rs:7777/ZINFO8-WS/PaketiSoapHttpPort";
    
    //action
    private static final String SOAP_ACTION = NAMESPACE + "/" + METHOD_NAME;
    
//	public static final String EXTRA_MESSAGE = "com.example.barcode.test.MESSAGE";
//	public static final String VALUE_LIST = "com.example.barcode.test.VALUE_LIST";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        inputBarcode = (EditText)findViewById(R.id.editBarcode);

        if (lista == null) lista = new ArrayList<ZinfoPaket>();
        populateList(lista,this);
        
        inputBarcode.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String barKod = inputBarcode.getText().toString();
                    if (barKod != null && barKod.length() > 0){
                    	decodeBarcode(barKod);
                    	
                    	InputMethodManager imm = 
                    			(InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    	
                    	imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    	
                    	populateList(new ArrayList<ZinfoPaket>(),MainActivity.this);
                    }
                    else{
                    	Crouton.showText(MainActivity.this, R.string.NoInputData, Style.ALERT); 
                    	
                    	populateList(new ArrayList<ZinfoPaket>(),MainActivity.this);               	
                    }
                    return true;
                }
                return false;
            }
        });
        
        listView1.setEmptyView(findViewById(R.id.emptyListView));
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
//            case R.id.menu_settings:
//                openSettings();
//                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    };
    
    private void openScanActivity(){
        Intent intent = new Intent(this, ScanActivity.class);
        intent.putExtra("SCAN_MODE", "EAN_8");
        intent.putExtra("SCAN_MODE", "EAN_16");
        intent.putExtra("SCAN_MODE", "UPC_A");
        intent.putExtra("SCAN_MODE", "UPC_E");
        startActivityForResult(intent, ACTIVITY_SCAN);
    }
    
    //popunjavanje liste na osnovu barkoda 
    private void decodeBarcode(String barcode){        
        //Za dohvacen rezultat poziva web servis                      
    	WebserviceCall call = new WebserviceCall(this);
    	try{
    		Long nfeCheck = Long.parseLong(barcode);
    		call.execute(barcode);
    	}
    	
    	//TODO: ovaj catch treba da se izmesti drugde da bi radilo upozorenje
    	catch(Throwable nfe){
    		Crouton.showText(this, "Procitani barkod nije broj", Style.ALERT);
    	}
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == ACTIVITY_SCAN) {
            
            // Handle successful scan
            if (resultCode == RESULT_OK) {
            	
            	//Dohvata rezultat skena
                String barKod = intent.getStringExtra("SCAN_RESULT");
                
                if (barKod != null){
                    inputBarcode.setText(barKod);
                	decodeBarcode(barKod);
                }
                else{
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
        
		listView1 = (ListView) findViewById(R.id.paketListView);
		
		if (listView1.getHeaderViewsCount()== 0) {
        	View header = (View)getLayoutInflater().inflate(R.layout.paket_pregled_header, null);
	        listView1.addHeaderView(header);
        }
        
        listView1.setAdapter(adapter);
	}
    
    private class WebserviceCall extends AsyncTask<String, Void, ZinfoPaket> {

    	Context context;
    	
    	public WebserviceCall(Context ctx){
    		this.context = ctx;
    	}
    	
        protected ZinfoPaket doInBackground(String... barKod) {
          
        	ZinfoPaket response = null;
        	
          try{
        	  PaketKSOAP2Parser paket = dohvatiRezultat(barKod[0]);
          
        	  response = 
        		  new ZinfoPaket(paket.proizvod, 
        				  		 paket.broj,
        				  		 paket.numeracija_od, 
        				  		 paket.numeracija_do);
          }
          
          catch(Throwable t){
        	response = new ZinfoPaket("Probne markice sa tufnama", Long.parseLong(barKod[0]), "1001", "2000");
          }
          return response;
        }

        protected void onPostExecute(ZinfoPaket result) {
          
            ArrayList<ZinfoPaket> list = new ArrayList<ZinfoPaket>();

            paket = result;
            
            list.add(result);
            lista= list;
            
            populateList(list, context);
        }
      }

	private PaketKSOAP2Parser dohvatiRezultat(String barKod){
    	SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
        PropertyInfo pi = new PropertyInfo();
        pi.setName("l");
        try{
        	pi.setValue(barKod);
        }
        catch(NumberFormatException nfe){
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
        	
        	try{
        		response = (SoapObject)envelope.getResponse();
        	}
        	catch(ClassCastException e){
        		response = (SoapObject) envelope.bodyIn;
        	}
        	
        	return new PaketKSOAP2Parser(response);
   
        } catch (Exception e) {
			Log.e("ERROR","Ne moze da parsira paket!");
			return null;
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Read values from the "savedInstanceState"-object
//        paket = savedInstanceState.getParcelable(PAKET);
//        ArrayList<ZinfoPaket> list = new ArrayList<ZinfoPaket>();
//        list.add(paket);
        ArrayList<ZinfoPaket> list = savedInstanceState.getParcelableArrayList(PAKET);
        lista = list;
        populateList(list, this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // Save the values you need
        outState.putParcelableArrayList(PAKET, lista);
        super.onSaveInstanceState(outState);
    }
}
