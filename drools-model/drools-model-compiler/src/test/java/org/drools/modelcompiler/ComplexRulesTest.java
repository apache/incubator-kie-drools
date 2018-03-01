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
                "import " + PropertyUsageEnum.class.getCanonicalName() + ";\n" +
                "import " + ExitStrategyTypeEnum.class.getCanonicalName() + ";\n" +
                "import " + GSEPropertyTypeEnum.class.getCanonicalName() + ";\n" +
                "import " + AssetTypeEnum.class.getCanonicalName() + ";\n" +
                "import " + InvokeDDServicesRequest.class.getCanonicalName() + ";\n" +
                "import " + LoanFile.class.getCanonicalName() + ";\n" +
                "import " + TransactionDetail.class.getCanonicalName() + ";\n" +
                "import " + Collateral.class.getCanonicalName() + ";\n" +
                "import " + PolicySet.class.getCanonicalName() + ";\n" +
                "import " + PolicySetIdentifier.class.getCanonicalName() + ";\n" +
                "import " + Borrower.class.getCanonicalName() + ";\n" +
                "import " + Asset.class.getCanonicalName() + ";\n" +
                "import " + LendingProduct.class.getCanonicalName() + ";\n" +
                "import " + BusinessFunctions.class.getCanonicalName() + ";\n" +
                "global BusinessFunctions functions;\n" +
                "global java.util.List list;\n" +
                "rule \"RS6106.34.65_RF6365_341_50313085\"\n" +
                "    dialect \"java\"\n" +
                "when\n" +
                "\n" +
                "    $invokeDDServices : \n" +
                "    InvokeDDServicesRequest(  ) \n" +
                "\n" +
                "    $lendingTransaction : \n" +
                "    LoanFile(  parentId == $invokeDDServices.myId ) \n" +
                "\n" +
                "    $transactionDetail : \n" +
                "    TransactionDetail(  parentId == $lendingTransaction.myId ) \n" +
                "\n" +
                "    $collateral : \n" +
                "    Collateral(  parentId == $transactionDetail.myId\n" +
                "      , occupancy == PropertyUsageEnum.INVESTOR ) \n" +
                "\n" +
                "    $policySet : \n" +
                "    PolicySet(  parentId == $transactionDetail.myId ) \n" +
                "\n" +
                "    $policySetIdentifier : \n" +
                "    PolicySetIdentifier(  parentId == $policySet.myId\n" +
                "      , exitStrategyType == ExitStrategyTypeEnum.PORTFOLIO ) \n" +
                "\n" +
                "// Aggregate function [count] creates a separate context \n" +
                "    $countOfAll_1 : Long( $result : intValue > 0) from accumulate (\n" +
                "    $invokeDDServices_C1 : \n" +
                "    InvokeDDServicesRequest(  ) \n" +
                "\n" +
                "    and\n" +
                "    $lendingTransaction_C1 : \n" +
                "    LoanFile(  parentId == $invokeDDServices_C1.myId ) \n" +
                "\n" +
                "    and\n" +
                "    $borrower_C1 : \n" +
                "    Borrower(  parentId == $lendingTransaction_C1.myId ) \n" +
                "\n" +
                "    and\n" +
                "    $asset_C1 : \n" +
                "    Asset(  parentId == $borrower_C1.myId\n" +
                "      , isSetPropertyType == true\n" +
                "      , propertyType not in (GSEPropertyTypeEnum.COMMERCIAL_NON_RESIDENTIAL, GSEPropertyTypeEnum.LAND, GSEPropertyTypeEnum.MULTIFAMILY_MORE_THAN_FOUR_UNITS)\n" +
                "      , $asset_C1_myId : myId\n" +
                "      , assetType == AssetTypeEnum.REAL_ESTATE\n" +
                "      , subjectIndicator != true ) \n" +
                "    ;count($asset_C1_myId))\n" +
                "// end of count aggregation\n" +
                "\n" +
                "// EXISTS creates a separate context.\n" +
                "    exists (\n" +
                "    $lendingProduct_C2 : \n" +
                "    LendingProduct(  parentId == $lendingTransaction.myId  // IN operator ties this element to the main context (LendingTransaction)\n" +
                "      , exitStrategyType == ExitStrategyTypeEnum.AGENCY ) \n" +
                "\n" +
                "    )\n" +
                "// end of EXISTS\n" +
                "\n" +
                "    not ( \n" +
                "    $policySet_C1587379_AR0 : PolicySet ( myId == $policySet.myId\n" +
                "      , eval(true == functions.arrayContainsInstanceWithParameters((Object[])$policySet_C1587379_AR0.getStipulations(),\n" +
                "        new Object[]{\"getMessageId\", \"42103\"}))\n" +
                "    )\n" +
                "\n" +
                "  )\n" +
                "\n" +
                "  then\n" +
                "    list.add($result);\n" +
                "end\n";

        KieSession ksession = getKieSession( str );

        List<Integer> list = new ArrayList<>();
        ksession.setGlobal( "list", list );
        ksession.setGlobal( "functions", new BusinessFunctions() );

        ksession.insert( new InvokeDDServicesRequest() );
        ksession.insert( new LoanFile() );
        ksession.insert( new TransactionDetail() );
        ksession.insert( new Collateral() );
        ksession.insert( new PolicySet() );
        ksession.insert( new PolicySetIdentifier() );
        ksession.insert( new Borrower() );
        ksession.insert( new Asset() );
        ksession.insert( new LendingProduct() );

        assertEquals(1, ksession.fireAllRules());
        assertEquals(1, list.size());
        assertEquals(1, (int)list.get(0));
    }

    @Test
    public void testEnum() {
        String str =
                "import " + PropertyUsageEnum.class.getCanonicalName() + ";\n" +
                "import " + Collateral.class.getCanonicalName() + ";\n" +
                "rule R when\n" +
                "\n" +
                "    $collateral : \n" +
                "    Collateral(  parentId == 3, occupancy == PropertyUsageEnum.INVESTOR ) \n" +
                "  then\n" +
                "end\n";

        KieSession ksession = getKieSession( str );
        ksession.insert( new Collateral() );
        assertEquals(1, ksession.fireAllRules());
    }

    @Test
    public void testNotInEnum() {
        String str =
                "import " + GSEPropertyTypeEnum.class.getCanonicalName() + ";\n" +
                "import " + Asset.class.getCanonicalName() + ";\n" +
                "rule R when\n" +
                "\n" +
                "    $asset : \n" +
                "    Asset(  propertyType not in (GSEPropertyTypeEnum.COMMERCIAL_NON_RESIDENTIAL, GSEPropertyTypeEnum.LAND, GSEPropertyTypeEnum.MULTIFAMILY_MORE_THAN_FOUR_UNITS) ) \n" +
                "  then\n" +
                "end\n";

        KieSession ksession = getKieSession( str );
        ksession.insert( new Asset() );
        assertEquals(1, ksession.fireAllRules());
    }

    @Test
    public void testConstraintWithFunctionUsingThis() {
        String str =
                "import " + PolicySet.class.getCanonicalName() + ";\n" +
                "import " + BusinessFunctions.class.getCanonicalName() + ";\n" +
                "global BusinessFunctions functions;\n" +
                "rule R when\n" +
                "\n" +
                "    $policySet_C1587379_AR0 : PolicySet ( myId == 5\n" +
                "      , !functions.arrayContainsInstanceWithParameters((Object[])this.getStipulations(),\n" +
                "                                                       new Object[]{\"getMessageId\", \"42103\"})\n" +
                "    )\n" +
                "  then\n" +
                "end\n";

        KieSession ksession = getKieSession( str );
        ksession.setGlobal( "functions", new BusinessFunctions() );
        ksession.insert( new PolicySet() );
        assertEquals(1, ksession.fireAllRules());
    }

    @Test
    public void testCastInConstraint() {
        String str =
                "import " + PolicySet.class.getCanonicalName() + ";\n" +
                "import " + BusinessFunctions.class.getCanonicalName() + ";\n" +
                "global BusinessFunctions functions;\n" +
                "rule R when\n" +
                "\n" +
                "    PolicySet ( ((Object[])stipulations).length == 0\n )\n" +
                "  then\n" +
                "end\n";

        KieSession ksession = getKieSession( str );
        ksession.setGlobal( "functions", new BusinessFunctions() );
        ksession.insert( new PolicySet() );
        assertEquals(1, ksession.fireAllRules());
    }

    @Test
    public void testConstraintWithFunctionAndStringConcatenation() {
        String str =
                "import " + PolicySet.class.getCanonicalName() + ";\n" +
                "import " + BusinessFunctions.class.getCanonicalName() + ";\n" +
                "global BusinessFunctions functions;\n" +
                "rule R when\n" +
                "\n" +
                "    $policySet_C1587379_AR0 : PolicySet ( myId == 5\n" +
                "      , !functions.arrayContainsInstanceWithParameters((Object[])stipulations,\n" +
                "                                                       new Object[]{\"getMessageId\", \"\" + myId})\n" +
                "    )\n" +
                "  then\n" +
                "end\n";

        KieSession ksession = getKieSession( str );
        ksession.setGlobal( "functions", new BusinessFunctions() );
        ksession.insert( new PolicySet() );
        assertEquals(1, ksession.fireAllRules());
    }

    @Test
    public void testEvalWithFunction() {
        String str =
                "import " + PolicySet.class.getCanonicalName() + ";\n" +
                "import " + BusinessFunctions.class.getCanonicalName() + ";\n" +
                "global BusinessFunctions functions;\n" +
                "rule R when\n" +
                "\n" +
                "    $policySet_C1587379_AR0 : PolicySet ( myId == 5\n" +
                "      , eval(false == functions.arrayContainsInstanceWithParameters((Object[])$policySet_C1587379_AR0.getStipulations(),\n" +
                "                                                                    new Object[]{\"getMessageId\", \"42103\"}))\n" +
                "    )\n" +
                "  then\n" +
                "end\n";

        KieSession ksession = getKieSession( str );
        ksession.setGlobal( "functions", new BusinessFunctions() );
        ksession.insert( new PolicySet() );
        assertEquals(1, ksession.fireAllRules());
    }

    public enum PropertyUsageEnum { INVESTOR }

    public enum ExitStrategyTypeEnum { PORTFOLIO, AGENCY }

    public enum GSEPropertyTypeEnum { COMMERCIAL_NON_RESIDENTIAL, LAND, MULTIFAMILY_MORE_THAN_FOUR_UNITS, RESIDENTIAL }

    public enum AssetTypeEnum { REAL_ESTATE }

    public static class InvokeDDServicesRequest {
        public int getMyId() {
            return 1;
        }
    }

    public static class LoanFile {
        public int getParentId() {
            return 1;
        }

        public int getMyId() {
            return 2;
        }
    }

    public static class TransactionDetail {
        public int getParentId() {
            return 2;
        }

        public int getMyId() {
            return 3;
        }
    }

    public static class Collateral {
        public int getParentId() {
            return 3;
        }

        public int getMyId() {
            return 4;
        }

        public PropertyUsageEnum getOccupancy() {
            return PropertyUsageEnum.INVESTOR;
        }
    }

    public static class PolicySet {
        public int getParentId() {
            return 3;
        }

        public int getMyId() {
            return 5;
        }

        public Object getStipulations() {
            return new Object[0];
        }
    }

    public static class PolicySetIdentifier {
        public int getParentId() {
            return 5;
        }

        public int getMyId() {
            return 6;
        }

        public ExitStrategyTypeEnum getExitStrategyType() {
            return ExitStrategyTypeEnum.PORTFOLIO;
        }
    }

    public static class Borrower {
        public int getParentId() {
            return 2;
        }

        public int getMyId() {
            return 7;
        }
    }

    public static class Asset {
        public int getParentId() {
            return 7;
        }

        public int getMyId() {
            return 8;
        }

        public boolean getIsSetPropertyType() {
            return true;
        }

        public GSEPropertyTypeEnum getPropertyType() {
            return GSEPropertyTypeEnum.RESIDENTIAL;
        }

        public AssetTypeEnum getAssetType() {
            return AssetTypeEnum.REAL_ESTATE;
        }

        public boolean isSubjectIndicator() {
            return false;
        }
    }

    public static class LendingProduct {
        public int getParentId() {
            return 2;
        }

        public int getMyId() {
            return 9;
        }

        public ExitStrategyTypeEnum getExitStrategyType() {
            return ExitStrategyTypeEnum.AGENCY;
        }
    }

    public static class BusinessFunctions {
        public boolean arrayContainsInstanceWithParameters(Object[] a1, Object[] a2) {
            return false;
        }
    }
}
