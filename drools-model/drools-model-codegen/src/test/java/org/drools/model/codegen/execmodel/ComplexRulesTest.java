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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.model.codegen.execmodel.domain.ChildFactComplex;
import org.drools.model.codegen.execmodel.domain.ChildFactWithEnum1;
import org.drools.model.codegen.execmodel.domain.ChildFactWithEnum2;
import org.drools.model.codegen.execmodel.domain.ChildFactWithEnum3;
import org.drools.model.codegen.execmodel.domain.ChildFactWithFirings1;
import org.drools.model.codegen.execmodel.domain.ChildFactWithId1;
import org.drools.model.codegen.execmodel.domain.ChildFactWithId2;
import org.drools.model.codegen.execmodel.domain.ChildFactWithId3;
import org.drools.model.codegen.execmodel.domain.ChildFactWithObject;
import org.drools.model.codegen.execmodel.domain.EnumFact1;
import org.drools.model.codegen.execmodel.domain.EnumFact2;
import org.drools.model.codegen.execmodel.domain.InterfaceAsEnum;
import org.drools.model.codegen.execmodel.domain.ManyPropFact;
import org.drools.model.codegen.execmodel.domain.Person;
import org.drools.model.codegen.execmodel.domain.RootFact;
import org.drools.model.codegen.execmodel.domain.SubFact;
import org.junit.Test;
import org.kie.api.runtime.KieSession;

import static org.assertj.core.api.Assertions.assertThat;

public class ComplexRulesTest extends BaseModelTest {

    public ComplexRulesTest( RUN_TYPE testRunType ) {
        super( testRunType );
    }

    @Test
    public void test1() {
        String str =
                "import " + EnumFact1.class.getCanonicalName() + ";\n" +
                "import " + EnumFact2.class.getCanonicalName() + ";\n" +
                "import " + RootFact.class.getCanonicalName() + ";\n" +
                "import " + ChildFactWithId1.class.getCanonicalName() + ";\n" +
                "import " + ChildFactWithId2.class.getCanonicalName() + ";\n" +
                "import " + ChildFactWithObject.class.getCanonicalName() + ";\n" +
                "import " + ChildFactWithId3.class.getCanonicalName() + ";\n" +
                "import " + ChildFactWithEnum1.class.getCanonicalName() + ";\n" +
                "import " + ChildFactWithEnum2.class.getCanonicalName() + ";\n" +
                "import " + ChildFactWithEnum3.class.getCanonicalName() + ";\n" +
                "import " + ChildFactComplex.class.getCanonicalName() + ";\n" +
                "import " + BusinessFunctions.class.getCanonicalName() + ";\n" +
                "global BusinessFunctions functions;\n" +
                "global java.util.List list;\n" +
                "rule \"R1\"\n" +
                "    dialect \"java\"\n" +
                "when\n" +
                "    $rootFact : RootFact(  ) \n" +
                "    $childFact1 : ChildFactWithId1(  parentId == $rootFact.id ) \n" +
                "    $childFact2 : ChildFactWithId2(  parentId == $childFact1.id ) \n" +
                "    $childFactWithEnum1 : ChildFactWithEnum1(  parentId == $childFact2.id, enumValue == EnumFact1.FIRST ) \n" +
                "    $childFactWithObject : ChildFactWithObject(  parentId == $childFact2.id ) \n" +
                "    $childFactWithEnum2 : ChildFactWithEnum2(  parentId == $childFactWithObject.id, enumValue == EnumFact2.SECOND ) \n" +
                "    $countOf : Long( $result : intValue > 0) from accumulate (\n" +
                "        $rootFact_acc : RootFact(  ) \n" +
                "        and $childFact1_acc : ChildFactWithId1(  parentId == $rootFact_acc.id ) \n" +
                "        and $childFact3_acc : ChildFactWithId3(  parentId == $childFact1_acc.id ) \n" +
                "        and $childFactComplex_acc : ChildFactComplex(  parentId == $childFact3_acc.id, \n" +
                "            travelDocReady == true, \n" +
                "            enum1Value not in (EnumFact1.FIRST, EnumFact1.THIRD, EnumFact1.FOURTH), \n" +
                "            $childFactComplex_id : id, \n" +
                "            enum2Value == EnumFact2.FIRST, \n" +
                "            cheeseReady != true ) \n" +
                "        ;count($childFactComplex_id))\n" +
                "    exists ( $childFactWithEnum3_ex : ChildFactWithEnum3(  parentId == $childFact1.id, enumValue == EnumFact1.FIRST ) )\n" +
                "    not ( \n" +
                "        $policySet_not : ChildFactWithObject ( " +
                "            id == $childFactWithObject.id , \n" +
                "            eval(true == functions.arrayContainsInstanceWithParameters((Object[])$policySet_not.getObjectValue(), new Object[]{\"getMessageId\", \"42103\"}))\n" +
                "        )\n" +
                "    )\n" +
                "  then\n" +
                "    list.add($result);\n" +
                "end\n";

        testComplexRule(str);
    }

