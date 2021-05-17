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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.drools.core.command.impl.CommandFactoryServiceImpl;
import org.kie.api.KieBase;
import org.kie.api.command.BatchExecutionCommand;
import org.kie.api.command.Command;
import org.kie.api.definition.type.FactType;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.pmml.PMML4Result;
import org.kie.api.runtime.StatelessKieSession;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.evaluator.api.exceptions.KiePMMLModelException;
import org.kie.pmml.models.drools.executor.KiePMMLStatusHolder;
import org.kie.pmml.models.drools.tuples.KiePMMLOriginalTypeGeneratedType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.pmml.models.drools.commons.factories.KiePMMLDescrFactory.OUTPUTFIELDS_MAP_IDENTIFIER;
import static org.kie.pmml.models.drools.commons.factories.KiePMMLDescrFactory.PMML4_RESULT_IDENTIFIER;

/**
 * Class used to isolate all the <code>KieSession</code> instantiation/usage details
 */
public class KiePMMLSessionUtils {

    private static final Logger logger = LoggerFactory.getLogger(KiePMMLSessionUtils.class.getName());

    private static final CommandFactoryServiceImpl COMMAND_FACTORY_SERVICE = new CommandFactoryServiceImpl();
    final StatelessKieSession kieSession;
    final String modelName;
    final String packageName;
    final List<Command> commands;

    private KiePMMLSessionUtils(final KieBase knowledgeBase, final String modelName, final String packageName, final PMML4Result pmml4Result) {
        this.modelName = modelName;
        this.packageName = packageName;
        kieSession = getKieSession(knowledgeBase);
        commands = new ArrayList<>();
        commands.add(COMMAND_FACTORY_SERVICE.newInsert(new KiePMMLStatusHolder()));
        commands.add(COMMAND_FACTORY_SERVICE.newInsert(pmml4Result));
        commands.add(COMMAND_FACTORY_SERVICE.newSetGlobal(PMML4_RESULT_IDENTIFIER, pmml4Result));
    }

    public static Builder builder(final KieBase knowledgeBase, final String modelName, final String packageName, final PMML4Result pmml4Result) {
        return new Builder(knowledgeBase, modelName, packageName, pmml4Result);
    }

    /**
     * Invoke <code>KieSession.fireAllRules()</code>
     */
    public void fireAllRules() {
        BatchExecutionCommand batchExecutionCommand = COMMAND_FACTORY_SERVICE.newBatchExecution(commands);
        kieSession.execute(batchExecutionCommand);
    }

    StatelessKieSession getKieSession(final KieBase knowledgeBase) {
        StatelessKieSession toReturn;
        try {
            toReturn = knowledgeBase.newStatelessKieSession();
            if (toReturn == null) {
                throw new KiePMMLException("Failed to create KieSession for model " + modelName);
            }
            return toReturn;
        } catch (Throwable t) {
            throw new KiePMMLException("Failed to create KieSession for model " + modelName, t);
        }
    }

    /**
     * Insert an <code>Object</code> to the underlying <code>KieSession</code>.
     * @param toInsert the <code>Object</code> to insert
     * @param globalName its global name
     */
    void insertObjectInSession(final Object toInsert, final String globalName) {
        commands.add(COMMAND_FACTORY_SERVICE.newInsert(toInsert));
        commands.add(COMMAND_FACTORY_SERVICE.newSetGlobal(globalName, toInsert));
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
                FactType factType = kieSession.getKieBase().getFactType(packageName, generatedTypeName);
                Object toAdd = factType.newInstance();
                factType.set(toAdd, "value", entry.getValue());
                commands.add(COMMAND_FACTORY_SERVICE.newInsert(toAdd));
            } catch (Exception e) {
                throw new KiePMMLModelException(e.getMessage(), e);
            }
        }
    }

    public static class Builder {

        KiePMMLSessionUtils toBuild;

        private Builder(final KieBase knowledgeBase, final String modelName, final String packageName, final PMML4Result pmml4Result) {
            this.toBuild = new KiePMMLSessionUtils(knowledgeBase, modelName, packageName, pmml4Result);
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
