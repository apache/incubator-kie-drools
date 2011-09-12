package org.drools.rule.builder.dialect.asm;

public interface ConsequenceDataProvider {
    String getPackageName();
    String getRuleClassName();
    String getInternalRuleClassName();
    String getMethodName();
    String[] getDeclarationTypes();
    String[] getGlobals();
    String[] getGlobalTypes();
    Boolean[] getNotPatterns();
}
