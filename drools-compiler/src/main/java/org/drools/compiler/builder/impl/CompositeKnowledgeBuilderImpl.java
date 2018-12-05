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

package org.drools.compiler.builder.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.drools.compiler.compiler.BPMN2ProcessFactory;
import org.drools.compiler.compiler.CMMNCaseFactory;
import org.drools.compiler.lang.descr.CompositePackageDescr;
import org.drools.compiler.lang.descr.PackageDescr;
import org.drools.core.builder.conf.impl.JaxbConfigurationImpl;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceConfiguration;
import org.kie.api.io.ResourceType;
import org.kie.api.io.ResourceWithConfiguration;
import org.kie.internal.builder.ChangeType;
import org.kie.internal.builder.CompositeKnowledgeBuilder;
import org.kie.internal.builder.ResourceChange;
import org.kie.internal.builder.ResourceChangeSet;
import org.kie.internal.io.ResourceWithConfigurationImpl;

public class CompositeKnowledgeBuilderImpl implements CompositeKnowledgeBuilder {

    private final KnowledgeBuilderImpl kBuilder;

    private final Map<ResourceType, List<ResourceDescr>> resourcesByType = new HashMap<>();

    private RuntimeException buildException = null;

    public ResourceType currentType = null;

    public CompositeKnowledgeBuilderImpl(KnowledgeBuilderImpl kBuilder) {
        this.kBuilder = kBuilder;
    }

    public CompositeKnowledgeBuilder type(ResourceType type) {
        currentType = type;
        return this;
    }

    public CompositeKnowledgeBuilder add(Resource resource) {
        if (currentType == null) {
            throw new RuntimeException("You must declare the type of the resource");
        }
        return add(resource, currentType);
    }

    public CompositeKnowledgeBuilder add(Resource resource, ResourceType type) {
        return add(resource, type, resource.getConfiguration());
    }

    public CompositeKnowledgeBuilder add(Resource resource, ResourceType type, ResourceChangeSet changes) {
        return add(resource, type, resource.getConfiguration(), changes);
    }

    public CompositeKnowledgeBuilder add(Resource resource, ResourceType type, ResourceConfiguration configuration) {
        return add(resource, type, configuration, null);
    }

    public CompositeKnowledgeBuilder add(Resource resource, ResourceType type, ResourceConfiguration configuration, ResourceChangeSet changes) {
        ResourceDescr resourceDescr = new ResourceDescr(configuration, resource, changes);
        List<ResourceDescr> resourceDescrs = this.resourcesByType.get(type);
        if (resourceDescrs == null) {
            resourceDescrs = new ArrayList<>();
            resourcesByType.put(type, resourceDescrs);
        }
        resourceDescrs.add(resourceDescr);
        return this;
    }

    private List<Resource> getResources() {
        List<Resource> resources = new ArrayList<>();
        for (List<ResourceDescr> resourceDescrs : resourcesByType.values()) {
            for (ResourceDescr resourceDescr : resourceDescrs) {
                resources.add(resourceDescr.resource);
            }
        }
        return resources;
    }

    public void build() {
        build(true);
    }

    public void build(boolean buildRules) {
        buildException = null;
        kBuilder.registerBuildResources(getResources());
        buildResources();
        if(buildRules) {
            kBuilder.buildPackages(buildPackageDescr());
        } else {
            kBuilder.buildPackagesWithoutRules(buildPackageDescr() );
        }
        buildProcesses();
        buildOthers();
        resourcesByType.clear();
        if (buildException != null) {
            throw buildException;
        }
    }

    private void buildProcesses() {
    	buildResourceType(ResourceBuilder.BPMN2_RESOURCE_BUILDER, ResourceType.BPMN2);
    	buildResourceType(ResourceBuilder.CMMN_RESOURCE_BUILDER, ResourceType.CMMN);
    }

    private void buildResources() {
        buildResourceType(ResourceBuilder.DSL_RESOURCE_BUILDER, ResourceType.DSL);
        buildResourceType(ResourceBuilder.DRF_RESOURCE_BUILDER, ResourceType.DRF);
        buildResourceType(ResourceBuilder.PKG_RESOURCE_BUILDER, ResourceType.PKG);
        buildResourceType(ResourceBuilder.CHANGE_SET_RESOURCE_BUILDER, ResourceType.CHANGE_SET);
        buildResourceType(ResourceBuilder.XSD_RESOURCE_BUILDER, ResourceType.XSD);
        buildResourceType(ResourceBuilder.SCD_RESOURCE_BUILDER, ResourceType.SCARD);
        buildResourceType(ResourceBuilder.GSCD_RESOURCE_BUILDER, ResourceType.SCGD);
    }

