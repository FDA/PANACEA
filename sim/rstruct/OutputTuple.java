package com.eng.cber.na.sim.rstruct;

/**
 * OutputTuple is a structure that stores a reportid and its MedDRA hierarchy
 * 
 */

public class OutputTuple {
	private int report;
	private String pt,hlt,hglt,soc;

	public OutputTuple(int report, String pt, String hlt, String hglt, String soc) {
		this.report = report;
		this.pt = pt;
		this.hlt = hlt;
		this.hglt = hglt;
		this.soc = soc;
	}
	
	public int getReport() {
		return report;
	}
	
	public void setReport(int report) {
		this.report = report;
	}
	public String getPt() {
		return pt;
	}
	public void setPt(String pt) {
		this.pt = pt;
	}
	public String getHlt() {
		return hlt;
	}
	public void setHlt(String hlt) {
		this.hlt = hlt;
	}
	public String getHglt() {
		return hglt;
	}
	public void setHglt(String hglt) {
		this.hglt = hglt;
	}
	public String getSoc() {
		return soc;
	}
	public void setSoc(String soc) {
		this.soc = soc;
	}
}
