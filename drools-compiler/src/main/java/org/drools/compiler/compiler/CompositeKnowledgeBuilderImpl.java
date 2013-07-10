package org.drools.compiler.compiler;

import org.drools.core.builder.conf.impl.JaxbConfigurationImpl;
import org.drools.compiler.lang.descr.CompositePackageDescr;
import org.drools.compiler.lang.descr.ImportDescr;
import org.drools.compiler.lang.descr.PackageDescr;
import org.drools.compiler.lang.descr.TypeDeclarationDescr;
import org.kie.internal.builder.CompositeKnowledgeBuilder;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceConfiguration;
import org.kie.api.io.ResourceType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CompositeKnowledgeBuilderImpl implements CompositeKnowledgeBuilder {

    private final PackageBuilder pkgBuilder;

    private final Map<ResourceType, List<ResourceDescr>> resourcesByType = new HashMap<ResourceType, List<ResourceDescr>>();

    private RuntimeException buildException = null;

    public ResourceType currentType = null;

    public CompositeKnowledgeBuilderImpl(PackageBuilder pkgBuilder) {
        this.pkgBuilder = pkgBuilder;
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

    public CompositeKnowledgeBuilder add(Resource resource, ResourceType type, ResourceConfiguration configuration) {
        ResourceDescr resourceDescr = new ResourceDescr(configuration, resource);
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
        pkgBuilder.registerBuildResources(getResources());
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
        buildTypeDeclarations(packages);
        buildRules(packages);
    }

    private void registerDSL() {
        List<ResourceDescr> resourcesByType = this.resourcesByType.remove(ResourceType.DSL);
        if (resourcesByType != null) {
            for (ResourceDescr resourceDescr : resourcesByType) {
                try {
                    pkgBuilder.addDsl(resourceDescr.resource);
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
                    pkgBuilder.addProcessFromXml(resourceDescr.resource);
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

        resourcesByType = this.resourcesByType.remove(ResourceType.BPMN2);
        if (resourcesByType != null) {
            for (ResourceDescr resourceDescr : resourcesByType) {
                try {
                    BPMN2ProcessFactory.configurePackageBuilder( pkgBuilder );
                    pkgBuilder.addProcessFromXml(resourceDescr.resource);
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

        resourcesByType = this.resourcesByType.remove(ResourceType.PKG);
        if (resourcesByType != null) {
            for (ResourceDescr resourceDescr : resourcesByType) {
                try {
                    pkgBuilder.addPackageFromInputStream(resourceDescr.resource);
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

        resourcesByType = this.resourcesByType.remove(ResourceType.CHANGE_SET);
        if (resourcesByType != null) {
            for (ResourceDescr resourceDescr : resourcesByType) {
                try {
                    pkgBuilder.addPackageFromChangeSet(resourceDescr.resource);
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

        resourcesByType = this.resourcesByType.remove(ResourceType.XSD);
        if (resourcesByType != null) {
            for (ResourceDescr resourceDescr : resourcesByType) {
                try {
                    pkgBuilder.addPackageFromXSD(resourceDescr.resource, (JaxbConfigurationImpl) resourceDescr.configuration);
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

        resourcesByType = this.resourcesByType.remove(ResourceType.PMML);
        if (resourcesByType != null) {
            for (ResourceDescr resourceDescr : resourcesByType) {
                try {
                    pkgBuilder.addPackageFromPMML(resourceDescr.resource, ResourceType.PMML, resourceDescr.configuration);
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

    private void buildOthers() {
        try {
            for (Map.Entry<ResourceType, List<ResourceDescr>> entry : resourcesByType.entrySet()) {
                for (ResourceDescr resourceDescr : entry.getValue()) {
                    pkgBuilder.addPackageForExternalType(resourceDescr.resource, entry.getKey(), resourceDescr.configuration);
                }
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException( e );
        }
    }

    private void buildRules(Collection<CompositePackageDescr> packages) {
        for (PackageDescr packageDescr : packages) {
            PackageRegistry pkgRegistry = pkgBuilder.getPackageRegistry(packageDescr.getNamespace());
            pkgBuilder.processOtherDeclarations(pkgRegistry, packageDescr);
            pkgBuilder.compileAllRules(packageDescr, pkgRegistry);
        }
    }

    private void buildTypeDeclarations(Collection<CompositePackageDescr> packages) {
        for (PackageDescr packageDescr : packages) {
            for (TypeDeclarationDescr typeDeclarationDescr : packageDescr.getTypeDeclarations()) {
                if (pkgBuilder.isEmpty( typeDeclarationDescr.getNamespace() )) {
                    typeDeclarationDescr.setNamespace( packageDescr.getNamespace() ); // set the default namespace
                }
                pkgBuilder.registerGeneratedType(typeDeclarationDescr);
            }
        }

        List<PackageBuilder.TypeDefinition> unresolvedTypes = new ArrayList<PackageBuilder.TypeDefinition>();
        for (PackageDescr packageDescr : packages) {
            buildTypeDeclarations(packageDescr, unresolvedTypes);
        }

        for (PackageBuilder.TypeDefinition unresolvedType : unresolvedTypes) {
            pkgBuilder.processUnresolvedType(pkgBuilder.getPackageRegistry(unresolvedType.getNamespace()), unresolvedType);
        }

        for (PackageDescr packageDescr : packages) {
            for (ImportDescr importDescr : packageDescr.getImports()) {
                pkgBuilder.getPackageRegistry(packageDescr.getNamespace()).addImport( importDescr );
            }
        }
    }

    private List<PackageBuilder.TypeDefinition> buildTypeDeclarations(PackageDescr packageDescr, List<PackageBuilder.TypeDefinition> unresolvedTypes) {
        PackageRegistry pkgRegistry = pkgBuilder.initPackageRegistry(packageDescr);
        if (pkgRegistry == null) {
            return null;
        }

        pkgBuilder.processEntryPointDeclarations(pkgRegistry, packageDescr);
        return pkgBuilder.processTypeDeclarations(pkgRegistry, packageDescr, unresolvedTypes);
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
        buildResource(packages, ResourceType.SCARD, DTABLE_TO_PKG_DESCR);
        return packages.values();
    }

    private void buildResource(Map<String, CompositePackageDescr> packages, ResourceType resourceType, ResourceToPkgDescrMapper mapper) {
        List<ResourceDescr> resourcesByType = this.resourcesByType.remove(resourceType);
        if (resourcesByType != null) {
            for (ResourceDescr resourceDescr : resourcesByType) {
                try {
                    registerPackageDescr(packages, resourceDescr.resource, mapper.map(pkgBuilder, resourceDescr));
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

    private void registerPackageDescr(Map<String, CompositePackageDescr> packages, Resource resource, PackageDescr packageDescr) {
        if (packageDescr != null) {
            CompositePackageDescr compositePackageDescr = packages.get(packageDescr.getNamespace());
            if (compositePackageDescr == null) {
                packages.put(packageDescr.getNamespace(), new CompositePackageDescr(resource, packageDescr));
            } else {
                compositePackageDescr.addPackageDescr(resource, packageDescr);
            }
        }
    }

    private static class ResourceDescr {
        final Resource resource;
        final ResourceConfiguration configuration;

        private ResourceDescr(ResourceConfiguration configuration, Resource resource) {
            this.configuration = configuration;
            this.resource = resource;
        }
    }

    private interface ResourceToPkgDescrMapper {
        PackageDescr map(PackageBuilder pkgBuilder, ResourceDescr resourceDescr) throws Exception;
    }

    private static final ResourceToPkgDescrMapper DRL_TO_PKG_DESCR = new ResourceToPkgDescrMapper() {
        public PackageDescr map(PackageBuilder pkgBuilder, ResourceDescr resourceDescr) throws Exception {
            return pkgBuilder.drlToPackageDescr(resourceDescr.resource);
        }
    };

    private static final ResourceToPkgDescrMapper DSLR_TO_PKG_DESCR = new ResourceToPkgDescrMapper() {
        public PackageDescr map(PackageBuilder pkgBuilder, ResourceDescr resourceDescr) throws Exception {
            return pkgBuilder.dslrToPackageDescr(resourceDescr.resource);
        }
    };

    private static final ResourceToPkgDescrMapper XML_TO_PKG_DESCR = new ResourceToPkgDescrMapper() {
        public PackageDescr map(PackageBuilder pkgBuilder, ResourceDescr resourceDescr) throws Exception {
            return pkgBuilder.xmlToPackageDescr(resourceDescr.resource);
        }
    };

    private static final ResourceToPkgDescrMapper DTABLE_TO_PKG_DESCR = new ResourceToPkgDescrMapper() {
        public PackageDescr map(PackageBuilder pkgBuilder, ResourceDescr resourceDescr) throws Exception {
            return pkgBuilder.decisionTableToPackageDescr(resourceDescr.resource, resourceDescr.configuration);
        }
    };
}
