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

package org.jbpm.kie.services.impl;

import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.persistence.EntityManagerFactory;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.codec.binary.Base64;
import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.drools.compiler.kie.builder.impl.KieContainerImpl;
import org.appformer.maven.support.DependencyFilter;
import org.drools.core.common.ProjectClassLoader;
import org.drools.core.marshalling.impl.ClassObjectMarshallingStrategyAcceptor;
import org.drools.core.marshalling.impl.SerializablePlaceholderResolverStrategy;
import org.drools.core.util.StringUtils;
import org.jbpm.kie.services.impl.bpmn2.ProcessDescriptor;
import org.jbpm.kie.services.impl.model.ProcessAssetDesc;
import org.jbpm.process.audit.event.AuditEventBuilder;
import org.jbpm.runtime.manager.impl.KModuleRegisterableItemsFactory;
import org.jbpm.runtime.manager.impl.deploy.DeploymentDescriptorImpl;
import org.jbpm.runtime.manager.impl.deploy.DeploymentDescriptorManager;
import org.jbpm.runtime.manager.impl.deploy.DeploymentDescriptorMerger;
import org.jbpm.runtime.manager.impl.jpa.EntityManagerFactoryManager;
import org.jbpm.services.api.DefinitionService;
import org.jbpm.services.api.DeploymentService;
import org.jbpm.services.api.model.DeployedAsset;
import org.jbpm.services.api.model.DeployedUnit;
import org.jbpm.services.api.model.DeploymentUnit;
import org.jbpm.services.api.service.ServiceRegistry;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.executor.ExecutorService;
import org.kie.api.marshalling.ObjectMarshallingStrategy;
import org.kie.api.remote.Remotable;
import org.kie.api.runtime.EnvironmentName;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.manager.RegisterableItemsFactory;
import org.kie.api.runtime.manager.RuntimeEnvironmentBuilder;
import org.kie.internal.runtime.conf.DeploymentDescriptor;
import org.kie.internal.runtime.conf.MergeMode;
import org.kie.internal.runtime.conf.NamedObjectModel;
import org.kie.internal.runtime.conf.ObjectModel;
import org.kie.internal.runtime.conf.ObjectModelResolver;
import org.kie.internal.runtime.conf.ObjectModelResolverProvider;
import org.kie.internal.runtime.conf.PersistenceMode;
import org.kie.internal.runtime.manager.InternalRuntimeManager;
import org.kie.scanner.KieMavenRepository;
import org.reflections.Reflections;
import org.reflections.util.ConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;

import static org.kie.scanner.KieMavenRepository.getKieMavenRepository;


public class KModuleDeploymentService extends AbstractDeploymentService {

    protected static Logger logger = LoggerFactory.getLogger(KModuleDeploymentService.class);
    private static final String DEFAULT_KBASE_NAME = "defaultKieBase";
    private static final String PROCESS_ID_XPATH = "/*[local-name() = 'definitions']/*[local-name() = 'process']/@id";

    protected DefinitionService bpmn2Service;

    protected DeploymentDescriptorMerger merger = new DeploymentDescriptorMerger();

    protected FormManagerService formManagerService;

    protected ExecutorService executorService;

    protected XPathExpression processIdXPathExpression;

    public KModuleDeploymentService() {
        try {
            processIdXPathExpression = XPathFactory.newInstance().newXPath().compile(PROCESS_ID_XPATH);
        } catch (XPathExpressionException e) {
            logger.error("Unable to parse '{}' XPath expression due to {}", PROCESS_ID_XPATH, e.getMessage());
        }
        ServiceRegistry.get().register(DeploymentService.class.getSimpleName(), this);
    }

    public void onInit() {
    	EntityManagerFactoryManager.get().addEntityManagerFactory("org.jbpm.domain", getEmf());
    }

