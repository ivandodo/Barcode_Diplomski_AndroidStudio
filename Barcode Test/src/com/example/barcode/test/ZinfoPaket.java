package com.example.barcode.test;

import java.io.Serializable;

public class ZinfoPaket implements Serializable{
	
	private static final long serialVersionUID = -8392407005981380059L;
	private String proizvod;
	private Long brojPaketa;
	private String numOd;
	private String numDo;
	
	public ZinfoPaket(){}
	
	public ZinfoPaket(String proizvod, Long brojPaketa, String numOd,
			String numDo) {
		this.proizvod = proizvod;
		this.brojPaketa = brojPaketa;
		this.numOd = numOd;
		this.numDo = numDo;
	}
	
	public String getProizvod() {
		return proizvod;
	}
	public void setProizvod(String proizvod) {
		this.proizvod = proizvod;
	}
	public Long getBrojPaketa() {
		return brojPaketa;
	}
	public void setBrojPaketa(Long brojPaketa) {
		this.brojPaketa = brojPaketa;
	}
	public String getNumOd() {
		return numOd;
	}
	public void setNumOd(String numOd) {
		this.numOd = numOd;
	}
	public String getNumDo() {
		return numDo;
	}
	public void setNumDo(String numDo) {
		this.numDo = numDo;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}	
}