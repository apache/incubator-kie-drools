/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.decisiontable.integrationtests;

import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.internal.builder.IncrementalResults;
import org.kie.internal.builder.InternalKieBuilder;

import java.io.InputStream;

import static org.junit.Assert.assertEquals;

public class IncrementalCompilationTest {

    @Test
    public void testDuplicateXLSResources() throws Exception {

        KieServices ks = KieServices.Factory.get();

        KieFileSystem kfs = ks.newKieFileSystem();

        InputStream in1 = null;
        InputStream in2 = null;

        try {

            //Add XLS decision table
            in1 = this.getClass().getResourceAsStream( "incrementalBuild.dtable.xls" );
            kfs.write( "src/main/resources/incrementalBuild1.dtable.xls",
                       KieServices.Factory.get().getResources().newInputStreamResource( in1 ) );

            //Add the same XLS decision table again as a different resource
            in2 = this.getClass().getResourceAsStream( "incrementalBuild.dtable.xls" );
            kfs.write( "src/main/resources/incrementalBuild2.dtable.xls",
                       KieServices.Factory.get().getResources().newInputStreamResource( in2 ) );

            //Check errors on a full build
            assertEquals( 2, ks.newKieBuilder( kfs ).buildAll().getResults().getMessages().size() );

        } finally {
            if ( in1 != null ) {
                in1.close();
            }
            if ( in2 != null ) {
                in2.close();
            }
        }

    }

    @Test
    public void testIncrementalCompilationDuplicateXLSResources() throws Exception {

        KieServices ks = KieServices.Factory.get();

        KieFileSystem kfs = ks.newKieFileSystem();

        InputStream in1 = null;
        InputStream in2 = null;

        try {

            //Add XLS decision table
            in1 = this.getClass().getResourceAsStream( "incrementalBuild.dtable.xls" );
            kfs.write( "src/main/resources/incrementalBuild1.dtable.xls",
                       KieServices.Factory.get().getResources().newInputStreamResource( in1 ) );

            //Expect no errors
            KieBuilder kieBuilder = ks.newKieBuilder( kfs ).buildAll();
            assertEquals( 0, kieBuilder.getResults().getMessages( org.kie.api.builder.Message.Level.ERROR ).size() );

            //Add the same XLS decision table again as a different resource
            in2 = this.getClass().getResourceAsStream( "incrementalBuild.dtable.xls" );
            kfs.write( "src/main/resources/incrementalBuild2.dtable.xls",
                       KieServices.Factory.get().getResources().newInputStreamResource( in2 ) );
            IncrementalResults addResults = ( (InternalKieBuilder) kieBuilder ).createFileSet( "src/main/resources/incrementalBuild2.dtable.xls" ).build();

            //Expect duplicate rule errors
            assertEquals( 1, addResults.getAddedMessages().size() );
            assertEquals( 0, addResults.getRemovedMessages().size() );

            //Check errors on a full build
            assertEquals( 2, ks.newKieBuilder( kfs ).buildAll().getResults().getMessages().size() );

        } finally {
            if ( in1 != null ) {
                in1.close();
            }
            if ( in2 != null ) {
                in2.close();
            }
        }

    }

}
