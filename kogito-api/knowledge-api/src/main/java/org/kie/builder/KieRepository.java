package org.kie.builder;

public interface KieRepository {
    void addKieJar(KieJar kjar);

    Messages verfyKieJar(GAV gav);
    KieJar getKieJar(GAV gav);
}