    @Test
    public void testNotWithEval() {
        String str =
                "import " + EnumFact1.class.getCanonicalName() + ";\n" +
                "import " + EnumFact2.class.getCanonicalName() + ";\n" +
                "import " + RootFact.class.getCanonicalName() + ";\n" +
                "import " + ChildFactWithId1.class.getCanonicalName() + ";\n" +
                "import " + ChildFactWithId2.class.getCanonicalName() + ";\n" +
                "import " + ChildFactWithObject.class.getCanonicalName() + ";\n" +
                "import " + ChildFactWithId3.class.getCanonicalName() + ";\n" +
                "import " + ChildFactWithEnum1.class.getCanonicalName() + ";\n" +
                "import " + ChildFactWithEnum2.class.getCanonicalName() + ";\n" +
                "import " + ChildFactWithEnum3.class.getCanonicalName() + ";\n" +
                "import " + ChildFactComplex.class.getCanonicalName() + ";\n" +
                "import " + BusinessFunctions.class.getCanonicalName() + ";\n" +
                "global BusinessFunctions functions;\n" +
                "global java.util.List list;\n" +
                "rule \"R1\"\n" +
                "    dialect \"java\"\n" +
                "when\n" +
                "    $rootFact : RootFact(  ) \n" +
                "    $childFact1 : ChildFactWithId1(  parentId == $rootFact.id ) \n" +
                "    $childFact2 : ChildFactWithId2(  parentId == $childFact1.id ) \n" +
                "    $childFactWithEnum1 : ChildFactWithEnum1(  parentId == $childFact2.id, enumValue == EnumFact1.FIRST ) \n" +
                "    $childFactWithObject : ChildFactWithObject(  parentId == $childFact2.id ) \n" +
                "    $childFactWithEnum2 : ChildFactWithEnum2(  parentId == $childFactWithObject.id, enumValue == EnumFact2.SECOND ) \n" +
                "    $countOf : Long( $result : intValue > 0) from accumulate (\n" +
                "        $rootFact_acc : RootFact(  ) \n" +
                "        and $childFact1_acc : ChildFactWithId1(  parentId == $rootFact_acc.id ) \n" +
                "        and $childFact3_acc : ChildFactWithId3(  parentId == $childFact1_acc.id ) \n" +
                "        and $childFactComplex_acc : ChildFactComplex(  parentId == $childFact3_acc.id, \n" +
                "            travelDocReady == true, \n" +
                "            enum1Value not in (EnumFact1.FIRST, EnumFact1.THIRD, EnumFact1.FOURTH), \n" +
                "            $childFactComplex_id : id, \n" +
                "            enum2Value == EnumFact2.FIRST, \n" +
                "            cheeseReady != true ) \n" +
                "        ;count($childFactComplex_id))\n" +
                "    exists ( $childFactWithEnum3_ex : ChildFactWithEnum3(  parentId == $childFact1.id, enumValue == EnumFact1.FIRST ) )\n" +
                "    not ( \n" +
                "        $policySet_not : ChildFactWithObject ( " +
                "            id == $childFactWithObject.id , \n" +
                "            eval(true == functions.arrayContainsInstanceWithParameters((Object[])$policySet_not.getObjectValue(), new Object[]{\"getMessageId\", \"42103\"}))\n" +
                "        ) and ChildFactWithId1() and eval(false)\n" +
                "    )\n" +
                "  then\n" +
                "    list.add($result);\n" +
                "end\n";

        testComplexRule(str);
    }

