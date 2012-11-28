package org.drools.builder.impl;

import org.drools.compiler.PackageBuilderConfiguration;
import org.kie.builder.KieBaseModel;
import org.kie.builder.KieProject;
import org.kie.builder.KieSessionModel;
import org.kie.KBaseUnit;
import org.kie.KnowledgeBase;
import org.kie.builder.KnowledgeBuilderConfiguration;
import org.kie.builder.KnowledgeContainer;
import org.kie.runtime.StatefulKnowledgeSession;
import org.kie.runtime.StatelessKnowledgeSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import static org.drools.builder.impl.KBaseUnitCachingFactory.evictKBaseUnit;
import static org.drools.builder.impl.KBaseUnitCachingFactory.getOrCreateKBaseUnit;
import static org.drools.core.util.IoUtils.copyFile;
import static org.drools.kproject.KieBaseModelImpl.getFiles;
import static org.drools.kproject.KieProjectImpl.fromXML;

public class KnowledgeContainerImpl implements KnowledgeContainer {

    private static final Logger log = LoggerFactory.getLogger(KnowledgeContainer.class);

    public static final String KBASES_FOLDER = "src/kbases";
    public static final String KPROJECT_JAR_PATH = "META-INF/kproject.xml";
    public static final String KPROJECT_RELATIVE_PATH = "src/main/resources/" + KPROJECT_JAR_PATH;

    private final Map<String, KieBaseModel> kBases = new HashMap<String, KieBaseModel>();
    private final Map<String, String> kSessions = new HashMap<String, String>();
    private final Map<String, URL> urls = new HashMap<String, URL>();

    private final ClassLoader classLoader;

    public KnowledgeContainerImpl(KnowledgeBuilderConfiguration kConf) {
        classLoader = ((PackageBuilderConfiguration)kConf).getClassLoader();
        loadKProjects(classLoader);
    }

    public static void clearCache() {
        KBaseUnitCachingFactory.clear();
    }

    public void deploy(File... kJars) {
        for (File kJar : kJars) {
            if (kJar.isDirectory()) {
                deployDirectory(kJar);
            } else {
                deployJar(kJar);
            }
        }
    }

