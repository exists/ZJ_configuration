package com.sotos.bom.configmgt.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.apache.commons.lang3.StringUtils;

import com.sotos.bom.configmgt.model.T2Model;


public class OPRuleUtils {

    /**
     * 表达式解析
     */
	
    public static ExpressionEvaluator ExpressionEvaluator;
    
    //private static String opruleString = "";
    static {// 静态初始化块
    	ExpressionEvaluator = new ExpressionEvaluator();
        
    }
    public OPRuleUtils() {
        //ExpressionEvaluator = new ExpressionEvaluator();
    };

    public enum ExpressionNodeType {

        Unknown, 
        LParentheses, // (
        RParentheses, /// )
        And, // & (逻辑与)
        Or, /// / (逻辑或)
        LowOr, /// / (逻辑或)
        Not, /// - (逻辑非)
        String, 
        Logic,
        StartWith, // 已什么开始
        EndWith// 已什么结尾

    }


    public static class ExpressionNode {
    	
    	
        private String value;

        private ExpressionNodeType type;

        private int pri;

        private ExpressionNode unitaryNode;
     
        private Object logic;


        public ExpressionNode(String value) {
            this.value = value;
            this.type = parseNodeType(value);
            this.pri = getNodeTypePRI(this.type);
            //this.numeric = null;
            this.logic = null;
        }
 

        public Object getLogic() {
            if (this.logic == null) {
                if (this.type != ExpressionNodeType.Logic) {
                    return false;
                }
                else if(value.equals("true") || value.equals("T")) {
                	return true;
                }
                else if(value.equals("false") || value.equals("F")) {
                	return false;
                }
                
            }
            return logic;
        }
        
        public void setLogic(Object logic) {
            this.logic = logic;
            this.value = this.logic.toString();
        }
        

        /**
         * 设置或返回与当前节点相关联的一元操作符节点
         *
         * @param unitaryNode
         */
        public void setUnitaryNode(ExpressionNode unitaryNode) {
            this.unitaryNode = unitaryNode;
        }

        /**
         * 解析节点类型
         *
         * @param value
         * @return
         */
        private ExpressionNodeType parseNodeType(String value) {
            if (StringUtils.isEmpty(value)) {
                return ExpressionNodeType.Unknown;
            }
            switch (value) {
	            case "(":
	                return ExpressionNodeType.LParentheses;
	            case ")":
	                return ExpressionNodeType.RParentheses;
	            case "&":
	                return ExpressionNodeType.And;
	            case "/":
	                return ExpressionNodeType.Or;
	            case "#":
	                return ExpressionNodeType.LowOr;
	            case "-":
	                return ExpressionNodeType.Not;
            }
            if(isLogic(value)) {
            	return ExpressionNodeType.Logic;
            }
            if (value.contains("\"")) {
                return ExpressionNodeType.String;
            }
            return ExpressionNodeType.Unknown;
        }

