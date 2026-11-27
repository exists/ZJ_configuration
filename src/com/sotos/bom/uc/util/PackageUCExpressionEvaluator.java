package com.sotos.bom.uc.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.apache.commons.lang3.StringUtils;

//import com.sotos.bom.uc.util.UCUtils2.ExpressionException;
import com.sotos.bom.util.StringUtil;

public class PackageUCExpressionEvaluator {

    public PackageUCExpressionEvaluator() {

    }

    /**
     * 将算术表达式转换为逆波兰表达式
     *
     * @param expression
     *            要计算的表达式,如"1+2+3+4"
     * @return
     */
//    private static  List<UCExpressionNode> parseExpression(String expression) {
//        if (StringUtils.isEmpty(expression)) {
//            return new ArrayList<UCExpressionNode>();
//        }
//
//        List<UCExpressionNode> listOperator = new ArrayList<UCExpressionNode>(10);
//        Stack<UCExpressionNode> stackOperator = new Stack<UCExpressionNode>();
//
//        UCExpressionParser expParser = new UCExpressionParser(expression);
//        UCExpressionNode beforeExpNode = null; // 前一个节点
//        UCExpressionNode unitaryNode = null; // 一元操作符
//        UCExpressionNode expNode;
//        // 是否需要操作数
//        boolean requireOperand = false;
//
//        while ((expNode = expParser.readNode()) != null) {
//            if (expNode.getType() == UCExpressionNodeType.Logic ) {
//                // 操作数， 直接加入后缀表达式中
//                if (unitaryNode != null) {
//                    // 设置一元操作符节点
//                    expNode.setUnitaryNode(unitaryNode);
//                    unitaryNode = null;
//                }
//
//                listOperator.add(expNode);
//                requireOperand = false;
//                continue;
//            }
//            else {
//                if (stackOperator.size() == 0) {
//                    stackOperator.push(expNode);
//                    requireOperand = true; // 下一个节点需要操作数
//                    continue;
//                } else {
//                    if (requireOperand) {
//
//                            throw new ExpressionException(String.format("表达式\"%s\"在位置({1})上缺少操作数!",
//                                    expParser.getExpression(), expParser.getPosition()));
//                    } else {
//                        // 对前面的所有操作符进行优先级比较
//                        do {
//                            // 取得上一次的操作符
//                            beforeExpNode = stackOperator.peek();
//
//                            // 如果前一个操作符优先级较高，则将前一个操作符加入后缀表达式中
//                            if ((beforeExpNode.getPri() - expNode.getPri()) >= 0) {
//                                listOperator.add(stackOperator.pop());
//                            } else {
//                                break;
//                            }
//
//                        } while (stackOperator.size() > 0);
//
//                        // 将操作符压入操作符栈
//                        stackOperator.push(expNode);
//                        requireOperand = true;
//                    }
//                }
//            }
//        }
//
//        if (requireOperand) {
//            // 丢失操作数
//            throw new ExpressionException(
//                    String.format("表达式\"%s\"在位置({1})上缺少操作数!", expParser.getExpression(), expParser.getPosition()));
//        }
//        // 清空堆栈
//        while (stackOperator.size() > 0) {
//            // 取得操作符
//            beforeExpNode = stackOperator.pop();
//            listOperator.add(beforeExpNode);
//        }
//
//        return listOperator;
//    }

    /**
     * 对逆波兰表达式进行计算
     *
     * @param nodes
     * @return
     * @throws Exception 
     */
    @SuppressWarnings({ "rawtypes", "unchecked", "incomplete-switch" })
    private static  String CalcExpression(UCExpressionNode node,Map<String, String> packagefeatureOpt) throws Exception {

    	int index = 1;
    	List<UCExpressionNode> subnodes = node.getSubNodes();
    	String str = "";
    	
    	if(node.getType() == UCExpressionNodeType.Option) {
    		if( StringUtil.isEmpty(node.getValue())   ) {
    			return "S";
    		}
    		str = packagefeatureOpt.get(node.getValue());
    		node.setStrResult(str);
    		return str;
    	
    	}
    	else if(subnodes.size() == 1) {
    		str = CalcExpression(subnodes.get(0),packagefeatureOpt);
    		node.setStrResult(str);
    		return str;
    	}
    	else {
    		str = CalcExpression(subnodes.get(0),packagefeatureOpt);
    		
    		while(index < subnodes.size()) {
    			String str2 = CalcExpression(subnodes.get(index),packagefeatureOpt);
    			str = calculate(node.getType(), str,str2);
    			
    			index++;
    		}
    		
    		node.setStrResult(str);
    		return str;
    	}
    	

    }

