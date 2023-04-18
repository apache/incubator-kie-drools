/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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
package org.drools.quarkus.testext.test;

import static org.assertj.core.api.Assertions.assertThat;

import javax.inject.Inject;

import org.drools.drl.quarkus.testext.deployment.OtnClassesSingleton;
import org.drools.quarkus.examples.otn.model.ASubclassOfMeasurement;
import org.drools.quarkus.examples.otn.model.Measurement;
import org.drools.quarkus.examples.otn.model.MyImplementation;
import org.drools.quarkus.examples.otn.model.MyInterface;
import org.drools.quarkus.examples.otn.model.MyUnusedClass;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.quarkus.test.QuarkusUnitTest;

public class ExtensionTest {

    @RegisterExtension                                                                  
    final static QuarkusUnitTest test = new QuarkusUnitTest()
            .setArchiveProducer(() ->
                    ShrinkWrap.create(JavaArchive.class)
                            .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml")
                            .addClasses(ASubclassOfMeasurement.class,
                                        Measurement.class,
                                        MyImplementation.class,
                                        MyInterface.class,
                                        MyUnusedClass.class)
                            .addAsResource("rules.txt", "src/main/resources/org/drools/quarkus/examples/otn/rules.drl")
            );
    
    @Inject
    OtnClassesSingleton myBean;
    
    @Test
    public void test() {
        assertThat(myBean.getAllKnown()).contains(ASubclassOfMeasurement.class.getCanonicalName(),
                Measurement.class.getCanonicalName(),
                MyImplementation.class.getCanonicalName(),
                MyInterface.class.getCanonicalName())
                .as("these classes or subclasses are derived from OTNs in the rules.");
        
        assertThat(myBean.getAllKnown()).doesNotContain(MyUnusedClass.class.getCanonicalName())
                .as("this class is unused in the rules despite star-import");
    }
}
