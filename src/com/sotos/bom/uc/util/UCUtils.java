package com.sotos.bom.uc.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.SortedSet;
import java.util.Stack;
import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;

import com.sotos.bom.uc.model.UCModel;


public class UCUtils {

    /**
     * 表达式解析
     */
    public static UCExpressionEvaluator UCExpressionEvaluator ;
    
    static {// 静态初始化块
    	UCExpressionEvaluator = new UCExpressionEvaluator();
        
    }
    /**
     * 表达式解析
     */
    public static PackageUCExpressionEvaluator PackageUCExpressionEvaluator ;
    
    static {// 静态初始化块
    	PackageUCExpressionEvaluator = new PackageUCExpressionEvaluator();
        
    }
    public UCUtils() {            
        //UCExpressionEvaluator = new UCExpressionEvaluator();
    };





    //UC字符串格式化，自动排序去重
    public static String formatUCString(String ucs) {
    	String formatStr =  formatByKey(ucs.replace(" ", ""),0);
    	
    	return formatStr;
    }
    public static String formatUCStringForGBOM(String inputUC) {
 	
    	
    	String ucs = inputUC.replace(" ", "");
    	ucs = ucs.replace("\\s", "");
    	ucs = ucs.replace(".", "");
    	ucs = ucs.replace("。", "");
    	ucs = ucs.toUpperCase();
    	
    	int aa0index = ucs.indexOf("AA0");
    	String preString ="";
    	if (aa0index >= 3) {
    		preString = ucs.substring(aa0index -3, aa0index+3);
    	}
    	else {
    		preString = "AA0";
    	}
    	if(ucs.indexOf("&" + preString) >= 0)
    		ucs = ucs.replace("&" + preString, "");
    	else if(ucs.indexOf(preString+"&") >= 0)
    		ucs = ucs.replace(preString+"&", "");
    	else if(ucs.indexOf(preString) >= 0)
    		ucs = ucs.replace(preString, "");
   	
    	
    	String formatStr =  formatByKey(ucs,0);
    	
    	formatStr = formatStr.replaceAll("#", "#"+preString+ " &");
    	if(!formatStr.equals(""))
    		formatStr = preString + " &" + formatStr + " .";
    	else
    		formatStr = preString + formatStr + " .";
    	
    	return formatStr;
    }
  
    
    private static String[] priLevel = new String[]{"#","-","&","/"};
    public static String  formatByKey(String ucStr, int level) {
    	if(level > 3 ) {
    		return ucStr;
    	}
    	
    	String[] strArray  = ucStr.split(priLevel[level] );
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
    		return formatByKey(ucStr,  level+1);
    	}
    	
