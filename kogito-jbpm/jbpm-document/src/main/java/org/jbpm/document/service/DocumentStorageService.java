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

package org.jbpm.document.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.jbpm.document.Document;

/**
 * Simple storage service definition
 */
public interface DocumentStorageService {

    /**
     * Generates a Document instance.
     * @param name          The document name
     * @param size          The document size
     * @param lastModified  The lastModified date of the document
     * @param params    A Map<String, String> containing the params to create the document.
     * @return
     */
    Document buildDocument( String name, long size, Date lastModified, Map<String, String> params );

    /**
     * Method to store the uploaded file on the system
     * @param document      The document to store the content
     * @param content       The document content
     * @return              A Document
     */
    Document saveDocument(Document document, byte[] content);

    /**
     * Method to obtain a File for the given storage id
     * @param id            The Document id to obtain the Document
     * @return              The java.io.File identified with the id
     */
    Document getDocument(String id);
    
    /**
     * Loads document content 
     * @param id unique id of the document
     * @return loaded document's content
     */
    byte[] loadContent(String id);

    /**
     * Deletes the File identified by the given id
     * @param id            The Document id to delete
     * @return              true if it was possible to remove, false if not
     */
    boolean deleteDocument(String id);

    /**
     * Deletes the File identified by the given id
     * @param document      The Document to delete
     * @return              true if it was possible to remove, false if not
     */
    boolean deleteDocument(Document document);
    
    /**
     * Lists available document with paging support.
     * @param page page to be displayed
     * @param pageSize number of elements to return
     * @return
     */
    List<Document> listDocuments(Integer page, Integer pageSize);
}
