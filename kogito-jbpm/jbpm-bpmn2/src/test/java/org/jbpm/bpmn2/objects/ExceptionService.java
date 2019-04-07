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

package org.jbpm.bpmn2.objects;

import org.kie.api.runtime.process.WorkItem;

public class ExceptionService {
    
    public static String exceptionParameterName = "my.exception.parameter.name";
    private static ThreadLocal<Object []> localCaughtEventObjectHolder = new ThreadLocal<Object []>();
    
    public String throwException(String message) {
        throw new RuntimeException(message);
    }
    
    public void handleException(WorkItem workItem) {
        Object [] resultHolder =  localCaughtEventObjectHolder.get();
        if( resultHolder != null && resultHolder.length > 0 ) { 
            resultHolder[0] = workItem;
        }
    }
    
    public void setExceptionParameterName(String exceptionParam) { 
        this.exceptionParameterName = exceptionParam;
    }

    public static void setCaughtEventObjectHolder(Object [] testVarArrayHolder) { 
        localCaughtEventObjectHolder.set(testVarArrayHolder);
    }
}
