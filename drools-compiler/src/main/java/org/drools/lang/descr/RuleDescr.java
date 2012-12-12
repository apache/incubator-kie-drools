/*
 * Copyright 2005 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.lang.descr;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.*;

import org.drools.rule.Dialectable;
import org.drools.rule.Namespaceable;
import org.kie.io.Resource;

public class RuleDescr extends AnnotatedBaseDescr
    implements
    Dialectable {

    private static final long           serialVersionUID = 510l;
    private String                      name;
    private String                      parentName;
    private String                      documentation;
    private Map<String, AttributeDescr> attributes;

    private AndDescr                    lhs;
    private Object                      consequence;
    private Map<String, Object>         namedConsequence;
    private int                         consequenceLine;
    private int                         consequencePattern;
    private int                         offset;

    private String                      className;

    private List<String>                errors;

    public RuleDescr() {
        this( null,
              "" );
    }

    public RuleDescr(final String name) {
        this( name,
              "" );
    }

    public RuleDescr(final String ruleName,
                     final String documentation) {
        this.name = ruleName;
        this.documentation = documentation;
        this.attributes = new LinkedHashMap<String, AttributeDescr>();
        this.namedConsequence = new HashMap<String, Object>();
        this.lhs = new AndDescr();
        this.consequence = "";
    }

    @SuppressWarnings("unchecked")
    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        super.readExternal( in );
        name = (String) in.readObject();
        parentName = (String) in.readObject();
        documentation = (String) in.readObject();
        consequence = in.readObject();
        namedConsequence = (Map<String, Object>) in.readObject();
        lhs = (AndDescr) in.readObject();
        consequenceLine = in.readInt();
        consequencePattern = in.readInt();
        offset = in.readInt();
        attributes = (Map<String, AttributeDescr>) in.readObject();
        className = (String) in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal( out );
        out.writeObject( name );
        out.writeObject( parentName );
        out.writeObject( documentation );
        out.writeObject( consequence );
        out.writeObject( namedConsequence );
        out.writeObject( lhs );
        out.writeInt( consequenceLine );
        out.writeInt( consequencePattern );
        out.writeInt( offset );
        out.writeObject( attributes );
        out.writeObject( className );
    }

    public String getName() {
        return this.name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public String getDialect() {
        AttributeDescr dialect = this.attributes.get( "dialect" );
        return dialect != null ? dialect.getValue() : null;
    }

    public String getSalience() {
        AttributeDescr salience = this.attributes.get( "salience" );
        return salience != null ? salience.getValue() : null;
    }

    public String getClassName() {
        return this.className;
    }

    public void setClassName(final String className) {
        this.className = className;
    }

    public String getDocumentation() {
        return this.documentation;
    }

    public Map<String, AttributeDescr> getAttributes() {
        return this.attributes;
    }

    public void addAttribute(final AttributeDescr attribute) {
        if ( attribute != null ) {
            this.attributes.put( attribute.getName(), attribute );
        }
    }

    public AndDescr getLhs() {
        return this.lhs;
    }

    public void setLhs(final AndDescr lhs) {
        this.lhs = lhs;
    }

    public Object getConsequence() {
        return this.consequence;
    }

    public void setConsequence(final Object consequence) {
        this.consequence = consequence;
    }
    
    public Map<String, Object> getNamedConsequences() {
        return this.namedConsequence;
    }

    public void addNamedConsequences(String name, Object consequence) {
        if ( namedConsequence.containsKey(name) ) {
            if (errors == null) {
                errors = new ArrayList<String>();
            }
            errors.add("Duplicate consequence name: " + name);
        } else {
            namedConsequence.put(name, consequence);
        }
    }

    public void setConsequenceLocation(final int line,
                                       final int pattern) {
        this.consequenceLine = line;
        this.consequencePattern = pattern;
    }

    public void setConsequenceOffset(final int offset) {
        this.offset = offset;
    }

    public int getConsequenceOffset() {
        return this.offset;
    }

    public int getConsequenceLine() {
        return this.consequenceLine;
    }

    public int getConsequencePattern() {
        return this.consequencePattern;
    }

    public String getEnabled() {
        AttributeDescr enabled = this.attributes.get( "enabled" );
        return enabled != null ? enabled.getValue() : null;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public String getParentName() {
        return parentName;
    }

    public boolean isRule() {
        return true;
    }
    
    public boolean isQuery() {
        return false;
    }

    public List<String> getErrors() {
        return errors;
    }

    public boolean hasErrors() {
        return errors != null;
    }

    public String toString() {
        return "[Rule name='" + this.name + "']";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((attributes == null) ? 0 : attributes.hashCode());
        result = prime * result + ((className == null) ? 0 : className.hashCode());
        result = prime * result + ((consequence == null) ? 0 : consequence.hashCode());
        result = prime * result + consequenceLine;
        result = prime * result + consequencePattern;
        result = prime * result + ((documentation == null) ? 0 : documentation.hashCode());
        result = prime * result + ((errors == null) ? 0 : errors.hashCode());
        result = prime * result + ((lhs == null) ? 0 : lhs.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((namedConsequence == null) ? 0 : namedConsequence.hashCode());
        result = prime * result + offset;
        result = prime * result + ((parentName == null) ? 0 : parentName.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        RuleDescr other = (RuleDescr) obj;
        if ( attributes == null ) {
            if ( other.attributes != null ) return false;
        } else if ( !attributes.equals( other.attributes ) ) return false;
        if ( className == null ) {
            if ( other.className != null ) return false;
        } else if ( !className.equals( other.className ) ) return false;
        if ( consequence == null ) {
            if ( other.consequence != null ) return false;
        } else if ( !consequence.equals( other.consequence ) ) return false;
        if ( consequenceLine != other.consequenceLine ) return false;
        if ( consequencePattern != other.consequencePattern ) return false;
        if ( documentation == null ) {
            if ( other.documentation != null ) return false;
        } else if ( !documentation.equals( other.documentation ) ) return false;
        if ( errors == null ) {
            if ( other.errors != null ) return false;
        } else if ( !errors.equals( other.errors ) ) return false;
        if ( lhs == null ) {
            if ( other.lhs != null ) return false;
        } else if ( !lhs.equals( other.lhs ) ) return false;
        if ( name == null ) {
            if ( other.name != null ) return false;
        } else if ( !name.equals( other.name ) ) return false;
        if ( namedConsequence == null ) {
            if ( other.namedConsequence != null ) return false;
        } else if ( !namedConsequence.equals( other.namedConsequence ) ) return false;
        if ( offset != other.offset ) return false;
        if ( parentName == null ) {
            if ( other.parentName != null ) return false;
        } else if ( !parentName.equals( other.parentName ) ) return false;
        return true;
    }
    
    
}
