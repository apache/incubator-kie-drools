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

package org.jbpm.kie.services.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import org.junit.Test;
import org.kie.api.command.Command;
import org.kie.internal.command.ProcessInstanceIdCommand;
import org.reflections.Reflections;
import org.reflections.scanners.FieldAnnotationsScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommonUtilsTest {

    private static final Logger logger = LoggerFactory.getLogger(CommonUtilsTest.class);

    private static final Reflections reflections = new Reflections(
            ClasspathHelper.forPackage("org.drools"),
            ClasspathHelper.forPackage("org.jbpm"),
            new TypeAnnotationsScanner(), 
            new FieldAnnotationsScanner(), new SubTypesScanner());

    @Test
    public void testProcessInstanceIdCommands() {

        List<Class<? extends Command>> cmdClasses 
            = new ArrayList<Class<? extends Command>>(reflections.getSubTypesOf(Command.class));
        assertFalse( "Empty set of command classes to test?!?", cmdClasses.isEmpty() );
       
        // sort alphabetically in order to easily find problems and to make test reproducible
        Collections.sort(cmdClasses, new Comparator<Class>() {
            @Override
            public int compare( Class o1, Class o2 ) {
                if( o1 == null ) { 
                    return -1;
                } else if( o2 == null ) { 
                    return 1;
                } else { 
                    return o1.getName().compareTo(o2.getName());
                }
            }
        });
        
        for( Class<? extends Command> cmdClass : cmdClasses ) { 
            System.out.println(cmdClass.getName());
           Field procInstIdField = findProcessInstanceIdField(cmdClass);
           if( procInstIdField != null ) { 
              List<Class<?>> cmdClassInterfaces = Arrays.asList(cmdClass.getInterfaces());
              assertTrue( cmdClass.getName() + " does not implement the " 
                      + ProcessInstanceIdCommand.class.getSimpleName() + " interface!",
                      cmdClassInterfaces.contains(ProcessInstanceIdCommand.class));
           }
        }
    }

    private static Field findProcessInstanceIdField( Class<? extends Command> cmdClass ) {
        // This code
        try {
            Field[] fields = cmdClass.getDeclaredFields();

            for( Field field : fields ) {
                field.setAccessible(true);
                if( field.isAnnotationPresent(XmlAttribute.class) ) {
                    String attributeName = field.getAnnotation(XmlAttribute.class).name();

                    if( "process-instance-id".equalsIgnoreCase(attributeName) ) {
                        return field;
                    } else if( "processInstanceId".equals(field.getName()) ) {
                        return field;
                    }
                } else if( field.isAnnotationPresent(XmlElement.class) ) {
                    String elementName = field.getAnnotation(XmlElement.class).name();

                    if( "process-instance-id".equalsIgnoreCase(elementName) ) {
                        return field;
                    } else if( "processInstanceId".equals(field.getName()) ) {
                        return field;
                    }
                } else if( "processInstanceId".equals(field.getName()) ) {
                    return field;
                }
            }
        } catch( Exception e ) {
            logger.debug("Unable to find process instance id field in {} due to {}", cmdClass.getName(), e.getMessage());
        }

        return null;
    }
}
