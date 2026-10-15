/*
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
package org.drools.mvel.compiler.kie.builder.impl;

import java.io.StringReader;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.drools.compiler.kie.builder.impl.KieBuilderImpl;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;

class KieBuilderPackageFolderTest {

    // https://github.com/apache/incubator-kie/issues/6515
    @ParameterizedTest(name = "sourcePath={0}, packageFilter={1}")
    @CsvSource({
            "org/example/rules/rules.drl, true",
            "/org/example/rules/rules.drl, true",
            "org/example/rules/rules.drl, false",
            "/org/example/rules/rules.drl, false"
    })
    void matchingPackageAndFolderShouldNotWarn(String sourcePath, boolean packageFilter) {
        KieServices ks = KieServices.Factory.get();
        KieModuleModel module = ks.newKieModuleModel();
        KieBaseModel base = module.newKieBaseModel("rules").setDefault(true);
        if (packageFilter) {
            base.addPackage("org.example.rules");
        }
        base.newKieSessionModel("session").setDefault(true);

        KieFileSystem kfs = ks.newKieFileSystem();
        kfs.writeKModuleXML(module.toXML());
        kfs.write(ks.getResources().newReaderResource(new StringReader(
                "package org.example.rules;\n" +
                "rule R when then end\n"))
                .setResourceType(ResourceType.DRL).setSourcePath(sourcePath));

        Logger logger = (Logger) LoggerFactory.getLogger(KieBuilderImpl.class);
        Level previousLevel = logger.getLevel();
        ListAppender<ILoggingEvent> appender = new ListAppender<>();
        appender.start();
        logger.addAppender(appender);
        logger.setLevel(Level.WARN);
        try {
            KieBuilder builder = ks.newKieBuilder(kfs).buildAll();
            assertThat(builder.getResults().getMessages(Message.Level.ERROR)).isEmpty();
            KieSession session = ks.newKieContainer(builder.getKieModule().getReleaseId()).newKieSession();
            try {
                assertThat(session.fireAllRules()).isEqualTo(1);
            } finally {
                session.dispose();
            }
            // This warning is logged directly, so builder.getResults() cannot detect it.
            assertThat(appender.list)
                    .filteredOn(event -> event.getLevel() == Level.WARN)
                    .extracting(ILoggingEvent::getFormattedMessage)
                    .noneMatch(message -> message.contains("correspondance between package and folder names"));
        } finally {
            logger.detachAppender(appender);
            appender.stop();
            logger.setLevel(previousLevel);
        }
    }
}
