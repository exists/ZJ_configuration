package com.sotos.bom.configmgt.model;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;

import com.sotos.bom.configmgt.util.FeatureRuleUtils;
import com.sotos.bom.uc.model.UCModel;
import com.sotos.bom.uc.util.UCUtils;
import com.sotos.bom.util.StringUtil;

public class PackageModel {
	//private Map<String,String> featureValueMap;
	private String packageCode;
	private String packageName;
	private List<OptionModel> optionList;
	private Map<String,FamilyModel> familyMap;
	private Map<String,FamilyModel> familyKeyMap;
	private List<String> featureCodeList;   //Feature Code 总列表
	private Set<String> extCodeSet;   //Feature Code 总列表
	private Set<String> intCodeSet;   //Feature Code 总列表
	private List<Boolean[]> featureTableOption; //选装列表
	//private List<Boolean[]> VCtableOption; //T2选装列表
	private T1Model t1 = null;
	//private OptionPackagesModel opm = null;
	private List<T2Model> t2List = null;
	private List<VCModel> vcList = null;
	
	private OptionPackagesModel optionPackages;
	private List<FeatureRuleModel> FRModelList;   //Feature Rule 总列表
	
	private boolean isOptionLoaded = false;
	private boolean isOPLoaded = false;
	private boolean isFRLoaded = false;
	private boolean isT2Loaded = false;
	private boolean isVCLoaded = false;
	//private Set<String> familySet; //family 集合
	
	public boolean if_log = false;
	
	private Stack<Boolean> stack = null; //临时堆栈用于生成T2/VC
	
    public enum CheckReturnType {

        Pass, // & (检查通过)
        NotPass, /// / (检查失败)
        NotRelated /// / (不相关)

    }



    public void setIfLog(boolean ifLog) {
    	this.if_log = ifLog;
    }


	public PackageModel(T1Model t1,String packageCode) {
		this.optionList = new ArrayList<OptionModel>();
		this.familyMap = new HashMap<String,FamilyModel>(); //package 自己的family,用feature Code作为KEY，用于生成T2，VC排列组合的临时存储
		this.familyKeyMap = new HashMap<String,FamilyModel>(); //package 自己的family,用family Code作为KEY，记录family
		this.featureTableOption = new ArrayList<Boolean[]>(); //package 自己的T2/VC选项值表清单
		
		this.optionPackages = new OptionPackagesModel(); //package 自己的选装包
		
		this.packageCode = packageCode;
		this.t1 = t1;
		this.featureCodeList = t1.getFeatureCodeList();
		this.FRModelList = new ArrayList<FeatureRuleModel>();
		//this.opm = t1.getOptionPackagesModel();
		//this.familySet
		//this.t2List = null;
		this.t2List =  new ArrayList<T2Model>(); 
		this.vcList =  new ArrayList<VCModel>(); 
		this.extCodeSet = new HashSet<String>();
		this.intCodeSet = new HashSet<String>();
		
		
		this.isFRLoaded = false;
		this.isOPLoaded = false;
		this.isT2Loaded = false;
		this.isVCLoaded = false;
		this.isOptionLoaded = false;
		
		
		
		this.stack = new Stack<Boolean>();//临时堆栈用于生成T2/VC
	}
	public Map<String,FamilyModel> getFamilyMap(){
		return this.familyMap;
	}
	public Map<String,FamilyModel> getFamilyKeyMap(){
		return this.familyKeyMap;
	}
	
	
	public List<String> getFeatureCodeList(){
		return featureCodeList;
	}	
	
	//public List<Boolean[]> getT2tableOption(){
	public List<Boolean[]> getT2tableOption(){
		return featureTableOption;
	}
	private List<Boolean[]> getVCtableOption(){
		return featureTableOption;
	}
	//以前写的VC变化识别
	/*
	public Set<String>  checkDifference(PackageModel oldpm) {
		Set<String> diffFeatureCodeSet = new HashSet<String>();
		//this.featureCodeList
		Map<String,FamilyModel> newFamilyMap = this.getFamilyMap();
		Map<String,FamilyModel> oldFamilyMap = oldpm.getFamilyMap();
		Iterator<String> it= newFamilyMap.keySet().iterator();
		FamilyModel newfamilyModel;
		FamilyModel oldfamilyModel;
		
		
		while(it.hasNext()) {
			String familystr = it.next();
			newfamilyModel = newFamilyMap.get(familystr);
			
			if(oldFamilyMap.containsKey(familystr)) { //如果两边都有同一个family
				
				oldfamilyModel = oldFamilyMap.get(familystr);
				for(String newfeatureStr : newfamilyModel.getFeatureList()) {
					OptionModel newOpt = this.getOptionbyFeature(newfeatureStr);
					OptionModel oldOpt = oldpm.getOptionbyFeature(newfeatureStr);
					
					
					
					if( newOpt.isS() ) {
						if(oldOpt == null ) {  //如果有新feature 为s,s变-
						
							diffFeatureCodeSet.add(newfeatureStr);
						}
						else if (oldOpt.isN() ){ //如果有s变-，-变s
							diffFeatureCodeSet.add(newfeatureStr);
							for(String oldfeatureStr2 : oldfamilyModel.getFeatureList()) {
								OptionModel oldOpt2 = oldpm.getOptionbyFeature(oldfeatureStr2);
								if(oldOpt2.isS()) {
									diffFeatureCodeSet.add(oldfeatureStr2);
								}
							}
							
							
						}
						
						
					}
				}
				
				for(String oldfeatureStr : oldfamilyModel.getFeatureList()) {
					OptionModel newOpt2 = this.getOptionbyFeature(oldfeatureStr);
					OptionModel oldOpt2 = oldpm.getOptionbyFeature(oldfeatureStr);
					if(oldOpt2.isS()) {
						if(newOpt2 == null ) { //有删feature 为s,-变s
							diffFeatureCodeSet.add(oldfeatureStr);
						}
					}
				}
				
			}
			else {//增加新family
				if(!newfamilyModel.isOptional()) {
					for(String featureStr : newfamilyModel.getFeatureList()) {
						OptionModel newOpt = this.getOptionbyFeature(featureStr);
						if( newOpt.isS() ) {
							diffFeatureCodeSet.add(featureStr);
						}
					}
				}
			}
			
			
			
		}
		
		return diffFeatureCodeSet;
	}
	
	*/
	

	//打印Feature Rule
	public String  printFeatureRule() {
		StringBuffer sb = new StringBuffer();
		sb.append("Loaded feature Rule:");

		for(int i=0; i<this.FRModelList.size();i++) {
			sb.append("Feature Rule " +i+":"+FRModelList.get(i).printFRRule() ) .append(";  ");
		}
		

		return sb.toString();
	}

	
	public List<FeatureRuleModel> getFeatureRule(){
		return this.FRModelList;
	}