    private void testComplexRule(final String rule) {
        KieSession ksession = getKieSession( rule );

        List<Integer> list = new ArrayList<>();
        ksession.setGlobal( "list", list );
        ksession.setGlobal( "functions", new BusinessFunctions() );

        ksession.insert( new RootFact(1) );
        ksession.insert( new ChildFactWithId1(2, 1) );
        ksession.insert( new ChildFactWithId2(3, 2) );
        ksession.insert( new ChildFactWithEnum1(4, 3, EnumFact1.FIRST) );
        ksession.insert( new ChildFactWithObject(5, 3, new Object[0]) );
        ksession.insert( new ChildFactWithEnum2(6, 5, EnumFact2.SECOND) );
        ksession.insert( new ChildFactWithId3(7, 2) );
        ksession.insert( new ChildFactComplex(8, 7, true, false, EnumFact1.SECOND, EnumFact2.FIRST) );
        ksession.insert( new ChildFactWithEnum3(9, 2, EnumFact1.FIRST) );

        assertThat(ksession.fireAllRules()).isEqualTo(1);
        assertThat(list.size()).isEqualTo(1);
        assertThat((int) list.get(0)).isEqualTo(1);
    }

    @Test
    public void test2() {
        final String drl =
                " import org.drools.model.codegen.execmodel.domain.*;\n" +
                        " rule \"R1\"\n" +
                        " dialect \"java\"\n" +
                        " when\n" +
                        "     $rootFact : RootFact( )\n" +
                        "     $childFact1 : ChildFactWithId1( parentId == $rootFact.id )\n" +
                        "     $childFact2 : ChildFactWithId2( parentId == $childFact1.id )\n" +
                        "     $childFact3 : ChildFactWithEnum1( \n" +
                        "         parentId == $childFact2.id, \n" +
                        "         enumValue == EnumFact1.FIRST, \n" +
                        "         $enumValue : enumValue )\n" +
                        "     $childFact4 : ChildFactWithFirings1( \n" +
                        "         parentId == $childFact1.id, \n" +
                        "         $evaluationName : evaluationName, \n" +
                        "         firings not contains \"R1\" )\n" +
                        " then\n" +
                        "     $childFact4.setEvaluationName(String.valueOf($enumValue));\n" +
                        "     $childFact4.getFirings().add(\"R1\");\n" +
                        "     update($childFact4);\n" +
                        " end\n";

        KieSession ksession = getKieSession( drl );

        int initialId = 1;
        final RootFact rootFact = new RootFact(initialId);

        final ChildFactWithId1 childFact1First = new ChildFactWithId1(initialId + 1, rootFact.getId());
        final ChildFactWithId2 childFact2First = new ChildFactWithId2(initialId + 3, childFact1First.getId());
        final ChildFactWithEnum1 childFact3First = new ChildFactWithEnum1(initialId + 4, childFact2First.getId(), EnumFact1.FIRST);
        final ChildFactWithFirings1 childFact4First = new ChildFactWithFirings1(initialId + 2, childFact1First.getId());

        ksession.insert(rootFact);
        ksession.insert(childFact1First);
        ksession.insert(childFact2First);
        ksession.insert(childFact3First);
        ksession.insert(childFact4First);

        assertThat(ksession.fireAllRules()).isEqualTo(1);
        assertThat(ksession.fireAllRules()).isEqualTo(0);
    }

