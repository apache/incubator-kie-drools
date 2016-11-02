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

import org.drools.core.command.impl.ExecutableCommand;
import org.drools.core.command.impl.RegistryContext;
import org.drools.core.util.StringUtils;
import org.drools.core.xml.jaxb.util.JaxbUnknownAdapter;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.EntryPoint;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.command.Context;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlAccessorType(XmlAccessType.NONE)
public class DeleteObjectCommand
        implements ExecutableCommand<Void> {

    @XmlAttribute(name="object")
    @XmlJavaTypeAdapter(JaxbUnknownAdapter.class)
    private Object object;

    @XmlAttribute(name="entry-point")
    private String entryPoint = "DEFAULT";

    public DeleteObjectCommand() {
    }

    public DeleteObjectCommand( Object object, String entryPoint ) {
        this.object = object;
        if ( ! StringUtils.isEmpty( this.entryPoint ) ) {
            this.entryPoint = entryPoint;
        }
    }

    public Void execute(Context context) {
        KieSession ksession = ((RegistryContext) context).lookup( KieSession.class );
        EntryPoint ep = ksession.getEntryPoint( entryPoint );
        if ( ep != null ) {
            FactHandle handle = ksession.getEntryPoint( entryPoint ).getFactHandle( object );
            ksession.delete( handle );
        }
        return null;
    }

    public Object getObject() {
        return this.object;
    }

    public String getEntryPointId() {
        return entryPoint;
    }

    public String toString() {
        return "session.entryPoints(" + ((this.entryPoint == null ) ? "DEFAULT" : this.entryPoint) + ").delete( " + object + " );";
    }

}
