package com.sotos.bom.configmgt.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class T2Model {
	
	private Map<String, Boolean> featureOpt = null;
	private String OPdesc = "";
	private String OP = "";
	private String ID = "";
	private String intColorCode = "NA";
	private Map<String,String> extOptionMap = new HashMap<String,String>();

	
	private Set<VCModel> vcModelSet = new HashSet<VCModel>();   //T2相关VC
	
	//private String operator = "";
	private OperationType operation = OperationType.NA;
	
	public T2Model() {
		this.featureOpt = new HashMap<String, Boolean>();
	}
	
	public void setValue(String feature, Boolean opt) {
		this.featureOpt.put(feature, opt);
	}
	public Boolean getOptValue(String feature) {
		return this.featureOpt.get(feature);
	}
	
	public Map<String, Boolean> getFeatureOpt(){
		return this.featureOpt;
	}
	public void setOptionMap(Map<String, Boolean> featureOpt) {
		this.featureOpt = featureOpt;
	}
	

	 public void  setVCOperation(OperationType operation) {
		if(getVCSet() == null) {
			return;
		}
		for(VCModel vc: getVCSet() ) {
			vc.setOperator(operation);
		}
	}
	
	public String getID() {
		return ID;
	}
	public void setID(String ID) {
		this.ID = ID;
	}
	public String putIntColorCode(Map<String,FamilyModel> familyMap) {
		if(intColorCode.equals("NA")) {
			
			for(String code : this.featureOpt.keySet()) {
				//在这里设置外饰颜色
		    	FamilyModel fm = familyMap.get(code);
		    	if(fm == null) {
		    		continue;
		    	}
		    	
		    	//如果是选装包
		    	if(fm.isInterior() == true && featureOpt.get(code) == true) { //内饰
		    		this.intColorCode = code;
		    		return intColorCode;
		    	}
			}
    	}
		return intColorCode;
	}
	public String getIntColorCode() {
//		if(extColorCode.equals("NA")) {
//			//在这里设置外饰颜色
//        	FamilyModel fm = familyMap.get(featureCodeList.get(cur));
//        	
//        	//如果是选装包
//        	if(fm.isOptionPackage() == true  && (fm.isExterior() == false )) { //排除外饰
//        		OptionModel om = optionList.get(cur);
//        		
//		}
		return intColorCode;
	}
	private void setIntColorCode(String intColorCode) {
		this.intColorCode = intColorCode;
	}


	public Map<String,String> getExtOptionMap() {
		return extOptionMap;
	}
	public void setExtOptionMap(Map<String,String> extOptionMap) {
		this.extOptionMap = extOptionMap;
	}

	public Set<VCModel> getVCSet() {
		return vcModelSet;
	}
	public void setVCSet(Set<VCModel> vcModelSet) {
		this.vcModelSet = vcModelSet;
		//return ;
	}
	

	//判断2个T2的选项是否一致
	private boolean isEqual(T2Model oldT2) {
		Map<String,Boolean> newMap = this.getFeatureOpt();
		Map<String,Boolean> oldMap = oldT2.getFeatureOpt();
		boolean isEqual = true;
		
		if(newMap == null || oldMap == null) {
			return false;
		}
		else if(newMap.size() != oldMap.size()) {
			return false;
		}
		for(String key : newMap.keySet()) {
			if(!oldMap.containsKey(key)) {
				return false;
			}
			if(!newMap.get(key).equals(oldMap.get(key))) {
				return false;
			}
		}
		
		
		
		return isExtEqual(oldT2);
		
	}
	
	
	//判断2个T2的外饰选项是否一致
	public boolean isExtEqual(T2Model oldT2) {
		Map<String,String> newMap = this.getExtOptionMap();
		Map<String,String> oldMap = oldT2.getExtOptionMap();
		boolean isEqual = true;
		
		if(newMap == null || oldMap == null) {
			return false;
		}
//		else if(newMap.size() != oldMap.size()) {
//			return false;
//		}
		for(String key : newMap.keySet()) {
			
			if( newMap.get(key).equals("-") ) {
				continue;
			}
			if(!oldMap.containsKey(key)) {
				return false;
			}
			if(!newMap.get(key).equals(oldMap.get(key))) {
				return false;
			}
		}
		for(String key : oldMap.keySet()) {
			if( oldMap.get(key).equals("-") ) {
				continue;
			}
			
			if(!newMap.containsKey(key)) {
				return false;
			}
			if(!oldMap.get(key).equals(newMap.get(key))) {
				return false;
			}
			
		}
		
		return isEqual;
		
	}
	//映射
	public void matchOldT2VC(T2Model oldT2) {
		Set<VCModel> vcSet =  this.getVCSet();
		Set<VCModel> tmpOldSet =  new HashSet<VCModel>();
		boolean findMatch = false;
		for(VCModel newVC : vcSet) {
			findMatch = false;
			String exCode = newVC.getExteriorCode();
			for(VCModel oldVC : oldT2.getVCSet()) {
				if(exCode.equals(oldVC.getExteriorCode())) {
					newVC.setID(oldVC.getID());
					findMatch = true;
					tmpOldSet.add(oldVC);
				}
			}
			
			if(findMatch == false) {
				newVC.setOperator(OperationType.ADD);
			}
		}
		for(VCModel oldVC : oldT2.getVCSet()) {
			if(!tmpOldSet.contains(oldVC)) {
				oldVC.setOperator(OperationType.DELETE);
			}
		}
	}
	
	public void addVC(VCModel vcModel) {
		if(vcModelSet == null) {
			vcModelSet = new HashSet<VCModel>();
		}
		vcModelSet.add(vcModel);
	}

	public String getOPdesc() {
		return OPdesc;
	}
	public void setOPdesc(String OPdesc) {
		this.OPdesc = OPdesc;
	}
	public String getOP() {
		return OP;
	}
	
	
	public void setOP(String OP) {
		this.OP = OP;
	}
	public OperationType getOperator() {
		return this.operation;
	}
	public void setOperator(OperationType operation) {
		this.operation = operation;
	}
	
	public String printT2() {
		String str = "T2 OP:" + this.OP+"   操作类型："+ this.operation +"  OPDesc:" + this.OPdesc+"   外饰option："+this.extOptionMap.toString() +"    featureOpt:  [" + featureOpt.toString() +"] \n";
		
		for(VCModel vc:this.vcModelSet) {
			str += vc.printVC() +"\n";
		}

		return str;
	}


}
