package org.droolsjbpm.services.impl;

import static org.kie.scanner.MavenRepository.getMavenRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;
import javax.persistence.EntityManagerFactory;

import org.drools.compiler.kie.builder.impl.KieContainerImpl;
import org.drools.compiler.kie.builder.impl.KieModuleKieProject;
import org.drools.compiler.kie.builder.impl.KieProject;
import org.droolsjbpm.services.api.DeploymentUnit;
import org.jbpm.runtime.manager.impl.RuntimeEnvironmentBuilder;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieScanner;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.KieContainer;
import org.kie.scanner.MavenRepository;
import org.sonatype.aether.artifact.Artifact;

@ApplicationScoped
@Alternative
public class KModuleDeploymentService extends AbstractDeploymentService {

    @Inject
    private EntityManagerFactory emf;
    
    @Override
    public void deploy(DeploymentUnit unit) {
        if (!(unit instanceof KModuleDeploymentUnit)) {
            throw new IllegalArgumentException("Invalid deployment unit provided - " + unit.getClass().getName());
        }
        KModuleDeploymentUnit kmoduleUnit = (KModuleDeploymentUnit) unit;
        
        KieServices ks = KieServices.Factory.get();
        MavenRepository repository = getMavenRepository();
        Artifact artifact = repository.resolveArtifact(kmoduleUnit.getIdentifier());
        
        ReleaseId releaseId = ks.newReleaseId(kmoduleUnit.getGroupId(), kmoduleUnit.getArtifactId(), kmoduleUnit.getVersion());
        KieContainer kieContainer = ks.newKieContainer(releaseId);
        KieModuleKieProject project = (KieModuleKieProject) ((KieContainerImpl)kieContainer).getKieProject();
        
        
        KieScanner scanner = ks.newKieScanner(kieContainer);

        scanner.scanNow();
        
        KieBase kbase = kieContainer.getKieBase("KBase-test");
        
        RuntimeEnvironmentBuilder builder = RuntimeEnvironmentBuilder.getDefault()
                .entityManagerFactory(emf)
                .knowledgeBase(kbase);
        
        DeployedUnitImpl deployedUnit = new DeployedUnitImpl(unit);
        commonDdeploy(unit, deployedUnit, builder.get());
//        scanner.start(10000);
    }

}
