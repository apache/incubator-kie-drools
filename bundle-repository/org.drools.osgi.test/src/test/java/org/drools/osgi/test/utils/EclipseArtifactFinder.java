package org.drools.osgi.test.utils;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import org.apache.log4j.Logger;
import org.osgi.framework.Constants;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

/**
 * Find a packaged and unpackaged artifact from Eclipse Workspace and Eclipse Target areas.
 * Does not use groupId to resolve bundles.
 * 
 * @author Frederic Conrotte
 */
public class EclipseArtifactFinder {

    private static final Logger log = Logger.getLogger(EclipseArtifactFinder.class);
    
    private static final String s_PROP_WORKSPACE_AREA = "eclipse.workspace.area";
    private static final String s_PROP_TARGET_AREA = "eclipse.target.area";

    private Set<Plugin> m_WorkspacePlugins = new HashSet<Plugin>();
    private Set<Plugin> m_TargetPlugins = new HashSet<Plugin>();

    private static final String FILE_SCHEME = "file:///";
    private static final String REFERENCE_PROTOCOL = "reference";
    
    private final FileFilter m_DirectoryFilter = new FileFilter() {
        public boolean accept(File file) {
            return file.isDirectory();
        }
    };

    private final FileFilter m_JARFileFilter = new FileFilter() {
        public boolean accept(File file) {
            String fileExtension = getExtension(file.getName());
            return file.isFile() && "jar".equals(fileExtension);
        }

        private String getExtension(String fileName) {
            String ext;

            int dotPlace = fileName.lastIndexOf('.');

            if (dotPlace >= 0)
               ext = fileName.substring( dotPlace + 1 );
            else
               ext = "";
            
            return ext;
        }
    };

    private final FileFilter m_ManifestDirectoryFilter = new FileFilter() {
        public boolean accept(File file) {
            return file.isDirectory() && "META-INF".equalsIgnoreCase(file.getName());
        }
    };

    private FilenameFilter m_ManifestFilter = new FilenameFilter(){

        public boolean accept(File file, String aName) {
            return "MANIFEST.MF".equals(aName);
        }
    };

    /**
     * Quick model for an Eclipse plugin 
     */
    private class Plugin
    {
        private String m_BundleSymbolicName, m_BundleVersion;
        private Resource m_Path;
        private boolean m_IsExploded;

        public Plugin(String aBundleSymbolicName, String aVersion, Resource aPath, boolean isExploded) 
        {
            m_BundleSymbolicName = aBundleSymbolicName;
            m_BundleVersion = aVersion;
            m_Path = aPath;
            m_IsExploded = isExploded;
        }
        
        public boolean isExploded() {
            return m_IsExploded;
        }

        public boolean match(String artifactId, String version)
        {
            return m_BundleSymbolicName.equals(artifactId) && m_BundleVersion.startsWith(version);
        }

        public String getBundleSymbolicName() {
            return m_BundleSymbolicName;
        }

        public void setBundleSymbolicName(String aBundleSymbolicName) {
            m_BundleSymbolicName = aBundleSymbolicName;
        }

        public String getBundleVersion() {
            return m_BundleVersion;
        }

        public void setBundleVersion(String aBundleVersion) {
            m_BundleVersion = aBundleVersion;
        }

        public Resource getPath() {
            return m_Path;
        }

        public void setPath(Resource aPath) {
            m_Path = aPath;
        }
        
        @Override
        public boolean equals(Object aOther) {
            if (aOther instanceof Plugin == false)
                return false;

            if (this == aOther)
                return true;

            Plugin rhs = (Plugin) aOther;
            
            return m_BundleSymbolicName.equals(rhs.getBundleSymbolicName()) && m_BundleVersion.equals(rhs.getBundleVersion()) && m_Path.equals(rhs.getPath());
        }
        
        @Override
        public int hashCode() {
            return m_BundleSymbolicName.hashCode() + m_BundleVersion.hashCode() + m_Path.hashCode();
        }
        
        @Override
        public String toString() {
            return m_BundleSymbolicName + " " + m_BundleVersion;
        }
    }
    
    public Resource findArtifact(String aArtifactId, String aVersion) throws IOException {
        
        if (m_WorkspacePlugins.isEmpty()) {

            File folder = getEclipseWorkspace();
            if (folder != null)
                importPluginFromFolder(folder, m_WorkspacePlugins);
        }

        if (m_TargetPlugins.isEmpty()) {

            File[] folders = getEclipseTarget();
            
            for (File folder : folders)
                importPluginFromFolder(folder, m_TargetPlugins);

        }

        for (Plugin plugin : m_WorkspacePlugins) {
            if (plugin.match(aArtifactId, aVersion))
            {
                if (plugin.isExploded())
                    return getExplodedPluginResource(plugin);
                else
                    return getJARPluginResource(plugin);
            }
        }

        for (Plugin plugin : m_TargetPlugins) {
            if (plugin.match(aArtifactId, aVersion))
            {
                if (plugin.isExploded())
                    return getExplodedPluginResource(plugin);
                else
                    return getJARPluginResource(plugin);
            }
        }

        return null;
    }

