package org.drools.rule;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.List;

public class ConsequenceMetaData implements Externalizable {

    private List<Statement> statements;

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject( statements );
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        statements = (List<Statement>) in.readObject();
    }

    public List<Statement> getStatements() {
        return statements;
    }

    public void addStatement(Statement statement) {
        if (statements == null) {
            statements = new ArrayList<Statement>();
        }
        statements.add(statement);
    }

    @Override
    public String toString() {
        return statements.toString();
    }

    public static class Statement implements Externalizable {

        public enum Type { INSERT, RETRACT, MODIFY }

        private Type type;
        private Class<?> factClass;
        private List<Field> fields;

        public Statement() { }

        public Statement(Type type, Class<?> factClass) {
            this.type = type;
            this.factClass = factClass;
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeObject( type );
            out.writeObject( factClass );
            out.writeObject( fields );
        }

        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
            type = (Type) in.readObject();
            factClass = (Class<?>) in.readObject();
            fields = (List<Field>) in.readObject();
        }

        public Type getType() {
            return type;
        }

        public Class<?> getFactClass() {
            return factClass;
        }

        public List<Field> getFields() {
            return fields;
        }

        public void addField(Field field) {
            if (fields == null) {
                fields = new ArrayList<Field>();
            }
            fields.add(field);
        }

        public void addField(String name, String value) {
            addField(new Field(name, value));
        }

        @Override
        public String toString() {
            return "Modifications to " + factClass + ": " + fields;
        }
    }

    public static class Field implements Externalizable {
        private String name;
        private String value;

        public Field() { }

        public Field(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeObject( name );
            out.writeObject( value );
        }

        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
            name = (String) in.readObject();
            value = (String) in.readObject();
        }

        public String getName() {
            return name;
        }

        public String getValue() {
            return value;
        }

        public boolean isLiteral() {
            return value != null && value.length() > 0 && ( Character.isDigit(value.charAt(0)) || value.charAt(0) == '"' );
        }

        @Override
        public String toString() {
            return name + " = " + value;
        }
    }
}
