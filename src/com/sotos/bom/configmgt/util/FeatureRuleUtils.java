package com.sotos.bom.configmgt.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.Stack;
import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;



public class FeatureRuleUtils {

    /**
     * 表达式解析
     */
    public static FRExpressionEvaluator FRExpressionEvaluator ;
    
    static {// 静态初始化块
    	FRExpressionEvaluator = new FRExpressionEvaluator();
        
    }

    public FeatureRuleUtils() {            
        //UCExpressionEvaluator = new UCExpressionEvaluator();
    };





    //UC字符串格式化，自动排序
    public static String formatFRString(String frs) {
    	String formatStr =  formatByKey(frs.replace(" ", ""),0);
    	
    	return formatStr;
    }
    
    private static final String[] priLevel = new String[]{"#","-","&","/"};
    public static String  formatByKey(String frStr, int level) {
    	if(level > 3 ) {
    		return frStr;
    	}
    	
    	String[] strArray  = frStr.split(priLevel[level] );
    	StringBuffer sb = new StringBuffer();
    	
    	if(!priLevel[level].equals("-") ) {
	    	SortedSet<String> sortedTreeSet = new TreeSet<String>(); 
	    	for (String str : strArray){
	    		String formateStr = formatByKey(str,  level+1) ;
	    		sortedTreeSet.add(formateStr);
	    	}
	    	
	    	Iterator<String> strItr = sortedTreeSet.iterator();
	    	while(strItr.hasNext()){
	    		sb.append(strItr.next()).append(priLevel[level]); 
	    	}
    	}
    	else if(strArray.length>1) {
    		String str1 = formatByKey(strArray[0],  level+1) ;
    		String str2 = formatByKey(strArray[1],  level+1) ;
    		
    		sb.append(str1).append("-").append(str2).append("-");
    	}
    	else {
    		return formatByKey(frStr,  level+1);
    	}
    	
    	return sb.substring(0, sb.length()-1);
    	
    }
    
    public static boolean checkByRule(String rule,Map<String, Boolean> featureOpt) throws Exception {
    	
    	
    	boolean b = eval(loadFRString(rule), featureOpt);
        
    	return b;
    }
    
    public static boolean checkByRule(FRExpressionNode topNode,Map<String, Boolean> featureOpt) throws Exception {
    	
    	
    	boolean b = eval(topNode, featureOpt);
        
    	return b;
    }


    public static boolean eval(FRExpressionNode topNode,Map<String, Boolean> featureOpt) throws Exception {
    	boolean b =false;
    	
    	b =  FRExpressionEvaluator.eval(topNode,featureOpt);
 
    	return b;
    }



    //加载UCString结构化
    public static FRExpressionNode loadFRString(String frStr) {
    	
    	return loadByKey(frStr.replace(" ", ""),0);
    }
    

    public static FRExpressionNode  loadByKey(String frStr, int level) {
    	
    	
    	FRExpressionNodeType type = null;
    	if(level == 4) {
    		type = FRExpressionNodeType.Option;
    		
    	}
    	else if(level == 0) {
    		type = FRExpressionNodeType.LowOr;
    	}
    	else if(level == 1) {
    		type = FRExpressionNodeType.Not;
    	}
    	else if(level == 2) {
    		type = FRExpressionNodeType.And;
    	}
    	else if(level == 3) {
    		type = FRExpressionNodeType.Or;
    	}
    	
    	FRExpressionNode newNode = new FRExpressionNode(type);
//    	if(level > 3 ) {
//    		return null;
//    	}
//    	if(!ucStr.contains(priLevel[level])) {
//    		
//    	}
    	if(type == FRExpressionNodeType.Option) {
    		newNode.setValue(frStr);
    		return newNode;
    	}
    	String[] strArray  = frStr.split(priLevel[level] );
    	//StringBuffer sb = new StringBuffer();
    	
    	if(type != FRExpressionNodeType.Not ) {
    		//List<UCExpressionNode> subList = new ArrayList<UCExpressionNode>();
	    	//SortedSet<UCExpressionNode> sortedTreeSet = new TreeSet<UCExpressionNode>(); 
	    	for (String str : strArray){
	    		FRExpressionNode n = null;
	    		
	    		if(level != 4) {
	    			n = loadByKey(str,  level+1) ;
	    		}
	    		else {
	    			n.setValue(str);
	    		}
	    		
	    		//subList.add(nodes);
	    		//正式代码要先排序
	    		//sortedTreeSet.add(n);
	    		//排完序再加入
	    		newNode.addSubNode(n);
	    	}
	    	
//	    	Iterator<UCExpressionNode> strItr = sortedTreeSet.iterator();
//	    	while(strItr.hasNext()){
//	    		
//	    		//sb.append(strItr.next()).append(priLevel[level]); 
//	    	}
    	}
    	else if(strArray.length>1) {
    		//UCExpressionNode n = new UCExpressionNode(type, "");
    		
    		FRExpressionNode node1 = loadByKey(strArray[0],  level+1) ;
    		FRExpressionNode node2 = loadByKey(strArray[1],  level+1) ;
    		newNode.addSubNode(node1);
    		newNode.addSubNode(node2);
    		//sb.append(str1).append("-").append(str2).append("-");
    	}
    	else {
    		FRExpressionNode n = loadByKey(frStr,  level+1) ;
    		newNode.addSubNode(n);
    		
    	}
    	
    	return newNode;
    	
    }
    
    /**
     * 测试
     *
     * @方法名:main
     * @参数 @param args
     * @返回类型 void
     */
    public static void main(String[] args) {
    	
    	//System.out.println(!true);
        FRExpressionEvaluator exp = new FRExpressionEvaluator();
        String s1 = "R35R&F11#F2/F1&B3/F4-F5-9#F6&F8/F7-F9/F10#A993-F7/F0";
        //List<ExpressionNode> nodes =  exp.parseExpression(s1);
        //String s0 = formatUCString(s1);
        FRExpressionNode top = loadFRString(s1);
        
        //System.out.println("formated str:"+s0);
        
        
        String s2 = "&A30S&C60A&E00W #&C60A&E00L";
        //List<ExpressionNode> nodes =  exp.parseExpression(s1);
        //s0 = formatUCString(s2);
        
        //System.out.println("formated str:"+s0);
        
        
        
//        String s2 = "T-F";  // TRUE
//        System.out.println(exp.eval(s2));



    }

}