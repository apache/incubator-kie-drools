package org.drools.rule.builder.dialect.asm;

import java.util.*;

public class ConsequenceContext implements ConsequenceDataProvider {

    private Map<String, Object> consequenceContext;

    public ConsequenceContext(Map<String, Object> consequenceContext) {
        this.consequenceContext = consequenceContext;
    }

    @Override
    public int hashCode() {
        return (Integer)consequenceContext.get("hashCode");
    }

    public String getPackageName() {
        return (String)consequenceContext.get("package");
    }

    public String getRuleClassName() {
        return (String)consequenceContext.get("ruleClassName");
    }

    public String getInternalRuleClassName() {
        return (getPackageName() + "." + getRuleClassName()).replace(".", "/");
    }

    public String getMethodName() {
        return (String)consequenceContext.get("methodName");
    }

    public String[] getDeclarationTypes() {
        return (String[])consequenceContext.get("declarationTypes");
    }

    public String[] getGlobals() {
        return (String[])consequenceContext.get("globals");
    }

    public String[] getGlobalTypes() {
        return (String[])consequenceContext.get("globalTypes");
    }

    public Boolean[] getNotPatterns() {
        return (Boolean[])consequenceContext.get("notPatterns");
    }
}
