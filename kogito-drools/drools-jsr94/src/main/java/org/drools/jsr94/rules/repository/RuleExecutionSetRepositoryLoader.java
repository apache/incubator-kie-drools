/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.drools.jsr94.rules.repository;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Properties;

/**
 * Load the <code>RuleExecutionSetRepository</code> using the following algorithm.
 * 
 * 1. If a resource with the name of META-INF/services/org.kie.jsr94.rules.repository.RuleExecutionSetRepository exists,
 * then its first line, if present, is used as the UTF-8 encoded name of the implementation class.
 * 
 * 2. If the drools.properties file exists in the classpath and it is readable by the 
 * java.util.Properties.load(InputStream) method and it contains an entry whose key is 
 * org.kie.jsr94.rules.repository.RuleExecutionSetRepository, then the value of that
 * entry is used as the name of the implementation class.
 * 
 * 3. If a system property with the name org.kie.jsr94.rules.repository.RuleExecutionSetRepository
 * is defined, then its value is used as the name of the implementation class.
 * 
 * 4. Finally, a default implementation class name, if provided, is used.
 * 
 * @version $Revision$ $Date$
 */
public abstract class RuleExecutionSetRepositoryLoader
{
    /**
     * Loads the <code>RuleExecutionSetRepository</code> using the
     * algorithm described above.
     *
     * @param defaultFactoryName the className of the default
     *  <code>RuleExecutionSetRepository</code> implementation
     * @return
     */
    public static RuleExecutionSetRepository loadRuleExecutionSetRepository(
            String defaultFactoryName) {

        Object factory = null;
        String factoryName = null;
        ClassLoader cL = Thread.currentThread().getContextClassLoader();

        // Use the Services API (as detailed in the JAR specification), if available, to determine the classname.
        String propertyName = "org.drools.jsr94.rules.repository.RuleExecutionSetRepository";
        String fileName = "META-INF/services/" + propertyName;
        InputStream in = cL.getResourceAsStream(fileName);

        if (in != null) {
            BufferedReader reader = null;

            try {
                reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
                factoryName = reader.readLine();

                if (factoryName != null) {
                    factory = createFactory(cL, factoryName);
                }

            } catch (UnsupportedEncodingException e) {
                throw new IllegalStateException("Failed to load " + propertyName + ": " + factoryName, e);
            } catch (IOException e) {
                throw new IllegalStateException("Failed to load " + propertyName + ": " + factoryName, e);
            } finally {
                close(reader);
            }
        }

        // Use the properties file "drools.properties"
        if (factory == null) {
            // TODO
        }

        // Use system property
        if (factory == null) {
            PrivilegedAction action = new PropertyAccessAction(propertyName);
            factoryName = (String)AccessController.doPrivileged(action);

            if (factoryName != null) {
                factory = createFactory(cL, factoryName);
            }
        }

        // Use the default factory implementation class.
        if (factory == null && defaultFactoryName != null) {
            factory = createFactory(cL, defaultFactoryName);
        }

        return (RuleExecutionSetRepository)factory;
    }

    /**
     * TODO
     *
     * @param closeable
     */
    private static void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                // ignored
            }
        }
    }

    /**
     * TODO
     *
     * @param cL
     * @param factoryName
     * @return
     */
    private static Object createFactory(ClassLoader cL, String factoryName) {
        try {
            Class factoryClass = cL.loadClass(factoryName);
            return factoryClass.newInstance();
        } catch (Throwable t) {
            throw new IllegalStateException("Failed to load: " + factoryName, t);
        }
    }

    private static class PropertyAccessAction implements PrivilegedAction
    {
        private String name;

        PropertyAccessAction(String name) {
            this.name = name;
        }

        public Object run() {
            return System.getProperty(name);
        }
    }

    private static class PropertyFileAccessAction implements PrivilegedAction
    {
        private String fileName;

        PropertyFileAccessAction(String fileName) {
            this.fileName = fileName;
        }

        public Object run() {
            InputStream in = null;

            try {
                in = new FileInputStream(fileName);
                Properties props = new Properties();
                props.load(in);
                return props;
            } catch (IOException e) {
                throw new SecurityException("Cannot load properties: " + fileName, e);
            } finally {
                close(in);
            }
        }
    }
}
