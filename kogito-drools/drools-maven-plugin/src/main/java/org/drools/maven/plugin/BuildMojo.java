package org.drools.maven.plugin;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.kie.KieServices;
import org.kie.builder.KieModule;
import org.kie.builder.KieRepository;
import org.kie.builder.Message;
import org.drools.compiler.kie.builder.impl.KieContainerImpl;
import org.drools.compiler.kie.builder.impl.KieProject;
import org.drools.compiler.kie.builder.impl.ResultsImpl;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

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

    public void execute() throws MojoExecutionException, MojoFailureException {
        URLClassLoader projectClassLoader = null;
        try {
            projectClassLoader = new URLClassLoader(new URL[]{outputDirectory.toURI().toURL()});
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(projectClassLoader);

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
