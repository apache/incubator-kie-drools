package org.drools.modelcompiler;

import java.util.List;

import org.drools.compiler.kproject.models.KieModuleModelImpl;
import org.drools.model.Model;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.builder.model.KieSessionModel;

public interface CanonicalKieModuleModel {

    String getVersion();

    List<Model> getModels();

    List<Model> getModelsForKieBase( String kieBaseName );

    default KieModuleModel getKieModuleModel() {
        KieModuleModel kModuleModel = new KieModuleModelImpl();
        KieBaseModel kieBaseModel = kModuleModel.newKieBaseModel( "defaultKieBase" ).addPackage( "*" ).setDefault( true );
        kieBaseModel.newKieSessionModel( "defaultKieSession" ).setDefault( true );
        kieBaseModel.newKieSessionModel( "defaultStatelessKieSession" ).setType( KieSessionModel.KieSessionType.STATELESS ).setDefault( true );
        return kModuleModel;
    }

    default ReleaseId getReleaseId() {
        return KieServices.get().getRepository().getDefaultReleaseId();
    }
}
