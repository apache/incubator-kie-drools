package org.kie.builder;

import org.kie.conf.EqualityBehaviorOption;
import org.kie.conf.EventProcessingOption;

import java.util.List;
import java.util.Map;

public interface KieBaseModel {

    KieSessionModel newKieSessionModel(String name);

    KieBaseModel removeKieSessionModel(String qName);

    Map<String, KieSessionModel> getKieSessionModels();

    KieBaseModel addInclude(String kBaseQName);

    KieBaseModel removeInclude(String kBaseQName);

    String getName();
    
    KieBaseModel setName(String name);

    List<String> getPackages();
    
    KieBaseModel addPackage(String pkg);
    
    KieBaseModel removePackage(String pkg);

    EqualityBehaviorOption getEqualsBehavior();

    KieBaseModel setEqualsBehavior(EqualityBehaviorOption equalsBehaviour);

    EventProcessingOption getEventProcessingMode();

    KieBaseModel setEventProcessingMode(EventProcessingOption eventProcessingMode);
    
    KieBaseModel setScope(String scope);
    
    String getScope();

    boolean isDefault();

    KieBaseModel setDefault(boolean isDefault);
}
