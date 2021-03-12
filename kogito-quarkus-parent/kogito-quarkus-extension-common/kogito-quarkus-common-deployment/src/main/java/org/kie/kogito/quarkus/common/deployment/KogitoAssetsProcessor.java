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
package org.kie.kogito.quarkus.common.deployment;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.inject.Inject;

import org.jboss.jandex.DotName;
import org.jboss.jandex.IndexView;
import org.jboss.jandex.Indexer;
import org.kie.kogito.codegen.api.GeneratedFile;
import org.kie.kogito.codegen.api.context.KogitoBuildContext;
import org.kie.kogito.codegen.core.utils.ApplicationGeneratorDiscovery;

import io.quarkus.arc.deployment.GeneratedBeanBuildItem;
import io.quarkus.bootstrap.model.AppDependency;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.ArchiveRootBuildItem;
import io.quarkus.deployment.builditem.CombinedIndexBuildItem;
import io.quarkus.deployment.builditem.GeneratedResourceBuildItem;
import io.quarkus.deployment.builditem.LiveReloadBuildItem;
import io.quarkus.deployment.builditem.nativeimage.NativeImageResourceBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ReflectiveClassBuildItem;
import io.quarkus.deployment.index.IndexingUtil;
import io.quarkus.deployment.pkg.builditem.CurateOutcomeBuildItem;

import static org.kie.kogito.quarkus.common.deployment.KogitoQuarkusResourceUtils.compileGeneratedSources;
import static org.kie.kogito.quarkus.common.deployment.KogitoQuarkusResourceUtils.dumpFilesToDisk;
import static org.kie.kogito.quarkus.common.deployment.KogitoQuarkusResourceUtils.kogitoBuildContext;
import static org.kie.kogito.quarkus.common.deployment.KogitoQuarkusResourceUtils.registerResources;

/**
 * Main class of the Kogito extension
 */
public class KogitoAssetsProcessor {

    @Inject
    ArchiveRootBuildItem root;
    @Inject
    LiveReloadBuildItem liveReload;
    @Inject
    CurateOutcomeBuildItem curateOutcomeBuildItem;
    @Inject
    CombinedIndexBuildItem combinedIndexBuildItem;

    /**
     * Main entry point of the Quarkus extension
     */
    @BuildStep
    public List<KogitoGeneratedClassesBuildItem> generateModel(
            BuildProducer<GeneratedBeanBuildItem> generatedBeans,
            BuildProducer<NativeImageResourceBuildItem> resource,
            BuildProducer<ReflectiveClassBuildItem> reflectiveClass,
            BuildProducer<GeneratedResourceBuildItem> genResBI) throws IOException {

        if (liveReload.isLiveReload()) {
            return Collections.emptyList();
        }

        // configure the application generator
        KogitoBuildContext context = kogitoBuildContext(root.getPaths(), combinedIndexBuildItem.getIndex());

        Collection<GeneratedFile> generatedFiles = generateFiles(context);

        // dump files to disk
        dumpFilesToDisk(context.getAppPaths(), generatedFiles);

        // build Java source code and register the generated beans
        Optional<IndexView> optionalIndex = compileAndIndexJavaSources(
                context,
                generatedFiles,
                generatedBeans);

        registerDataEventsForReflection(optionalIndex, context, reflectiveClass);

        registerResources(generatedFiles, resource, genResBI);

        return optionalIndex
                .map(KogitoGeneratedClassesBuildItem::new)
                .map(Collections::singletonList)
                .orElse(Collections.emptyList());
    }

    private Collection<GeneratedFile> generateFiles(KogitoBuildContext context) {
        return ApplicationGeneratorDiscovery
                .discover(context)
                .generate();
    }

    private Optional<IndexView> compileAndIndexJavaSources(
            KogitoBuildContext context,
            Collection<GeneratedFile> generatedFiles,
            BuildProducer<GeneratedBeanBuildItem> generatedBeans) throws IOException {

        List<AppDependency> dependencies = curateOutcomeBuildItem.getEffectiveModel().getUserDependencies();

        Collection<GeneratedBeanBuildItem> generatedBeanBuildItems =
                compileGeneratedSources(context, dependencies, generatedFiles);
        generatedBeanBuildItems.forEach(generatedBeans::produce);
        return Optional.of(indexBuildItems(context, generatedBeanBuildItems));
    }

    @BuildStep
    public ReflectiveClassBuildItem reflectionJobsManagement() {
        return new ReflectiveClassBuildItem(true, true, "org.kie.kogito.jobs.api.Job");
    }

