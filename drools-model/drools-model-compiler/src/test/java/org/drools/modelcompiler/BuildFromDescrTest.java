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

import java.util.Collection;

import org.drools.compiler.lang.api.DescrFactory;
import org.drools.compiler.lang.descr.PackageDescr;
import org.drools.modelcompiler.domain.Person;
import org.drools.modelcompiler.domain.Result;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.internal.utils.KieHelper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.drools.modelcompiler.BaseModelTest.getObjectsIntoList;

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
        assertThat(ksession.fireAllRules()).isEqualTo(1);
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
        assertThat(results.size()).isEqualTo(1);
        assertThat(results.iterator().next().getValue()).isEqualTo(77);
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
        assertThat(results.size()).isEqualTo(2);
        assertThat(results.stream().map(Result::toString).anyMatch(s -> "M:77".equals(s))).isTrue();
        assertThat(results.stream().map(Result::toString).anyMatch(s -> "E:68".equals(s))).isTrue();
    }
}
