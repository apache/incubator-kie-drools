/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.model.codegen.execmodel.exchange;

import java.util.ArrayList;
import java.util.List;

import org.drools.core.reteoo.AsyncMessagesCoordinator;
import org.drools.model.Global;
import org.drools.model.Model;
import org.drools.model.Rule;
import org.drools.model.Variable;
import org.drools.model.impl.Exchange;
import org.drools.model.impl.ModelImpl;
import org.drools.modelcompiler.KieBaseBuilder;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;

import static org.assertj.core.api.Assertions.assertThat;
import static org.drools.model.DSL.declarationOf;
import static org.drools.model.DSL.exchangeOf;
import static org.drools.model.DSL.globalOf;
import static org.drools.model.DSL.on;
import static org.drools.model.PatternDSL.pattern;
import static org.drools.model.PatternDSL.receive;
import static org.drools.model.PatternDSL.rule;
import static org.drools.model.PatternDSL.send;

public class SendReceiveTest {

    @Test
    public void testAsync() {
        Global<List> messages = globalOf( List.class, "defaultpkg", "messages" );
        Variable<Integer> length = declarationOf( Integer.class );
        Exchange<String> exchange = exchangeOf( String.class );

        Rule send = rule( "send" )
                .build(
                        send(exchange).message( () -> {
                            try {
                                Thread.sleep(1_000L);
                            } catch (InterruptedException e) {
                                throw new RuntimeException( e );
                            }
                            return "Hello World!";
                        } )
                );

        Rule receive = rule( "receive" )
                .build(
                        pattern(length),
                        receive(exchange).expr(length, (s, l) -> s.length() > l),
                        on(exchange, length, messages).execute((s, l, m) -> m.add( "received message '" + s + "' longer than " + l))
                );

        Model model = new ModelImpl().addRule( send ).addRule( receive ).addGlobal( messages );
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel( model );

        KieSession ksession = kieBase.newKieSession();

        List<String> list = new ArrayList<>();
        ksession.setGlobal( "messages", list );

        assertThat(AsyncMessagesCoordinator.get().getListeners().size()).isEqualTo(1);

        ksession.insert( 10 );

        new Thread( () -> ksession.fireUntilHalt() ).start();

        try {
            Thread.sleep( 2_000L );
        } catch (InterruptedException e) {
            throw new RuntimeException( e );
        }

        ksession.halt();
        ksession.dispose();

        assertThat(list.size()).isEqualTo(1);
        assertThat(list.get(0)).isEqualTo("received message 'Hello World!' longer than 10");
        assertThat(AsyncMessagesCoordinator.get().getListeners().size()).isEqualTo(0);
    }

    @Test
    public void testAsyncWith2KBase() {
        Exchange<String> exchange = exchangeOf( String.class );

        Rule send = rule( "send" )
                .build(
                        send(exchange).message( () -> {
                            try {
                                Thread.sleep(1_000L);
                            } catch (InterruptedException e) {
                                throw new RuntimeException( e );
                            }
                            return "Hello World!";
                        } )
                );

        Variable<Integer> length = declarationOf( Integer.class );
        Global<List> messages = globalOf( List.class, "defaultpkg", "messages" );

        Rule receive = rule( "receive" )
                .build(
                        pattern(length),
                        receive(exchange).expr(length, (s, l) -> s.length() > l),
                        on(exchange, length, messages).execute((s, l, m) -> m.add( "received message '" + s + "' longer than " + l))
                );

        Model model1 = new ModelImpl().addRule( send );
        KieBase kieBase1 = KieBaseBuilder.createKieBaseFromModel( model1 );
        KieSession ksession1 = kieBase1.newKieSession();

        Model model2 = new ModelImpl().addRule( receive ).addGlobal( messages );
        KieBase kieBase2 = KieBaseBuilder.createKieBaseFromModel( model2 );
        KieSession ksession2 = kieBase2.newKieSession();

        List<String> list = new ArrayList<>();
        ksession2.setGlobal( "messages", list );

        ksession2.insert( 10 );
        new Thread( () -> ksession2.fireUntilHalt() ).start();

        new Thread( () -> ksession1.fireUntilHalt() ).start();

        try {
            Thread.sleep( 2_000L );
        } catch (InterruptedException e) {
            throw new RuntimeException( e );
        }

        ksession1.halt();
        ksession1.dispose();
        ksession2.halt();
        ksession2.dispose();

        assertThat(list.size()).isEqualTo(1);
        assertThat(list.get(0)).isEqualTo("received message 'Hello World!' longer than 10");
    }
}
