package org.drools.compiler;

import org.drools.builder.CompositeKnowledgeBuilder;
import org.drools.builder.ResourceConfiguration;
import org.drools.builder.ResourceType;
import org.drools.builder.conf.impl.JaxbConfigurationImpl;
import org.drools.core.util.Memento;
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

    private Memento<PackageBuilder> pkgBuilder;

    private Map<ResourceType, List<ResourceDescr>> resources = new HashMap<ResourceType, List<ResourceDescr>>();

    public ResourceType currentType = null;

    public CompositeKnowledgeBuilderImpl(Memento<PackageBuilder> pkgBuilder) {
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
        List<ResourceDescr> resourcesByType = resources.get(type);
        if (resourcesByType == null) {
            resourcesByType = new ArrayList<ResourceDescr>();
            resources.put(type, resourcesByType);
        }
        resourcesByType.add(resourceDescr);
        return this;
    }

    public void build() {
        pkgBuilder.record();
        PackageBuilder currentBuilder = pkgBuilder.get();
        buildPackages(currentBuilder);
        buildResources(currentBuilder);
        buildOthers(currentBuilder);
        resources.clear();
    }

    private void buildPackages(PackageBuilder pkgBuilder) {
        Collection<CompositePackageDescr> packages = buildPackageDescr(pkgBuilder);
        buildTypeDeclarations(pkgBuilder, packages);
        buildRules(pkgBuilder, packages);
    }

    private void buildResources(PackageBuilder pkgBuilder) {
        try {
            List<ResourceDescr> resourcesByType = resources.remove(ResourceType.DSL);
            if (resourcesByType != null) {
                for (ResourceDescr resourceDescr : resourcesByType) {
                    pkgBuilder.addDsl(resourceDescr.resource);
                }
            }

            resourcesByType = resources.remove(ResourceType.DRF);
            if (resourcesByType != null) {
                for (ResourceDescr resourceDescr : resourcesByType) {
                    pkgBuilder.addProcessFromXml(resourceDescr.resource);
                }
            }

            resourcesByType = resources.remove(ResourceType.BPMN2);
            if (resourcesByType != null) {
                for (ResourceDescr resourceDescr : resourcesByType) {
                    BPMN2ProcessFactory.configurePackageBuilder( pkgBuilder );
                    pkgBuilder.addProcessFromXml(resourceDescr.resource);
                }
            }

            resourcesByType = resources.remove(ResourceType.PKG);
            if (resourcesByType != null) {
                for (ResourceDescr resourceDescr : resourcesByType) {
                    pkgBuilder.addPackageFromInputStream(resourceDescr.resource);
                }
            }

            resourcesByType = resources.remove(ResourceType.CHANGE_SET);
            if (resourcesByType != null) {
                for (ResourceDescr resourceDescr : resourcesByType) {
                    pkgBuilder.addPackageFromChangeSet(resourceDescr.resource);
                }
            }

            resourcesByType = resources.remove(ResourceType.XSD);
            if (resourcesByType != null) {
                for (ResourceDescr resourceDescr : resourcesByType) {
                    pkgBuilder.addPackageFromXSD(resourceDescr.resource, (JaxbConfigurationImpl) resourceDescr.configuration);
                }
            }

            resourcesByType = resources.remove(ResourceType.PMML);
            if (resourcesByType != null) {
                for (ResourceDescr resourceDescr : resourcesByType) {
                    pkgBuilder.addPackageFromPMML(resourceDescr.resource, ResourceType.PMML, resourceDescr.configuration);
                }
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException( e );
        }
    }

    private void buildOthers(PackageBuilder pkgBuilder) {
        try {
            for (Map.Entry<ResourceType, List<ResourceDescr>> entry : resources.entrySet()) {
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

    private void buildRules(PackageBuilder pkgBuilder, Collection<CompositePackageDescr> packages) {
        for (PackageDescr packageDescr : packages) {
            PackageRegistry pkgRegistry = pkgBuilder.getPackageRegistry(packageDescr.getNamespace());
            pkgBuilder.processOtherDeclarations(pkgRegistry, packageDescr);
            pkgBuilder.compileAllRules(packageDescr, pkgRegistry);
        }
    }

    private void buildTypeDeclarations(PackageBuilder pkgBuilder, Collection<CompositePackageDescr> packages) {
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
            List<PackageBuilder.TypeDefinition> unresolvedTypesForPkg = buildTypeDeclarations(pkgBuilder, packageDescr);
            if (unresolvedTypesForPkg != null) {
                unresolvedTypes.put(packageDescr.getNamespace(), unresolvedTypesForPkg);
            }
        }

        for (Map.Entry<String, List<PackageBuilder.TypeDefinition>> unresolvedType : unresolvedTypes.entrySet()) {
            pkgBuilder.processUnresolvedTypes(pkgBuilder.getPackageRegistry(unresolvedType.getKey()), unresolvedType.getValue());
        }

        for (PackageDescr packageDescr : packages) {
            for (ImportDescr importEntry : packageDescr.getImports()) {
                pkgBuilder.getPackageRegistry(packageDescr.getNamespace()).addImport( importEntry.getTarget() );
            }
        }
    }

    private List<PackageBuilder.TypeDefinition> buildTypeDeclarations(PackageBuilder pkgBuilder, PackageDescr packageDescr) {
        PackageRegistry pkgRegistry = pkgBuilder.initPackageRegistry(packageDescr);
        if (pkgRegistry == null) {
            return null;
        }

        pkgBuilder.processEntryPointDeclarations(pkgRegistry, packageDescr);
        return pkgBuilder.processTypeDeclarations(pkgRegistry, packageDescr);
    }

    private Collection<CompositePackageDescr> buildPackageDescr(PackageBuilder pkgBuilder) {
        Map<String, CompositePackageDescr> packages = new HashMap<String, CompositePackageDescr>();
        try {
            List<ResourceDescr> resourcesByType = resources.remove(ResourceType.DRL);
            if (resourcesByType != null) {
                for (ResourceDescr resourceDescr : resourcesByType) {
                    registerPackageDescr(packages, resourceDescr.resource, pkgBuilder.drlToPackageDescr(resourceDescr.resource));
                }
            }

            resourcesByType = resources.remove(ResourceType.DESCR);
            if (resourcesByType != null) {
                for (ResourceDescr resourceDescr : resourcesByType) {
                    registerPackageDescr(packages, resourceDescr.resource, pkgBuilder.drlToPackageDescr(resourceDescr.resource));
                }
            }

            resourcesByType = resources.remove(ResourceType.DSLR);
            if (resourcesByType != null) {
                for (ResourceDescr resourceDescr : resourcesByType) {
                    registerPackageDescr(packages, resourceDescr.resource, pkgBuilder.dslrToPackageDescr(resourceDescr.resource));
                }
            }

            resourcesByType = resources.remove(ResourceType.XDRL);
            if (resourcesByType != null) {
                for (ResourceDescr resourceDescr : resourcesByType) {
                    registerPackageDescr(packages, resourceDescr.resource, pkgBuilder.xmlToPackageDescr(resourceDescr.resource));
                }
            }

            resourcesByType = resources.remove(ResourceType.BRL);
            if (resourcesByType != null) {
                for (ResourceDescr resourceDescr : resourcesByType) {
                    registerPackageDescr(packages, resourceDescr.resource, pkgBuilder.brlToPackageDescr(resourceDescr.resource));
                }
            }

            resourcesByType = resources.remove(ResourceType.DTABLE);
            if (resourcesByType != null) {
                for (ResourceDescr resourceDescr : resourcesByType) {
                    registerPackageDescr(packages, resourceDescr.resource, pkgBuilder.decisionTableToPackageDescr(resourceDescr.resource, resourceDescr.configuration));
                }
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException( e );
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