    private void registerDataEventsForReflection(Optional<IndexView> optionalIndex, KogitoBuildContext context, BuildProducer<ReflectiveClassBuildItem> reflectiveClass) {
        reflectiveClass.produce(
                new ReflectiveClassBuildItem(true, true, "org.kie.kogito.event.AbstractDataEvent"));
        reflectiveClass.produce(
                new ReflectiveClassBuildItem(true, true, "org.kie.kogito.services.event.AbstractProcessDataEvent"));
        reflectiveClass.produce(
                new ReflectiveClassBuildItem(true, true, "org.kie.kogito.services.event.ProcessInstanceDataEvent"));
        reflectiveClass.produce(
                new ReflectiveClassBuildItem(true, true, "org.kie.kogito.services.event.VariableInstanceDataEvent"));
        reflectiveClass.produce(
                new ReflectiveClassBuildItem(true, true, "org.kie.kogito.services.event.impl.ProcessInstanceEventBody"));
        reflectiveClass.produce(
                new ReflectiveClassBuildItem(true, true, "org.kie.kogito.services.event.impl.NodeInstanceEventBody"));
        reflectiveClass.produce(
                new ReflectiveClassBuildItem(true, true, "org.kie.kogito.services.event.impl.ProcessErrorEventBody"));
        reflectiveClass.produce(
                new ReflectiveClassBuildItem(true, true, "org.kie.kogito.services.event.impl.VariableInstanceEventBody"));
        reflectiveClass.produce(
                new ReflectiveClassBuildItem(true, true, "org.kie.kogito.services.event.UserTaskInstanceDataEvent"));
        reflectiveClass.produce(
                new ReflectiveClassBuildItem(true, true, "org.kie.kogito.services.event.impl.UserTaskInstanceEventBody"));

        if (context.getAddonsConfig().useMonitoring()) {
            registerMonitoringAddonClasses(reflectiveClass);
        }

        if (context.getAddonsConfig().useTracing()) {
            registerTracingAddonClasses(reflectiveClass);
        }

        if (context.getAddonsConfig().useExplainability()) {
            registerExplainabilityAddonClasses(reflectiveClass);
        }

        optionalIndex.ifPresent(index -> {
            // not sure there is any generated class directly inheriting from AbstractDataEvent, keeping just in case
            addChildrenClasses(index, "org.kie.kogito.event.AbstractDataEvent", reflectiveClass);
            addChildrenClasses(index, "org.kie.kogito.services.event.AbstractProcessDataEvent", reflectiveClass);
        });
    }

    private void registerMonitoringAddonClasses(BuildProducer<ReflectiveClassBuildItem> reflectiveClass) {
        reflectiveClass.produce(
                new ReflectiveClassBuildItem(true, true, "org.HdrHistogram.Histogram"));
        reflectiveClass.produce(
                new ReflectiveClassBuildItem(true, true, "org.HdrHistogram.ConcurrentHistogram"));
    }

