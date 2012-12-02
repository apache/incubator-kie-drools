package org.kie.builder.impl;

import org.kie.builder.GAV;
import org.kie.builder.KieBaseModel;
import org.kie.builder.KieJar;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CompositeKieJar { // extends AbstractKieJar {
    // @TODO(mdp) temp delete to allow merge

//    private final Map<String, InternalKieJar> kieJars = new HashMap<String, InternalKieJar>();
//
//    public CompositeKieJar(GAV gav) {
//        super(gav);
//    }
//
//    public void addKieJar(KieJar kieJar) {
//        InternalKieJar internalKieJar = (InternalKieJar)kieJar;
//        kieJars.put(getKieJarKey(kieJar), internalKieJar);
//        
//        //internalKieJar.g
//        
//        for (String kieBaseName : internalKieJar.getKieBaseNames()) {
//            addKieBase(kieBaseName, internalKieJar.getKieBase(kieBaseName));
//        }
//    }
//
//    public void updateKieJar(KieJar kieJar) {
//        InternalKieJar oldKieJar = kieJars.remove(getKieJarKey(kieJar));
//        
//        if (oldKieJar != null) {
//            
//            for (String kieBaseName : oldKieJar.getKnowledgePackageCache().keySet()  ) {
//                getKnowledgePackageCache().remove( kieBaseName );
//            }
//        }
//        
//        addKieJar(kieJar);
//        kSessions = indexKieSessions();
//    }
//
//    public byte[] getBytes() {
//        throw new UnsupportedOperationException("org.kie.builder.impl.CompositeKieJar.getBytes -> TODO");
//    }
//
//    public InputStream getInputStream() {
//        throw new UnsupportedOperationException("org.kie.builder.impl.CompositeKieJar.getInputStream -> TODO");
//    }
//
//    public Collection<String> getFiles() {
//        List<String> files = new ArrayList<String>();
//        for (InternalKieJar kieJar : kieJars.values()) {
//            files.addAll(kieJar.getFiles());
//        }
//        return files;
//    }
//
//    public byte[] getBytes(String path) {
//        for (InternalKieJar kieJar : kieJars.values()) {
//            byte[] bytes = kieJar.getBytes(path);
//            if (bytes != null) {
//                return bytes;
//            }
//        }
//        return null;
//    }
//
//    public InputStream getInputStream(String path) {
//        for (InternalKieJar kieJar : kieJars.values()) {
//            InputStream is = kieJar.getInputStream(path);
//            if (is != null) {
//                return is;
//            }
//        }
//        return null;
//    }
//
//    protected Map<String, KieBaseModel> indexKieSessions() {
//        Map<String, KieBaseModel> kSessions = new HashMap<String, KieBaseModel>();
//        for (InternalKieJar kieJar : kieJars.values()) {
//            kSessions.putAll(((AbstractKieJar) kieJar).indexKieSessions());
//        }
//        return kSessions;
//    }
//
//    private String getKieJarKey(KieJar kieJar) {
//        GAV gav = kieJar.getGAV();
//        return gav.getGroupId() + ":" + gav.getArtifactId();
//    }
}
