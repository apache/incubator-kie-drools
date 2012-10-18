package org.drools.builder.impl;

import com.thoughtworks.xstream.XStream;
import org.drools.KBaseUnit;
import org.drools.KnowledgeBase;
import org.drools.builder.KnowledgeJarBuilder;
import org.drools.kproject.KBase;
import org.drools.kproject.KProject;
import org.drools.kproject.KSession;
import org.drools.runtime.StatefulKnowledgeSession;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.drools.builder.impl.KBaseUnitCachingFactory.getOrCreateKBaseUnit;

public class KnowledgeJarBuilderImpl implements KnowledgeJarBuilder {

    public static final String KBASES_FOLDER = "kbases";
    public static final String KPROJECT_JAR_PATH = "META-INF/kproject.xml";
    public static final String KPROJECT_RELATIVE_PATH = "src/main/resources/" + KPROJECT_JAR_PATH;

    public File buildKJar(File rootFolder, File outputFolder, String jarName) {
        KProject kProject = (KProject)new XStream().fromXML(new File(rootFolder, KPROJECT_RELATIVE_PATH));
        return writeKJar(rootFolder, outputFolder, jarName, kProject);
    }

    public List<KBaseUnit> getKBaseUnits() {
        List<KBaseUnit> units = new ArrayList<KBaseUnit>();
        KProject kProject = loadKProject();
        for (KBase kBase : kProject.getKBases().values()) {
            units.add(getOrCreateKBaseUnit( kProject, kBase.getQName() ));
        }
        return units;
    }

    public List<KBaseUnit> getKBaseUnits(File rootFolder, File sourceFolder) {
        List<KBaseUnit> units = new ArrayList<KBaseUnit>();
        KProject kProject = (KProject)new XStream().fromXML(new File(rootFolder, KPROJECT_RELATIVE_PATH));
        for (KBase kBase : kProject.getKBases().values()) {
            units.add(new KBaseUnitImpl( kProject, kBase.getQName(), sourceFolder ));
        }
        return units;
    }

    public KBaseUnit getKBaseUnit(String kBaseName) {
        return getOrCreateKBaseUnit( loadKProject(), kBaseName );
    }

    public KnowledgeBase getKnowledgeBase(String kBaseName) {
        KBaseUnit kBaseUnit = getKBaseUnit(kBaseName);
        return kBaseUnit.hasErrors() ? null : kBaseUnit.getKnowledgeBase();
    }

    public StatefulKnowledgeSession getStatefulKnowlegeSession(String kSessionName) {
        KProject kProject = loadKProject();
        for (KBase kBase : kProject.getKBases().values()) {
            KSession kSession = kBase.getKSessions().get(kSessionName);
            if (kSession != null) {
                return getOrCreateKBaseUnit( kProject, kBase.getQName() ).newStatefulKnowledegSession(kSessionName);
            }
        }
        return null;
    }

    private KProject loadKProject() {
        InputStream kProjectStream = null;
        try {
            kProjectStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(KPROJECT_JAR_PATH);
            return (KProject)new XStream().fromXML(kProjectStream);
        } finally {
            try {
                if (kProjectStream != null) {
                    kProjectStream.close();
                }
            } catch (IOException e) { }
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
            java.io.File jarFile = new java.io.File( outputFolder, jarName );
            out = new ZipOutputStream( new FileOutputStream(jarFile) );

            writeJarEntries( out, rootFolder, entries );
            out.close();

            return jarFile;
        } catch ( IOException e ) {
            throw new RuntimeException(e);
        } finally {
            try {
                out.close();
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
