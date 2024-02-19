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
package org.kie.kogito.quarkus.workflow.deployment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.drools.codegen.common.GeneratedFile;
import org.drools.codegen.common.GeneratedFileType;
import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.DotName;
import org.jboss.jandex.IndexView;
import org.jboss.jandex.Type;
import org.jbpm.process.core.context.exception.ExceptionScope;
import org.kie.kogito.Model;
import org.kie.kogito.ProcessInput;
import org.kie.kogito.UserTask;
import org.kie.kogito.codegen.api.context.KogitoBuildContext;
import org.kie.kogito.codegen.json.JsonSchemaGenerator;
import org.kie.kogito.codegen.process.ProcessContainerGenerator;
import org.kie.kogito.codegen.process.persistence.PersistenceGenerator;
import org.kie.kogito.correlation.CompositeCorrelation;
import org.kie.kogito.correlation.Correlation;
import org.kie.kogito.correlation.CorrelationInstance;
import org.kie.kogito.correlation.SimpleCorrelation;
import org.kie.kogito.quarkus.common.deployment.KogitoBuildContextBuildItem;
import org.kie.kogito.quarkus.common.deployment.KogitoGeneratedClassesBuildItem;
import org.kie.kogito.quarkus.common.deployment.KogitoGeneratedSourcesBuildItem;
import org.kie.kogito.quarkus.extensions.spi.deployment.KogitoProcessContainerGeneratorBuildItem;
import org.kie.kogito.quarkus.workflow.KogitoBeanProducer;
import org.kie.kogito.serialization.process.ObjectMarshallerStrategy;
import org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf;
import org.kie.kogito.serialization.process.protobuf.KogitoProcessInstanceProtobuf;
import org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf;
import org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf;

import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.arc.deployment.GeneratedBeanBuildItem;
import io.quarkus.deployment.Capabilities;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.CombinedIndexBuildItem;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.GeneratedResourceBuildItem;
import io.quarkus.deployment.builditem.IndexDependencyBuildItem;
import io.quarkus.deployment.builditem.LiveReloadBuildItem;
import io.quarkus.deployment.builditem.nativeimage.NativeImageResourceBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ReflectiveClassBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ReflectiveHierarchyBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ServiceProviderBuildItem;
import io.quarkus.deployment.pkg.builditem.CurateOutcomeBuildItem;
import io.quarkus.deployment.pkg.steps.NativeOrNativeSourcesBuild;
import io.quarkus.maven.dependency.ResolvedDependency;
import io.quarkus.vertx.http.deployment.spi.AdditionalStaticResourceBuildItem;

import static java.util.Arrays.asList;
import static org.drools.quarkus.util.deployment.DroolsQuarkusResourceUtils.compileGeneratedSources;
import static org.kie.kogito.codegen.core.utils.GeneratedFileValidation.validateGeneratedFileTypes;
import static org.kie.kogito.quarkus.common.deployment.KogitoQuarkusResourceUtils.dumpFilesToDisk;
import static org.kie.kogito.quarkus.common.deployment.KogitoQuarkusResourceUtils.generateAggregatedIndex;
import static org.kie.kogito.quarkus.common.deployment.KogitoQuarkusResourceUtils.registerResources;

public abstract class WorkflowProcessor {

    private static final String PERSISTENCE_CAPABILITY = "org.kie.addons.persistence";

    public abstract FeatureBuildItem featureBuildItem();

    @BuildStep(onlyIf = NativeOrNativeSourcesBuild.class)
    public void addProtoDependencies(BuildProducer<IndexDependencyBuildItem> indexDependency,
            BuildProducer<ReflectiveHierarchyBuildItem> reflectiveHierarchyClass,
            BuildProducer<NativeImageResourceBuildItem> resource,
            BuildProducer<ReflectiveClassBuildItem> reflectiveClass,
            BuildProducer<ServiceProviderBuildItem> serviceProviderBuildItemBuildProducer,
            Capabilities capabilities) {

        // configure the application generator
        if (capabilities.isCapabilityWithPrefixPresent(PERSISTENCE_CAPABILITY)) {
            indexDependency.produce(new IndexDependencyBuildItem("com.google.protobuf", "protobuf-java"));
            resource.produce(new NativeImageResourceBuildItem("kogito-types.proto"));
            reflectiveClass.produce(new ReflectiveClassBuildItem(true, true, "org.kie.kogito.persistence.ProtostreamObjectMarshaller"));
            resource.produce(new NativeImageResourceBuildItem("META-INF/services/" + ObjectMarshallerStrategy.class.getName()));
            serviceProviderBuildItemBuildProducer.produce(ServiceProviderBuildItem.allProvidersFromClassPath(ObjectMarshallerStrategy.class.getName()));
            addInnerClasses(KogitoProcessInstanceProtobuf.class, reflectiveHierarchyClass);
            addInnerClasses(KogitoTypesProtobuf.class, reflectiveHierarchyClass);
            addInnerClasses(KogitoNodeInstanceContentsProtobuf.class, reflectiveHierarchyClass);
            addInnerClasses(KogitoWorkItemsProtobuf.class, reflectiveHierarchyClass);
        }
    }

