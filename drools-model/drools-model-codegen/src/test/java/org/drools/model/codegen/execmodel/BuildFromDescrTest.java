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
package org.drools.model.codegen.execmodel;

import java.util.Collection;

import org.drools.drl.ast.descr.PackageDescr;
import org.drools.drl.ast.dsl.DescrFactory;
import org.drools.model.codegen.ExecutableModelProject;
import org.drools.model.codegen.execmodel.domain.Person;
import org.drools.model.codegen.execmodel.domain.Result;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.internal.utils.KieHelper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.drools.model.codegen.execmodel.BaseModelTest.getObjectsIntoList;

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
