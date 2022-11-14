/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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
package org.jbpm.process.builder;

import java.util.Collection;

import org.jbpm.compiler.xml.compiler.SemanticKnowledgeBuilderConfigurationImpl;
import org.jbpm.test.util.AbstractBaseTest;
import org.junit.jupiter.api.Test;
import org.kie.api.definition.KiePackage;
import org.kie.api.definition.process.Process;
import org.kie.api.io.ResourceType;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;

import static org.assertj.core.api.Assertions.assertThat;

public class KnowledgeBuilderTest extends AbstractBaseTest {

    @Test
    public void testKnowledgeProviderWithProcesses() {
        KnowledgeBuilder builder = KnowledgeBuilderFactory.newKnowledgeBuilder(new SemanticKnowledgeBuilderConfigurationImpl());

        String str = "";
        str += "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
        str += "<process xmlns=\"http://drools.org/drools-5.0/process\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\" xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\" ";
        str += "         type=\"RuleFlow\" name=\"flow1\" id=\"0\" package-name=\"org.test1\" >";
        str += "  <header/>\n";
        str += "  <nodes><start id=\"1\" name=\"Start\" /><end id=\"2\" name=\"End\" /></nodes>\n";
        str += "  <connections><connection from=\"1\" to=\"2\"/></connections>";
        str += "</process>";
        builder.add(ResourceFactory.newByteArrayResource(str.getBytes()), ResourceType.DRF);

        str = "";
        str += "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
        str += "<process xmlns=\"http://drools.org/drools-5.0/process\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\" xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\" ";
        str += "         type=\"RuleFlow\" name=\"flow2\" id=\"0\" package-name=\"org.test2\" >";
        str += "  <header/>\n";
        str += "  <nodes><start id=\"1\" name=\"Start\" /><end id=\"2\" name=\"End\" /></nodes>\n";
        str += "  <connections><connection from=\"1\" to=\"2\"/></connections>";
        str += "</process>";
        builder.add(ResourceFactory.newByteArrayResource(str.getBytes()), ResourceType.DRF);

        Collection<KiePackage> pkgs = builder.getKnowledgePackages();
        assertThat(pkgs).isNotNull().hasSize(2);

        KiePackage test1 = getKnowledgePackage(pkgs, "org.test1");
        Collection<Process> processes = test1.getProcesses();
        assertThat(processes).hasSize(1);
        Process process = getProcess(processes, "flow1");
        assertThat(process.getName()).isEqualTo("flow1");

        KiePackage test2 = getKnowledgePackage(pkgs, "org.test2");
        processes = test2.getProcesses();
        assertThat(processes).hasSize(1);
        process = getProcess(processes, "flow2");
        assertThat(process.getName()).isEqualTo("flow2");

    }

    public Process getProcess(Collection<Process> processes, String name) {
        for (Process process : processes) {
            if (process.getName().equals(name)) {
                return process;
            }
        }
        return null;
    }

    public KiePackage getKnowledgePackage(Collection<KiePackage> pkgs, String name) {
        for (KiePackage pkg : pkgs) {
            if (pkg.getName().equals(name)) {
                return pkg;
            }
        }
        return null;
    }

}
