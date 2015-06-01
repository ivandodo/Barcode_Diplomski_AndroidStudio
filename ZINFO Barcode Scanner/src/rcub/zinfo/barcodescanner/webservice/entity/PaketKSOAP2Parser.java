package rcub.zinfo.barcodescanner.webservice.entity;

import org.ksoap2.serialization.SoapObject;

import rcub.zinfo.barcodescanner.BarcodeConstants;
import rcub.zinfo.barcodescanner.ZinfoPaket;

/**
 * Klasa koja sluzi za rekreiranje sadrzaja klase ZinfoPaket koja se prenosi SOAP porukom
 */
public class PaketKSOAP2Parser extends KSOAP2Parser {

    static final int BROJ = 0;
    static final int PROIZVOD = 1;
    static final int NUMERACIJA_OD = 2;
    static final int NUMERACIJA_DO = 3;
    static final int ID_PAKET = 4;
    static final int ID_PROIZVOD = 5;
    static final int ELEMENT_COUNT = 6;

    public static String PROP_BROJ_PAKETA = "brojPaketa";
    public static String PROP_PROIZVOD = "proizvod";
    public static String PROP_NUM_OD = "numOd";
    public static String PROP_NUM_DO = "numDo";
    public static String PROP_ID_PAKET = "idPaket";
    public static String PROP_ID_PROIZVOD = "idProizvod";

    public long broj;
    public String proizvod;
    public String numeracija_od;
    public String numeracija_do;
    public Long idPaket;
    public Long idProizvod;

    private ZinfoPaket paket = null;

    public PaketKSOAP2Parser(SoapObject value) throws BarcodeReaderException {
        super(value);
    }

    @Override
    public int getPropertyCount() {
        return ELEMENT_COUNT;
    }

    @Override
    public int getPropertyIndex(String propertyName) {
        if (PROP_BROJ_PAKETA.equals(propertyName)) {
            return BROJ;
        }
        if (PROP_PROIZVOD.equals(propertyName)) {
            return PROIZVOD;
        }
        if (PROP_NUM_OD.equals(propertyName)) {
            return NUMERACIJA_OD;
        }
        if (PROP_NUM_DO.equals(propertyName)) {
            return NUMERACIJA_DO;
        }
        if (PROP_ID_PAKET.equals(propertyName)) {
            return ID_PAKET;
        }
        if (PROP_ID_PROIZVOD.equals(propertyName)) {
            return ID_PROIZVOD;
        }
        return -1;
    }

    @Override
    public String getPropertyClass(String propertyName) {
        if (PROP_BROJ_PAKETA.equals(propertyName)) {
            return BarcodeConstants.CLASS_SOAP_PRIMITIVE;
        }
        if (PROP_PROIZVOD.equals(propertyName)) {
            return BarcodeConstants.CLASS_SOAP_PRIMITIVE;
        }
        if (PROP_NUM_OD.equals(propertyName)) {
            return BarcodeConstants.CLASS_SOAP_PRIMITIVE;
        }
        if (PROP_NUM_DO.equals(propertyName)) {
            return BarcodeConstants.CLASS_SOAP_PRIMITIVE;
        }
        if (PROP_ID_PAKET.equals(propertyName)) {
            return BarcodeConstants.CLASS_SOAP_PRIMITIVE;
        }
        if (PROP_ID_PROIZVOD.equals(propertyName)) {
            return BarcodeConstants.CLASS_SOAP_PRIMITIVE;
        }
        return null;
    }

    @Override
    public void setProperty(String indexName, Object value) {
        int index = getPropertyIndex(indexName);
        String val = (String) value;

        if (index == BROJ) broj = Long.valueOf(val);
        else if (index == PROIZVOD) proizvod = val;
        else if (index == NUMERACIJA_OD) numeracija_od = val;
        else if (index == NUMERACIJA_DO) numeracija_do = val;
        else if (index == ID_PAKET) idPaket = Long.valueOf(val);
        else if (index == ID_PROIZVOD) idProizvod = Long.valueOf(val);
    }

    /**
     * Metod kojim se vraca objekat dobijen iz poruke
     * @return Ako poruka nije konvertovana u trenutku poziva, vrsi se konverzija u trenutku poziva
     */
    public ZinfoPaket getPaket() {
        if (paket == null) {
            paket = new ZinfoPaket(proizvod, broj, numeracija_od, numeracija_do, idPaket, idProizvod);
        }

        return paket;
    }
}