/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pj.client.rest;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

/**
 *
 * @author luzhenwen
 */
public final class RestURISQLBuilder {

    public static enum Database {
        MySQL,
        Oracle,
        SQLServer
    }

    private static final Map<RestURICondition.Operator, RestURISQLBuilder.ConditionStringGenerator> CONDITION_STRING_GENERATORS;
    private static final Map<String, OperationSQLGenerator> OPERATION_SQL_GENERATORS;

    static {
        HashMap<RestURICondition.Operator, RestURISQLBuilder.ConditionStringGenerator> map = new HashMap<RestURICondition.Operator, ConditionStringGenerator>(10);

        map.put(RestURICondition.Operator.between, new BetweenConditionStringGenerator());
        map.put(RestURICondition.Operator.contains, new ContainsConditionStringGenerator());
        map.put(RestURICondition.Operator.ends, new EndsConditionStringGenerator());
        map.put(RestURICondition.Operator.eq, new EqConditionStringGenerator());
        map.put(RestURICondition.Operator.gt, new GtConditionStringGenerator());
        map.put(RestURICondition.Operator.in, new InConditionStringGenerator());
        map.put(RestURICondition.Operator.lt, new LtConditionStringGenerator());
        map.put(RestURICondition.Operator.starts, new StartsConditionStringGenerator());
        map.put(RestURICondition.Operator.order, new OrderConditionStringGenerator());
        map.put(RestURICondition.Operator.id, new IdConditionStringGenerator());

        CONDITION_STRING_GENERATORS = Collections.unmodifiableMap(map);

        HashMap<String, OperationSQLGenerator> sqlGenerators = new HashMap<String, OperationSQLGenerator>(5);
        sqlGenerators.put(RestURIPart.OPERATION_ADD, new AddSQLGenerator());
        sqlGenerators.put(RestURIPart.OPERATION_DELETE, new DeleteSQLGenerator());
        sqlGenerators.put(RestURIPart.OPERATION_GET, new GetSQLGenerator());
        sqlGenerators.put(RestURIPart.OPERATION_UPDATE, new UpdateSQLGenerator());

        OPERATION_SQL_GENERATORS = Collections.unmodifiableMap(sqlGenerators);
    }

    private final LinkedList<RestURIPart> restURIParts;
    private final LinkedList<RestURIBuilderDataSource> dataSources;
    private final Database database;

    public RestURISQLBuilder(Database db) {
        database = db;
        restURIParts = new LinkedList<RestURIPart>();
        dataSources = new LinkedList<RestURIBuilderDataSource>();
        addDataSource(new CamelCaseDataSource());
    }

    public RestURISQLBuilder append(RestURIPart part) {
        restURIParts.add(part);
        return this;
    }

    public RestURISQLBuilder append(Collection<RestURIPart> parts) {
        restURIParts.addAll(parts);
        return this;
    }

    public RestURISQLBuilder addDataSource(RestURIBuilderDataSource dataSource) {
        dataSources.addFirst(dataSource);
        return this;
    }

    public Tuple<CharSequence,Object> build() throws Exception{
        if (restURIParts.size() < 1) {
            throw new IllegalArgumentException("no restURIParts");
        }
        return build(restURIParts.getLast());
    }
    
    Tuple<CharSequence,Object> build(RestURIPart part) throws Exception{
        StringBuilder sb = new StringBuilder();
        Tuple<CharSequence,Object> result = new Tuple<CharSequence,Object>();
        result.keyObject = sb;
                
        OperationSQLGenerator sqlGenerator = OPERATION_SQL_GENERATORS.get(part.getOperation());
        Tuple<CharSequence,Object> t1 = sqlGenerator.generateSQL(this, part);
        sb.append(t1.keyObject);
        result.valueObject.addAll(t1.valueObject);

        if (part.getCondition() != null && part.getCondition().isValid()) {
            Tuple<StringBuilder,Object> cdt = ConditionStringGenerator.generateConditionSQL(this, part);
            if (part.getCondition().getFirstPart().getOperator() != RestURICondition.Operator.order) {
                sb.append(" where");
            }
            sb.append(' ').append(cdt.keyObject);
            result.valueObject.addAll(cdt.valueObject);
        }
        result.isUpdate = !RestURIPart.OPERATION_GET.equalsIgnoreCase(part.getOperation());
        return result;
    }

