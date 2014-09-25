package com.example.barcode.test.webservice.entity;

import org.ksoap2.serialization.SoapObject;

import com.example.barcode.test.BarcodeConstants;
import com.example.barcode.test.ZinfoPaket;

public class PaketKSOAP2Parser extends KSOAP2Parser {
    
	static final int BROJ 			= 0;
	static final int PROIZVOD 		= 1;
	static final int NUMERACIJA_OD 	= 2;
	static final int NUMERACIJA_DO 	= 3;
	static final int ELEMENT_COUNT 	= 4;
    
    public static String PROP_BROJ_PAKETA = "brojPaketa";
    public static String PROP_PROIZVOD = "proizvod";
    public static String PROP_NUM_OD = "numOd";
    public static String PROP_NUM_DO = "numDo";

    public long 	broj;
    public String 	proizvod;
    public String 	numeracija_od;
    public String 	numeracija_do;
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
		if (PROP_BROJ_PAKETA.equals(propertyName)) {return BROJ;}
		if (PROP_PROIZVOD.equals(propertyName)) {return PROIZVOD;}
    	if (PROP_NUM_OD.equals(propertyName)) {return NUMERACIJA_OD;}
    	if (PROP_NUM_DO.equals(propertyName)) {return NUMERACIJA_DO;}
		return -1;
	}

	@Override
	public String getPropertyClass(String propertyName) {
    	if (PROP_BROJ_PAKETA.equals(propertyName)) {return BarcodeConstants.CLASS_SOAP_PRIMITIVE;}
    	if (PROP_PROIZVOD.equals(propertyName)) {return BarcodeConstants.CLASS_SOAP_PRIMITIVE;}
    	if (PROP_NUM_OD.equals(propertyName)) {return BarcodeConstants.CLASS_SOAP_PRIMITIVE;}
    	if (PROP_NUM_DO.equals(propertyName)) {return BarcodeConstants.CLASS_SOAP_PRIMITIVE;}
		return null;
	}

	@Override
	public void setProperty(String indexName, Object value){
		int index = getPropertyIndex(indexName);
		String val = (String)value;
		
		if 		(index == BROJ) broj = Long.valueOf(val);
		else if (index == PROIZVOD) proizvod = val;
		else if (index == NUMERACIJA_OD) numeracija_od = val;
		else if (index == NUMERACIJA_DO) numeracija_do = val;    
	}   
	
	public ZinfoPaket getPaket(){
		if (paket == null){
			paket = new ZinfoPaket(proizvod, broj, numeracija_od, numeracija_do);
		}
		
		return paket;
	}
}