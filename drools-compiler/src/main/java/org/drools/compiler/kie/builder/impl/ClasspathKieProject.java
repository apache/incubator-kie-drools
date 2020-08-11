/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.compiler.kie.builder.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import org.appformer.maven.support.PomModel;
import org.drools.compiler.kie.builder.impl.event.KieModuleDiscovered;
import org.drools.compiler.kie.builder.impl.event.KieServicesEventListerner;
import org.drools.compiler.kproject.ReleaseIdImpl;
import org.drools.compiler.kproject.models.KieModuleModelImpl;
import org.drools.core.util.IoUtils;
import org.drools.core.util.StringUtils;
import org.kie.api.KieServices;
import org.kie.api.builder.KieRepository;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.model.KieModuleModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.drools.compiler.kie.builder.impl.KieBuilderImpl.setDefaultsforEmptyKieModule;
import static org.drools.compiler.kproject.models.KieModuleModelImpl.KMODULE_JAR_PATH;
import static org.drools.reflective.classloader.ProjectClassLoader.createProjectClassLoader;

/**
 * Discovers all KieModules on the classpath, via the kmodule.xml file.
 * KieBaseModels and KieSessionModels are then indexed, with helper lookups
 * Each resulting KieModule is added to the KieRepository
 *
 */
public class ClasspathKieProject extends AbstractKieProject {

    private static final Logger             log               = LoggerFactory.getLogger( ClasspathKieProject.class );

    public static final String OSGI_KIE_MODULE_CLASS_NAME     = "org.kie.osgi.compiler.OsgiKieModule";

    private Map<ReleaseId, InternalKieModule>     kieModules  = new HashMap<>();

    private Map<String, InternalKieModule>  kJarFromKBaseName = new HashMap<>();

    private final KieRepository kieRepository;
    
    private final ClassLoader parentCL;

    private ClassLoader classLoader;

    private final WeakReference<KieServicesEventListerner> listener;

    private ReleaseId classPathreleaseId;

    ClasspathKieProject(ClassLoader parentCL, WeakReference<KieServicesEventListerner> listener, ReleaseId classPathreleaseId) {
        this.kieRepository = KieServices.Factory.get().getRepository();
        this.listener = listener;
        this.parentCL = parentCL;
        this.classPathreleaseId = classPathreleaseId;
    }

    ClasspathKieProject(ClassLoader parentCL, WeakReference<KieServicesEventListerner> listener) {
        this(parentCL, listener, null);
    }

    public void init() {
        this.classLoader = createProjectClassLoader(parentCL);
        discoverKieModules();
        indexParts(null, kieModules.values(), kJarFromKBaseName);
    }

    public ReleaseId getGAV() {
        return classPathreleaseId;
    }

    public long getCreationTimestamp() {
        return 0L;
    }

    public void discoverKieModules() {
        String[] configFiles = {KieModuleModelImpl.KMODULE_JAR_PATH, KieModuleModelImpl.KMODULE_SPRING_JAR_PATH};
        for ( String configFile : configFiles) {
            final Set<URL> resources = new HashSet<>();
            try {
                ClassLoader currentClassLoader = classLoader;
                while (currentClassLoader != null) {
                    Enumeration<URL> list = currentClassLoader.getResources(configFile);
                    while (list.hasMoreElements()) {
                        resources.add(list.nextElement());
                    }
                    currentClassLoader = currentClassLoader.getParent();
                }
            } catch ( IOException exc ) {
                log.error( "Unable to find and build index of "+configFile+"." + exc.getMessage() );
                return;
            }

            // Map of kmodule urls
            Iterator<URL> e = resources.iterator();
            while (e.hasNext()) {
                URL url = e.next();
                notifyKieModuleFound(url);
                try {
                    InternalKieModule kModule = fetchKModule(url);

                    if (kModule != null) {
                        ReleaseId releaseId = kModule.getReleaseId();
                        kieModules.put(releaseId, kModule);

                        log.debug( "Discovered classpath module " + releaseId.toExternalForm() );

                        kieRepository.addKieModule(kModule);
                    }

                } catch ( Exception exc ) {
                    log.error( "Unable to build index of kmodule.xml url=" + url.toExternalForm() + "\n" + exc.getMessage() );
                }
            }
        }
    }

    private void notifyKieModuleFound(URL url) {
        log.info( "Found kmodule: " + url);
        if (listener != null && listener.get() != null) {
            listener.get().onKieModuleDiscovered(new KieModuleDiscovered(url.toString()));
        }
    }

    public static InternalKieModule fetchKModule(URL url) {
        if (url.toString().equals( "resource:" + KMODULE_JAR_PATH )) {
            return InternalKieModuleProvider.getFromClasspath();
        }
        if (url.toString().startsWith("bundle:") || url.toString().startsWith("bundleresource:")) {
            return fetchOsgiKModule(url);
        }
        return fetchKModule(url, fixURLFromKProjectPath(url));
    }

