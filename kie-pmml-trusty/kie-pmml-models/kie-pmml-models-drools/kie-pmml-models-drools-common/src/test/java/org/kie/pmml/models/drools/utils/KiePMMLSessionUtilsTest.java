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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.kie.pmml.models.drools.commons.factories.KiePMMLDescrFactory.OUTPUTFIELDS_MAP_IDENTIFIER;

public class KiePMMLSessionUtilsTest {

    private static final KieBase KIE_BASE = new KnowledgeBaseImpl("PMML", null);
    private final static String MODEL_NAME = "MODELNAME";
    private final static String PACKAGE_NAME = "PACKAGENAME";
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
        assertNotNull(builder);
        assertNotNull(kiePMMLSessionUtils);
    }

    @Test
    public void kiePMMLSessionUtils() {
        List<Command> retrieved = kiePMMLSessionUtils.commands;
        assertNotNull(retrieved);
        assertEquals(3, retrieved.size());
        assertTrue(retrieved.get(0) instanceof InsertObjectCommand);
        InsertObjectCommand insertObjectCommand = (InsertObjectCommand) retrieved.get(0);
        assertEquals("DEFAULT", insertObjectCommand.getEntryPoint());
        assertNotNull(insertObjectCommand.getObject());
        assertTrue(insertObjectCommand.getObject() instanceof KiePMMLStatusHolder);
        KiePMMLStatusHolder kiePMMLStatusHolder = (KiePMMLStatusHolder) insertObjectCommand.getObject();
        assertEquals(0.0, kiePMMLStatusHolder.getAccumulator(), 0.0);
        assertNull(kiePMMLStatusHolder.getStatus());
        assertTrue(retrieved.get(1) instanceof InsertObjectCommand);
        insertObjectCommand = (InsertObjectCommand) retrieved.get(1);
        assertEquals("DEFAULT", insertObjectCommand.getEntryPoint());
        assertNotNull(insertObjectCommand.getObject());
        assertTrue(insertObjectCommand.getObject() instanceof PMML4Result);
        assertEquals(PMML4_RESULT, insertObjectCommand.getObject());
        assertTrue(retrieved.get(2) instanceof SetGlobalCommand);
        SetGlobalCommand setGlobalCommand = (SetGlobalCommand) retrieved.get(2);
        assertEquals("$pmml4Result", setGlobalCommand.getIdentifier());
        assertTrue(setGlobalCommand.getObject() instanceof PMML4Result);
        assertEquals(PMML4_RESULT, setGlobalCommand.getObject());
    }

    @Test
    public void getKieSession() {
        StatelessKieSession retrieved =  kiePMMLSessionUtils.getKieSession(KIE_BASE);
        assertNotNull(retrieved);
    }

    @Test
    public void insertObjectInSession() {
        final List<Command> retrieved = kiePMMLSessionUtils.commands;
        assertNotNull(retrieved);
        assertEquals(3, retrieved.size());
        final Object toInsert = "TO_INSERT";
        final String globalName = "GLOBAL_NAME";
        kiePMMLSessionUtils.insertObjectInSession(toInsert, globalName);
        assertEquals(5, retrieved.size());
        assertTrue(retrieved.get(3) instanceof InsertObjectCommand);
        InsertObjectCommand insertObjectCommand = (InsertObjectCommand) retrieved.get(3);
        assertEquals("DEFAULT", insertObjectCommand.getEntryPoint());
        assertNotNull(insertObjectCommand.getObject());
        assertEquals(toInsert, insertObjectCommand.getObject());
        assertTrue(retrieved.get(4) instanceof SetGlobalCommand);
        SetGlobalCommand setGlobalCommand = (SetGlobalCommand) retrieved.get(4);
        assertEquals(globalName, setGlobalCommand.getIdentifier());
        assertEquals(toInsert, setGlobalCommand.getObject());
    }
}