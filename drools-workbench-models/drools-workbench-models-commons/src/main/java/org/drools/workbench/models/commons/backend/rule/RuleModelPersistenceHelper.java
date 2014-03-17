package org.drools.workbench.models.commons.backend.rule;

import java.util.List;
import java.util.Map;

import org.drools.workbench.models.datamodel.oracle.DataType;
import org.drools.workbench.models.datamodel.oracle.MethodInfo;
import org.drools.workbench.models.datamodel.oracle.PackageDataModelOracle;
import org.drools.workbench.models.datamodel.rule.FieldNatureType;
import org.drools.workbench.models.datamodel.rule.RuleModel;

class RuleModelPersistenceHelper {

    static String unwrapParenthesis(String s) {
        int start = s.indexOf('(');
        int end = s.lastIndexOf(')');
        return s.substring(start + 1, end).trim();
    }

    static int inferFieldNature(String param,
            Map<String, String> boundParams) {
        if (param.startsWith("sdf.parse")) {
            return FieldNatureType.TYPE_UNDEFINED;
        }
        if (boundParams.keySet().contains(param)) {
            return FieldNatureType.TYPE_VARIABLE;
        }
        if (param.contains("+") || param.contains("-") || param.contains("*") || param.contains("/")) {
            return FieldNatureType.TYPE_FORMULA;
        }
        if (param.startsWith("\"") || Character.isDigit(param.charAt(0))) {
            return FieldNatureType.TYPE_LITERAL;
        }
        return FieldNatureType.TYPE_UNDEFINED;
    }

    static String inferDataType(
            String param,
            boolean isJavaDialect) {
        if (param.startsWith("sdf.parse(\"")) {
            return DataType.TYPE_DATE;
        } else if (param.startsWith("\"")) {
            return DataType.TYPE_STRING;
        } else if (param.equals("true") || param.equals("false")) {
            return DataType.TYPE_BOOLEAN;
        } else if (param.endsWith("B") || (isJavaDialect && param.startsWith("new java.math.BigDecimal"))) {
            return DataType.TYPE_NUMERIC_BIGDECIMAL;
        } else if (param.endsWith("I") || (isJavaDialect && param.startsWith("new java.math.BigInteger"))) {
            return DataType.TYPE_NUMERIC_BIGINTEGER;
        } else if (param.startsWith("[") && param.endsWith("]")) {
            return DataType.TYPE_COLLECTION;
        }
        return DataType.TYPE_NUMERIC;
    }

    static String adjustParam(
            String dataType,
            String param,
            boolean isJavaDialect) {
        if (dataType == DataType.TYPE_DATE) {
            return param.substring("sdf.parse(\"".length(), param.length() - 2);
        } else if (dataType == DataType.TYPE_STRING) {
            return param.substring(1, param.length() - 1);
        } else if (dataType == DataType.TYPE_NUMERIC_BIGDECIMAL || dataType == DataType.TYPE_NUMERIC_BIGINTEGER) {
            if (isJavaDialect) {
                return param.substring("new java.math.BigDecimal(\"".length(), param.length() - 2);
            } else {
                return param.substring(0, param.length() - 1);
            }
        }
        return param;
    }

    static List<MethodInfo> getMethodInfosForType(RuleModel
            model,
            PackageDataModelOracle dmo,
            String variableType) {
        List<MethodInfo> methods = dmo.getProjectMethodInformation().get(variableType);
        if (methods == null) {
            for (String imp : model.getImports().getImportStrings()) {
                if (imp.endsWith("." + variableType)) {
                    methods = dmo.getProjectMethodInformation().get(imp);
                    if (methods != null) {
                        break;
                    }
                }
            }
        }
        return methods;
    }
}
