package com.sotos.bom.configmgt.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.sotos.bom.util.StringUtil;


public class FRExpressionNode {

    private String value;

    private FRExpressionNodeType type;

    //private int pri;

//    private UCExpressionNode unitaryNode;
 
    //private Object logic;
    
    private boolean bResult;
    
    private List<FRExpressionNode> subNodes = null;


    protected FRExpressionNode(FRExpressionNodeType type) {
        //this.value = value;
        this.type = type;
        subNodes = new ArrayList<FRExpressionNode>();
        //this.pri = getNodeTypePRI(this.type);
        
        //this.logic = null;
    }
    
    public String print() {
    	StringBuffer sb = new StringBuffer("");
    	//if(subNodes)
    	if(type == FRExpressionNodeType.Option) {
    		return value;
    	}
    	if(subNodes.size() == 1) {
    		return subNodes.get(0).print();
    	}
    	for (FRExpressionNode sub:subNodes) {
    		String s = sub.print();
    		if(!StringUtil.isEmpty(s)) {
    			sb.append(s);
    			
	    		 switch (type) {
	    		 	
	    	        case And:
	    	            sb.append("&");
	    	        case Or:
	    	        	sb.append("/");
	    	        case LowOr:
	    	        	sb.append("#");
	
	    	        case Not:
	    	        	sb.append("-");
	    	        	
	    	        }
    		}
    		 
    		
    	}
    	if(sb.length() != 0) {
    		return sb.toString().substring(0,sb.length()-1);
    	}
    	else {
    		return "";
    	}
    }
    //formated str:A30S&/#-C60A&/#-E00W&/#-#-C60A&/#-E00L&/#-#-
    protected void addSubNode(FRExpressionNode subNode) {
    	subNodes.add(subNode);
    	
    }
    protected List<FRExpressionNode> getSubNodes(){
    	return subNodes;
    }
    
    protected void setResult(boolean bResult) {
    	this.bResult = bResult;
    }
    
    protected boolean getResult() {
    	return this.bResult;
    }

     
    
    protected void setValue(String value) {
    	this.value = value;
    }
    
    protected String getValue() {
    	return this.value;
    }

//    public Object getLogic( ) {
//        if (this.logic == null) {
//            if (this.type != UCExpressionNodeType.Logic) {
//                return false;
//            }
//            else if(value.equals("true") || value.equals("T")) {
//            	return true;
//            }
//            else if(value.equals("false") || value.equals("F")) {
//            	return false;
//            }
//            
//        }
//        return logic;
//    }
//    
//    public void setLogic(Object logic) {
//        this.logic = logic;
//        this.value = this.logic.toString();
//    }
//    

    /**
     * 设置或返回与当前节点相关联的一元操作符节点
     *
     * @param unitaryNode
     */
//    public void setUnitaryNode(UCExpressionNode unitaryNode) {
//        this.unitaryNode = unitaryNode;
//    }

    /**
     * 解析节点类型
     *
     * @param value
     * @return
     */
//    private UCExpressionNodeType parseNodeType(String value) {
////        if (StringUtils.isEmpty(value)) {
////            return UCExpressionNodeType.Unknown;
////        }
//        switch (value) {
//            case "&":
//                return UCExpressionNodeType.And;
//            case "/":
//                return UCExpressionNodeType.Or;
//            case "#":
//                return UCExpressionNodeType.LowOr;
//            case "-":
//                return UCExpressionNodeType.Not;
//        }
//        if(isLogic(value)) {
//        	return UCExpressionNodeType.Logic;
//        }
//
//        return UCExpressionNodeType.Top;
//    }

    /**
     * 获取各节点类型的优先级
     *
     * @param nodeType
     * @return
     */
    private int getNodeTypePRI(FRExpressionNodeType nodeType) {
        switch (nodeType) {
            case Or:
                return 7;
            
            case And:
            	return 5;
            	
            case Not:
                return 3;
           	
            case LowOr:
                return 1;
                
            default:
                return 0;
        }

    }

    /**
     * 判断是否为逻辑
     *
     * @param op
     * @return
     */
//    public boolean isLogic(String op) {
//    	if(op.equals("true") || op.equals("false") || op.equals("T") || op.equals("F"))
//    		return true;
//    	return false;
//    }

//    public boolean isString(String op) {
//        return op.matches("^[a-z0-9A-Z]+$");
//    }

    /**
     * 判断某个字符后是否需要更多的操作符
     *
     * @param c
     * @return
     */
//    public boolean needMoreOperator(char c) {
//        switch (c) {
//        case 't':
//        case 'r':
//        case 'u':
//        case 'f':
//        case 'a':
//        case 'l':
//        case 's':
//
//            return true;
//        }
//        // //数字则需要更多
//        return Character.isDigit(c);
//    }

    /**
     * 判断两个字符是否是同一类
     *
     * @param c1
     * @param c2
     * @return
     */
//    public boolean IsCongener(char c1, char c2) {
//        if ((c1 == '(') || (c2 == '(')) {
//            return false;
//        }
//        if ((c1 == ')') || (c2 == ')')) {
//            return false;
//        }
//        if ((c1 == '"') || (c2 == '"')) {
//            return false;
//        }
//        if (Character.isDigit(c1) || (c1 == '.')) {
//            // c1为数字,则c2也为数字
//            return (Character.isDigit(c2) || (c2 == '.'));
//        }
//        return (!Character.isDigit(c2) && (c2 != '.'));
//    }

    /**
     * 判断某个字符是否是空白字符
     *
     * @param c
     * @return
     */
//    public boolean IsWhileSpace(char c) {
//        return c == ' ' || c == '\t';
//    }

    /**
     * 判断是否是一元操作符节点
     *
     * @param nodeType
     * @return
     */
//    public static boolean IsUnitaryNode(UCExpressionNodeType nodeType) {
//    	
//        //return (nodeType == ExpressionNodeType.Not);
//    	return false;
//    }
//
//    public String getValue() {
//        return value;
//    }
//
//    public void setValue(String value) {
//        this.value = value;
//    }

    protected FRExpressionNodeType getType() {
        return type;
    }

    protected void setType(FRExpressionNodeType type) {
        this.type = type;
    }

//    public int getPri() {
//        return pri;
//    }
//
//    public void setPri(int pri) {
//        this.pri = pri;
//    }

//    public UCExpressionNode getUnitaryNode() {
//        return unitaryNode;
//    }


}
