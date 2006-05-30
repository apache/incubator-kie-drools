package org.drools.lang.dsl.template;

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

import java.util.List;

import junit.framework.TestCase;

public class TemplateFactoryTest extends TestCase {

    public void testMake() {
        final TemplateFactory factory = new TemplateFactory();
        final Template ctx = factory.getTemplate( "something {0} going {1} on." );
        assertNotNull( ctx );
    }

    public void testLex() {
        final TemplateFactory factory = new TemplateFactory();
        List list = factory.lexChunks( "one chunk" );
        assertEquals( 1,
                      list.size() );
        assertEquals( "one chunk",
                      list.get( 0 ) );

        list = factory.lexChunks( "three {0} chunks" );
        assertEquals( 3,
                      list.size() );

        assertEquals( "three",
                      list.get( 0 ) );
        assertEquals( "{0}",
                      list.get( 1 ) );
        assertEquals( "chunks",
                      list.get( 2 ) );

        list = factory.lexChunks( "{42} more '{0}' chunks" );
        assertEquals( 4,
                      list.size() );

        assertEquals( "{42}",
                      list.get( 0 ) );
        assertEquals( "more '",
                      list.get( 1 ) );
        assertEquals( "{0}",
                      list.get( 2 ) );
        assertEquals( "' chunks",
                      list.get( 3 ) );

    }

}