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

import org.junit.Assert;
import org.junit.Test;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.internal.utils.KieHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;

// DROOLS-1010
public class AlphaNodeSharingWithDiffPackageNameTest {

    public static class TypeA {
        private int parentId = 2;
        private int id = 3;
        public int getParentId() { return parentId; }
        public int getId() { return id; }
        private String alphaNode;
        private HashSet<String> firings = new HashSet<String>();
        public HashSet<String> getFirings(){if (firings == null) firings = new HashSet<String>(); return firings;}
        public void setFirings(HashSet<String> x){firings = x;}

        private String data = "AlphaNodeHashingThreshold Data";
        public String getData() { return data; }

        public String getAlphaNode() {
            return alphaNode;
        }

        public void setAlphaNode(String alphaNode) {
            this.alphaNode = alphaNode;
        }
    }

    public static class TypeB {
        private int parentId = 1;
        private int id = 2;
        public int getParentId() { return parentId; }
        public int getId() { return id; }
    }

    public static class TypeC {
        private int parentId = 0;
        private int id = 1;
        public int getParentId() { return parentId; }
        public int getId() { return id; }
    }

    public static class TypeD { }

    public static class TypeE { }

    static String rule1 ="package com.test.rule1;\r\n" +
                         "\r\n" +
                         "import "+ TypeA.class.getCanonicalName()	+";\r\n" +
                         "import "+ TypeB.class.getCanonicalName()	+";\r\n" +
                         "import "+ TypeC.class.getCanonicalName()	+";\r\n" +
                         "			\r\n" +
                         "rule R1\r\n" +
                         "when\r\n" +
                         "	$c : TypeC()\r\n" +
                         "	$b : TypeB(parentId == $c.Id)\r\n" +
                         "	$a : TypeA( parentId == $b.Id, firings not contains \"R1 Fired\")\r\n" +
                         "then\r\n" +
                         "	$a.setAlphaNode(\"value contains TypeD TypeE data type\");\r\n" +
                         "	$a.getFirings().add(\"R1 Fired\");\r\n" +
                         "	update($a);\r\n" +
                         "end";

    static String rule2 ="package com.test.rule2;\r\n" +
                         "\r\n" +
                         "import "+ TypeA.class.getCanonicalName()	+";\r\n" +
                         "import "+ TypeB.class.getCanonicalName()	+";\r\n" +
                         "import "+ TypeC.class.getCanonicalName()	+";\r\n" +
                         "\r\n" +
                         "rule R2 \r\n" +
                         "when\r\n" +
                         "	$c : TypeC()\r\n" +
                         "	$b : TypeB(parentId == $c.Id)\r\n" +
                         "	$a : TypeA(parentId == $b.Id, \r\n" +
                         "				alphaNode==\"value contains TypeD TypeE data type\", \r\n" +
                         "				firings not contains \"R2 Fired\")\r\n" +
                         "then\r\n" +
                         "		$a.getFirings().add(\"R2 Fired\");\r\n" +
                         "		update($a);\r\n" +
                         "end";

    static String rule3 ="package com.test.rule3;\r\n" +
                         "\r\n" +
                         "import "+ TypeA.class.getCanonicalName()	+";\r\n" +
                         "import "+ TypeB.class.getCanonicalName()	+";\r\n" +
                         "import "+ TypeC.class.getCanonicalName()	+";\r\n" +
                         "import "+ TypeD.class.getCanonicalName()	+";\r\n" +
                         "\r\n" +
                         "rule R3 \r\n" +
                         "when\r\n" +
                         "	$d : TypeD()\r\n" +
                         "	$c : TypeC()\r\n" +
                         "	$b : TypeB(parentId == $c.Id)\r\n" +
                         "	$a : TypeA( parentId == $b.Id,\r\n" +
                         "			    alphaNode==\"value contains TypeD TypeE data type\", \r\n" +
                         "				firings not contains \"R3 Fired\")\r\n" +
                         "then\r\n" +
                         "	$a.getFirings().add(\"R3 Fired\");\r\n" +
                         "	update($a);\r\n" +
                         "end;";

    static String rule4 ="package com.test.rule4;\r\n" +
                         "\r\n" +
                         "import "+ TypeA.class.getCanonicalName()	+";\r\n" +
                         "import "+ TypeB.class.getCanonicalName()	+";\r\n" +
                         "import "+ TypeC.class.getCanonicalName()	+";\r\n" +
                         "import "+ TypeE.class.getCanonicalName()	+";\r\n" +
                         "\r\n" +
                         "rule R4 \r\n" +
                         "when\r\n" +
                         "	$e : TypeE()\r\n" +
                         "	$c : TypeC()\r\n" +
                         "	$b : TypeB(parentId == $c.Id)\r\n" +
                         "	$a : TypeA( parentId == $b.Id,\r\n" +
                         "			    alphaNode==\"value contains TypeD TypeE data type\", \r\n" +
                         "				firings not contains \"R4 Fired\")\r\n" +
                         "then\r\n" +
                         "	$a.getFirings().add(\"R4 Fired\");\r\n" +
                         "	update($a);\r\n" +
                         "end;";


    @Test
    public void testAlphaNode() {
        Logger logger = LoggerFactory.getLogger(AlphaNodeSharingWithDiffPackageNameTest.class );
        KieSession ksession = new KieHelper().addContent( rule1, ResourceType.DRL )
                                             .addContent( rule2, ResourceType.DRL )
                                             .addContent( rule3, ResourceType.DRL )
                                             .addContent( rule4, ResourceType.DRL )
                                             .build()
                                             .newKieSession();

        TypeC c= new TypeC();
        TypeB b= new TypeB();
        TypeA a= new TypeA();
        TypeD d= new TypeD();
        TypeE e= new TypeE();

        ksession.insert(a);
        ksession.insert(b);
        ksession.insert(c);
        ksession.insert(d);
        ksession.insert(e);

        ksession.fireAllRules();

        Assert.assertEquals(true, a.getFirings().contains("R1 Fired"));
        Assert.assertEquals(true, a.getFirings().contains("R2 Fired"));
        Assert.assertEquals(true, a.getFirings().contains("R3 Fired"));
        Assert.assertEquals(true, a.getFirings().contains("R4 Fired"));
    }
}