	public void loadFeatureRule( List<String> ifList, List<String> thenList) throws Exception {

         if( ifList.size() != thenList.size()) {
        	 throw new Exception("codeList , ifList, thenList is not correct");
         }
          //加载所有Feature Rule
            for (int i = 0; i < ifList.size(); i++) {
               
                //获取UC Code行
                //String ucCode = codeList.get(i);
                FeatureRuleModel ucm = new FeatureRuleModel();
                
              //获取IF行
                ucm.setIfRule(ifList.get(i));
                
                //获取THEN行
                ucm.setThenRule(thenList.get(i));
                
                
                FRModelList.add(ucm);
                
            }
            

		this.isFRLoaded = true;

		
	}

	
	//打印OptionPackage
	public String printOptionPackages() {
		StringBuffer sb = new StringBuffer();
		sb.append("Loaded option Package:");
		//System.out.println();
		List<Set<String>>  list = optionPackages.getOptionPackageList();
		for(int i =0; i < list.size(); i++) {
			//this.optionPackages.printOptionPackages(i);
			
			sb.append(optionPackages.printOptionPackages(i)).append(";");
		}
		
		return sb.toString();
	}
	public OptionPackagesModel getOptionPackages(){
		return this.optionPackages;
	}
	
	//加载固定的选装包
	public void loadOptionPackage(List<String> opList, Map<String,String> optionPackageDescMap) throws Exception {
		
		if(optionPackages.getIsFixedOP() == false) {
			throw new Exception ("Can not load fix Option Package in free mode");
		}

		
			optionPackages.setOptionPackageDescMap(optionPackageDescMap);
			List<Set<String>> tmpList = new ArrayList<Set<String>>();
			optionPackages.generateOptionPackageModelFromPackageModel(this);
			
            for (String strOptionPackage:opList) {
            	Set<String> opSet = optionPackages.loadOptionPackageModel(strOptionPackage);
            	tmpList.add(opSet);
                
            }
            
            optionPackages.removeOPNotOnList(tmpList);// 把不是固定选装包的组合去掉
            
            optionPackages.regenerateOPMap();
            
            this.isOPLoaded = true;
            
	}
	//加载自由选配的选装包
	//public void loadOptionPackage(Map<String,String> optionPackageDescMap ) throws Exception {
	public void loadOptionPackage(Map<String,String> optionPackageDescMap,List<String> OPifList, List<String> OPthenList ) throws Exception {

		if(optionPackages.getIsFixedOP() == true) {//只能自由选配使用
			throw new Exception ("Can not load free Option Package in fix mode");
		}
		if(OPifList == null || OPthenList== null ) {//选装约束参数有问题
			throw new Exception("Option Package Rule not correct, if ："+OPifList+"; then ：" +OPthenList);

		}
		
		
			//for(PackageModel pm: this.getPackageList()) {
			optionPackages.setOptionPackageDescMap(optionPackageDescMap);
			optionPackages.generateOptionPackageModelFromPackageModel(this);
			
			
			//移除不满足选装包校验条件的组合
			optionPackages.removeByOPRule(OPifList, OPthenList);
			//}
			optionPackages.regenerateOPMap();
            this.isOPLoaded = true;
            
           
	}

//	public OptionPackagesModel getOptionPackagesModel() {
//		return this.optionPackages;
//	}
//	
	public void setT2List(List<T2Model> t2List) {
		
//      if(t2List == null || t2List.size() == 0) {
//     	 	throw new Exception("Need to generate T2 first");
//      }
//      
		this.t2List = t2List;
		this.isT2Loaded = true;
		
		this.isVCLoaded = true;
			//return t2List;
	}
	
	public List<T2Model> getT2List() {
		
//        if(t2List == null || t2List.size() == 0) {
//       	 	throw new Exception("Need to generate T2 first");
//        }
//        
		
			return t2List;
	}
	
	private List<VCModel> getVCList() {
		
//        if(vcList == null || vcList.size() == 0) {
//       	 	throw new Exception("Need to generate VC first");
//        }
        
		
			return vcList;
		/*
		else {
			vcList =  new ArrayList<VCModel>(); 
			
			for(Boolean[] vcopt :  featureTableOption) {
				VCModel vcModel = new VCModel();
				for (int i =0;i<featureCodeList.size();i++) {
					vcModel.setValue(featureCodeList.get(i), vcopt[i]);	
				}
				
				vcList.add(vcModel);
				
			}
				
			
		}
		return vcList;
		*/
	}

	private void loadVCList(List<VCModel> vcList) throws Exception{
		if(vcList == null) {
			throw new Exception("VC List is NULL");
		}
		
        this.vcList = vcList;
        
        this.isVCLoaded = true;
	}

	public void setPackageCode(String packageCode) {
		this.packageCode = packageCode;
		
	}
	public String getPackageCode() {
		return this.packageCode;
		
	}
	

	public void addOption(String value,String featureCode,String familyStr,boolean isFamilyExterior,boolean isFamilyInterior,int rowNum,boolean isNewFamilyModel) {
		OptionModel om = new OptionModel(value);
		this.optionList.add(om);
		
		
		
		if(isNewFamilyModel) { //是否需要创建新的FamilyModel
			//设置Family是否选装
			//String featureCode = featureCodeList.get(this.optionList.size()) ;
			FamilyModel fm = new FamilyModel(familyStr,isFamilyExterior,isFamilyInterior);
			fm.addFeature(featureCode);
			fm.setOptionalListValue(value);

			this.familyMap.put(featureCode, fm);
			
			this.familyKeyMap.put(familyStr, fm);
		}
		else {
			//String lastFeatureCode = 
			
			FamilyModel fm = familyMap.get(this.getFeatureCodeList().get(rowNum -1 ) ); //获取上一个的feature code的family
			fm.addFeature(featureCode);
			
			this.familyMap.put(featureCode, fm);
			fm.setOptionalListValue(value);

			
		}
		
		if(isFamilyExterior) {
			this.extCodeSet.add(featureCode);
		}
		if(isFamilyInterior) {
			this.intCodeSet.add(featureCode);
		}
		
		this.isOptionLoaded = true;
						
	}
	
//	private String parseRule(String rule,Boolean[] olist) {
//		String strRule = rule;
//		
//		for( int i = 0; i<olist.length; i++ ) {
//			String fc = featureCodeList.get(i);
//			if(olist[i]) {
//				strRule = strRule.replaceAll(fc, "T");
//			}
//			else {
//				strRule = strRule.replaceAll(fc, "F");
//			}
//		}
//		
//		return strRule;
//
//	}
	