    	return sb.substring(0, sb.length()-1);
    	
    }



    public static boolean eval(UCExpressionNode topNode,Map<String, Boolean> featureOpt) throws Exception {
    	boolean b =false;
    	
    	b =  UCExpressionEvaluator.eval(topNode,featureOpt);
 
    	return b;
    }

    public static String packageEval(UCModel uc,Map<String, String> packagefeatureOpt) throws Exception {
    	String str = "";
    	
    	str =  PackageUCExpressionEvaluator.eval(uc.getTopNode(),packagefeatureOpt);
 
    	return str;
    }


    //加载UCString结构化
    public static UCExpressionNode loadUCString(String ucs) {
    	
    	return loadByKey(ucs.replace(" ", ""),0);
    }
    public static boolean evalMatch(String parentUC, String childUC) {
    	boolean bMatch = false;
    	
    	if("".equals(childUC)) {
    		System.out.println("AA0直接匹配：true");
    		return true;
    	}
    	
    	UCExpressionNode parentTop = loadUCString(formatUCString(parentUC));
    	UCExpressionNode childTop = loadUCString(formatUCString(childUC));
    	
    	parentTop.printNode(1);
    	childTop.printNode(1);
    	//System.out.println("parentTop:"+parentTop.printNode(0));
    	//System.out.println("childTop :"+childTop.print());
    	bMatch = isUCMatch (parentTop,childTop,1);
    	
    	System.out.println("匹配结果:"+bMatch);
    	return bMatch;
    }
    private static boolean isUCMatch (UCExpressionNode node1,UCExpressionNode node2,int level) {
    	boolean bMatch = true;
    	UCExpressionNode pNode = null; //父节点（比较小的范围）
    	UCExpressionNode cNode = null; //子节点（比较大的范围）
    	boolean needCalc = true;
 
		System.out.println("准备处理匹配关系 level：" + level + "  TYPE: "+ node1.getType() +"  node1("+node1.getValue()+")  node2(" +node2.getValue()+")  ");

    	switch(level) {
    	case 1:   //如果是#
    		pNode = node1;  //下一层是1在前2在后
    		cNode = node2;
    		break;
    	case 2:   //如果是-
    		needCalc = false;
    		pNode = node1;  //下一层是1在前2在后
    		cNode = node2;
    		bMatch =  isUCMatch (pNode.getSubNodes().get(0),cNode.getSubNodes().get(0),level+1);
    		break;
    	case 3:   //如果是&
    		cNode = node1;  //下一层是2在前1在后
    		pNode = node2;
    		break;
    	case 4:   //如果是/
    		cNode = node1;  //下一层是1在前2在后
    		pNode = node2;
    		break;
    	case 5:   //如果是opt
    		if(node1.getValue().equals(node2.getValue())) {
    			bMatch =  true;
    		}
    		else {
    			bMatch =  false;
    		}
    		needCalc = false;
    	}
    	
    	if(needCalc == true) {  //不是-的情况
    		boolean findsubMatch = false;
    		List<UCExpressionNode> pSubNodes = pNode.getSubNodes();
    		List<UCExpressionNode> cSubNodes = cNode.getSubNodes();
    		int i = 0;
	    	for (UCExpressionNode pSub:pSubNodes) {
	    		findsubMatch = false;
	    		i++;
    			System.out.println("---循环处理匹配关系 level：" + level+i + "  TYPE: "+ node1.getType() +"  node1("+node1.getValue()+")  node2(" +node2.getValue()+")    ");
	    		for (UCExpressionNode cSub:cSubNodes) {
	    			
	    			if(isUCMatch (pSub,cSub,level+1)) {
	    				findsubMatch = true;
	    				break;
	    			}
	    			
	    		}
	    		if(findsubMatch == false) {
	    			bMatch =  false;
	    			break;
	    		}
	    		
	    	}
    	}
//    	else {  //是-的情况
//    		List<UCExpressionNode> pSubNodes = pNode.getSubNodes();
//    		List<UCExpressionNode> cSubNodes = cNode.getSubNodes();
//    		bMatch =  isUCMatch (pSubNodes.get(0),cSubNodes.get(0),level+1);
//    		 
//
//    	}
    	
		System.out.println("完成处理匹配关系 level：" + level + "  TYPE: "+ node1.getType() +"  node1("+node1.getValue()+")  node2(" +node2.getValue()+")    结果："+bMatch );

    	return bMatch;
    }
    

    public static UCExpressionNode  loadByKey(String ucStr, int level) {
    	
    	
    	UCExpressionNodeType type = null;
    	if(level == 4) {
    		type = UCExpressionNodeType.Option;
    		
    	}
    	else if(level == 0) {
    		type = UCExpressionNodeType.LowOr;
    	}
    	else if(level == 1) {
    		type = UCExpressionNodeType.Not;
    	}
    	else if(level == 2) {
    		type = UCExpressionNodeType.And;
    	}
    	else if(level == 3) {
    		type = UCExpressionNodeType.Or;
    	}
    	
    	UCExpressionNode newNode = new UCExpressionNode(type);
//    	if(level > 3 ) {
//    		return null;
//    	}
//    	if(!ucStr.contains(priLevel[level])) {
//    		
//    	}
    	if(type == UCExpressionNodeType.Option) {
    		newNode.setValue(ucStr);
    		return newNode;
    	}
    	String[] strArray  = ucStr.split(priLevel[level] );
    	//StringBuffer sb = new StringBuffer();
    	
    	if(type != UCExpressionNodeType.Not ) {
    		//List<UCExpressionNode> subList = new ArrayList<UCExpressionNode>();
	    	//SortedSet<UCExpressionNode> sortedTreeSet = new TreeSet<UCExpressionNode>(); 
	    	for (String str : strArray){
	    		UCExpressionNode n = null;
	    		
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
    		
    		UCExpressionNode node1 = loadByKey(strArray[0],  level+1) ;
    		UCExpressionNode node2 = loadByKey(strArray[1],  level+1) ;
    		newNode.addSubNode(node1);
    		newNode.addSubNode(node2);
    		//sb.append(str1).append("-").append(str2).append("-");
    	}
    	else {
    		UCExpressionNode n = loadByKey(ucStr,  level+1) ;
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
    	
    	;
    	System.out.println("result:"+formatUCString(""));
    	//System.out.println(!true);
        UCExpressionEvaluator exp = new UCExpressionEvaluator();
        String s1 = "R35R&R35R&F11#F2/F1&B3/F4/F4#F6&F8/F7/F10#A993/F0&A994";
        String s2 = "F11#B3/F4/F4#F8/F7/F10#A994";
        //String s1 = "F1&F4&F5#F22";
        //String s2 = "F1/F2&F4/F9#F22&F44/F22";
        //List<ExpressionNode> nodes =  exp.parseExpression(s1);

        
        evalMatch(s1,s2);
        
        
        
//        UCExpressionNode top = loadUCString("");
//        
//        
//        
//
//        try {
//		//	boolean b = eval(top, new HashMap());
//			String b = PackageUCExpressionEvaluator.eval(top, new HashMap());
//			System.out.println("result:"+b);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}



    }

}