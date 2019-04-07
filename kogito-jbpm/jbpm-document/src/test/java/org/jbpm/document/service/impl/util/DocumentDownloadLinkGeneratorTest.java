/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.document.service.impl.util;

import org.assertj.core.api.Assertions;
import org.junit.Test;

public class DocumentDownloadLinkGeneratorTest {

    private static final String TEMPLATE_ID = "myTemplateId";

    private static final String DOC_ID = "myDocId";

    @Test
    public void testLinkGenerationFailure() {
        Assertions.assertThatThrownBy(() -> DocumentDownloadLinkGenerator.generateDownloadLink(null,
                                                                                               DOC_ID))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ServerTemplateId cannot be null");

        Assertions.assertThatThrownBy(() -> DocumentDownloadLinkGenerator.generateDownloadLink(TEMPLATE_ID,
                                                                                               null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("DocumentIdentifier cannot be null");
    }

    @Test
    public void testLinkGeneration() {
        Assertions.assertThat(DocumentDownloadLinkGenerator.generateDownloadLink(TEMPLATE_ID,
                                                                                 DOC_ID))
                .doesNotContain(DocumentDownloadLinkGenerator.TEMPLATE_ID_TOKEN)
                .doesNotContain(DocumentDownloadLinkGenerator.DOCUMENT_ID_TOKEN)
                .contains(TEMPLATE_ID)
                .contains(DOC_ID);
    }
}