    /**
     * 计算节点的值
     *
     * @param nodeType
     *            节点的类型
     * @param data
     *            要计算的值,有可能是两位或一位数
     * @return
     * @throws Exception 
     */
    @SuppressWarnings("incomplete-switch")
    private static  String calculate(UCExpressionNodeType nodeType, String s1, String s2) throws Exception {
       // String op = "";
       switch (nodeType) {
        
	        case And:
	        	//op = "&";
	        	if(s1.equals("S")  &&  s2.equals("S")  )
	        		return "S";
	        	if(s1.equals("O")  &&  s2.equals("O")  )
	        		return "O";
	        	if(s1.equals("-")  &&  s2.equals("-")  )
	        		return "-";
	        	if(s1.equals("S")  &&  s2.equals("O")  )
	        		return "O";
	        	if(s1.equals("O")  &&  s2.equals("S")  )
	        		return "O";
	        	if(s1.equals("-")  &&  s2.equals("S")  )
	        		return "-";
	        	if(s1.equals("S")  &&  s2.equals("-")  )
	        		return "-";
	        	if(s1.equals("O")  &&  s2.equals("-")  )
	        		return "-";
	        	if(s1.equals("-")  &&  s2.equals("O")  )
	        		return "-";
	        case Or:
	        	if(s1.equals("S")  &&  s2.equals("S")  )
	        		return "S";
	        	if(s1.equals("O")  &&  s2.equals("O")  )
	        		return "O";
	        	if(s1.equals("-")  &&  s2.equals("-")  )
	        		return "-";
	        	if(s1.equals("S")  &&  s2.equals("O")  )
	        		return "S";
	        	if(s1.equals("O")  &&  s2.equals("S")  )
	        		return "S";
	        	if(s1.equals("-")  &&  s2.equals("S")  )
	        		return "S";
	        	if(s1.equals("S")  &&  s2.equals("-")  )
	        		return "S";
	        	if(s1.equals("O")  &&  s2.equals("-")  )
	        		return "O";
	        	if(s1.equals("-")  &&  s2.equals("O")  )
	        		return "O";
	        case LowOr:
	        	if(s1.equals("S")  &&  s2.equals("S")  )
	        		return "S";
	        	if(s1.equals("O")  &&  s2.equals("O")  )
	        		return "O";
	        	if(s1.equals("-")  &&  s2.equals("-")  )
	        		return "-";
	        	if(s1.equals("S")  &&  s2.equals("O")  )
	        		return "S";
	        	if(s1.equals("O")  &&  s2.equals("S")  )
	        		return "S";
	        	if(s1.equals("-")  &&  s2.equals("S")  )
	        		return "S";
	        	if(s1.equals("S")  &&  s2.equals("-")  )
	        		return "S";
	        	if(s1.equals("O")  &&  s2.equals("-")  )
	        		return "O";
	        	if(s1.equals("-")  &&  s2.equals("O")  )
	        		return "O";
	
	        case Not:
	        	if(s1.equals("S")  &&  s2.equals("-")  )
	        		return "S";
	        	if(s1.equals("O")  &&  s2.equals("O")  )
	        		return "O";
	        	if(s1.equals("-")  &&  s2.equals("S")  )
	        		return "-";
	        	if(s1.equals("S")  &&  s2.equals("O")  )
	        		return "O";
	        	if(s1.equals("O")  &&  s2.equals("-")  )
	        		return "O";
	        	if(s1.equals("-")  &&  s2.equals("-")  )
	        		return "-";
	        	if(s1.equals("S")  &&  s2.equals("S")  )
	        		return "-";
	        	if(s1.equals("O")  &&  s2.equals("S")  )
	        		return "-";
	        	if(s1.equals("-")  &&  s2.equals("O")  )
	        		return "-";
	        	
        }

        throw new Exception("不能解析 "+s1 +" "+nodeType +" "+s2 +" ");
    }


 
    /**
     *
     * @param expression
     *            要计算的表达式,如"1+2+3+4"
     * @return 返回计算结果,如果带有逻辑运算符则返回true/false,否则返回数值
     * @throws Exception 
     */
//    public Object eval(String expression) {
//    	//expression = expression.replaceAll("-", "&-");
//    	expression = expression.replaceAll("#-", "#T-");
//    	if(expression.startsWith("-")) {
//    		expression = "T"+expression;
//    	}
//        return CalcExpression(parseExpression(expression));
//    }
    public String eval( UCExpressionNode topNode,Map<String, String> packagefeatureOpt) throws Exception {

    	
    	
    	
        return CalcExpression(topNode,packagefeatureOpt);
    	
    	
    }
   


}
