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
import org.drools.core.common.DisconnectedFactHandle;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.EntryPoint;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.command.Context;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import java.util.Arrays;

@XmlRootElement(name="update-command")
@XmlAccessorType(XmlAccessType.NONE)
public class UpdateCommand implements ExecutableCommand<Void> {

    private static final long serialVersionUID = 3255044102543531497L;

    private DisconnectedFactHandle handle;

    @XmlElement
    private Object object;

    @XmlElement
    @XmlSchemaType(name="string")
    private String entryPoint = "DEFAULT";

    @XmlElement
    private String[] modifiedProperties;

    public UpdateCommand() {
    }

    public UpdateCommand(FactHandle handle,
                         Object object) {
        this.handle = DisconnectedFactHandle.newFrom( handle );
        this.object = object;
    }

    public UpdateCommand(FactHandle handle,
                         Object object,
                         String[] modifiedProperties) {
        this( handle, object );
        this.modifiedProperties = modifiedProperties;
    }

    public String getEntryPoint() {
        return entryPoint;
    }

    public void setEntryPoint(String entryPoint) {
    	if (entryPoint == null) {
    		entryPoint = "DEFAULT";
    	}
        this.entryPoint = entryPoint;
    }

    public Object getObject() {
        return object;
    }

    public DisconnectedFactHandle getHandle() {
        return this.handle;
    }

    @XmlElement(name="fact-handle", required=true)
    public void setFactHandleFromString(String factHandleId) {
        handle = new DisconnectedFactHandle(factHandleId);
    }

    public String getFactHandleFromString() {
        return handle.toExternalForm();
    }

    public Void execute(Context context) {
        KieSession ksession = ((RegistryContext) context).lookup( KieSession.class );
        EntryPoint ep = ksession.getEntryPoint( handle.getEntryPointId() );
        if (modifiedProperties != null) {
            ep.update( handle, object, modifiedProperties );
        } else {
            ep.update( handle, object );
        }
        return null;
    }

    public String toString() {
        return "session.update( " + handle + ", " + object +
               (modifiedProperties != null ? ", " + Arrays.toString( modifiedProperties ) : "") + " );";
    }
}
