package org.kie.dmn.typesafe;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.drools.util.StringUtils;
import org.kie.dmn.feel.codegen.feel11.CodegenStringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum FieldGenStrategy {

    JAVA_BEAN {

        @Override
        public String generateFieldName(String dmnTypeFieldName) {
            return CodegenStringUtil.escapeIdentifier(StringUtils.lcFirst(dmnTypeFieldName));
        }

        @Override
        public String generateGetterName(String fieldName) {
            return "get" + StringUtils.ucFirst(fieldName);
        }

        @Override
        public String generateSetterName(String fieldName) {
            return "set" + StringUtils.ucFirst(fieldName);
        }
    },

    NO_CAPITALIZATION {

        @Override
        public String generateFieldName(String dmnTypeFieldName) {
            return CodegenStringUtil.escapeIdentifier(dmnTypeFieldName);
        }

        @Override
        public String generateGetterName(String fieldName) {
            return "get" + fieldName;
        }

        @Override
        public String generateSetterName(String fieldName) {
            return "set" + fieldName;
        }
    };

    private static final Logger logger = LoggerFactory.getLogger(FieldGenStrategy.class);

    abstract String generateFieldName(String dmnTypeFieldName);

    abstract String generateGetterName(String fieldName);

    abstract String generateSetterName(String fieldName);

    public static FieldGenStrategy getFieldGenStrategy(Set<String> fieldsKeySet, String typeName) {
        Set<String> lowerCaseSet = fieldsKeySet.stream().map(StringUtils::lcFirst).collect(Collectors.toSet());
        if (fieldsKeySet.size() == lowerCaseSet.size()) {
            return JAVA_BEAN;
        } else {
            Set<String> copiedSet = new HashSet<>(fieldsKeySet);
            copiedSet.removeAll(lowerCaseSet);
            logger.warn("Capitalization name conflict found {} in {}."
                    + " Generated class will use DMN names as-is for fields, getters and setters without capitalization",
                    copiedSet, typeName);
            return NO_CAPITALIZATION;
        }
    }
}