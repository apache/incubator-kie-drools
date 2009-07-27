package org.drools.result;

public abstract class AbstractResult {

    private String identifier;

    public AbstractResult( String identifier ){
        this.identifier = identifier;
    }

    public String getIdentifier(){
        return identifier;
    }

    public Object getFactHandle(){
	return null;
    }

}