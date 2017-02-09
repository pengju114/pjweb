/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pj.client.rest;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author luzhenwen
 */
public class RestURICondition {

    // ends，starts，contains，eq，lt，gt，in 和 ID;如: "name.!eq(123.00)",group分组为:
    // 1:name             -- 变量名
    // 2:.!eq(123.00)     -- 条件运算数据
    // 3:!                -- 非运算符号
    // 4:eq               -- 运算符
    // 5:123.00           -- 值
    public static final Pattern CONDICTION_PATTERN = Pattern.compile("^(\\w+)(\\.(!)?([a-z]+)\\(([^\\(\\)]+)\\))?$");
    public static final Pattern DIGIT_PATTERN = Pattern.compile("^\\d+$");
    public static final Pattern NUMBER_PATTERN = Pattern.compile("^[-+]?\\d+(\\.\\d+)?$");

    public static enum Operator {
        ends("ends"),
        starts("starts"),
        contains("contains"),
        eq("eq"),
        lt("lt"),
        gt("gt"),
        in("in"),
        between("between"),
        order("order"),
        id("id");

        private final String name;

        Operator(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return name; //To change body of generated methods, choose Tools | Templates.
        }
    }
    
    private final String condition;
    private final boolean valid;
    private Part firstPart;
    private boolean requireParent;

    public RestURICondition(String conditionString) throws Exception {
        condition = conditionString;
        requireParent = false;
        parseCondition();
        valid = firstPart != null;
        if (valid) {
        }
    }

    public boolean isValid() {
        return valid;
    }

    public String getCondition() {
        return condition;
    }

    public Part getFirstPart() {
        return firstPart;
    }

    public boolean isRequireParent() {
        return requireParent;
    }
    
    

    private void parseCondition() throws Exception {
        LinkedList<Part> parts = new LinkedList<Part>();
        // 先查找 "&" 和 "|",然后切割成部分(part)
        int lastIndex = 0;
        int current = 0;
        int orderCount = 0;
        for (; current < condition.length(); current++) {
            char rs = condition.charAt(current);
            if (rs == Relationship.AND || rs == Relationship.OR) {
                Relationship relationship = new Relationship(rs);
                Part part = parsePart(condition.substring(lastIndex, current));
                part.relationship = relationship;
                if (parts.size() > 0) {
                    parts.getLast().relationship.otherPart = part;
                }
                parts.add(part);
                lastIndex = current + 1;
                if (part.getOperator() == Operator.order) {
                    orderCount ++;
                }
            }
        }
        Part lastPart = parsePart(condition.substring(lastIndex, current));
        if (parts.size() > 0) {
            parts.getLast().relationship.otherPart = lastPart;
        }
        parts.add(lastPart);
        if (lastPart.getOperator() == Operator.order) {
            orderCount++;
        }

        if (orderCount > 1) {
            throw new IllegalArgumentException("illegal condition,order operator can't more than 1");
        }
        if (orderCount > 0 && lastPart.getOperator() != Operator.order) {
            throw new IllegalArgumentException("illegal condition,order operator must at the end of this condition");
        }
        
        if (parts.size() > 0) {
            firstPart = parts.getFirst();
        }
    }

