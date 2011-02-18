/*
 * Copyright 2010 JBoss Inc
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

package org.drools.xml.jaxb.util;

import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.drools.command.runtime.BatchExecutionCommandImpl;
import org.drools.command.runtime.GetGlobalCommand;
import org.drools.command.runtime.SetGlobalCommand;
import org.drools.command.runtime.process.AbortWorkItemCommand;
import org.drools.command.runtime.process.CompleteWorkItemCommand;
import org.drools.command.runtime.process.SignalEventCommand;
import org.drools.command.runtime.process.StartProcessCommand;
import org.drools.command.runtime.rule.FireAllRulesCommand;
import org.drools.command.runtime.rule.GetObjectsCommand;
import org.drools.command.runtime.rule.InsertElementsCommand;
import org.drools.command.runtime.rule.InsertObjectCommand;
import org.drools.command.runtime.rule.ModifyCommand;
import org.drools.command.runtime.rule.QueryCommand;
import org.drools.command.runtime.rule.RetractCommand;
import org.drools.command.runtime.rule.ModifyCommand.SetterImpl;
import org.drools.common.DefaultFactHandle;
import org.drools.runtime.impl.ExecutionResultImpl;
import org.drools.runtime.rule.impl.FlatQueryResults;

/**
 *
 * @author salaboy
 */
public class DroolsJaxbContextHelper {

    public static final String[] JAXB_ANNOTATED_CMD = {BatchExecutionCommandImpl.class.getName(),
        												SetGlobalCommand.class.getName(),
        												GetGlobalCommand.class.getName(),
        												FireAllRulesCommand.class.getName(),
        												InsertElementsCommand.class.getName(),
        												InsertObjectCommand.class.getName(),
        												ModifyCommand.class.getName(),
        												SetterImpl.class.getName(),
        												QueryCommand.class.getName(),
        												RetractCommand.class.getName(),
        												AbortWorkItemCommand.class.getName(),
        												SignalEventCommand.class.getName(),
        												StartProcessCommand.class.getName(),
        												BatchExecutionCommandImpl.class.getName(),
        												ExecutionResultImpl.class.getName(),
        												DefaultFactHandle.class.getName(),
        												JaxbListWrapper.class.getName(),
        												FlatQueryResults.class.getName(),
        												CompleteWorkItemCommand.class.getName(),
        												GetObjectsCommand.class.getName()
    };

    public static JAXBContext createDroolsJaxbContext(List<String> classNames, Map<String, ?> properties) throws ClassNotFoundException, JAXBException {
        int i = 0;
        Class<?>[] classes = new Class[classNames.size() + JAXB_ANNOTATED_CMD.length];

        for (i = 0; i < classNames.size(); i++) {
        	classes[i] = Class.forName(classNames.get(i));
        }
        int j = 0;
        for (i = classNames.size(); i < classes.length; i++, j++) {
        	classes[i] = Class.forName(JAXB_ANNOTATED_CMD[j]);
        }
        return JAXBContext.newInstance(classes, properties);

    }
}