    /**
     * 创建完整select *语句,不包含 where 后面部分
     *
     * @param restURIPart
     * @param vars
     * @return
     */
    CharSequence createSelectSqlByURIPart(RestURIPart restURIPart) {
        StringBuilder builder = new StringBuilder(30);
        builder.append("select ");
        RestURIPartImpl impl = (RestURIPartImpl) restURIPart;
        if (impl.getSelectVars().size() > 0) {
            for (String var : impl.getSelectVars()) {
                builder.append(restURIPart.getTargetAlias()).append('.');
                builder.append(evaluateVar(var)).append(',');
            }
            builder.deleteCharAt(builder.length() - 1);
        }else{
            builder.append('*');
        }
        String table = evaluateTarget(restURIPart.getTarget());
        builder.append(" from ").append(table).append(" as ").append(restURIPart.getTargetAlias());

        return builder;
    }

    /**
     * 创建完整select语句,不包含 where 后面部分
     *
     * @param restURIPart
     * @param vars
     * @return
     */
    CharSequence createSelectSqlByVars(RestURIPart restURIPart, String... vars) {
        StringBuilder builder = new StringBuilder(30);
        builder.append("select ");
        for (String var : vars) {
            String name = evaluateVar(var);
            builder.append(restURIPart.getTargetAlias()).append('.').append(name).append(',');
        }

        builder.deleteCharAt(builder.length() - 1);// 删除最后的","

        String table = evaluateTarget(restURIPart.getTarget());
        builder.append(" from ").append(table).append(" as ").append(restURIPart.getTargetAlias());

        return builder;
    }

    private String evaluateTarget(String target) {
        Iterator<RestURIBuilderDataSource> iterator = dataSources.iterator();
        while (iterator.hasNext()) {
            RestURIBuilderDataSource next = iterator.next();
            String t = next.evaluateTarget(this, target);
            if (t != null && t.length() > 1) {
                return t;
            }
        }
        return "";
    }

    private String evaluateVar(String varName) {
        Iterator<RestURIBuilderDataSource> iterator = dataSources.iterator();
        while (iterator.hasNext()) {
            RestURIBuilderDataSource next = iterator.next();
            String var = next.evaluateVarName(this, varName);
            if (var != null && var.length() > 1) {
                return var;
            }
        }
        return "";
    }

    private Map<String, Object> getKeysAndObjects(RestURIPart part) {
        Iterator<RestURIBuilderDataSource> iterator = dataSources.iterator();
        while (iterator.hasNext()) {
            RestURIBuilderDataSource next = iterator.next();
            Map<String, Object> map = next.getKeysAndObjects(this, part);
            if (map != null && map.size() > 0) {
                return map;
            }
        }
        return null;
    }

    public CharSequence joinString(String sep, String... strings) {
        StringBuilder sb = new StringBuilder();
        for (String string : strings) {
            sb.append(string).append(sep);
        }

        if (strings.length > 0) {
            sb.delete(sb.length() - sep.length(), sb.length());
        }

        return sb;
    }

    

    private static class CamelCaseDataSource implements RestURIBuilderDataSource {

        private static final Pattern UPPER_CASE_PATTERN = Pattern.compile("([A-Z]{2,})");// 缩写大写检测
        private static final Pattern CAMEL_CASE_PATTERN = Pattern.compile("([a-z])([A-Z])([^A-Z])");// 驼峰规则
        private static final Pattern DANGEROUS_CHAR_PATTERN = Pattern.compile("[^\\w]+");

