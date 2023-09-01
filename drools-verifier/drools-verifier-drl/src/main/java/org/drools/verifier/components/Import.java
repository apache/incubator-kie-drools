package org.drools.verifier.components;

import org.drools.drl.ast.descr.ImportDescr;

public class Import extends PackageComponent<ImportDescr> {

    private String name;
    private String shortName;

    public Import(ImportDescr descr, RulePackage rulePackage) {
        super(descr, rulePackage );
    }

    @Override
    public String getPath() {
        return String.format( "%s/import[@name='%s']",
                              getPackagePath(),
                              getName() );
    }

    @Override
    public VerifierComponentType getVerifierComponentType() {
        return VerifierComponentType.IMPORT;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }
}