    @Override
    public void deploy(DeploymentUnit unit) {
    	try {
    		super.deploy(unit);
            if (!(unit instanceof KModuleDeploymentUnit)) {
                throw new IllegalArgumentException("Invalid deployment unit provided - " + unit.getClass().getName());
            }
            KModuleDeploymentUnit kmoduleUnit = (KModuleDeploymentUnit) unit;
            DeployedUnitImpl deployedUnit = new DeployedUnitImpl(unit);
            deployedUnit.setActive(kmoduleUnit.isActive());

            // Create the release id
            KieContainer kieContainer = kmoduleUnit.getKieContainer();
            ReleaseId releaseId = null;
            if (kieContainer == null) {
	            KieServices ks = KieServices.Factory.get();

	            releaseId = ks.newReleaseId(kmoduleUnit.getGroupId(), kmoduleUnit.getArtifactId(), kmoduleUnit.getVersion());

	            KieMavenRepository repository = getKieMavenRepository();
	            repository.resolveArtifact(releaseId.toExternalForm());

	            kieContainer = ks.newKieContainer(releaseId);

	            kmoduleUnit.setKieContainer(kieContainer);
            }
            releaseId = kieContainer.getReleaseId();

            // retrieve the kbase name
            String kbaseName = kmoduleUnit.getKbaseName();
            if (StringUtils.isEmpty(kbaseName)) {
                KieBaseModel defaultKBaseModel = ((KieContainerImpl)kieContainer).getKieProject().getDefaultKieBaseModel();
                if (defaultKBaseModel != null) {
                    kbaseName = defaultKBaseModel.getName();
                } else {
                    kbaseName = DEFAULT_KBASE_NAME;
                }
            }
            InternalKieModule module = (InternalKieModule) ((KieContainerImpl)kieContainer).getKieModuleForKBase(kbaseName);
            if (module == null) {
                throw new IllegalStateException("Cannot find kbase, either it does not exist or there are multiple default kbases in kmodule.xml");
            }

            KieBase kbase = kieContainer.getKieBase(kbaseName);
            Map<String, ProcessDescriptor> processDescriptors = new HashMap<String, ProcessDescriptor>();
            for (org.kie.api.definition.process.Process process : kbase.getProcesses()) {
                processDescriptors.put(process.getId(), ((ProcessDescriptor) process.getMetaData().get("ProcessDescriptor")).clone());
            }

            // TODO: add forms data?
            Collection<String> files = module.getFileNames();

            processResources(module, files, kieContainer, kmoduleUnit, deployedUnit, releaseId, processDescriptors);

            // process the files in the deployment
            if (module.getKieDependencies() != null) {
    	        Collection<InternalKieModule> dependencies = module.getKieDependencies().values();
    	        for (InternalKieModule depModule : dependencies) {

    	        	logger.debug("Processing dependency module " + depModule.getReleaseId());
    	        	files = depModule.getFileNames();

    	        	processResources(depModule, files, kieContainer, kmoduleUnit, deployedUnit, depModule.getReleaseId(), processDescriptors);
    	        }
            }
            Collection<ReleaseId> dependencies = module.getJarDependencies(new DependencyFilter.ExcludeScopeFilter("test", "provided"));

            // process deployment dependencies
            if (dependencies != null && !dependencies.isEmpty()) {
                // Classes 2: classes added from project and dependencies added
            	processClassloader(kieContainer, deployedUnit);
            }

            AuditEventBuilder auditLoggerBuilder = setupAuditLogger(identityProvider, unit.getIdentifier());

            RuntimeEnvironmentBuilder builder = boostrapRuntimeEnvironmentBuilder(
            		kmoduleUnit, deployedUnit, kieContainer, kmoduleUnit.getMergeMode())
                    .knowledgeBase(kbase)
                    .classLoader(kieContainer.getClassLoader());

            builder.registerableItemsFactory(getRegisterableItemsFactory(auditLoggerBuilder, kieContainer, kmoduleUnit));

            commonDeploy(unit, deployedUnit, builder.get(), kieContainer);
            kmoduleUnit.setDeployed(true);
    	} catch (Throwable e) {
    		logger.warn("Unexpected error while deploying unit {}", unit.getIdentifier(), e);
    		// catch all possible errors to be able to report them to caller as RuntimeException
    		throw new RuntimeException(e);
    	}
    }

    protected RegisterableItemsFactory getRegisterableItemsFactory(AuditEventBuilder auditLoggerBuilder,
    		KieContainer kieContainer,KModuleDeploymentUnit unit) {
    	KModuleRegisterableItemsFactory factory = new KModuleRegisterableItemsFactory(kieContainer, unit.getKsessionName());
    	factory.setAuditBuilder(auditLoggerBuilder);
		return factory;
    }

