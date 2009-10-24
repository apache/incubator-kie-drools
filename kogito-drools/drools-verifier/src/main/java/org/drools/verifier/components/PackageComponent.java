package org.drools.verifier.components;

import org.drools.verifier.data.VerifierComponent;

public abstract class PackageComponent extends VerifierComponent {

    private String packageName;
    private String packageGuid;

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getPackageGuid() {
        return packageGuid;
    }

    public void setPackageGuid(String packageGuid) {
        this.packageGuid = packageGuid;
    }
}
