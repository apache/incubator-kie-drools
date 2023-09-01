package org.drools.base.rule;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.kie.internal.definition.KnowledgeDefinition;
import org.kie.api.io.Resource;

public class Function implements  KnowledgeDefinition, Dialectable, Externalizable {
    private String name;
    private String namespace;
    private String dialect;
    private Resource resource;

    public Function() {

    }

    public Function(String namespace, String name,
                    String dialect) {
        this.namespace = namespace;
        this.name = name;
        this.dialect = dialect;
    }

    public String getName() {
        return this.name;
    }

    public String getDialect() {
        return this.dialect;
    }
    
    public Resource getResource() {
        return this.resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getClassName() {
        return namespace + "." + (name.length() > 1 ? name.substring(0, 1).toUpperCase() + name.substring(1) : name.toUpperCase());
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        name = (String)in.readObject();
        namespace = (String)in.readObject();
        dialect = (String)in.readObject();
        resource = ( Resource ) in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(name);
        out.writeObject(namespace);
        out.writeObject(dialect);
        out.writeObject(resource);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "[Function " + name + "]";
    }

    public KnowledgeType getKnowledgeType() {
        return KnowledgeType.FUNCTION;
    }

    public String getId() {
        return getName();
    }
    
}
