/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
package org.drools.mvel.compiler.runtime.pipeline.impl;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.sun.tools.xjc.Options;
import org.drools.core.builder.conf.impl.JaxbConfigurationImpl;
import org.drools.core.common.DefaultFactHandle;
import org.drools.core.io.impl.BaseResource;
import org.drools.core.io.impl.InputStreamResource;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.KieUtil;
import org.drools.testcoverage.common.util.TestConstants;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

@RunWith(Parameterized.class)
public class DroolsJaxbHelperTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public DroolsJaxbHelperTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
     // TODO: EM failed with some tests. File JIRAs
        return TestParametersUtil.getKieBaseCloudConfigurations(false);
    }

    private static final String simpleXsdRelativePath = "simple.xsd";

    @Test
    public void testXsdModelInRule() {
        // DRL rule that references object created in XSD
        final String s1 = "package test; " +
                          "global java.util.List list; " +

                          "rule Init when " +
                          "then " +
                          "  insert( new Sub() ); " +
                          "  insert( new Message() ); " +
                          "  insert( new Test() ); " +
                          "  insert( new Left() ); " +
                          "end\n" +

                          "rule CheckSub when " +
                          " $s : Sub() " +
                          "then " +
                          "  list.add( \"Sub\" );  " +
                          "end\n" +

                          "rule CheckMsg when " +
                          " $s : Message() " +
                          "then " +
                          "  list.add( \"Message\" );  " +
                          "end\n ";

        List<Resource> resources = KieUtil.getResourcesFromDrls(s1);

        // XSD that defines "Sub" class
        InputStream simpleXsdStream = getClass().getResourceAsStream(simpleXsdRelativePath);
        assertThat(simpleXsdStream).as("Could not find resource: " + simpleXsdRelativePath).isNotNull();
        BaseResource xsdResource = new InputStreamResource(simpleXsdStream);
        xsdResource.setResourceType(ResourceType.XSD);
        xsdResource.setSourcePath(TestConstants.TEST_RESOURCES_FOLDER + simpleXsdRelativePath);

        Options xjcOptions = new Options();
        xsdResource.setConfiguration(new JaxbConfigurationImpl(xjcOptions, "test-system-id"));

        resources.add(xsdResource);

        KieBase kbase = KieBaseUtil.getKieBaseFromResources(kieBaseTestConfiguration, resources.toArray(new Resource[]{}));
        KieSession ks = kbase.newKieSession();

        List list = new ArrayList();
        ks.setGlobal( "list", list );
        ks.fireAllRules();

        // Verify results
        assertThat(list.size()).isEqualTo(2);
        assertThat(list.containsAll(asList("Sub", "Message"))).isTrue();

        Collection<FactHandle> fhs = ks.getFactHandles();

        Iterator<FactHandle> iter = fhs.iterator();
        DefaultFactHandle subFh = null, msgFh = null, leftFh = null, testFh = null;
        while( iter.hasNext() ) {
            DefaultFactHandle dfh = (DefaultFactHandle) iter.next();
            if( dfh.getObjectClassName().equals("test.Sub") ) {
                subFh = dfh;
            } else if( dfh.getObjectClassName().equals("test.Message") ) {
                msgFh = dfh;
            } else if( dfh.getObjectClassName().equals("test.Left") ) {
                leftFh = dfh;
            } else if( dfh.getObjectClassName().equals("test.Test") ) {
                testFh = dfh;
            } else {
                fail( "Unexpected FH class: " + dfh.getObjectClassName() );
            }
        }
        assertThat(subFh).as("No FactHandle for Sub found!").isNotNull();
        assertThat(msgFh).as("No FactHandle for Message found!").isNotNull();

        Object xsdObj = subFh.getObject();

        Class xsdClass = xsdObj.getClass();
        try {
            Method m2 = xsdClass.getMethod( "getFld" );
            assertThat(m2).isNotNull();
            assertThat(m2.getReturnType()).isEqualTo(String.class);

            assertThat(xsdClass.getFields().length).isEqualTo(0);
            java.lang.reflect.Field[] declaredFields = xsdClass.getDeclaredFields();
            assertThat(declaredFields.length).isEqualTo(1);
            assertThat(declaredFields[0].getName()).isEqualTo("fld");
        } catch ( Exception e ) {
            e.printStackTrace();
            fail( e.getMessage() );
        }

        // "Message" has attribute 'mixed="true"' which means only one field "content"
        xsdObj = msgFh.getObject();

        xsdClass = xsdObj.getClass();
        try {
            Method m2 = xsdClass.getMethod( "getContent" );
            assertThat(m2).isNotNull();
            assertThat(m2.getReturnType()).isEqualTo(List.class);

            assertThat(xsdClass.getFields().length).isEqualTo(0);
            java.lang.reflect.Field[] declaredFields = xsdClass.getDeclaredFields();
            assertThat(declaredFields.length).isEqualTo(1);
            assertThat(declaredFields[0].getName()).isEqualTo("content");
        } catch ( Exception e ) {
            e.printStackTrace();
            fail( e.getMessage() );
        }
    }

}
