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

package org.kie.dmn.model.api;

import org.junit.Test;
import org.kie.dmn.model.v1_2.TDMNElement;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class TUnaryTestsTest {

    @Test
    public void smokeTest() {
        UnaryTests ut = new STUnaryTests();
        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(() -> ut.getTypeRef());
        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(() -> ut.setTypeRef(null));
    }

    /**
     * Up to DMNv1.2.
     */
    private static class STUnaryTests extends TDMNElement implements UnaryTests {

        @Override
        public String getText() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setText(String value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getExpressionLanguage() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setExpressionLanguage(String value) {
            throw new UnsupportedOperationException();
        }

    }
}
