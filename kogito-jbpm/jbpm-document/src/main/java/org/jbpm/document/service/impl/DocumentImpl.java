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

package org.jbpm.document.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.jbpm.document.Document;
import org.jbpm.document.service.DocumentStorageService;
import org.kie.internal.utils.LazyLoaded;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "document-object")
public class DocumentImpl implements Document, LazyLoaded<DocumentStorageService> {

    private static final long serialVersionUID = -7422666286189013484L;

    private static final Logger logger = LoggerFactory.getLogger(DocumentImpl.class);
    
    private String identifier = "";
    private String name;
    private String link = "";
    private long size;
    private Date lastModified;
    private byte[] content;
    private Map<String, String> attributes;
    
    private transient DocumentStorageService service;

    public DocumentImpl() {
        // Setting default values for identifier && download link
        this.identifier = UUID.randomUUID().toString();
        this.attributes = new HashMap<String, String>();
    }

    public DocumentImpl(String identifier,
                        String name,
                        long size,
                        Date lastModified) {
        this.identifier = identifier;
        this.name = name;
        this.size = size;
        this.lastModified = lastModified;
        attributes = new HashMap<String, String>();
    }

    public DocumentImpl(String name,
                        long size,
                        Date lastModified) {
        this();
        this.name = name;
        this.size = size;
        this.lastModified = lastModified;
        attributes = new HashMap<String, String>();
    }

    public DocumentImpl(String identifier,
                        String name,
                        long size,
                        Date lastModified,
                        String link) {
        this.identifier = identifier;
        this.name = name;
        this.size = size;
        this.lastModified = lastModified;
        this.link = link;
        attributes = new HashMap<String, String>();
    }

    @Override
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setLink(String link) {
        this.link = link;
    }

    @Override
    public String getLink() {
        return this.link;
    }

    @Override
    public void setSize(long size) {
        this.size = size;
    }

    @Override
    public long getSize() {
        return size;
    }

    @Override
    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    @Override
    public Date getLastModified() {
        return lastModified;
    }

    @Override
    public String getAttribute(String attributeName) {
        return attributes.get(attributeName);
    }

    @Override
    public void addAttribute(String attributeName,
                             String attributeValue) {
        attributes.put(attributeName,
                       attributeValue);
    }

    @Override
    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }

    @Override
    public Map<String, String> getAttributes() {
        return attributes;
    }

    @Override
    public void setContent(byte[] content) {
        this.content = content;
        addAttribute(UPDATED_ATTRIBUTE, "true");
    }

    @Override
    public byte[] getContent() {
        load();
        
        return content;
    }

    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat(DOCUMENT_DATE_PATTERN);
        return name + PROPERTIES_SEPARATOR + size + PROPERTIES_SEPARATOR + ((lastModified != null) ? sdf.format(lastModified) : "") + PROPERTIES_SEPARATOR + identifier;
    }

    /*
     * lazy load support
     */
    
    @Override
    public void setLoadService(DocumentStorageService service) {
        this.service = service;
    }

    @Override
    public void load() {
        if (content == null && service != null && identifier != null) {
            content = service.loadContent(identifier);
        } else {
            logger.debug("Cannot load content due to missing service {} or identifier {}", service, identifier);
        }
    }
}
