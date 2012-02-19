/*
 * Copyright 2011 JBoss Inc 
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
package org.jbpm.formbuilder.client.bus.ui;

import org.jbpm.formbuilder.shared.task.TaskRef;

import com.google.gwt.event.shared.GwtEvent;

public class EmbededIOReferenceEvent extends GwtEvent<EmbededIOReferenceHandler> {

    public static final Type<EmbededIOReferenceHandler> TYPE = new Type<EmbededIOReferenceHandler>();
    
    private final TaskRef ioRef;
    private final String profileName;
    
    public EmbededIOReferenceEvent(TaskRef ioRef, String profileName) {
        super();
        this.ioRef = ioRef;
        this.profileName = profileName;
    }

    public TaskRef getIoRef() {
        return ioRef;
    }
    
    public String getProfileName() {
        return profileName;
    }
    
    @Override
    public Type<EmbededIOReferenceHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(EmbededIOReferenceHandler handler) {
        handler.onEvent(this);
    }

}
