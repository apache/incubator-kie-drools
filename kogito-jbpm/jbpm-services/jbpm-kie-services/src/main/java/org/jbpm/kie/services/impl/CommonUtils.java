/*
 * Copyright 2014 JBoss by Red Hat.
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

package org.jbpm.kie.services.impl;

import java.lang.reflect.Field;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import org.jbpm.process.audit.command.AuditCommand;
import org.kie.api.command.Command;
import org.kie.internal.command.ProcessInstanceIdCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommonUtils {
	
	private static final Logger logger = LoggerFactory.getLogger(CommonUtils.class);

	// TODO: https://issues.jboss.org/browse/JBPM-4296
	public static Long getProcessInstanceId(Command<?> command) {
		if (command instanceof ProcessInstanceIdCommand<?>) {
			return ((ProcessInstanceIdCommand<?>) command).getProcessInstanceId();
		} else if( command instanceof AuditCommand<?> ) { 
            return null;
        }
        try {
            Field[] fields = command.getClass().getDeclaredFields();

            for (Field field : fields) {
                field.setAccessible(true);
                if (field.isAnnotationPresent(XmlAttribute.class)) {
                    String attributeName = field.getAnnotation(XmlAttribute.class).name();

                    if ("process-instance-id".equalsIgnoreCase(attributeName)) {
                        return (Long) field.get(command);
                    } else if ("processInstanceId".equals(field.getName())) {
                    	return (Long) field.get(command);
                    }
                } else if (field.isAnnotationPresent(XmlElement.class)) {
                    String elementName = field.getAnnotation(XmlElement.class).name();

                    if ("process-instance-id".equalsIgnoreCase(elementName)) {
                        return (Long) field.get(command);
                    } else if ("processInstanceId".equals(field.getName())) {
                    	return (Long) field.get(command);
                    }
                } else if ("processInstanceId".equals(field.getName())) {
                	return (Long) field.get(command);
                }
            }
        } catch (Exception e) {
            logger.debug("Unable to find process instance id on command {} due to {}", command, e.getMessage());
        }

        return null;
		
	}
}
