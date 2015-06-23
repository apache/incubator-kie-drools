/*
 * Copyright 2015 JBoss Inc
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

import org.drools.compiler.kie.builder.impl.event.KieModuleDiscovered;
import org.drools.compiler.kie.builder.impl.event.KieServicesEventListerner;
import org.drools.core.util.IoUtils;
import org.drools.core.util.StringUtils;
import org.drools.compiler.kproject.ReleaseIdImpl;
import org.drools.compiler.kproject.models.KieModuleModelImpl;
import org.drools.compiler.kproject.xml.PomModel;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.builder.KieRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static org.drools.compiler.kie.builder.impl.KieBuilderImpl.setDefaultsforEmptyKieModule;
import static org.drools.core.common.ProjectClassLoader.createProjectClassLoader;

/**
 * Discovers all KieModules on the classpath, via the kmodule.xml file.
 * KieBaseModels and KieSessionModels are then indexed, with helper lookups
 * Each resulting KieModule is added to the KieRepository
 *
 */
public class ClasspathKieProject extends AbstractKieProject {

    private static final Logger             log               = LoggerFactory.getLogger( ClasspathKieProject.class );

    public static final String OSGI_KIE_MODULE_CLASS_NAME     = "org.drools.osgi.compiler.OsgiKieModule";

    private Map<ReleaseId, InternalKieModule>     kieModules  = new HashMap<ReleaseId, InternalKieModule>();

    private Map<String, InternalKieModule>  kJarFromKBaseName = new HashMap<String, InternalKieModule>();

    private final KieRepository kieRepository;
    
    private final ClassLoader parentCL;

    private ClassLoader classLoader;

    private final WeakReference<KieServicesEventListerner> listener;

    ClasspathKieProject(ClassLoader parentCL, WeakReference<KieServicesEventListerner> listener) {
        this.kieRepository = KieServices.Factory.get().getRepository();
        this.listener = listener;
        this.parentCL = parentCL;
    }

    public void init() {
        this.classLoader = createProjectClassLoader(parentCL);
        discoverKieModules();
        indexParts(kieModules.values(), kJarFromKBaseName);
    }

    public ReleaseId getGAV() {
        return null;
    }

    public long getCreationTimestamp() {
        return 0L;
    }