    @BuildStep
    public ReflectiveClassBuildItem reflectionProcess(BuildProducer<ServiceProviderBuildItem> serviceProvider) {
        serviceProvider.produce(ServiceProviderBuildItem.allProvidersFromClassPath("org.kogito.workitem.rest.decorators.RequestDecorator"));
        return new ReflectiveClassBuildItem(true, true,
                "org.kogito.workitem.rest.bodybuilders.ParamsRestWorkItemHandlerBodyBuilder",
                "org.kogito.workitem.rest.decorators.CollectionParamsDecorator",
                "org.kogito.workitem.rest.auth.ClientOAuth2AuthDecorator",
                "org.kogito.workitem.rest.auth.PasswordOAuth2AuthDecorator",
                "io.vertx.core.http.HttpMethod",
                "org.kie.kogito.process.impl.BaseWorkItem",
                "org.kie.kogito.event.Topic",
                "org.kie.kogito.event.cloudevents.CloudEventMeta",
                "org.kie.kogito.event.cloudevents.SpecVersionDeserializer",
                "org.kie.kogito.event.cloudevents.SpecVersionSerializer",
                "org.kie.kogito.jobs.api.Job",
                CompositeCorrelation.class.getCanonicalName(),
                SimpleCorrelation.class.getCanonicalName(),
                Correlation.class.getCanonicalName(),
                CorrelationInstance.class.getCanonicalName(),
                ExceptionScope.class.getCanonicalName());
    }

    @BuildStep
    public AdditionalBeanBuildItem additionalBeans() {
        return AdditionalBeanBuildItem.builder().addBeanClasses(KogitoBeanProducer.class).build();
    }

    /**
     * Produces the {@link KogitoProcessContainerGeneratorBuildItem} after generating the Kogito classes
     */
    @BuildStep
    public void processApplicationSection(KogitoBuildContextBuildItem kogitoBuildContextBuildItem,
            BuildProducer<KogitoProcessContainerGeneratorBuildItem> processContainerProducer,
            KogitoGeneratedSourcesBuildItem generatedKogitoClasses) {
        final KogitoProcessContainerGeneratorBuildItem buildItem = new KogitoProcessContainerGeneratorBuildItem(
                kogitoBuildContextBuildItem.getKogitoBuildContext().getApplicationSections()
                        .stream()
                        .filter(ProcessContainerGenerator.class::isInstance)
                        .map(ProcessContainerGenerator.class::cast)
                        .collect(Collectors.toSet()));
        if (!buildItem.getProcessContainerGenerators().isEmpty()) {
            processContainerProducer.produce(buildItem);
        }
    }

    /**
     * Main entry point of the Quarkus extension
     */
    @BuildStep
    public void postGenerationProcessing(
            List<KogitoGeneratedClassesBuildItem> generatedKogitoClasses,
            BuildProducer<GeneratedBeanBuildItem> generatedBeans,
            BuildProducer<AdditionalStaticResourceBuildItem> staticResProducer,
            BuildProducer<NativeImageResourceBuildItem> resource,
            BuildProducer<GeneratedResourceBuildItem> genResBI,
            CombinedIndexBuildItem combinedIndexBuildItem,
            KogitoBuildContextBuildItem kogitoBuildContextBuildItem,
            Capabilities capabilities,
            LiveReloadBuildItem liveReload,
            CurateOutcomeBuildItem curateOutcomeBuildItem) throws IOException {

        // merge project index with classes generated by Kogito
        IndexView aggregatedIndex = generateAggregatedIndex(combinedIndexBuildItem.getComputingIndex(), generatedKogitoClasses);

        // configure the application generator
        KogitoBuildContext context = kogitoBuildContextBuildItem.getKogitoBuildContext();

        Collection<GeneratedFile> generatedFiles = new ArrayList<>();

        if (capabilities.isCapabilityWithPrefixPresent(PERSISTENCE_CAPABILITY)) {
            generatedFiles.addAll(generatePersistenceInfo(
                    context,
                    aggregatedIndex,
                    generatedBeans,
                    curateOutcomeBuildItem,
                    liveReload.isLiveReload()));
        }

        Map<String, byte[]> classes = new HashMap<>();
        for (KogitoGeneratedClassesBuildItem generatedKogitoClass : generatedKogitoClasses) {
            classes.putAll(generatedKogitoClass.getGeneratedClasses());
        }

        // Json schema files
        generatedFiles.addAll(generateJsonSchema(context, aggregatedIndex, classes));

        // Write files to disk
        dumpFilesToDisk(context.getAppPaths(), generatedFiles);

        // register resources to the Quarkus environment
        registerResources(generatedFiles, staticResProducer, resource, genResBI);
    }

