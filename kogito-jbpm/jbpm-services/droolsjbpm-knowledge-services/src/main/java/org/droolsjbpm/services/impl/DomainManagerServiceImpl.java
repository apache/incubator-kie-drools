/*
 * Copyright 2013 JBoss by Red Hat.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.droolsjbpm.services.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.naming.InitialContext;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

import org.droolsjbpm.services.api.DomainManagerService;
import org.droolsjbpm.services.api.IdentityProvider;
import org.droolsjbpm.services.api.bpmn2.BPMN2DataService;
import org.droolsjbpm.services.domain.entities.Domain;
import org.droolsjbpm.services.domain.entities.Organization;
import org.droolsjbpm.services.domain.entities.RuntimeId;
import org.droolsjbpm.services.impl.audit.ServicesAwareAuditEventBuilder;
import org.droolsjbpm.services.impl.model.ProcessDesc;
import org.jboss.seam.transaction.Transactional;
import org.jbpm.process.audit.AbstractAuditLogger;
import org.jbpm.process.audit.AuditLoggerFactory;
import org.jbpm.runtime.manager.impl.DefaultRuntimeEnvironment;
import org.jbpm.runtime.manager.impl.RuntimeEnvironmentBuilder;
import org.jbpm.runtime.manager.impl.SimpleRuntimeEnvironment;
import org.jbpm.runtime.manager.impl.cdi.InjectableRegisterableItemsFactory;
import org.jbpm.shared.services.api.FileException;
import org.jbpm.shared.services.api.FileService;
import org.jbpm.shared.services.api.JbpmServicesPersistenceManager;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.EnvironmentName;
import org.kie.commons.java.nio.file.Path;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.manager.RuntimeEnvironment;
import org.kie.internal.runtime.manager.RuntimeManager;
import org.kie.internal.runtime.manager.RuntimeManagerFactory;

/**
 *
 * @author salaboy
 */
@Transactional
@ApplicationScoped
public class DomainManagerServiceImpl implements DomainManagerService {

    @Inject
    private BeanManager beanManager;
    @Inject
    private JbpmServicesPersistenceManager pm;
    @Inject
    private FileService fs;
    @Inject
    private RuntimeManagerFactory managerFactory;
 
    @Inject
    private EntityManagerFactory emf;
    
    @Inject
    private BPMN2DataService bpmn2Service;
    
    private Map<String, RuntimeManager> domainsMap = new HashMap<String, RuntimeManager>();
    
    // Process Path / Process Id - String 
    private Map<String, List<String>> processDefinitionNamesByDomain = new HashMap<String, List<String>>();
    
    @Inject
    private IdentityProvider identityProvider; 

    public void setPm(JbpmServicesPersistenceManager pm) {
        this.pm = pm;
    }

    @Override
    public List<Domain> getAllDomains() {
        return (List<Domain>) pm.queryStringInTransaction("select d from Domain d");
    }

    @Override
    public List<Domain> getAllDomainsByOrganization(long organizationId) {
        return (List<Domain>) pm.queryStringWithParametersInTransaction("select d from Domain d where d.organization.id = :organizationId",
                pm.addParametersToMap("organizationId", organizationId));
    }

    @Override
    public Domain getDomainByName(String domainName) {
        return ((List<Domain>) pm.queryStringWithParametersInTransaction("select d from Domain d where d.name = :domainName",
                pm.addParametersToMap("domainName", domainName))).get(0);
    }

    @Override
    public long storeDomain(Domain domain) {
        pm.persist(domain);
        return domain.getId();
    }

    @Override
    public void removeDomain(long domainId) {
        Domain domain = pm.find(Domain.class, domainId);
        pm.remove(domain);
    }

    @Override
    public List<Organization> getAllOrganizations() {
        return (List<Organization>) pm.queryStringInTransaction("select o from Organization o");
    }

    @Override
    public long storeOrganization(Organization organization) {
        if (organization.getDomains() == null || organization.getDomains().isEmpty()) {
            Map<String, Domain> domains = discoverDomains(organization, "processes");
            organization.setDomains(new ArrayList<Domain>(domains.values()));
        }
        pm.persist(organization);
        return organization.getId();
    }

    @Override
    public void removeOrganization(long organizationId) {
        Organization o = pm.find(Organization.class, organizationId);
        pm.remove(o);
    }

    @Override
    public Organization getOrganizationById(long organizationId) {
        return pm.find(Organization.class, organizationId);
    }

    @Override
    public Domain getDomainById(long domainId) {
        return pm.find(Domain.class, domainId);
    }

    @Override
    public void initOrganization(long organizationId) {
        Organization org = getOrganizationById(organizationId);
        Map<String, Domain> discovered = discoverDomains(org, "processes");
        for (Domain d : org.getDomains()) {
            initDomain(d.getId());
            discovered.remove(d.getName());
        }
        if (!discovered.isEmpty()) {
            List<Domain> domains = new ArrayList<Domain>(discovered.values());
            for (Domain d : domains) {
                pm.persist(d);
                initDomain(d.getId());
            }
        }
    }

