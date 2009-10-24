package org.drools.verifier.components;

/**
 * 
 * @author Toni Rikkola
 */
public class VerifierFromDescr extends RuleComponent {

    private VerifierComponentType dataSourceType;
    private String                dataSourceGuid;

    public String getDataSourceGuid() {
        return dataSourceGuid;
    }

    public void setDataSourceGuid(String guid) {
        this.dataSourceGuid = guid;
    }

    public VerifierComponentType getDataSourceType() {
        return dataSourceType;
    }

    public void setDataSourceType(VerifierComponentType dataSourceType) {
        this.dataSourceType = dataSourceType;
    }

    public VerifierComponentType getVerifierComponentType() {
        return VerifierComponentType.FROM;
    }
}
