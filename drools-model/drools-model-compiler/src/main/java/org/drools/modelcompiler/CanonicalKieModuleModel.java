/*
 * Copyright 2005 JBoss Inc
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