	private boolean isValidByRule(Boolean[] olist) throws Exception {
		 List<FeatureRuleModel> frlist = getFeatureRule();
		 
		 Map<String, Boolean> featureOpt = new HashMap<String, Boolean>();
			for (int i =0;i<featureCodeList.size();i++) {
				
				featureOpt.put(featureCodeList.get(i), olist[i]);
			}

		 
		for(FeatureRuleModel fr : frlist) {
			
			if(if_log) {
				System.out.print("Checking Rule: IF "+ fr.getIfRule() + " ");
			}
			
			String strIfRule = fr.getIfRule();
			boolean ifCondition = FeatureRuleUtils.checkByRule(fr.getIfRuleNode(),featureOpt);
			if(if_log) {
				System.out.print(" RESULT:"+ ifCondition + "; ");
			}
			if(ifCondition) {
				if(if_log) {
					System.out.print("Checking Rule: THEN "+ fr.getThenRule() + ": ");
				}
				String strThenRule = fr.getThenRule();
				if( FeatureRuleUtils.checkByRule(fr.getThenRuleNode(),featureOpt) == false) {
					//System.out.println("Check Rule Failed: IF "+fr.getIfRule()+" Then "+fr.getThenRule());
					if(if_log) {
						System.out.println(" RESULT: NOT PASS!  ");
					}
					return false;
				}
				else {
					if(if_log) {
						System.out.println(" RESULT:  PASS! ");
					}
				}
			}
			else {
				if(if_log) {
					System.out.println(" SKIP! ");
				}
			}
		}
		
		return true;
	}
	
	//检查FeatureRule在package上的所有T2是否有一个生效的
	public CheckReturnType checkFeatureRuleIsValid(String ifRule,String thenRule) throws Exception {
		//boolean result = true;
		boolean findIf = false;
		boolean findThen = false;
//		if(this.isT2Loaded == false) {
//			throw new Exception("T2 needed to be loaded first before checkFeatureRuleIsValid");
//		}
		Map<String, Boolean> featureOpt = new HashMap<String, Boolean>();
		for(int i= 0; i < this.getOptionSize(); i++) {
			String v = this.getOptionValue(i);
			if("-".equals(v)) {
				featureOpt.put(this.getFeatureCodeList().get(i), false);
			}
			else {
				featureOpt.put(this.getFeatureCodeList().get(i), true);
			}
		}
		
		
		//for(T2Model t2 : this.getT2List()) {
		
		 
		 //Map<String, Boolean> featureOpt = t2.getFeatureOpt();

			boolean ifcondition = FeatureRuleUtils.checkByRule(ifRule,featureOpt);
			if(ifcondition) {
				findIf = true;
				boolean thencondition = FeatureRuleUtils.checkByRule(thenRule,featureOpt);
				
				if(thencondition ) {
					findThen = true;
					//当有1条满足条件，就返回正确
					//return result;
				}
				
			}
			
			
		//}
		
		if(findIf == false) {//如果一个都没匹配上，则算不通过
			return CheckReturnType.NotRelated;
		}
		else if(findIf == true && findThen == false) {
			return CheckReturnType.NotPass;
		}
		else if(findIf == true && findThen == true) {
			return CheckReturnType.Pass;
		}
		
		return CheckReturnType.NotRelated;
	}
	
	public String getOptionValue(int index) {
		return this.optionList.get(index).getVaule();
	}
	
	public int getOptionSize() {
		return this.optionList.size();
	}	
	
	public OptionModel getOptionbyFeature(String featureCode) {
		int idx = featureCodeList.indexOf(featureCode);
		if(idx >= 0)
			return this.optionList.get(idx); 
		else
			return null;
	}

	public List<OptionModel> getOptionList() {
		return this.optionList; 
	}
	
	public String printPackageModel() {
		String str = "PackageCode:" + this.packageCode +"    optionList:  [";
		for(OptionModel om : optionList) {
			str = str + om.getVaule() +" , ";
		}
		str = str +"]";
		return str;
	}
	
	//根据UC计算是否属于某一个package的结果
	public String evalUConPackage(UCModel uc) throws Exception {
		if(this.isOptionLoaded == false) {
			throw new Exception("Option List needed to be loaded first");
		}
		
		Map<String,String> featureOptMap = new HashMap<String,String>();
		for(int i=0; i<this.featureCodeList.size(); i++) {
			OptionModel opt = optionList.get(i);
			String str = opt.getVaule();
			if(str.startsWith("O") || str.startsWith("o")) {
				str = "O";
			}
			
			featureOptMap.put(featureCodeList.get(i), str);
		}
		String result = UCUtils.packageEval(uc,featureOptMap );
		
		return result;
	}
	public List<Boolean[]> generateT15Optoin(boolean ifCheckRule) throws Exception {
		
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        
        
		System.out.println("Start generate T1.5 for package "+ this.packageCode+", Time:" + df.format(new Date()) );
		
		if(this.isOPLoaded == false) {
			throw new Exception("Option Pakcage needed to be loaded first");
		}
		if(ifCheckRule == true && this.isFRLoaded == false) {
			throw new Exception("Feature Rule needed to be loaded first");
		}
		
		//
		if(featureTableOption.size() != 0 ) {
			throw new Exception("Can not generate again");
		}
		
		List<Set<String>> opList = optionPackages.getOptionPackageList();
		
		int i = 0;
		for(Set<String> opSet: opList) {
			//System.out.println("Generating option Package:"+opm.printOptionPackages(i));
			if(opSet == null || opSet.size() == 0 ) {
				continue;
			}
			combineT2VC(this.featureCodeList.size(),0,opSet,new HashMap<String,String>(),"NA",ifCheckRule,false);
			i++;
		}
		
		this.isT2Loaded = true;
		
		System.out.println("End generate T1.5, size:"+featureTableOption.size()+"  Time:" + df.format(new Date()) );
		return featureTableOption;
	}
	public List<Boolean[]> generateT2Optoin(boolean ifCheckRule) throws Exception {
		
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        
        
		System.out.println("Start generate T2 for package "+ this.packageCode+", Time:" + df.format(new Date()) );
		
		if(this.isOPLoaded == false) {
			throw new Exception("Option Pakcage needed to be loaded first");
		}
		if(ifCheckRule == true && this.isFRLoaded == false) {
			throw new Exception("Feature Rule needed to be loaded first");
		}
		
		//
		if(featureTableOption.size() != 0 ) {
			throw new Exception("Can not generate again");
		}
		
		List<Set<String>> opList = optionPackages.getOptionPackageList();
		
		int i = 0;
		for(Set<String> opSet: opList) {
			//System.out.println("Generating option Package:"+opm.printOptionPackages(i));
			if(opSet == null || opSet.size() == 0 ) {
				continue;
			}
			combineT2VC(this.featureCodeList.size(),0,opSet,new HashMap<String,String>(),"NA",ifCheckRule,true);
			i++;
		}
		
		this.isT2Loaded = true;
		
		System.out.println("End generate T2, size:"+featureTableOption.size()+"  Time:" + df.format(new Date()) );
		return featureTableOption;
	}
	/*
	public List<Boolean[]> generateVCOptoin(boolean ifCheckRule) throws Exception {
		
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        
        
		System.out.println("Start generate VC for package "+ this.packageCode+", Time:" + df.format(new Date()) );
		
		if(this.isOPLoaded == false) {
			throw new Exception("Option Pakcage needed to be loaded first");
		}
		if(ifCheckRule == true && this.isFRLoaded == false) {
			throw new Exception("Feature Rule needed to be loaded first");
		}
		if(featureTableOption.size() != 0 ) {
			throw new Exception("Can not generate again");
		}

		List<Set<String>> opList = optionPackages.getOptionPackageList();
		
		int i = 0;
		for(Set<String> opSet: opList) {
			//System.out.println("Generating option Package:"+opm.printOptionPackages(i));
			if(opSet == null || opSet.size() == 0 ) {
				continue;
			}
			combineT2VC(this.featureCodeList.size(),0,opSet,ifCheckRule,true);
			i++;
		}
		
		this.isVCLoaded = true;
		
		System.out.println("End generate VC, size:"+featureTableOption.size()+"  Time:" + df.format(new Date()) );
		return featureTableOption;
	}

	
	private boolean paraseOption(OptionModel om,boolean isExterior,boolean isvc ) throws Exception {
		String strOption = om.getVaule();
		if(StringUtil.isEmpty(strOption)) {
			throw new Exception("Option is not right");
		}
		else if(isvc == false && isExterior == true && strOption.startsWith("O")) {
			return false;
		}
		else if(strOption.equals("-")){
			return false;
		}
		else if(strOption.equals("S") ){
			return true;
		}
		throw new Exception("Option is not right");
	}
	
	*/
	private boolean paraseOption(OptionModel om,boolean isExterior ) throws Exception {
		String strOption = om.getVaule();
		if(StringUtil.isEmpty(strOption)) {
			throw new Exception("Option is not right");
		}
		else if(isExterior == true && strOption.startsWith("O")) {
			return false;
		}
		else if(strOption.equals("-")){
			return false;
		}
		else if(strOption.equals("S") ){
			return true;
		}
		throw new Exception("Option is not right");
	}
	
