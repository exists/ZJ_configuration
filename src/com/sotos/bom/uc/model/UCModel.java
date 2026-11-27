package com.sotos.bom.uc.model;

import java.util.ArrayList;
import java.util.Map;

import com.sotos.bom.configmgt.model.T2Model;
import com.sotos.bom.configmgt.util.FeatureRuleUtils;
import com.sotos.bom.uc.util.UCExpressionNode;
import com.sotos.bom.uc.util.UCUtils;

public class UCModel {
	private String UCString = null;
	private String UCCode = null;
	private UCExpressionNode topNode = null;
	
	public UCModel(String UCCode,String UCString) {
		this.UCString = UCString;
		this.UCCode = UCCode;
		this.topNode = UCUtils.loadUCString(UCString);
		
		
	}
	
//	public String format() {
//		//UCString = UCString.replace("MM2AA0", "").replace(".", "");
//		this.UCString =  UCUtils.formatUCString(UCString);
//		return UCString;
//	}
//	
	public boolean calcUC(T2Model t2Model) throws Exception {
		Map<String, Boolean> featureOpt = t2Model.getFeatureOpt();
		return UCUtils.eval(topNode,featureOpt);
	}
	 
	public String print() {
		
		return "UCCode:"+UCCode +"       UCString:"+UCString;
	}
	public String getUCCode() {
		// TODO Auto-generated method stub
		return UCCode;
	}
	public String getUCString() {
		// TODO Auto-generated method stub
		return UCString;
	}
	public UCExpressionNode getTopNode() {
		// TODO Auto-generated method stub
		return topNode;
	}
	

}
