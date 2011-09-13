package org.drools.rule.builder.dialect.asm;

public interface InvokerDataProvider {
    String getPackageName();
    String getRuleClassName();
    String getInternalRuleClassName();
    String getMethodName();
    String getInvokerClassName();
    String[] getDeclarationTypes();
    String[] getGlobals();
    String[] getGlobalTypes();
}