	private boolean isExteriorOption(OptionModel om ) throws Exception {
		String strOption = om.getVaule();
		if(StringUtil.isEmpty(strOption)) {
			throw new Exception("Option is not right");
		}
		
		else if(strOption.startsWith("O")) {
			return true;
		}
		else if(strOption.equals("-")){
			return false;
		}
		else if(strOption.equals("S") ){
			return true;
		}
		throw new Exception("Option is not right");
	}
		
	private String printOptionlist(Boolean[] olist) {
		String str = "Optionlist [";

    	for(boolean b:olist) {
    		
    		if (b) {
    			str += "S";
    		}
    		else {
    			str += "-";
    		}
    		str += "|";
    	}
    	str += "]";
    	
    	return str;
		
		
	}
	


	private void createT2(Boolean[] t2opt,Set<String> opSet,Map<String,String> exteriorOptionMap) throws Exception {
		T2Model t2Model = new T2Model();
		
		
		for (int i =0;i<featureCodeList.size();i++) {
			t2Model.setValue(featureCodeList.get(i), t2opt[i]);	
		}
		
		String op = OptionPackagesModel.genOPstr(opSet);
		String opDesc ;
		if(this.getOptionPackages().getOptionPackageDescMap().containsKey(op)) {
			opDesc = this.getOptionPackages().getOptionPackageDescMap().get(op);
		}
		else {
			throw new Exception("need to load Option Package description before generate T2");
		}
		
		t2Model.setOP(op);
		t2Model.setOPdesc(opDesc);
		
		t2Model.setExtOptionMap(exteriorOptionMap);
		
		if(!"T2".equals(this.t1.getGenerateMode()))
		{
			for(String exteriorOptionCode  : exteriorOptionMap.keySet()) {
				if(!exteriorOptionMap.get(exteriorOptionCode).equals("-"))
					createVC(t2opt,opSet,exteriorOptionCode,t2Model);
			}
		}
		t2List.add(t2Model);
		
		
	}
	
	private void loadVC(String id,Map<String,Boolean> vcoptMap,String op,String opDesc) throws Exception {
		VCModel vcModel = new VCModel();
		vcModel.setID(id);
		//Boolean[] vcopt = new Boolean[vcoptMap.keySet().size()];
		
		vcModel.setOptionMap(vcoptMap);
		
//		for (int i =0;i<featureCodeList.size();i++) {
//			vcModel.setValue(featureCodeList.get(i), vcoptMap.get(featureCodeList.get(i)));	
//		}
//		
		vcModel.setOP(op);
		vcModel.setOPdesc(opDesc);
		vcList.add(vcModel);
		this.isVCLoaded = true;
	}
	public void loadT2(String id,Map<String,Boolean> t2optMap,String op,String opDesc, Set<VCModel> vcModelSet) throws Exception {
		T2Model t2Model = new T2Model();
		t2Model.setID(id);
		//Boolean[] t2opt = new Boolean[t2optMap.keySet().size()];
		
		t2Model.setOptionMap(t2optMap);
		
//		for (int i =0;i<featureCodeList.size();i++) {
//			t2Model.setValue(featureCodeList.get(i), t2opt[i]);	
//		}
//		
		t2Model.setOP(op);
		t2Model.setOPdesc(opDesc);
		
		
		t2Model.setVCSet(vcModelSet);
		t2List.add(t2Model);
		this.isT2Loaded = true;
	}