    @Test
    public void test3() {
        String str =
                "import " + RootFact.class.getCanonicalName() + ";\n" +
                        "import " + ChildFactWithObject.class.getCanonicalName() + ";\n" +
                        "import " + ChildFactComplex.class.getCanonicalName() + ";\n" +
                        "import " + BusinessFunctions.class.getCanonicalName() + ";\n" +
                        "global BusinessFunctions functions;\n" +
                        "global java.util.List list;\n" +
                        "rule \"R1\"\n" +
                        "    dialect \"java\"\n" +
                        "when\n" +
                        "    $childFactWithObject : ChildFactWithObject( $idAsShort : idAsShort ) \n" +
                        "    $countOf : Long( $result : intValue > 0) from accumulate (\n" +
                        "        $rootFact_acc : RootFact(  ) \n" +
                        "        and $childFactComplex_acc : ChildFactComplex(  \n" +
                        "            $childFactComplex_id : id, \n" +
                        "            idAsShort == $idAsShort ) \n" +
                        "        ;count($childFactComplex_id))\n" +
                        "  then\n" +
                        "    list.add($result);\n" +
                        "end\n";

        KieSession ksession = getKieSession( str );

        List<Integer> list = new ArrayList<>();
        ksession.setGlobal( "list", list );
        ksession.setGlobal( "functions", new BusinessFunctions() );

        ksession.insert( new RootFact(1) );
        ksession.insert( new ChildFactWithObject(5, 3, new Object[0]) );
        ksession.insert( new ChildFactComplex(5, 7, true, false, EnumFact1.SECOND, EnumFact2.FIRST) );

        assertThat(ksession.fireAllRules()).isEqualTo(1);
        assertThat(list.size()).isEqualTo(1);
        assertThat((int) list.get(0)).isEqualTo(1);
    }

    @Test
    public void test4() {
        String str =
                "import " + RootFact.class.getCanonicalName() + ";\n" +
                        "import " + ChildFactWithObject.class.getCanonicalName() + ";\n" +
                        "import " + ChildFactComplex.class.getCanonicalName() + ";\n" +
                        "import " + BusinessFunctions.class.getCanonicalName() + ";\n" +
                        "global BusinessFunctions functions;\n" +
                        "global java.util.List list;\n" +
                        "rule \"R1\"\n" +
                        "    dialect \"java\"\n" +
                        "when\n" +
                        "    $childFactWithObject : ChildFactWithObject( $idAsShort : idAsShort ) \n" +
                        "    $countOf : Long( $result : intValue > 0) from accumulate (\n" +
                        "        $childFactComplex_acc : ChildFactComplex(  \n" +
                        "            $childFactComplex_id : id, \n" +
                        "            idAsShort == $idAsShort ) \n" +
                        "        ;count($childFactComplex_id))\n" +
                        "  then\n" +
                        "    list.add($result);\n" +
                        "end\n";

        KieSession ksession = getKieSession( str );

        List<Integer> list = new ArrayList<>();
        ksession.setGlobal( "list", list );
        ksession.setGlobal( "functions", new BusinessFunctions() );

        ksession.insert( new ChildFactWithObject(5, 3, new Object[0]) );
        ksession.insert( new ChildFactComplex(5, 7, true, false, EnumFact1.SECOND, EnumFact2.FIRST) );

        assertThat(ksession.fireAllRules()).isEqualTo(1);
        assertThat(list.size()).isEqualTo(1);
        assertThat((int) list.get(0)).isEqualTo(1);
    }

    @Test
    public void testEnum() {
        String str =
                "import " + EnumFact1.class.getCanonicalName() + ";\n" +
                "import " + ChildFactWithEnum1.class.getCanonicalName() + ";\n" +
                "rule R when\n" +
                "\n" +
                "    $factWithEnum : ChildFactWithEnum1(  parentId == 3, enumValue == EnumFact1.FIRST ) \n" +
                "  then\n" +
                "end\n";

        KieSession ksession = getKieSession( str );
        ksession.insert( new ChildFactWithEnum1(1, 3, EnumFact1.FIRST) );
        assertThat(ksession.fireAllRules()).isEqualTo(1);
    }

