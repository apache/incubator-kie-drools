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

package org.kie.kogito.runtime.tools.quarkus.extension.runtime.forms.impl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.kie.kogito.runtime.tools.quarkus.extension.runtime.forms.FormsStorage;
import org.kie.kogito.runtime.tools.quarkus.extension.runtime.forms.model.Form;
import org.kie.kogito.runtime.tools.quarkus.extension.runtime.forms.model.FormFilter;
import org.kie.kogito.runtime.tools.quarkus.extension.runtime.forms.model.FormInfo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class FormsStorageImplTest {

    FormsStorage formsStorage;
    private static final String FORM_NAME = "hiring_HRInterview";
    private static final String PARTIAL_FORM_NAME = "hiring";
    private static final String FORM_NAME_WITH_OUT_CONFIG = "hiring_HRInterviewWithoutConfig";

    @BeforeAll
    public void init() {
        URL formsFolder = Thread.currentThread().getContextClassLoader().getResource("forms");
        formsStorage = new FormsStorageImpl();
        ((FormsStorageImpl) formsStorage).initForTestCase(formsFolder);
    }

    @Test
    public void testGetFormsCount() {
        assertEquals(2, formsStorage.getFormsCount());
    }

    @Test
    public void testGetFormInfoList() {
        Collection<FormInfo> formInfosAll = formsStorage.getFormInfoList(null);
        assertEquals(2, formInfosAll.size());

        FormFilter filterEmpty = new FormFilter();
        filterEmpty.setNames(Collections.emptyList());
        Collection<FormInfo> formInfosAllEmptyFilter = formsStorage.getFormInfoList(filterEmpty);
        assertEquals(2, formInfosAll.size());

        FormFilter filter = new FormFilter();
        filter.setNames(Arrays.asList(FORM_NAME));

        Collection<FormInfo> formInfos = formsStorage.getFormInfoList(filter);
        assertEquals(1, formInfos.size());

        FormFilter partialFilter = new FormFilter();
        partialFilter.setNames(Arrays.asList(PARTIAL_FORM_NAME));

        Collection<FormInfo> formInfosPartial = formsStorage.getFormInfoList(partialFilter);
        assertEquals(2, formInfosPartial.size());
    }

    @Test
    public void testGetFormContent() throws IOException {
        Form formContent = formsStorage.getFormContent(FORM_NAME);
        assertNotNull(formContent);
        assertEquals(FORM_NAME, formContent.getName());
    }

    @Test
    public void testGetFormContentWithoutConfig() {
        assertThrows(FileNotFoundException.class, () -> formsStorage.getFormContent(FORM_NAME_WITH_OUT_CONFIG));
        assertThrows(FileNotFoundException.class, () -> formsStorage.getFormContent("ERROR"));
    }
}