    @Override
    public void initDomain(long domainId) {
        final Domain d = getDomainById(domainId);
        fs.fetchChanges();
        if( d != null){
            Collection<ProcessDesc> loadedProcesses = new ArrayList<ProcessDesc>();
            for (RuntimeId r : d.getRuntimes()) {
                String reference = r.getReference();
                // Create Runtime Manager Based on the Reference
                RuntimeEnvironmentBuilder builder = RuntimeEnvironmentBuilder.getDefault()
                        .entityManagerFactory(emf);
                
                UserTransaction ut = null;
                try {
                    ut = InitialContext.doLookup("java:comp/UserTransaction");
                } catch (Exception ex) {
                    try {
                        ut = InitialContext.doLookup(System.getProperty("jbpm.ut.jndi.lookup", "java:jboss/UserTransaction"));
                        builder.addEnvironmentEntry(EnvironmentName.TRANSACTION, ut);
                    } catch (Exception e1) {
                        throw new RuntimeException("Cannot find UserTransaction", e1);
                    }
                }
                AbstractAuditLogger auditLogger = AuditLoggerFactory.newJPAInstance(emf);
                ServicesAwareAuditEventBuilder auditEventBuilder = new ServicesAwareAuditEventBuilder();
                auditEventBuilder.setIdentityProvider(identityProvider);
                auditEventBuilder.setDomain(d);
                auditLogger.setBuilder(auditEventBuilder);
                
                builder.registerableItemsFactory(InjectableRegisterableItemsFactory.getFactory(beanManager, auditLogger));
                Iterable<Path> loadProcessFiles = null;

                try {
                    loadProcessFiles = fs.loadFilesByType(reference, ".+bpmn[2]?$");
                } catch (FileException ex) {
                    Logger.getLogger(DomainManagerServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
                }
                for (Path p : loadProcessFiles) {
                    String processString = "";
                    try {
                        processString = new String(fs.loadFile(p));
                        builder.addAsset(ResourceFactory.newByteArrayResource(processString.getBytes()), ResourceType.BPMN2);
                        ProcessDesc process = bpmn2Service.findProcessId(processString);
                        if (process != null) {
                            process.setDomainName(d.getName());
                            process.setOriginalPath(p.toString());
                            loadedProcesses.add(process);          
                        }
                        
                    } catch (Exception ex) {
                        Logger.getLogger(DomainManagerServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                
                if (!loadedProcesses.isEmpty()) {  
                    synchronized (domainsMap) {
                    
                        if (domainsMap.containsKey(d.getName())) {
                            RuntimeManager manager = domainsMap.remove(d.getName()); 
                            manager.close();
                            Collection<ProcessDesc> existingProcesses = getProcessesByDomainName(d.getName());
                            for (ProcessDesc toDelete : existingProcesses) {
                                pm.remove(toDelete);
                            }
                        } 
                        for (ProcessDesc process : loadedProcesses) {
                            pm.persist(process);
                        }
                        RuntimeManager manager = managerFactory.newSingletonRuntimeManager(builder.get(), d.getName());
    
                        domainsMap.put(d.getName(), manager);                    
                    }
                }
                
            }
        }
    }

    
    protected void addProcessDefinitionToDomain(String domainName,
            String processId) {
        if (processDefinitionNamesByDomain.get(domainName) == null) {
            processDefinitionNamesByDomain.put(domainName, new ArrayList<String>());
        }
        processDefinitionNamesByDomain.get(domainName).add(processId);
    }

    protected Map<String, Domain> discoverDomains(Organization organization, String location) {
        Iterable<Path> domainDirs = fs.listDirectories(location);
        Map<String, Domain> domains = new HashMap<String, Domain>();
        for (Path domainDir : domainDirs) {
            String dirName = domainDir.getFileName().toString();
            Domain domain = new Domain();
            domain.setName(dirName + " Domain");
            List<RuntimeId> runtimesRelease = new ArrayList<RuntimeId>();
            RuntimeId releaseRuntime = new RuntimeId();
            releaseRuntime.setName(dirName + " Runtime");
            releaseRuntime.setReference(location + "/" + dirName + "/");
            releaseRuntime.setType("Folder/Runtime Manager(Singleton)");

            runtimesRelease.add(releaseRuntime);

            domain.setRuntimes(runtimesRelease);
            domain.setOrganization(organization);

            domains.put(domain.getName(), domain);
        }
        
        return domains;
    }
    
    public Collection<ProcessDesc> getProcessesByDomainName(String domainName) {
      List<ProcessDesc> processes = (List<ProcessDesc>)pm.queryStringWithParametersInTransaction("select pd from ProcessDesc pd where pd.domainName=:domainName GROUP BY pd.id ORDER BY pd.dataTimeStamp DESC",
              pm.addParametersToMap("domainName", domainName));
      return processes;
    }
    
    public Map<String, RuntimeManager> getDomainsMap() {
        return domainsMap;
    }

    public RuntimeManager getRuntimesByDomain(String domainName) {
        return domainsMap.get(domainName);
    }

    public void clearDomains() {
        domainsMap.clear();
    }
}
