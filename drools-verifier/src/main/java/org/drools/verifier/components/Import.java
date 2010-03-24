package org.drools.verifier.components;

/**
 * 
 * @author Toni Rikkola
 */
public class Import extends PackageComponent {

    private String name;
    private String shortName;

    public Import(RulePackage rulePackage) {
        super( rulePackage );
    }

    @Override
    public String getPath() {
        return String.format( "%s.import[name=%s]",
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
