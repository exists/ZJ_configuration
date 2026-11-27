package com.sotos.bom.configmgt.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import com.sotos.bom.configmgt.util.OPRuleUtils;
import com.sotos.bom.util.StringUtil;



public class OptionPackagesModel {
	
	List<Set<String>>  optionPackageList = null;
	List<Set<String>>  optionPackageListDesc = null;
	Map<String, String> optionPackageDescMap = null;
	
	protected static final String OPTION_PACKAGE_SEPERATER = "+";  //选装包分隔符
	protected static final String OPTION_PACKAGE_STANDARD = "S";  //标准包
	
	private boolean isFixedOP = true;
	
	private Stack<String> stack = null;
	
	
	public OptionPackagesModel() {
		//this.optionPackageMap = new HashMap<String,Boolean>();
		//this.isFixedOP = isFixedOP;
		optionPackageList = new ArrayList<Set<String>>();
		optionPackageDescMap = new HashMap<String,String>();
		
		stack = new Stack<String>();
	}
	
	public void setIsFixedOP(boolean isFixedOP) {
		this.isFixedOP = isFixedOP;
	}
	
	public boolean getIsFixedOP() {
		return isFixedOP;
	}
	
	protected void setOptionPackageDescMap(Map<String,String> optionPackageDescMap)  {
		this.optionPackageDescMap = optionPackageDescMap;
		
		
		
	}

	public Map<String,String>  getOptionPackageDescMap()  {
		return optionPackageDescMap;
		
		
		
	}

	
	protected Set<String> loadOptionPackageModel(String strOptionPackage)  {
		//loadOptionPackageModel(optionPackageDescMap);
		
//		String[] opts = null;
//		if(strOptionPackage.indexOf(OPTION_PACKAGE_SEPERATER) > 0) {
//			opts = strOptionPackage.split("\\+") ;
//		}
//		else {
//			opts = new String[1];
//			opts[0] = "S";
//			
//		}
		
		String[] opts ;
		opts = strOptionPackage.split("\\+");
		
		Set<String> optionPackageSet = new HashSet<String>();
		
		if(opts.length == 1  && opts[0].equals("S")) {
			optionPackageSet.add("S");
			
		}
		else if(opts.length >= 1 ) {
			for(String opt:opts) {
				
				if(!opt.equals("S")) {
					optionPackageSet.add(opt);
				}
				
			}
			
		}
		
		return optionPackageSet;
		
		
		//optionPackageList.add(optionPackageSet);
		
	}
	
	
	protected void removeOPNotOnList(List<Set<String>> fixedList) {
		
		for(Set<String> freeOPSet : this.getOptionPackageList() ) {
			boolean isFind = false;
			for(Set<String> fixOPSet : fixedList ) {
				if(isSetEqual(freeOPSet,fixOPSet) == true) {
					isFind = true;
				}
				
			}
			
			if(isFind == false) {
				freeOPSet.clear();
			}
			
		}
		
		
	}
	public static boolean isSetEqual(Set<String> set1, Set<String> set2) {
		if (set1 == null && set2 == null) {
		return true; // Both are null

		}

		if (set1 == null || set2 == null || set1.size() != set2.size()

		|| set1.size() == 0 || set2.size() == 0) {
		return false;

		}

		//Iterator<String> ite1 = set1.iterator();

		Iterator<String> ite2 = set2.iterator();

		boolean isFullEqual = true;

		while (ite2.hasNext()) {
			if (!set1.contains(ite2.next())) {
				isFullEqual = false;
			}
		}
		return isFullEqual;

	}
	
