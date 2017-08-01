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

import org.osjava.sj.memory.MemoryContext;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;
import java.util.Hashtable;

public class CloseSafeMemoryContextFactory implements InitialContextFactory {
    
    @SuppressWarnings("rawtypes")
    public Context getInitialContext(Hashtable environment) throws NamingException {
    
        return new MemoryContext((Hashtable)environment.clone()) {
            @Override
            public Object lookup(String name) throws NamingException {
                Object toReturn = super.lookup(name);
                if (toReturn == null) {
                    throw new NamingException("Name not found: " + name);
                }
                return toReturn;
            }

            @Override
            public void close() throws NamingException {
                // simple-jndi will close your context: http://meri-stuff.blogspot.co.uk/2012/01/running-jndi-and-jpa-without-j2ee.html
            }
        };
    }
}