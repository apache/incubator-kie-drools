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
package org.kie.kogito.codegen.api;

import org.drools.codegen.common.GeneratedFileType;
import org.drools.codegen.common.GeneratedFileType.Category;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GeneratedFileTypeTest {

    @Test
    public void ofCategory() {
        assertThatThrownBy(() -> GeneratedFileType.of(null))
                .isInstanceOf(NullPointerException.class);

        GeneratedFileType sourceGeneratedType = GeneratedFileType.of(Category.SOURCE);
        assertThat(sourceGeneratedType.category())
                .isEqualTo(Category.SOURCE);
        assertThat(sourceGeneratedType.name())
                .isEqualTo(Category.SOURCE.name());
        assertThat(sourceGeneratedType.canHotReload()).isTrue();
        assertThat(sourceGeneratedType.isCustomizable()).isFalse();

    }

    @Test
    public void ofNameCategory() {
        assertThatThrownBy(() -> GeneratedFileType.of(null, Category.SOURCE))
                .isInstanceOf(NullPointerException.class);
        String sourceName = Category.SOURCE.name();
        assertThatThrownBy(() -> GeneratedFileType.of(sourceName, null))
                .isInstanceOf(NullPointerException.class);

        GeneratedFileType sourceGeneratedType = GeneratedFileType.of("name", Category.SOURCE);
        assertThat(sourceGeneratedType.category())
                .isEqualTo(Category.SOURCE);
        assertThat(sourceGeneratedType.name())
                .isEqualTo("name");
        assertThat(sourceGeneratedType.canHotReload()).isTrue();
        assertThat(sourceGeneratedType.isCustomizable()).isFalse();
    }

    @Test
    public void ofNameCategoryCanHotReload() {
        assertThatThrownBy(() -> GeneratedFileType.of(null, Category.SOURCE, true))
                .isInstanceOf(NullPointerException.class);
        String sourceName = Category.SOURCE.name();
        assertThatThrownBy(() -> GeneratedFileType.of(sourceName, null, true))
                .isInstanceOf(NullPointerException.class);

        GeneratedFileType sourceGeneratedType = GeneratedFileType.of("name", Category.SOURCE, false);
        assertThat(sourceGeneratedType.category())
                .isEqualTo(Category.SOURCE);
        assertThat(sourceGeneratedType.name())
                .isEqualTo("name");
        assertThat(sourceGeneratedType.canHotReload()).isFalse();
        assertThat(sourceGeneratedType.isCustomizable()).isFalse();
    }

    @Test
    public void ofNameCategoryCanHotReloadCustomizable() {
        assertThatThrownBy(() -> GeneratedFileType.of(null, Category.SOURCE, true, true))
                .isInstanceOf(NullPointerException.class);
        String sourceName = Category.SOURCE.name();
        assertThatThrownBy(() -> GeneratedFileType.of(sourceName, null, true, true))
                .isInstanceOf(NullPointerException.class);

        GeneratedFileType sourceGeneratedType = GeneratedFileType.of("name", Category.SOURCE, false, true);
        assertThat(sourceGeneratedType.category())
                .isEqualTo(Category.SOURCE);
        assertThat(sourceGeneratedType.name())
                .isEqualTo("name");
        assertThat(sourceGeneratedType.canHotReload()).isFalse();
        assertThat(sourceGeneratedType.isCustomizable()).isTrue();
    }

    @Test
    public void equalsOnValue() {
        GeneratedFileType mock = new MockGenericFileType();
        GeneratedFileType sameType = GeneratedFileType.of(mock.name(), mock.category(), mock.canHotReload(), mock.isCustomizable());
        GeneratedFileType differentType = GeneratedFileType.of(mock.name(), mock.category(), mock.canHotReload(), !mock.isCustomizable());

        // GeneratedFileType equals should only consider values and not actual class
        assertThat(sameType).isEqualTo(mock);
        assertThat(differentType).isNotEqualTo(mock);
    }

    private static class MockGenericFileType implements GeneratedFileType {

        @Override
        public String name() {
            return "name";
        }

        @Override
        public Category category() {
            return Category.INTERNAL_RESOURCE;
        }

        @Override
        public boolean canHotReload() {
            return true;
        }

        @Override
        public boolean isCustomizable() {
            return false;
        }
    }
}