        /**
         * 获取各节点类型的优先级
         *
         * @param nodeType
         * @return
         */
        private int getNodeTypePRI(ExpressionNodeType nodeType) {
            switch (nodeType) {
	            case LParentheses:
	            case RParentheses:
	                return 9;

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
        public boolean isLogic(String op) {
        	if(op.equals("true") || op.equals("false") || op.equals("T") || op.equals("F"))
        		return true;
        	return false;
        }



        /**
         * 判断某个字符后是否需要更多的操作符
         *
         * @param c
         * @return
         */
        public boolean needMoreOperator(char c) {
            switch (c) {
            case 't':
            case 'r':
            case 'u':
            case 'f':
            case 'a':
            case 'l':
            case 's':

                return true;
            }
            // //数字则需要更多
            return Character.isDigit(c);
        }

        /**
         * 判断两个字符是否是同一类
         *
         * @param c1
         * @param c2
         * @return
         */
        public boolean IsCongener(char c1, char c2) {
            if ((c1 == '(') || (c2 == '(')) {
                return false;
            }
            if ((c1 == ')') || (c2 == ')')) {
                return false;
            }
            if ((c1 == '"') || (c2 == '"')) {
                return false;
            }
            if (Character.isDigit(c1) || (c1 == '.')) {
                // c1为数字,则c2也为数字
                return (Character.isDigit(c2) || (c2 == '.'));
            }
            return (!Character.isDigit(c2) && (c2 != '.'));
        }

        /**
         * 判断某个字符是否是空白字符
         *
         * @param c
         * @return
         */
        public boolean IsWhileSpace(char c) {
            return c == ' ' || c == '\t';
        }

        /**
         * 判断是否是一元操作符节点
         *
         * @param nodeType
         * @return
         */
        public static boolean IsUnitaryNode(ExpressionNodeType nodeType) {
        	
            //return (nodeType == ExpressionNodeType.Not);
        	return false;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public ExpressionNodeType getType() {
            return type;
        }

        public void setType(ExpressionNodeType type) {
            this.type = type;
        }

        public int getPri() {
            return pri;
        }

        public void setPri(int pri) {
            this.pri = pri;
        }

        public ExpressionNode getUnitaryNode() {
            return unitaryNode;
        }
    }


    public static class ExpressionParser {

        // 当前分析的表达式
        private String expression;

        // 当前读取的位置
        private int position;

        public String getExpression() {
            return expression;
        }

        public void setExpression(String expression) {
            this.expression = expression;
        }

        public int getPosition() {
            return position;
        }

        public void setPosition(int position) {
            this.position = position;
        }

        public ExpressionParser(String expression) {
            this.expression = expression;
            this.position = 0;
        }

        /**
         * 读取下一个表达式节点,如果读取失败则返回null
         *
         * @return
         */
        public ExpressionNode readNode()  {
            ExpressionNode s = new ExpressionNode(null);
            // 空格的位置
            int whileSpacePos = -1;
            boolean flag = false;
            StringBuffer buffer = new StringBuffer(10);
            while (this.position < this.expression.length()) {
                char c = this.expression.charAt(this.position);
                if (c == '"') {
                    flag = !flag;
                    if (!flag) {
                        this.position++;
                        buffer.append(c);
                        break;
                    }
                    if (buffer.length() != 0) {
                        break;
                    }
                }
                if (flag) {
                    this.position++;
                    buffer.append(c);
                } else {
                    if (s.IsWhileSpace(c)) {
                        if ((whileSpacePos >= 0) && ((this.position - whileSpacePos) > 1)) {
                            throw new ExpressionException(
                                    String.format("表达式\"%s\"在位置(%s)上的字符非法!", this.getExpression(), this.getPosition()));
                        }
                        if (buffer.length() == 0) {
                            whileSpacePos = -1;
                        } else {
                            whileSpacePos = this.position;
                        }
                        this.position++;
                        continue;
                    }
                    if ((buffer.length() == 0) || s.IsCongener(c, buffer.charAt(buffer.length() - 1))) {
                        this.position++;
                        buffer.append(c);
                    } else {
                        break;
                    }
                    if (!s.needMoreOperator(c)) {
                        break;
                    }
                }
            }
            if (buffer.length() == 0) {
                return null;
            }
            ExpressionNode node = new ExpressionNode(buffer.toString());
            if (node.getType() == ExpressionNodeType.Unknown) {
                throw new ExpressionException(String.format("约束规则\"%s\"存在未定义的选装包，请更正选装包约束规则",node.getValue(),
                        this.getPosition() - node.getValue().length(), node.getValue()));
            }
            return node;
        }

    }

    public static class ExpressionEvaluator {

        private ExpressionEvaluator() {

        }

        /**
         * 将算术表达式转换为逆波兰表达式
         *
         * @param expression
         *            要计算的表达式,如"1+2+3+4"
         * @return
         */
        private static  List<ExpressionNode> parseExpression(String expression) {
            if (StringUtils.isEmpty(expression)) {
                return new ArrayList<ExpressionNode>();
            }

            List<ExpressionNode> listOperator = new ArrayList<ExpressionNode>(10);
            Stack<ExpressionNode> stackOperator = new Stack<ExpressionNode>();

            ExpressionParser expParser = new ExpressionParser(expression);
            ExpressionNode beforeExpNode = null; // 前一个节点
            ExpressionNode unitaryNode = null; // 一元操作符
            ExpressionNode expNode;
            // 是否需要操作数
            boolean requireOperand = false;

            while ((expNode = expParser.readNode()) != null) {
                if ((expNode.getType() == ExpressionNodeType.String)
                        || (expNode.getType() == ExpressionNodeType.Logic)) {
                    // 操作数， 直接加入后缀表达式中
                    if (unitaryNode != null) {
                        // 设置一元操作符节点
                        expNode.setUnitaryNode(unitaryNode);
                        unitaryNode = null;
                    }

                    listOperator.add(expNode);
                    requireOperand = false;
                    continue;
                } else if (expNode.getType() == ExpressionNodeType.LParentheses) {
                    // 左括号， 直接加入操作符栈
                    stackOperator.push(expNode);
                    continue;
                } else if (expNode.getType() == ExpressionNodeType.RParentheses) {
                    // 右括号则在操作符栈中反向搜索，直到遇到匹配的左括号为止，将中间的操作符依次加到后缀表达式中。
                    ExpressionNode lpNode = null;
                    while (stackOperator.size() > 0) {
                        lpNode = stackOperator.pop();
                        if (lpNode.getType() == ExpressionNodeType.LParentheses)
                            break;
                        listOperator.add(lpNode);
                    }
                    if (lpNode == null || lpNode.getType() != ExpressionNodeType.LParentheses) {
                        throw new ExpressionException(String.format("在表达式\"%s\"中没有与在位置(%s)上\")\"匹配的\"(%s)\"字符!",expParser.getExpression(), expParser.getPosition()));
                    }
                } else {
                    if (stackOperator.size() == 0) {
                        // 第一个节点则判断此节点是否是一元操作符"+,-,!,("中的一个,否则其它都非法
                        if (listOperator.size() == 0 && !(expNode.getType() == ExpressionNodeType.LParentheses
                                || expNode.getType() == ExpressionNodeType.Not)) {
                            // 后缀表达式没有任何数据则判断是否是一元操作数
                            if (ExpressionNode.IsUnitaryNode(expNode.getType())) {
                                unitaryNode = expNode;
                            } else {
                                // 丢失操作数
                                throw new ExpressionException(String.format("表达式\"%s\"在位置(%s)上缺少操作数!",
                                        expParser.getExpression(), expParser.getPosition()));
                            }
                        } else {
                            // 直接压入操作符栈
                            stackOperator.push(expNode);
                        }
                        requireOperand = true; // 下一个节点需要操作数
                        continue;
                    } else {
                        if (requireOperand) {
                            // 如果需要操作数则判断当前的是否是"+","-"号(一元操作符),如果是则继续
                            if (ExpressionNode.IsUnitaryNode(expNode.getType()) && unitaryNode == null) {
                                unitaryNode = expNode;
                            } else {
                                // 丢失操作数
                                throw new ExpressionException(String.format("表达式\"%s\"在位置({1})上缺少操作数!",
                                        expParser.getExpression(), expParser.getPosition()));
                            }
                        } else {
                            // 对前面的所有操作符进行优先级比较
                            do {
                                // 取得上一次的操作符
                                beforeExpNode = stackOperator.peek();

                                // 如果前一个操作符优先级较高，则将前一个操作符加入后缀表达式中
                                if (beforeExpNode.getType() != ExpressionNodeType.LParentheses
                                        && (beforeExpNode.getPri() - expNode.getPri()) >= 0) {
                                    listOperator.add(stackOperator.pop());
                                } else {
                                    break;
                                }

                            } while (stackOperator.size() > 0);

                            // 将操作符压入操作符栈
                            stackOperator.push(expNode);
                            requireOperand = true;
                        }
                    }
                }
            }

            if (requireOperand) {
                // 丢失操作数
                throw new ExpressionException(
                        String.format("表达式\"%s\"在位置({1})上缺少操作数!", expParser.getExpression(), expParser.getPosition()));
            }
            // 清空堆栈
            while (stackOperator.size() > 0) {
                // 取得操作符
                beforeExpNode = stackOperator.pop();
                if (beforeExpNode.getType() == ExpressionNodeType.LParentheses) {
                    throw new ExpressionException(String.format("表达式\"%s\"中括号不匹配,丢失右括号!", expParser.getExpression(),
                            expParser.getPosition()));
                }
                listOperator.add(beforeExpNode);
            }

            return listOperator;
        }

        /**
         * 对逆波兰表达式进行计算
         *
         * @param nodes
         * @return
         */
        @SuppressWarnings({ "rawtypes", "unchecked", "incomplete-switch" })
        private static  Object CalcExpression(List<ExpressionNode> nodes) {
            if (nodes == null || nodes.size() == 0)
                return null;

            if (nodes.size() > 1) {
                int index = 0;
                // 储存数据
                ArrayList values = new ArrayList();
                while (index < nodes.size()) {
                    ExpressionNode node = nodes.get(index);

                    switch (node.getType()) {
                    // 如果是数字，则将值存入 values 中
                    case Logic:
                    case String:
                    	values.add(node.getValue());
                        index++;
                        break;
                    default:
                        // 二元表达式，需要二个参数， 如果是Not的话，则只要一个参数
                        int paramCount = 2;
//                        if (node.getType() == ExpressionNodeType.Not )
//                            paramCount = 2;
                        // 计算操作数的值
                        if (values.size() < paramCount) {
                            throw new ExpressionException("缺少操作数");
                        }
                        // 传入参数
                        Object[] data = new Object[paramCount];
                        for (int i = 0; i < paramCount; i++) {
                            data[i] = values.get(index - paramCount + i);
                        }
                        // 将计算结果再存入当前节点
                        node.setLogic(calculate(node.getType(), data));
                        node.setType(ExpressionNodeType.Logic);
                        // 将操作数节点删除
                        for (int i = 0; i < paramCount; i++) {
                            nodes.remove(index - i - 1);
                            values.remove(index - i - 1);
                        }
                        index -= paramCount;
                        break;
                    }

                }
            }

            if (nodes.size() != 1) {
                throw new ExpressionException("缺少操作符或操作数");
            }
            switch (nodes.get(0).getType()) {
            case Logic:
                return nodes.get(0).getLogic();

            case String:
                return nodes.get(0).getLogic().toString().replace("\"", "");
            }
            throw new ExpressionException("缺少操作数");
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
        private static  Object calculate(ExpressionNodeType nodeType, Object[] data) {
            boolean b1, b2;
            Object obj1 = data[0];
            Object obj2 = null;
            if(data.length >=2) {
            	obj2 = data[1];
            }
            else {
            	obj2 = "1";
            }
            String str1 = obj1.toString();
            String str2 = obj2.toString();

            boolean strflag = str1.contains("\"") || str2.contains("\"");
            str1 = str1.replace("\"", "");
            str2 = str2.replace("\"", "");

            switch (nodeType) {
            
            case And:
                b1 = ConvertToBool(obj1);
                b2 = ConvertToBool(obj2);
                return b1 && b2;
            case Or:
                b1 = ConvertToBool(obj1);
                b2 = ConvertToBool(obj2);
                return b1 || b2;
            case LowOr:
                b1 = ConvertToBool(obj1);
                b2 = ConvertToBool(obj2);
                return b1 || b2;
                
            case Not:
                b1 = ConvertToBool(obj1);
                b2 = ConvertToBool(obj2);
                return b1 && (!b2);

            case StartWith:
                if (!strflag) {
                    return false;
                }
                return str1.startsWith(str2);
            case EndWith:
                if (!strflag) {
                    return false;
                }
                return str1.endsWith(str2);
            }

            return 0;
        }

        /**
         * 某个值转换为bool值
         *
         * @param value
         * @return
         */
        private static  Boolean ConvertToBool(Object value) {  //
            if (value instanceof Boolean) {
                return (Boolean) value;
            }
            else if(value instanceof String) {
            	if("true".equals(value) || "T".equals(value) ) {
            		return new Boolean(true);
            		
            	}
            	else if("false".equals(value) || "F".equals(value)) {
            		return new Boolean(false);
            	}
            }
            
            return value != null;
            
        }


        /**
         *
         * @param expression
         *            要计算的表达式,如"1+2+3+4"
         * @return 返回计算结果,如果带有逻辑运算符则返回true/false,否则返回数值
         */
        public Object eval(String expression) {
        	//expression = expression.replaceAll("-", "&-");
        	expression = expression.replaceAll("#-", "#T-");
        	if(expression.startsWith("-")) {
        		expression = "T"+expression;
        	}
            return CalcExpression(parseExpression(expression));
        }

        public  Object evalThreeOperand(String expression) {
            int index = expression.indexOf("?");
            if (index > -1) {
                String str = expression.substring(0, index);
                String str2 = expression.substring(index + 1);
                index = str2.indexOf(":");

                if (Boolean.parseBoolean((CalcExpression(parseExpression(str))).toString())) {
                    return eval(str2.substring(0, index));
                }
                return eval(str2.substring(index + 1));
            }
            return CalcExpression(parseExpression(expression));
        }

    }
    private static boolean checkRule(String rule) {
    	
    	
        //ExpressionEvaluator exp = new ExpressionEvaluator();
        
        boolean b = (boolean)ExpressionEvaluator.eval(rule);
    	
        
    	return b;
    }
    
	private static String parseRule(String rule,Map<String,Boolean>  OPMap) {
		String strRule = rule;
		//opruleString = rule;
		Iterator<String> it = OPMap.keySet().iterator();
		while( it.hasNext() ) {
			//String fc = featureCodeList.get(i);
			String key = it.next();
			if(OPMap.get(key) == true) {
				strRule = strRule.replaceAll(key, "T");
			}
			else {
				strRule = strRule.replaceAll(key, "F");
			}
		}
		
		return strRule;

	}
	
	
    public static boolean checkByRule(String ifRule,String thenRule, Map<String,Boolean>  OPMap ) {
        //ExpressionEvaluator exp = new ExpressionEvaluator();
		boolean findIf = false;
		boolean findThen = false;

        
		//boolean result = true;
		
	
		 
		 //Map<String, Boolean> featureOpt = t2.getFeatureOpt();

			boolean ifcondition = checkRule(parseRule(ifRule,OPMap));
			if(ifcondition) {
				findIf = true;
				
				boolean thencondition = checkRule(parseRule(thenRule,OPMap));
				
				if(thencondition ) {
					findThen = true;
					//当有1条满足条件，就返回正确
					//return result;
				}
				
				//result = false;
			}
			
			if(findIf == false) {
				return true;
			}
			else if(findIf == true && findThen == false) {
				return false;
			}
			else if(findIf == true && findThen == true) {
				return true;
			}
			
			return true;
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
        ExpressionEvaluator exp = new ExpressionEvaluator();

        
        String s2 = "O1/O3#O2";  // TRUE
        Map<String, Boolean> m = new HashMap<String, Boolean>();
       // m.put("O1", true);
        m.put("O2", true);
        m.put("O3", true);
        System.out.println( checkRule(parseRule(s2,m)));
//
//        String s1 = "F/T&F/F-F/F#-F";  // TRUE
//       System.out.println(OPRuleUtils.checkRule(s1));
//       
//       

    }

}