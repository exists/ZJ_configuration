package com.sotos.bom.uc.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.apache.commons.lang3.StringUtils;

//import com.sotos.bom.uc.util.UCUtils2.ExpressionException;
import com.sotos.bom.util.StringUtil;

public class UCExpressionEvaluator {

    public UCExpressionEvaluator() {

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
    private static  boolean CalcExpression(UCExpressionNode node,Map<String, Boolean> featureOp) throws Exception {

    	int index = 1;
    	List<UCExpressionNode> subnodes = node.getSubNodes();
    	boolean b = true;
    	
    	if(node.getType() == UCExpressionNodeType.Option) {
    		if( StringUtil.isEmpty(node.getValue())   ) {
    			return true;
    		}
    		if(!featureOp.containsKey(node.getValue())) {
    			throw new Exception("Can not find feature code in the room");
    		}
    		
    		b= featureOp.get(node.getValue());
    		node.setResult(b);
    		return b;
    	
    	}
    	else if(subnodes.size() == 1) {
    		b = CalcExpression(subnodes.get(0),featureOp);
    		node.setResult(b);
    		return b;
    	}
    	else {
    		b = CalcExpression(subnodes.get(0),featureOp);
    		
    		while(index < subnodes.size()) {
    			boolean b2 = CalcExpression(subnodes.get(index),featureOp);
    			b = calculate(node.getType(), b,b2);
    			
    			index++;
    		}
    		
    		node.setResult(b);
    		return b;
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
     */
    @SuppressWarnings("incomplete-switch")
    private static  boolean calculate(UCExpressionNodeType nodeType, boolean b1, boolean b2) {
        
       switch (nodeType) {
        
        case And:
            return b1 && b2;
        case Or:
            return b1 || b2;
        case LowOr:
            return b1 || b2;

        case Not:
            return b1 && (!b2);
        }

        return true;
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
    public boolean eval( UCExpressionNode topNode,Map<String, Boolean> featureOp) throws Exception {

    	
    	
    	
        return CalcExpression(topNode,featureOp);
    	
    	
    }
   


}
