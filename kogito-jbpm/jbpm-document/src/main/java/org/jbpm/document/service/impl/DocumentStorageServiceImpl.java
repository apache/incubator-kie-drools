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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.jbpm.document.Document;
import org.jbpm.document.service.DocumentStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This a Sample Implementation of the DocumentStorageService saves the uploaded files on the File System on a folder (by default /docs)
 * and return the complete path to the file that will be stored in the form field property.
 * <p>
 * Check that the user that is running the app has write permissions on the storage folder.
 */
public class DocumentStorageServiceImpl implements DocumentStorageService {

    private Logger log = LoggerFactory.getLogger(DocumentStorageServiceImpl.class);

    /**
     * This is the root folder where the files are going to be stored, please check that the user that is running the app has permissions to read/write inside
     */
    private String storagePath = System.getProperty("org.jbpm.document.storage",
                                                    ".docs");
    private File storageFile;

    public DocumentStorageServiceImpl(String storagePath) {
        this.storagePath = storagePath;
        this.storageFile = new File(storagePath);
    }

    public DocumentStorageServiceImpl() {
        this.storageFile = new File(this.storagePath);
    }

    @Override
    public Document buildDocument(String name,
                                  long size,
                                  Date lastModified,
                                  Map<String, String> params) {
        String identifier = generateUniquePath();

        return new DocumentImpl(identifier,
                                name,
                                size,
                                lastModified);
    }

    @Override
    public Document saveDocument(Document document,
                                 byte[] content) {

        if (StringUtils.isEmpty(document.getIdentifier())) {
            document.setIdentifier(generateUniquePath());
        }

        File destination = getFileByPath(document.getIdentifier() + File.separator + document.getName());

        try {
            FileUtils.writeByteArrayToFile(destination,
                                           content);
            destination.getParentFile().setLastModified(document.getLastModified().getTime());
            destination.setLastModified(document.getLastModified().getTime());
        } catch (IOException e) {
            log.error("Error writing file {}: {}",
                      document.getName(),
                      e);
        }

        return document;
    }

    @Override
    public Document getDocument(String id) {
        File file = getFileByPath(id);

        if (file.exists() && !file.isFile() && !ArrayUtils.isEmpty(file.listFiles())) {
            try {
                File destination = file.listFiles()[0];
                DocumentImpl doc = new DocumentImpl(id,
                                                destination.getName(),
                                                destination.length(),
                                                new Date(destination.lastModified()));
                doc.setLoadService(this);
                return doc;
            } catch (Exception e) {
                log.error("Error loading document '{}': {}",
                          id,
                          e);
            }
        }

        return null;
    }

    @Override
    public boolean deleteDocument(String id) {
        if (StringUtils.isEmpty(id)) {
            return true;
        }
        return deleteDocument(getDocument(id));
    }

    @Override
    public boolean deleteDocument(Document doc) {
        if (doc != null) {
            File rootDoc = getDocumentContent(doc);
            if (!ArrayUtils.isEmpty(rootDoc.listFiles())) {
                return deleteFile(rootDoc.listFiles()[0]);
            }
            return deleteFile(rootDoc);
        }
        return true;
    }

    public File getDocumentContent(Document doc) {
        if (doc != null) {
            return getFileByPath(doc.getIdentifier());
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
            log.error("Error deleting file: ",
                      e);
            return false;
        }
        return true;
    }

    /**
     * Generates a random path to store the file to avoid overwritting files with the same name
     * @return A String containging the path where the document is going to be stored.
     */
    protected String generateUniquePath() {
        File parent;
        String destinationPath;
        do {
            destinationPath = UUID.randomUUID().toString();

            parent = getFileByPath(destinationPath);
        } while (parent.exists());

        return destinationPath;
    }

    protected File getFileByPath(String path) {
        return new File(storagePath + File.separator + path);
    }

    @Override
    public List<Document> listDocuments(Integer page, Integer pageSize) {
        List<Document> listOfDocs = new ArrayList<Document>();

        int startIndex = page * pageSize;
        int endIndex = startIndex + pageSize;

        File[] documents = storageFile.listFiles();

        if (documents == null) { // happens when the docs folder doesn't exist
            return Collections.emptyList();
        }

        // make sure the endIndex is not bigger then amount of files
        if (documents.length < endIndex) {
            endIndex = documents.length;
        }
        Arrays.sort(documents,
                Comparator.comparingLong(File::lastModified)
        );
        for (int i = startIndex; i < endIndex; i++) {
            Document doc = getDocument(documents[i].getName());
            listOfDocs.add(doc);
        }
        return listOfDocs;
    }

    @Override
    public byte[] loadContent(String id) {
        File file = getFileByPath(id);

        if (file.exists() && !file.isFile() && !ArrayUtils.isEmpty(file.listFiles())) {
            try {
                File destination = file.listFiles()[0];
                return FileUtils.readFileToByteArray(destination);
            } catch (IOException e) {
                log.error("Unable to laod content due to {}", e.getMessage(), e);
            }
        } 
        
        return null;
    }
}
