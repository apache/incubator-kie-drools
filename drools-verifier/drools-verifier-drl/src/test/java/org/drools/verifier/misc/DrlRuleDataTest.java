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
package org.drools.verifier.misc;

import java.util.List;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class DrlRuleDataTest {
    @Test
    void testHandleDrl() {
        String drl = "rule \"Something\" \n ";
        drl += "dialect \"Java\" \n ";
        drl += "	when \n ";
        drl += "		Person() \n ";
        drl += "		Cheesery() \n ";
        drl += "	then \n ";
        drl += "		insert( new Person()) \n ";
        drl += "		insert( new Car()) \n ";
        drl += "		insert( new Cheese()) \n ";
        drl += "end ";

        DrlRuleParser s = DrlRuleParser.findRulesDataFromDrl(drl).get(0);

        assertThat(s.getHeader().size()).isEqualTo(1);
        assertThat(s.getLhs().size()).isEqualTo(2);
        assertThat(s.getRhs().size()).isEqualTo(3);
        assertThat(s.getDescription()).isEqualTo("");

    }

    @Test
    void testHandleDrlNoLineBreaks() {
        String drl = "rule \"CreditScoreApproval\" \n";
        drl += "	dialect \"mvel\" \n";
        drl += "	when    then";
        drl += "		applicant.setApproved(true) \n";
        drl += "		applicant.setName( \"Toni\" ) \n";
        drl += "		applicant.setAge( 10 ) \n";
        drl += "end";
        DrlRuleParser s = DrlRuleParser.findRulesDataFromDrl(drl).get(0);

        assertThat(s).isNotNull();

        assertThat(s.getHeader().size()).isEqualTo(1);
        assertThat(s.getLhs().size()).isEqualTo(0);
        assertThat(s.getRhs().size()).isEqualTo(3);
        assertThat(s.getDescription()).isEqualTo("");

    }

    @Test
    void testHandleDrlWithComment() {
        String drl = "# Really important information about this rule \n";
        drl += "# Another line because one was not enough \n";
        drl += "#  \n";
        drl += "# @author: trikkola \n";
        drl += "rule \"First\" \n";
        drl += "	dialect \"mvel\" \n";
        drl += "	when \n ";
        drl += "		Person() \n ";
        drl += "		Cheesery() \n ";
        drl += "	then \n ";
        drl += "		applicant.setApproved(true) \n";
        drl += "		applicant.setName( \"Toni\" ) \n";
        drl += "		applicant.setAge( 10 ) \n";
        drl += "end \n";
        drl += "\n";
        drl += "# Really important information about this rule \n";
        drl += "# Another line because one was not enough \n";
        drl += "#  \n";
        drl += "# @author: trikkola \n";
        drl += "# @created: 29.12.2001 \n";
        drl += "# @edited: 5.5.2005 \n";
        drl += "rule \"Second\" \n";
        drl += "	dialect \"mvel\" \n";
        drl += "	when \n ";
        drl += "		Person() \n ";
        drl += "		Cheesery() \n ";
        drl += "	then \n ";
        drl += "		applicant.setApproved(true) \n";
        drl += "		applicant.setName( \"Toni\" ) \n";
        drl += "		applicant.setAge( 10 ) \n";
        drl += "end";
        drl += "\n";
        drl += "rule \"Third\" \n";
        drl += "	dialect \"mvel\" \n";
        drl += "	when \n ";
        drl += "		Person() \n ";
        drl += "		Cheesery() \n ";
        drl += "	then \n ";
        drl += "		applicant.setApproved(true) \n";
        drl += "		applicant.setName( \"Toni\" ) \n";
        drl += "		applicant.setAge( 10 ) \n";
        drl += "end";

        List<DrlRuleParser> list = DrlRuleParser.findRulesDataFromDrl(drl);

        assertThat(list.size()).isEqualTo(3);

        DrlRuleParser rd = list.get(0);

        assertThat(rd).isNotNull();

        assertThat(rd.getHeader().size()).isEqualTo(1);
        assertThat(rd.getLhs().size()).isEqualTo(2);
        assertThat(rd.getRhs().size()).isEqualTo(3);
        assertThat(rd.getMetadata().size()).isEqualTo(1);
        assertThat(rd.getDescription()).isNotNull();
        assertThat(rd.getDescription()).isNotEqualTo("");

        DrlRuleParser rd2 = list.get(1);

        assertThat(rd2).isNotNull();

        assertThat(rd2.getHeader().size()).isEqualTo(1);
        assertThat(rd2.getLhs().size()).isEqualTo(2);
        assertThat(rd2.getRhs().size()).isEqualTo(3);
        assertThat(rd2.getMetadata().size()).isEqualTo(3);
        assertThat(rd2.getDescription()).isNotNull();

        String description = "Really important information about this rule\n";
        description += "Another line because one was not enough\n\n";

        assertThat(rd2.getDescription()).isEqualTo(description);
        assertThat(rd2.getDescription()).isNotEqualTo("");

        DrlRuleParser rd3 = list.get(2);

        assertThat(rd3).isNotNull();

        assertThat(rd3.getHeader().size()).isEqualTo(1);
        assertThat(rd3.getLhs().size()).isEqualTo(2);
        assertThat(rd3.getRhs().size()).isEqualTo(3);
        assertThat(rd3.getDescription()).isNotNull();
        assertThat(rd3.getDescription()).isEqualTo("");
    }
}