        @Override
        public String evaluateVarName(RestURISQLBuilder restURISQLBuilder, String name) {
            if (name == null) {
                return "";
            }
            // 去掉特殊字符,name只能是字母和数字
            name = DANGEROUS_CHAR_PATTERN.matcher(name).replaceAll("");
            // 将驼峰规则换成用下划线间开的名字,并且全为小写
            return CAMEL_CASE_PATTERN.matcher(UPPER_CASE_PATTERN.matcher(name).replaceAll("_$1_").replaceAll("^\\_|\\_$", "")).replaceAll("$1_$2$3").toLowerCase(Locale.ENGLISH);
        }

        public Map<String, Object> getKeysAndObjects(RestURISQLBuilder restURISQLBuilder, RestURIPart part) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        public String evaluateTarget(RestURISQLBuilder restURISQLBuilder, String target) {
            if (target == null) {
                return "";
            }
            // path,路径对应到表名,表名加上前缀"t_"
            return "t_" + evaluateVarName(restURISQLBuilder, target);
        }
    }
    
    public static final class DefaultDataSource extends CamelCaseDataSource{}

    /**
     * 生成 where 后面的条件语句,不包含where. ends，starts，contains，eq，lt，gt，in, between
     */
    private static abstract class ConditionStringGenerator {

        public static Tuple<StringBuilder,Object> generateConditionSQL(RestURISQLBuilder builder, RestURIPart uriPart) throws Exception{
            RestURICondition condition = uriPart.getCondition();
            if (!condition.isValid()) {
                return null;
            }

            Tuple<StringBuilder,Object> tuple = new Tuple<StringBuilder,Object>();
            tuple.keyObject = new StringBuilder(50);
            // 处理多个条件
            RestURICondition.Part currentPart = condition.getFirstPart();
            
            while (currentPart != null) {
                ConditionStringGenerator currentGenerator = CONDITION_STRING_GENERATORS.get(currentPart.getOperator());
                Tuple<CharSequence,Object> currentTuple = currentGenerator.conditionString(builder, uriPart, currentPart);
                
                tuple.keyObject.append(currentTuple.keyObject);
                tuple.valueObject.addAll(currentTuple.valueObject);
                
                RestURICondition.Relationship relationship = currentPart.getRelationship();
                
                currentPart = null;
                
                if (relationship != null) {
                    // 空格
                    tuple.keyObject.append(' ');
                    currentPart = relationship.getOtherPart();
                    
                    if (currentPart.getOperator() != RestURICondition.Operator.order) { // order不需要 and 和 or
                        switch (relationship.getType()) {
                            case RestURICondition.Relationship.AND:
                                tuple.keyObject.append("and");
                                break;
                            case RestURICondition.Relationship.OR:
                                tuple.keyObject.append("or");
                                break;
                            default:
                                throw new IllegalArgumentException("Illegal condition relationship:" + Character.toString(relationship.getType()));
                        }
                        // 空格
                        tuple.keyObject.append(' ');
                    }
                    

                    
                }
            }
            return tuple;
        }

        /**
         *
         * @param sqlBuilder
         * @param uriPart
         * @param condition
         * @param conditionPart
         * @return
         * @throws Exception
         */
        public abstract Tuple<CharSequence,Object> conditionString(RestURISQLBuilder sqlBuilder, RestURIPart uriPart, RestURICondition.Part conditionPart) throws Exception;

        public CharSequence concatSql(RestURISQLBuilder sqlBuilder, String... strings) {
            StringBuilder sb = new StringBuilder();
            switch (sqlBuilder.database) {
                case MySQL:
                    sb.append("concat(").append(sqlBuilder.joinString(",", strings)).append(')');
                    break;
                case Oracle:
                    sb.append('(').append(sqlBuilder.joinString("||", strings)).append(')');
                    break;
                default:// MS SQL server
                    sb.append('(').append(sqlBuilder.joinString("+", strings)).append(')');
                    break;
            }

            return sb;
        }
        
