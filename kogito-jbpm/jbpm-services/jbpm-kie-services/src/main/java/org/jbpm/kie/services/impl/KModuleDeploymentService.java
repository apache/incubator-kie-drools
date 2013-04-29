package org.jbpm.kie.services.impl;

import static org.kie.scanner.MavenRepository.getMavenRepository;

import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManagerFactory;

import org.apache.commons.codec.binary.Base64;
import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.drools.compiler.kie.builder.impl.KieContainerImpl;
import org.jbpm.kie.services.api.DeploymentUnit;
import org.jbpm.kie.services.api.IdentityProvider;
import org.jbpm.kie.services.api.Kjar;
import org.jbpm.kie.services.api.bpmn2.BPMN2DataService;
import org.jbpm.kie.services.impl.model.ProcessDesc;
import org.jbpm.runtime.manager.impl.KModuleRegisterableItemsFactory;
import org.jbpm.runtime.manager.impl.RuntimeEnvironmentBuilder;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieScanner;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.KieContainer;
import org.kie.scanner.MavenRepository;
import org.sonatype.aether.artifact.Artifact;

@ApplicationScoped
@Kjar
public class KModuleDeploymentService extends AbstractDeploymentService {

    @Inject
    private EntityManagerFactory emf;
    @Inject
    private IdentityProvider identityProvider; 
    @Inject
    private BPMN2DataService bpmn2Service;
    
    @Override
    public void deploy(DeploymentUnit unit) {
        super.deploy(unit);
        if (!(unit instanceof KModuleDeploymentUnit)) {
            throw new IllegalArgumentException("Invalid deployment unit provided - " + unit.getClass().getName());
        }
        KModuleDeploymentUnit kmoduleUnit = (KModuleDeploymentUnit) unit;
        DeployedUnitImpl deployedUnit = new DeployedUnitImpl(unit);
        KieServices ks = KieServices.Factory.get();
        MavenRepository repository = getMavenRepository();
        Artifact artifact = repository.resolveArtifact(kmoduleUnit.getIdentifier());
        
        ReleaseId releaseId = ks.newReleaseId(kmoduleUnit.getGroupId(), kmoduleUnit.getArtifactId(), kmoduleUnit.getVersion());
        KieContainer kieContainer = ks.newKieContainer(releaseId);
        InternalKieModule module = (InternalKieModule) ((KieContainerImpl)kieContainer).getKieModuleForKBase("KBase-test");
        Map<String, String> formsData = new HashMap<String, String>();
        Collection<String> files = module.getFileNames();
        for (String fileName : files) {
            if(fileName.matches(".+bpmn[2]?$")) {
                ProcessDesc process;
                try {
                    String processString = new String(module.getBytes(fileName), "UTF-8");
                    process = bpmn2Service.findProcessId(processString);
                    process.setEncodedProcessSource(Base64.encodeBase64String(processString.getBytes()));
                    process.setDeploymentId(unit.getIdentifier());
                    process.setForms(formsData);
                    deployedUnit.addAssetLocation(process.getId(), process);
                    
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            } else if (fileName.matches(".+ftl$")) {
                try {
                    String formContent = new String(module.getBytes(fileName), "UTF-8");
                    Pattern regex = Pattern.compile("(.{0}|.*/)([^/]*?)\\.ftl");
                    Matcher m = regex.matcher(fileName);
                    String key = fileName;
                    while (m.find()) {
                        key = m.group(2);
                    }
                    formsData.put(key, formContent);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }
        KieScanner scanner = ks.newKieScanner(kieContainer);

        scanner.scanNow();
        
        KieBase kbase = null;
        if (kmoduleUnit.getKbaseName() != null) {
            kbase = kieContainer.getKieBase(kmoduleUnit.getKbaseName());
        } else {
            kbase = kieContainer.getKieBase();
        }
        
        RuntimeEnvironmentBuilder builder = RuntimeEnvironmentBuilder.getDefault()
                .entityManagerFactory(emf)
                .knowledgeBase(kbase)
                .registerableItemsFactory(new KModuleRegisterableItemsFactory(kieContainer, kmoduleUnit.getKsessionName()));
        
        
        commonDeploy(unit, deployedUnit, builder.get());
//        scanner.start(10000);
    }

}
