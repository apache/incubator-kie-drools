/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.bpmn2.handler;

import java.util.*;

import org.kie.api.runtime.process.WorkItemHandler;


/**
 * This exception provides extra information about the WorkItemHandler operation called to catchers of this exception.
 * It is only meant to be thrown from a {@link WorkItemHandler} instance method.
 */
public class WorkItemHandlerRuntimeException extends RuntimeException {

    /** Generated serial version uid */
    private static final long serialVersionUID = 1217036861831832336L;
    
    public final static String WORKITEMHANDLERTYPE = "workItemHandlerType";
    
    private HashMap<String, Object> info = new HashMap<String, Object>();
 
    public WorkItemHandlerRuntimeException(Throwable cause, String message) { 
        super(message, cause);
    }

    public WorkItemHandlerRuntimeException(Throwable cause) { 
        super(cause);
    }

    public void setInformation( String informationName, Object information ) { 
        this.info.put(informationName, information);
    }
    
    public Map<String, Object> getInformationMap() { 
        return Collections.unmodifiableMap(this.info);
    }

}