        public boolean isRequirePathAlias(RestURIPart part){
            return (!part.getOperation().equalsIgnoreCase(RestURIPart.OPERATION_DELETE) && !part.getOperation().equalsIgnoreCase(RestURIPart.OPERATION_UPDATE));
        }
    }

    private static abstract class CommonConditionStringGenerator extends ConditionStringGenerator {

        @Override
        public Tuple<CharSequence,Object> conditionString(RestURISQLBuilder sqlBuilder, RestURIPart uriPart, RestURICondition.Part conditionPart)  throws Exception{
            StringBuilder builder = new StringBuilder();

            Tuple<CharSequence,Object> tuple = new Tuple<CharSequence,Object>();
            // 加上表的别名前缀,update 和 delete不能有别名
            if (isRequirePathAlias(uriPart)) {
                builder.append(uriPart.getTargetAlias()).append('.');
            }
            
            // append 当前rest uri的变量名
            builder.append(sqlBuilder.evaluateVar(conditionPart.getVar()));

            // 空格
            builder.append(' ');
            appendOperator(builder, sqlBuilder, uriPart, conditionPart);
            // 空格
            builder.append(' ');
            appendConditionPattern(builder, tuple, sqlBuilder, uriPart, conditionPart);

            tuple.keyObject = builder;

            return tuple;
        }

        /**
         * 默认实现为 "="操作符
         */
        public void appendOperator(StringBuilder stringBuilder, RestURISQLBuilder sqlBuilder, RestURIPart uriPart, RestURICondition.Part conditionPart) {
            appendEqOperator(stringBuilder, sqlBuilder, uriPart, conditionPart);
        }

        /**
         * 默认实现为 "="操作符
         */
        public void appendEqOperator(StringBuilder stringBuilder, RestURISQLBuilder sqlBuilder, RestURIPart uriPart, RestURICondition.Part conditionPart) {
            if (conditionPart.isNot()) {
                stringBuilder.append('<').append('>');// 不等于 "<>"
            } else {
                stringBuilder.append('=');
            }
        }

        /**
         * 默认只处理单参数情况
         *
         * @param stringBuilder
         * @param sqlBuilder
         * @param uriPart
         * @param condition
         * @param conditionPart
         */
        public void appendConditionPattern(StringBuilder stringBuilder, Tuple<CharSequence,Object> tuple, RestURISQLBuilder sqlBuilder, RestURIPart uriPart, RestURICondition.Part conditionPart) throws Exception{
            if (conditionPart.getOperator() != RestURICondition.Operator.between || conditionPart.getOperator() != RestURICondition.Operator.in) {
                RestURICondition.Part.Param param = conditionPart.getParams().get(0);
                if (param.getParamType() == RestURICondition.Part.Param.TYPE_VAR) {// 如果是变量参数
                    if (uriPart.getParent() == null) {// 变量参数只能引用父 REST URI的
                        throw new IllegalArgumentException("require a parent URIPart for " + uriPart);
                    }
                    appendParentConditionPattern(stringBuilder,tuple, sqlBuilder, uriPart, conditionPart, param);
                } else {
                    stringBuilder.append('?');
                    appendParamValue(tuple, sqlBuilder, uriPart, conditionPart);
                }
            } else {
                throw new UnsupportedOperationException("Unsupported operator:" + conditionPart.getOperator());
            }
        }
        
        public Tuple<CharSequence,Object> generateQuerySQL(RestURISQLBuilder builder, RestURIPart uriPart, RestURICondition.Part.Param param) throws Exception{
            RestURIPartImpl impl = (RestURIPartImpl) uriPart;
            impl.getSelectVars().add(param.getParam());
            return builder.build(uriPart);
        }

