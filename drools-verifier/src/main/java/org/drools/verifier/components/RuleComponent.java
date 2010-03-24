package org.drools.verifier.components;

/**
 * 
 * @author Toni Rikkola
 */
public abstract class RuleComponent extends PackageComponent
    implements
    ChildComponent {

    private String                ruleName;

    private VerifierComponentType parentType;
    private String                parentPath;
    private int                   orderNumber;

    public RuleComponent(VerifierRule rule) {
        this( rule.getPackageName(),
              rule.getName() );
    }

    RuleComponent(String packageName,
                  String ruleName) {
        super( packageName );

        setRuleName( ruleName );
    }

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

    protected void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public String getRulePath() {
        return String.format( "%s.rule[name=%s]",
                              getPackagePath(),
                              getRuleName() );
    }

    @Override
    public String getPath() {
        return String.format( "%s.ruleComponent[%s]",
                              getRulePath(),
                              getOrderNumber() );
    }

    public VerifierComponentType getParentType() {
        return parentType;
    }

    public String getParentPath() {
        return parentPath;
    }

    public int getOrderNumber() {
        return orderNumber;
    }

    public void setParentType(VerifierComponentType parentType) {
        this.parentType = parentType;
    }

    public void setParentPath(String parentPath) {
        this.parentPath = parentPath;
    }

    public void setOrderNumber(int orderNumber) {
        this.orderNumber = orderNumber;
    }

}
