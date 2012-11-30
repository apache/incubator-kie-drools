package org.kie.builder;

public interface KieRepository {
    
    public void setDefaultGAV(GAV gav);

    public GAV getDefaultGAV();    
    
    void addKieJar(KieJar kjar);

    Messages verfyKieJar(GAV gav);
    
    KieJar getKieJar(GAV gav);
}
