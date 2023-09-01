package org.drools.base.rule;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.kie.internal.definition.KnowledgeDefinition;

/**
 * The window declaration defines a window that can be used by patterns
 * in rules
 */
public class WindowDeclaration
    implements
    KnowledgeDefinition,
    Externalizable {
    
    private String               name;
    private String               namespace;
    private Pattern              pattern;

    public WindowDeclaration() {
    }

    public WindowDeclaration(String name, String namespace) {
        this.name = name;
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        this.name = (String) in.readObject();
        this.namespace = (String) in.readObject();
        this.pattern = (Pattern) in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject( name );
        out.writeObject( namespace );
        out.writeObject( pattern );
    }
    
    public String getName() {
        return name;
    }
    
    public void setName( String name ) {
        this.name = name;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public void setPattern( Pattern pattern ) {
        this.pattern = pattern;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ( ( name == null ) ? 0 : name.hashCode() );
        result = prime * result + ( ( pattern == null ) ? 0 : pattern.hashCode() );
        return result;
    }

    @Override
    public boolean equals( Object obj ) {
        if (this == obj)
            return true;
        if (!super.equals( obj ))
            return false;
        if (getClass() != obj.getClass())
            return false;
        WindowDeclaration other = (WindowDeclaration) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals( other.name ))
            return false;
        if (pattern == null) {
            if (other.pattern != null)
                return false;
        } else if (!pattern.equals( other.pattern ))
            return false;
        return true;
    }

    public KnowledgeType getKnowledgeType() {
        return KnowledgeType.WINDOW;
    }

    public String getNamespace() {
        return namespace;
    }

    public String getId() {
        return getName();
    }
}
