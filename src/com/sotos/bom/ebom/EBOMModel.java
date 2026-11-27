package com.sotos.bom.ebom;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


import com.sotos.bom.configmgt.model.T1Model;
import com.sotos.bom.configmgt.model.FeatureRuleModel;
import com.sotos.bom.configmgt.model.OptionPackagesModel;
import com.sotos.bom.configmgt.model.PackageModel;

import com.sotos.bom.uc.model.UCModel;


public class EBOMModel {
	Map<String, UCModel>  ucMap = null;
	private List<LOUModel> louList;   //Feature Code 总列表
	
	private boolean isBOMLoaded = false;
	private boolean isUCCodeLoaded = false;
	
	
		
		public EBOMModel() {
			this.ucMap = new HashMap<String, UCModel>();
			this.louList = new ArrayList<LOUModel>();
			
		}
		
		public List<LOUModel> getLOUList() {
			return this.louList;
		}
		
		public void printEBOM() {
			System.out.println("Get EBOM:"+this.louList.size());
			
			for(LOUModel lou: louList) {
				
				System.out.println(lou.print());
			}
		}
		public void printUCCODE() {
			System.out.println("Get UC CODE:"+ucMap.size());
			Set<String> keySet = ucMap.keySet();
			for(String key: keySet) {
				UCModel ucmode = ucMap.get(key);
				System.out.println(ucmode.print());
			}
		}
		public void loadUCCODE(List<String> strUCCodeList, List<String> strUCStringList) throws Exception {
			

	         if(strUCCodeList == null || strUCCodeList.size() != strUCStringList.size()) {
	        	 throw new Exception("codeList , ifList, thenList is not correct");
	         }
	          //加载所有UCCODE
	            for (int i = 0; i < strUCCodeList.size(); i++) {

	            
	                String strUCCode = strUCCodeList.get(i);
	                
	                if(ucMap.containsKey(strUCCode)) {
	                	continue;
	                }
	                
	                String strUCString = strUCStringList.get(i);
	                
	                strUCString = strUCString.replace("MM2AA0 &", "").replace(".", "").replace(" ", "");
	                strUCString = strUCString.replace("MM2AA0", "");
	                UCModel ucModel = new UCModel(strUCCode,strUCString);

	                ucMap.put(strUCCode, ucModel);
	                	                

	            }
	            
	            this.isUCCodeLoaded = true;
	            

		}
		

		
		public void loadEBOM(List<LOUModel> louList) throws Exception {
			
	         if(louList == null || louList.size() == 0) {
	        	 throw new Exception("LOU List is null");
	         }
	            

	            
	          //加载这个EBOM所有的LOU行

		        for (LOUModel lou : louList) {
	                
		        	UCModel uc = ucMap.get(lou.getUCCode());
	                lou.setUC(uc);
	                
	                this.louList.add(lou);
	            }
	            
	            //对于list排序
	            louList.sort(new Comparator<LOUModel>(){
	                public int compare(LOUModel lou1,LOUModel lou2) {
	                    return lou1.getSVPPSFFC().compareTo(lou2.getSVPPSFFC());
	                }
	             });
	            
	            this.isBOMLoaded = true;
	            
	            

		}
		
		
}

