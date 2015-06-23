/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.core.rule;

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
        return statements != null ? statements.toString() : "";
    }

    public static class Statement implements Externalizable {

        public enum Type { INSERT, RETRACT, MODIFY }

        private Type type;
        private String factClassName;
        private List<Field> fields;

        public Statement() { }

        public Statement(Type type, Class<?> factClass) {
            this.type = type;
            this.factClassName = factClass.getName();
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeObject( type );
            out.writeObject( factClassName );
            out.writeObject( fields );
        }

        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
            type = (Type) in.readObject();
            factClassName = (String) in.readObject();
            fields = (List<Field>) in.readObject();
        }

        public Type getType() {
            return type;
        }

        public String getFactClassName() {
            return factClassName;
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
            return "Modifications to " + factClassName + ": " + fields;
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
            return value != null && value.length() > 0 &&
                    ( Character.isDigit(value.charAt(0)) || value.charAt(0) == '"' || "true".equals(value) || "false".equals(value) );
        }

        @Override
        public String toString() {
            return name + " = " + value;
        }
    }
}