	//基于T2生成VC，记录外饰code
	private void createVC(Boolean[] t2opt,Set<String> opSet,String exteriorOptionCode,T2Model t2Model) throws Exception {
		VCModel vcModel = new VCModel();
		
		for (int i =0;i<featureCodeList.size();i++) {
			vcModel.setValue(featureCodeList.get(i), t2opt[i]);	
		}
		
		String op = OptionPackagesModel.genOPstr(opSet);
		String opDesc ;
		if(this.getOptionPackages().getOptionPackageDescMap().containsKey(op)) {
			opDesc = this.getOptionPackages().getOptionPackageDescMap().get(op);
		}
		else {
			throw new Exception("need to load Option Package description before generate T2");
		}
		
		vcModel.setOP(op);
		vcModel.setOPdesc(opDesc);
		vcModel.setExteriorCode(exteriorOptionCode);
		
		vcModel.setT2Model(t2Model);
		
		t2Model.addVC(vcModel);
		vcList.add(vcModel);
		
	}
	
	
	 private void combineT2VC(int targ, int cur,Set<String> opSet,Map<String,String>  exteriorOptionMap,String extOption,boolean ifCheckRule,boolean extendO) throws Exception {
	        // TODO Auto-generated method stub
	        if(cur == targ) {
	        	Boolean[] olist= new Boolean[stack.size()];
	        	
	        	int i = 0;
	        	for(boolean b:stack) {
	        		olist[i] = stack.get(i);
	        		i++;
	        	}
	        	
	        	if(if_log) {
	        		System.out.println(printOptionlist(olist));
	        	}
	        	
	        	if(ifCheckRule) {
	        		if(isValidByRule(olist)) {
	        			featureTableOption.add(olist);
	        			
		        		
		        		if(featureTableOption.size() % 1000 == 0) {
		        			System.out.print(".");
		        			if(featureTableOption.size() % 100000 == 0) {
		        				System.out.println();
		        			}
		        		}

	        		}
	        		else {
	        			return;
	        		}
	        	}
	        	else {
	        		featureTableOption.add(olist);
	        	}
	        	
	        	//if(isvc == true) {
	        	//	createVC(olist,opSet);
	        	//}
	        	//else {
	        		createT2(olist,opSet,exteriorOptionMap);
	        	//}
	        	
	            return;
	        }
	        
	        	FamilyModel fm = familyMap.get(featureCodeList.get(cur));
	        	
	        	//如果是选装包
	        	if(fm.isOptionPackage() == true  && (fm.isExterior() == false )) { //排除外饰
	        		OptionModel om = optionList.get(cur);
	        		
	        		
	        		if(opSet.contains(om.getVaule())) {  //如果匹配选装包
	   	         		stack.add(true);
	   	         		
	   	         	}	 
	        		else if(om.getVaule().contains("/")) {  //如果是组合选装包，应该任意匹配一个就行
	        			String[] opts = om.getVaule().split("/");
	        			boolean isFind = false;
	        			for(String optstr : opts) {
	        				if(opSet.contains(optstr)) {
	        					isFind = true;
	        				}
	        				
	        			}
	        			if(isFind == true) {
	        				stack.add(true);
	        			}
	        			else {
	        				stack.add(false);
	        			}
	        		}
	        		else if(om.getVaule().equals("S")) {
	        			//需要判断是否包含这个
	        			boolean isContains = false;
	        			
	        			for(String value : opSet) {
	        				
	        				if( !value.equals("S") && fm.getOptionList().contains(value) ) {
	        					isContains = true;
	        				}
	        			}
	        			
	        			if(isContains) {
	        				stack.add(false);
	        			}
	        			else {
	        				stack.add(true);
	        			}
	        			
	        		}
	   	         	else {
	   	         		//fm.getFeatureList().contains(o)
	   	         		stack.add(false);
	   	         		
	   	         	}
	        		
	        		//stack.add( paraseOption(om) );
	        		combineT2VC(targ, cur+1,opSet,exteriorOptionMap,extOption,ifCheckRule,extendO);
	        		stack.pop();
	        	}
	        	//如果是是外饰family
	        	else if(fm.isOptional() == false || (fm.isExterior() == true)) {
	        		OptionModel om = optionList.get(cur);
	        		
	        		if(fm.isExterior() == true) { //如果外饰，记录可选的外饰选项
	        			//if(isExteriorOption(om) == true) {
	        				exteriorOptionMap.put(featureCodeList.get(cur),om.getVaule());
	        			//}
	        			
	        		}
	        		
	        		stack.add( paraseOption(om, fm.isExterior()) );
	        		combineT2VC(targ, cur+1,opSet,exteriorOptionMap,om.getVaule(),ifCheckRule,extendO);
	        		stack.pop();
	        		
	        		
	        	}
	        	else {//如果是自由选装O
	        		List fList = fm.getFeatureList();
	        		
	   	         	for(int i = 0; i< fList.size(); i++) { //双循环一遍，i作为主循环判断每个option在自己轮次会是S
	   	         		int cnt=0;
	   	         		boolean isValid = true;
	   	         		for(int j=0;j<fList.size(); j++) {
	   	         			OptionModel om = optionList.get(cur);
	   	         			
	   	         		if( ( om.getVaule().equals("O") || om.getVaule().equals("OT")|| om.getVaule().equals("-") ) && extendO == false ) {
		   	         		if(i==j) { //如果这个轮次是-， 则撤回到开始
		   	         			//i++;
		   	         			isValid = false;
		   	         			break;
		   	         		}
	   	         		
	   	         			stack.add(false);

	   	         		}

	   	         			
	   	         		else if(om.getVaule().equals("-")   )
			   	         	{
			   	         		
			   	         		if(i==j) { //如果这个轮次是-， 则撤回到开始
			   	         			//i++;
			   	         			isValid = false;
			   	         			break;
				   	         		//for(int k=0;k<cnt; k++) {
				   	         		//	stack.pop();
				   	         			//cur--;
				   	         		//}
			   	         			//cur++;
			   	         			//cnt++;
			   	         			
			   	         		}
			   	         		
			   	         		stack.add(false); 
			   	         	}
			   	         	else if(om.getVaule().equals("O") || om.getVaule().equals("OT") || om.getVaule().equals("S"))  {
				   	         	if(i == j) { //当i，j重合，认为该轮次选S
					   	         	stack.add(true);
				   	         	}
				   	         	else { //否则就是-
					   	         	stack.add(false);
				   	         	}
			   	         	}
			   	         	 
			   	         	cur++;
			   	         	cnt++;
		   	         		
	   	         		}
	   	         	    
	   	         		if(isValid) {
	   	         			combineT2VC(targ, cur,opSet,exteriorOptionMap,extOption,ifCheckRule,extendO);
	   	         		}
	   	         		
	   	         		for(int k=0;k<cnt; k++) {
	   	         			stack.pop();
	   	         			cur--;
	   	         		}
	   	         	}
	        	}
	    }
	 