    private static InternalKieModule fetchOsgiKModule(URL url) {
        Method m;
        try {
            Class<?> c = Class.forName(OSGI_KIE_MODULE_CLASS_NAME);
            m = c.getMethod("create", URL.class);
        } catch (Exception e) {
            log.error("It is necessary to have the kie-osgi-integration module on the path in order to create a KieProject from an OSGi bundle", e);
            throw new RuntimeException(e);
        }
        try {
            return (InternalKieModule) m.invoke(null, url);
        } catch (Exception e) {
            log.error("Failure creating a OsgiKieModule caused by: " + e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    private static void fetchKModuleFromSpring(URL kModuleUrl) {
        try{
            Class clazz = Class.forName("org.kie.spring.KModuleSpringMarshaller");
            Method method = clazz.getDeclaredMethod("fromXML", java.net.URL.class);
            method.invoke(null, kModuleUrl);
        } catch (Exception e) {
            log.error("It is necessary to have the kie-spring module on the path in order to create a KieProject from a spring context", e);
            throw new RuntimeException(e);
        }
    }

    private static InternalKieModule fetchKModule(URL url, String fixedURL) {
        if ( url.getPath().endsWith("-spring.xml")) {
            // the entire kmodule creation is happening in the kie-spring module,
            // hence we force a null return
            fetchKModuleFromSpring(url);
            return null;
        }
        KieModuleModel kieProject = KieModuleModelImpl.fromXML( url );

        setDefaultsforEmptyKieModule(kieProject);

        String pomProperties = getPomProperties( fixedURL );
        if (pomProperties == null) {
            log.warn("Cannot find maven pom properties for this project. Using the container's default ReleaseId");
        }

        ReleaseId releaseId = pomProperties != null ?
                              ReleaseIdImpl.fromPropertiesString(pomProperties) :
                              KieServices.Factory.get().getRepository().getDefaultReleaseId();

        String rootPath = fixedURL;
        if ( rootPath.lastIndexOf( ':' ) > 0 ) {
            rootPath = IoUtils.asSystemSpecificPath( rootPath, rootPath.lastIndexOf( ':') );
        }

        return createInternalKieModule(kieProject, releaseId, rootPath);
    }

    public static InternalKieModule createInternalKieModule(KieModuleModel kieProject, ReleaseId releaseId, String rootPath) {
        return InternalKieModuleProvider.get( releaseId, kieProject, new File( rootPath ) );
    }

    public static String getPomProperties(String urlPathToAdd) {
        String pomProperties;
        String rootPath = urlPathToAdd;
        if ( rootPath.lastIndexOf( ':' ) > 0 ) {
            rootPath = IoUtils.asSystemSpecificPath( rootPath, rootPath.lastIndexOf( ':' ) );
        }

        if ( urlPathToAdd.endsWith( ".apk" ) || isJarFile( urlPathToAdd, rootPath ) || urlPathToAdd.endsWith( "/content" ) ) {
            
            if (rootPath.indexOf(".jar!") > 0) {
                pomProperties = getPomPropertiesFromZipStream(rootPath);
            } else {
                pomProperties = getPomPropertiesFromZipFile(rootPath);
            }
        } else {
            pomProperties = getPomPropertiesFromFileSystem(rootPath);
            if (pomProperties == null) {
                int webInf = rootPath.indexOf("/WEB-INF");
                if (webInf > 0) {
                    rootPath = rootPath.substring(0, webInf);
                    pomProperties = getPomPropertiesFromFileSystem(rootPath);
                }
            }
            if (pomProperties == null) {
                pomProperties = generatePomPropertiesFromPom(rootPath);
            }
        }

        if (pomProperties == null) {
            log.warn( "Unable to load pom.properties from" + urlPathToAdd );
        }
        return pomProperties;
    }

    private static boolean isJarFile(String urlPathToAdd, String rootPath) {
        boolean result = false;
        if (urlPathToAdd.endsWith( ".jar" )) {
            File actualZipFile = new File( rootPath );
            if (actualZipFile.exists() && actualZipFile.isFile()) {
                result = true;
            } else if (urlPathToAdd.indexOf( ".jar!" ) > 0) {
                // nested jar inside uberjar if the path includes .jar!
                result = true;
            }
        } 
        return result;
    }

    private static String getPomPropertiesFromZipFile(String rootPath) {
        File actualZipFile = new File( rootPath );
        if ( !actualZipFile.exists() ) {
            if (rootPath.indexOf(".jar!") > 0) {
                return getPomPropertiesFromZipStream(rootPath);
            }
            
            log.error( "Unable to load pom.properties from" + rootPath + " as jarPath cannot be found\n" + rootPath );
            return null;
        }

        try (ZipFile zipFile = new ZipFile( actualZipFile )) {
            String file = KieBuilderImpl.findPomProperties( zipFile );
            if ( file == null ) {
                log.warn( "Unable to find pom.properties in " + rootPath );
                return null;
            }
            ZipEntry zipEntry = zipFile.getEntry( file );

            String pomProps = StringUtils.readFileAsString(
                    new InputStreamReader( zipFile.getInputStream( zipEntry ), IoUtils.UTF8_CHARSET ) );
            log.debug( "Found and used pom.properties " + file);
            return pomProps;
        } catch ( Exception e ) {
            log.error( "Unable to load pom.properties from " + rootPath + "\n" + e.getMessage() );
        }
        return null;
    }
    
    private static String getPomPropertiesFromZipStream(String rootPath) {
       
        rootPath = rootPath.substring( rootPath.lastIndexOf( '!' ) + 1 );
        // read jar file from uber-jar
        InputStream in = ClasspathKieProject.class.getResourceAsStream(rootPath);
        ZipInputStream zipIn = new ZipInputStream(in);
        try {
            ZipEntry entry = zipIn.getNextEntry();
            while (entry != null) {
                // process each entry
                String fileName = entry.getName();
                if ( fileName.endsWith( "pom.properties" ) && fileName.startsWith( "META-INF/maven/" ) ) {
                    String pomProps = StringUtils.readFileAsString( new InputStreamReader( zipIn, IoUtils.UTF8_CHARSET ) );
                    //zipIn.closeEntry();
                    
                    return pomProps;
                }
                
                // get next entry if needed
                entry = zipIn.getNextEntry();
            }
        } catch ( Exception e ) {
            log.error( "Unable to load pom.properties from zip input stream " + rootPath + "\n" + e.getMessage() );
        } finally {
            try {
                if (zipIn != null) {
                    zipIn.close();
                }
            } catch ( IOException e ) {
                log.error( "Error when closing zip InputStream to " + rootPath + "\n" + e.getMessage() );
            }
        }   
        
        return null;
    }

    private static String getPomPropertiesFromFileSystem(String rootPath) {
        File file = KieBuilderImpl.findPomProperties( new File( rootPath ) );
        if ( file == null ) {
            log.warn( "Unable to find pom.properties in " + rootPath );
            return null;
        }
        log.debug( "Found and used pom.properties " + file);
        try (Reader reader = new InputStreamReader( new FileInputStream( file ), IoUtils.UTF8_CHARSET )) {
            return StringUtils.toString( reader );
        } catch ( Exception e ) {
            log.warn( "Unable to load pom.properties tried recursing down from " + rootPath + "\n" + e.getMessage() );
        }
        return null;
    }

    private static String generatePomPropertiesFromPom(String rootPath) {
        // recurse until we reach root or find a pom.xml
        File file = null;
        for ( File folder = new File( rootPath ); folder.getParent() != null; folder = new File( folder.getParent() ) ) {
            file = new File( folder, "pom.xml" );
            if ( file.exists() ) {
                break;
            }
            file = null;
        }

        if ( file != null ) {
            try (FileInputStream fis = new FileInputStream( file )) {
                PomModel pomModel = PomModel.Parser.parse( rootPath + "/pom.xml", fis);

                KieBuilderImpl.validatePomModel( pomModel ); // throws an exception if invalid

                org.appformer.maven.support.AFReleaseId gav = pomModel.getReleaseId();

                String str =  KieBuilderImpl.generatePomProperties( gav );
                log.info( "Recursed up folders, found and used pom.xml " + file );

                return str;
            } catch ( Exception e ) {
                log.error( "As folder project tried to fall back to pom.xml " + file + "\nbut failed with exception:\n" + e.getMessage() );
            }
        } else {
            log.warn( "As folder project tried to fall back to pom.xml, but could not find one" );
        }
        return null;
    }

    public static String fixURLFromKProjectPath(URL url) {
        String urlPath = url.toExternalForm();

        // determine resource type (eg: jar, file, bundle)
        String urlType = "file";
        int colonIndex = urlPath.indexOf( ":" );
        if ( colonIndex != -1 ) {
            urlType = urlPath.substring( 0,
                                         colonIndex );
        }

        urlPath = url.getPath();

        if ( "jar".equals( urlType ) ) {
            // switch to using getPath() instead of toExternalForm()
            if ( urlPath.indexOf( '!' ) > 0 ) {
                urlPath = urlPath.substring( 0,
                                             urlPath.lastIndexOf( '!' ) );
            }
        } else if ( "vfs".equals( urlType ) ) {
            urlPath = getPathForVFS(url);
        } else {
            if (url.toString().contains("-spring.xml")){
                urlPath = urlPath.substring( 0, urlPath.length() - ("/" + KieModuleModelImpl.KMODULE_SPRING_JAR_PATH).length() );
            } else if (url.toString().endsWith(KieModuleModelImpl.KMODULE_JAR_PATH)) {
                urlPath = urlPath.substring( 0,
                        urlPath.length() - ("/" + KieModuleModelImpl.KMODULE_JAR_PATH).length() );
            }
        }

        if (urlPath.endsWith(".jar!")) {
            urlPath = urlPath.substring( 0, urlPath.length() - 1 );
        }

        // remove any remaining protocols, normally only if it was a jar
        int firstSlash = urlPath.indexOf( '/' );
        colonIndex = firstSlash > 0 ? urlPath.lastIndexOf( ":", firstSlash ) : urlPath.lastIndexOf( ":" );
        if ( colonIndex >= 0 ) {
            urlPath = IoUtils.asSystemSpecificPath(urlPath, colonIndex);
        }

        try {
            urlPath = URLDecoder.decode( urlPath,
                                         "UTF-8" );
        } catch ( UnsupportedEncodingException e ) {
            throw new IllegalArgumentException( "Error decoding URL (" + url + ") using UTF-8",
                                                e );
        }

        log.debug("KieModule URL type=" + urlType + " url=" + urlPath);

        return urlPath;
    }

    private static String getPathForVFS(URL url) {
        Method m = null;
        try {
            m = Class.forName("org.jboss.vfs.VirtualFile").getMethod("getPhysicalFile");
        } catch (Exception e) {
            try {
                // Try to retrieve the VirtualFile class also on TCCL
                m = Class.forName("org.jboss.vfs.VirtualFile", true, Thread.currentThread().getContextClassLoader()).getMethod("getPhysicalFile");
            } catch (Exception e1) {
                // VirtualFile is not available on the classpath - ignore
                log.warn( "Found virtual file " + url + " but org.jboss.vfs.VirtualFile is not available on the classpath" );
            }
        }
        Method m2 = null;
        try {
            m2 = Class.forName("org.jboss.vfs.VFS").getMethod("getChild", URI.class);
        } catch (Exception e) {
            try {
                // Try to retrieve the org.jboss.vfs.VFS class also on TCCL
                m2 = Class.forName("org.jboss.vfs.VFS", true, Thread.currentThread().getContextClassLoader()).getMethod("getChild", URI.class);
            } catch (Exception e1) {
                // VFS is not available on the classpath - ignore
                log.warn( "Found virtual file " + url + " but org.jboss.vfs.VFS is not available on the classpath" );
            }
        }

        if (m == null || m2 == null) {
            return url.getPath();
        }

        String path = null;
        try {
            File f = (File)m.invoke( m2.invoke(null, url.toURI()) );
            path = f.getPath();
        } catch (Exception e) {
            log.error( "Error when reading virtual file from " + url.toString(), e );
        }

        if (path == null) {
            return url.getPath();
        }

        String urlString = url.toString();
        if (!urlString.contains( "/" + KieModuleModelImpl.KMODULE_JAR_PATH )) {
            return path;
        }

        int kModulePos = urlString.length() - ("/" + KieModuleModelImpl.KMODULE_JAR_PATH).length();
        boolean isInJar = urlString.substring(kModulePos - 4, kModulePos).equals(".jar");

        try {
            if (isInJar && path.contains("contents" + File.separator)) {
                String jarName = urlString.substring(0, kModulePos);
                jarName = jarName.substring(jarName.lastIndexOf('/')+1);
                String jarFolderPath = path.substring( 0, path.length() - ("contents/" + KieModuleModelImpl.KMODULE_JAR_PATH).length() );
                String jarPath = jarFolderPath + jarName;
                path = new File(jarPath).exists() ? jarPath : jarFolderPath + "content";
            } else if (path.endsWith(File.separator + KieModuleModelImpl.KMODULE_FILE_NAME)) {
                path = path.substring( 0, path.length() - ("/" + KieModuleModelImpl.KMODULE_JAR_PATH).length() );
            }

            log.info( "Virtual file physical path = " + path );
            return path;
        } catch (Exception e) {
            log.error( "Error when reading virtual file from " + url.toString(), e );
        }
        return url.getPath();
    }

    public InternalKieModule getKieModuleForKBase(String kBaseName) {
        return this.kJarFromKBaseName.get( kBaseName );
    }

    public ClassLoader getClassLoader() {
        return this.classLoader;
    }

    public InputStream getPomAsStream() {
        return classLoader.getResourceAsStream("pom.xml");
    }
}