    @Override
	public void undeploy(DeploymentUnit unit) {
    	if (!(unit instanceof KModuleDeploymentUnit)) {
            throw new IllegalArgumentException("Invalid deployment unit provided - " + unit.getClass().getName());
        }
        KModuleDeploymentUnit kmoduleUnit = (KModuleDeploymentUnit) unit;
		super.undeploy(unit);

        formManagerService.unRegisterForms( unit.getIdentifier() );

        KieServices ks = KieServices.Factory.get();
		ReleaseId releaseId = ks.newReleaseId(kmoduleUnit.getGroupId(), kmoduleUnit.getArtifactId(), kmoduleUnit.getVersion());
		ks.getRepository().removeKieModule(releaseId);
	}

    /**
     * This creates and fills a {@link RuntimeEnvironmentBuilder} instance, which is later used when creating services.
     * </p>
     * A lot of the logic here is used to process the information in the {@link DeploymentDescriptor} instance, which is
     * part of the {@link DeploymentUnit}.
     *
     * @param deploymentUnit The {@link KModuleDeploymentUnit}, which is filled by the method
     * @param deployedUnit The {@link DeployedUnit}, which is also filled by the method
     * @param kieContainer The {@link KieContainer}, which contains information needed to fill the above two arguments
     * @param mode The {@link MergeMode} used to resolve conflicts in the {@link DeploymentDescriptor}.
     * @return A {@link RuntimeEnvironmentBuilder} instance ready for use
     */
    protected RuntimeEnvironmentBuilder boostrapRuntimeEnvironmentBuilder(KModuleDeploymentUnit deploymentUnit,
    		DeployedUnit deployedUnit, KieContainer kieContainer, MergeMode mode) {
    	DeploymentDescriptor descriptor = deploymentUnit.getDeploymentDescriptor();
    	if (descriptor == null || ((DeploymentDescriptorImpl)descriptor).isEmpty()) { // skip empty descriptors as its default can override settings
	    	DeploymentDescriptorManager descriptorManager = new DeploymentDescriptorManager("org.jbpm.domain");
	    	List<DeploymentDescriptor> descriptorHierarchy = descriptorManager.getDeploymentDescriptorHierarchy(kieContainer);

			descriptor = merger.merge(descriptorHierarchy, mode);
			deploymentUnit.setDeploymentDescriptor(descriptor);
    	} else if (descriptor != null && !deploymentUnit.isDeployed()) {
    		DeploymentDescriptorManager descriptorManager = new DeploymentDescriptorManager("org.jbpm.domain");
	    	List<DeploymentDescriptor> descriptorHierarchy = descriptorManager.getDeploymentDescriptorHierarchy(kieContainer);

	    	descriptorHierarchy.add(0, descriptor);
	    	descriptor = merger.merge(descriptorHierarchy, mode);
			deploymentUnit.setDeploymentDescriptor(descriptor);
    	}

		// first set on unit the strategy
		deploymentUnit.setStrategy(descriptor.getRuntimeStrategy());

		// setting up runtime environment via builder
		RuntimeEnvironmentBuilder builder = null;
		if (descriptor.getPersistenceMode() == PersistenceMode.NONE) {
			builder = RuntimeEnvironmentBuilder.Factory.get().newDefaultInMemoryBuilder();
		} else {
			builder = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder();
		}
		// populate various properties of the builder
		EntityManagerFactory emf = EntityManagerFactoryManager.get().getOrCreate(descriptor.getPersistenceUnit());
		builder.entityManagerFactory(emf);

		Map<String, Object> contaxtParams = new HashMap<String, Object>();
		contaxtParams.put("entityManagerFactory", emf);
		contaxtParams.put("classLoader", kieContainer.getClassLoader());
		// process object models that are globally configured (environment entries, session configuration)
		for (NamedObjectModel model : descriptor.getEnvironmentEntries()) {
			Object entry = getInstanceFromModel(model, kieContainer, contaxtParams);
			builder.addEnvironmentEntry(model.getName(), entry);
		}

		for (NamedObjectModel model : descriptor.getConfiguration()) {
			Object entry = getInstanceFromModel(model, kieContainer, contaxtParams);
			builder.addConfiguration(model.getName(), (String) entry);
		}
		ObjectMarshallingStrategy[] mStrategies = new ObjectMarshallingStrategy[descriptor.getMarshallingStrategies().size() + 1];
		int index = 0;
		for (ObjectModel model : descriptor.getMarshallingStrategies()) {
			Object strategy = getInstanceFromModel(model, kieContainer, contaxtParams);
			mStrategies[index] = (ObjectMarshallingStrategy)strategy;
			index++;
		}
		// lastly add the main default strategy
		mStrategies[index] = new SerializablePlaceholderResolverStrategy(ClassObjectMarshallingStrategyAcceptor.DEFAULT);
		builder.addEnvironmentEntry(EnvironmentName.OBJECT_MARSHALLING_STRATEGIES, mStrategies);

		builder.addEnvironmentEntry("KieDeploymentDescriptor", descriptor);
		builder.addEnvironmentEntry("KieContainer", kieContainer);
		if (executorService != null) {
		    builder.addEnvironmentEntry("ExecutorService", executorService);
		}
		if (identityProvider != null) {
            builder.addEnvironmentEntry(EnvironmentName.IDENTITY_PROVIDER, identityProvider);
        }
		// populate all assets with roles for this deployment unit
		List<String> requiredRoles = descriptor.getRequiredRoles(DeploymentDescriptor.TYPE_VIEW);
		if (requiredRoles != null && !requiredRoles.isEmpty()) {
			for (DeployedAsset desc : deployedUnit.getDeployedAssets()) {
				if (desc instanceof ProcessAssetDesc) {
					((ProcessAssetDesc) desc).setRoles(requiredRoles);
				}
			}
		}

		// Classes 3: classes added from descriptor
		List<String> remoteableClasses = descriptor.getClasses();
		if (remoteableClasses != null && !remoteableClasses.isEmpty()) {
			for (String className : remoteableClasses) {
			    Class descriptorClass = null;
				try {
				    descriptorClass = kieContainer.getClassLoader().loadClass(className);
                    logger.debug( "Loaded {} into the classpath from deployment descriptor {}", className, kieContainer.getReleaseId().toExternalForm());
                } catch (ClassNotFoundException cnfe) {
                    throw new IllegalArgumentException("Class " + className + " not found in the project");
                } catch (NoClassDefFoundError e) {
                	throw new IllegalArgumentException("Class " + className + " not found in the project");
				}
				addClassToDeployedUnit(descriptorClass, (DeployedUnitImpl) deployedUnit);
			}
		}

    	return builder;
    }


