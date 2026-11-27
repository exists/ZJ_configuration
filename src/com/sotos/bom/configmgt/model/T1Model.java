package com.sotos.bom.configmgt.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;



import com.sotos.bom.util.StringUtil;

public class T1Model {
	

	
	private List<String> featureCodeList;   //Feature Code 总列表
	private List<PackageModel> packageList; //package 集合
	//private List<PackageModel> oldpackageList; //package 集合
	private Set<String> familySet; //family与feature的映射关系
	
	private String generateMode = "VC"; // T2, vc, T1.5 
	
	//private Map<String,FamilyModel> familyMap;
	
	private boolean isFFLLoaded = false;
	
	//private boolean[][]  T2Table;
	
	public T1Model() {
		this.packageList = new ArrayList<PackageModel>();
		//this.oldpackageList = new ArrayList<PackageModel>();
		this.featureCodeList = new ArrayList<String>();
		//this.familyMap = new HashMap<String,FamilyModel>();
		this.familySet = new HashSet<String>();
		
		this.isFFLLoaded = false;
	}
	
	
	//打印T2到EXCEL
	/*
	public void printAllVC(boolean debugMode,String name) throws Exception {
		
		System.out.println("print vc  of FFL["+name+"] {");
		

			for(PackageModel pm : packageList) {
				List<VCModel> vclist = pm.getVCList();
				
				System.out.println("vc in package ["+pm.getPackageCode() +"] size :"+ vclist.size());
					for(VCModel vc : vclist) {
						if(debugMode == true)
							System.out.println("print vc :"+vc.printVC());
						
						
					}
					
			}
			

			System.out.println("}");
		return;
	}
*/
	//打印T2到EXCEL
	public void printAllT2(String name) {
		
		System.out.println("print t2["+name+"] :");
		
		for (int i =0;i<featureCodeList.size();i++) {
			System.out.print(featureCodeList.get(i) + " : ");
			
			
			StringBuffer sb = new StringBuffer();
			for(PackageModel pm : packageList) {
				System.out.print("");
				
				List<Boolean[]> tableOption = pm.getT2tableOption();
				
					
					for(Boolean[] opts : tableOption) {
						
						if(opts[i]) {
							sb.append("S");
						}
						else {
							sb.append("-");
						}
						
					}
					
					sb.append("|");
			}
			
			System.out.println(sb);
		}
		
		
		System.out.println("print t2 of FFL {");
		
//		for (int i =0;i<featureCodeList.size();i++) {
//			System.out.print(featureCodeList.get(i) + " : ");
			
			
			StringBuffer sb = new StringBuffer();
			for(PackageModel pm : packageList) {
				List<T2Model> t2list = pm.getT2List();
				
				System.out.println("vc in package ["+pm.getPackageCode() +"] size :"+ t2list.size());
					for(T2Model t2 : t2list) {
							System.out.println("print t2 :"+t2.printT2());
					}
			}
			
			System.out.print(sb);
		
		return;
	}
	
	public void printAllFR() throws Exception {
		
		System.out.println("print FR 校验结果 :");
		
	
			
			StringBuffer sb = new StringBuffer();
			for(PackageModel pm : packageList) {
				System.out.println("Check PackageCode:"+pm.getPackageCode());
				List<FeatureRuleModel> frlist = pm.getFeatureRule();
				for(FeatureRuleModel fr : frlist) {
					
					String res = pm.checkFeatureRuleIsValid(fr.getIfRule(), fr.getThenRule()).toString();
					System.out.println("FR  :" + "If " + fr.getIfRule() +" Then "+fr.getThenRule() +"; Check  Result: "+res);
				}
				
				       
			}
			
			System.out.println(sb);

		return;
	}

	//打印T1
	public void printT1() {
		
		System.out.println("Feature Code : [" +featureCodeList.toString() );
		
//		for(int i=0; i<featureCodeList.size();i++) {
//			System.out.println("Feature Code " +i+":"+featureCodeList.get(i) + "    ");
//		}
		System.out.println("Family" + familySet.toString());
			
		for(PackageModel pm : packageList) {
			System.out.println(pm.printPackageModel());
			System.out.println(pm.printFeatureRule());
			System.out.println(pm.printOptionPackages());
			
		}
		

		return;
	}
	/*
	public void generateVC() throws Exception {
		generateVC(true);
		
	}
	
	public void generateVC(boolean ifCheckRule) throws Exception {
		if(!isFFLLoaded) {
			throw new Exception("FFL Table is not Loaded");

		}
		
		for(PackageModel pm : packageList) {
			pm.generateVCOptoin(ifCheckRule);
		}
		
	}
	
	*/
	public String getGenerateMode() {
		return this.generateMode;
	}
	public void setGenerateMode(String generateMode) {
		this.generateMode = generateMode;
	}
	
	
	public void generateOnlyT2(boolean ifCheckRule) throws Exception {
		this.setGenerateMode("T2"); 
		generateT2(ifCheckRule);
		
	}