    @Test
    public void testNotInEnum() {
        String str =
                "import " + EnumFact1.class.getCanonicalName() + ";\n" +
                "import " + ChildFactWithEnum1.class.getCanonicalName() + ";\n" +
                "rule R when\n" +
                "\n" +
                "    $factWithEnum : ChildFactWithEnum1(  enumValue not in (EnumFact1.FIRST, EnumFact1.THIRD, EnumFact1.FOURTH) ) \n" +
                "  then\n" +
                "end\n";

        KieSession ksession = getKieSession( str );
        ksession.insert( new ChildFactWithEnum1(1, 3, EnumFact1.SECOND) );
        assertThat(ksession.fireAllRules()).isEqualTo(1);
    }

    @Test
    public void testNotInInterfaceAsEnum() {
        String str =
                "import " + InterfaceAsEnum.class.getCanonicalName() + ";\n" +
                "import " + ChildFactWithEnum1.class.getCanonicalName() + ";\n" +
                "rule R when\n" +
                "\n" +
                "    $factWithEnum : ChildFactWithEnum1(  enumValueFromInterface not in (InterfaceAsEnum.FIRST, InterfaceAsEnum.THIRD, InterfaceAsEnum.FOURTH) ) \n" +
                "  then\n" +
                "end\n";

        KieSession ksession = getKieSession( str );
        ksession.insert( new ChildFactWithEnum1(1, 3, EnumFact1.SECOND) );
        assertThat(ksession.fireAllRules()).isEqualTo(1);
    }

    @Test
    public void testConstraintWithFunctionUsingThis() {
        String str =
                "import " + ChildFactWithObject.class.getCanonicalName() + ";\n" +
                "import " + BusinessFunctions.class.getCanonicalName() + ";\n" +
                "global BusinessFunctions functions;\n" +
                "rule R when\n" +
                "    $childFactWithObject : ChildFactWithObject ( id == 5\n" +
                "      , !functions.arrayContainsInstanceWithParameters((Object[])this.getObjectValue(),\n" +
                "                                                       new Object[]{\"getMessageId\", \"42103\"})\n" +
                "    )\n" +
                "  then\n" +
                "end\n";

        KieSession ksession = getKieSession( str );
        ksession.setGlobal( "functions", new BusinessFunctions() );
        ksession.insert( new ChildFactWithObject(5, 1, new Object[0]) );
        assertThat(ksession.fireAllRules()).isEqualTo(1);
    }

    @Test
    public void testConstraintWithTernaryOperator() {
        String str =
                "import " + ChildFactWithObject.class.getCanonicalName() + ";\n" +
                "import " + BusinessFunctions.class.getCanonicalName() + ";\n" +
                "global BusinessFunctions functions;\n" +
                "rule R when\n" +
                "    $s : String()\n" +
                "    $childFactWithObject : ChildFactWithObject ( id == 5\n" +
                "      , !functions.arrayContainsInstanceWithParameters((Object[])this.getObjectValue(),\n" +
                "                                                       new Object[]{\"getMessageId\", ($s != null ? $s : \"42103\")})\n" +
                "    )\n" +
                "  then\n" +
                "end\n";

        KieSession ksession = getKieSession( str );
        ksession.setGlobal( "functions", new BusinessFunctions() );
        ksession.insert( "test" );
        ksession.insert( new ChildFactWithObject(5, 1, new Object[0]) );
        assertThat(ksession.fireAllRules()).isEqualTo(1);
    }

    @Test
    public void testCastInConstraint() {
        String str =
                "import " + ChildFactWithObject.class.getCanonicalName() + ";\n" +
                "import " + BusinessFunctions.class.getCanonicalName() + ";\n" +
                "global BusinessFunctions functions;\n" +
                "rule R when\n" +
                "    ChildFactWithObject ( ((Object[])objectValue).length == 0\n )\n" +
                "  then\n" +
                "end\n";

        KieSession ksession = getKieSession( str );
        ksession.setGlobal( "functions", new BusinessFunctions() );
        ksession.insert( new ChildFactWithObject(5, 1, new Object[0]) );
        assertThat(ksession.fireAllRules()).isEqualTo(1);
    }

