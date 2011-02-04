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

import org.drools.io.Resource;
import org.drools.rule.Dialectable;
import org.drools.rule.Namespaceable;

public class RuleDescr extends AnnotatedBaseDescr
    implements
    Dialectable,
    Namespaceable {

    private static final long           serialVersionUID = 510l;
    private String                      namespace;
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

    private Resource                    resource;

    public RuleDescr() {
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
    }

    @SuppressWarnings("unchecked")
    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        super.readExternal( in );
        namespace = (String) in.readObject();
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
        resource = (Resource) in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal( out );
        out.writeObject( namespace );
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
        out.writeObject( resource );
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getNamespace() {
        return this.namespace;
    }

    public Resource getResource() {
        return resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
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

    public String toString() {
        return "[Rule name='" + this.name + "']";
    }
}