        /**
         * 默认实现为 直接追加 父 REST URI 变量参数名
         *
         * @param stringBuilder
         * @param sqlBuilder
         * @param uriPart
         * @param condition
         * @param conditionPart
         * @param param
         */
        public void appendParentConditionPattern(StringBuilder stringBuilder, Tuple<CharSequence,Object> tuple, RestURISQLBuilder sqlBuilder, RestURIPart uriPart, RestURICondition.Part conditionPart, RestURICondition.Part.Param param) throws Exception{
            Tuple<CharSequence,Object> parentTuple = generateQuerySQL(sqlBuilder, uriPart.getParent(), param);
            stringBuilder.append('(').append(parentTuple.keyObject).append(')');
            tuple.valueObject.addAll(parentTuple.valueObject);
        }

        /**
         * 默认实现为直接追加 参数值
         *
         * @param tuple
         * @param sqlBuilder
         * @param uriPart
         * @param condition
         * @param conditionPart
         */
        public void appendParamValue(Tuple<CharSequence,Object> tuple, RestURISQLBuilder sqlBuilder, RestURIPart uriPart, RestURICondition.Part conditionPart) {
            if (conditionPart.isId()) {
                tuple.valueObject.add(conditionPart.getIdValue());
            } else {
                for (RestURICondition.Part.Param param : conditionPart.getParams()) {
                    if (param.getParamType() != RestURICondition.Part.Param.TYPE_NONE && param.getParamType() != RestURICondition.Part.Param.TYPE_NULL) {
                        tuple.valueObject.add(param.getParamValue());
                    }

                }
            }
        }
    }

    private static class EndsConditionStringGenerator extends CommonConditionStringGenerator {

        @Override
        public void appendOperator(StringBuilder stringBuilder, RestURISQLBuilder sqlBuilder, RestURIPart uriPart, RestURICondition.Part conditionPart) {
            if (conditionPart.isNot()) {
                stringBuilder.append("not ");
            }
            stringBuilder.append("like");
        }

        @Override
        public void appendParentConditionPattern(StringBuilder stringBuilder, Tuple<CharSequence,Object> tuple, RestURISQLBuilder sqlBuilder, RestURIPart uriPart, RestURICondition.Part conditionPart, RestURICondition.Part.Param param)  throws Exception{
            Tuple<CharSequence,Object> parentTuple = generateQuerySQL(sqlBuilder, uriPart.getParent(), param);
            stringBuilder.append(concatSql(sqlBuilder, "'%'", ("("+parentTuple.keyObject+")")));
            tuple.valueObject.addAll(parentTuple.valueObject);
        }

        @Override
        public void appendParamValue(Tuple<CharSequence,Object> tuple, RestURISQLBuilder sqlBuilder, RestURIPart uriPart, RestURICondition.Part conditionPart) {
            tuple.valueObject.add(("%" + conditionPart.getParams().get(0).getParamValue()));
        }

    }

    private static class StartsConditionStringGenerator extends EndsConditionStringGenerator {

        @Override
        public void appendParentConditionPattern(StringBuilder stringBuilder, Tuple<CharSequence,Object> tuple, RestURISQLBuilder sqlBuilder, RestURIPart uriPart, RestURICondition.Part conditionPart, RestURICondition.Part.Param param)  throws Exception{
            Tuple<CharSequence,Object> parentTuple = generateQuerySQL(sqlBuilder, uriPart.getParent(), param);
            stringBuilder.append(concatSql(sqlBuilder, ("("+parentTuple.keyObject+")"), "'%'"));
            tuple.valueObject.addAll(parentTuple.valueObject);
        }
        
        @Override
        public void appendParamValue(Tuple<CharSequence,Object> tuple, RestURISQLBuilder sqlBuilder, RestURIPart uriPart, RestURICondition.Part conditionPart) {
            tuple.valueObject.add((conditionPart.getParams().get(0).getParamValue() + "%"));
        }
    }

    private static class ContainsConditionStringGenerator extends EndsConditionStringGenerator {

