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

import java.util.List;

import org.assertj.core.data.Offset;
import org.drools.core.command.runtime.SetGlobalCommand;
import org.drools.core.command.runtime.rule.InsertObjectCommand;
import org.drools.core.impl.KnowledgeBaseImpl;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.command.Command;
import org.kie.api.pmml.PMML4Result;
import org.kie.api.runtime.StatelessKieSession;
import org.kie.pmml.models.drools.executor.KiePMMLStatusHolder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.pmml.commons.Constants.PACKAGE_NAME;

public class KiePMMLSessionUtilsTest {

    private static final KieBase KIE_BASE = new KnowledgeBaseImpl("PMML", null);
    private final static String MODEL_NAME = "MODELNAME";
    private final static PMML4Result PMML4_RESULT = new PMML4Result();
    private KiePMMLSessionUtils.Builder builder;
    private KiePMMLSessionUtils kiePMMLSessionUtils;

    @Before
    public void setup() {
        builder = KiePMMLSessionUtils.builder(KIE_BASE, MODEL_NAME, PACKAGE_NAME, PMML4_RESULT);
        kiePMMLSessionUtils = builder.build();
    }

    @Test
    public void builder() {
        assertThat(builder).isNotNull();
        assertThat(kiePMMLSessionUtils).isNotNull();
    }

    @Test
    public void kiePMMLSessionUtils() {
        List<Command> retrieved = kiePMMLSessionUtils.commands;
        assertThat(retrieved).isNotNull();
        assertThat(retrieved).hasSize(3);
        assertThat(retrieved.get(0)).isInstanceOf(InsertObjectCommand.class);
        InsertObjectCommand insertObjectCommand = (InsertObjectCommand) retrieved.get(0);
        assertThat(insertObjectCommand.getEntryPoint()).isEqualTo("DEFAULT");
        assertThat(insertObjectCommand.getObject()).isNotNull();
        assertThat(insertObjectCommand.getObject()).isInstanceOf(KiePMMLStatusHolder.class);
        KiePMMLStatusHolder kiePMMLStatusHolder = (KiePMMLStatusHolder) insertObjectCommand.getObject();
        assertThat(kiePMMLStatusHolder.getAccumulator()).isCloseTo(0.0, Offset.offset(0.0));
        assertThat(kiePMMLStatusHolder.getStatus()).isNull();
        assertThat(retrieved.get(1)).isInstanceOf(InsertObjectCommand.class);
        insertObjectCommand = (InsertObjectCommand) retrieved.get(1);
        assertThat(insertObjectCommand.getEntryPoint()).isEqualTo("DEFAULT");
        assertThat(insertObjectCommand.getObject()).isNotNull();
        assertThat(insertObjectCommand.getObject()).isInstanceOf(PMML4Result.class);
        assertThat(insertObjectCommand.getObject()).isEqualTo(PMML4_RESULT);
        assertThat(retrieved.get(2)).isInstanceOf(SetGlobalCommand.class);
        SetGlobalCommand setGlobalCommand = (SetGlobalCommand) retrieved.get(2);
        assertThat(setGlobalCommand.getIdentifier()).isEqualTo("$pmml4Result");
        assertThat(setGlobalCommand.getObject()).isInstanceOf(PMML4Result.class);
        assertThat(setGlobalCommand.getObject()).isEqualTo(PMML4_RESULT);
    }

    @Test
    public void getKieSession() {
        StatelessKieSession retrieved = kiePMMLSessionUtils.getKieSession(KIE_BASE);
        assertThat(retrieved).isNotNull();
    }

    @Test
    public void insertObjectInSession() {
        final List<Command> retrieved = kiePMMLSessionUtils.commands;
        assertThat(retrieved).isNotNull();
        assertThat(retrieved).hasSize(3);
        final Object toInsert = "TO_INSERT";
        final String globalName = "GLOBAL_NAME";
        kiePMMLSessionUtils.insertObjectInSession(toInsert, globalName);
        assertThat(retrieved).hasSize(5);
        assertThat(retrieved.get(3)).isInstanceOf(InsertObjectCommand.class);
        InsertObjectCommand insertObjectCommand = (InsertObjectCommand) retrieved.get(3);
        assertThat(insertObjectCommand.getEntryPoint()).isEqualTo("DEFAULT");
        assertThat(insertObjectCommand.getObject()).isNotNull();
        assertThat(insertObjectCommand.getObject()).isEqualTo(toInsert);
        assertThat(retrieved.get(4)).isInstanceOf(SetGlobalCommand.class);
        SetGlobalCommand setGlobalCommand = (SetGlobalCommand) retrieved.get(4);
        assertThat(setGlobalCommand.getIdentifier()).isEqualTo(globalName);
        assertThat(setGlobalCommand.getObject()).isEqualTo(toInsert);
    }
}