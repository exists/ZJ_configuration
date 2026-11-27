package com.sotos.bom.ebom;

import com.sotos.bom.uc.model.UCModel;

public class LOUModel {
	private String ID = "";
	private String FFC = "";
	private String VPPS = "";
	private String SVPPS = "";
	private String partNumber = "";
	private String desc = "";
	private String UCCode = "";
	
	private String SVPPSFFC="";
	//private String UCCode = "";
	
	private UCModel uc = null;
	
	public void setID(String ID) {
		this.ID = ID;
	}
	public String getID() {
		return ID;
	}
	
	public void setDesc(String desc) {
		this.desc = desc;
		
	}
	public String getDesc() {
		return desc;
	}
	
	public void setFFC(String FFC) {
		this.FFC = FFC;
		this.SVPPSFFC = SVPPS+FFC;
	}
	public String getFFC() {
		return FFC;
	}
	public void setSVPPS(String SVPPS) {
		this.SVPPS = SVPPS;
		this.SVPPSFFC = SVPPS+FFC;
	}
	public String getSVPPS() {
		return SVPPS;
	}
	public void setVPPS(String VPPS) {
		this.VPPS = VPPS;
	}
	public String getVPPS() {
		return VPPS;
	}
	public void setPartNumber(String partNumber) {
		this.partNumber = partNumber;
	}
	public String getPartNumber() {
		return partNumber;
	}
	
	public void setUCCode(String UCCode) {
		this.UCCode = UCCode;
	}
	public String getUCCode() {
		return UCCode;
	}
	
	public void setUC(UCModel uc) {
		this.uc = uc;
	}
	public UCModel getUC() {
		return uc;
	}
	
	public String getSVPPSFFC() {
		return SVPPSFFC;
	}

	public String print() {
//		if(uc == null) {
//			return "ID: " +ID+"  SVPPS:"+SVPPS +"   FFC:"+FFC+" partNumber:"+ partNumber+"    UC:ERROR !!!!!!!!!!!!!!!";
//		}
		return "ID: " +ID+"  SVPPS:"+SVPPS +"   FFC:"+FFC+" partNumber:"+ partNumber+"    UC:"+uc.getUCString();
	}

}
