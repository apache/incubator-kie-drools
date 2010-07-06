package org.drools.verifier.components;

import org.drools.verifier.data.VerifierComponent;

/**
 * 
 * @author Toni Rikkola
 */
public abstract class PackageComponent extends VerifierComponent {

    private String packageName;

    public PackageComponent(RulePackage rulePackage) {
        setPackageName( rulePackage.getName() );
    }

    protected PackageComponent(String packageName) {
        setPackageName( packageName );
    }

    public String getPackageName() {
        return packageName;
    }

    protected void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getPackagePath() {
        return String.format( "package[@name='%s']",
                              getPackageName() );
    }

}
