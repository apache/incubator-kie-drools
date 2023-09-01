package org.drools.verifier.components;

import org.drools.drl.ast.descr.BaseDescr;
import org.drools.verifier.data.VerifierComponent;

public abstract class PackageComponent<D extends BaseDescr> extends VerifierComponent<D> {

    private String packageName;
    
    public PackageComponent(D descr, RulePackage rulePackage) {
        super(descr);
        setPackageName( rulePackage.getName() );
    }

    protected PackageComponent(D descr, String packageName) {
        super(descr);
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