	 private Set<String> compareVCWithOldPackage(PackageModel oldPkg) throws Exception  {
			if(this.isVCLoaded == false) {
				throw new Exception(" New package VC needed to be generate first before compareVC");
			}
			if(oldPkg.isVCLoaded == false) {
				throw new Exception(" Old package VC needed to be Loaded first before compareVC");
			}
			
			List<VCModel> oldVClist = oldPkg.getVCList();
			List<VCModel> newVClist = this.getVCList();
			
			Map<String,Set<VCModel>> oldVCMap = new HashMap<String,Set<VCModel>>();
			Map<String,Set<VCModel>> newVCMap = new HashMap<String,Set<VCModel>>();
			
			Set<String> opSet = new HashSet<String>();
			
			PackageModel newPkg = this;
			
			//获取老vc中这些对应different Features的值
			Set<String> noChangedFeatureSet = compareSameFeatureWithOldPackage(oldPkg);//识别是O的feature
			Set<String> changedFeatureSet = compareDiffFeatureWithOldPackage(oldPkg);  //识别差异的feature
			
			
			//为了避免出现连续发布T1表，导致比较2个版本一样的T1
			if(changedFeatureSet.size() == 0) {
				for(VCModel vc : oldVClist) {
					vc.setOperator(OperationType.NOCHANGE);
				}
				for(VCModel vc : newVClist) {
					vc.setOperator(OperationType.NOCHANGE);
				}
				return changedFeatureSet;
				
			}
			
			for(VCModel vc : oldVClist) {
				String opstr = formatOPStr(vc.getOP());
				vc.setOperator(OperationType.DELETE);
				
				if(oldVCMap.containsKey(opstr)) {
					Set<VCModel> vcSet = oldVCMap.get(opstr);
					vcSet.add(vc);
					oldVCMap.put(opstr, vcSet);
				}
				else {
					Set<VCModel> vcSet = new HashSet<VCModel>();
					vcSet.add(vc);
					oldVCMap.put(opstr, vcSet);
				}
				
				opSet.add(opstr);
			}
			for(VCModel vc : newVClist) {
				String opstr = formatOPStr(vc.getOP());
				vc.setOperator(OperationType.ADD);
				
				
				if(newVCMap.containsKey(opstr)) {
					Set<VCModel> vcSet = newVCMap.get(opstr);
					vcSet.add(vc);
					newVCMap.put(opstr, vcSet);
				}
				else {
					Set<VCModel> vcSet = new HashSet<VCModel>();
					vcSet.add(vc);
					newVCMap.put(opstr, vcSet);
				}
				
				opSet.add(opstr);
			}
			
			

			//每个vc比较一下
			 for(String opstr : opSet ) {
				 
				 if(!oldVCMap.containsKey(opstr)  || !newVCMap.containsKey(opstr)) {
					 continue;
					 
				 }
				 
				 for(VCModel newVC : newVCMap.get(opstr) ) {
					 for(VCModel oldVC : oldVCMap.get(opstr) ) {
						 if(oldVC.getOperator() == OperationType.NOCHANGE || oldVC.getOperator() == OperationType.CHANGE) {
							 continue;
						 }
						 
						 CompareResult res = comapreChangedVC( newVC,oldVC , noChangedFeatureSet,featureCodeList);
						 if(res == CompareResult.EQUAL) { //如果完全相同
							 newVC.setOperator(OperationType.NOCHANGE);
							 oldVC.setOperator(OperationType.NOCHANGE);
							 newVC.setID(oldVC.getID());
						 }
						 else if(res == CompareResult.NOEQUAL ) { //如果不相同
							 continue;
							 
						 }
						 else if(res == CompareResult.CHANGED ) { //如果O项都相同
							 newVC.setOperator(OperationType.CHANGE);
							 oldVC.setOperator(OperationType.CHANGE);
							 newVC.setID(oldVC.getID());
						 }
						 else {
							 continue;
						 }
						 
						 
						 
					 }
				 }
					 
					 
					 
				 
			 }
			 return changedFeatureSet;
			

	 }
	 private String formatOPStr(String opString){
		 String opstr = "";
		 
		 if(opString.indexOf(OptionPackagesModel.OPTION_PACKAGE_SEPERATER) > 0 ) {
			 String[] ops1 = opString.split("\\+");
			 List<String> oplist1 = new ArrayList<>(Arrays.asList(ops1));
			 opstr = oplist1.stream().distinct().sorted().collect(Collectors.joining());
		 }
		 else {
			 opstr = opString;
		 }
		 return opstr;
	 }
	 
	 private String formatColorStr(String opString){
		 String opstr = "";
		 
		 if(opString.indexOf(OptionPackagesModel.OPTION_PACKAGE_SEPERATER) > 0 ) {
			 String[] ops1 = opString.split("\\+");
			 List<String> oplist1 = new ArrayList<>(Arrays.asList(ops1));
			 opstr = oplist1.stream().distinct().sorted().collect(Collectors.joining());
		 }
		 else {
			 opstr = opString;
		 }
		 return opstr;
	 }

	 private CompareResult comapreChangedVC(VCModel newVC, VCModel oldVC,Set<String> OfeatureSet,List<String> featureCodeList) {
		 boolean isEqual = true;
		 //boolean findNoMatch = false;


		 for(String fc : featureCodeList) {
			 boolean newOption = false;
			 boolean oldOption = false;
			 
			 if(newVC.getOptValue(fc) == null ) {
				 newOption = false; 
			 }
			 else{
				 newOption = newVC.getOptValue(fc);
			 }
			 if(oldVC.getOptValue(fc) == null ) {
				 oldOption = false;
			 }
			 else{
				 oldOption = oldVC.getOptValue(fc);
			 }
			 
			 if(newOption == oldOption && !OfeatureSet.contains(fc)) {
				 continue;
			 }
			 else if(newOption == oldOption && OfeatureSet.contains(fc)) {  //找到O的feature是相同的
				 //findOMatch = true;
				 continue;
			 }
			 else if(newOption != oldOption && OfeatureSet.contains(fc)) {  //找到O的feature是不相同的，说明不匹配
				 return CompareResult.NOEQUAL;
			 }
			 else if(newOption != oldOption && !OfeatureSet.contains(fc)) {  //发现是不相同
				 //findNoMatch = true;
				 isEqual = false;
			 }
			 
		 }
		 
		 if(isEqual == false) { //有的情况
			 return CompareResult.CHANGED;
		 }
		 //else if(isEqual == true ) { //全部相同的情况
			 return CompareResult.EQUAL;
		// }
		 
		 
		 //如果所有的值都一样，则表示完全相同
		// return CompareResult.UNKNOWN;

	 }

