package rcub.zinfo.barcodescanner.webservice.entity;

import android.util.Log;

import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;

//import rs.edb.android.R;
//import rs.edb.android.exception.ExceptionEdbSystem;

/**
 * Apstraktna klasa koja predstvlja prototip za konverziju SOAP odgovora u odgovarajuci tip objekta.
 */
public abstract class KSOAP2Parser {
    protected static final String LOG_TAG = "rcub.zinfo.barcodescanner.webservice.entityKsoap2Parser";

    public KSOAP2Parser() {
    }

    public abstract int getPropertyCount();

    public abstract int getPropertyIndex(String propertyName);

    public abstract String getPropertyClass(String propertyName);

    public abstract void setProperty(String indexName, Object value) throws BarcodeReaderException;

    public KSOAP2Parser(SoapObject root) throws BarcodeReaderException {
//		if (root.getPropertyCount() != getPropertyCount()) {
//			// <string name="error_system_uid_030">XsdWsResponse() razlicit broj property-ja.</string>
//			throw new ExceptionEdbSystem(R.string.error_system_uid_030);
//		}
        PropertyInfo pi = new PropertyInfo();
        for (int i = 0; i < root.getPropertyCount(); i++) {
            pi.setValue(null);
            pi.setName(null);
            root.getPropertyInfo(i, pi);
            if (pi.getValue() == null) {
                continue;
            } else if (pi.getValue() instanceof SoapObject) {
                if (((SoapObject) pi.getValue()).getPropertyCount() != 0) {
                    if (checkPropertyClass(pi.getName(), pi.getValue())) {
                        setProperty(pi.getName(), pi.getValue());
                    }
                } else {
                    // ako SoapObject nema properties onda je to polje u stvari prazno i treba ga preskociti
                }
            } else if (pi.getValue() instanceof SoapPrimitive) {
                if (checkPropertyClass(pi.getName(), pi.getValue())) {
                    setProperty(pi.getName(), pi.getValue().toString());
                }
            } else {
                //Neocekivani format poruke
                throw new BarcodeReaderException();
            }
        }
    }

    /**
     * Pomocni metod koji sluzi za utvrdjivanje da li element poruke treba da bude obradjen ili ne.
     * @param indexName redni broj elementa koji se slika u polje klase
     * @param value ime elemnta koji se slika u polje klase
     * @return
     */
    private boolean checkPropertyClass(String indexName, Object value) {
        int index = getPropertyIndex(indexName);
        String propClass = getPropertyClass(indexName);
        if (!propClass.equals(value.getClass().getName())) {
            Log.e(LOG_TAG, this.getClass() + " element[" + index + "] -" + indexName + "- nije " + propClass + ". Preskace se polje u obradi!");
            return false;
        }
        return true;
    }

}