    private void deployJar(File kJar) {
        ZipFile zipFile;
        try {
            zipFile = new ZipFile( kJar );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ZipEntry zipEntry = zipFile.getEntry( KPROJECT_JAR_PATH );
        if (zipEntry != null) {
            InputStream zipStream = null;
            try {
                zipStream = zipFile.getInputStream( zipEntry );
                indexKSessions(fromXML(zipStream), kJar.toURI().toURL(), true);
            } catch (IOException e) {
                log.error("Unable to access kJar " + kJar.getAbsolutePath() + " caused by: " + e.getLocalizedMessage());
            } finally {
                if (zipStream != null) {
                    try {
                        zipStream.close();
                    } catch (IOException e) { }
                }
            }
        }
    }

    private void deployDirectory(File kJar) {
        File kProkjectFile = new File(kJar, KPROJECT_JAR_PATH);
        if (kProkjectFile.exists()) {
            try {
                indexKSessions(fromXML(kProkjectFile), kProkjectFile.toURI().toURL(), true);
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public File buildKJar(File rootFolder, File outputFolder, String jarName) {
        KieProject kieProject = fromXML(new File(rootFolder, KPROJECT_RELATIVE_PATH));
        return writeKJar(rootFolder, outputFolder, jarName, kieProject);
    }

    public List<KBaseUnit> getKBaseUnits() {
        List<KBaseUnit> units = new ArrayList<KBaseUnit>();
        for (KieBaseModel kieBaseModel : kBases.values()) {
            units.add(getOrCreateKBaseUnit(urls.get(kieBaseModel.getName()), kieBaseModel));
        }
        return units;
    }

    public List<KBaseUnit> getKBaseUnits(File rootFolder, File sourceFolder) {
        List<KBaseUnit> units = new ArrayList<KBaseUnit>();
        KieProject kieProject = fromXML(new File(rootFolder, KPROJECT_RELATIVE_PATH));
        for (KieBaseModel kieBaseModel : kieProject.getKieBaseModels().values()) {
            units.add(new KBaseUnitImpl( sourceFolder.getAbsolutePath() + "/" + kieBaseModel.getName(), kieBaseModel, classLoader ));
        }
        return units;
    }

    public void copyKBasesToOutput(File rootFolder, File outputFolder) {
        File kProjectFile = new File(rootFolder, KPROJECT_RELATIVE_PATH);
        KieProject kieProject = fromXML(new File(rootFolder, KPROJECT_RELATIVE_PATH));
        copyFile(kProjectFile, new File(outputFolder, KPROJECT_JAR_PATH));

        for (KieBaseModel kieBaseModel : kieProject.getKieBaseModels().values()) {
            for (String kBaseFile : getFiles(kieBaseModel.getName(), new File(rootFolder, KBASES_FOLDER))) {
                copyFile(new File(rootFolder, KBASES_FOLDER + "/" + kBaseFile), new File(outputFolder, kBaseFile));
            }
        }
    }

    public KBaseUnit getKBaseUnit(String kBaseName) {
        KieBaseModel kieBaseModel = kBases.get(kBaseName);
        if (kieBaseModel == null) {
            throw new RuntimeException("Unknown KnowledgeBase: " + kBaseName);
        }
        KBaseUnitImpl unit = getOrCreateKBaseUnit(urls.get(kBaseName), kieBaseModel);
        if (!unit.hasIncludes() && kieBaseModel.getIncludes() != null) {
            for ( String include : kieBaseModel.getIncludes() ) {
                unit.addInclude(kBases.get(include));
            }
        }
        return unit;
    }

    public KnowledgeBase getKnowledgeBase(String kBaseName) {
        KBaseUnit kBaseUnit = getKBaseUnit(kBaseName);
        return kBaseUnit.hasErrors() ? null : kBaseUnit.getKnowledgeBase();
    }

    public StatefulKnowledgeSession getStatefulKnowlegeSession(String kSessionName) {
        return getKBaseUnit(kSessions.get(kSessionName)).newStatefulKnowledegSession(kSessionName);
    }

    public StatelessKnowledgeSession getStatelessKnowlegeSession(String kSessionName) {
        String kBaseName = kSessions.get(kSessionName);
        if (kBaseName == null) {
            throw new RuntimeException("Unknown KnowledgeSession: " + kSessionName);
        }
        return getKBaseUnit(kBaseName).newStatelessKnowledegSession(kSessionName);
    }

    private void loadKProjects(ClassLoader classLoader) {
        Enumeration<URL> e = null;
        try {
            e = classLoader.getResources( KPROJECT_JAR_PATH );
        } catch ( IOException exc ) {
            log.error("Unable to load kproject(s) caused by: " + exc);
        }

        while ( e.hasMoreElements() ) {
            URL url = e.nextElement();
            indexKSessions(fromXML(url), url, false);
        }
    }

    private void indexKSessions(KieProject kieProject, URL url, boolean doEvict) {
        for (KieBaseModel kieBaseModel : kieProject.getKieBaseModels().values()) {
            cleanUpExistingKBase(kieBaseModel, doEvict);
            kBases.put(kieBaseModel.getName(), kieBaseModel);
            urls.put(kieBaseModel.getName(), url);
            for (KieSessionModel kieSessionModel : kieBaseModel.getKieSessionModels().values()) {
                kSessions.put(kieSessionModel.getName(), kieBaseModel.getName());
            }
        }
    }

    private void cleanUpExistingKBase(KieBaseModel kieBaseModel, boolean doEvict) {
        if (doEvict) {
            evictKBaseUnit(kieBaseModel.getName());
        }
        KieBaseModel oldKbase = kBases.get(kieBaseModel.getName());
        if (oldKbase != null) {
            urls.remove(oldKbase.getName());
            for (KieSessionModel kieSessionModel : oldKbase.getKieSessionModels().values()) {
                kSessions.remove(kieSessionModel.getName());
            }
        }
    }

    private File writeKJar(File rootFolder, File outputFolder, String jarName, KieProject kieProject) {
        File kBasesFolder = new File(rootFolder, KBASES_FOLDER);
        Map<String, String> jarEntries = new HashMap<String, String>();
        jarEntries.put(KPROJECT_RELATIVE_PATH, KPROJECT_JAR_PATH);
        for (KieBaseModel kieBaseModel : kieProject.getKieBaseModels().values()) {
            for (String kBaseFile : getFiles(kieBaseModel.getName(), kBasesFolder)) {
                jarEntries.put(KBASES_FOLDER + "/" + kBaseFile, kBaseFile);
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
