package com.sotos.bom.configmgt.model;

import java.util.ArrayList;
import java.util.List;

public class FamilyModel {
	private boolean isOptional = false;
	private boolean isOptionPackage = false;
	private boolean isExterior  = false;
	private boolean isInterior  = false;
	
	private String familyCode = null;
	private List<String> featureList = null;
	private List<String> optList = null;  //选装包组合
	
	
	public FamilyModel(String familyCode,boolean isExterior,boolean isInterior) {
		this.familyCode = familyCode;
		this.featureList = new ArrayList<String>();	
		this.optList = new ArrayList<String>();	
		this.isExterior = isExterior;
		this.isInterior = isInterior;
	}
	
	//判断这个family是否包含选择包或者选装特征
	protected void setOptionalListValue(String value) {
		
		if(value.contains("/")) {
			String[] opts = value.split("/");
			for(String opt:opts) {
				optList.add(opt);
			}
			this.isOptionPackage = true;
		}
		else
			optList.add(value);
		if(value.equals("O")) {
			this.isOptional = true;
			//this.isOptionPackage = false;
			
		}
		else if(value.equals("-") || value.equals("S")) {
			//this.isOptional = true;
			return;
		}
		else if( value.startsWith("O") && value.length()>1 ){
			this.isOptionPackage = true;
		}
		
		
	}
	public boolean isExterior() {
		return isExterior;
	}
	public boolean isInterior() {
		return isInterior;
	}
	
	public String getFamilyCode() {
		return familyCode;
	}


	//
	//public void addOption(String opt) {
	//	this.optList.add(opt);
	//}
	
	public List<String> getOptionList() {
		return this.optList;
	}

	
	public boolean isOptionPackage() {
		return isOptionPackage;
	}
	
	public void setIsOptionPackage(boolean isOptionPackage) {
		this.isOptionPackage = isOptionPackage;
	}
	
	public boolean isOptional() {
		return isOptional;
	}
	
	public void setIsOptional(boolean isOptional) {
		this.isOptional = isOptional;
	}
	
	public void addFeature(String strFeature) {
		this.featureList.add(strFeature);
	}
	public List<String> getFeatureList(){
		return this.featureList;
	}

}