    private Collection<GeneratedFile> generatePersistenceInfo(
            KogitoBuildContext context,
            IndexView index,
            BuildProducer<GeneratedBeanBuildItem> generatedBeans,
            CurateOutcomeBuildItem curateOutcomeBuildItem,
            boolean useDebugSymbols) {

        Collection<GeneratedFile> persistenceGeneratedFiles = getGeneratedPersistenceFiles(index, context);

        validateGeneratedFileTypes(persistenceGeneratedFiles, asList(GeneratedFileType.Category.SOURCE, GeneratedFileType.Category.INTERNAL_RESOURCE, GeneratedFileType.Category.STATIC_HTTP_RESOURCE));

        Collection<ResolvedDependency> dependencies = curateOutcomeBuildItem.getApplicationModel().getRuntimeDependencies();
        compileGeneratedSources(context, dependencies, persistenceGeneratedFiles, useDebugSymbols)
                .forEach(generatedBeans::produce);

        return persistenceGeneratedFiles;
    }

    private Collection<GeneratedFile> getGeneratedPersistenceFiles(IndexView index,
            KogitoBuildContext context) {

        Collection<ClassInfo> modelClasses = index
                .getAllKnownImplementors(DotName.createSimple(Model.class.getCanonicalName()));
        JandexProtoGenerator protoGenerator = JandexProtoGenerator.builder(index)
                .build(modelClasses);

        PersistenceGenerator persistenceGenerator = new PersistenceGenerator(
                context,
                protoGenerator,
                new JandexMarshallerGenerator(context, modelClasses));

        return persistenceGenerator.generate();
    }

    private void addInnerClasses(Class<?> superClass, BuildProducer<ReflectiveHierarchyBuildItem> reflectiveHierarchyClass) {
        DotName dotName = DotName.createSimple(superClass.getName());
        Type type = Type.create(dotName, Type.Kind.CLASS);
        reflectiveHierarchyClass.produce(new ReflectiveHierarchyBuildItem.Builder().type(type).build());
        asList(superClass.getDeclaredClasses()).forEach(c -> addInnerClasses(c, reflectiveHierarchyClass));
    }

    private Collection<GeneratedFile> generateJsonSchema(KogitoBuildContext context, IndexView index, Map<String, byte[]> generatedClasses) throws IOException {
        ClassLoader cl = new InMemoryClassLoader(context.getClassLoader(), generatedClasses);

        List<AnnotationInstance> annotations = new ArrayList<>();

        annotations.addAll(index.getAnnotations(DotName.createSimple(ProcessInput.class.getCanonicalName())));
        annotations.addAll(index.getAnnotations(DotName.createSimple(UserTask.class.getCanonicalName())));

        List<Class<?>> annotatedClasses = annotations.stream()
                .map(ann -> loadClassFromAnnotation(ann, cl))
                .filter(Optional::isPresent)
                .map(Optional::get).collect(Collectors.toList());

        JsonSchemaGenerator jsonSchemaGenerator = new JsonSchemaGenerator.ClassBuilder(annotatedClasses.stream())
                .withSchemaVersion(System.getProperty("kogito.jsonSchema.version"))
                .build();

        return jsonSchemaGenerator.generate();
    }

    private Optional<Class<?>> loadClassFromAnnotation(AnnotationInstance annotationInstance, ClassLoader classLoader) {
        try {
            return Optional.of(classLoader.loadClass(annotationInstance.target().asClass().name().toString()));
        } catch (ClassNotFoundException e) {
            return Optional.empty();
        }
    }
}
