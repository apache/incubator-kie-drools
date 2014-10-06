package org.drools.compiler.builder.impl;

import org.drools.compiler.compiler.BPMN2ProcessFactory;
import org.drools.compiler.compiler.PackageRegistry;
import org.drools.compiler.lang.descr.AbstractClassTypeDeclarationDescr;
import org.drools.compiler.lang.descr.CompositePackageDescr;
import org.drools.compiler.lang.descr.EnumDeclarationDescr;
import org.drools.compiler.lang.descr.ImportDescr;
import org.drools.compiler.lang.descr.PackageDescr;
import org.drools.compiler.lang.descr.TypeDeclarationDescr;
import org.drools.core.builder.conf.impl.JaxbConfigurationImpl;
import org.drools.core.util.StringUtils;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceConfiguration;
import org.kie.api.io.ResourceType;
import org.kie.internal.builder.ChangeType;
import org.kie.internal.builder.CompositeKnowledgeBuilder;
import org.kie.internal.builder.ResourceChange;
import org.kie.internal.builder.ResourceChangeSet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CompositeKnowledgeBuilderImpl implements CompositeKnowledgeBuilder {

    private final KnowledgeBuilderImpl kBuilder;

    private final Map<ResourceType, List<ResourceDescr>> resourcesByType = new HashMap<ResourceType, List<ResourceDescr>>();

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
            resourceDescrs = new ArrayList<ResourceDescr>();
            resourcesByType.put(type, resourceDescrs);
        }
        resourceDescrs.add(resourceDescr);
        return this;
    }

    private List<Resource> getResources() {
        List<Resource> resources = new ArrayList<Resource>();
        for (List<ResourceDescr> resourceDescrs : resourcesByType.values()) {
            for (ResourceDescr resourceDescr : resourceDescrs) {
                resources.add(resourceDescr.resource);
            }
        }
        return resources;
    }

    public void build() {
        buildException = null;
        kBuilder.registerBuildResources(getResources());
        registerDSL();
        buildResources();
        buildPackages();
        buildOthers();
        resourcesByType.clear();
        if (buildException != null) {
            throw buildException;
        }
    }

    private void buildPackages() {
        Collection<CompositePackageDescr> packages = buildPackageDescr();
        initPackageRegistries(packages);
        normalizeTypeAnnotations( packages );
        buildTypeDeclarations(packages);
        buildEntryPoints( packages );
        buildOtherDeclarations(packages);
        normalizeRuleAnnotations( packages );
        buildRules(packages);
    }

    private void normalizeTypeAnnotations( Collection<CompositePackageDescr> packages ) {
        for (CompositePackageDescr packageDescr : packages) {
            kBuilder.normalizeTypeDeclarationAnnotations( packageDescr );
        }
    }
      
    private void normalizeRuleAnnotations( Collection<CompositePackageDescr> packages ) {
        for (CompositePackageDescr packageDescr : packages) {
            kBuilder.normalizeRuleAnnotations( packageDescr );
        }
    }

    private void buildEntryPoints( Collection<CompositePackageDescr> packages ) {
        for (CompositePackageDescr packageDescr : packages) {
            kBuilder.processEntryPointDeclarations(kBuilder.getPackageRegistry( packageDescr.getNamespace() ), packageDescr);
        }
    }

    private void registerDSL() {
        List<ResourceDescr> resourcesByType = this.resourcesByType.remove(ResourceType.DSL);
        if (resourcesByType != null) {
            for (ResourceDescr resourceDescr : resourcesByType) {
                try {
                    kBuilder.setAssetFilter(resourceDescr.getFilter());
                    kBuilder.addDsl( resourceDescr.resource );
                    kBuilder.setAssetFilter( null );
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

    private void buildResources() {
        List<ResourceDescr> resourcesByType = this.resourcesByType.remove(ResourceType.DRF);
        if (resourcesByType != null) {
            for (ResourceDescr resourceDescr : resourcesByType) {
                try {
                    kBuilder.setAssetFilter(resourceDescr.getFilter());
                    kBuilder.addProcessFromXml(resourceDescr.resource);
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

        resourcesByType = this.resourcesByType.remove(ResourceType.BPMN2);
        if (resourcesByType != null) {
            for (ResourceDescr resourceDescr : resourcesByType) {
                try {
                    BPMN2ProcessFactory.configurePackageBuilder(kBuilder);
                    kBuilder.setAssetFilter(resourceDescr.getFilter());
                    kBuilder.addProcessFromXml(resourceDescr.resource);
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

        resourcesByType = this.resourcesByType.remove(ResourceType.PKG);
        if (resourcesByType != null) {
            for (ResourceDescr resourceDescr : resourcesByType) {
                try {
                    kBuilder.setAssetFilter(resourceDescr.getFilter());
                    kBuilder.addPackageFromInputStream(resourceDescr.resource);
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

        resourcesByType = this.resourcesByType.remove(ResourceType.CHANGE_SET);
        if (resourcesByType != null) {
            for (ResourceDescr resourceDescr : resourcesByType) {
                try {
                    kBuilder.setAssetFilter(resourceDescr.getFilter());
                    kBuilder.addPackageFromChangeSet(resourceDescr.resource);
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

        resourcesByType = this.resourcesByType.remove(ResourceType.XSD);
        if (resourcesByType != null) {
            for (ResourceDescr resourceDescr : resourcesByType) {
                try {
                    kBuilder.setAssetFilter(resourceDescr.getFilter());
                    kBuilder.addPackageFromXSD(resourceDescr.resource, (JaxbConfigurationImpl) resourceDescr.configuration);
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

        resourcesByType = this.resourcesByType.remove(ResourceType.PMML);
        if (resourcesByType != null) {
            for (ResourceDescr resourceDescr : resourcesByType) {
                try {
                    kBuilder.setAssetFilter(resourceDescr.getFilter());
                    kBuilder.addPackageFromPMML(resourceDescr.resource, ResourceType.PMML, resourceDescr.configuration);
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
                for (ResourceDescr resourceDescr : entry.getValue()) {
                    kBuilder.setAssetFilter(resourceDescr.getFilter());
                    kBuilder.addPackageForExternalType(resourceDescr.resource, entry.getKey(), resourceDescr.configuration);
                    kBuilder.setAssetFilter(null);
                }
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException( e );
        }
    }

    private void buildOtherDeclarations(Collection<CompositePackageDescr> packages) {
        for (CompositePackageDescr packageDescr : packages) {
            kBuilder.setAssetFilter(packageDescr.getFilter());
            PackageRegistry pkgRegistry = kBuilder.getPackageRegistry(packageDescr.getNamespace());
            kBuilder.processOtherDeclarations( pkgRegistry, packageDescr );
            kBuilder.setAssetFilter(null);
        }
    }

    private void buildRules(Collection<CompositePackageDescr> packages) {
        for (CompositePackageDescr packageDescr : packages) {
            kBuilder.setAssetFilter(packageDescr.getFilter());
            PackageRegistry pkgRegistry = kBuilder.getPackageRegistry(packageDescr.getNamespace());
            kBuilder.compileAllRules(packageDescr, pkgRegistry);
            kBuilder.setAssetFilter(null);
        }
    }

    private void buildTypeDeclarations( Collection<CompositePackageDescr> packages ) {
        Map<String,AbstractClassTypeDeclarationDescr> unprocesseableDescrs = new HashMap<String,AbstractClassTypeDeclarationDescr>();
        List<TypeDefinition> unresolvedTypes = new ArrayList<TypeDefinition>();
        List<AbstractClassTypeDeclarationDescr> unsortedDescrs = new ArrayList<AbstractClassTypeDeclarationDescr>();
        for (CompositePackageDescr packageDescr : packages) {
            for (TypeDeclarationDescr typeDeclarationDescr : packageDescr.getTypeDeclarations()) {
                unsortedDescrs.add( typeDeclarationDescr );
            }
            for (EnumDeclarationDescr enumDeclarationDescr : packageDescr.getEnumDeclarations()) {
                unsortedDescrs.add( enumDeclarationDescr );
            }
        }

        kBuilder.getTypeBuilder().processTypeDeclarations( packages, unsortedDescrs, unresolvedTypes, unprocesseableDescrs );

        for ( CompositePackageDescr packageDescr : packages ) {
            for ( ImportDescr importDescr : packageDescr.getImports() ) {
                kBuilder.getPackageRegistry( packageDescr.getNamespace() ).addImport( importDescr );
            }
        }
    }

    private void initPackageRegistries(Collection<CompositePackageDescr> packages) {
        for ( CompositePackageDescr packageDescr : packages ) {
            if ( kBuilder.getPackageRegistry( packageDescr.getName() ) == null ) {
                if ( StringUtils.isEmpty(packageDescr.getName()) ) {
                    packageDescr.setName( kBuilder.getBuilderConfiguration().getDefaultPackageName() );
                }
                kBuilder.createPackageRegistry( packageDescr );
            }
        }
    }

    private Collection<CompositePackageDescr> buildPackageDescr() {
        Map<String, CompositePackageDescr> packages = new HashMap<String, CompositePackageDescr>();
        buildResource(packages, ResourceType.DRL, DRL_TO_PKG_DESCR);
        buildResource(packages, ResourceType.GDRL, DRL_TO_PKG_DESCR);
        buildResource(packages, ResourceType.RDRL, DRL_TO_PKG_DESCR);
        buildResource(packages, ResourceType.DESCR, DRL_TO_PKG_DESCR);
        buildResource(packages, ResourceType.DSLR, DSLR_TO_PKG_DESCR);
        buildResource(packages, ResourceType.RDSLR, DSLR_TO_PKG_DESCR);
        buildResource(packages, ResourceType.XDRL, XML_TO_PKG_DESCR);
        buildResource(packages, ResourceType.DTABLE, DTABLE_TO_PKG_DESCR);
        buildResource(packages, ResourceType.SCARD, SCARD_TO_PKG_DESCR);
        buildResource(packages, ResourceType.TDRL, DRL_TO_PKG_DESCR);
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
                compositePackageDescr = new CompositePackageDescr(resource, packageDescr);
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

        private ResourceDescr(ResourceConfiguration configuration, Resource resource, ResourceChangeSet changes) {
            this.configuration = configuration;
            this.resource = resource;
            this.changes = changes;
            if( changes != null ) {
                changeMap = new HashMap<String, ResourceChange>();
                for( ResourceChange c : changes.getChanges() ) {
                    changeMap.put(c.getName(), c);
                }
            } else {
                changeMap = null;
            }
        }
        
        public KnowledgeBuilderImpl.AssetFilter getFilter() {
            return changeMap == null ? null : this.new ChangeSetAssetFilter();
        }
        
        private class ChangeSetAssetFilter implements KnowledgeBuilderImpl.AssetFilter {
            @Override
            public Action accept(String pkgName, String assetName) {
                ResourceChange change = changeMap.get(assetName);
                if( change == null ) {
                    return Action.DO_NOTHING;
                } else if( change.getChangeType().equals(ChangeType.ADDED) ) {
                    return Action.ADD;
                } else if( change.getChangeType().equals(ChangeType.REMOVED) ) {
                    return Action.REMOVE;
                } else if( change.getChangeType().equals(ChangeType.UPDATED) ) {
                    return Action.UPDATE;
                }
                return Action.DO_NOTHING;
            }
        }
    }

    private interface ResourceToPkgDescrMapper {
        PackageDescr map(KnowledgeBuilderImpl kBuilder, ResourceDescr resourceDescr) throws Exception;
    }

    private static final ResourceToPkgDescrMapper DRL_TO_PKG_DESCR = new ResourceToPkgDescrMapper() {
        public PackageDescr map(KnowledgeBuilderImpl kBuilder, ResourceDescr resourceDescr) throws Exception {
            return kBuilder.drlToPackageDescr(resourceDescr.resource);
        }
    };

    private static final ResourceToPkgDescrMapper DSLR_TO_PKG_DESCR = new ResourceToPkgDescrMapper() {
        public PackageDescr map(KnowledgeBuilderImpl kBuilder, ResourceDescr resourceDescr) throws Exception {
            return kBuilder.dslrToPackageDescr(resourceDescr.resource);
        }
    };

    private static final ResourceToPkgDescrMapper XML_TO_PKG_DESCR = new ResourceToPkgDescrMapper() {
        public PackageDescr map(KnowledgeBuilderImpl kBuilder, ResourceDescr resourceDescr) throws Exception {
            return kBuilder.xmlToPackageDescr(resourceDescr.resource);
        }
    };

    private static final ResourceToPkgDescrMapper DTABLE_TO_PKG_DESCR = new ResourceToPkgDescrMapper() {
        public PackageDescr map(KnowledgeBuilderImpl kBuilder, ResourceDescr resourceDescr) throws Exception {
            return kBuilder.decisionTableToPackageDescr(resourceDescr.resource, resourceDescr.configuration);
        }
    };

    private static final ResourceToPkgDescrMapper SCARD_TO_PKG_DESCR = new ResourceToPkgDescrMapper() {
        public PackageDescr map(KnowledgeBuilderImpl kBuilder, ResourceDescr resourceDescr) throws Exception {
            return kBuilder.scoreCardToPackageDescr(resourceDescr.resource, resourceDescr.configuration);
        }
    };
}
