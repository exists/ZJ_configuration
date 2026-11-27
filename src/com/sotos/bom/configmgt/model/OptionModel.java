package com.sotos.bom.configmgt.model;

import java.util.HashSet;
import java.util.Set;

public class OptionModel {
	private PackageModel packageModel = null;
	//private FeatureCodeModel featureCodeModel = null;
	private String value;
	
	public OptionModel(String value) {
		this.value = value;
	}
	
	public String getVaule() {
		return value;
	}
	
	public Set getOptions() {
		Set<String> OptionSet = new HashSet<String>();
		
		
		if(value.contains("/")) {
			String[] optstrs = value.split("/");
			for(String optstr2:optstrs) {
				OptionSet.add(optstr2);
			}
		}
		else {
			OptionSet.add(value);
		}
		return OptionSet;
	}
	
	
	public boolean isS() {
		if(value.equals("S") || value.equals("s"))
			return true;
		return false;
	}
	public boolean isN() {
		if(value.equals("-") )
			return true;
		return false;
	}
	public boolean isO() {
		if(value.equals("O") )
			return true;
		return false;
	}
	
	public boolean isOptional() {
		if( (value.indexOf("O") >= 0 || value.indexOf("o") >= 0 ) &&  value.length()>1 )
			return true;
		return false;
	}

	
	public void setPackageModel (PackageModel packageModel) {
		this.packageModel = packageModel;
	}
	public PackageModel getPackageModel () {
		return  this.packageModel ;
	}
	
}
