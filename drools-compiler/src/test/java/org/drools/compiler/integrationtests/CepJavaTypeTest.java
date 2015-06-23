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

package org.drools.compiler.integrationtests;

import org.drools.compiler.CommonTestMethodBase;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;

public class CepJavaTypeTest extends CommonTestMethodBase {

    @Test
    public void testJavaTypeAnnotatedWithRole_WindowTime() {
        String drl = "package org.drools.compiler.integrationtests\n"
                + "\n"
                + "import org.drools.compiler.integrationtests.CepJavaTypeTest.Event;\n"
                + "\n"
                + "rule \"CEP Window Time\"\n"
                + "when\n"
                + "    Event() over window:time (1d)\n"
                + "then\n"
                + "end\n";

        KieServices ks = KieServices.Factory.get();

        KieModuleModel module = ks.newKieModuleModel();

        KieBaseModel defaultBase = module.newKieBaseModel( "defaultKBase" )
                .setDefault( true )
                .addPackage( "*" );
        defaultBase.newKieSessionModel( "defaultKSession" )
                .setDefault( true );

        KieFileSystem kfs = ks.newKieFileSystem().write( "src/main/resources/r1.drl", drl );
        kfs.writeKModuleXML( module.toXML() );
        KieBuilder builder = ks.newKieBuilder( kfs ).buildAll();

        assertTrue( builder.getResults().getMessages().isEmpty() );
    }

    @Test
    public void testJavaTypeAnnotatedWithRole_WindowLength() {
        String drl = "package org.drools.compiler.integrationtests\n"
                + "\n"
                + "import org.drools.compiler.integrationtests.CepJavaTypeTest.Event;\n"
                + "\n"
                + "rule \"CEP Window Length\"\n"
                + "when\n"
                + "    Event() over window:length (10)\n"
                + "then\n"
                + "end\n";

        KieServices ks = KieServices.Factory.get();

        KieModuleModel module = ks.newKieModuleModel();

        KieBaseModel defaultBase = module.newKieBaseModel( "defaultKBase" )
                .setDefault( true )
                .addPackage( "*" );
        defaultBase.newKieSessionModel( "defaultKSession" )
                .setDefault( true );

        KieFileSystem kfs = ks.newKieFileSystem().write( "src/main/resources/r1.drl", drl );
        kfs.writeKModuleXML( module.toXML() );
        KieBuilder builder = ks.newKieBuilder( kfs ).buildAll();

        assertTrue( builder.getResults().getMessages().isEmpty() );
    }

    @org.kie.api.definition.type.Role(value = org.kie.api.definition.type.Role.Type.EVENT)
    public static class Event {

    }

}