    protected Object getInstanceFromModel(ObjectModel model, KieContainer kieContainer, Map<String, Object> contaxtParams) {
    	ObjectModelResolver resolver = ObjectModelResolverProvider.get(model.getResolver());
		if (resolver == null) {
		    // if we don't throw an exception here, we have an NPE below..
			throw new IllegalStateException("Unable to find ObjectModelResolver for " + model.getResolver());
		}

		return resolver.getInstance(model, kieContainer.getClassLoader(), contaxtParams);
    }

    /**
     * Goes through all files in a deployment, and processes them so that they are then ready
     * for use after deployment.
     *
     * @param module The {@link InternalKieModule}, necessary to get form content
     * @param files The {@link List} of file (names) to process.
     * @param kieContainer The {@link KieContainer}, necesary in order to load classes
     * @param deploymentUnit The {@link DeploymentUnit}, necessary to get the deployment id
     * @param deployedUnit The {@link DeployedUnit}, which contains the results of actions here
     */
	protected void processResources(InternalKieModule module, Collection<String> files,
    		KieContainer kieContainer, DeploymentUnit unit, DeployedUnitImpl deployedUnit, ReleaseId releaseId, Map<String, ProcessDescriptor> processes) {
        for (String fileName : files) {
            if(fileName.matches(".+bpmn[2]?$")) {
                ProcessAssetDesc process;
                try {
                    String processString = new String(module.getBytes(fileName), "UTF-8");
                    String processId = getProcessId(processString);
                    ProcessDescriptor processDesriptor = processes.get(processId);
                    if (processDesriptor != null) {
                        process = processDesriptor.getProcess();
                        if (process == null) {
                            throw new IllegalArgumentException("Unable to read process " + fileName);
                        }
                        process.setEncodedProcessSource(Base64.encodeBase64String(processString.getBytes()));
                        process.setDeploymentId(unit.getIdentifier());

                        deployedUnit.addAssetLocation(process.getId(), process);
                        bpmn2Service.addProcessDefinition(unit.getIdentifier(), processId, processDesriptor, kieContainer);
                    }
                } catch (UnsupportedEncodingException e) {
                    throw new IllegalArgumentException("Unsupported encoding while processing process " + fileName);
                }
            } else if (fileName.matches(".+ftl$") || fileName.matches(".+form$") || fileName.matches( ".+frm$" )) {
                try {
                    String formContent = new String(module.getBytes(fileName), "UTF-8");
                    if (fileName.indexOf( "/" ) != -1) fileName = fileName.substring( fileName.lastIndexOf( "/" ) + 1);
                    formManagerService.registerForm(unit.getIdentifier(), fileName, formContent);
                } catch (UnsupportedEncodingException e) {
                	throw new IllegalArgumentException("Unsupported encoding while processing form " + fileName);
                }
            } else if( fileName.matches(".+class$")) {
                // Classes 1: classes from deployment added
                String className = fileName.replaceAll("/", ".");
                className = className.substring(0, fileName.length() - ".class".length());
                Class deploymentClass = null;
                try {
                    deploymentClass = kieContainer.getClassLoader().loadClass(className);
                } catch (ClassNotFoundException cnfe) {
                    throw new IllegalArgumentException("Class " + className + " not found in the project");
                } catch (NoClassDefFoundError e) {
                	throw new IllegalArgumentException("Class " + className + " not found in the project");
				}
                addClassToDeployedUnit(deploymentClass, deployedUnit);
            }
        }
    }

