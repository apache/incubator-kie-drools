package org.drools.kproject;

import org.drools.KBaseUnit;
import org.drools.builder.KnowledgeBuilderConfiguration;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.impl.KBaseUnitImpl;
import org.drools.runtime.StatefulKnowledgeSession;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;

import static org.drools.builder.impl.KnowledgeJarBuilderImpl.KPROJECT_JAR_PATH;
import static org.drools.kproject.KProjectImpl.fromXML;

public class KnowledgeContainer {

    private Map<String, KBaseUnit> kBaseUnits = new HashMap<String, KBaseUnit>();
    private Map<String, String> kSessionNames = new HashMap<String, String>();

    public KnowledgeContainer() {
        ClassLoader classLoader = getClass().getClassLoader();
        KProject kProject = loadKProject(classLoader);
        if (kProject != null) {
            build(classLoader, kProject);
        }
    }

    public boolean deploy(java.io.File kJar) {
        InputStream kProjectStream = null;
        URLClassLoader urlClassLoader = null;
        try {
            urlClassLoader = new URLClassLoader( new URL[] { kJar.toURI().toURL() } );
        } catch (MalformedURLException e) {
            return false;
        }

        KProject kProject = loadKProject(urlClassLoader);
        if (kProject == null) {
            return false;
        }

        return build(urlClassLoader, kProject);
    }

    private KProject loadKProject(ClassLoader classLoader) {
        InputStream kProjectStream = classLoader.getResourceAsStream(KPROJECT_JAR_PATH);
        if (kProjectStream == null) {
            return null;
        }
        KProject kProject = fromXML(kProjectStream);
        try {
            kProjectStream.close();
        } catch (IOException e) { }
        return kProject;
    }

    private boolean build(ClassLoader classLoader, KProject kProject) {
        KnowledgeBuilderConfiguration kConf = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration(null, classLoader);
        boolean buildOk = true;
        for (KBase kBase : kProject.getKBases().values()) {
            KBaseUnit kBaseUnit = new KBaseUnitImpl(kConf, kProject, kBase.getQName());
            buildOk = !kBaseUnit.hasErrors() && buildOk;
            kBaseUnits.put(kBaseUnit.getKBaseName(), kBaseUnit);
            for (KSession kSession : kBase.getKSessions().values()) {
                kSessionNames.put(kSession.getQName(), kBaseUnit.getKBaseName());
            }
        }
        return buildOk;
    }

    public StatefulKnowledgeSession createKnowledgeSession(String kSessionName) {
        String kBaseName = kSessionNames.get(kSessionName);
        KBaseUnit kBaseUnit = kBaseUnits.get(kBaseName);
        return kBaseUnit.newStatefulKnowledegSession(kSessionName);
    }
}