    @Test
    public void testConstraintWithFunctionAndStringConcatenation() {
        String str =
                "import " + ChildFactWithObject.class.getCanonicalName() + ";\n" +
                "import " + BusinessFunctions.class.getCanonicalName() + ";\n" +
                "global BusinessFunctions functions;\n" +
                "rule R when\n" +
                "\n" +
                "    $childFactWithObject : ChildFactWithObject ( id == 5\n" +
                "      , !functions.arrayContainsInstanceWithParameters((Object[])objectValue,\n" +
                "                                                       new Object[]{\"getMessageId\", \"\" + id})\n" +
                "    )\n" +
                "  then\n" +
                "end\n";

        KieSession ksession = getKieSession( str );
        ksession.setGlobal( "functions", new BusinessFunctions() );
        ksession.insert( new ChildFactWithObject(5, 1, new Object[0]) );
        assertThat(ksession.fireAllRules()).isEqualTo(1);
    }

    @Test
    public void testEvalWithFunction() {
        String str =
                "import " + ChildFactWithObject.class.getCanonicalName() + ";\n" +
                "import " + BusinessFunctions.class.getCanonicalName() + ";\n" +
                "global BusinessFunctions functions;\n" +
                "rule R when\n" +
                "\n" +
                "    $childFactWithObject : ChildFactWithObject ( id == 5\n" +
                "      , eval(false == functions.arrayContainsInstanceWithParameters((Object[])$childFactWithObject.getObjectValue(),\n" +
                "                                                                    new Object[]{\"getMessageId\", \"42103\"}))\n" +
                "    )\n" +
                "  then\n" +
                "end\n";

        KieSession ksession = getKieSession( str );
        ksession.setGlobal( "functions", new BusinessFunctions() );
        ksession.insert( new ChildFactWithObject(5, 1, new Object[0]) );
        assertThat(ksession.fireAllRules()).isEqualTo(1);
    }

    @Test
    public void testEqualOnShortField() {
        String str =
                "import " + ChildFactWithObject.class.getCanonicalName() + ";\n" +
                "rule R when\n" +
                "\n" +
                "    ChildFactWithObject( idAsShort == 5 )\n" +
                "  then\n" +
                "end\n";

        KieSession ksession = getKieSession( str );
        ksession.insert( new ChildFactWithObject(5, 1, new Object[0]) );
        assertThat(ksession.fireAllRules()).isEqualTo(1);
    }

    @Test
    public void testGreaterOnShortField() {
        String str =
                "import " + ChildFactWithObject.class.getCanonicalName() + ";\n" +
                "rule R when\n" +
                "\n" +
                "    ChildFactWithObject( idAsShort > 0 )\n" +
                "  then\n" +
                "end\n";

        KieSession ksession = getKieSession( str );
        ksession.insert( new ChildFactWithObject(5, 1, new Object[0]) );
        assertThat(ksession.fireAllRules()).isEqualTo(1);
    }

    @Test
    public void testBooleanField() {
        String str =
                "import " + ChildFactWithObject.class.getCanonicalName() + ";\n" +
                "rule R when\n" +
                "\n" +
                "    ChildFactWithObject( idIsEven == false )\n" +
                "  then\n" +
                "end\n";

        KieSession ksession = getKieSession( str );
        ksession.insert( new ChildFactWithObject(5, 1, new Object[0]) );
        assertThat(ksession.fireAllRules()).isEqualTo(1);
    }

    @Test
    public void testConsequenceThrowingException() {
        String str =
                "import " + ChildFactWithObject.class.getCanonicalName() + ";\n" +
                "import " + BusinessFunctions.class.getCanonicalName() + ";\n" +
                "global BusinessFunctions functions;\n" +
                "rule R when\n" +
                "\n" +
                "    $c : ChildFactWithObject( idIsEven == false )\n" +
                "  then\n" +
                "    functions.doSomethingRisky($c);" +
                "end\n";

        KieSession ksession = getKieSession( str );
        ksession.setGlobal( "functions", new BusinessFunctions() );
        ksession.insert( new ChildFactWithObject(5, 1, new Object[0]) );
        assertThat(ksession.fireAllRules()).isEqualTo(1);
    }

