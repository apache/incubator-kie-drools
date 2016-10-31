/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.compiler.kie.builder.impl;

import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.core.io.impl.BaseResource;
import org.kie.api.KieServices;
import org.kie.internal.builder.CompositeKnowledgeBuilder;
import org.kie.internal.builder.IncrementalResults;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.internal.builder.KieBuilderSet;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderError;
import org.kie.api.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.Arrays.asList;
import static org.drools.compiler.kie.builder.impl.KieBuilderImpl.filterFileInKBase;

public class KieBuilderSetImpl implements KieBuilderSet {

    private final KieBuilderImpl kieBuilder;
    private String[] files;

    private List<KnowledgeBuilderError> previousErrors;

    private final Map<String, Set<String>> resourcesWithErrors = new HashMap<String, Set<String>>();

    public KieBuilderSetImpl(KieBuilderImpl kieBuilder) {
        this.kieBuilder = kieBuilder;
        registerInitialErrors(kieBuilder);
    }

    private void registerInitialErrors(KieBuilderImpl kieBuilder) {
        previousErrors = new ArrayList<KnowledgeBuilderError>();
        InternalKieModule kieModule = (InternalKieModule) kieBuilder.getKieModuleIgnoringErrors();
        for (KieBaseModel kBaseModel : kieModule.getKieModuleModel().getKieBaseModels().values()) {
            KnowledgeBuilder kBuilder = kieModule.getKnowledgeBuilderForKieBase( kBaseModel.getName() );
            if (kBuilder != null) {
                for ( KnowledgeBuilderError error : kBuilder.getErrors() ) {
                    previousErrors.add(error);
                }
                resourcesWithErrors.put(kBaseModel.getName(), findResourcesWithErrors(kBuilder));
            }
        }
    }

    KieBuilderSetImpl setFiles(String[] files) {
        this.files = files;
        return this;
    }

    @Override
    public IncrementalResults build() {
        Collection<String> srcFiles = files != null ? asList(files) : kieBuilder.getModifiedResourcesSinceLastMark();
        Collection<String> filesToBuild = new ArrayList<String>();
        if ( srcFiles.isEmpty() ) {
            return new IncrementalResultsImpl();
        }
        kieBuilder.cloneKieModuleForIncrementalCompilation();
        for (String file : srcFiles) {
            String trgFile = kieBuilder.copySourceToTarget(file);
            if (trgFile != null) {
                filesToBuild.add(trgFile);
            }
        }
        IncrementalResults result = buildChanges(filesToBuild);
        files = null;
        kieBuilder.markSource();
        return result;
    }

    private Set<String> findResourcesWithErrors(KnowledgeBuilder kBuilder) {
        if ( kBuilder.hasErrors() ) {
            Set<String> resourcesWithErrors = new HashSet<String>();
            for ( KnowledgeBuilderError error : kBuilder.getErrors() ) {
                resourcesWithErrors.add(error.getResource().getSourcePath());
            }
            return resourcesWithErrors;
        }
        return Collections.emptySet();
    }

    private IncrementalResults buildChanges(Collection<String> filesToBuild) {
        List<KnowledgeBuilderError> currentErrors = new ArrayList<KnowledgeBuilderError>();

        InternalKieModule kieModule = (InternalKieModule) kieBuilder.getKieModuleIgnoringErrors();
        for (KieBaseModel kBaseModel : kieModule.getKieModuleModel().getKieBaseModels().values()) {
            KnowledgeBuilder kBuilder = kieModule.getKnowledgeBuilderForKieBase( kBaseModel.getName() );
            if (kBuilder == null) {
                continue;
            }
            CompositeKnowledgeBuilder ckbuilder = kBuilder.batch();

            boolean modified = false;
            KnowledgeBuilderImpl pkgBuilder = ((KnowledgeBuilderImpl)kBuilder);
            Set<String> wrongResources = resourcesWithErrors.get(kBaseModel.getName());
            for ( String resourceName : wrongResources ) {
                modified = pkgBuilder.removeObjectsGeneratedFromResource(new DummyResource(resourceName)) || modified;
                modified = addResource(ckbuilder, kBaseModel, kieModule, resourceName) || modified;
            }

            for (String file : filesToBuild) {
                if ( wrongResources.contains(file) ) {
                    modified = true;
                } else {
                    // remove the objects generated by the old Resource
                    modified = pkgBuilder.removeObjectsGeneratedFromResource(new DummyResource(file)) || modified;
                    // add the modified Resource
                    modified = addResource(ckbuilder, kBaseModel, kieModule, file) || modified;
                }
            }

            if (modified) {
                ckbuilder.build();
                resourcesWithErrors.put(kBaseModel.getName(), findResourcesWithErrors(kBuilder));
                if ( kBuilder.hasErrors() ) {
                    currentErrors.addAll(kBuilder.getErrors());
                    kBuilder.undo();
                } else {
                    KieServices.Factory.get().getRepository().addKieModule( kieModule );
                    kieBuilder.updateKieModuleMetaInfo();
                }
            }
        }

        IncrementalResultsImpl results = getIncrementalResults(currentErrors);
        previousErrors = currentErrors;
        return results;
    }

    private IncrementalResultsImpl getIncrementalResults(List<KnowledgeBuilderError> currentErrors) {
        IncrementalResultsImpl results = new IncrementalResultsImpl();
        for (KnowledgeBuilderError error : currentErrors) {
            if (!previousErrors.remove(error)) {
                results.addMessage(error);
            }
        }
        for (KnowledgeBuilderError error : previousErrors) {
            results.removeMessage(error);
        }
        return results;
    }

    private boolean addResource( CompositeKnowledgeBuilder ckbuilder,
                                 KieBaseModel kieBaseModel,
                                 InternalKieModule kieModule,
                                 String resourceName ) {
        return !resourceName.endsWith(".properties") &&
               filterFileInKBase(kieModule, kieBaseModel, resourceName) &&
               kieModule.addResourceToCompiler(ckbuilder, kieBaseModel, resourceName);
    }

    public static class DummyResource extends BaseResource {
        public DummyResource(String resourceName) {
            setSourcePath( decode( resourceName ) );
        }

        @Override
        public URL getURL() throws IOException {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean hasURL() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isDirectory() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Collection<Resource> listResources() {
            throw new UnsupportedOperationException();
        }

        @Override
        public long getLastModified() {
            throw new UnsupportedOperationException();
        }

        @Override
        public long getLastRead() {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getEncoding() {
            throw new UnsupportedOperationException();
        }

        @Override
        public InputStream getInputStream() throws IOException {
            throw new UnsupportedOperationException();
        }

        @Override
        public Reader getReader() throws IOException {
            throw new UnsupportedOperationException();
        }

        private String decode( final String resourceName ) {
            try {
                return URLDecoder.decode( resourceName, "UTF-8" );
            } catch ( UnsupportedEncodingException e ) {
                return resourceName;
            }
        }
    }
}
