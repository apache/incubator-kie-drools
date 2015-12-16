/*
Copyright 2013 Red Hat, Inc. and/or its affiliates.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.*/

package org.jbpm.examples.exceptions;

import java.util.HashMap;
import java.util.Map;

import org.jbpm.workflow.instance.WorkflowRuntimeException;
import org.kie.api.KieServices;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.manager.RuntimeEnvironment;
import org.kie.api.runtime.manager.RuntimeEnvironmentBuilder;
import org.kie.api.runtime.manager.RuntimeManagerFactory;

public class ScriptTaskExceptionExample {

    public static final void main(String[] args) {
        runExample();
    }

    public static void runExample() {
    	RuntimeManager manager = createManager();
        KieSession ksession = manager.getRuntimeEngine(null).getKieSession();
        Map<String, Object> params = new HashMap<String, Object>();
        String varName = "var1";
        params.put( varName , "valueOne" );
        try { 
            ksession.startProcess("ExceptionScriptTask", params);
        } catch( WorkflowRuntimeException wfre ) { 
            String msg = "An exception happened in "
                    + "process instance [" + wfre.getProcessInstanceId()
                    + "] of process [" + wfre.getProcessId()
                    + "] in node [id: " + wfre.getNodeId() 
                    + ", name: " + wfre.getNodeName()
                    + "] and variable " + varName + " had the value [" + wfre.getVariables().get(varName)
                    + "]";
            System.out.println(msg);
        }
        
        manager.close();
    }
    
    private static RuntimeManager createManager() {
    	RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newEmptyBuilder()
            .addAsset(KieServices.Factory.get().getResources()
        		.newClassPathResource("exceptions/ScriptTaskException.bpmn2"), ResourceType.BPMN2)
            .get();
        return RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment);
    }
 
}
