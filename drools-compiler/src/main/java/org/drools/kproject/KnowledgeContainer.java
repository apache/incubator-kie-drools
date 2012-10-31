package org.drools.kproject;

import org.drools.KBaseUnit;
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

    public boolean deploy(java.io.File kJar) {
        InputStream kProjectStream = null;
        URLClassLoader urlClassLoader = null;
        try {
            urlClassLoader = new URLClassLoader( new URL[] { kJar.toURI().toURL() } );
        } catch (MalformedURLException e) {
            return false;
        }

        kProjectStream = urlClassLoader.getResourceAsStream(KPROJECT_JAR_PATH);
        KProject kProject = fromXML(kProjectStream);
        try {
            kProjectStream.close();
        } catch (IOException e) { }

        ClassLoader originalClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(urlClassLoader);
            return build(kProject);
        } finally {
            Thread.currentThread().setContextClassLoader(originalClassLoader);
        }
    }

    private boolean build(KProject kProject) {
        boolean buildOk = true;
        for (KBase kBase : kProject.getKBases().values()) {
            KBaseUnit kBaseUnit = new KBaseUnitImpl(kProject, kBase.getQName());
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