        @Override
        public void appendParentConditionPattern(StringBuilder stringBuilder, Tuple<CharSequence,Object> tuple, RestURISQLBuilder sqlBuilder, RestURIPart uriPart, RestURICondition.Part conditionPart, RestURICondition.Part.Param param)  throws Exception{
            Tuple<CharSequence,Object> parentTuple = generateQuerySQL(sqlBuilder, uriPart.getParent(), param);
            stringBuilder.append(concatSql(sqlBuilder, "'%'", ("("+parentTuple.keyObject+")"), "'%'"));
            tuple.valueObject.addAll(parentTuple.valueObject);
        }
        
        @Override
        public void appendParamValue(Tuple<CharSequence,Object> tuple, RestURISQLBuilder sqlBuilder, RestURIPart uriPart, RestURICondition.Part conditionPart) {
            tuple.valueObject.add(("%" + conditionPart.getParams().get(0).getParamValue() + "%"));
        }
    }

    private static class EqConditionStringGenerator extends CommonConditionStringGenerator {
    }

    private static class LtConditionStringGenerator extends CommonConditionStringGenerator {

        @Override
        public void appendOperator(StringBuilder stringBuilder, RestURISQLBuilder sqlBuilder, RestURIPart uriPart, RestURICondition.Part conditionPart) {
            if (conditionPart.isNot()) {
                stringBuilder.append('>').append('=');
            } else {
                stringBuilder.append('<');
            }
        }

    }

    private static class GtConditionStringGenerator extends CommonConditionStringGenerator {

        @Override
        public void appendOperator(StringBuilder stringBuilder, RestURISQLBuilder sqlBuilder, RestURIPart uriPart, RestURICondition.Part conditionPart) {
            if (conditionPart.isNot()) {
                stringBuilder.append('<').append('=');
            } else {
                stringBuilder.append('>');
            }
        }
    }

    private static class BetweenConditionStringGenerator extends CommonConditionStringGenerator {

        @Override
        public void appendOperator(StringBuilder stringBuilder, RestURISQLBuilder sqlBuilder, RestURIPart uriPart, RestURICondition.Part conditionPart) {
            if (conditionPart.isNot()) {
                stringBuilder.append("not ");
            }
            stringBuilder.append("between");
        }

        @Override
        public void appendConditionPattern(StringBuilder stringBuilder, Tuple<CharSequence,Object> tuple, RestURISQLBuilder sqlBuilder, RestURIPart uriPart, RestURICondition.Part conditionPart) {
            List<RestURICondition.Part.Param> params = conditionPart.getParams();
            for (int i = 0; i < 2; i++) {
                RestURICondition.Part.Param param = params.get(i);
                if (param.getParamType() == RestURICondition.Part.Param.TYPE_VAR) {
                    if (uriPart.getParent() == null) {
                        throw new IllegalArgumentException("require a parent URIPart for " + uriPart);
                    }
                    String paramName = sqlBuilder.evaluateVar(param.getParam());
                    if (isRequirePathAlias(uriPart.getParent())) {
                        stringBuilder.append(uriPart.getParent().getTargetAlias()).append('.');
                    }
                    stringBuilder.append(paramName);
                } else {
                    stringBuilder.append('?');
                    tuple.valueObject.add(param.getParamValue());
                }

                if (i == 0) {
                    stringBuilder.append(" and ");// between * and *
                }
            }
        }
    }

    private static class InConditionStringGenerator extends CommonConditionStringGenerator {

        @Override
        public void appendOperator(StringBuilder stringBuilder, RestURISQLBuilder sqlBuilder, RestURIPart uriPart, RestURICondition.Part conditionPart) {
            if (conditionPart.isNot()) {
                stringBuilder.append("not ");
            }
            stringBuilder.append('i').append('n');
        }

