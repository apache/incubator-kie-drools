/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.command.runtime.rule;

import org.drools.core.ClassObjectSerializationFilter;
import org.drools.core.command.IdentifiableResult;
import org.drools.core.command.impl.ExecutableCommand;
import org.drools.core.command.impl.RegistryContext;
import org.drools.core.runtime.impl.ExecutionResultImpl;
import org.kie.api.runtime.ClassObjectFilter;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.ObjectFilter;
import org.kie.internal.command.Context;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@XmlAccessorType(XmlAccessType.NONE)
public class GetObjectsCommand
    implements
    ExecutableCommand<Collection>, IdentifiableResult {

    @XmlElement(name="class-object-filter", required=false)
    private ClassObjectSerializationFilter classObjectFilter = null;

    private transient ObjectFilter filter = null;

    @XmlAttribute(name="out-identifier")
    private String outIdentifier;

    public String getOutIdentifier() {
        return outIdentifier;
    }

    public void setOutIdentifier(String outIdentifier) {
        this.outIdentifier = outIdentifier;
    }

    public GetObjectsCommand() {
    }

    public GetObjectsCommand(ObjectFilter filter) {
        setFilter(filter);
    }

    public GetObjectsCommand(ObjectFilter filter, String outIdentifier) {
        setFilter(filter);
        this.outIdentifier = outIdentifier;
    }

    public void setFilter(ObjectFilter filter) {
        this.filter = filter;
        if( filter instanceof ClassObjectFilter ) {
            this.classObjectFilter = new ClassObjectSerializationFilter((ClassObjectFilter)filter);
        }
    }

    public ObjectFilter getFilter() {
        if( this.filter == null ) {
           this.filter = this.classObjectFilter;
        }
        return this.filter;
    }

    public Collection execute(Context context) {
        KieSession ksession = ((RegistryContext) context).lookup( KieSession.class );

        Collection col = null;

        if ( getFilter() != null ) {
            col =  ksession.getObjects( this.filter );
        } else {
            col =  ksession.getObjects( );
        }

        if ( this.outIdentifier != null ) {
            List objects = new ArrayList( col );

            ((RegistryContext) context).lookup( ExecutionResultImpl.class ).setResult( this.outIdentifier, objects );
        }

        return col;
    }

    public String toString() {
        if ( getFilter() != null ) {
            return "session.iterateObjects( " + filter + " );";
        } else {
            return "session.iterateObjects();";
        }
    }

}
