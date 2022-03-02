/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.addon.cloudevents.quarkus.deployment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.inject.Inject;

import org.jboss.jandex.DotName;
import org.jbpm.compiler.canonical.ProcessMetaData;
import org.kie.kogito.codegen.api.GeneratedFile;
import org.kie.kogito.codegen.api.GeneratedFileType;
import org.kie.kogito.codegen.api.context.KogitoBuildContext;
import org.kie.kogito.codegen.process.ProcessGenerator;
import org.kie.kogito.quarkus.addons.common.deployment.AnyEngineKogitoAddOnProcessor;
import org.kie.kogito.quarkus.common.deployment.KogitoAddonsGeneratedSourcesBuildItem;
import org.kie.kogito.quarkus.common.deployment.KogitoBuildContextBuildItem;
import org.kie.kogito.quarkus.extensions.spi.deployment.KogitoProcessContainerGeneratorBuildItem;

import com.github.javaparser.ast.CompilationUnit;

import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.pkg.builditem.CurateOutcomeBuildItem;

public class KogitoAddOnMessagingProcessor extends AnyEngineKogitoAddOnProcessor {

    private static final String FEATURE = "kogito-addon-messaging-extension";

    @Inject
    CurateOutcomeBuildItem curateOutcomeBuildItem;

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    @BuildStep
    KogitoAddonsGeneratedSourcesBuildItem generate(Optional<KogitoProcessContainerGeneratorBuildItem> processBuildItem, BuildProducer<KogitoMessagingMetadataBuildItem> metadataProducer,
            KogitoBuildContextBuildItem kogitoContext) {

        Collection<ChannelInfo> channelsInfo = ChannelMappingStrategy.getChannelMapping();
        Map<DotName, EventGenerator> generators = new HashMap<>();
        Map<String, EventGenerator> channels = new HashMap<>();

        processBuildItem.ifPresent(kogitoProcessContainerGeneratorBuildItem -> kogitoProcessContainerGeneratorBuildItem.getProcessContainerGenerators()
                .forEach(containerGenerator -> containerGenerator.getProcesses().forEach(process -> collect(process, channelsInfo, generators, channels, kogitoContext.getKogitoBuildContext()))));

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

        return new KogitoAddonsGeneratedSourcesBuildItem(generatedFiles);
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

    private void collect(Map<String, CompilationUnit> map, ChannelInfo channelInfo, Map<DotName, EventGenerator> eventGenerators, Map<String, EventGenerator> channels, KogitoBuildContext context) {
        for (String trigger : channelInfo.getTriggers()) {
            CompilationUnit cu = map.get(trigger);
            if (cu != null) {
                eventGenerators.computeIfAbsent(DotNamesHelper.createDotName(cu), k -> channels.computeIfAbsent(channelInfo.getChannelName(), c -> buildEventGenerator(context, channelInfo)));
            }
        }
    }

    private EventGenerator buildEventGenerator(KogitoBuildContext context, ChannelInfo channelInfo) {
        return channelInfo.isInput() ? new EventGenerator(context, channelInfo, "EventReceiver") : new EventGenerator(context, channelInfo, "EventEmitter");
    }
}