	 private Set<String> compareSameFeatureWithOldPackage(PackageModel oldPkg) throws Exception  {
			
			
			PackageModel newPkg = this;
			
			//List<String> featureCodeList = newPkg.getFeatureCodeList();
			
			Map<String,FamilyModel> famKeyMap = newPkg.getFamilyKeyMap();
			Set<String> oFeatureSet = new HashSet<String>();
			
			
			
			for(String featureCode : newPkg.getFeatureCodeList()) {

				//匹配“o”的feature
				 if(  (newPkg.getOptionbyFeature(featureCode) != null && newPkg.getOptionbyFeature(featureCode).isO())  || 
						 (oldPkg.getOptionbyFeature(featureCode) != null && oldPkg.getOptionbyFeature(featureCode).isO() )  ) {
					 oFeatureSet.add(featureCode);
					
				}
					//匹配“内饰”的feature
				 FamilyModel fm = familyMap.get(featureCode);
				 if(fm.isInterior()) {
					 oFeatureSet.add(featureCode);
				 }
				 
				
			}
			
			System.out.println("O featureList:"+oFeatureSet);
			
			return oFeatureSet;
	 }
	
	 
	 private Set<String> compareDiffFeatureWithOldPackage(PackageModel oldPkg) throws Exception  {
			
			
			PackageModel newPkg = this;
			
			//List<String> featureCodeList = newPkg.getFeatureCodeList();
			
			//Map<String,FamilyModel> famKeyMap = newPkg.getFamilyKeyMap();
			Set<String> changedFeatureSet = new HashSet<String>();
			
			for(String featureCode : newPkg.featureCodeList) {
				OptionModel newOpt = newPkg.getOptionbyFeature(featureCode);
				OptionModel oldOpt = oldPkg.getOptionbyFeature(featureCode);
				if(oldOpt == null) {
					changedFeatureSet.add(featureCode);
					continue;
				}
				if(newOpt == null) {
					changedFeatureSet.add(featureCode);
					continue;
				}
				
				if(!newOpt.getVaule() .equals(oldOpt.getVaule())  ) {
					changedFeatureSet.add(featureCode);
				}
			}
			for(String featureCode : oldPkg.featureCodeList) {
				OptionModel newOpt = newPkg.getOptionbyFeature(featureCode);
				OptionModel oldOpt = oldPkg.getOptionbyFeature(featureCode);
				if(oldOpt == null) {
					changedFeatureSet.add(featureCode);
					continue;
				}
				if(newOpt == null) {
					changedFeatureSet.add(featureCode);
					continue;
				}
				
			}
			
			System.out.println("Different featureList:"+changedFeatureSet);
			
			return changedFeatureSet;
	 }
	 
	 
	 public Set<String> compareT2WithOldPackage(PackageModel oldPkg) throws Exception  {
		 System.out.println("Start to compare the T2 different");
		 
			if(this.isT2Loaded == false) {
				throw new Exception(" New package T2 needed to be generate first before compareT2");
			}
			if(oldPkg.isT2Loaded == false) {
				throw new Exception(" Old package T2 needed to be Loaded first before compareT2");
			}
			
			List<T2Model> oldT2list = oldPkg.getT2List();
			List<T2Model> newT2list = this.getT2List();
			/*
			for(T2Model t2 : oldT2list) {
				t2.setOperator(OperationType.DELETE);
			}
			for(T2Model t2 : newT2list) {
				t2.setOperator(OperationType.ADD);
			}
			*/
			
			Map<String,T2Model> oldVT2Map = new HashMap<String,T2Model>();  //内饰颜色+选择包与老T2的MAP
			Map<String,T2Model> newT2Map = new HashMap<String,T2Model>();  //内饰颜色+选择包与新T2的MAP
			
			Set<String> opSet = new HashSet<String>();  //所有的选装包集合
			
			PackageModel newPkg = this;
			//获取老T2中这些对应differenceFeatures的值
			
			//Set<String> noChangedFeatureSet = compareSameFeatureWithOldPackage(oldPkg);//识别是O或者是内饰的feature
			Set<String> changedFeatureSet = compareDiffFeatureWithOldPackage(oldPkg);//识别差异的feature
			
			
			//为了避免出现连续发布T1表，导致比较2个版本一样的T1
//			if(changedFeatureSet.size() == 0) {
//				for(T2Model t2 : oldT2list) {
//					t2.setOperator(OperationType.NOCHANGE);
//					t2.setVCOperation(OperationType.NOCHANGE);
//				}
//				for(T2Model t2 : newT2list) {
//					t2.setOperator(OperationType.NOCHANGE);
//					t2.setVCOperation(OperationType.NOCHANGE);
//				}
//				return changedFeatureSet;
//				
//			}

			//把老T2设置默认D,新T2默认A
			for(T2Model t2 : oldT2list) {
				t2.putIntColorCode(familyMap);
				String opcolorstr = formatOPStr(t2.getOP() + "|"+t2.getIntColorCode() );
				//String colorStr = formatColorStr();
				t2.setOperator(OperationType.DELETE);
				t2.setVCOperation(OperationType.DELETE);
				
				//构建选装包与老T2的MAP
				if(!oldVT2Map.containsKey(opcolorstr)) {
					oldVT2Map.put(opcolorstr, t2);
				}
//				else {
//					Set<T2Model> t2Set = new HashSet<T2Model>();
//					t2Set.add(t2);
//					oldVT2Map.put(opstr, t2Set);
//				}
				
				opSet.add(opcolorstr);
			}
			for(T2Model t2 : newT2list) {
				t2.putIntColorCode(familyMap);
				String opcolorstr = formatOPStr(t2.getOP() + "|"+t2.getIntColorCode() );
				t2.setOperator(OperationType.ADD);
				t2.setVCOperation(OperationType.ADD);
				
				//构建选装包与新T2的MAP
				if(!newT2Map.containsKey(opcolorstr)) {
					newT2Map.put(opcolorstr, t2);
				}
//				else {
//					Set<T2Model> t2Set = new HashSet<T2Model>();
//					t2Set.add(t2);
//					newT2Map.put(opstr, t2Set);
//				}
				
				opSet.add(opcolorstr);
			}
		
			
			
			//先把每个t2比较一下，如果如果选项一致，则认为是同号
			 for(T2Model newT2 : newPkg.getT2List() ) {
				 for(T2Model oldT2 : oldPkg.getT2List()) {
					 if(oldT2.getOperator() == OperationType.NOCHANGE 
							 ||oldT2.getOperator() == OperationType.CHANGEDESC 
							 || oldT2.getOperator() == OperationType.CHANGE) { // 如果已经判断过了不用重复计算
						 continue;
					 }
					 CompareResult res = comapreChangedT2( newT2,oldT2 , featureCodeList);
					 if(res == CompareResult.EQUAL) { //如果完全相同
						 if(newT2.getOPdesc().equals(oldT2.getOPdesc()) ) {
							 
							 if(newT2.isExtEqual(oldT2)) {
							 
								 newT2.setOperator(OperationType.NOCHANGE);
								 newT2.setVCOperation(OperationType.NOCHANGE);
								 oldT2.setOperator(OperationType.NOCHANGE);
								 oldT2.setVCOperation(OperationType.NOCHANGE);
								 newT2.setID(oldT2.getID());
								 newT2.matchOldT2VC(oldT2);
							 }
							 else {
								 newT2.setOperator(OperationType.CHANGE);
								 newT2.setVCOperation(OperationType.NOCHANGE);
								 oldT2.setOperator(OperationType.CHANGE);
								 oldT2.setVCOperation(OperationType.NOCHANGE);
								 newT2.setID(oldT2.getID());
								 newT2.matchOldT2VC(oldT2);
							 }
							 
						 }
						 else {
							 newT2.setOperator(OperationType.CHANGEDESC);
							 newT2.setVCOperation(OperationType.CHANGEDESC);
							 oldT2.setOperator(OperationType.CHANGEDESC);
							 oldT2.setVCOperation(OperationType.CHANGEDESC);
							 newT2.setID(oldT2.getID());
							 newT2.matchOldT2VC(oldT2);
						 }
					 }
//					 else if(res == CompareResult.NOEQUAL ) { //如果不相同
//						 continue;
//						 
//					 }
//					 else if(res == CompareResult.CHANGED ) { //如果只有C项不相同
//						 newT2.setOperator(OperationType.CHANGE);
//						 newT2.setVCOperation(OperationType.CHANGE);
//						 oldT2.setOperator(OperationType.CHANGE);
//						 oldT2.setVCOperation(OperationType.CHANGE);
//						 newT2.setID(oldT2.getID());
//						 newT2.matchOldT2VC(oldT2);
//					 }
					 else {
						 continue;
					 }
					 
					 
					 
				 }
			 }
			 
			
			
			
			
			 for(String opstr : opSet ) {
				 
				 if(!oldVT2Map.containsKey(opstr)  || !newT2Map.containsKey(opstr)) {
					 continue;
					 
				 }
				 T2Model newT2 = newT2Map.get(opstr);
				 T2Model oldT2 = oldVT2Map.get(opstr);
				 
					 //如果这个新T2已经判断过了，则跳过
					 if(newT2.getOperator() == OperationType.NOCHANGE 
							 || newT2.getOperator() == OperationType.CHANGEDESC
							 || newT2.getOperator() == OperationType.CHANGE) {
						 continue;
					 }					 
					 
					 
					 //for(T2Model oldT2 : oldVT2Map.get(opstr) ) 
						 
						//如果这个老T2已经判断过了，则跳过
						 if(oldT2.getOperator() == OperationType.NOCHANGE 
								 || oldT2.getOperator() == OperationType.CHANGEDESC
								 || oldT2.getOperator() == OperationType.CHANGE) {
							 continue;
						 }
						 
//						 CompareResult res = comapreChangedT2( newT2,oldT2 , featureCodeList);
//						 if(res == CompareResult.EQUAL) { //如果完全相同
//							 
//							 if(newT2.getOP().equals(oldT2.getOP())) {
//								 newT2.setOperator(OperationType.NOCHANGE);
//								 newT2.setVCOperation(OperationType.NOCHANGE);
//								 oldT2.setOperator(OperationType.NOCHANGE);
//								 oldT2.setVCOperation(OperationType.NOCHANGE);
//								 newT2.setID(oldT2.getID());
//								 newT2.matchOldT2VC(oldT2);
//							 }
//							 else {
//								 newT2.setOperator(OperationType.CHANGEDESC);
//								 newT2.setVCOperation(OperationType.CHANGEDESC);
//								 oldT2.setOperator(OperationType.CHANGEDESC);
//								 oldT2.setVCOperation(OperationType.CHANGEDESC);
//								 newT2.setID(oldT2.getID());
//								 newT2.matchOldT2VC(oldT2);
//							 }
//							 
//						 }
//						 else if(res == CompareResult.NOEQUAL ) { //如果不相同
//							 continue;
//							 
//						 }
//						 else if(res == CompareResult.CHANGED ) { //如果O项都相同
							 newT2.setOperator(OperationType.CHANGE);
							 newT2.setOperator(OperationType.CHANGE);
							 newT2.setVCOperation(OperationType.CHANGE);
							 
							 oldT2.setOperator(OperationType.CHANGE);
							 oldT2.setVCOperation(OperationType.CHANGE);
							 newT2.setID(oldT2.getID());
							 
							 newT2.matchOldT2VC(oldT2);
//						 }
//						 else {
//							 continue;
//						 }
						 
				 
			 }
			 

				//打印结果
					List<T2Model> t2list = newPkg.getT2List();
					
					System.out.println("new t2 in package ["+newPkg.getPackageCode() +"] size :"+ t2list.size());
					for(T2Model t2 : t2list) {
							System.out.println("print t2 :"+t2.printT2());
					}
					t2list = oldPkg.getT2List();
					
					System.out.println("Old t2 in package ["+oldPkg.getPackageCode() +"] size :"+ t2list.size());
					for(T2Model t2 : t2list) {
							System.out.println("print t2 :"+t2.printT2());
					}


			 
			 
			 return changedFeatureSet;
			

	 }
	 
