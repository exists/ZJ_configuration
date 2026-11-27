package com.sotos.bom.configmgt.model;

import com.sotos.bom.configmgt.util.FRExpressionNode;
import com.sotos.bom.configmgt.util.FeatureRuleUtils;

public class FeatureRuleModel {
	
	//private String UCCode = null;
	private String ifRule = null;
	private String thenRule = null;
	
	private FRExpressionNode ifNode = null;
	private FRExpressionNode thenNode = null;
	
	
	public FeatureRuleModel() {
		//this.UCCode = UCCode;
		this.ifRule = "";
		this.thenRule = "";
		
	}

	
	public FeatureRuleModel(String UCCode,String ifRule,String thenRule) {
		//this.UCCode = UCCode;
		this.ifRule = ifRule;
		this.thenRule = thenRule;
		
	}
	public void setIfRule(String ifRule) {
		this.ifRule = ifRule;
		this.ifNode = FeatureRuleUtils.loadFRString(ifRule);
	}
	public String getIfRule() {
		return this.ifRule;
	}
	public FRExpressionNode getIfRuleNode() throws Exception {
		if(ifNode == null) { //没加载rule
			throw new Exception("Needs to load If rule first!");
		}
		return this.ifNode;
	}
	
	public void setThenRule(String thenRule) {
		this.thenRule = thenRule;
		this.thenNode = FeatureRuleUtils.loadFRString(thenRule);
	}
	
	public String getThenRule() {
		return this.thenRule;
	}
	
	public FRExpressionNode getThenRuleNode() throws Exception {
		if(thenNode == null) { //没加载rule
			throw new Exception("Needs to load Then rule first!");
		}
		return this.thenNode;
	}
	
	
	
//	public String getFeatureCode() {
//		return this.UCCode;
//	}
	public String printFRRule() {
		String str = "Feature Rule: IF "+ifRule+" THEN   "+thenRule;
		return str;
	}

	

}
