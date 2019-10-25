/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.drools.scenariosimulation.api.model;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.drools.scenariosimulation.api.utils.ConstantsHolder.VALUE;

public class FactMappingValueTest {

    @Test
    public void emptyFactMappingValue() {
        assertThatThrownBy(() -> new FactMappingValue(null, ExpressionIdentifier.DESCRIPTION, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("FactIdentifier has to be not null");

        assertThatThrownBy(() -> new FactMappingValue(FactIdentifier.DESCRIPTION, null, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("ExpressionIdentifier has to be not null");
    }

    @Test
    public void resetStatus() {
        FactMappingValue value = new FactMappingValue();
        value.resetStatus();
        assertThat(value.getStatus()).isEqualTo(FactMappingValueStatus.SUCCESS);
        assertThat(value.getExceptionMessage()).isNull();
        assertThat(value.getErrorValue()).isNull();
    }

    @Test
    public void setErrorValue() {
        String errorValue = VALUE;
        FactMappingValue value = new FactMappingValue();
        value.setErrorValue(errorValue);
        assertThat(value.getStatus()).isEqualTo(FactMappingValueStatus.FAILED_WITH_ERROR);
        assertThat(value.getExceptionMessage()).isNull();
        assertThat(value.getErrorValue()).isEqualTo(errorValue);
    }

    @Test
    public void setExceptionMessage() {
        String exceptionValue = "Exception";
        FactMappingValue value = new FactMappingValue();
        value.setExceptionMessage(exceptionValue);
        assertThat(value.getStatus()).isEqualTo(FactMappingValueStatus.FAILED_WITH_EXCEPTION);
        assertThat(value.getExceptionMessage()).isEqualTo(exceptionValue);
        assertThat(value.getErrorValue()).isNull();
    }
}