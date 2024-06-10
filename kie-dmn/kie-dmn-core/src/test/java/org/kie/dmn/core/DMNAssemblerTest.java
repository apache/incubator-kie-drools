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
package org.kie.dmn.core;

import java.time.OffsetTime;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.KieServices;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message.Level;
import org.kie.api.builder.Results;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.runtime.KieContainer;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.core.util.DMNRuntimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;

public class DMNAssemblerTest extends BaseInterpretedVsCompiledTest {
    public static final Logger LOG = LoggerFactory.getLogger(DMNAssemblerTest.class);

    @ParameterizedTest
    @MethodSource("params")
    void duplicateModel(boolean useExecModelCompiler) {
        init(useExecModelCompiler);
        final KieServices ks = KieServices.Factory.get();
        final KieFileSystem kfs = ks.newKieFileSystem();
        
        kfs.write(ks.getResources().newClassPathResource("0001-input-data-string.dmn", this.getClass()));
        kfs.write(ks.getResources().newClassPathResource("duplicate.0001-input-data-string.dmn", this.getClass()));
        
        final Results results = ks.newKieBuilder(kfs ).buildAll().getResults();
        
        LOG.info("buildAll() completed.");
        results.getMessages(Level.ERROR).forEach( e -> LOG.error("{}", e));

        assertThat(results.getMessages(Level.ERROR)).hasSizeGreaterThan(0);
    }

    @ParameterizedTest
    @MethodSource("params")
    void extendedMode(boolean useExecModelCompiler) {
        init(useExecModelCompiler);
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("strictMode.dmn", this.getClass() );
        final DMNModel model = runtime.getModel("http://www.trisotech.com/dmn/definitions/_ecf4ea54-2abc-4e2f-a101-4fe14e356a46", "strictMode" );
        final DMNContext ctx = runtime.newContext();
        ctx.set( "timestring", "2016-12-20T14:30:22z" );
        final DMNResult result = runtime.evaluateAll(model, ctx);
        assertThat(result.getDecisionResultByName("time").getResult()).isEqualTo(DateTimeFormatter.ISO_TIME.parse("14:30:22z", OffsetTime::from));
    }

    @ParameterizedTest
    @MethodSource("params")
    void strictMode(boolean useExecModelCompiler) {
        init(useExecModelCompiler);
        System.setProperty("org.kie.dmn.strictConformance", "true");
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("strictMode.dmn", this.getClass() );
        final DMNModel model = runtime.getModel("http://www.trisotech.com/dmn/definitions/_ecf4ea54-2abc-4e2f-a101-4fe14e356a46", "strictMode" );
        final DMNContext ctx = runtime.newContext();
        ctx.set( "timestring", "2016-12-20T14:30:22z" );
        final DMNResult result = runtime.evaluateAll(model, ctx);
        assertThat(result.getDecisionResultByName("time").getResult()).isNull();
    }

    @ParameterizedTest
    @MethodSource("params")
    void strictModeProp(boolean useExecModelCompiler) {
        init(useExecModelCompiler);
        final KieServices services = KieServices.Factory.get();
        final KieFileSystem fileSystem = services.newKieFileSystem();
        final KieModuleModel moduleModel = services.newKieModuleModel();
        moduleModel.setConfigurationProperty("org.kie.dmn.strictConformance", "true");
        fileSystem.writeKModuleXML(moduleModel.toXML());
        fileSystem.write(services.getResources().newClassPathResource("strictMode.dmn", this.getClass()));
        services.newKieBuilder(fileSystem).buildAll();
        final KieContainer container = services.newKieContainer(services.getRepository().getDefaultReleaseId());
        final DMNRuntime runtime = container.newKieSession().getKieRuntime(DMNRuntime.class);
        final DMNModel model = runtime.getModel("http://www.trisotech.com/dmn/definitions/_ecf4ea54-2abc-4e2f-a101-4fe14e356a46", "strictMode" );
        final DMNContext ctx = runtime.newContext();
        ctx.set( "timestring", "2016-12-20T14:30:22z" );
        final DMNResult result = runtime.evaluateAll(model, ctx);
        assertThat(result.getDecisionResultByName("time").getResult()).isNull();
    }

    @AfterEach
    void clearSystemProperty() {
        System.clearProperty("org.kie.dmn.strictConformance");
    }
}
