/**
 * Copyright (C) 2012 JBoss Inc
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
package org.jbpm.document.service.impl;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.jbpm.document.Document;
import org.jbpm.document.service.FileStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * This a Sample Implementation of the FileStorageService saves the uploaded files on the File System on a folder (by default /docs)
 * and return the complete path to the file that will be stored in the form field property.
 *
 * Check that the user that is running the app has write permissions on the storage folder.
 */
public class FileStorageServiceImpl implements FileStorageService {

    private Logger log = LoggerFactory.getLogger(FileStorageServiceImpl.class);

    /**
     * This is the root folder where the files are going to be stored, please check that the user that is running the app has permissions to read/write inside
     */
    private String storagePath = ".docs";

    @Override
    public Document saveDocument(File file) {
        try {
            String destinationPath = generateUniquePath(file.getName());

            File destination = new File(destinationPath);

            FileUtils.copyFile(file, destination);

            return new DocumentImpl(destinationPath, file.getName(), file.length(), new Date(destination.lastModified()));
        } catch (Exception ex) {

        }
        return null;
    }

    @Override
    public Document getDocument(String id) {
        File file = new File(id);

        if (file.exists()) {
            return new DocumentImpl(id, file.getName(), file.length(), new Date(file.lastModified()));
        }

        return null;
    }

    @Override
    public boolean deleteDocument(String id) {
        if (StringUtils.isEmpty(id)) return true;
        return deleteDocument(getDocument(id));
    }

    @Override
    public boolean deleteDocument(Document doc) {
        if (doc != null) {
            return deleteFile(getDocumentContent(doc));
        }
        return true;
    }

    @Override
    public File getDocumentContent(Document doc) {
        if (doc != null) {
            return new File(doc.getIdentifier());
        }
        return null;
    }

    protected boolean deleteFile(File file) {
        try {
            if (file != null) {
                if (file.isFile()) {
                    file.delete();
                    return deleteFile(file.getParentFile());
                } else {
                    if (!file.getName().equals(storagePath)) {
                        String[] list = file.list();
                        if (list == null || list.length == 0) {
                            file.delete();
                            return deleteFile(file.getParentFile());
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.warn("Error deleting file: ", e);
            return false;
        }
        return true;
    }

    /**
     * Generates a random path to store the file to avoid overwritting files with the same name
     * @param fileName The fileName that is going to be stored
     * @return A String
     */
    protected String generateUniquePath(String fileName) {
        String destinationPath = storagePath + "/";

        destinationPath += UUID.randomUUID().toString();
        destinationPath = destinationPath.replaceAll("-", "/");
        if (!destinationPath.endsWith("/")) destinationPath += "/";

        return destinationPath + "/" + fileName;
    }

    private class DocumentImpl implements Document {
        private String identifier;
        private String name;
        private long size;
        private Date lastModified;

        private Map<String, String> attributes;

        public DocumentImpl(String identifier, String name, long size, Date lastModified) {
            this.identifier = identifier;
            this.name = name;
            this.size = size;
            this.lastModified = lastModified;

            attributes = new HashMap<String, String>();
        }

        public String getIdentifier() {
            return identifier;
        }

        public String getName() {
            return name;
        }

        public long getSize() {
            return size;
        }

        public Date getLastModified() {
            return lastModified;
        }

        public String getAttribute(String attributeName) {
            return attributes.get(attributeName);
        }

        public void addAttribute(String attributeName, String attributeValue) {
            attributes.put(attributeName, attributeValue);
        }

        @Override
        public String toString() {
            return "Document{" +
                    "identifier='" + identifier + '\'' +
                    ", name='" + name + '\'' +
                    ", size=" + size +
                    ", lastModified=" + lastModified +
                    ", attributes=" + attributes +
                    '}';
        }
    }
}
