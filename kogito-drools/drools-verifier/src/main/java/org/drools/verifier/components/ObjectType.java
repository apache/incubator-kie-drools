package org.drools.verifier.components;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.drools.verifier.data.VerifierComponent;

/**
 * @author Toni Rikkola
 * 
 */
public class ObjectType extends VerifierComponent
    implements
    Serializable {
    private static final long serialVersionUID = -783733402566313623L;

    private int               offset = 0;
    
    private String            name;

    private Set<Field>        fields           = new HashSet<Field>();
    
    public int getOffset() {
        offset++;
        return offset % 2;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Field> getFields() {
        return fields;
    }

    public void setFields(Set<Field> fields) {
        this.fields = fields;
    }


    public VerifierComponentType getVerifierComponentType() {
        return VerifierComponentType.OBJECT_TYPE;
    }
}
