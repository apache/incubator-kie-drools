package org.drools.verifier.components;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.drools.verifier.data.VerifierComponent;

/**
 * @author Toni Rikkola
 * 
 */
public class ObjectType extends VerifierComponent
    implements
    Serializable {
    private static final long   serialVersionUID = -783733402566313623L;

    private int                 offset           = 0;

    private String              fullName;

    private String              name;

    private Set<Field>          fields           = new HashSet<Field>();

    private Map<String, String> metadata         = new HashMap<String, String>();

    public int getOffset() {
        offset++;
        return offset % 2;
    }

    @Override
    public String getPath() {
        return String.format( "objectType[name=%s]",
                              getName() );
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

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String toString() {
        return "ObjectType: " + fullName;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }
}