	public List<Set<String>> getOptionPackageList(){
		return optionPackageList;
	}
	
	
	//重新生成一遍选择定义表，加入组合的情况
	public void regenerateOPMap() {
		for(Set<String> opset:  optionPackageList) {
			if(opset == null || opset.size() == 0) {
				continue;
			}
			String opstr = genOPstr(opset);
			
			if(!optionPackageDescMap.containsKey(opstr)) {
				
				StringBuffer opdescsb = new StringBuffer();
				for(String optstr:opset) {
					//opsb.append(optstr).append("+");
					if(!StringUtil.isEmpty(optstr) ) {
						opdescsb.append( optionPackageDescMap.get(optstr) ).append(OPTION_PACKAGE_SEPERATER);
					}
				}

				String opdescstr = opdescsb.toString().substring(0,opdescsb.length()-1);
				optionPackageDescMap.put(opstr, opdescstr);
			}
			
			
		}
	}
	
	
	private  void combinOP(String[] strs, int targ, int cur) {
        // TODO Auto-generated method stub
        if(cur == targ) {
        	//System.out.println(stack);
        	Set<String> optionPackageSet = new HashSet<String>();
        	if(stack.size() > 0) {
	        	for(String optStr : stack ) {
	        		if(optStr.contains("/")) {     //应该不会出现
	        			System.out.println("错误：出现不该有的/字符");
//	        			throw new Exception("出现不该有的字符");
//	        			String[] optstrs = optStr.split("/");
//	        			for(String optstr2:optstrs) {
//	        				optionPackageSet.add(optstr2);
//						}
	        		}
	        		optionPackageSet.add(optStr);
	        	}
	        	optionPackageList.add(optionPackageSet);
        	}
            return;
        }
        
        //stack.add(strs[i]);
        combinOP(strs, targ, cur+1);
        //stack.pop();
        
        stack.add(strs[cur]);
        combinOP(strs, targ, cur+1);
        stack.pop();
         
//        for(int i=0;i<strs.length;i++) {
//            if(!stack.contains(strs[i])) {
//                stack.add(strs[i]);
//                combinOP(strs, targ, cur+1);
//                stack.pop();
//            }
//             
//        }
    }
	

