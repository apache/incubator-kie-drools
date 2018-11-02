/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.kie.test.util;

import static org.junit.Assert.fail;

import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import org.dashbuilder.DataSetCore;
import org.jbpm.kie.services.impl.FormManagerService;
import org.jbpm.kie.services.impl.utils.DefaultKieServiceConfigurator;
import org.jbpm.services.api.model.DeploymentUnit;
import org.jbpm.test.services.AbstractKieServicesTest;
import org.kie.api.KieServices;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.builder.model.KieSessionModel;
import org.kie.api.conf.EqualityBehaviorOption;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.kie.internal.io.ResourceFactory;

public abstract class AbstractKieServicesBaseTest extends AbstractKieServicesTest {

    protected static final String ARTIFACT_ID = "test-module";
    protected static final String GROUP_ID = "org.jbpm.test";
    protected static final String VERSION = "1.0.0";

    protected FormManagerService formManagerService;

    protected void close() {
        DataSetCore.set(null);
        super.close();
    }

    @Override
    protected DeploymentUnit prepareDeploymentUnit() throws Exception {
        return null;
    }

    @Override
    protected List<String> getProcessDefinitionFiles() {
        return null;
    }

    @Override
    protected void configureServices() {
        super.configureServices();

        this.formManagerService = ((DefaultKieServiceConfigurator) serviceConfigurator).getFormManagerService();
        this.emf = ((DefaultKieServiceConfigurator) serviceConfigurator).getEmf();
    }

    protected static void waitForTheOtherThreads(CyclicBarrier barrier) {
        try {
            barrier.await();
        } catch (InterruptedException e) {
            fail("Thread 1 was interrupted while waiting for the other threads!");
        } catch (BrokenBarrierException e) {
            fail("Thread 1's barrier was broken while waiting for the other threads!");
        }
    }

    protected KieFileSystem createKieFileSystemWithKProject(KieServices ks) {
        KieModuleModel kproj = ks.newKieModuleModel();

        KieBaseModel kieBaseModel1 = kproj.newKieBaseModel("KBase-test").setDefault(true).addPackage("*")
                .setEqualsBehavior( EqualityBehaviorOption.EQUALITY )
                .setEventProcessingMode( EventProcessingOption.STREAM );


        kieBaseModel1.newKieSessionModel("ksession-test").setDefault(true)
                .setType(KieSessionModel.KieSessionType.STATEFUL)
                .setClockType( ClockTypeOption.get("realtime") )
                .newWorkItemHandlerModel("Log", "new org.jbpm.process.instance.impl.demo.SystemOutWorkItemHandler()");

        kieBaseModel1.newKieSessionModel("ksession-test-2").setDefault(false)
                .setType(KieSessionModel.KieSessionType.STATEFUL)
                .setClockType( ClockTypeOption.get("realtime") )
                .newWorkItemHandlerModel("Log", "new org.jbpm.kie.services.test.objects.KieConteinerSystemOutWorkItemHandler(kieContainer)");

        kieBaseModel1.newKieSessionModel("ksession-test2").setDefault(false)
                .setType(KieSessionModel.KieSessionType.STATEFUL)
                .setClockType( ClockTypeOption.get("realtime") );

        KieFileSystem kfs = ks.newKieFileSystem();
        kfs.writeKModuleXML(kproj.toXML());

        kfs.write("src/main/resources/forms/DefaultProcess.ftl", ResourceFactory.newClassPathResource("repo/globals/forms/DefaultProcess.ftl"));
        kfs.write("src/main/resources/forms/DefaultProcess.form", ResourceFactory.newClassPathResource("repo/globals/forms/DefaultProcess.form"));
        kfs.write("src/main/resources/forms/DefaultProcess.frm", ResourceFactory.newClassPathResource("repo/globals/forms/DefaultProcess.frm"));

        return kfs;
    }

}