    private void buildResourceType(ResourceBuilder resourceBuilder, ResourceType resourceType) {
        List<ResourceDescr> resourcesByType = this.resourcesByType.remove(resourceType);
        if (resourcesByType != null) {
            for (ResourceDescr resourceDescr : resourcesByType) {
                try {
                    kBuilder.setAssetFilter(resourceDescr.getFilter());
                    resourceBuilder.build( kBuilder, resourceDescr );
                } catch (RuntimeException e) {
                    if (buildException == null) {
                        buildException = e;
                    }
                } catch (Exception e) {
                    if (buildException == null) {
                        buildException = new RuntimeException( e );
                    }
                } finally{
                    kBuilder.setAssetFilter(null);
                }
            }
        }
    }

    private void buildOthers() {
        try {
            for (Map.Entry<ResourceType, List<ResourceDescr>> entry : resourcesByType.entrySet()) {
                List<ResourceWithConfiguration> rds = entry.getValue().stream().map(CompositeKnowledgeBuilderImpl::descrToResourceWithConfiguration).collect(Collectors.toList());
                kBuilder.addPackageForExternalType(entry.getKey(), rds);
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException( e );
        }
    }

    private static ResourceWithConfiguration descrToResourceWithConfiguration(ResourceDescr rd) {
        return new ResourceWithConfigurationImpl(rd.resource,
                                                 rd.configuration,
                                                 kb -> ((KnowledgeBuilderImpl) kb).setAssetFilter(rd.getFilter()),
                                                 kb -> ((KnowledgeBuilderImpl) kb).setAssetFilter(null));
    }

    private Collection<CompositePackageDescr> buildPackageDescr() {
        Map<String, CompositePackageDescr> packages = new HashMap<>();
        buildResource(packages, ResourceType.DRL, ResourceToPkgDescrMapper.DRL_TO_PKG_DESCR);
        buildResource(packages, ResourceType.GDRL,ResourceToPkgDescrMapper. DRL_TO_PKG_DESCR);
        buildResource(packages, ResourceType.RDRL, ResourceToPkgDescrMapper.DRL_TO_PKG_DESCR);
        buildResource(packages, ResourceType.DESCR, ResourceToPkgDescrMapper.DRL_TO_PKG_DESCR);
        buildResource(packages, ResourceType.DSLR, ResourceToPkgDescrMapper.DSLR_TO_PKG_DESCR);
        buildResource(packages, ResourceType.RDSLR, ResourceToPkgDescrMapper.DSLR_TO_PKG_DESCR);
        buildResource(packages, ResourceType.XDRL, ResourceToPkgDescrMapper.XML_TO_PKG_DESCR);
        buildResource(packages, ResourceType.DTABLE, ResourceToPkgDescrMapper.DTABLE_TO_PKG_DESCR);
        buildResource(packages, ResourceType.TDRL, ResourceToPkgDescrMapper.DRL_TO_PKG_DESCR);
        buildResource(packages, ResourceType.TEMPLATE, ResourceToPkgDescrMapper.TEMPLATE_TO_PKG_DESCR);
        buildResource(packages, ResourceType.GDST, ResourceToPkgDescrMapper.GUIDED_DTABLE_TO_PKG_DESCR);
        this.resourcesByType.remove(ResourceType.DRT); // drt is a template for dtables but doesn't have to be built on its own
        return packages.values();
    }

    private void buildResource(Map<String, CompositePackageDescr> packages, ResourceType resourceType, ResourceToPkgDescrMapper mapper) {
        List<ResourceDescr> resourcesByType = this.resourcesByType.remove(resourceType);
        if (resourcesByType != null) {
            for (ResourceDescr resourceDescr : resourcesByType) {
                try {
                    registerPackageDescr(resourceDescr, packages, resourceDescr.resource, mapper.map(kBuilder, resourceDescr));
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

    private void registerPackageDescr(ResourceDescr resourceDescr, Map<String, CompositePackageDescr> packages, Resource resource, PackageDescr packageDescr) {
        if (packageDescr != null) {
            CompositePackageDescr compositePackageDescr = packages.get(packageDescr.getNamespace());
            if (compositePackageDescr == null) {
                compositePackageDescr = packageDescr instanceof CompositePackageDescr ?
                                        ( (CompositePackageDescr) packageDescr ) :
                                        new CompositePackageDescr(resource, packageDescr);
                packages.put(packageDescr.getNamespace(), compositePackageDescr);
            } else {
                compositePackageDescr.addPackageDescr(resource, packageDescr);
            }
            compositePackageDescr.addFilter( resourceDescr.getFilter() );
        }
    }

    private static class ResourceDescr {
        final Resource resource;
        final ResourceConfiguration configuration;
        final ResourceChangeSet changes;
        final Map<String, ResourceChange> changeMap;
        final ChangeType globalChangeType;

        private ResourceDescr(ResourceConfiguration configuration, Resource resource, ResourceChangeSet changes) {
            this.configuration = configuration;
            this.resource = resource;
            this.changes = changes;
            if ( changes != null ) {
                changeMap = new HashMap<>();
                if (!changes.getChanges().isEmpty()) {
                    for ( ResourceChange c : changes.getChanges() ) {
                        changeMap.put( assetId( c.getType(), c.getName() ), c );
                    }
                    globalChangeType = null;
                } else {
                    globalChangeType = changes.getChangeType();
                }
            } else {
                changeMap = null;
                globalChangeType = null;
            }
        }

        public KnowledgeBuilderImpl.AssetFilter getFilter() {
            return changeMap == null ? null : this.new ChangeSetAssetFilter();
        }

        private class ChangeSetAssetFilter implements KnowledgeBuilderImpl.AssetFilter {
            @Override
            public Action accept(ResourceChange.Type type, String pkgName, String assetName) {
                if (globalChangeType != null) {
                    return toFilterAction( globalChangeType );
                }
                ResourceChange change = changeMap.get( assetId(type, assetName) );
                return change != null ? toFilterAction( change.getChangeType() ) : Action.DO_NOTHING;
            }

            private Action toFilterAction( ChangeType changeType ) {
                switch (changeType) {
                    case ADDED: return Action.ADD;
                    case REMOVED: return Action.REMOVE;
                    case UPDATED: return Action.UPDATE;
                }
                return Action.DO_NOTHING;
            }
        }

        private String assetId(ResourceChange.Type type, String assetName) {
            return type + "_" + assetName;
        }
    }

    @FunctionalInterface
    private interface ResourceBuilder {
        void build(KnowledgeBuilderImpl kBuilder, ResourceDescr resourceDescr) throws Exception;

        ResourceBuilder DSL_RESOURCE_BUILDER = ( kBuilder, resourceDescr ) -> kBuilder.addDsl( resourceDescr.resource );


        ResourceBuilder XSD_RESOURCE_BUILDER = ( kBuilder, resourceDescr ) -> {
            if (resourceDescr.configuration instanceof JaxbConfigurationImpl) {
                // if the xsd file doesn't have a jaxb configuration it doesn't belong to the kprojact and then can be skipped
                kBuilder.addPackageFromXSD( resourceDescr.resource, (JaxbConfigurationImpl) resourceDescr.configuration );
            }
        };

        ResourceBuilder SCD_RESOURCE_BUILDER = ( kBuilder, resourceDescr ) -> kBuilder.addPackageFromScoreCard(resourceDescr.resource, resourceDescr.configuration);

        ResourceBuilder GSCD_RESOURCE_BUILDER = ( kBuilder, resourceDescr ) -> kBuilder.addPackageFromGuidedScoreCard(resourceDescr.resource);

        ResourceBuilder CHANGE_SET_RESOURCE_BUILDER = ( kBuilder, resourceDescr ) -> kBuilder.addPackageFromChangeSet( resourceDescr.resource);

        ResourceBuilder PKG_RESOURCE_BUILDER = ( kBuilder, resourceDescr ) -> kBuilder.addPackageFromInputStream(resourceDescr.resource );

        ResourceBuilder BPMN2_RESOURCE_BUILDER = ( kBuilder, resourceDescr ) -> {
            kBuilder.addKnowledgeResource( resourceDescr.resource, ResourceType.BPMN2, resourceDescr.configuration );
        };
        
        ResourceBuilder CMMN_RESOURCE_BUILDER = ( kBuilder, resourceDescr ) -> {
            kBuilder.addKnowledgeResource( resourceDescr.resource, ResourceType.CMMN, resourceDescr.configuration );
        };

        ResourceBuilder DRF_RESOURCE_BUILDER = ( kBuilder, resourceDescr ) -> {
            kBuilder.addKnowledgeResource( resourceDescr.resource, ResourceType.DRF, resourceDescr.configuration );
        };
    }

    @FunctionalInterface
    private interface ResourceToPkgDescrMapper {
        PackageDescr map(KnowledgeBuilderImpl kBuilder, ResourceDescr resourceDescr) throws Exception;

        ResourceToPkgDescrMapper DRL_TO_PKG_DESCR = ( kBuilder, resourceDescr ) -> kBuilder.drlToPackageDescr(resourceDescr.resource);
        ResourceToPkgDescrMapper TEMPLATE_TO_PKG_DESCR = ( kBuilder, resourceDescr ) -> kBuilder.templateToPackageDescr( resourceDescr.resource);
        ResourceToPkgDescrMapper DSLR_TO_PKG_DESCR = ( kBuilder, resourceDescr ) -> kBuilder.dslrToPackageDescr(resourceDescr.resource);
        ResourceToPkgDescrMapper XML_TO_PKG_DESCR = ( kBuilder, resourceDescr ) -> kBuilder.xmlToPackageDescr(resourceDescr.resource);
        ResourceToPkgDescrMapper DTABLE_TO_PKG_DESCR = ( kBuilder, resourceDescr ) -> kBuilder.decisionTableToPackageDescr(resourceDescr.resource, resourceDescr.configuration);
        ResourceToPkgDescrMapper GUIDED_DTABLE_TO_PKG_DESCR = ( kBuilder, resourceDescr ) -> kBuilder.guidedDecisionTableToPackageDescr(resourceDescr.resource);
    }
}
