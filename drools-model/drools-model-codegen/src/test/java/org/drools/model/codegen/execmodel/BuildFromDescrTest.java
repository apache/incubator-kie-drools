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

package org.drools.model.codegen.execmodel;

import java.util.Collection;

import org.drools.drl.ast.descr.PackageDescr;
import org.drools.drl.ast.dsl.DescrFactory;
import org.drools.model.codegen.ExecutableModelProject;
import org.drools.model.codegen.execmodel.domain.Person;
import org.drools.model.codegen.execmodel.domain.Result;
import org.junit.Assert;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.internal.utils.KieHelper;

import static org.drools.model.codegen.execmodel.BaseModelTest.getObjectsIntoList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BuildFromDescrTest {

    @Test
    public void testBuildDescr() {
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

    @Test
    public void testAccumulateDescr() {
        PackageDescr pkg =
                DescrFactory.newPackage()
                .name( "org.drools.compiler" )
                .newImport().target( Person.class.getCanonicalName() ).end()
                .newImport().target( Result.class.getCanonicalName() ).end()
                .newRule().name( "R1" )
                .lhs()
                .accumulate()
                .source()
                .pattern("Person").constraint( "name.startsWith(\"M\")" ).bind( "$a", "age", false ).end()
                .end()
                .function( "sum", "$sum", false, "$a" )
                .end()
                .end()
                .rhs( "insert(new Result($sum));" )
                .end()
                .getDescr();

        KieSession ksession = new KieHelper()
                .addContent( pkg )
                .build(ExecutableModelProject.class)
                .newKieSession();

        ksession.insert(new Person("Mark", 37));
        ksession.insert(new Person("Edson", 35));
        ksession.insert(new Person("Mario", 40));

        ksession.fireAllRules();

        Collection<Result> results = getObjectsIntoList(ksession, Result.class);
        assertEquals(1, results.size());
        Assert.assertEquals(77, results.iterator().next().getValue());
    }

    @Test
    public void testGroupByDescr() {
        PackageDescr pkg =
                DescrFactory.newPackage()
                .name( "org.drools.compiler" )
                .newImport().target( Person.class.getCanonicalName() ).end()
                .newImport().target( Result.class.getCanonicalName() ).end()
                .newRule().name( "R1" )
                .lhs()
                .groupBy()
                .groupingFunction("$p.name.substring(0, 1)", "$key")
                .source()
                .pattern("Person").id("$p", false).bind( "$a", "age", false ).end()
                .end()
                .function( "sum", "$sum", false, "$a" )
                .end()
                .end()
                .rhs( "insert(new Result($key+\":\"+$sum));" )
                .end()
                .getDescr();

        KieSession ksession = new KieHelper()
                .addContent( pkg )
                .build(ExecutableModelProject.class)
                .newKieSession();

        ksession.insert(new Person("Mark", 37));
        ksession.insert(new Person("Edson", 35));
        ksession.insert(new Person("Mario", 40));
        ksession.insert(new Person("Edoardo", 33));


        ksession.fireAllRules();

        Collection<Result> results = getObjectsIntoList(ksession, Result.class);
        assertEquals(2, results.size());
        assertTrue( results.stream().map(Result::toString).anyMatch(s -> "M:77".equals(s)) );
        assertTrue( results.stream().map(Result::toString).anyMatch(s -> "E:68".equals(s)) );
    }
}
