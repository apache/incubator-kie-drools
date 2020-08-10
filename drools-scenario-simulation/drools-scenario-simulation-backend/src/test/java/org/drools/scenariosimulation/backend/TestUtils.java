/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
package org.drools.scenariosimulation.backend;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.drools.scenariosimulation.api.model.AuditLogLine;
import org.drools.scenariosimulation.backend.util.ResourceHelper;
import org.kie.api.builder.Message;
import org.kie.dmn.api.core.DMNMessage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Class used to provide commonly used method for test classes
 */
public class TestUtils {

    public static String getFileContent(String fileName) throws IOException {
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1);
        String filePath = ResourceHelper.getResourcesByExtension(extension)
                .filter(path -> path.endsWith(fileName))
                .findFirst()
                .orElse(null);
        assertNotNull(filePath);
        File sourceFile = new File(filePath);
        assertTrue(sourceFile.exists());
        return new String(Files.readAllBytes(sourceFile.toPath()));
    }

    public static List<DMNMessage> getRandomlyGeneratedDMNMessageList() {
        return IntStream.range(0, 5).mapToObj(index -> {
            Message.Level level = Message.Level.values()[new Random().nextInt(Message.Level.values().length)];
            return createDMNMessageMock("dmnMessage-" + index, level);
        }).collect(Collectors.toList());
    }

    public static void commonCheckAuditLogLine(AuditLogLine toCheck, String expectedDecisionOrRuleName, String expectedResult, String expectedMessage) {
        assertNotNull(toCheck);
        assertEquals(expectedDecisionOrRuleName, toCheck.getDecisionOrRuleName());
        assertEquals(expectedResult, toCheck.getResult());
        if (expectedMessage == null) {
            assertFalse(toCheck.getMessage().isPresent());
        } else {
            assertEquals(expectedMessage, toCheck.getMessage().get());
        }
    }

    private static DMNMessage createDMNMessageMock(String text, Message.Level level) {
        DMNMessage dmnMessageMock = mock(DMNMessage.class);
        when(dmnMessageMock.getText()).thenReturn(text);
        when(dmnMessageMock.getLevel()).thenReturn(level);
        return dmnMessageMock;
    }
}
