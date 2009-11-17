package org.drools.verifier.components;

public abstract class RuleComponent extends PackageComponent
    implements
    ChildComponent {

    private String                ruleName;
    private String                ruleGuid;

    private VerifierComponentType parentType;
    private String                parentGuid;
    private int                   orderNumber;

    /**
     * 
     * @return Rule package name + rule name.
     */
    public String getFullRulePath() {
        return getPackageName() + "." + getRuleName();
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public String getRuleGuid() {
        return ruleGuid;
    }

    public void setRuleGuid(String ruleGuid) {
        this.ruleGuid = ruleGuid;
    }

    public VerifierComponentType getParentType() {
        return parentType;
    }

    public String getParentGuid() {
        return parentGuid;
    }

    public int getOrderNumber() {
        return orderNumber;
    }

    public void setParentType(VerifierComponentType parentType) {
        this.parentType = parentType;
    }

    public void setParentGuid(String parentGuid) {
        this.parentGuid = parentGuid;
    }

    public void setOrderNumber(int orderNumber) {
        this.orderNumber = orderNumber;
    }

}
