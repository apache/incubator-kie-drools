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
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.drools.builder.impl.KnowledgeJarBuilderImpl.KPROJECT_JAR_PATH;
import static org.drools.kproject.KProjectImpl.fromXML;

public class KnowledgeContainer {

    private Map<String, KBaseUnit> kBaseUnits = new HashMap<String, KBaseUnit>();
    private Map<String, String> kSessionNames = new HashMap<String, String>();

    public KnowledgeContainer() {
        ClassLoader classLoader = getClass().getClassLoader();
        for (KProject kProject : loadKProjects(classLoader)) {
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

        List<KProject> kProjects = loadKProjects(urlClassLoader);
        if (kProjects.isEmpty()) {
            return false;
        }

        boolean ok = true;
        for (KProject kProject : kProjects) {
            ok = build(urlClassLoader, kProject) && ok;
        }

        return ok;
    }

    private List<KProject> loadKProjects(ClassLoader classLoader) {
        List<KProject> kProjects = new ArrayList<KProject>();

        final Enumeration<URL> e;
        try {
            e = classLoader.getResources( KPROJECT_JAR_PATH );
        } catch ( IOException exc ) {
            return kProjects;
        }

        while ( e.hasMoreElements() ) {
            kProjects.add(fromXML(e.nextElement()));
        }
        return kProjects;
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