	 //比较2个T2是否变化
	 private CompareResult comapreChangedT2(T2Model newT2, T2Model oldT2,List<String> featureCodeList) {
		 boolean isEqual = true;
		 
//		 if(newT2.getFeatureOpt().size() != oldT2.getFeatureOpt().size()) {
//			 return CompareResult.NOEQUAL;
//		 }
		 
//		 String opstr1 = formatOPStr(newT2.getOP());
//		 String opstr2 = formatOPStr(oldT2.getOP());
//		 
		 for(String fc : featureCodeList) {
			 boolean newOption = false;
			 boolean oldOption = false;
			 
			 if(this.extCodeSet.contains(fc)) {
				 continue;
			 }
			 
			 if(newT2.getOptValue(fc) == null ) {
				 newOption = false; 
			 }
			 else{
				 newOption = newT2.getOptValue(fc);
			 }
			 if(oldT2.getOptValue(fc) == null ) {
				 oldOption = false;
			 }
			 else{
				 oldOption = oldT2.getOptValue(fc);
			 }
			 
			 if(newOption == oldOption ) {
				 continue;
			 }
//			 else if(newOption != oldOption && OfeatureSet.contains(fc)) {  //找到O或者内饰的feature是不相同的，说明不匹配
//				 return CompareResult.NOEQUAL;
//			 }
			 else if(newOption != oldOption ) {  //发现有变更
				 isEqual = false;
			 }
			 
		 }
		 
		 if(isEqual == false) { //有变更的情况
			 return CompareResult.NOEQUAL;
		 }
		//完全一致的情况
		 return CompareResult.EQUAL;

	 }
	 public enum CompareResult {

		    
		    EQUAL, //  完全相同 
		    NOEQUAL, /// / 不相同
		    CHANGED, /// / 
		    UNKNOWN  
		}


	
}
