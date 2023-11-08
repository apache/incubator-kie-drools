/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.compiler.builder.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import org.drools.compiler.builder.impl.resources.ResourceHandler;
import org.drools.compiler.lang.descr.CompositePackageDescr;
import org.drools.drl.ast.descr.PackageDescr;
import org.drools.drl.parser.lang.dsl.DefaultExpander;
import org.kie.api.builder.ReleaseId;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.internal.builder.KnowledgeBuilderResult;

public class PackageDescrBuilder {
    private final BuildResultCollector buildResultCollector;
    private ResourceHandlerManager handlerManager;
    private RuntimeException buildException;

    public PackageDescrBuilder(KnowledgeBuilderConfigurationImpl configuration, ReleaseId releaseId, Supplier<DefaultExpander> dslExpander) {
        this.buildResultCollector = new BuildResultCollectorImpl();
        this.handlerManager = new ResourceHandlerManager(configuration,releaseId,dslExpander);
    }

    public Collection<CompositePackageDescr> build(Map<ResourceType, List<CompositeKnowledgeBuilderImpl.ResourceDescr>> resourcesByType) {
        Map<String, CompositePackageDescr> packages = new HashMap<>();

        for (ResourceType type : this.handlerManager.getOrderedResourceTypes()) {
            ResourceHandler mapper = this.handlerManager.handlerForType(type);
            buildResource(packages, type, mapper, resourcesByType);
        }

        return packages.values();
    }

    public Collection<? extends KnowledgeBuilderResult> getResults() {
        return buildResultCollector.getAllResults();
    }

    public RuntimeException getBuildException() {
        return buildException;
    }

    private void buildResource(
            Map<String, CompositePackageDescr> packages,
            ResourceType resourceType, ResourceHandler mapper,
            Map<ResourceType, List<CompositeKnowledgeBuilderImpl.ResourceDescr>> resourcesByType) {
        List<CompositeKnowledgeBuilderImpl.ResourceDescr> resourceDescrs = resourcesByType.remove(resourceType);
        if (resourceDescrs != null) {
            for (CompositeKnowledgeBuilderImpl.ResourceDescr resourceDescr : resourceDescrs) {
                try {
                    PackageDescr packageDescr = mapper.process(resourceDescr.resource, resourceDescr.configuration);
                    mapper.getResults().forEach(buildResultCollector::addBuilderResult);
                    registerPackageDescr(resourceDescr, packages, resourceDescr.resource, packageDescr);
                } catch (RuntimeException e) {
                    if (buildException == null) {
                        buildException = e;
                    }
                } catch (Exception e) {
                    if (buildException == null) {
                        buildException = new RuntimeException( e );
                    }
                }
            }
        }
    }


    private void registerPackageDescr(
            CompositeKnowledgeBuilderImpl.ResourceDescr resourceDescr, Map<String, CompositePackageDescr> packages, Resource resource, PackageDescr packageDescr) {
        if (packageDescr == null) { return; }
        CompositePackageDescr compositePackageDescr = packages.get(packageDescr.getNamespace());
        if (compositePackageDescr == null) {
            compositePackageDescr = makeCompositePackageDescr(resource, packageDescr);
            packages.put(packageDescr.getNamespace(), compositePackageDescr);
        } else {
            compositePackageDescr.addPackageDescr(resource, packageDescr);
        }
        compositePackageDescr.addFilter( resourceDescr.getFilter() );
    }

    private CompositePackageDescr makeCompositePackageDescr(Resource resource, PackageDescr packageDescr) {
        CompositePackageDescr compositePackageDescr;
        compositePackageDescr = packageDescr instanceof CompositePackageDescr ?
                ( (CompositePackageDescr) packageDescr) :
                new CompositePackageDescr(resource, packageDescr);
        return compositePackageDescr;
    }
}
