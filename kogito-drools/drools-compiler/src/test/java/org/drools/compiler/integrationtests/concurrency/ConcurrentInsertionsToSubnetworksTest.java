/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.drools.compiler.integrationtests.concurrency;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class ConcurrentInsertionsToSubnetworksTest extends AbstractConcurrentInsertionsTest {

    static Stream<Arguments> parameters() {
        return Stream.of(sharedSubnetworkAccumulateRule,
                         sharedSubnetworkExistsRule,
                         sharedSubnetworkNotRule,
                         noSharingSubnetworkAccumulateRule)
                .map(Arguments::arguments);
    }

    private final static String sharedSubnetworkAccumulateRule =
            "import " + AtomicInteger.class.getCanonicalName() + ";\n" +
                    "rule R1y when\n" +
                    "    Number() from accumulate ( AtomicInteger() and $s : String( this == \"test_1\" ) ; count($s)" +
                    " )" +
                    "    AtomicInteger() \n" +
                    "    Long()\n" +
                    "then\n" +
                    "    System.out.println(\"R1y\");" +
                    "end\n" +
                    "\n" +
                    "rule R1x when\n" +
                    "    Number() from accumulate ( AtomicInteger() and $s : String( this == \"test_1\" ) ; count($s)" +
                    " )\n" +
                    "    AtomicInteger( get() == 1 ) \n" +
                    "then\n" +
                    "    System.out.println(\"R1x\");" +
                    "end\n" +
                    "" +
                    "rule R2 when\n" +
                    "    $i : AtomicInteger( get() < 3 )\n" +
                    "then\n" +
                    "    System.out.println(\"R2\");" +
                    "    $i.incrementAndGet();" +
                    "    update($i);" +
                    "end\n";
    private final static String noSharingSubnetworkAccumulateRule =
            "import " + AtomicInteger.class.getCanonicalName() + ";\n" +
                    "rule R1y when\n" +
                    "    AtomicInteger() \n" +
                    "    Number() from accumulate ( AtomicInteger() and $s : String( this == \"test_1\" ) ; count($s)" +
                    " )" +
                    "    Long()\n" +
                    "then\n" +
                    "    System.out.println(\"R1y\");" +
                    "end\n" +
                    "\n" +
                    "rule R1x when\n" +
                    "    AtomicInteger() \n" +
                    "    Number() from accumulate ( $i : AtomicInteger( get() == 1) and String( this == \"test_2\" ) " +
                    "; count($i) )\n" +
                    "then\n" +
                    "    System.out.println(\"R1x\");" +
                    "end\n" +
                    "" +
                    "rule R2 when\n" +
                    "    $i : AtomicInteger( get() < 3 )\n" +
                    "then\n" +
                    "    System.out.println(\"R2\");" +
                    "    $i.incrementAndGet();" +
                    "    update($i);" +
                    "end\n";
    private final static String sharedSubnetworkNotRule =
            "import " + AtomicInteger.class.getCanonicalName() + ";\n" +
                    "rule R1 when\n" +
                    "    AtomicInteger() \n" +
                    "    not(AtomicInteger( get() == 1 ) and String( this == \"test_1\" )) \n" +
                    "then\n" +
                    "    System.out.println(\"R1\");" +
                    "end\n" +
                    "\n" +
                    "rule R2 when\n" +
                    "    AtomicInteger() \n" +
                    "    not(AtomicInteger( get() == 1 ) and String( this == \"test_1\" )) \n" +
                    "    String( this != \"test_2\" ) \n" +
                    "then\n" +
                    "    System.out.println(\"R2\");" +
                    "end\n";
    private final static String sharedSubnetworkExistsRule =
            "import " + AtomicInteger.class.getCanonicalName() + ";\n" +
                    "rule R1 when\n" +
                    "    AtomicInteger() \n" +
                    "    exists(AtomicInteger( get() == 1 ) and String( this == \"test_1\" )) \n" +
                    "then\n" +
                    "    System.out.println(\"R1\");" +
                    "end\n" +
                    "\n" +
                    "rule R2 when\n" +
                    "    AtomicInteger() \n" +
                    "    exists(AtomicInteger( get() == 1 ) and String( this == \"test_1\" )) \n" +
                    "    String( this != \"test_2\" ) \n" +
                    "then\n" +
                    "    System.out.println(\"R2\");" +
                    "end\n";

    @ParameterizedConcurrentInsertionsToSubnetworksTest
    public void testConcurrentInsertionsFewObjectsManyThreads(String drl) throws InterruptedException {
        testConcurrentInsertions(drl, 1, 1000, false, false);
    }

    @ParameterizedConcurrentInsertionsToSubnetworksTest
    public void testConcurrentInsertionsManyObjectsFewThreads(String drl) throws InterruptedException {
        testConcurrentInsertions(drl, 500, 4, false, false);
    }

    @ParameterizedConcurrentInsertionsToSubnetworksTest
    public void testConcurrentInsertionsManyObjectsSingleThread(String drl) throws InterruptedException {
        testConcurrentInsertions(drl, 1000, 1, false, false);
    }

    @ParameterizedConcurrentInsertionsToSubnetworksTest
    public void testConcurrentInsertionsNewSessionEachThread(String drl) throws InterruptedException {
        testConcurrentInsertions(drl, 10, 1000, true, false);
    }

    @ParameterizedConcurrentInsertionsToSubnetworksTest
    public void testConcurrentInsertionsNewSessionEachThreadUpdate(String drl) throws InterruptedException {
        testConcurrentInsertions(drl, 10, 1000, true, true);
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @ParameterizedTest
    @MethodSource("parameters")
    public @interface ParameterizedConcurrentInsertionsToSubnetworksTest {

    }
}

