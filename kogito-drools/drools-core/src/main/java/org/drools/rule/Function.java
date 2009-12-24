package org.drools.rule;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.drools.definition.KnowledgeDefinition;
import org.drools.io.Resource;

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

    

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        name    = (String)in.readObject();
        dialect = (String)in.readObject();
        resource = ( Resource ) in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(name);
        out.writeObject(dialect);
        out.writeObject(resource);
    }
}
