package org.kie.builder;

import java.util.Map;


public interface KieProject {

    KieBaseModel newKieBaseModel(String name);
    
    void removeKieBaseModel(String qName);

    Map<String, KieBaseModel> getKieBaseModels();

    String toXML();
}