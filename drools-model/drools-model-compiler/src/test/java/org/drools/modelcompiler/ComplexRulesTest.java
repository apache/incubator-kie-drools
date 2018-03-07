/*
 * Copyright 2005 JBoss Inc
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

package org.drools.modelcompiler;

import java.util.ArrayList;
import java.util.List;

import org.drools.modelcompiler.domain.ChildFactComplex;
import org.drools.modelcompiler.domain.ChildFactWithEnum1;
import org.drools.modelcompiler.domain.ChildFactWithEnum2;
import org.drools.modelcompiler.domain.ChildFactWithEnum3;
import org.drools.modelcompiler.domain.ChildFactWithId1;
import org.drools.modelcompiler.domain.ChildFactWithId2;
import org.drools.modelcompiler.domain.ChildFactWithId3;
import org.drools.modelcompiler.domain.ChildFactWithObject;
import org.drools.modelcompiler.domain.EnumFact1;
import org.drools.modelcompiler.domain.EnumFact2;
import org.drools.modelcompiler.domain.RootFact;
import org.junit.Test;
import org.kie.api.runtime.KieSession;

import static org.junit.Assert.assertEquals;

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
                "    $rootFact : RootFact(  ) \n" + // invokeDDServices
                "    $childFact1 : ChildFactWithId1(  parentId == $rootFact.id ) \n" + // lendingTransaction
                "    $childFact2 : ChildFactWithId2(  parentId == $childFact1.id ) \n" + // transactionDetail
                "    $childFactWithEnum1 : ChildFactWithEnum1(  parentId == $childFact2.id, enumValue == EnumFact1.FIRST ) \n" + // collateral
                "    $childFactWithObject : ChildFactWithObject(  parentId == $childFact2.id ) \n" + // policySet
                "    $childFactWithEnum2 : ChildFactWithEnum2(  parentId == $childFactWithObject.id, enumValue == EnumFact2.SECOND ) \n" + // policySetIdentifier
                "    $countOf : Long( $result : intValue > 0) from accumulate (\n" +
                "        $rootFact_acc : RootFact(  ) \n" +
                "        and $childFact1_acc : ChildFactWithId1(  parentId == $rootFact_acc.id ) \n" +
                "        and $childFact3_acc : ChildFactWithId3(  parentId == $childFact1_acc.id ) \n" + // borrower
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

        KieSession ksession = getKieSession( str );

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

        assertEquals(1, ksession.fireAllRules());
        assertEquals(1, list.size());
        assertEquals(1, (int)list.get(0));
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
        assertEquals(1, ksession.fireAllRules());
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
        assertEquals(1, ksession.fireAllRules());
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
        assertEquals(1, ksession.fireAllRules());
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
        assertEquals(1, ksession.fireAllRules());
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
        assertEquals(1, ksession.fireAllRules());
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
        assertEquals(1, ksession.fireAllRules());
    }

    public static class BusinessFunctions {
        public boolean arrayContainsInstanceWithParameters(Object[] a1, Object[] a2) {
            return false;
        }
    }
}
