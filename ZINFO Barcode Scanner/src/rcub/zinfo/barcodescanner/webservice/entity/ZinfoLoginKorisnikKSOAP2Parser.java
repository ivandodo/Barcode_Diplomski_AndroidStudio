package rcub.zinfo.barcodescanner.webservice.entity;

import org.ksoap2.serialization.SoapObject;

import rcub.zinfo.barcodescanner.BarcodeConstants;
import rcub.zinfo.barcodescanner.ZinfoLoginKorisnik;

/**
 * Created by ivan.radojevic on 07.05.2015..
 * Klasa koja sluzi za rekreiranje sadrzaja klase ZinfoPaket koja se prenosi SOAP porukom
 */
public class ZinfoLoginKorisnikKSOAP2Parser extends KSOAP2Parser {

    static final int ID = 0;
    static final int USERNAME = 1;
    static final int PASSWORD = 2;
    static final int ELEMENT_COUNT = 3;

    public static String PROP_ID = "id";
    public static String PROP_USERNAME = "username";
    public static String PROP_PASSWORD = "password";

    public long id;
    public String username;
    public String password;
    private ZinfoLoginKorisnik korisnik = null;

    public ZinfoLoginKorisnikKSOAP2Parser(SoapObject value) throws BarcodeReaderException {
        super(value);
    }

    @Override
    public int getPropertyCount() {
        return ELEMENT_COUNT;
    }

    @Override
    public int getPropertyIndex(String propertyName) {
        if (PROP_ID.equals(propertyName)) {
            return ID;
        }
        if (PROP_USERNAME.equals(propertyName)) {
            return USERNAME;
        }
        if (PROP_PASSWORD.equals(propertyName)) {
            return PASSWORD;
        }
        return -1;
    }

    @Override
    public String getPropertyClass(String propertyName) {
        if (PROP_ID.equals(propertyName)) {
            return BarcodeConstants.CLASS_SOAP_PRIMITIVE;
        }
        if (PROP_USERNAME.equals(propertyName)) {
            return BarcodeConstants.CLASS_SOAP_PRIMITIVE;
        }
        if (PROP_PASSWORD.equals(propertyName)) {
            return BarcodeConstants.CLASS_SOAP_PRIMITIVE;
        }
        return null;
    }

    @Override
    public void setProperty(String indexName, Object value) {
        int index = getPropertyIndex(indexName);
        String val = (String) value;

        if (index == ID) id = Long.valueOf(val);
        else if (index == USERNAME) username = val;
        else if (index == PASSWORD) password = val;
    }

    /**
     * Metod kojim se vraca objekat dobijen iz poruke
     * @return Ako poruka nije konvertovana u trenutku poziva, vrsi se konverzija u trenutku poziva
     */
    public ZinfoLoginKorisnik getZinfoLoginKorisnik() {
        if (korisnik == null) {
            korisnik = new ZinfoLoginKorisnik(id, username, password);
        }

        return korisnik;
    }
}
