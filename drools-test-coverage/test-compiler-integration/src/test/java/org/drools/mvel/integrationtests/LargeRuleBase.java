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
package org.drools.mvel.integrationtests;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;

import org.drools.drl.parser.DroolsParserException;
import org.drools.base.common.DroolsObjectInputStream;
import org.drools.base.common.DroolsObjectOutputStream;
import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.drools.kiesession.rulebase.KnowledgeBaseFactory;
import org.kie.api.KieBase;
import org.kie.api.io.ResourceType;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;

/**
 * This generates a large number of rules (complex ones) and then times
 * compiling, serializing etc.
 */
public class LargeRuleBase {

    private static final int RULE_COUNT = 20000;

    public static void main(String[] args) throws Exception {
        System.err.println(Runtime.getRuntime().freeMemory());

        bigBlobCompile();
        //realisticSmallBlobCompile();
        System.gc();
        Thread.sleep(5000);
        System.err.println(Runtime.getRuntime().freeMemory());

    }

    private static void bigBlobCompile() throws DroolsParserException,
            IOException, Exception {
        StringBuilder buf = new StringBuilder();
        buf.append(getHeader());

        for (int i = 0; i < 1; i++) {
            String name = "x" + i;
            int status = i;

            String r = getTemplate1(name, status);
            buf.append(r);
        }

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource(buf.toString().getBytes()), ResourceType.DRL );

        InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addPackages(kbuilder.getKnowledgePackages());

        File f = new File("foo.rulebase");
        if (f.exists())
            f.delete();

        ObjectOutput out = new DroolsObjectOutputStream(new FileOutputStream(f));
        out.writeObject(kbase);
        out.flush();
        out.close();
        ObjectInputStream in = new DroolsObjectInputStream(new FileInputStream(f));
        KieBase rb_ = (KieBase) in.readObject();
    }
    public static String getHeader() {
        return "package org.kie.test; \n " + "import org.drools.mvel.compiler.Person; \n "
                + "import org.drools.mvel.compiler.Cheese; \n "
                + "import org.drools.compiler.Cheesery; \n "
                + " import java.util.List \n "
                + " global List list \n dialect 'mvel'\n  ";
    }

    public static String getTemplate1(String name, int status) {
        return "rule 'match Person "
                + name
                + "' \n"
                + " agenda-group \'xxx\' \n"
                + " salience ($age2 - $age1) \n "
                + " dialect 'mvel' \n"
                + "	when \n "
                + " 		$person : Person(name=='"
                + name
                + "', $age1 : age ) \n "
                + "	    cheesery : Cheesery( cheeses contains $person, status == "
                + status + " ) \n "
                + " 		cheeses : List() from cheesery.getCheeses() \n "
                + "		Person( age < ( $age1 ) ) \n "
                + "		Person( $age2 : age, eval( $age1 == $age2 ) ) \n "
                + "		eval( $age1 == $age2 ) \n " + "   then \n "
                + "		list.add( $person ); \n "
                + "		$person.setStatus(\"match Person ok\"); \n " + " end \n";
    }

    private static String getTemplate2(String name, int num,  int status) {
        return "rule 'match Person "
                + num
                + "' \n"
                + " dialect 'mvel' \n"
                + "	when \n "
                + " 		$person : Person(name=='"
                + name
                + "', $age1 : age ) \n "
                + "	    cheesery : Cheesery( cheeses contains $person, status == "
                + status + " ) \n "
                + " Person(age < " + num + ") \n"
                + " then \n "
                + "		list.add( $person ); \n "
                + "		$person.setStatus(\"match Person ok\"); \n " + " end \n";
    }

}
