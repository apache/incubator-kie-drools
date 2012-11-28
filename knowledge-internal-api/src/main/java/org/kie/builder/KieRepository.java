package org.kie.builder;

public interface KieRepository {
    void addKieJar(KieJar kjar);

    Problems verfyKieJar(GAV gav);
    KieJar getKieJar(GAV gav);
}
