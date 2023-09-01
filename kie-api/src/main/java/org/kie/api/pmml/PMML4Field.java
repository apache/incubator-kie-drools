package org.kie.api.pmml;


public interface PMML4Field {

    public String getContext();

    public boolean isValid();

    public boolean isMissing();

    public String getName();
}
