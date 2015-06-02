package rcub.zinfo.barcodescanner.webservice.entity;

import org.ksoap2.serialization.SoapObject;

import rcub.zinfo.barcodescanner.BarcodeConstants;
import rcub.zinfo.barcodescanner.ZinfoNeispravnaNumeracija;

/**
 * Created by ivan.radojevic on 01.06.2015..
 */
public class NumeracijaKSOAP2Parser extends KSOAP2Parser {

    static final int ID = 0;
    static final int NUMERACIJA = 1;
    static final int STATUS = 2;
    static final int ID_PAKET = 3;
    static final int ELEMENT_COUNT = 4;

    public static String PROP_ID = "id";
    public static String PROP_NUMERACIJA = "numeracija";
    public static String PROP_STATUS = "status";
    public static String PROP_ID_PAKET = "idPaket";

    private Long id;
    private String numeracija;
    private Long status;
    private Long idPaket;

    private ZinfoNeispravnaNumeracija mNumeracija = null;

    public NumeracijaKSOAP2Parser(SoapObject value) throws BarcodeReaderException {
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
        if (PROP_NUMERACIJA.equals(propertyName)) {
            return NUMERACIJA;
        }
        if (PROP_STATUS.equals(propertyName)) {
            return STATUS;
        }
        if (PROP_ID_PAKET.equals(propertyName)) {
            return ID_PAKET;
        }
        return -1;
    }

    @Override
    public String getPropertyClass(String propertyName) {
        if (PROP_ID.equals(propertyName)) {
            return BarcodeConstants.CLASS_SOAP_PRIMITIVE;
        }
        if (PROP_NUMERACIJA.equals(propertyName)) {
            return BarcodeConstants.CLASS_SOAP_PRIMITIVE;
        }
        if (PROP_STATUS.equals(propertyName)) {
            return BarcodeConstants.CLASS_SOAP_PRIMITIVE;
        }
        if (PROP_ID_PAKET.equals(propertyName)) {
            return BarcodeConstants.CLASS_SOAP_PRIMITIVE;
        }
        return null;
    }

    @Override
    public void setProperty(String indexName, Object value) throws BarcodeReaderException {
        int index = getPropertyIndex(indexName);
        String val = (String) value;

        if (index == ID) id = Long.valueOf(val);
        else if (index == NUMERACIJA) numeracija = val;
        else if (index == STATUS) status = Long.valueOf(val);
        else if (index == ID_PAKET) idPaket = Long.valueOf(val);

    }

    /**
     * Metod kojim se vraca objekat dobijen iz poruke
     * @return Ako poruka nije konvertovana u trenutku poziva, vrsi se konverzija u trenutku poziva
     */
    public ZinfoNeispravnaNumeracija getNumeracija() {
        if (mNumeracija == null) {
            mNumeracija = new ZinfoNeispravnaNumeracija(id, numeracija, status, idPaket);
        }

        return mNumeracija;
    }
}
