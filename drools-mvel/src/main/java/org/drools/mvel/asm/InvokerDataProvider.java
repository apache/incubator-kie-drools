package org.drools.mvel.asm;

public interface InvokerDataProvider {
    String getPackageName();
    String getRuleClassName();
    String getInternalRuleClassName();
    String getMethodName();
    String getInvokerClassName();
    String[] getGlobals();
    String[] getGlobalTypes();
}
