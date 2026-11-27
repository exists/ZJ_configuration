package com.sotos.bom.configmgt.model;

import java.util.HashMap;
import java.util.Map;


public class VCModel {
	
	private Map<String, Boolean> featureOpt = null;
	private String OPdesc = "";
	//private String desc = "";
	private String OP = "";
	private String ID = "";
	private String exteriorCode = "";
	private String VCCode  = "";
	private T2Model t2Model = null; //关联T2
	
	//private String operator = "";
	
	private OperationType operation = OperationType.NA;
//	private boolean isChange = false;
//	private boolean isDelete = false;
	
	
	
	public VCModel() {
		this.featureOpt = new HashMap<String, Boolean>();
	}
	
	public String printVC() {
		String str = "VC OP:" + this.OP+"   操作类型："+ this.operation +"  OPDesc:" + this.OPdesc+"  外饰code:" + this.exteriorCode + "    featureOpt:  [" + featureOpt.toString() +"]";
		

		return str;
	}
	public void setValue(String feature, Boolean opt) {
		this.featureOpt.put(feature, opt);
	}
	public Boolean getOptValue(String feature) {
		return this.featureOpt.get(feature);
	}
	
//	public Map<String, Boolean> getFeatureOpt(){
//		return this.featureOpt;
//	}
	public Map<String, Boolean>  getOptionMap() {
		return featureOpt;
	}
	public void setOptionMap(Map<String, Boolean> featureOpt) {
		this.featureOpt = featureOpt;
	}
	public OperationType getOperator() {
		return this.operation;
	}
	public void setOperator(OperationType operation) {
		this.operation = operation;
	}
	
	public String getID() {
		return ID;
	}
	public void setID(String ID) {
		this.ID = ID;
	}
	
//	public String getDesc() {
//		return desc;
//	}
//	protected void setDesc(String desc) {
//		this.desc = desc;
//	}
	public T2Model getT2Model() {
		return t2Model;
	}
	public void setT2Model(T2Model t2Model) {
		this.t2Model = t2Model;
	}

	public String getExteriorCode() {
		return exteriorCode;
	}
	public void setExteriorCode(String exteriorCode) {
		this.exteriorCode = exteriorCode;
	}

	public String getOPdesc() {
		return OPdesc;
	}
	public void setOPdesc(String OPdesc) {
		this.OPdesc = OPdesc;
	}
	public String getOP() {
//		if(OP.endsWith("+")) {
//			OP = OP.substring(0,OP.length()-1); 
//		}
		return OP;
	}
	public void setOP(String OP) {
		if(OP.endsWith("+")) {
			this.OP = OP.substring(0,OP.length()-1); 
		}
		else
		
			this.OP = OP;
	}

}