    private void importPluginFromFolder(File folder, Set<Plugin> plugins) throws IOException {
        
        log.info("Importing plugins from folder " + folder.getAbsolutePath());
        
        Set<Resource> eclipseProjects = new HashSet<Resource>();
        
        // Scan plugins exploded as unpacked JAR directories
        for (File projectFolder : folder.listFiles(m_DirectoryFilter))
            eclipseProjects.add(new FileSystemResource(projectFolder));

        for (Resource resource : eclipseProjects) {
            Manifest man = getManifestFromProject(resource);
            if (man != null)
                addPlugin(plugins, resource, man, true);
        }

        Set<Resource> packagedBundles = new HashSet<Resource>();

        // Scan plugins provided as JAR files
        for (File jarFile : folder.listFiles(m_JARFileFilter))
            packagedBundles.add(new FileSystemResource(jarFile));

        for (Resource resource : packagedBundles) {
            Manifest man = getManifestFromJAR(resource);
            if (man != null)
                addPlugin(plugins, resource, man, false);
        }
    }

    private void addPlugin(Set<Plugin> plugins, Resource resource, Manifest man, boolean isExploded) {
        // read the manifest
        Attributes attrs = man.getMainAttributes();
        String symbolicName = attrs.getValue(Constants.BUNDLE_SYMBOLICNAME);
        
        if (symbolicName != null)
        {
            symbolicName = symbolicName.replace("singleton:=true", "");
            symbolicName = symbolicName.trim().replace(";", "");
            
            String version = attrs.getValue(Constants.BUNDLE_VERSION);
            
            if (symbolicName != null && version != null)
                plugins.add(new Plugin(symbolicName, version, resource, isExploded));
        }
    }

    private Resource getJARPluginResource(Plugin aPlugin) throws IOException {
        return new FileSystemResource(aPlugin.getPath().getFile());
    }

    private Resource getExplodedPluginResource(Plugin plugin) throws IOException {
        
            URL url = new URL(REFERENCE_PROTOCOL, null, FILE_SCHEME + plugin.getPath().getFile().getCanonicalPath() + File.separator + "target" + File.separator + "classes");
            
            return new UnpackedOSGiBundleResource(url);
    }

    /**
     * Return an Eclipse project's Manifest
     * @param aResource an Eclipse project resource path
     * @return The project Manifest, null if none exist
     * @throws IOException 
     */
    private Manifest getManifestFromProject(Resource aResource) throws IOException {
        
        try {
            for (File manifestFolder : aResource.getFile().listFiles(m_ManifestDirectoryFilter)) {
                for (File manifestFile : manifestFolder.listFiles(m_ManifestFilter))
                    return new Manifest(new FileInputStream(manifestFile));
            }
        } catch (IOException aEx) {
            log.error("Problem reading MANIFEST.MF from resource" + aResource.getFilename());
            throw aEx;
        }
        
        return null;
    }

    /**
     * Return an Eclipse bundle's JAR Manifest
     * @param aResource an Eclipse JAR path
     * @return The JAR Manifest
     * @throws IOException 
     */
    private Manifest getManifestFromJAR(Resource aResource) throws IOException {
            JarFile jar = new JarFile(aResource.getFile());
            return jar.getManifest();
    }

    private File getEclipseWorkspace() {
        
        String workspaceAreaProp = System.getProperty(s_PROP_WORKSPACE_AREA, "../..");
        
        System.out.println( "workspace area: " + workspaceAreaProp );
        
        if (workspaceAreaProp != null)
            return new File(workspaceAreaProp);
        else
            return null;
        
    }

    private File[] getEclipseTarget() {
        
        List<File> result = new ArrayList<File>();
        
        Properties props = System.getProperties();

        for (Iterator<Object> iterator = props.keySet().iterator(); iterator.hasNext();) {
            String prop = (String) iterator.next();
            if (prop.startsWith(s_PROP_TARGET_AREA))
            {
                System.out.println( "target area: " + System.getProperty(prop) );
                File f = new File(System.getProperty(prop));
                if ( !f.isDirectory() ) {
                    throw new IllegalStateException(s_PROP_TARGET_AREA + " not set.");
                }                
                result.add(f);
            }
        }
        
        if (result.isEmpty()) {
            File f = new File( "../plugins" );
            if ( !f.isDirectory() ) {
                throw new IllegalStateException(s_PROP_TARGET_AREA + " not set.");
            }
            result.add( f );
        }
        
        if (result.size() == 0)
            throw new IllegalStateException(s_PROP_TARGET_AREA + " not set.");
        else
            return result.toArray(new File[]{});
        
    }
    
}
