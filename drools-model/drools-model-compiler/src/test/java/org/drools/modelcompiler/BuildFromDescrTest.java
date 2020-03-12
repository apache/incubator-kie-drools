/*
 * Copyright (c) 2020. Red Hat, Inc. and/or its affiliates.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.modelcompiler;

import org.drools.compiler.lang.api.DescrFactory;
import org.drools.compiler.lang.descr.PackageDescr;
import org.drools.modelcompiler.domain.Person;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.internal.utils.KieHelper;

import static org.junit.Assert.assertEquals;

public class BuildFromDescrTest {

    @Test
    public void testBuildDescr() throws Exception {
        PackageDescr pkg =
                DescrFactory.newPackage()
                .name( "org.drools.compiler" )
                .newImport().target( Person.class.getCanonicalName() ).end()
                .newRule().name( "R1" )
                .lhs()
                .pattern( "Person" ).constraint( "name == \"Mark\"" ).end()
                .end()
                .rhs( "// do something" )
                .end()
                .newRule().name( "R2" )
                .lhs()
                .pattern( "Person" ).constraint( "name == \"Mario\"" ).end()
                .end()
                .rhs( "// do something" )
                .end()
                .getDescr();

        KieSession ksession = new KieHelper()
                .addContent( pkg )
                .build(ExecutableModelProject.class)
                .newKieSession();

        ksession.insert( new Person( "Mario" ) );
        assertEquals(1, ksession.fireAllRules());
    }
}
