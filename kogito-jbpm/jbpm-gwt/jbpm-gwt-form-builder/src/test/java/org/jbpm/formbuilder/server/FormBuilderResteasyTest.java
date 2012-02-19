/*
 * Copyright 2011 JBoss Inc
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
package org.jbpm.formbuilder.server;

import java.util.Set;

import junit.framework.TestCase;

public class FormBuilderResteasyTest extends TestCase {

    public void testFormBuilderResteasyGetOK() throws Exception {
        FormBuilderResteasy fbr = new FormBuilderResteasy();
        Set<Class<?>> classes = fbr.getClasses();
        Set<Object> singletons = fbr.getSingletons();
        assertNotNull("classes shouldn't be null", classes);
        assertFalse("classes shouldn't be empty", classes.isEmpty());
        assertNotNull("singletons shouldn't be null", singletons);
        assertFalse("singletons shouldn't be empty", singletons.isEmpty());
        
    }
}
