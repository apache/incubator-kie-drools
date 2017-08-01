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

package org.jbpm.process.audit.command;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.drools.core.command.runtime.rule.InsertObjectCommand;
import org.jbpm.process.audit.JPAAuditLogService;
import org.junit.Test;
import org.kie.api.command.Command;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SerializationTest {

    private static final Logger log = LoggerFactory.getLogger(JPAAuditLogService.class);
    
    private static Class [] jaxbClasses = { 
        ClearHistoryLogsCommand.class,
        FindActiveProcessInstancesCommand.class,
        FindNodeInstancesCommand.class,
        FindProcessInstanceCommand.class,
        FindProcessInstancesCommand.class,
        FindSubProcessInstancesCommand.class,
        FindVariableInstancesCommand.class
    };
    
    public Object testRoundtrip(Object in) throws Exception {
        String xmlObject = convertJaxbObjectToString(in);
        log.debug(xmlObject);
        return convertStringToJaxbObject(xmlObject);
    }
    
    private static String convertJaxbObjectToString(Object object) throws JAXBException {
        Marshaller marshaller = JAXBContext.newInstance(jaxbClasses).createMarshaller();
        StringWriter stringWriter = new StringWriter();
        
        marshaller.marshal(object, stringWriter);
        String output = stringWriter.toString();
        
        return output;
    }
    
    private static Object convertStringToJaxbObject(String xmlStr) throws JAXBException {
        Unmarshaller unmarshaller = JAXBContext.newInstance(jaxbClasses).createUnmarshaller();
        ByteArrayInputStream xmlStrInputStream = new ByteArrayInputStream(xmlStr.getBytes());
        
        Object jaxbObj = unmarshaller.unmarshal(xmlStrInputStream);
        
        return jaxbObj;
    }
 
    @Test
    public void commandsTest() throws Exception { 
        List<Command<?>> cmds = new ArrayList<Command<?>>();
        cmds.add(new ClearHistoryLogsCommand());
        cmds.add(new FindActiveProcessInstancesCommand("org.jbpm.test.jaxb"));
        cmds.add(new FindNodeInstancesCommand(23, "node"));
        cmds.add(new FindNodeInstancesCommand(42));
        cmds.add(new FindProcessInstanceCommand(125));
        cmds.add(new FindProcessInstancesCommand("org.kie.serialization"));
        cmds.add(new FindProcessInstancesCommand());
        cmds.add(new FindSubProcessInstancesCommand(2048));
        cmds.add(new FindVariableInstancesCommand(37));
        cmds.add(new FindVariableInstancesCommand(74, "mars"));
        
        for( Command<?> cmd : cmds ) {
            testRoundtrip(cmd);
        }
    }
}
