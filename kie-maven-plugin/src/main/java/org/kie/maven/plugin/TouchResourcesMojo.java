package org.kie.maven.plugin;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.drools.core.util.DroolsStreamUtils;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.Message;
import org.kie.api.builder.Results;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieContainer;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderConfiguration;
import org.kie.internal.builder.KnowledgeBuilderError;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.definition.KnowledgePackage;
import org.kie.internal.io.ResourceFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

/**
 * Compiles and serializes knowledge packages.
 * @author kedzie
 *
 */
@Mojo(name = "touch",
      requiresProject = true,
      defaultPhase = LifecyclePhase.GENERATE_RESOURCES)
public class TouchResourcesMojo extends AbstractMojo {

    /**
     * DRL rule package
     */
    @Parameter(property = "kie.ruleFiles",required = true)
    private List<String> ruleFiles;

    /**
     * KnowledgeBases to serialize
     */
    @Parameter(property = "kie.kiebases",required = true)
    private List<String> kiebases;

    /**
     * Output folder
     */
    @Parameter(property = "kie.resDirectory", defaultValue = "${project.basedir}/res/raw" )
    private String resDirectory;

    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    private MavenProject project;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            File outputFolder = new File(resDirectory);
            outputFolder.mkdirs();

            for(String kbase : kiebases) {
                getLog().info("Touching KBase: " + kbase);
                File file = new File(outputFolder, kbase.replace('.', '_').toLowerCase());
                file.createNewFile();
            }
        } catch (Exception e) {
            throw new MojoExecutionException("error", e);
        }
    }
}
