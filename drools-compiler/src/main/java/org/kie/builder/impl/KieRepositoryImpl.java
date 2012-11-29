package org.kie.builder.impl;

import org.kie.builder.GAV;
import org.kie.builder.KieJar;
import org.kie.builder.KieRepository;
import org.kie.builder.Messages;

import java.util.HashMap;
import java.util.Map;

public class KieRepositoryImpl implements KieRepository {

    static final KieRepositoryImpl INSTANCE = new KieRepositoryImpl();

    private final Map<GAV, KieJar> kieJars = new HashMap<GAV, KieJar>();

    public void addKieJar(KieJar kjar) {
        kieJars.put(kjar.getGAV(), kjar);
    }

    public Messages verfyKieJar(GAV gav) {
        throw new UnsupportedOperationException("org.kie.builder.impl.KieRepositoryImpl.verfyKieJar -> TODO");

    }

    public KieJar getKieJar(GAV gav) {
        return kieJars.get(gav);
    }
}
