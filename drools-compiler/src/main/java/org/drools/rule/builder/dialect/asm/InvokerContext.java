package org.drools.rule.builder.dialect.asm;

import java.util.*;

public class InvokerContext implements InvokerDataProvider {

    private Map<String, Object> invokerContext;

    public InvokerContext(Map<String, Object> invokerContext) {
        this.invokerContext = invokerContext;
    }

    @Override
    public int hashCode() {
        return (Integer) invokerContext.get("hashCode");
    }

    public String getPackageName() {
        return (String) invokerContext.get("package");
    }

    public String getRuleClassName() {
        return (String) invokerContext.get("ruleClassName");
    }

    public String getInternalRuleClassName() {
        return (getPackageName() + "." + getRuleClassName()).replace(".", "/");
    }

    public String getInvokerClassName() {
        return (String) invokerContext.get("invokerClassName");
    }

    public String getMethodName() {
        return (String) invokerContext.get("methodName");
    }

    public String[] getGlobals() {
        return (String[]) invokerContext.get("globals");
    }

    public String[] getGlobalTypes() {
        return (String[]) invokerContext.get("globalTypes");
    }

    public Boolean[] getNotPatterns() {
        return (Boolean[]) invokerContext.get("notPatterns");
    }
}
