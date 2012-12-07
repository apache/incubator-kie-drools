package org.kie.builder;

import org.kie.conf.AssertBehaviorOption;
import org.kie.conf.EventProcessingOption;

import java.util.Map;
import java.util.Set;

public interface KieBaseModel {

    KieSessionModel newKieSessionModel(String name);

    KieBaseModel removeKieSessionModel(String qName);

    Map<String, KieSessionModel> getKieSessionModels();

    KieBaseModel addInclude(String kBaseQName);

    KieBaseModel removeInclude(String kBaseQName);

    String getName();
    
    KieBaseModel setName(String name);

    Set<String> getPackages();
    
    KieBaseModel addPackage(String pkg);
    
    KieBaseModel removePackage(String pkg);

    AssertBehaviorOption getEqualsBehavior();

    KieBaseModel setEqualsBehavior(AssertBehaviorOption equalsBehaviour);

    EventProcessingOption getEventProcessingMode();

    KieBaseModel setEventProcessingMode(EventProcessingOption eventProcessingMode);
    
    KieBaseModel setScope(String scope);
    
    String getScope();
}
