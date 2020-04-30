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

import static org.kie.pmml.models.drools.commons.factories.KiePMMLDescrFactory.OUTPUTFIELDS_MAP_IDENTIFIER;
import static org.kie.pmml.models.drools.commons.factories.KiePMMLDescrFactory.PMML4_RESULT_IDENTIFIER;

/**
 * Class used to isolate all the <code>KieSession</code> instantiation/usage details
 */
public class KiePMMLSessionUtils {

    private final PackageDescr packageDescr;
    private final KieSession kieSession;

    private KiePMMLSessionUtils(final PackageDescr packageDescr, final PMML4Result pmml4Result) {
        this.packageDescr = packageDescr;
        kieSession = new KieHelper()
                .addContent(packageDescr)
                .build(ExecutableModelProject.class)
                .newKieSession();
        kieSession.insert(new KiePMMLStatusHolder());
        kieSession.insert(pmml4Result);
        kieSession.setGlobal(PMML4_RESULT_IDENTIFIER, pmml4Result);
    }

    public static Builder builder(final PackageDescr packageDescr, final PMML4Result pmml4Result) {
        return new Builder(packageDescr, pmml4Result);
    }

    /**
     * Invoke <code>KieSession.fireAllRules()</code>
     */
    public void fireAllRules() {
        kieSession.fireAllRules();
    }

    /**
     * Add <code>Object</code>s to the underlying <code>KieSession</code>.
     * Such <code>Object</code>s are retrieved/instantiated from the given <code>Map</code>s and the content of the current kieSession' <code>KieBase</code>
     * @param unwrappedInputParams
     * @param fieldTypeMap
     */
    private void addObjectsToSession(final Map<String, Object> unwrappedInputParams, final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap) {
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

    /**
     * Insert an <code>Object</code> to the underlying <code>KieSession</code>.
     * @param toInsert the <code>Object</code> to insert
     * @param globalName its global name
     */
    private void insertObjectInSession(final Object toInsert, final String globalName) {
        kieSession.insert(toInsert);
        kieSession.setGlobal(globalName, toInsert);
    }

    public static class Builder {

        private KiePMMLSessionUtils toBuild;

        private Builder(final PackageDescr packageDescr, final PMML4Result pmml4Result) {
            this.toBuild = new KiePMMLSessionUtils(packageDescr, pmml4Result);
        }

        /**
         * Add an <code>AgendaEventListener</code> to the underlying <code>KieSession</code>
         * @param agendaEventListener
         * @return
         */
        public Builder withAgendaEventListener(AgendaEventListener agendaEventListener) {
            this.toBuild.kieSession.addEventListener(agendaEventListener);
            return this;
        }

        /**
         * Insert <code>Object</code>s to the underlying <code>KieSession</code>.
         * Such <code>Object</code>s are retrieved out of the given <code>Map</code>s
         * @param unwrappedInputParams
         * @param fieldTypeMap
         * @return
         */
        public Builder withObjectsInSession(final Map<String, Object> unwrappedInputParams, final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap) {
            this.toBuild.addObjectsToSession(unwrappedInputParams, fieldTypeMap);
            return this;
        }

        /**
         * Insert <code>Map&lt;String, Object&gt;</code> <b>outputFieldsMap</b> to the underlying <code>KieSession</code>.
         * @param outputFieldsMap
         * @return
         */
        public Builder withOutputFieldsMap(final Map<String, Object> outputFieldsMap) {
            this.toBuild.insertObjectInSession(outputFieldsMap, OUTPUTFIELDS_MAP_IDENTIFIER);
            return this;
        }

        public KiePMMLSessionUtils build() {
            return this.toBuild;
        }
    }
}
