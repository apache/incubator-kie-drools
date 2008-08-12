package org.drools.process.core;

public interface ValueObject extends TypeObject {
	
	Object getValue();

    void setValue(Object value);
}
