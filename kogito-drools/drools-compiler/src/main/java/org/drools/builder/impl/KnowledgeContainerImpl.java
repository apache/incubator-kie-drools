package org.drools.builder.impl;

import org.drools.compiler.PackageBuilderConfiguration;
import org.drools.kproject.KBase;
import org.drools.kproject.KProject;
import org.drools.kproject.KSession;
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
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
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
import static org.drools.kproject.KBaseImpl.getFiles;
import static org.drools.kproject.KProjectImpl.fromXML;

public class KnowledgeContainerImpl implements KnowledgeContainer {

    private static final Logger log = LoggerFactory.getLogger(KnowledgeContainer.class);

    public static final String KBASES_FOLDER = "kbases";
    public static final String KPROJECT_JAR_PATH = "META-INF/kproject.xml";
    public static final String KPROJECT_RELATIVE_PATH = "src/main/resources/" + KPROJECT_JAR_PATH;

    private final Map<String, KBase> kBases = new HashMap<String, KBase>();
    private final Map<String, String> kSessions = new HashMap<String, String>();
    private final Map<String, String> urls = new HashMap<String, String>();

    private final ClassLoader classLoader;

    public KnowledgeContainerImpl(KnowledgeBuilderConfiguration kConf) {
        classLoader = ((PackageBuilderConfiguration)kConf).getClassLoader();
        loadKProjects(classLoader, false);
    }

    public static void clearCache() {
        KBaseUnitCachingFactory.clear();
    }

    public void deploy(File... kJars) {
        for (File kJar : kJars) {
            URLClassLoader urlClassLoader;
            try {
                urlClassLoader = new URLClassLoader( new URL[] { kJar.toURI().toURL() } );
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
            loadKProjects(urlClassLoader, true );
        }
    }

    public File buildKJar(File rootFolder, File outputFolder, String jarName) {
        KProject kProject = fromXML(new File(rootFolder, KPROJECT_RELATIVE_PATH));
        return writeKJar(rootFolder, outputFolder, jarName, kProject);
    }

    public List<KBaseUnit> getKBaseUnits() {
        List<KBaseUnit> units = new ArrayList<KBaseUnit>();
        for (KBase kBase : kBases.values()) {
            units.add(getOrCreateKBaseUnit(urls.get(kBase.getQName()), kBase));
        }
        return units;
    }

    public List<KBaseUnit> getKBaseUnits(File rootFolder, File sourceFolder) {
        List<KBaseUnit> units = new ArrayList<KBaseUnit>();
        KProject kProject = fromXML(new File(rootFolder, KPROJECT_RELATIVE_PATH));
        for (KBase kBase : kProject.getKBases().values()) {
            units.add(new KBaseUnitImpl( sourceFolder.getAbsolutePath() + "/" + kBase.getQName(), kBase, classLoader ));
        }
        return units;
    }

    public void copyKBasesToOutput(File rootFolder, File outputFolder) {
        File kProjectFile = new File(rootFolder, KPROJECT_RELATIVE_PATH);
        KProject kProject = fromXML(new File(rootFolder, KPROJECT_RELATIVE_PATH));
        copyFile(kProjectFile, new File(outputFolder, KPROJECT_JAR_PATH));

        for (KBase kBase : kProject.getKBases().values()) {
            for (String kBaseFile : getFiles(kBase.getQName(), rootFolder)) {
                String file = KBASES_FOLDER + "/" + kBase.getQName() + "/" + kBaseFile;
                copyFile(new File(rootFolder + "/src", file), new File(outputFolder, kBaseFile));
            }
        }
    }

    public KBaseUnit getKBaseUnit(String kBaseName) {
        KBase kBase = kBases.get(kBaseName);
        if (kBase == null) {
            throw new RuntimeException("Unknown KnowledgeBase: " + kBaseName);
        }
        KBaseUnitImpl unit = getOrCreateKBaseUnit(urls.get(kBaseName), kBase);
        if (!unit.hasIncludes() && kBase.getIncludes() != null) {
            for ( String include : kBase.getIncludes() ) {
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
        return getKBaseUnit(kSessions.get(kSessionName)).newStatelessKnowledegSession(kSessionName);
    }

    private void loadKProjects(ClassLoader classLoader, boolean doEvict) {
        Enumeration<URL> e = null;
        try {
            e = classLoader.getResources( KPROJECT_JAR_PATH );
        } catch ( IOException exc ) {
            log.error("Unable to load kproject(s) caused by: " + exc);
        }

        while ( e.hasMoreElements() ) {
            URL url = e.nextElement();
            indexKSessions(fromXML(url), fixURL(url), doEvict);
        }
    }

    private void indexKSessions(KProject kProject, String url, boolean doEvict) {
        for (KBase kBase : kProject.getKBases().values()) {
            cleanUpExistingKBase(kBase, doEvict);
            kBases.put(kBase.getQName(), kBase);
            urls.put(kBase.getQName(), url);
            for (KSession kSession : kBase.getKSessions().values()) {
                kSessions.put(kSession.getQName(), kBase.getQName());
            }
        }
    }

    private void cleanUpExistingKBase(KBase kBase, boolean doEvict) {
        if (doEvict) {
            evictKBaseUnit(kBase.getQName());
        }
        KBase oldKbase = kBases.get(kBase.getQName());
        if (oldKbase != null) {
            urls.remove(oldKbase.getQName());
            for (KSession kSession : oldKbase.getKSessions().values()) {
                kSessions.remove(kSession.getQName());
            }
        }
    }

    private File writeKJar(File rootFolder, File outputFolder, String jarName, KProject kProject) {
        File kBasesFolder = new File(rootFolder, "src/" + KBASES_FOLDER);
        Map<String, String> jarEntries = new HashMap<String, String>();
        jarEntries.put(KPROJECT_RELATIVE_PATH, KPROJECT_JAR_PATH);
        for (KBase kBase : kProject.getKBases().values()) {
            for (String kBaseFile : getFiles(kBase.getQName(), kBasesFolder)) {
                jarEntries.put("src/" + KBASES_FOLDER + "/" + kBaseFile, kBaseFile);
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

    private String fixURL(URL url) {
        String urlPath = url.toExternalForm();

        // determine resource type (eg: jar, file, bundle)
        String urlType = "file";
        int colonIndex = urlPath.indexOf( ":" );
        if ( colonIndex != -1 ) {
            urlType = urlPath.substring( 0, colonIndex );
        }

        urlPath = url.getPath();


        if ( "jar".equals( urlType ) ) {
            // switch to using getPath() instead of toExternalForm()

            if ( urlPath.indexOf( '!' ) > 0 ) {
                urlPath = urlPath.substring( 0, urlPath.indexOf( '!' ) );
            }
        } else {
            urlPath = urlPath.substring( 0, urlPath.length() - "/META-INF/kproject.xml".length() );
        }


        // remove any remaining protocols, normally only if it was a jar
        colonIndex = urlPath.lastIndexOf( ":" );
        if ( colonIndex >= 0 ) {
            urlPath = urlPath.substring( colonIndex +  1  );
        }

        try {
            urlPath = URLDecoder.decode(urlPath, "UTF-8");
        } catch ( UnsupportedEncodingException e ) {
            throw new IllegalArgumentException( "Error decoding URL (" + url + ") using UTF-8", e );
        }

        log.debug( "KProject URL Type + URL: " + urlType + ":" + urlPath );

        return urlPath;
    }
}
