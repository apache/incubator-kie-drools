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
package org.kie.kogito.addon.cloudevents.quarkus.deployment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.drools.codegen.common.GeneratedFile;
import org.drools.codegen.common.GeneratedFileType;
import org.jboss.jandex.DotName;
import org.jbpm.compiler.canonical.ProcessMetaData;
import org.kie.kogito.addon.quarkus.common.reactive.messaging.http.CloudEventHttpOutgoingDecorator;
import org.kie.kogito.codegen.api.context.KogitoBuildContext;
import org.kie.kogito.codegen.process.ProcessGenerator;
import org.kie.kogito.quarkus.addons.common.deployment.AnyEngineKogitoAddOnProcessor;
import org.kie.kogito.quarkus.common.deployment.KogitoAddonsPostGeneratedSourcesBuildItem;
import org.kie.kogito.quarkus.common.deployment.KogitoBuildContextBuildItem;
import org.kie.kogito.quarkus.config.KogitoBuildTimeConfig;
import org.kie.kogito.quarkus.extensions.spi.deployment.KogitoProcessContainerGeneratorBuildItem;

import com.github.javaparser.ast.CompilationUnit;

import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.arc.processor.DotNames;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.FeatureBuildItem;

public class KogitoAddOnMessagingProcessor extends AnyEngineKogitoAddOnProcessor {

    private static final String FEATURE = "kie-addon-messaging-extension";

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    @BuildStep
    void httpMessageDecorator(BuildProducer<AdditionalBeanBuildItem> beanBuildItem, KogitoBuildTimeConfig buildTimeConfig, KogitoBuildContextBuildItem kogitoContext) {
        if (buildTimeConfig.useCloudEvents && kogitoContext.getKogitoBuildContext().hasClassAvailable("io.quarkus.reactivemessaging.http.runtime.OutgoingHttpMetadata")) {
            beanBuildItem.produce(AdditionalBeanBuildItem.builder().addBeanClass(CloudEventHttpOutgoingDecorator.class).setDefaultScope(DotNames.APPLICATION_SCOPED).build());
        }
    }

    @BuildStep
    KogitoAddonsPostGeneratedSourcesBuildItem generate(List<KogitoProcessContainerGeneratorBuildItem> processBuildItem, BuildProducer<KogitoMessagingMetadataBuildItem> metadataProducer,
            KogitoBuildContextBuildItem kogitoContext) {

        Collection<ChannelInfo> channelsInfo = ChannelMappingStrategy.getChannelMapping();
        Map<DotName, EventGenerator> generators = new HashMap<>();
        Map<String, EventGenerator> channels = new HashMap<>();

        processBuildItem.stream().flatMap(it -> it.getProcessContainerGenerators().stream())
                .forEach(containerGenerator -> containerGenerator.getProcesses().forEach(process -> collect(process, channelsInfo, generators, channels, kogitoContext.getKogitoBuildContext())));

        Collection<GeneratedFile> generatedFiles = new ArrayList<>();
        metadataProducer.produce(new KogitoMessagingMetadataBuildItem(generators));

        for (EventGenerator generator : getGenerators(generators, channelsInfo, kogitoContext.getKogitoBuildContext())) {
            generatedFiles.add(new GeneratedFile(GeneratedFileType.SOURCE, generator.getPath(), generator.getCode()));
            Optional<String> annotationName = generator.getAnnotationName();
            if (annotationName.isPresent()) {
                AnnotationGenerator annotationGen = new AnnotationGenerator(kogitoContext.getKogitoBuildContext(), annotationName.get());
                generatedFiles.add(new GeneratedFile(GeneratedFileType.SOURCE, annotationGen.getPath(), annotationGen.getCode()));
            }
        }

        return new KogitoAddonsPostGeneratedSourcesBuildItem(generatedFiles);
    }

    private Set<EventGenerator> getGenerators(Map<DotName, EventGenerator> generators,
            Collection<ChannelInfo> channelsInfo, KogitoBuildContext context) {

        Set<EventGenerator> result = new HashSet<>();
        boolean inputDefault = false;
        boolean outputDefault = false;
        for (EventGenerator generator : generators.values()) {
            result.add(generator);
            if (generator.getAnnotationName().isEmpty()) {
                if (generator.getChannelInfo().isInput()) {
                    inputDefault = true;
                } else {
                    outputDefault = true;
                }
            }
        }

        if (!inputDefault) {
            channelsInfo.stream().filter(ChannelInfo::isInputDefault).findFirst().ifPresent(c -> result.add(buildEventGenerator(context, c)));
        }
        if (!outputDefault) {
            channelsInfo.stream().filter(ChannelInfo::isOutputDefault).findFirst().ifPresent(c -> result.add(buildEventGenerator(context, c)));
        }
        return result;

    }

    private void collect(ProcessGenerator process, Collection<ChannelInfo> channelsInfo, Map<DotName, EventGenerator> eventGenerators, Map<String, EventGenerator> channels,
            KogitoBuildContext context) {
        ProcessMetaData processMetadata = process.getProcessExecutable().generate();
        for (ChannelInfo channelInfo : channelsInfo) {
            if (!channelInfo.isDefault()) {
                collect(channelInfo.isInput() ? processMetadata.getConsumers() : processMetadata.getProducers(), channelInfo, eventGenerators, channels, context);
            }
        }
    }

    private void collect(Map<String, Collection<CompilationUnit>> map, ChannelInfo channelInfo, Map<DotName, EventGenerator> eventGenerators, Map<String, EventGenerator> channels,
            KogitoBuildContext context) {
        for (String trigger : channelInfo.getTriggers()) {
            Collection<CompilationUnit> cus = map.get(trigger);
            if (cus != null) {
                cus.forEach(cu -> eventGenerators.computeIfAbsent(DotNamesHelper.createDotName(cu),
                        k -> channels.computeIfAbsent(channelInfo.getChannelName(), c -> buildEventGenerator(context, channelInfo))));
            }
        }
    }

    private EventGenerator buildEventGenerator(KogitoBuildContext context, ChannelInfo channelInfo) {
        return channelInfo.isInput() ? new EventReceiverGenerator(context, channelInfo) : new EventEmitterGenerator(context, channelInfo);
    }
}