        @Override
        public void appendConditionPattern(StringBuilder stringBuilder, Tuple<CharSequence,Object> tuple, RestURISQLBuilder sqlBuilder, RestURIPart uriPart, RestURICondition.Part conditionPart)  throws Exception{
            List<RestURICondition.Part.Param> params = conditionPart.getParams();
            int type = params.get(0).getParamType();
            boolean isVar = false;
            if (type == RestURICondition.Part.Param.TYPE_VAR) {
                if (params.size() != 1) {
                    throw new IllegalArgumentException("illegal parameter for condition:" + conditionPart + "; var param must be 1 parameter");
                }
                isVar = true;
            }
            for (RestURICondition.Part.Param param : params) {
                if (param.getParamType() != type) {
                    throw new IllegalArgumentException("illegal parameters for condition:" + conditionPart + "; parameters must be the same type");
                }
            }

            stringBuilder.append('(');
            if (isVar) {
                if (uriPart.getParent() == null) {
                    throw new IllegalArgumentException("require a parent URIPart for " + uriPart);
                }
                RestURIPartImpl parent = (RestURIPartImpl) uriPart.getParent();
                parent.getSelectVars().add(params.get(0).getParam());
                Tuple<CharSequence,Object> t = sqlBuilder.build(parent);
                stringBuilder.append(t.keyObject);
                tuple.valueObject.addAll(t.valueObject);
            } else {
                for (RestURICondition.Part.Param param : params) {
                    stringBuilder.append('?').append(',');
                    tuple.valueObject.add(param.getParamValue());
                }
                stringBuilder.deleteCharAt(stringBuilder.length() - 1);//去掉最后的","
            }
            stringBuilder.append(')');
        }
    }

    private static class OrderConditionStringGenerator extends CommonConditionStringGenerator {

        @Override
        public Tuple<CharSequence,Object> conditionString(RestURISQLBuilder sqlBuilder, RestURIPart uriPart, RestURICondition.Part conditionPart) {
            if (!uriPart.getOperation().equalsIgnoreCase(RestURIPart.OPERATION_GET)) {
                throw new IllegalArgumentException("illegal condition:order ; order condition can only use for get operation");
            }
            StringBuilder builder = new StringBuilder();

            Tuple<CharSequence,Object> tuple = new Tuple<CharSequence,Object>();
            
            appendOperator(builder, sqlBuilder, uriPart, conditionPart);
            // 空格
            builder.append(' ');
            appendConditionPattern(builder, tuple, sqlBuilder, uriPart, conditionPart);

            tuple.keyObject = builder;

            return tuple;
        }
        
        @Override
        public void appendOperator(StringBuilder stringBuilder, RestURISQLBuilder sqlBuilder, RestURIPart uriPart, RestURICondition.Part conditionPart) {
            stringBuilder.append("order by");
        }

        @Override
        public void appendConditionPattern(StringBuilder stringBuilder, Tuple<CharSequence,Object> tuple, RestURISQLBuilder sqlBuilder, RestURIPart uriPart, RestURICondition.Part conditionPart) {
            List<RestURICondition.Part.Param> params = conditionPart.getParams();
            int type = params.get(0).getParamType();
            if (type != RestURICondition.Part.Param.TYPE_VAR) {
                throw new IllegalArgumentException("illegal parameter for condition:" + conditionPart + "; order param must be var parameter");
            }
            RestURICondition.Part.Param param = params.get(0);
            stringBuilder.append(uriPart.getTargetAlias()).append('.');
            stringBuilder.append(sqlBuilder.evaluateVar(conditionPart.getVar()));
            stringBuilder.append(' ');
            if (param.getParam().equalsIgnoreCase("desc") || param.getParam().equalsIgnoreCase("asc")) {
                stringBuilder.append(param.getParam());
            } else {
                throw new IllegalArgumentException("illegal parameter for condition:" + conditionPart + "; order param must be 'desc' or 'asc'");
            }
        }
    }
    
    private static class IdConditionStringGenerator extends CommonConditionStringGenerator {

