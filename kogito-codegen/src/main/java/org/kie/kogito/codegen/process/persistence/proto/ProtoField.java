package org.kie.kogito.codegen.process.persistence.proto;

public class ProtoField {

    private String applicability;
    private String type;
    private String name;
    private int index;

    public ProtoField(String applicability, String type, String name, int index) {
        super();
        this.applicability = applicability;
        this.type = type;
        this.name = name;
        this.index = index;
    }

    public String getApplicability() {
        return applicability;
    }

    public void setApplicability(String applicability) {
        this.applicability = applicability;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "\t" + applicability + " " + type + " " + name + " = " + index + "; \n";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ProtoField other = (ProtoField) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }

}