    public void discoverKieModules() {
        String[] configFiles = {KieModuleModelImpl.KMODULE_JAR_PATH, KieModuleModelImpl.KMODULE_SPRING_JAR_PATH};
        for ( String configFile : configFiles) {
            final Enumeration<URL> e;
            try {
                e = classLoader.getResources(configFile );
            } catch ( IOException exc ) {
                log.error( "Unable to find and build index of "+configFile+"." + exc.getMessage() );
                return;
            }

            // Map of kmodule urls
            while ( e.hasMoreElements() ) {
                URL url = e.nextElement();
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
        if (url.toString().startsWith("bundle:")) {
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
            log.error("It is necessary to have the drools-osgi-integration module on the path in order to create a KieProject from an ogsi bundle", e);
            throw new RuntimeException(e);
        }
        try {
            return (InternalKieModule) m.invoke(null, url);
        } catch (Exception e) {
            log.error("Failure creating a OsgiKieModule caused by: " + e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    private static void fetchKModuleFromSpring(URL kModuleUrl, String fixedURL){
        try{
            Class clazz = Class.forName("org.kie.spring.KModuleSpringMarshaller");
            Method method = clazz.getDeclaredMethod("fromXML", java.net.URL.class, String.class);
            method.invoke(null, kModuleUrl, fixedURL);
        } catch (Exception e) {
            log.error("It is necessary to have the kie-spring module on the path in order to create a KieProject from a spring context", e);
            throw new RuntimeException(e);
        }
    }

    private static InternalKieModule fetchKModule(URL url, String fixedURL) {
        if ( url.getPath().endsWith("-spring.xml")) {
            // the entire kmodule creation is happening in the kie-spring module,
            // hence we force a null return
            fetchKModuleFromSpring(url, fixedURL);
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

        return createInternalKieModule(url, fixedURL, kieProject, releaseId, rootPath);
    }

    public static InternalKieModule createInternalKieModule(URL url, String fixedURL, KieModuleModel kieProject, ReleaseId releaseId, String rootPath) {
        File file = new File( rootPath );
        return file.isDirectory() ?
               new FileKieModule( releaseId, kieProject, file ) :
               new ZipKieModule( releaseId, kieProject, file );
    }

    public static String getPomProperties(String urlPathToAdd) {
        String pomProperties = null;
        String rootPath = urlPathToAdd;
        if ( rootPath.lastIndexOf( ':' ) > 0 ) {
            rootPath = IoUtils.asSystemSpecificPath( rootPath, rootPath.lastIndexOf( ':' ) );
        }

        if ( urlPathToAdd.endsWith( ".apk" ) || urlPathToAdd.endsWith( ".jar" ) || urlPathToAdd.endsWith( "/content" ) ) {
            pomProperties = getPomPropertiesFromZipFile(rootPath);
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

    private static String getPomPropertiesFromZipFile(String rootPath) {
        File actualZipFile = new File( rootPath );
        if ( !actualZipFile.exists() ) {
            log.error( "Unable to load pom.properties from" + rootPath + " as jarPath cannot be found\n" + rootPath );
            return null;
        }

        ZipFile zipFile = null;

        try {
            zipFile = new ZipFile( actualZipFile );

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
        } finally {
            try {
                zipFile.close();
            } catch ( IOException e ) {
                log.error( "Error when closing InputStream to " + rootPath + "\n" + e.getMessage() );
            }
        }
        return null;
    }

    private static String getPomPropertiesFromFileSystem(String rootPath) {
        Reader reader = null;
        try {
            File file = KieBuilderImpl.findPomProperties( new File( rootPath ) );
            if ( file == null ) {
                log.warn( "Unable to find pom.properties in " + rootPath );
                return null;
            }
            reader = new InputStreamReader( new FileInputStream( file ), IoUtils.UTF8_CHARSET );
            log.debug( "Found and used pom.properties " + file);
            return StringUtils.toString( reader );
        } catch ( Exception e ) {
            log.warn( "Unable to load pom.properties tried recursing down from " + rootPath + "\n" + e.getMessage() );
        } finally {
            if ( reader != null ) {
                try {
                    reader.close();
                } catch ( IOException e ) {
                    log.error( "Error when closing InputStream to " + rootPath + "\n" + e.getMessage() );
                }
            }
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
            FileInputStream fis = null;
            try {
                fis = new FileInputStream( file ) ;
                PomModel pomModel = PomModel.Parser.parse( rootPath + "/pom.xml", fis);

                KieBuilderImpl.validatePomModel( pomModel ); // throws an exception if invalid

                ReleaseIdImpl gav = (ReleaseIdImpl) pomModel.getReleaseId();

                String str =  KieBuilderImpl.generatePomProperties( gav );
                log.info( "Recursed up folders, found and used pom.xml " + file );

                return str;
            } catch ( Exception e ) {
                log.error( "As folder project tried to fall back to pom.xml " + file + "\nbut failed with exception:\n" + e.getMessage() );
            } finally {
                if ( fis != null ) {
                    try {
                        fis.close();
                    } catch ( IOException e ) {
                        log.error( "Error when closing InputStream to " + file + "\n" + e.getMessage() );
                    }
                }
            }
        } else {
            log.warn( "As folder project tried to fall back to pom.xml, but could not find one for " + file );
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
                                             urlPath.indexOf( '!' ) );
            }
        } else if ( "vfs".equals( urlType ) ) {
            urlPath = getPathForVFS(url);
        } else {
            if (url.toString().contains("-spring.xml")){
                urlPath = urlPath.substring( 0, urlPath.length() - ("/" + KieModuleModelImpl.KMODULE_SPRING_JAR_PATH).length() );
            } else {
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
        String urlString = url.toString();
        int kModulePos = urlString.length() - ("/" + KieModuleModelImpl.KMODULE_JAR_PATH).length();
        boolean isInJar = urlString.substring(kModulePos - 4, kModulePos).equals(".jar");

        try {
            Method m = Class.forName("org.jboss.vfs.VirtualFile").getMethod("getPhysicalFile");
            Object content = url.openConnection().getContent();
            File f = (File)m.invoke(content);
            String path = f.getPath();

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

    public ClassLoader getClonedClassLoader() {
        return createProjectClassLoader(classLoader.getParent());
    }

    public InputStream getPomAsStream() {
        return classLoader.getResourceAsStream("pom.xml");
    }
}