    @Test
    public void testCompareDate() {
        String str =
                "import " + ChildFactWithObject.class.getCanonicalName() + ";\n" +
                "rule R when\n" +
                "\n" +
                "    $c: ChildFactWithObject( )\n" +
                "    ChildFactWithObject( date > $c.date )\n" +
                "  then\n" +
                "end\n";

        KieSession ksession = getKieSession( str );
        ksession.insert( new ChildFactWithObject(5, 1, new Object[0]) );
        ksession.insert( new ChildFactWithObject(6, 1, new Object[0]) );
        assertThat(ksession.fireAllRules()).isEqualTo(1);
    }

    @Test
    public void testCompareDateWithString() {
        String str =
                "import " + ChildFactWithObject.class.getCanonicalName() + ";\n" +
                "rule R when\n" +
                "\n" +
                "    ChildFactWithObject( date < \"10-Jul-1974\" )\n" +
                "  then\n" +
                "end\n";

        KieSession ksession = getKieSession( str );
        ksession.insert( new ChildFactWithObject(5, 1, new Object[0]) );
        assertThat(ksession.fireAllRules()).isEqualTo(1);
    }

    @Test
    public void test2UpperCaseProp() {
        String str =
                "import " + ChildFactWithObject.class.getCanonicalName() + ";\n" +
                "rule R when\n" +
                "\n" +
                        "    $c: ChildFactWithObject( )\n" +
                        "    ChildFactWithObject( VAr == $c.VAr )\n" +
                "  then\n" +
                "end\n";

        KieSession ksession = getKieSession( str );
        ksession.insert( new ChildFactWithObject(5, 1, new Object[0]) );
        assertThat(ksession.fireAllRules()).isEqualTo(1);
    }

    public static class BusinessFunctions {
        public boolean arrayContainsInstanceWithParameters(Object[] a1, Object[] a2) {
            return false;
        }

        public void doSomethingRisky(Object arg) throws Exception {

        }
    }

    public static class ListContainer {
        public List<String> getList() {
            return List.of("ciao");
        }
    }

    @Test
    public void testNameClashBetweenAttributeAndGlobal() {
        String str =
                "import " + ListContainer.class.getCanonicalName() + ";\n" +
                "global java.util.List list;\n" +
                "rule R when\n" +
                "    $l : ListContainer( list contains \"ciao\" )\n" +
                "then\n" +
                "    list.add($l);" +
                "end\n";

        KieSession ksession = getKieSession( str );

        List list = new ArrayList();
        ksession.setGlobal( "list", list );

        ksession.insert( new ListContainer() );
        ksession.fireAllRules();
        assertThat(list.size()).isEqualTo(1);
    }

    public static class Primitives {
        public char[] getCharArray() {
            return new char[0];
        }
    }

    @Test
    public void testPrimitiveArray() {
        String str =
                "import " + Primitives.class.getCanonicalName() + ";\n" +
                "global java.util.List list;\n" +
                "rule R when\n" +
                "    Primitives( $c : charArray )\n" +
                "then\n" +
                "    list.add($c);" +
                "end\n";

        KieSession ksession = getKieSession( str );

        List list = new ArrayList();
        ksession.setGlobal( "list", list );

        ksession.insert( new Primitives() );
        ksession.fireAllRules();
        assertThat(list.size()).isEqualTo(1);
    }

    @Test
    public void testUseConstructorInConstraint() {
        // DROOLS-2990
        String str =
                "rule R when\n" +
                "    $s: Short()" +
                "    $d: Double( this > new Double($s) )\n" +
                "then\n" +
                "end\n";

        KieSession ksession = getKieSession( str );

        ksession.insert( (short) 1 );
        ksession.insert( 2.0 );
        assertThat(ksession.fireAllRules()).isEqualTo(1);
    }

