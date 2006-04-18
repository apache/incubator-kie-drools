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

public class ClassFieldExtractorFactoryTest extends TestCase {

    public void testIt() throws Exception {
        FieldExtractor ex = ClassFieldExtractorFactory.getClassFieldExtractor( TestBean.class,
                                                                               "name" );
        assertEquals( 0,
                      ex.getIndex() );
        assertEquals( "michael",
                      ex.getValue( new TestBean() ) );
        ex = ClassFieldExtractorFactory.getClassFieldExtractor( TestBean.class,
                                                                "age" );
        assertEquals( 1,
                      ex.getIndex() );
        assertEquals( new Integer( 42 ),
                      ex.getValue( new TestBean() ) );

    }

    public void testInterface() throws Exception {
        FieldExtractor ex = ClassFieldExtractorFactory.getClassFieldExtractor( TestInterface.class,
                                                                               "something" );
        assertEquals( 0,
                      ex.getIndex() );
        assertEquals( "foo",
                      ex.getValue( new TestInterfaceImpl() ) );
    }

    public void testAbstract() throws Exception {
        FieldExtractor ex = ClassFieldExtractorFactory.getClassFieldExtractor( TestAbstract.class,
                                                                               "something" );
        assertEquals( 0,
                      ex.getIndex() );
        assertEquals( "foo",
                      ex.getValue( new TestAbstractImpl() ) );
    }

    public void testInherited() throws Exception {
        FieldExtractor ex = ClassFieldExtractorFactory.getClassFieldExtractor( BeanInherit.class,
                                                                               "text" );
        assertEquals( "hola",
                      ex.getValue( new BeanInherit() ) );
    }

}