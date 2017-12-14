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
package org.jbpm.document;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

public interface Document extends Serializable {

    public static final String DOCUMENT_DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static final String PROPERTIES_SEPARATOR = "####";
    public static final String UPDATED_ATTRIBUTE = "_UPDATED_";

    void setIdentifier(String identifier);

    String getIdentifier();

    void setName(String name);

    String getName();

    void setSize(long size);

    long getSize();

    void setLastModified(Date lastModified);

    Date getLastModified();

    void setLink(String link);

    String getLink();

    String getAttribute(String attributeName);

    void addAttribute(String attributeName, String attributeValue);

    void setAttributes(Map<String, String> attributes);

    Map<String, String> getAttributes();

    public void setContent(byte[] content);

    byte[] getContent();
}