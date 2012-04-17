package org.drools.compiler;

import org.drools.builder.CompositeKnowledgeBuilder;
import org.drools.builder.ResourceConfiguration;
import org.drools.builder.ResourceType;
import org.drools.builder.conf.impl.JaxbConfigurationImpl;
import org.drools.io.Resource;
import org.drools.io.impl.BaseResource;
import org.drools.lang.descr.CompositePackageDescr;
import org.drools.lang.descr.ImportDescr;
import org.drools.lang.descr.PackageDescr;
import org.drools.lang.descr.TypeDeclarationDescr;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CompositeKnowledgeBuilderImpl implements CompositeKnowledgeBuilder {

    private PackageBuilder pkgBuilder;

    private Map<ResourceType, List<ResourceDescr>> resourcesByType = new HashMap<ResourceType, List<ResourceDescr>>();

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
        ResourceConfiguration configuration = resource instanceof BaseResource ? ((BaseResource) resource).getConfiguration() : null;
        return add(resource, type, configuration);
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
        buildPackages();
        buildResources();
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

    private void buildResources() {
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

        resourcesByType = this.resourcesByType.remove(ResourceType.DRF);
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

        Map<String, List<PackageBuilder.TypeDefinition>> unresolvedTypes = new HashMap<String, List<PackageBuilder.TypeDefinition>>();
        for (PackageDescr packageDescr : packages) {
            List<PackageBuilder.TypeDefinition> unresolvedTypesForPkg = buildTypeDeclarations(packageDescr);
            if (unresolvedTypesForPkg != null) {
                unresolvedTypes.put(packageDescr.getNamespace(), unresolvedTypesForPkg);
            }
        }

        for (Map.Entry<String, List<PackageBuilder.TypeDefinition>> unresolvedType : unresolvedTypes.entrySet()) {
            pkgBuilder.processUnresolvedTypes(pkgBuilder.getPackageRegistry(unresolvedType.getKey()), unresolvedType.getValue());
        }

        for (PackageDescr packageDescr : packages) {
            for (ImportDescr importDescr : packageDescr.getImports()) {
                pkgBuilder.getPackageRegistry(packageDescr.getNamespace()).addImport( importDescr );
            }
        }
    }

    private List<PackageBuilder.TypeDefinition> buildTypeDeclarations(PackageDescr packageDescr) {
        PackageRegistry pkgRegistry = pkgBuilder.initPackageRegistry(packageDescr);
        if (pkgRegistry == null) {
            return null;
        }

        pkgBuilder.processEntryPointDeclarations(pkgRegistry, packageDescr);
        return pkgBuilder.processTypeDeclarations(pkgRegistry, packageDescr);
    }

    private Collection<CompositePackageDescr> buildPackageDescr() {
        Map<String, CompositePackageDescr> packages = new HashMap<String, CompositePackageDescr>();
        List<ResourceDescr> resourcesByType = this.resourcesByType.remove(ResourceType.DRL);
        if (resourcesByType != null) {
            for (ResourceDescr resourceDescr : resourcesByType) {
                try {
                    registerPackageDescr(packages, resourceDescr.resource, pkgBuilder.drlToPackageDescr(resourceDescr.resource));
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

        resourcesByType = this.resourcesByType.remove(ResourceType.DESCR);
        if (resourcesByType != null) {
            for (ResourceDescr resourceDescr : resourcesByType) {
                try {
                    registerPackageDescr(packages, resourceDescr.resource, pkgBuilder.drlToPackageDescr(resourceDescr.resource));
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

        resourcesByType = this.resourcesByType.remove(ResourceType.DSLR);
        if (resourcesByType != null) {
            for (ResourceDescr resourceDescr : resourcesByType) {
                try {
                    registerPackageDescr(packages, resourceDescr.resource, pkgBuilder.dslrToPackageDescr(resourceDescr.resource));
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

        resourcesByType = this.resourcesByType.remove(ResourceType.XDRL);
        if (resourcesByType != null) {
            for (ResourceDescr resourceDescr : resourcesByType) {
                try {
                    registerPackageDescr(packages, resourceDescr.resource, pkgBuilder.xmlToPackageDescr(resourceDescr.resource));
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

        resourcesByType = this.resourcesByType.remove(ResourceType.BRL);
        if (resourcesByType != null) {
            for (ResourceDescr resourceDescr : resourcesByType) {
                try {
                    registerPackageDescr(packages, resourceDescr.resource, pkgBuilder.brlToPackageDescr(resourceDescr.resource));
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

        resourcesByType = this.resourcesByType.remove(ResourceType.DTABLE);
        if (resourcesByType != null) {
            for (ResourceDescr resourceDescr : resourcesByType) {
                try {
                    registerPackageDescr(packages, resourceDescr.resource, pkgBuilder.decisionTableToPackageDescr(resourceDescr.resource, resourceDescr.configuration));
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
        return packages.values();
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
}
