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
package org.drools.drl.quarkus.testext.deployment;

import static io.quarkus.deployment.annotations.ExecutionTime.STATIC_INIT;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.inject.Inject;

import org.drools.drl.quarkus.util.deployment.KmoduleKieBaseModelsBuiltItem;
import org.drools.drl.quarkus.util.deployment.PatternsTypesBuildItem;
import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.DotName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.arc.deployment.BeanContainerListenerBuildItem;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.deployment.builditem.CombinedIndexBuildItem;
import io.quarkus.deployment.builditem.FeatureBuildItem;

public class DroolsTestExtAssetsProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(DroolsTestExtAssetsProcessor.class);

    private static final String FEATURE = "drools-drl-quarkus-testext-extension";
    
    @Inject
    CombinedIndexBuildItem indexBI;

    @BuildStep
    public FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    @Record(STATIC_INIT)
    @BuildStep
    public void generateSources( PatternsTypesBuildItem otnClasesBI, Optional<KmoduleKieBaseModelsBuiltItem> kbaseModelsBI,
            OtnMetadataRecorder recorder, BuildProducer<AdditionalBeanBuildItem> additionalBeanProducer, BuildProducer<BeanContainerListenerBuildItem> containerListenerProducer ) {
        LOGGER.info("{}", otnClasesBI.getPatternsClasses());
        Set<String> allKnown = new HashSet<>();
        for (Class<?> c : otnClasesBI.getPatternsClasses().values().stream().flatMap(x -> x.stream()).collect(Collectors.toList())) {
            allKnown.add(c.getCanonicalName());
            if (c.isInterface()) {
                allKnown.addAll(indexBI.getIndex().getAllKnownImplementors(c).stream().map(ClassInfo::name).map(DotName::toString).collect(Collectors.toList()));
            } else {
                allKnown.addAll(indexBI.getIndex().getAllKnownSubclasses(c).stream().map(ClassInfo::name).map(DotName::toString).collect(Collectors.toList()));
            }
        }
        additionalBeanProducer.produce(AdditionalBeanBuildItem.unremovableOf(OtnClassesSingleton.class));
        containerListenerProducer.produce(new BeanContainerListenerBuildItem(recorder.setContent(otnClasesBI.getPatternsClasses(), allKnown)));
        if (kbaseModelsBI.isPresent()) {
            LOGGER.info("{}", kbaseModelsBI.get().getKieBaseModels());
        }
    }

}
