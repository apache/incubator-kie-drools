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
package org.kie.kogito.grafana.model.functions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class BaseExpressionTest {

    @Test
    public void testBaseExpressionRender() {
        // Arrange
        BaseExpression baseExpression = new BaseExpression("prefix", "suffix");

        // Act
        String result = baseExpression.render("body", Collections.singletonList(new Label("test", "test")));

        // Assert
        assertThat(result).isEqualTo("prefix_body_suffix{test=test}");
    }

    @Test
    public void testBaseExpressionRenderWithMultipleLabels() {
        // Arrange
        GrafanaFunction baseExpression = new BaseExpression("prefix", "suffix");
        List<Label> labels = new ArrayList<>();
        labels.add(new Label("first", "value"));
        labels.add(new Label("second", "\"value\""));

        // Act
        String result = baseExpression.render("body", labels);

        // Assert
        assertThat(result).isEqualTo("prefix_body_suffix{first=value,second=\"value\"}");
    }
}
