/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.compiler.integrationtests;

import org.drools.compiler.Person;
import org.junit.Test;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.utils.KieHelper;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class FireUntilHaltTest {

    @Test
    public void testSubmitOnFireUntilHalt() throws InterruptedException {
        String drl =
                "import " + Person.class.getCanonicalName() + "\n" +
                "global java.util.List list;" +
                "rule R when\n" +
                "    Person( happy, age >= 18 )\n" +
                "then\n" +
                "    list.add(\"happy adult\");" +
                "end";

        final KieSession kSession = new KieHelper().addContent( drl, ResourceType.DRL )
                                                     .build().newKieSession();

        List<String> list = new ArrayList<String>();
        kSession.setGlobal( "list", list );

        new Thread( new Runnable() {
            @Override
            public void run() {
                kSession.fireUntilHalt();
            }
        } ).start();

        final Person p = new Person("me", 17, true);
        final FactHandle fh = kSession.insert( p );

        Thread.sleep( 100L );
        assertEquals(0, list.size());

        kSession.submit( new KieSession.AtomicAction() {
            @Override
            public void execute( KieSession kieSession ) {
                p.setAge( 18 );
                p.setHappy( false );
                kieSession.update( fh, p );
            }
        } );

        Thread.sleep( 100L );
        assertEquals(0, list.size());

        kSession.submit( new KieSession.AtomicAction() {
            @Override
            public void execute( KieSession kieSession ) {
                p.setHappy( true );
                kieSession.update( fh, p );
            }
        } );

        Thread.sleep( 100L );
        assertEquals(1, list.size());

        kSession.halt();
        kSession.dispose();
    }
}