	protected void addClassToDeployedUnit(Class deploymentClass, DeployedUnitImpl deployedUnit) {
        if( deploymentClass != null ) {
            DeploymentUnit unit = deployedUnit.getDeploymentUnit();
            Boolean limitClasses = false;
            if( unit != null ) {
                DeploymentDescriptor depDesc = ((KModuleDeploymentUnit) unit).getDeploymentDescriptor();
                if( depDesc != null ) {
                   limitClasses = depDesc.getLimitSerializationClasses();
                }
            }
            if( limitClasses != null && limitClasses ) {
                filterClassesAddedToDeployedUnit(deployedUnit, deploymentClass);
            } else {
                logger.debug( "Loaded {} onto the classpath from deployment {}", deploymentClass.getName(), unit.getIdentifier());
                deployedUnit.addClass(deploymentClass);
            }
        }
	}

	/**
	 * This processes the deployment dependencies, which are made available by the {@link KieContainer} {@link ClassLoader}.
	 *
	 * @param kieContainer The {@link KieContainer}, used to get the {@link ClassLoader}
	 * @param deployedUnit The {@link DeployedUnitImpl}, used to store the classes loaded
	 */
	protected void processClassloader(KieContainer kieContainer, DeployedUnitImpl deployedUnit) {
		if (kieContainer.getClassLoader() instanceof ProjectClassLoader) {
			ClassLoader parentCl = kieContainer.getClassLoader().getParent();
			if (parentCl instanceof URLClassLoader) {
				URL[] urls = ((URLClassLoader) parentCl).getURLs();
				if (urls == null || urls.length == 0) {
					return;
				}
				ConfigurationBuilder builder = new ConfigurationBuilder();
				builder.addUrls(urls);
				builder.addClassLoader(kieContainer.getClassLoader());

				Reflections reflections = new Reflections(builder);

				Set<Class<?>> xmlRootElemClasses = reflections.getTypesAnnotatedWith(XmlRootElement.class);
				Set<Class<?>> xmlTypeClasses = reflections.getTypesAnnotatedWith(XmlType.class);
				Set<Class<?>> remoteableClasses = reflections.getTypesAnnotatedWith(Remotable.class);

				Set<Class<?>> allClasses = new HashSet<Class<?>>();
				for( Set<Class<?>> classesToAdd : new Set[] { xmlRootElemClasses, xmlTypeClasses, remoteableClasses } ) {
				   if( classesToAdd != null ) {
				       allClasses.addAll(classesToAdd);
				   }
				}

				for (Class<?> clazz : allClasses) {
				    filterClassesAddedToDeployedUnit(deployedUnit, clazz);
				}
			}
	    }
	}

