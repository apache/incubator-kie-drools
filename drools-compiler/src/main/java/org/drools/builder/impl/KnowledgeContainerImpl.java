package org.drools.builder.impl;

import org.drools.KBaseUnit;
import org.drools.KnowledgeBase;
import org.drools.builder.KnowledgeBuilderConfiguration;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.KnowledgeContainer;
import org.drools.compiler.PackageBuilderConfiguration;
import org.drools.kproject.KBase;
import org.drools.kproject.KProject;
import org.drools.kproject.KSession;
import org.drools.runtime.StatefulKnowledgeSession;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.drools.builder.impl.KBaseUnitCachingFactory.evictKBaseUnit;
import static org.drools.builder.impl.KBaseUnitCachingFactory.getOrCreateKBaseUnit;
import static org.drools.core.util.IoUtils.copyFile;
import static org.drools.kproject.KProjectImpl.fromXML;

public class KnowledgeContainerImpl implements KnowledgeContainer {

    public static final String KBASES_FOLDER = "kbases";
    public static final String KPROJECT_JAR_PATH = "META-INF/kproject.xml";
    public static final String KPROJECT_RELATIVE_PATH = "src/main/resources/" + KPROJECT_JAR_PATH;

    private final KnowledgeBuilderConfiguration kConf;

    private final Map<String, KBase> kBases = new HashMap<String, KBase>();
    private final Map<String, String> kSessions = new HashMap<String, String>();
    private final Map<String, KnowledgeBuilderConfiguration> kConfs = new HashMap<String, KnowledgeBuilderConfiguration>();

    public KnowledgeContainerImpl(KnowledgeBuilderConfiguration kConf) {
        this.kConf = kConf;
        ClassLoader classLoader = ((PackageBuilderConfiguration)kConf).getClassLoader();
        indexKSessions(loadKProjects(classLoader), null, false);
    }

    public static void clearCache() {
        KBaseUnitCachingFactory.clear();
    }

    public void deploy(File kJar) {
        URLClassLoader urlClassLoader;
        try {
            urlClassLoader = new URLClassLoader( new URL[] { kJar.toURI().toURL() } );
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        indexKSessions( loadKProjects(urlClassLoader),
                        KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration(null, urlClassLoader),
                        true );
    }

    public File buildKJar(File rootFolder, File outputFolder, String jarName) {
        KProject kProject = fromXML(new File(rootFolder, KPROJECT_RELATIVE_PATH));
        return writeKJar(rootFolder, outputFolder, jarName, kProject);
    }

    public List<KBaseUnit> getKBaseUnits() {
        List<KBaseUnit> units = new ArrayList<KBaseUnit>();
        for (KBase kBase : kBases.values()) {
            units.add(getOrCreateKBaseUnit(getKConf(kBase.getQName()), kBase));
        }
        return units;
    }

    public List<KBaseUnit> getKBaseUnits(File rootFolder, File sourceFolder) {
        List<KBaseUnit> units = new ArrayList<KBaseUnit>();
        KProject kProject = fromXML(new File(rootFolder, KPROJECT_RELATIVE_PATH));
        for (KBase kBase : kProject.getKBases().values()) {
            units.add(new KBaseUnitImpl( kConf, kBase, sourceFolder ));
        }
        return units;
    }

    public void copyKBasesToOutput(File rootFolder, File outputFolder) {
        File kProjectFile = new File(rootFolder, KPROJECT_RELATIVE_PATH);
        KProject kProject = fromXML(new File(rootFolder, KPROJECT_RELATIVE_PATH));
        copyFile(kProjectFile, new File(outputFolder, KPROJECT_JAR_PATH));

        for (KBase kBase : kProject.getKBases().values()) {
            for (String kBaseFile : kBase.getFiles()) {
                String file = KBASES_FOLDER + "/" + kBase.getQName() + "/" + kBaseFile;
                copyFile(new File(rootFolder + "/src", file), new File(outputFolder, file));
            }
        }
    }

    public KBaseUnit getKBaseUnit(String kBaseName) {
        return getOrCreateKBaseUnit(getKConf(kBaseName), kBases.get(kBaseName));
    }

    public KnowledgeBase getKnowledgeBase(String kBaseName) {
        KBaseUnit kBaseUnit = getKBaseUnit(kBaseName);
        return kBaseUnit.hasErrors() ? null : kBaseUnit.getKnowledgeBase();
    }

    public StatefulKnowledgeSession getStatefulKnowlegeSession(String kSessionName) {
        return getKBaseUnit(kSessions.get(kSessionName)).newStatefulKnowledegSession(kSessionName);
    }

    private KnowledgeBuilderConfiguration getKConf(String kBaseName) {
        KnowledgeBuilderConfiguration conf = kConfs.get(kBaseName);
        return conf != null ? conf : kConf;
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

    private void indexKSessions(List<KProject> kProjects, KnowledgeBuilderConfiguration conf, boolean doEvict) {
        for (KProject kProject : kProjects) {
            for (KBase kBase : kProject.getKBases().values()) {
                cleanUpExistingKBase(kBase, doEvict);
                kBases.put(kBase.getQName(), kBase);
                if (conf != null) {
                    kConfs.put(kBase.getQName(), conf);
                }
                for (KSession kSession : kBase.getKSessions().values()) {
                    kSessions.put(kSession.getQName(), kBase.getQName());
                }
            }
        }
    }

    private void cleanUpExistingKBase(KBase kBase, boolean doEvict) {
        if (doEvict) {
            evictKBaseUnit(kBase.getQName());
        }
        KBase oldKbase = kBases.get(kBase.getQName());
        if (oldKbase != null) {
            kConfs.remove(oldKbase.getQName());
            for (KSession kSession : oldKbase.getKSessions().values()) {
                kSessions.remove(kSession.getQName());
            }
        }
    }

    private File writeKJar(File rootFolder, File outputFolder, String jarName, KProject kProject) {
        Map<String, String> jarEntries = new HashMap<String, String>();
        jarEntries.put(KPROJECT_RELATIVE_PATH, KPROJECT_JAR_PATH);
        for (KBase kBase : kProject.getKBases().values()) {
            for (String kBaseFile : kBase.getFiles()) {
                String file = KBASES_FOLDER + "/" + kBase.getQName() + "/" + kBaseFile;
                jarEntries.put("src/" + file, file);
            }
        }
        return writeAsJar(rootFolder, outputFolder, jarName, jarEntries);
    }

    private File writeAsJar(File rootFolder, File outputFolder, String jarName, Map<String, String> entries) {
        ZipOutputStream out = null;
        try {
            outputFolder.mkdirs();
            File jarFile = new File( outputFolder, jarName );
            out = new ZipOutputStream( new FileOutputStream(jarFile) );

            writeJarEntries( out, rootFolder, entries );
            out.close();

            return jarFile;
        } catch ( IOException e ) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) { }
        }
    }

    private void writeJarEntries(ZipOutputStream out, File rootFolder, Map<String, String> entries) throws IOException {
        byte[] buf = new byte[1024];
        for ( Map.Entry<String, String> entry : entries.entrySet() ) {
            out.putNextEntry( new ZipEntry( entry.getValue() ) );

            FileInputStream fis = new FileInputStream( rootFolder + "/" + entry.getKey() );

            int len;
            while ( (len = fis.read( buf )) > 0 ) {
                out.write( buf, 0, len );
            }

            out.closeEntry();
            fis.close();
        }
    }
}
