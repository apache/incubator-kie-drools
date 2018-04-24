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

package org.jbpm.casemgmt.impl.marshalling;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.drools.core.marshalling.impl.ClassObjectMarshallingStrategyAcceptor;
import org.drools.core.marshalling.impl.SerializablePlaceholderResolverStrategy;
import org.drools.persistence.api.TransactionAware;
import org.drools.persistence.api.TransactionManager;
import org.jbpm.casemgmt.api.model.instance.CaseRoleInstance;
import org.jbpm.casemgmt.api.model.instance.CommentInstance;
import org.jbpm.casemgmt.impl.model.instance.CaseFileInstanceImpl;
import org.kie.api.marshalling.ObjectMarshallingStrategy;
import org.kie.internal.runtime.Cacheable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CaseFileInstanceMarshallingStrategy implements ObjectMarshallingStrategy, TransactionAware, Cacheable {

    private static final Logger logger = LoggerFactory.getLogger(CaseFileInstanceMarshallingStrategy.class);
    
    private static final String CASE_ID_KEY = "CaseId";
    private static final String CASE_DEF_ID_KEY = "CaseDefId";
    private static final String CASE_START_KEY = "CaseStart";
    private static final String CASE_END_KEY = "CaseEnd";
    private static final String CASE_REOPEN_KEY = "CaseReopen";
    private static final String CASE_ROLE_ASSIGNMENTS_KEY = "CaseRoleAssignments";
    private static final String CASE_COMMENTS_KEY = "CaseComments";
    private static final String CASE_DATA_KEY = "CaseData";
    private static final String CASE_DATA_RESTRICTIONS_KEY = "CaseDataRestrictions";
    private static final String CASE_PARENT_INSTANCE_ID_KEY = "ParentInstanceId";
    private static final String CASE_PARENT_WORK_ITEM_ID_KEY = "ParentWorkItemId";
    
    private Map<String, ObjectMarshallingStrategy> marshallersByName = new LinkedHashMap<String, ObjectMarshallingStrategy>();
    
    private SerializablePlaceholderResolverStrategy caseFileMarshaller = new SerializablePlaceholderResolverStrategy(ClassObjectMarshallingStrategyAcceptor.DEFAULT);
        
    public CaseFileInstanceMarshallingStrategy() {
        marshallersByName.put(caseFileMarshaller.getClass().getName(), caseFileMarshaller);
        logger.debug("Created CaseFileInstance marshaller with default marshaller only");
    }
    
    public CaseFileInstanceMarshallingStrategy(ObjectMarshallingStrategy...strategies) {
        for (ObjectMarshallingStrategy strategy : strategies) {
            logger.debug("Adding {} marshaller into CaseFileInstance marshaller under name {}", strategy, strategy.getClass().getName());
            marshallersByName.put(strategy.getClass().getName(), strategy);
        }
        marshallersByName.put(caseFileMarshaller.getClass().getName(), caseFileMarshaller);
        logger.debug("Created CaseFileInstance marshaller with following marshallers {}", marshallersByName);
    }
    
    @Override
    public void close() {
        marshallersByName.values().stream().filter(m -> m instanceof Cacheable).forEach(m -> {
                logger.debug("Closing {} marshaller on close of {}", m, this);
                ((Cacheable) m).close();
            });
        
    }

    @Override
    public void onStart(TransactionManager txm) {
        marshallersByName.values().stream().filter(m -> m instanceof TransactionAware).forEach(m -> {
            logger.debug("Calling onStart (txm) on {} marshaller", m);
            ((TransactionAware) m).onStart(txm);
        });
    }

    @Override
    public void onEnd(TransactionManager txm) {
        marshallersByName.values().stream().filter(m -> m instanceof TransactionAware).forEach(m -> {
            logger.debug("Calling onEnd (txm) on {} marshaller", m);
            ((TransactionAware) m).onEnd(txm);
        });
    }

    @Override
    public boolean accept(Object object) {
        if (object instanceof CaseFileInstanceImpl) {
            logger.debug("{} object is of CaseFileInstanceImpl type, will be serialized by CaseFileInstanceMarshaller", object);
            return true;
        }
        return false;
    }

    @Override
    public void write(ObjectOutputStream os, Object object) throws IOException {
        throw new UnsupportedOperationException("org.jbpm.casemgmt.impl.marshalling.CaseFileInstanceMarshallingStrategy.write(ObjectOutputStream, Object) is not supported");
    }

    @Override
    public Object read(ObjectInputStream os) throws IOException, ClassNotFoundException {
        throw new UnsupportedOperationException("org.jbpm.casemgmt.impl.marshalling.CaseFileInstanceMarshallingStrategy.read(ObjectInputStream) is not supported");
    }

    @Override
    public byte[] marshal(Context context, ObjectOutputStream os, Object object) throws IOException {
        logger.debug("About to marshal {}", object);
        CaseFileInstanceImpl caseFile = (CaseFileInstanceImpl) object;
        Map<String, Object> caseFileContent = new HashMap<>();
        caseFileContent.put(CASE_ID_KEY, caseFile.getCaseId());
        caseFileContent.put(CASE_DEF_ID_KEY, caseFile.getDefinitionId());
        caseFileContent.put(CASE_START_KEY, caseFile.getCaseStartDate());
        caseFileContent.put(CASE_END_KEY, caseFile.getCaseEndDate());
        caseFileContent.put(CASE_REOPEN_KEY, caseFile.getCaseReopenDate());
        caseFileContent.put(CASE_ROLE_ASSIGNMENTS_KEY, new HashMap<>(caseFile.getRolesAssignments()));
        caseFileContent.put(CASE_COMMENTS_KEY, new ArrayList<>(caseFile.getComments()));
        
        logger.debug("CaseFileContent before case file data is {}", caseFileContent);
        
        List<SerializedContent> caseDataContent = new ArrayList<>();
        caseFileContent.put(CASE_DATA_KEY, caseDataContent);
        // transform with various strategies data that belong to a case
        for (Entry<String, Object> dataEntry : caseFile.getData().entrySet()) {
            byte[] content = null;
            String marshallerName = null;
            logger.debug("About to find marshaller for {}", dataEntry.getValue());
            for (ObjectMarshallingStrategy marshaller : marshallersByName.values()) {
                
                if (marshaller.accept(dataEntry.getValue())) {
                    content = marshaller.marshal(context, os, dataEntry.getValue());
                    marshallerName = marshaller.getClass().getName();
                    logger.debug("Object {} marshalled by {}", dataEntry.getValue(), marshallerName);
                    break;
                }
            }
              
            SerializedContent serializedContent = new SerializedContent(marshallerName, dataEntry.getKey(), content);
            caseDataContent.add(serializedContent);
            logger.debug("Serialized content for object {} is {}", dataEntry.getValue(), serializedContent);
        }
        
        caseFileContent.put(CASE_DATA_RESTRICTIONS_KEY, new HashMap<>(caseFile.getAccessRestrictions()));        
        caseFileContent.put(CASE_PARENT_INSTANCE_ID_KEY, caseFile.getParentInstanceId());
        caseFileContent.put(CASE_PARENT_WORK_ITEM_ID_KEY, caseFile.getParentWorkItemId());
        
        byte[] caseFileBytes = caseFileMarshaller.marshal(context, os, caseFileContent);
        logger.debug("Content of the case file instance after marshaller is of length {}", (caseFileBytes == null ? 0 : caseFileBytes.length));
        return caseFileBytes;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object unmarshal(Context context, ObjectInputStream is, byte[] object, ClassLoader classloader) throws IOException, ClassNotFoundException {
        logger.debug("About to read {} bytes to unmarshal CaseFileInstance", (object == null ? 0 : object.length));
        Map<String, Object> caseFileContent = (Map<String, Object>) caseFileMarshaller.unmarshal(context, is, object, classloader);
        
        CaseFileInstanceImpl caseFileInstance = new CaseFileInstanceImpl();
        caseFileInstance.setCaseId((String)caseFileContent.get(CASE_ID_KEY));
        caseFileInstance.setDefinitionId((String)caseFileContent.get(CASE_DEF_ID_KEY));
        caseFileInstance.setCaseStartDate((Date)caseFileContent.get(CASE_START_KEY));
        caseFileInstance.setCaseEndDate((Date)caseFileContent.get(CASE_END_KEY));
        caseFileInstance.setCaseReopenDate((Date)caseFileContent.get(CASE_REOPEN_KEY));
        caseFileInstance.setRolesAssignments((Map<String, CaseRoleInstance>) caseFileContent.get(CASE_ROLE_ASSIGNMENTS_KEY));
        caseFileInstance.setComments((List<CommentInstance>) caseFileContent.get(CASE_COMMENTS_KEY));        
        logger.debug("CaseFileInstance meta data unmarshalled properly into {}", caseFileInstance);
        List<SerializedContent> caseDataContent = (List<SerializedContent>) caseFileContent.get(CASE_DATA_KEY); 
        logger.debug("About to read serialized content {}", caseDataContent);
        for (SerializedContent serializedContent : caseDataContent) {
            
            ObjectMarshallingStrategy marshaller = marshallersByName.get(serializedContent.getMarshaller());
            logger.debug("Marshaller for {} is of type {}", serializedContent, marshaller);
            Object value = marshaller.unmarshal(context, is, serializedContent.getContent(), classloader);
            caseFileInstance.add(serializedContent.getName(), value);
            logger.debug("Data unmarshalled into {} and put into case file under '{}' name", value, serializedContent.getName());
        }
        caseFileInstance.setAccessRestrictions((Map<String, List<String>>) caseFileContent.get(CASE_DATA_RESTRICTIONS_KEY));   
        caseFileInstance.setParentInstanceId((Long)caseFileContent.get(CASE_PARENT_INSTANCE_ID_KEY));
        caseFileInstance.setParentWorkItemId((Long)caseFileContent.get(CASE_PARENT_WORK_ITEM_ID_KEY));
        logger.debug("Unmarshal of CaseFileInstance completed - result {}", caseFileInstance);
        return caseFileInstance;
    }

    @Override
    public Context createContext() {
        return caseFileMarshaller.createContext();
    }

}
