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
package org.jbpm.services.task.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.drools.core.marshalling.impl.ClassObjectMarshallingStrategyAcceptor;
import org.drools.core.marshalling.impl.MarshallerReaderContext;
import org.drools.core.marshalling.impl.MarshallingConfigurationImpl;
import org.drools.core.marshalling.impl.PersisterHelper;
import org.drools.core.marshalling.impl.ProcessMarshallerWriteContext;
import org.drools.core.marshalling.impl.ProtobufMessages.Header;
import org.drools.core.marshalling.impl.SerializablePlaceholderResolverStrategy;
import org.jbpm.marshalling.impl.JBPMMessages;
import org.jbpm.marshalling.impl.JBPMMessages.Variable;
import org.jbpm.marshalling.impl.JBPMMessages.VariableContainer;
import org.jbpm.marshalling.impl.ProtobufProcessMarshaller;
import org.kie.api.marshalling.ObjectMarshallingStrategy;
import org.kie.api.marshalling.ObjectMarshallingStrategyStore;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.EnvironmentName;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.Task;
import org.kie.internal.task.api.TaskModelProvider;
import org.kie.internal.task.api.model.AccessType;
import org.kie.internal.task.api.model.ContentData;
import org.kie.internal.task.api.model.FaultData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.Message;

public class ContentMarshallerHelper {

    private static final Logger logger = LoggerFactory.getLogger(ContentMarshallerHelper.class);
    private static final String SINGLE_VAR_KEY = "_results_";

    public static ContentData marshal(Object o, Environment env) {
        return marshal(null, o, env);
    }
    
    public static ContentData marshal(Task task, Object o, Environment env) {
        if (o == null) {
            return null;
        }
        ContentData content = null;
        byte[] toByteArray = marshallContent(task, o, env);
        content = TaskModelProvider.getFactory().newContentData();
        content.setContent(toByteArray);
        content.setType(o.getClass().getCanonicalName());
        content.setAccessType(AccessType.Inline); 

        return content;
    }
    
    public static FaultData marshalFault(Map<String, Object> fault, Environment env) {
        return marshalFault(null, fault, env);
    }
    
    public static FaultData marshalFault(Task task, Map<String, Object> fault, Environment env) {
        
        FaultData content = null;
        byte[] toByteArray = marshallContent(task, fault, env);
        content = TaskModelProvider.getFactory().newFaultData();
        content.setContent(toByteArray);
        content.setType(fault.getClass().getCanonicalName());
        content.setAccessType(AccessType.Inline);
        content.setFaultName((String)fault.get("faultName"));
        content.setType((String)fault.get("faultType"));

        return content;
    }
    

    public static Object unmarshall(byte[] content, Environment env) {
        return unmarshall(content, env, null);
    }   

    public static Object unmarshall(byte[] content, Environment env, ClassLoader classloader) {
        MarshallerReaderContext context = null;
        try {
            ByteArrayInputStream stream = new ByteArrayInputStream(content);
            MarshallingConfigurationImpl marshallingConfigurationImpl = null;
            if (env != null) {
                marshallingConfigurationImpl = new MarshallingConfigurationImpl((ObjectMarshallingStrategy[]) env.get(EnvironmentName.OBJECT_MARSHALLING_STRATEGIES), false, false);
            } else {
                marshallingConfigurationImpl = new MarshallingConfigurationImpl(new ObjectMarshallingStrategy[]{new SerializablePlaceholderResolverStrategy(ClassObjectMarshallingStrategyAcceptor.DEFAULT)}, false, false);
            }
            ObjectMarshallingStrategyStore objectMarshallingStrategyStore = marshallingConfigurationImpl.getObjectMarshallingStrategyStore();
            context = new MarshallerReaderContext(stream, null, null, objectMarshallingStrategyStore, null, env);
            if (classloader != null) {
                context.classLoader = classloader;
            } else {
                context.classLoader = ContentMarshallerHelper.class.getClassLoader();
            }
            ExtensionRegistry registry = PersisterHelper.buildRegistry(context, null);
            Header _header = PersisterHelper.readFromStreamWithHeaderPreloaded(context, registry);
            
            try {
	            VariableContainer parseFrom = JBPMMessages.VariableContainer.parseFrom(_header.getPayload(), registry);
	            Map<String, Object> value = ProtobufProcessMarshaller.unmarshallVariableContainerValue(context, parseFrom);
	            // in case there was single variable stored return only that variable and not map
	            if (value.containsKey(SINGLE_VAR_KEY) && value.size() == 1) {
	            	return value.get(SINGLE_VAR_KEY);
	            }
	
	            return value;
            } catch (Exception e) {
            	// backward compatible fallback mechanism to ensure existing data can be read properly
            	return fallbackParse(context, _header, registry);
            }
        } catch (Exception ex) {
        	logger.warn("Exception while unmarshaling content", ex);
        }
        return null;
    }

