package org.drools.base;

/*
 * Copyright 2005 JBoss Inc
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

import junit.framework.TestCase;

import org.drools.spi.FieldExtractor;
import org.drools.util.asm.BeanInherit;
import org.drools.util.asm.TestAbstract;
import org.drools.util.asm.TestAbstractImpl;
import org.drools.util.asm.TestInterface;
import org.drools.util.asm.TestInterfaceImpl;

public class BaseClassFieldExtractorFactoryTest extends TestCase {

    private ClassFieldExtractorCache cache;

    protected void setUp() throws Exception {
        super.setUp();
        cache = ClassFieldExtractorCache.getInstance();
    }

    public void testIt() throws Exception {
        ClassFieldExtractorFactory factory = new ClassFieldExtractorFactory();

        FieldExtractor ex = factory.getClassFieldExtractor( TestBean.class,
                                                            "name",
                                                            Thread.currentThread().getContextClassLoader() );
        assertEquals( 0,
                      ex.getIndex() );
        assertEquals( "michael",
                      ex.getValue( null,
                                   new TestBean() ) );
        ex = factory.getClassFieldExtractor( TestBean.class,
                                             "age",
                                             Thread.currentThread().getContextClassLoader() );
        assertEquals( 1,
                      ex.getIndex() );
        assertEquals( 42,
                      ((Number) ex.getValue( null,
                                             new TestBean() )).intValue() );

    }

    public void testInterface() throws Exception {
        final FieldExtractor ex = cache.getExtractor( TestInterface.class,
                                                      "something",
                                                      getClass().getClassLoader() );
        assertEquals( 0,
                      ex.getIndex() );
        assertEquals( "foo",
                      ex.getValue( null,
                                   new TestInterfaceImpl() ) );
    }

    public void testAbstract() throws Exception {
        final FieldExtractor ex = cache.getExtractor( TestAbstract.class,
                                                      "something",
                                                      getClass().getClassLoader() );
        assertEquals( 0,
                      ex.getIndex() );
        assertEquals( "foo",
                      ex.getValue( null,
                                   new TestAbstractImpl() ) );
    }

    public void testInherited() throws Exception {
        final FieldExtractor ex = cache.getExtractor( BeanInherit.class,
                                                      "text",
                                                      getClass().getClassLoader() );
        assertEquals( "hola",
                      ex.getValue( null,
                                   new BeanInherit() ) );
    }

    public void testSelfReference() throws Exception {
        final FieldExtractor ex = cache.getExtractor( BeanInherit.class,
                                                      "this",
                                                      getClass().getClassLoader() );
        final TestBean bean = new TestBean();
        assertEquals( bean,
                      ex.getValue( null,
                                   bean ) );
    }

}