        @Override
        public Tuple<CharSequence,Object> conditionString(RestURISQLBuilder sqlBuilder, RestURIPart uriPart, RestURICondition.Part conditionPart) {
            StringBuilder builder = new StringBuilder();            
            // 加上表的别名前缀
            if (isRequirePathAlias(uriPart)) {
                builder.append(uriPart.getTargetAlias()).append('.');
            }
            String id = sqlBuilder.evaluateVar(uriPart.getTarget());
            builder.append(id).append("_id");

            // 空格
            builder.append(' ');
            appendEqOperator(builder, sqlBuilder, uriPart, conditionPart);
            // 空格
            builder.append(' ');

            Tuple<CharSequence,Object> tuple = new Tuple<CharSequence,Object>();

            builder.append('?');
            appendParamValue(tuple, sqlBuilder, uriPart, conditionPart);

            tuple.keyObject = builder;

            return tuple;
        }
    }

    /**
     * "add","get","update","delete","list"
     */
    private static abstract class OperationSQLGenerator {

        public abstract Tuple<CharSequence,Object> generateSQL(RestURISQLBuilder builder, RestURIPart restURIPart);
    }

    private static class AddSQLGenerator extends OperationSQLGenerator {

        @Override
        public Tuple<CharSequence,Object> generateSQL(RestURISQLBuilder builder, RestURIPart restURIPart) {
            StringBuilder stringBuilder = new StringBuilder(30);
            stringBuilder.append("insert into ");
            stringBuilder.append(builder.evaluateTarget(restURIPart.getTarget()));
            Map<String, Object> colsAndObjs = builder.getKeysAndObjects(restURIPart);
            Tuple<CharSequence,Object> tuple = new Tuple<CharSequence,Object>();
            String[] cols = new String[colsAndObjs.size()];
            String[] vals = new String[colsAndObjs.size()];
            int i = 0;
            for (Map.Entry<String, Object> entry : colsAndObjs.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                cols[i] = builder.evaluateVar(key);
                vals[i] = "?";
                i++;
                tuple.valueObject.add(value);
            }
            stringBuilder.append('(');
            stringBuilder.append(builder.joinString(",", cols));
            stringBuilder.append(')');
            stringBuilder.append(" values");
            stringBuilder.append('(');
            stringBuilder.append(builder.joinString(",", vals));
            stringBuilder.append(')');

            tuple.keyObject = stringBuilder;
            return tuple;
        }

    }

    private static class GetSQLGenerator extends OperationSQLGenerator {
        @Override
        public Tuple<CharSequence,Object> generateSQL(RestURISQLBuilder builder, RestURIPart restURIPart) {
            Tuple<CharSequence,Object> tuple = new Tuple<CharSequence,Object>();
            tuple.keyObject = builder.createSelectSqlByURIPart(restURIPart);
            return tuple;
        }
    }

    private static class UpdateSQLGenerator extends OperationSQLGenerator {

        @Override
        public Tuple<CharSequence,Object> generateSQL(RestURISQLBuilder builder, RestURIPart restURIPart) {
            StringBuilder stringBuilder = new StringBuilder(30);
            stringBuilder.append("update ").append(builder.evaluateTarget(restURIPart.getTarget()));
            stringBuilder.append(' ').append("set ");

            Map<String, Object> colsAndObjs = builder.getKeysAndObjects(restURIPart);
            Tuple<CharSequence,Object> tuple = new Tuple<CharSequence,Object>();

            for (Map.Entry<String, Object> entry : colsAndObjs.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                stringBuilder.append(builder.evaluateVar(key)).append('=').append('?').append(',');
                tuple.valueObject.add(value);
            }
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);// 删除最后的","

            tuple.keyObject = stringBuilder;
            return tuple;
        }

    }

    private static class DeleteSQLGenerator extends OperationSQLGenerator {

        @Override
        public Tuple<CharSequence,Object> generateSQL(RestURISQLBuilder builder, RestURIPart restURIPart) {
            StringBuilder stringBuilder = new StringBuilder(10);
            stringBuilder.append("delete from ").append(builder.evaluateTarget(restURIPart.getTarget()));

            Tuple<CharSequence,Object> tuple = new Tuple<CharSequence,Object>();
            tuple.keyObject = stringBuilder;
            return tuple;
        }

    }
}