    public static byte[] marshallContent(Object o, Environment env) {
        return marshallContent(null, o, env);
    }
    
    @SuppressWarnings("unchecked")
	public static byte[] marshallContent(Task task, Object o, Environment env) {
        ProcessMarshallerWriteContext context;
        try {
            MarshallingConfigurationImpl marshallingConfigurationImpl = null;
            if (env != null) {
                marshallingConfigurationImpl = new MarshallingConfigurationImpl((ObjectMarshallingStrategy[]) env.get(EnvironmentName.OBJECT_MARSHALLING_STRATEGIES), false, false);
            } else {
                marshallingConfigurationImpl = new MarshallingConfigurationImpl(new ObjectMarshallingStrategy[]{new SerializablePlaceholderResolverStrategy(ClassObjectMarshallingStrategyAcceptor.DEFAULT)}, false, false);
            }
            ObjectMarshallingStrategyStore objectMarshallingStrategyStore = marshallingConfigurationImpl.getObjectMarshallingStrategyStore();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();

            context = new ProcessMarshallerWriteContext(stream, null, null, null, objectMarshallingStrategyStore, env);
            if (task != null) {
                context.setTaskId(task.getId());
                context.setProcessInstanceId(task.getTaskData().getProcessInstanceId());
                context.setWorkItemId(task.getTaskData().getWorkItemId());
                // determine state of the task
                int taskState = ProcessMarshallerWriteContext.STATE_ACTIVE;
                if (task.getTaskData().getStatus() == Status.Completed || 
                        task.getTaskData().getStatus() == Status.Error ||
                        task.getTaskData().getStatus() == Status.Exited ||
                        task.getTaskData().getStatus() == Status.Failed ||
                        task.getTaskData().getStatus() == Status.Obsolete) {
                    taskState = ProcessMarshallerWriteContext.STATE_COMPLETED;
                }
                context.setState(taskState);
            }
            Map<String, Object> input = null;
            if (o instanceof Map) {
            	input = (Map<String, Object>) o;
            } else {
            	// in case there is only single variable to be stored place it into a map under special key
            	input = new HashMap<String, Object>();
            	input.put(SINGLE_VAR_KEY, o);
            }
            Message marshallVariable = ProtobufProcessMarshaller.marshallVariablesContainer(context, input);
            PersisterHelper.writeToStreamWithHeader(context, marshallVariable);

            context.close();

            return stream.toByteArray();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
	private static Object fallbackParse(MarshallerReaderContext context, Header header, ExtensionRegistry registry) throws Exception {
    	Variable parseFrom = JBPMMessages.Variable.parseFrom(header.getPayload(), registry);
        Object value = ProtobufProcessMarshaller.unmarshallVariableValue(context, parseFrom);

        if (value instanceof Map) {
            Map result = new HashMap();
            Map<String, Variable> variablesMap = (Map<String, Variable>) value;
            for (String key : variablesMap.keySet()) {
                result.put(key, ProtobufProcessMarshaller.unmarshallVariableValue(context, variablesMap.get(key)));
            }
            return result;
        }
        return value;
    }
}