	//自动计算自由选装组合
	protected void generateOptionPackageModelFromPackageModel(PackageModel pm) {
		
		
		System.out.println("Staring generate OptionPackageModel From PackageModel:"+pm.getPackageCode());
		Set<String> optSet = new HashSet<String>();
		List<Set<String>>  comboList = new ArrayList<Set<String>>();  //绑定选装包列表
		//List<Map<String,Set<String>>>  conflictList = new ArrayList<Map<String,Set<String>>>(); //互斥选装包列表
		Map<String, Set<String>> confilctMap = new HashMap<String, Set<String>>();//互斥选装包列表
		//默认给一个S
		Set<String>  firstoptSet = new  HashSet<String>();
		firstoptSet.add("S");
		optionPackageList.add(firstoptSet);

		
		
		//
		//optSet.add("S");
		for(int i =0; i < pm.getOptionSize(); i++) {
			String opt = pm.getOptionValue(i);
			
			if(!opt.toUpperCase().equals("O")  && opt.toUpperCase().startsWith("O")) {
				
				if(opt.contains("/")) { //如果有/说明这几个选装包必须是绑定的
					String[] optstrs = opt.split("/");
					boolean isfind = false;
					for(String optstr:optstrs) {
						optSet.add(optstr);
						//先默认出一位
						//break;
						
						for(Set<String> opts : comboList) { //在之前保存的绑定包查一遍，判定有没有加过
							if(opts.contains(optstr)) {
								isfind = true;
								for(String optstr2:optstrs) {
									opts.add(optstr2); //把绑定的选项都加在这个绑定包里
								}
							}
						}
						
					}
					/*
					if(isfind == false) {//如过之前没定义过绑定包，新定义一个
						Set<String> opts = new HashSet<String>();
						for(String optstr2:optstrs) {
							opts.add(optstr2);
						}
						comboList.add(opts);
					}
					*/
				}
				else {//单个选项直接加入临时OPTION集合
					optSet.add(opt);
				}
				
			}
			
		}
		String[] strs = new String[optSet.size()];
		strs = optSet.toArray(strs);
		
		//System.out.println("Staring combinOP:"+pm.getPackageCode());
		combinOP( strs, optSet.size(), 0); //排列组合生成所有选择可能
		//System.out.println("End combinOP:"+pm.getPackageCode());
		/*
		for(Set<String> opset1:  optionPackageList) {
			
			for(Set<String> opts : comboList) { //把每个绑定包检查一遍，如果有发现绑定包的选项没有同时出现在结果中，这去掉这个选项组合结果
				boolean nofind = false;
				boolean firstfind = false;
				
				for(String optstr:opts) {
					if(opset1.contains(optstr)) {
						//nofind = false;
//						if(firstfind == true)
//							firstfind = false;
//						else
							firstfind = true;
					}
					else {
						nofind = true;
					}
				}
				
				if(firstfind == true && nofind == true) {  //如果发现有匹配的同时又有不匹配的情况
					//System.out.println("find uncorrect op set:"+opset.toArray());
					opset1.clear();
					
				}
			
			}
		}
		*/

		Map<String,FamilyModel> familyMap = pm.getFamilyMap();
		//FamilyModel fm;
		//检查option组合如果在一个family下，则他们之间是互斥的
		Iterator<FamilyModel> it = familyMap.values().iterator();
		while(it.hasNext()) {
			FamilyModel fm = it.next();
			if(!fm.isOptionPackage()) {
				continue;
			}
			for (String feature : fm.getFeatureList()) {
				//pm.getOptionValue(index)
				OptionModel om = pm.getOptionbyFeature(feature);
				
				if(om == null ||  !om.isOptional()) {
					continue;
				}
				Set<String> optset = om.getOptions();
				
				for(String opt : optset) {
					for(String feature2 : fm.getFeatureList()) {
						if(feature != feature2) {
							OptionModel om2 = pm.getOptionbyFeature(feature2);
							if(om2 == null || !om2.isOptional()) {
								continue;
							}
							//Map<String, Set<String>> confilctMap = new HashMap<String, Set<String>>();
							Set<String> conflictSet = null;
							if(confilctMap.containsKey(opt)) {//先看有没有记录过
								conflictSet = confilctMap.get(opt); 
								conflictSet.addAll(om2.getOptions());
							}
							else {
								conflictSet = new HashSet<String>();
								conflictSet.addAll(om2.getOptions());
							}
							
							confilctMap.put(opt, conflictSet);
							
							
							
							
						}
					}
					
					
				}
			}
			
		}
		System.out.println("Staring 去除互斥选装包:"+pm.getPackageCode());
		for(Set<String> opset2:  optionPackageList) {//去除互斥选装包
			//System.out.println("Staring 去除互斥选装size:"+opset2.size());
			for(String opt2 : confilctMap.keySet()) { //把每个互斥包检查一遍，如果有发现互斥包的选项同时出现在结果中，这去掉这个选项组合结果
				boolean find = false;
				//boolean firstfind = false;
				if(opset2.contains(opt2)) { //发现有互斥的其中一个
					Set<String>  conflictSet2 = confilctMap.get(opt2);
					for(String conflictopt : conflictSet2) {
						if(opset2.contains(conflictopt)) { //发现另一个也冲突
							find = true;
						}
					}
				}
				
				
				
				
				if(find == true ) {  //如果发现有同时存在冲突的set
					//System.out.println("find uncorrect op set:"+opset.toArray());
					opset2.clear();
					
				}
			
			}
		}

		

		
		
		//regenerateOPMap();

		
	}
	
