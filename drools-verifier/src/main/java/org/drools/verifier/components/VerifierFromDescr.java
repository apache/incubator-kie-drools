package org.drools.verifier.components;

/**
 * 
 * @author Toni Rikkola
 */
public class VerifierFromDescr extends PatternComponentSource {

    private VerifierComponentType dataSourceType;
    private String                dataSourcePath;

    public VerifierFromDescr(Pattern pattern) {
        super( pattern );
    }

    public String getDataSourcePath() {
        return dataSourcePath;
    }

    public void setDataSourcePath(String path) {
        this.dataSourcePath = path;
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