	/**
     * This method is used to filter classes that are added to the {@link DeployedUnit}.
     * </p>
     * When this method is used, only classes that are meant to be used with serialization are
     * added to the deployment. This feature can be used to, for example, make sure that non-serialization-compatible
     * classes (such as interfaces), do not complicate the use of a deployment with the remote services (REST/JMS/WS).
     * </p>
     * Note to other developers, it's possible that classpath problems may arise, because
     * of either classloader or lazy class resolution problems: I simply don't know enough about the
     * inner workings of the JAXB implementations (plural!) to figure this out.
     *
     * @param deployedUnit The {@link DeployedUnit} to which the classes are added. The {@link DeployedUnit} to which the classes are added. The {@link DeployedUnit} to which the classes are added.
     * @param classToAdd The class to add to the {@link DeployedUnit}.
     */
    private static void filterClassesAddedToDeployedUnit( DeployedUnit deployedUnit, Class classToAdd) {

        if( classToAdd.isInterface()
                || classToAdd.isAnnotation()
                || classToAdd.isLocalClass()
                || classToAdd.isMemberClass() ) {
           return;
        }

        boolean jaxbClass = false;
        boolean remoteableClass = false;
        // @XmlRootElement and @XmlType may be used with inheritance
        for( Annotation anno : classToAdd.getAnnotations() ) {
           if( XmlRootElement.class.equals(anno.annotationType()) ) {
              jaxbClass = true;
              break;
           }
           if( XmlType.class.equals(anno.annotationType()) ) {
              jaxbClass = true;
              break;
           }
        }
        // @Remotable is not inheritable, and may not be used as such
        for( Annotation anno : classToAdd.getDeclaredAnnotations() ) {
           if( Remotable.class.equals(anno.annotationType()) ) {
               remoteableClass = true;
               break;
           }
        }

        if( jaxbClass || remoteableClass ) {
            DeployedUnitImpl deployedUnitImpl = (DeployedUnitImpl) deployedUnit;
            deployedUnitImpl.addClass(classToAdd);
        }
    }

	public void setBpmn2Service(DefinitionService bpmn2Service) {
	    this.bpmn2Service = bpmn2Service;
	}

	public void setMerger(DeploymentDescriptorMerger merger) {
		this.merger = merger;
	}

    public void setFormManagerService(FormManagerService formManagerService) {
        this.formManagerService = formManagerService;
    }

    public void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }


	@Override
	public void activate(String deploymentId) {
		DeployedUnit deployed = getDeployedUnit(deploymentId);
		if (deployed != null) {
			((DeployedUnitImpl)deployed).setActive(true);
			
			((InternalRuntimeManager)deployed.getRuntimeManager()).activate();
			
			notifyOnActivate(deployed.getDeploymentUnit(), deployed);
		}
		
	}

	@Override
	public void deactivate(String deploymentId) {
		DeployedUnit deployed = getDeployedUnit(deploymentId);
		if (deployed != null && deployed.isActive()) {
			((DeployedUnitImpl)deployed).setActive(false);
			
			((InternalRuntimeManager)deployed.getRuntimeManager()).deactivate();
					
			notifyOnDeactivate(deployed.getDeploymentUnit(), deployed);
		}		
	}


	protected String getProcessId(String processSource) {

	    try {
	        InputSource inputSource = new InputSource(new StringReader(processSource));
	        String processId = (String) processIdXPathExpression.evaluate(inputSource, XPathConstants.STRING);

            return processId;
        } catch (XPathExpressionException e) {
            logger.error("Unable to find process id from process source due to {}", e.getMessage());
            return null;
        }
	}

}
