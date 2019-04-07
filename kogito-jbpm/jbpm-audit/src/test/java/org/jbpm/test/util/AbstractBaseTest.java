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

package org.jbpm.test.util;

import java.lang.reflect.Field;
import java.sql.DatabaseMetaData;

import org.jbpm.process.instance.impl.util.LoggingPrintStream;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;

public abstract class AbstractBaseTest {
    
    @BeforeClass
    public static void configure() { 
        LoggingPrintStream.interceptSysOutSysErr();
        Logger logger = LoggerFactory.getLogger(DatabaseMetaData.class);
    }
   
    public static void hackTheDatabaseMetadataLoggerBecauseTheresALogbackXmlInTheClasspath() { 
        String [] fieldName = { "LOG", "log", "logger" };
        try {
            Object loggerObj = null;
            for( int i = 0; i < fieldName.length; ++i ) { 
                Field loggerField;
                Class objClass = null;
                if( loggerObj == null ) { 
                    objClass = DatabaseMetaData.class;
                } else { 
                   objClass = loggerObj.getClass();
                }
                loggerField = objClass.getDeclaredField(fieldName[i]);
                loggerField.setAccessible(true);
                loggerObj = loggerField.get(loggerObj);
            }
            ((ch.qos.logback.classic.Logger) loggerObj).setLevel(Level.OFF);
        } catch( Exception e ) {
            e.printStackTrace();
            // do nothing
        } 
    }
    
    @AfterClass
    public static void reset() { 
        LoggingPrintStream.resetInterceptSysOutSysErr();
    }
}
