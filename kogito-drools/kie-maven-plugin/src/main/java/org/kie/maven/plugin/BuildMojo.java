package org.kie.maven.plugin;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.kie.api.KieServices;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.KieRepository;
import org.kie.api.builder.Message;
import org.drools.compiler.kie.builder.impl.KieContainerImpl;
import org.drools.compiler.kie.builder.impl.KieProject;
import org.drools.compiler.kie.builder.impl.ResultsImpl;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This goal builds the drools file belonging to the kproject.
 *
 * @goal build
 * @phase compile
 */
public class BuildMojo extends AbstractMojo {

    /**
     * Directory containing the generated JAR.
     *
     * @parameter default-value="${project.build.outputDirectory}"
     * @required
     */
    private File outputDirectory;

    /**
     * Project sourceFolder folder.
     *
     * @parameter default-value="src/main/resources"
     * @required
     */
    private File sourceFolder;

    /**
     * @parameter default-value="${project}"
     * @required
     */
    private MavenProject project;

    public void execute() throws MojoExecutionException, MojoFailureException {
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();

        try {
            Set<URL> urls = new HashSet<URL>();
            for (String element : project.getCompileClasspathElements()) {
                urls.add(new File(element).toURI().toURL());
            }
            for (Artifact artifact : project.getDependencyArtifacts()) {
                File file = artifact.getFile();
                if (file != null) {
                    urls.add(file.toURI().toURL());
                }
            }
            urls.add(outputDirectory.toURI().toURL());

            ClassLoader projectClassLoader = URLClassLoader.newInstance(urls.toArray(new URL[0]),
                                                                        Thread.currentThread().getContextClassLoader());

            Thread.currentThread().setContextClassLoader(projectClassLoader);

        } catch (DependencyResolutionRequiredException e) {
            throw new RuntimeException(e);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        KieServices ks = KieServices.Factory.get();

        try {
            KieRepository kr = ks.getRepository();
            KieModule kModule = kr.addKieModule(ks.getResources().newFileSystemResource(sourceFolder));
            KieContainerImpl kContainer = (KieContainerImpl) ks.newKieContainer(kModule.getReleaseId());

            KieProject kieProject = kContainer.getKieProject();
            ResultsImpl messages = kieProject.verify();

            List<Message> errors = messages.filterMessages(Message.Level.ERROR);
            if (!errors.isEmpty()) {
                for (Message error : errors) {
                    getLog().error(error.toString());
                }
                throw new MojoFailureException("Build failed!");
            }
        } finally {
            Thread.currentThread().setContextClassLoader(contextClassLoader);
        }
        getLog().info("KieModule successfully built!");
    }
}
