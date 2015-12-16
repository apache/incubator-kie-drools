/*
 * Copyright 2005 Red Hat, Inc. and/or its affiliates.
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

package org.drools.compiler.integrationtests;

import org.drools.compiler.StockTick;
import org.drools.core.ClockType;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Results;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.utils.KieHelper;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class LengthSlidingWindowTest {

    @Test
    public void testSlidingWindowWithAlphaConstraint() {
        String drl =
                "import " + StockTick.class.getCanonicalName() + "\n" +
                "global java.util.List list;\n" +
                "declare StockTick @role( event ) end\n" +
                "rule R\n" +
                "when \n" +
                "   accumulate( StockTick( company == \"RHT\", $price : price ) over window:length( 3 ); $total : sum($price) )\n"  +
                "then \n" +
                "    list.add($total);\n" +
                "end \n";

        checkPrice( drl, 30.0 );
    }

    @Test
    public void testSlidingWindowWithBetaConstraint() {
        String drl =
                "import " + StockTick.class.getCanonicalName() + "\n" +
                "global java.util.List list;\n" +
                "declare StockTick @role( event ) end\n" +
                "rule R\n" +
                "when \n" +
                "   $s : String()" +
                "   accumulate( StockTick( company == $s, $price : price ) over window:length( 3 ); $total : sum($price) )\n"  +
                "then \n" +
                "    list.add($total);\n" +
                "end \n";

        checkPrice( drl, 10.0 );
    }

    @Test
    public void testSlidingWindowWithDeclaration() {
        String drl =
                "import " + StockTick.class.getCanonicalName() + "\n" +
                "global java.util.List list;\n" +
                "declare StockTick @role( event ) end\n" +
                "declare window RhtStocksWindow\n" +
                "    StockTick() over window:length( 3 )\n" +
                "end\n" +
                "rule R\n" +
                "when \n" +
                "   accumulate( StockTick( company == \"RHT\", $price : price ) from window RhtStocksWindow; $total : sum($price) )\n"  +
                "then \n" +
                "    list.add($total);\n" +
                "end \n";

        checkPrice( drl, 10.0 );
    }

    private void checkPrice( String drl, double expectedPrice ) {
        KieSessionConfiguration sessionConfig = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        sessionConfig.setOption( ClockTypeOption.get( ClockType.PSEUDO_CLOCK.getId() ) );

        KieSession ksession = new KieHelper().addContent( drl, ResourceType.DRL )
                                             .build( EventProcessingOption.STREAM )
                                             .newKieSession( sessionConfig, null );

        List<Double> list = new ArrayList<Double>();
        ksession.setGlobal( "list", list );

        ksession.insert( "RHT" );
        ksession.insert( new StockTick( 1L, "RHT", 10.0 ) );
        ksession.insert( new StockTick( 2L, "RHT", 10.0 ) );
        ksession.insert( new StockTick( 3L, "ABC", 20.0 ) );
        ksession.insert( new StockTick( 4L, "RHT", 10.0 ) );
        ksession.insert( new StockTick( 5L, "XYZ", 20.0 ) );
        ksession.insert( new StockTick( 6L, "XYZ", 20.0 ) );
        ksession.insert( new StockTick( 7L, "RHT", 10.0 ) );

        ksession.fireAllRules();

        assertEquals( 1, list.size() );
        assertEquals( expectedPrice, (double)list.get(0), 0.01 );
    }

    @Test
    public void testCompilationFailureWithUnknownWindow() {
        // DROOLS-841
        String drl =
                "import " + StockTick.class.getCanonicalName() + "\n" +
                "global java.util.List list;\n" +
                "declare StockTick @role( event ) end\n" +
                "declare window RhtStocksWindow\n" +
                "    StockTick() over window:length( 3 )\n" +
                "end\n" +
                "rule R\n" +
                "when \n" +
                "   accumulate( StockTick( company == \"RHT\", $price : price ) from window AbcStocksWindow; $total : sum($price) )\n"  +
                "then \n" +
                "    list.add($total);\n" +
                "end \n";

        KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem().write( "src/main/resources/r1.drl", drl );
        Results results = ks.newKieBuilder( kfs ).buildAll().getResults();
        assertEquals(1, results.getMessages().size());
    }
}
