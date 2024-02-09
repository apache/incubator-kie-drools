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
package org.drools.quarkus.deployment;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.assertj.core.api.InstanceOfAssertFactories;
import org.drools.quarkus.util.deployment.GlobalsBuildItem;
import org.drools.quarkus.util.deployment.PatternsTypesBuildItem;
import org.drools.quarkus.examples.otn.model.ASubclassOfMeasurement;
import org.drools.quarkus.examples.otn.model.Measurement;
import org.drools.quarkus.examples.otn.model.MyImplementation;
import org.drools.quarkus.examples.otn.model.MyInterface;
import org.drools.quarkus.examples.otn.model.MyUnusedClass;
import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.DotName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.builder.BuildContext;
import io.quarkus.builder.BuildStep;
import io.quarkus.deployment.builditem.CombinedIndexBuildItem;
import io.quarkus.test.QuarkusUnitTest;

/*
 * The scope of these tests is to check the resulting BuildItems as produced by this drools-quarkus-extension.
 */
public class BuildItemsTest {
    
    static final Logger LOG = LoggerFactory.getLogger(BuildItemsTest.class);

    @RegisterExtension
    static final QuarkusUnitTest config = new QuarkusUnitTest()
            .withApplicationRoot((jar) -> jar
                    .addAsResource("rules.txt", "src/main/resources/org/drools/quarkus/examples/otn/rules.drl")
                    .addClasses(ASubclassOfMeasurement.class, Measurement.class, MyImplementation.class, MyInterface.class, MyUnusedClass.class))
            .addBuildChainCustomizer(b -> {
                b.addBuildStep(new BuildStep() {
                    @Override
                    public void execute(BuildContext context) {
                        GlobalsBuildItem gbi = context.consume(GlobalsBuildItem.class);
                        assertGlobals(gbi.getGlobals());
                        
                        CombinedIndexBuildItem indexBI = context.consume(CombinedIndexBuildItem.class);
                        PatternsTypesBuildItem otnClasesBI = context.consume(PatternsTypesBuildItem.class);
                        Set<String> allKnown = computeAllKnown(otnClasesBI, indexBI);
                        assertAllKnownOTNs(allKnown);
                    }
                })
                .consumes(GlobalsBuildItem.class)
                .consumes(PatternsTypesBuildItem.class)
                .consumes(CombinedIndexBuildItem.class)
                .produces(AdditionalBeanBuildItem.class)
                .build();
            });
    
    @Test
    public void testQuarkusUTByAddBuildChainCustomizer() {
        assertThatNoException().isThrownBy(() -> LOG.info("looking for a successfully executed QuarkusUnitTest"
                + "with custom .addBuildChainCustomizer() containing assertions"));
    }
    
    /*
     * Compute all related indexed classes, from the known OTN/Pattern classes.
     */
    private static Set<String> computeAllKnown(PatternsTypesBuildItem otnClasesBI, CombinedIndexBuildItem indexBI) {
        Set<String> allKnown = new HashSet<>();
        for (Class<?> c : otnClasesBI.getPatternsClasses().values().stream().flatMap(x -> x.stream()).collect(Collectors.toList())) {
            allKnown.add(c.getCanonicalName());
            if (c.isInterface()) {
                allKnown.addAll(indexBI.getIndex().getAllKnownImplementors(c).stream().map(ClassInfo::name).map(DotName::toString).collect(Collectors.toList()));
            } else {
                allKnown.addAll(indexBI.getIndex().getAllKnownSubclasses(c).stream().map(ClassInfo::name).map(DotName::toString).collect(Collectors.toList()));
            }
        }
        return allKnown;
    }
    
    /*
     * check the DRL globals, given the defined rules.
     */
    private static void assertGlobals(Map<String, Map<String, Type>> globals) {
        LOG.info("GlobalsBuildItem.globals: {}", globals);
        
        assertThat(globals).containsKey("org.drools.quarkus.examples.otn")
            .extractingByKey("org.drools.quarkus.examples.otn", as(InstanceOfAssertFactories.MAP))
            .containsKey("controlSet");
    }
    
    /*
     * check the known Patterns/OTNs classes, given the defined DRL rules.
     */
    private static void assertAllKnownOTNs(Set<String> allKnown) {
        LOG.info("allKnown: {}", allKnown);
        
        assertThat(allKnown)
            .as("these classes or subclasses are derived from OTNs in the rules.")
            .contains(ASubclassOfMeasurement.class.getCanonicalName(),
                Measurement.class.getCanonicalName(),
                MyImplementation.class.getCanonicalName(),
                MyInterface.class.getCanonicalName()
            );
        
        assertThat(allKnown)
            .as("this class is unused in the rules despite star-import")
            .doesNotContain(MyUnusedClass.class.getCanonicalName());
    }
}
