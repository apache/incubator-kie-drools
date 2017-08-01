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

import java.util.ServiceLoader;

import org.jbpm.document.service.impl.DocumentStorageServiceImpl;

/**
 * Responsible for discovery and delivery of <code>DocumentStorageServiceProvider</code> implementations.
 * Default behavior is that is uses ServiceLoader for discovery and if no found it returns default/sample implementation
 */
public class DocumentStorageServiceProvider {

    private static final ServiceLoader<DocumentStorageService> storageServices = ServiceLoader.load(DocumentStorageService.class);
    private static DocumentStorageServiceProvider INSTANCE = new DocumentStorageServiceProvider();
    
    private DocumentStorageService documentStorageService;
    
    private DocumentStorageServiceProvider() {
        discover();
    }
    
    private synchronized void discover() {
        for (DocumentStorageService foundService : storageServices) {
            if (documentStorageService != null) {                
                throw new RuntimeException("Ambiguous DocumentStorageService discovery, found more than one implementation");
            }
            documentStorageService = foundService;
        }
        
        if (documentStorageService == null) {
            documentStorageService = new DocumentStorageServiceImpl();
        }
    }
    
    public static DocumentStorageServiceProvider get() {
        return INSTANCE;
    }
    
    public DocumentStorageService getStorageService() {
        return INSTANCE.documentStorageService;
    }
}