    // 1:name             -- 变量名
    // 2:.!eq(123.00)     -- 条件运算数据
    // 3:!                -- 非运算符号
    // 4:eq               -- 运算符
    // 5:123.00           -- 值
    private Part parsePart(String cdt) throws Exception {
        Matcher matcher = CONDICTION_PATTERN.matcher(cdt);
        if (matcher.find()) {
            Part part = new Part();
            part.var = matcher.group(1);
            part.not = matcher.group(3) != null;
            String operatorName = matcher.group(4);
            String param = matcher.group(5);
            
            // ID
            if (DIGIT_PATTERN.matcher(part.var).find()) {
                if (part.operator != null || param != null || part.not) {
                    throw new IllegalArgumentException("illegal format for condition string:" + cdt + "; ID condition can't contains any other thing");
                }
                part.id = true;
                part.operator = Operator.id;
            } else {
                
                Operator operator = null;
                try {
                    operator = Operator.valueOf(operatorName);
                } catch (Exception e) {
                }
                if (operator == null) {
                    throw new IllegalArgumentException("illegal format for condition string:" + cdt + "; wrong operator:" + part.operator);
                }
                part.operator = operator;
                part.id = false;
                
                
                if (param != null) {
                    // 处理多参数
                    String[] params = param.split(",");
                    for (String p : params) {
                        Part.Param pObj = new Part.Param();
                        pObj.param = p;
                        if (NUMBER_PATTERN.matcher(p).find()) {
                            if (p.lastIndexOf('.') != -1) {
                                pObj.paramType = Part.Param.TYPE_FLOAT;
                            } else {
                                pObj.paramType = Part.Param.TYPE_INTEGER;
                            }
                        } else if ( (p.startsWith("\"") && p.endsWith("\"")) || (p.startsWith("'") && p.endsWith("'")) ) {
                            pObj.paramType = Part.Param.TYPE_STRING;
                        } else if(p.equals("@null")){
                            pObj.paramType = Part.Param.TYPE_NULL;
                        } else{
                            pObj.paramType = Part.Param.TYPE_VAR;
                            requireParent = true;
                        }
                        part.params.add(pObj);
                    }
                }
                
                if (operator == Operator.between) {
                    if (part.params.size() != 2) {
                        throw new IllegalArgumentException("illegal format for condition string:" + cdt + "; between operator must be 2 params");
                    }
                }else if (operator != Operator.in) {
                    if (part.params.size() != 1) {
                        throw new IllegalArgumentException("illegal format for condition string:" + cdt + ";" + operator +" operator must be 1 param");
                    }
                }else{
                    if (part.params.size() < 1) {
                        throw new IllegalArgumentException("illegal format for condition string:" + cdt + ";" + operator +" operator must be >=1 param(s)");
                    }
                }
            }

            return part;
        }
        throw new IllegalArgumentException("illegal format for condition string:" + cdt);
    }

    @Override
    public String toString() {
        return condition; //To change body of generated methods, choose Tools | Templates.
    }

    public static class Part {
        public static class Param{
            public static final int TYPE_NONE = 0;
            public static final int TYPE_STRING = 1;
            public static final int TYPE_INTEGER = 2;
            public static final int TYPE_FLOAT = 3;
            public static final int TYPE_VAR = 4;
            public static final int TYPE_NULL = 5; // 代表数据库 is null
            
            private int paramType = TYPE_NONE;
            private String param;

            public int getParamType() {
                return paramType;
            }

            public String getParam() {
                return param;
            }
            
            
            public Object getParamValue() {
                switch (paramType) {
                    case TYPE_FLOAT:
                        return Float.parseFloat(getParam());
                    case TYPE_INTEGER:
                        return Integer.parseInt(getParam());
                    case TYPE_STRING:
                        return getParam().replaceAll("^['\"]|['\"]$", "");
                    default:
                        return null;
                }
            }
        }

        private boolean not;
        private String var;
        private Operator operator;
        private final List<Param> params = new ArrayList<Param>(3);
        private boolean id;
        

        private Relationship relationship;

        /**
         * @return the not
         */
        public boolean isNot() {
            return not;
        }

        /**
         * @return the var
         */
        public String getVar() {
            return var;
        }

        public Operator getOperator() {
            return operator;
        }


        /**
         * @return the param
         */
        public List<Param> getParams() {
            return params;
        }

        public Relationship getRelationship() {
            return relationship;
        }

        public boolean isId() {
            return id;
        }
        
        public Integer getIdValue(){
            return Integer.parseInt(getVar());
        }

        @Override
        public String toString() {
            return "/" + var + "/" + (not ? "!" : "") + operator + (params.size() < 1 ? "" : ("(" + params + ")")); //To change body of generated methods, choose Tools | Templates.
        }
    }

    public static class Relationship {

        public static final char AND = '&';
        public static final char OR = '|';

        private final char type;
        private Part otherPart;

        Relationship(char t) {
            type = t;
        }

        public char getType() {
            return type;
        }

        public Part getOtherPart() {
            return otherPart;
        }

        @Override
        public String toString() {
            return type + ""; //To change body of generated methods, choose Tools | Templates.
        }

    }
}
