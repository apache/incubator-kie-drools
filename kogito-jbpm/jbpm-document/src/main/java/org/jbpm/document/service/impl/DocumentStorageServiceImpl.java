/**
 * Copyright (C) 2012 Red Hat, Inc. and/or its affiliates
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
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.jbpm.document.Document;
import org.jbpm.document.service.DocumentStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

/**
 * This a Sample Implementation of the DocumentStorageService saves the uploaded files on the File System on a folder (by default /docs)
 * and return the complete path to the file that will be stored in the form field property.
 *
 * Check that the user that is running the app has write permissions on the storage folder.
 */
public class DocumentStorageServiceImpl implements DocumentStorageService {

    private Logger log = LoggerFactory.getLogger(DocumentStorageServiceImpl.class);

    /**
     * This is the root folder where the files are going to be stored, please check that the user that is running the app has permissions to read/write inside
     */
    private String storagePath = ".docs";

    public DocumentStorageServiceImpl( String storagePath ) {
        this.storagePath = storagePath;
    }

    public DocumentStorageServiceImpl() {

    }

    @Override
    public Document buildDocument( String name, long size, Date lastModified, Map<String, String> params ) {
        String identifier = generateUniquePath();

        String appURL = params.get("app.url");

        if (appURL == null) appURL = "";

        if (!appURL.isEmpty() && !appURL.endsWith("/")) appURL += "/";

        // Generating a default download link, don't use this donwloader in real environments use it as an example
        String link = appURL + "Controller?_fb=fdch&_fp=download&content=" + identifier;

        return new DocumentImpl( identifier, name, size, lastModified, link );
    }

    @Override
    public Document saveDocument(Document document, byte[] content) {
        if (document == null || StringUtils.isEmpty(document.getIdentifier())) return null;

        File destination = getFileByPath( document.getIdentifier() + "/" + document.getName() );

        try {
            FileUtils.writeByteArrayToFile(destination, content);
        } catch (IOException e) {
            log.error("Error writing file {}: {}", document.getName(), e);
        }

        return document;
    }

    @Override
    public Document getDocument(String id) {
        File file = getFileByPath( id );

        if (file.exists() && !file.isFile() && !ArrayUtils.isEmpty(file.listFiles())) {
            try {
                File destination = file.listFiles()[0];
                Document doc = new DocumentImpl(id, destination.getName(), destination.length(), new Date(destination.lastModified()));
                doc.setContent(FileUtils.readFileToByteArray(destination));
                return doc;
            } catch (IOException e) {
                log.error("Error loading document '{}': {}", id, e);
            }
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
            File rootDoc = getDocumentContent(doc);
            if (!ArrayUtils.isEmpty( rootDoc.listFiles() )) return deleteFile( rootDoc.listFiles()[0] );
            return deleteFile(rootDoc);
        }
        return true;
    }

    public File getDocumentContent(Document doc) {
        if (doc != null) {
            return getFileByPath( doc.getIdentifier() );
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
            log.error("Error deleting file: ", e);
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

            parent = getFileByPath( destinationPath );

        } while ( parent.exists() );

        return destinationPath;
    }

    protected File getFileByPath( String path ) {
        return new File( storagePath + "/" + path );
    }
}
