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

package org.drools.compiler.integrationtests;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.drools.compiler.CommonTestMethodBase;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;

public class DroolsConditionOrderingTest extends CommonTestMethodBase {

    private static final String COND_1            = "(Number() or String())";
    private static final String COND_2            = "String( this == ( $AUname ) ) from $ListNegAU";
    private static final String COND_2parenthesis = "(String( this == ( $AUname ) ) from $ListNegAU)";

    private static final String RULE =
            "dialect \"mvel\"\n" +
                    "import org.drools.compiler.integrationtests.DroolsConditionOrderingTest$ValueList;\n" +
                    "global java.util.List list;\n" +
                    "\n" +
                    "rule \"ATO_Negative_AU_Test\"\n" +
                    "        when\n" +
                    "            ValueList( listName == \"ATO_Negative_AuthUser_List_CC\" , $ListNegAU : listOfValues != null )\n" +
                    "                $AUname : String( )\n" +
                    "                 COND_1\n" +
                    "                 COND_2\n" +
                    "        then\n" +
                    "            list.add(\"rule fired\")\n" +
                    "end";


    @Test
    public void testOrder1() throws Throwable {
        assertEquals( 1, evaluateRules( RULE.replace("COND_1", COND_1).replace("COND_2", COND_2)) );
    }

    @Test
    public void testOrder1p() throws Throwable {
        assertEquals( 1, evaluateRules( RULE.replace("COND_1", COND_1).replace("COND_2", COND_2parenthesis)) );
    }

    @Test
    public void testOrder2() throws Throwable {
        assertEquals( 1, evaluateRules(RULE.replace("COND_1", COND_2).replace("COND_2", COND_1)) );
    }

    @Test
    public void testOrder2p() throws Throwable {
        assertEquals( 1, evaluateRules(RULE.replace("COND_1", COND_2parenthesis).replace("COND_2", COND_1)) );
    }

    private int evaluateRules(String rule) throws Throwable {
        System.out.println(rule);
        KieBase kbase = loadKnowledgeBaseFromString( rule);
        KieSession ksession = kbase.newKieSession();
        ValueList valueList = new ValueList();
        valueList.setListName("ATO_Negative_AuthUser_List_CC");
        List<String> listOfValues = new ArrayList<String>();
        listOfValues.add("VAL1");
        listOfValues.add("VAL2");
        valueList.setListOfValues(listOfValues);
        ksession.insert(valueList);
        ksession.insert(new String("VAL1"));
        List<Double> list = new ArrayList<Double>();
        ksession.setGlobal("list", list);
        return ksession.fireAllRules();
    }



    public class ValueList implements Serializable{
        private String listName;
        private String listVersion;
        private String listDescription;
        private String listData;
        private List<String>listOfValues;
        private Date updatedTs;
        private String updatedBy;

        public String getListName() {
            return listName;
        }

        public void setListName(String listName) {
            this.listName = listName;
        }

        public String getListVersion() {
            return listVersion;
        }

        public void setListVersion(String listVersion) {
            this.listVersion = listVersion;
        }

        public String getListDescription() {
            return listDescription;
        }

        public void setListDescription(String listDescription) {
            this.listDescription = listDescription;
        }

        public String getListData() {
            return listData;
        }

        public void setListData(String listData) {
            this.listData = listData;
        }

        // no XmlElement on this one. It is used by Execution Engine exclusively
        public List<String> getListOfValues() {
            return listOfValues;
        }

        public void setListOfValues(List<String> listOfValues) {
            this.listOfValues = listOfValues;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((updatedBy == null) ? 0 : updatedBy.hashCode());
            result = prime * result + ((updatedTs == null) ? 0 : updatedTs.hashCode());
            result = prime * result + ((listData == null) ? 0 : listData.hashCode());
            result = prime * result + ((listDescription == null) ? 0 : listDescription.hashCode());
            result = prime * result + ((listName == null) ? 0 : listName.hashCode());
            result = prime * result + ((listVersion == null) ? 0 : listVersion.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            ValueList other = (ValueList) obj;
            if (updatedBy == null) {
                if (other.updatedBy != null)
                    return false;
            } else if (!updatedBy.equals(other.updatedBy))
                return false;
            if (updatedTs == null) {
                if (other.updatedTs != null)
                    return false;
            } else if (!updatedTs.equals(other.updatedTs))
                return false;
            if (listData == null) {
                if (other.listData != null)
                    return false;
            } else if (!listData.equals(other.listData))
                return false;
            if (listDescription == null) {
                if (other.listDescription != null)
                    return false;
            } else if (!listDescription.equals(other.listDescription))
                return false;
            if (listName == null) {
                if (other.listName != null)
                    return false;
            } else if (!listName.equals(other.listName))
                return false;
            if (listVersion == null) {
                if (other.listVersion != null)
                    return false;
            } else if (!listVersion.equals(other.listVersion))
                return false;
            return true;
        }

        @Override
        public String toString() {
            return "ValueList [listName=" + listName + ", listVersion="
                    + listVersion + ", listDescription=" + listDescription
                    + ", updatedTs=" + updatedTs
                    + ", updatedBy=" + updatedBy + "]";
        }

        public Date getUpdatedTs() {
            return updatedTs;
        }

        public void setUpdatedTs(Date updatedTs) {
            this.updatedTs = updatedTs;
        }

        public String getUpdatedBy() {
            return updatedBy;
        }

        public void setUpdatedBy(String updatedBy) {
            this.updatedBy = updatedBy;
        }

    }

}