	public void removeByOPRule(List<String> OPifList, List<String> OPthenList) throws Exception {
		
		if(OPifList.size() != OPthenList.size() ) {
			throw new Exception("Option Package Rule not correct, if size："+OPifList.size()+"; then  size：" +OPthenList.size());
		}
		
		for(Set<String> freeOPSet : this.getOptionPackageList() ) {
			boolean isOK = false;
			
			//重置输入参数
			Map<String,Boolean> opMap = new HashMap<String,Boolean>();
				opMap.put("O1", false);
				opMap.put("O2", false);
				opMap.put("O3", false);
				opMap.put("O4", false);
				opMap.put("O5", false);
				opMap.put("O6", false);
				opMap.put("O7", false);
				opMap.put("O8", false);
				opMap.put("O9", false);
				opMap.put("O10", false);
				opMap.put("O11", false);
				opMap.put("O12", false);
				opMap.put("O13", false);
				opMap.put("O14", false);
				opMap.put("O15", false);
				opMap.put("O16", false);
				opMap.put("O17", false);
				opMap.put("O18", false);
				opMap.put("O19", false);
				opMap.put("O20", false);
				opMap.put("O21", false);
				opMap.put("O22", false);
				opMap.put("O23", false);
				opMap.put("O24", false);
				opMap.put("O25", false);
				opMap.put("O26", false);
				opMap.put("O27", false);
				opMap.put("O28", false);
				opMap.put("O29", false);
				opMap.put("O30", false);
				opMap.put("O31", false);
				opMap.put("O32", false);
				opMap.put("O33", false);
				opMap.put("O34", false);
				opMap.put("O35", false);
				opMap.put("O36", false);
				opMap.put("O37", false);
				opMap.put("O38", false);
				opMap.put("O39", false);
				opMap.put("O40", false);
				opMap.put("O41", false);
				opMap.put("O42", false);
				opMap.put("O43", false);
				opMap.put("O44", false);
				opMap.put("O45", false);
				opMap.put("O46", false);
				opMap.put("O47", false);
				opMap.put("O48", false);
				opMap.put("O49", false);
				opMap.put("O50", false);
				opMap.put("O99", false);

			
			
			for(String opkey: this.getOptionPackageDescMap().keySet()) {
				if(freeOPSet.contains(opkey)) {
					opMap.put(opkey, true);
				}
				else {
					opMap.put(opkey, false);
				}
			}
			
			for(int i = 0; i < OPifList.size(); i++ ) {
				isOK = OPRuleUtils.checkByRule(OPifList.get(i), OPthenList.get(i), opMap);
				
				if(isOK == false) {
					freeOPSet.clear();
				}
				
			}
			
			
			
		}
		

	}
	
	public static String genOPstr(Set<String> opset) {
		
		StringBuffer opsb = new StringBuffer();
		//StringBuffer opdescsb = new StringBuffer();
		for(String optstr:opset) {
			opsb.append(optstr).append("+");
			//opdescsb.append( optionPackageDescMap.get(optstr) ).append(";");
		}
		if(opsb.toString().endsWith("+")) {
			return opsb.toString().substring(0,opsb.length()-1);
		}
		
		return opsb.toString();

		
	}
	 
	
//	public void printOptionPackages() {
//		//Set<String> opSet = null;
//		int i = 1;
//		for(Set<String> opSet : optionPackageList) {
//			
//			System.out.print("选装包 "+i+" : [");
//			
//			Arrays.stream(opSet.toArray()).forEach(System.out::print);
//			i++;
//			System.out.println("] ");
//		}
//	}
	
	public String printOptionPackages(int i) {
		//Set<String> opSet = null;
		Set<String> opSet = optionPackageList.get(i);
		//int i = 1;
		StringBuffer sb = new StringBuffer();
			//System.out.print("选装包 "+i+" : [");
//			sb.append("选装包 ").append(i).append(" : [");
//			
//			for (String s : opSet) {
//				sb.append(s).append(",");
//			}
//			
//			//Arrays.stream(opSet.toArray()).forEach(System.out::print);
//
//			sb.append("] ");
		sb.append("选装包 ").append(i).append(" :  ").append(opSet.toString());
			return sb.toString();
	}
		

}
