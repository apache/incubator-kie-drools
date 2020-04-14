/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.kie.pmml.models.drools.utils;

import java.util.Map;

import org.drools.compiler.lang.descr.PackageDescr;
import org.drools.modelcompiler.ExecutableModelProject;
import org.kie.api.definition.type.FactType;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.pmml.PMML4Result;
import org.kie.api.runtime.KieSession;
import org.kie.internal.utils.KieHelper;
import org.kie.pmml.evaluator.api.exceptions.KiePMMLModelException;
import org.kie.pmml.models.drools.executor.KiePMMLStatusHolder;
import org.kie.pmml.models.drools.tuples.KiePMMLOriginalTypeGeneratedType;

/**
 * Class used to isolate all the <code>KieSession</code> instantiation/usage details
 */
public class KiePMMLSessionUtils {

    private final PackageDescr packageDescr;
    private final PMML4Result pmml4Result;
    private AgendaEventListener agendaEventListener;
    private Map<String, Object> unwrappedInputParams;
    private Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap;

    private KiePMMLSessionUtils(final PackageDescr packageDescr, final PMML4Result pmml4Result) {
        this.packageDescr = packageDescr;
        this.pmml4Result = pmml4Result;
    }

    public static Builder builder(final PackageDescr packageDescr, final PMML4Result pmml4Result) {
        return new Builder(packageDescr, pmml4Result);
    }

    public void fireAllRules() {
        KieSession kieSession = new KieHelper()
                .addContent(packageDescr)
                .build(ExecutableModelProject.class)
                .newKieSession();
        kieSession.addEventListener(agendaEventListener);
        kieSession.setGlobal("$pmml4Result", pmml4Result);
        addExecutionsParameters(kieSession);
        kieSession.fireAllRules();
    }

    private void addExecutionsParameters(final KieSession kieSession) {
        kieSession.insert(new KiePMMLStatusHolder());
        kieSession.insert(pmml4Result);
        for (Map.Entry<String, Object> entry : unwrappedInputParams.entrySet()) {
            if (!fieldTypeMap.containsKey(entry.getKey())) {
                throw new KiePMMLModelException(String.format("Field %s not mapped to generated type", entry.getKey()));
            }
            try {
                String generatedTypeName = fieldTypeMap.get(entry.getKey()).getGeneratedType();
                FactType factType = kieSession.getKieBase().getFactType(packageDescr.getName(), generatedTypeName);
                Object toAdd = factType.newInstance();
                factType.set(toAdd, "value", entry.getValue());
                kieSession.insert(toAdd);
            } catch (Exception e) {
                throw new KiePMMLModelException(e.getMessage(), e);
            }
        }
    }

    public static class Builder {

        private KiePMMLSessionUtils toBuild;

        private Builder(final PackageDescr packageDescr, final PMML4Result pmml4Result) {
            this.toBuild = new KiePMMLSessionUtils(packageDescr, pmml4Result);
        }

        public Builder withAgendaEventListener(AgendaEventListener agendaEventListener) {
            this.toBuild.agendaEventListener = agendaEventListener;
            return this;
        }

        public Builder withUnwrappedInputParams(final Map<String, Object> unwrappedInputParams) {
            this.toBuild.unwrappedInputParams = unwrappedInputParams;
            return this;
        }

        public Builder withFieldTypeMap(final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap) {
            this.toBuild.fieldTypeMap = fieldTypeMap;
            return this;
        }

        public KiePMMLSessionUtils build() {
            return this.toBuild;
        }
    }
}
