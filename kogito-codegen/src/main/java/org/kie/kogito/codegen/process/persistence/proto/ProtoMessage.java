package org.kie.kogito.codegen.process.persistence.proto;

import java.util.ArrayList;
import java.util.List;

public class ProtoMessage {

    private String name;
    private String javaPackageOption;
    private List<ProtoField> fields = new ArrayList<ProtoField>();
    private String comment;

    public ProtoMessage(String name, String javaPackageOption) {
        super();
        this.name = name;
        this.javaPackageOption = javaPackageOption;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ProtoField> getFields() {
        return fields;
    }

    public void setFields(List<ProtoField> fields) {
        this.fields = fields;
    }

    public String getJavaPackageOption() {
        return javaPackageOption;
    }

    public void setJavaPackageOption(String javaPackageOption) {
        this.javaPackageOption = javaPackageOption;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public ProtoField addField(String applicability, String type, String name) {

        int index = fields.size() + 1;
        ProtoField field = new ProtoField(applicability, type, name, index);
        if (!fields.contains(field)) {
            fields.add(field);
        }
        
        return field;
    }

    @Override
    public String toString() {
        StringBuilder tostring = new StringBuilder();
        if (comment != null) {
            tostring.append("/* " + comment + " */ \n");
        }
        tostring.append("message " + name + " { \n");
        if (javaPackageOption != null) {
            tostring.append("\toption java_package = \"" + javaPackageOption + "\";\n");
        }
        fields.forEach(f -> tostring.append(f.toString()));
        tostring.append("}\n");

        return tostring.toString();
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
        ProtoMessage other = (ProtoMessage) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }
}