	public void generateT2() throws Exception {
		generateT2(true);
		
	}
	
	public void generateT2(boolean ifCheckRule) throws Exception {
		if(!isFFLLoaded) {
			throw new Exception("FFL Table is not Loaded");

		}
		
		for(PackageModel pm : packageList) {
			pm.generateT2Optoin(ifCheckRule);
		}
		
	}
	public void generateT15(boolean ifCheckRule) throws Exception {
		if(!isFFLLoaded) {
			throw new Exception("FFL Table is not Loaded");

		}
		
		for(PackageModel pm : packageList) {
			pm.generateT15Optoin(ifCheckRule);
		}
		
	}
	
//	private void addFeatureFamily(String featureFamily) {
//		
//		familySet.add(featureFamily);
//
//	}

	//一个一个加载FeatureCode
	protected void addFeatureCode(String strFeatureCode) {
		this.featureCodeList.add(strFeatureCode);
	}
	
	protected List<String> getFeatureCodeList() {
		return this.featureCodeList;
	}
	

	//一个一个加载FeatureCode
//	protected void addFamilyMap(String strFeatureCode) {
//		this.featureCodeList.add(strFeatureCode);
//	}
//	
//	protected List<String> getFamilyMap() {
//		return this.getFamilyMap();
//	}
//	
/*
	//加载一行package option
	public void loadOldFFLPackageValue(int rowNum, String familyStr,boolean isFamilyExterior , String strFeatureCode, List<String> optionList) throws Exception {

		if(optionList.size() != oldpackageList.size()) {
			throw new Exception("size of optionList:" + oldpackageList.size() +" is not match packageList size:"+oldpackageList.size());
		}
		
		//addFeatureCode(strFeatureCode);
 		boolean isNewFamily = true;
		if(familySet.contains(familyStr)) {
			isNewFamily = false;
			
		}
		else {
			isNewFamily = true;
			familySet.add(familyStr);
		}
 			
		
 		for(int i=0; i<optionList.size(); i++) {
             
 				PackageModel pm = oldpackageList.get(i);

     			pm.addOption(optionList.get(i),strFeatureCode, familyStr,isFamilyExterior,rowNum,isNewFamily);

     			
     	}
            


        //this.addFeatureFamily(familyStr);
   
	}
*/
	//加载一行package option
	public void loadFFLPackageValue(int rowNum, String familyStr,boolean isFamilyExterior,boolean isFamilyInterior , String strFeatureCode, List<String> optionList) throws Exception {

		if(optionList.size() != packageList.size()) {
			throw new Exception("size of optionList:" + optionList.size() +" is not match packageList size:"+packageList.size());
		}
		
		addFeatureCode(strFeatureCode);
 		boolean isNewFamily = true;
		if(familySet.contains(familyStr)) {
			isNewFamily = false;
			
		}
		else {
			isNewFamily = true;
			familySet.add(familyStr);
		}
 			
		
 		for(int i=0; i<optionList.size(); i++) {
             
 				PackageModel pm = packageList.get(i);

     			pm.addOption(optionList.get(i),strFeatureCode, familyStr,isFamilyExterior,isFamilyInterior,rowNum,isNewFamily);

     			
     	}
            


        //this.addFeatureFamily(familyStr);
   
	}

	public void loadFFL() {

		this.isFFLLoaded = true;
		return;
	}
	

	



	public List<PackageModel> getPackageList() {
		return this.packageList;
	}
	/*
	//加载package表头
	public void loadOldPackageList(List<String> packageCodeList) {
		
		for(String packageCode: packageCodeList) {
			 PackageModel packageModel = new PackageModel(this,packageCode);
             this.oldpackageList.add(packageModel);
		}
		
		//this.packageList = packageList;
	}
*/
	
	//加载package表头
	public void loadPackageList(List<String> packageCodeList) {
		
		for(String packageCode: packageCodeList) {
			 PackageModel packageModel = new PackageModel(this,packageCode);
             this.packageList.add(packageModel);
		}
		this.familySet = new HashSet<String>();
		//this.packageList = packageList;
	}
	
	//加载自由选配的选装包
//	public void loadOptionPackage() throws Exception {
//
//			for(PackageModel pm: this.getPackageList()) {
//				//optionPackages.loadOptionPackageModelFromPackageModel(this);
//				pm.loadOptionPackage();
//			}
//
//            //this.isOPLoaded = true;
//            
//	}


}
