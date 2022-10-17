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
package org.kie.kogito.grafana.dmn;

import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.kie.kogito.grafana.model.functions.Label;

import static org.assertj.core.api.Assertions.assertThat;

public class DmnTypesTest {

    @Test
    public void testBooleanTypeRender() {
        // Arrange
        BooleanType booleanType = new BooleanType();

        // Act
        String result = booleanType.getGrafanaFunction().render("dmn_metrics", Collections.singletonList(new Label("id", "test")));

        // Assert
        assertThat(result).isEqualTo("sum by (identifier) (increase(boolean_dmn_metrics_total{id=test}[1m]))");
    }

    @Test
    public void testStringTypeRender() {
        // Arrange
        StringType stringType = new StringType();

        // Act
        String result = stringType.getGrafanaFunction().render("dmn_metrics", Collections.singletonList(new Label("id", "test")));

        // Assert
        assertThat(result).isEqualTo("sum by (identifier) (increase(string_dmn_metrics_total{id=test}[1m]))");
    }

    @Test
    public void testDaysAndTimeDurationTypeTypeRender() {
        // Arrange
        DaysAndTimeDurationType daysAndTimeDurationType = new DaysAndTimeDurationType();

        // Act
        String result = daysAndTimeDurationType.getGrafanaFunction().render("dmn_metrics", Collections.singletonList(new Label("id", "test")));

        // Assert
        assertThat(result).isEqualTo("(sum(increase(days_and_time_duration_dmn_metrics_sum{id=test}[1m])))/(sum(increase(days_and_time_duration_dmn_metrics_count{id=test}[1m])))");
    }

    @Test
    public void testLocalDateTypeRender() {
        // Arrange
        LocalDateType localDateType = new LocalDateType();

        // Act
        String result = localDateType.getGrafanaFunction().render("dmn_metrics", Collections.singletonList(new Label("id", "test")));

        // Assert
        assertThat(result).isEqualTo("(sum(increase(date_dmn_metrics_sum{id=test}[1m])))/(sum(increase(date_dmn_metrics_count{id=test}[1m])))");
    }

    @Test
    public void testLocalTimeTypeRender() {
        // Arrange
        LocalTimeType localTimeType = new LocalTimeType();

        // Act
        String result = localTimeType.getGrafanaFunction().render("dmn_metrics", Collections.singletonList(new Label("id", "test")));

        // Assert
        assertThat(result).isEqualTo("(sum(increase(time_dmn_metrics_sum{id=test}[1m])))/(sum(increase(time_dmn_metrics_count{id=test}[1m])))");
    }

    @Test
    public void testNumberTypeTypeRender() {
        // Arrange
        NumberType numberType = new NumberType();

        // Act
        String result = numberType.getGrafanaFunction().render("dmn_metrics", Collections.singletonList(new Label("id", "test")));

        // Assert
        assertThat(result).isEqualTo("(sum(increase(number_dmn_metrics_sum{id=test}[1m])))/(sum(increase(number_dmn_metrics_count{id=test}[1m])))");
    }

    @Test
    public void testTimeAndDateTypeRender() {
        // Arrange
        TimeAndDateType timeAndDateType = new TimeAndDateType();

        // Act
        String result = timeAndDateType.getGrafanaFunction().render("dmn_metrics", Collections.singletonList(new Label("id", "test")));

        // Assert
        assertThat(result).isEqualTo("(sum(increase(date_and_time_dmn_metrics_sum{id=test}[1m])))/(sum(increase(date_and_time_dmn_metrics_count{id=test}[1m])))");
    }

    @Test
    public void testYearsAndMonthsDurationTypeTypeRender() {
        // Arrange
        YearsAndMonthsDurationType yearsAndMonthsDurationType = new YearsAndMonthsDurationType();

        // Act
        String result = yearsAndMonthsDurationType.getGrafanaFunction().render("dmn_metrics", Collections.singletonList(new Label("id", "test")));

        // Assert
        assertThat(result).isEqualTo("(sum(increase(years_and_months_duration_dmn_metrics_sum{id=test}[1m])))/(sum(increase(years_and_months_duration_dmn_metrics_count{id=test}[1m])))");
    }
}