    @Test
    public void testManyPropFactWithNot() {
        // DROOLS-4572
        try {
            System.setProperty("drools.propertySpecific", "ALLOWED");
            String str =
                    "import " + ManyPropFact.class.getCanonicalName() + ";\n" +
                    "import " + SubFact.class.getCanonicalName() + ";\n" +
                    "rule R1\n" +
                    "    when\n" +
                    "        $fact : ManyPropFact(  id == 1 ) \n" +
                    "        $subFact : SubFact(  parentId == $fact.id) \n" +
                    "        not ( \n" +
                    "            $notFact : ManyPropFact ( id == $fact.id, indicator == true)\n" +
                    "            and\n" +
                    "            $notSubFact : SubFact ( parentId == $notFact.id, indicator == true)\n" +
                    "        )\n" +
                    "    then\n" +
                    "        $fact.setIndicator(true);\n" +
                    "        $subFact.setIndicator(true);\n" +
                    "        update($fact);\n" +
                    "        update($subFact);\n" +
                    "end";

            KieSession ksession = getKieSession(str);

            ManyPropFact fact = new ManyPropFact();
            fact.setId(1);

            SubFact subFact = new SubFact();
            subFact.setParentId(1);

            ksession.insert(fact);
            ksession.insert(subFact);

            int fired = ksession.fireAllRules(2); // avoid infinite loop

            assertThat(fired).isEqualTo(1);
        } finally {
            System.clearProperty("drools.propertySpecific");
        }
    }

    public static class CaseData {

        private final Object value;

        public CaseData( Object value ) {
            this.value = value;
        }

        public Map<String, Object> getData() {
            Map<String, Object> map = new HashMap<>();
            map.put( "test", value );
            return map;
        }

        public CaseData getMe() {
            return this;
        }
    }

    @Test
    public void testGetOnMapField() {
        // DROOLS-4999
        String str =
                "import " + CaseData.class.getCanonicalName() + ";\n" +
                "rule R1 when\n" +
                "    $d: CaseData( ((Number)data.get(\"test\")).intValue() > 3 )\n" +
                "then\n" +
                "end\n";

        KieSession ksession = getKieSession( str );

        ksession.insert( new CaseData( 5 ) );

        assertThat(ksession.fireAllRules()).isEqualTo(1);
    }

    @Test
    public void testEqualsOnMapField() {
        // DROOLS-4999
        String str =
                "import " + CaseData.class.getCanonicalName() + ";\n" +
                "rule R1 when\n" +
                "    $d: CaseData( me.data['test'] == \"OK\" )\n" +
                "then\n" +
                "end\n";

        KieSession ksession = getKieSession( str );

        ksession.insert( new CaseData( "OK" ) );

        assertThat(ksession.fireAllRules()).isEqualTo(1);
    }

    @Test
    public void testDoubleNegation() {
        // DROOLS-5545
        String str =
                "import " + Person.class.getCanonicalName() + ";\n" +
                "rule R1 when\n" +
                "    $d: Person( !(name != \"Mario\") )\n" +
                "then\n" +
                "end\n";

        KieSession ksession = getKieSession( str );
        ksession.insert( new Person( "Mario", 45 ) );
        assertThat(ksession.fireAllRules()).isEqualTo(1);
    }

    @Test
    public void testGlobalAsFunctionArgument() {
        // DROOLS-5999
        String str =
                "import java.util.*;\n"+
                "import java.time.*;\n"+
                "global List result;\n"+
                "global LocalDateTime $CURRENT_DATE\n"+
                "declare Job\n"+
                "    createdDate: LocalDateTime\n"+
                "end\n"+
                "rule \"init1\"\n" +
                "    when\n" +
                "    then\n" +
                "        Job job = new Job();\n" +
                "        job.setCreatedDate(LocalDateTime.now());\n" +
                "        insert(job);\n"+
                "end\n" +
                "rule \"Date check\"\n"+
                "    dialect \"mvel\"\n"+
                "    when\n"+
                "        $Job: Job(createdDate.compareTo($CURRENT_DATE)>0)\n"+
                "    then\n"+
                "        result.add(Integer.valueOf(42));\n"+
                "end\n";

        KieSession ksession = getKieSession( str );

        List<Object> result = new ArrayList<>();
        ksession.setGlobal("result", result);
        ksession.setGlobal("$CURRENT_DATE", LocalDateTime.of(1961, 5, 24, 9, 0));
        ksession.fireAllRules();
    }
}
