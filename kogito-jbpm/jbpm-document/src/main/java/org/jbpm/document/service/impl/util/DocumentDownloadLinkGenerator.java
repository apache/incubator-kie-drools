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

public class DocumentDownloadLinkGenerator {

    public static final String TEMPLATE_ID_TOKEN = "{serverTemplateId}";
    public static final String DOCUMENT_ID_TOKEN = "{documentIdentifier}";

    private static final String PATTERN = "jbpm/documents?templateid=" + TEMPLATE_ID_TOKEN + "&docid=" + DOCUMENT_ID_TOKEN;

    public static String generateDownloadLink(String serverTemplateId,
                                              String documentIdentifier) {
        if (serverTemplateId == null) {
            throw new IllegalArgumentException("ServerTemplateId cannot be null");
        }

        if (documentIdentifier == null) {
            throw new IllegalArgumentException("DocumentIdentifier cannot be null");
        }
        return PATTERN.replace(TEMPLATE_ID_TOKEN,
                               serverTemplateId).replace(DOCUMENT_ID_TOKEN,
                                                         documentIdentifier);
    }
}