    private void registerTracingAddonClasses(BuildProducer<ReflectiveClassBuildItem> reflectiveClass) {
        reflectiveClass.produce(
                new ReflectiveClassBuildItem(true, true, "org.kie.kogito.tracing.decision.event.trace.TraceEvent"));
        reflectiveClass.produce(
                new ReflectiveClassBuildItem(true, true, "org.kie.kogito.tracing.decision.event.trace.TraceHeader"));
        reflectiveClass.produce(
                new ReflectiveClassBuildItem(true, true, "org.kie.kogito.tracing.decision.event.trace.TraceEventType"));
        reflectiveClass.produce(
                new ReflectiveClassBuildItem(true, true, "org.kie.kogito.tracing.decision.event.trace.TraceResourceId"));
        reflectiveClass.produce(
                new ReflectiveClassBuildItem(true, true, "org.kie.kogito.tracing.decision.event.message.Message"));
        reflectiveClass.produce(
                new ReflectiveClassBuildItem(true, true, "org.kie.kogito.tracing.decision.event.message.MessageLevel"));
        reflectiveClass.produce(
                new ReflectiveClassBuildItem(true, true, "org.kie.kogito.tracing.decision.event.message.MessageCategory"));
        reflectiveClass.produce(
                new ReflectiveClassBuildItem(true, true, "org.kie.kogito.tracing.decision.event.message.MessageFEELEvent"));
        reflectiveClass.produce(
                new ReflectiveClassBuildItem(true, true, "org.kie.kogito.tracing.decision.event.message.MessageFEELEventSeverity"));
        reflectiveClass.produce(
                new ReflectiveClassBuildItem(true, true, "org.kie.kogito.tracing.decision.event.message.MessageExceptionField"));
        reflectiveClass.produce(
                new ReflectiveClassBuildItem(true, true, "org.kie.kogito.tracing.decision.event.message.MessageFEELEventSeverity"));
        reflectiveClass.produce(
                new ReflectiveClassBuildItem(true, true, "org.kie.kogito.tracing.decision.event.message.MessageFEELEventSeverity"));
        reflectiveClass.produce(
                new ReflectiveClassBuildItem(true, true, "org.kie.kogito.tracing.decision.event.message.MessageFEELEventSeverity"));

        reflectiveClass.produce(
                new ReflectiveClassBuildItem(true, true, "org.kie.kogito.tracing.decision.event.trace.TraceInputValue"));
        reflectiveClass.produce(
                new ReflectiveClassBuildItem(true, true, "org.kie.kogito.tracing.decision.event.trace.TraceOutputValue"));
        reflectiveClass.produce(
                new ReflectiveClassBuildItem(true, true, "org.kie.kogito.tracing.typedvalue.TypedValue"));
        reflectiveClass.produce(
                new ReflectiveClassBuildItem(true, true, "org.kie.kogito.tracing.typedvalue.UnitValue"));
        reflectiveClass.produce(
                new ReflectiveClassBuildItem(true, true, "org.kie.kogito.tracing.typedvalue.CollectionValue"));
        reflectiveClass.produce(
                new ReflectiveClassBuildItem(true, true, "org.kie.kogito.tracing.typedvalue.StructureValue"));
        reflectiveClass.produce(
                new ReflectiveClassBuildItem(true, true, "org.kie.kogito.tracing.decision.event.trace.TraceExecutionStep"));
        reflectiveClass.produce(
                new ReflectiveClassBuildItem(true, true, "org.kie.kogito.tracing.decision.event.trace.TraceExecutionStepType"));

        reflectiveClass.produce(
                new ReflectiveClassBuildItem(true, true, "org.kie.kogito.tracing.decision.event.model.ModelEvent"));
        reflectiveClass.produce(
                new ReflectiveClassBuildItem(true, true, "org.kie.kogito.tracing.decision.event.model.ModelEvent$GAV"));
        reflectiveClass.produce(
                new ReflectiveClassBuildItem(true, true, "org.kie.kogito.decision.DecisionModelType"));
        reflectiveClass.produce(
                new ReflectiveClassBuildItem(true, true, "org.kie.kogito.decision.DecisionModelMetadata"));
        reflectiveClass.produce(
                new ReflectiveClassBuildItem(true, true, "org.kie.kogito.decision.DecisionModelMetadata$Type"));
    }

    private void registerExplainabilityAddonClasses(BuildProducer<ReflectiveClassBuildItem> reflectiveClass) {
        reflectiveClass.produce(
                new ReflectiveClassBuildItem(true, true, "org.kie.kogito.explainability.model.PredictOutput"));
        reflectiveClass.produce(
                new ReflectiveClassBuildItem(true, true, "org.kie.kogito.explainability.model.PredictInput"));
        reflectiveClass.produce(
                new ReflectiveClassBuildItem(true, true, "org.kie.kogito.explainability.model.ModelIdentifier"));
    }

    private void addChildrenClasses(IndexView index,
            String superClass,
            BuildProducer<ReflectiveClassBuildItem> reflectiveClass) {
        index.getAllKnownSubclasses(DotName.createSimple(superClass))
                .forEach(c -> reflectiveClass.produce(
                        new ReflectiveClassBuildItem(true, true, c.name().toString())));
    }

    private IndexView indexBuildItems(KogitoBuildContext context, Collection<GeneratedBeanBuildItem> buildItems) {
        Indexer kogitoIndexer = new Indexer();
        Set<DotName> kogitoIndex = new HashSet<>();

        for (GeneratedBeanBuildItem generatedBeanBuildItem : buildItems) {
            IndexingUtil.indexClass(
                    generatedBeanBuildItem.getName(),
                    kogitoIndexer,
                    combinedIndexBuildItem.getIndex(),
                    kogitoIndex,
                    context.getClassLoader(),
                    generatedBeanBuildItem.getData());
        }

        return kogitoIndexer.complete();
    }
}
