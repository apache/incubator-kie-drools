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

package org.drools.mvel.compiler.kie.builder.impl;

import org.drools.compiler.kie.builder.impl.KieBuilderImpl;
import org.drools.compiler.kie.builder.impl.KieBuilderSetImpl;
import org.drools.mvel.CommonTestMethodBase;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.io.Resource;
import org.kie.internal.builder.IncrementalResults;
import org.kie.internal.io.ResourceFactory;

import static org.junit.Assert.assertEquals;

public class KieBuilderSetImplTest extends CommonTestMethodBase {

    @Test
    public void testBuild() throws Exception {
        final KieServices ks = KieServices.Factory.get();
        final KieFileSystem kfs = ks.newKieFileSystem();

        kfs.write( "src/main/resources/rule%201.drl", ruleContent() );

        final KieBuilderSetImpl kieBuilderSet = new KieBuilderSetImpl( kieBuilder( ks, kfs ) );

        kieBuilderSet.setFiles( new String[]{ "src/main/resources/rule%201.drl" } );

        final IncrementalResults build = kieBuilderSet.build();

        assertEquals( 0, build.getAddedMessages().size() );
        assertEquals( 0, build.getRemovedMessages().size() );
    }

    @Test
    @Ignore("RHPAM-1184, RHDM-601")
    public void testBuildPercentageAndWhiteSpaceInName() throws Exception {
        final KieServices ks = KieServices.Factory.get();
        final KieFileSystem kfs = ks.newKieFileSystem();

        kfs.write("src/main/resources/my rule 100% okay.rdrl",
                  ResourceFactory.newInputStreamResource(this.getClass().getResourceAsStream("my rule 100% okay.rdrl")));

        final KieBuilderSetImpl kieBuilderSet = new KieBuilderSetImpl(kieBuilder(ks, kfs));

        kieBuilderSet.setFiles(new String[]{"src/main/resources/my rule 100% okay.rdrl"});

        final IncrementalResults build = kieBuilderSet.build();

        assertEquals(0, build.getAddedMessages().size());
        assertEquals(0, build.getRemovedMessages().size());
    }

    @Test
    public void testDummyResourceWithAnEncodedFileName() {
        final Resource dummyResource = new KieBuilderSetImpl.DummyResource( "Dummy%20Resource" );
        final Resource testResource = new KieBuilderSetImpl.DummyResource( "Dummy Resource" );

        assertEquals( testResource, dummyResource );
    }

    @Test
    public void testDummyResourceWithWrongEncodedFileName() {
        final Resource dummyResource = new KieBuilderSetImpl.DummyResource("Dummy 100%");
        assertEquals(dummyResource.getSourcePath(), "Dummy 100%");
    }

    private KieBuilderImpl kieBuilder( final KieServices ks,
                                       final KieFileSystem kfs ) {
        final KieBuilder kieBuilder = ks.newKieBuilder( kfs );

        kieBuilder.buildAll();

        return (KieBuilderImpl) kieBuilder;
    }

    private String ruleContent() {
        return "package org.kie.test\n"
                + "import java.util.concurrent.atomic.AtomicInteger\n"
                + "global java.util.List list\n"
                + "rule 'rule 1'\n"
                + "when\n"
                + " $i: AtomicInteger(intValue > 0)\n"
                + "then\n"
                + " list.add( $i );\n"
                + "end\n"
                + "\n";
    }
}
