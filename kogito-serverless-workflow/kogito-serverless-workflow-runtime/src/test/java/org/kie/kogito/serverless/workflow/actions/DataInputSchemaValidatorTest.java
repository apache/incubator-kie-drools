/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.serverless.workflow.actions;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.kogito.jackson.utils.ObjectMapperFactory;
import org.kie.kogito.serverless.workflow.SWFConstants;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatNoException;

public class DataInputSchemaValidatorTest {

    private static DataInputSchemaValidator validator;

    @BeforeAll
    static void init() {
        validator = new DataInputSchemaValidator("expression.json", true);
    }

    @Test
    void testValidSchema() throws IOException {
        final Map<String, Object> model = Collections.singletonMap(SWFConstants.DEFAULT_WORKFLOW_VAR, createNode(new IntNode(4), new IntNode(3)));
        assertThatNoException().isThrownBy(() -> validator.validate(model));
    }

    @Test
    void testInvalidSchema() throws IOException {
        final Map<String, Object> model = Collections.singletonMap(SWFConstants.DEFAULT_WORKFLOW_VAR, createNode(new TextNode("xcdsfd"), new IntNode(3)));
        assertThatIllegalArgumentException().isThrownBy(() -> validator.validate(model));
    }

    @Test
    void testEmptyInput() throws IOException {
        assertThatIllegalArgumentException().isThrownBy(() -> validator.validate(Collections.emptyMap()));
    }

    private ObjectNode createNode(JsonNode x, JsonNode y) {
        ObjectMapper mapper = ObjectMapperFactory.get();
        return mapper.createObjectNode().set("numbers", mapper.createArrayNode().add(mapper.createObjectNode().<ObjectNode> set("x", x).set("y", y)));
    }

}
