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

package org.drools.core.common;

import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.util.ClassUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;

public class DroolsObjectInputStream extends ObjectInputStream
    implements
    DroolsObjectInput {

    private ClassLoader                     classLoader;
    private InternalKnowledgeBase           kBase;
    private InternalWorkingMemory           workingMemory;
    private Package                         pkg;

    public DroolsObjectInputStream(InputStream inputStream) throws IOException {
        this( inputStream,
              null );
    }

    public DroolsObjectInputStream(InputStream inputStream,
                                   ClassLoader classLoader) throws IOException {
        super( inputStream );
        if ( classLoader == null ) {
            classLoader = Thread.currentThread().getContextClassLoader();
            if ( classLoader == null ) {
                classLoader = getClass().getClassLoader();
            }
        }

        this.classLoader = classLoader;

    }

    protected Class resolveClass(String className) throws ClassNotFoundException {
        return ClassUtils.getClassFromName( className, true, this.classLoader );
    }

    protected Class< ? > resolveClass(ObjectStreamClass desc) throws IOException,
                                                             ClassNotFoundException {
        return resolveClass( desc.getName() );
    }

    public static InvalidClassException newInvalidClassException(Class clazz,
                                                                 Throwable cause) {
        InvalidClassException exception = new InvalidClassException( clazz.getName() );
        exception.initCause( cause );
        return exception;
    }

    public ClassLoader getClassLoader() {
        return this.classLoader;
    }

    public InternalKnowledgeBase getKnowledgeBase() {
        return kBase;
    }

    public void setKnowledgeBase(InternalKnowledgeBase kBase) {
        this.kBase = kBase;
        this.classLoader = this.kBase.getRootClassLoader();
    }

    public InternalWorkingMemory getWorkingMemory() {
        return workingMemory;
    }

    public void setWorkingMemory(InternalWorkingMemory workingMemory) {
        this.workingMemory = workingMemory;
    }

    public Package getPackage() {
        return pkg;
    }

    public void setPackage(Package pkg) {
        this.pkg = pkg;
    }

    //    public ClassFieldAccessorCache getExtractorFactory() {
    //        return extractorFactory;
    //    }
    //
    //    public void setExtractorFactory(ClassFieldAccessorCache extractorFactory) {
    //        this.extractorFactory = extractorFactory;
    //    }

    public ClassLoader getParentClassLoader() {
        return classLoader;
    }

    public void setClassLoader(ClassLoader classLoader) {
        if ( classLoader == null ) {
            classLoader = Thread.currentThread().getContextClassLoader();
            if ( classLoader == null ) {
                classLoader = getClass().getClassLoader();
            }
        }
        this.classLoader = classLoader;
    }

}
