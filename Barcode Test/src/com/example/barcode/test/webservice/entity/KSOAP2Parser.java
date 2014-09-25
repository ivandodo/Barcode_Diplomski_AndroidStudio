package com.example.barcode.test.webservice.entity;

import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;

//import rs.edb.android.R;
//import rs.edb.android.exception.ExceptionEdbSystem;

import android.util.Log;

public abstract class KSOAP2Parser {
	protected static final String LOG_TAG = "com.example.barcode.test.webservice.entityKsoap2Parser";
	
	public KSOAP2Parser() {}
	
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
			} else if (pi.getValue() instanceof SoapObject){
				if (((SoapObject)pi.getValue()).getPropertyCount() != 0) {
					if (checkPropertyClass(pi.getName(), pi.getValue())) {
						setProperty(pi.getName(), pi.getValue());
					}
				} else {
					// ako SoapObject nema properties onda je to polje u stvari prazno i treba ga preskociti
				}
			} else if (pi.getValue() instanceof SoapPrimitive){
				if (checkPropertyClass(pi.getName(), pi.getValue())) {
					setProperty(pi.getName(), pi.getValue().toString());
				}
			} else {
				//TODO: Hendlovati neocekivani tip objekta
				throw new BarcodeReaderException();
			}			
		}
	}
	
	private boolean checkPropertyClass(String indexName, Object value)
    {
		int index = getPropertyIndex(indexName);
		String propClass = getPropertyClass(indexName);
		if (!propClass.equals(value.getClass().getName())){
 		   	Log.e(LOG_TAG, this.getClass() + " element[" + index + "] -" + indexName + "- nije " + propClass + ". Preskace se polje u obradi!");
 		   	return false;
	 	}
		return true;
    }
		
}

