package org.kie.maven.plugin;

import static org.drools.compiler.kie.builder.impl.KieBuilderImpl.setDefaultsforEmptyKieModule;
import static org.kie.api.builder.Message.Level.ERROR;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.artifact.resolver.filter.CumulativeScopeArtifactFilter;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.drools.compiler.compiler.BPMN2ProcessFactory;
import org.drools.compiler.compiler.DecisionTableFactory;
import org.drools.compiler.compiler.PMMLCompilerFactory;
import org.drools.compiler.compiler.ProcessBuilderFactory;
import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.drools.compiler.kie.builder.impl.KieContainerImpl;
import org.drools.compiler.kie.builder.impl.KieProject;
import org.drools.compiler.kie.builder.impl.ResultsImpl;
import org.drools.compiler.kie.builder.impl.ZipKieModule;
import org.drools.compiler.kproject.ReleaseIdImpl;
import org.drools.compiler.kproject.models.KieModuleModelImpl;
import org.kie.api.KieServices;
import org.kie.api.builder.KieRepository;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.model.KieModuleModel;

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
		ClassLoader contextClassLoader = Thread.currentThread()
				.getContextClassLoader();
		List<InternalKieModule> kmoduleDeps = new ArrayList<InternalKieModule>();

		project.setArtifactFilter(new CumulativeScopeArtifactFilter(Arrays
				.asList("compile", "runtime")));

		try {
			kmoduleDeps.addAll(getKieModuleDependencies());
			Set<URL> urls = getKieModuleDependenciesAsUrls();

			Thread.currentThread().setContextClassLoader(
					getProjectClassLoader(urls));

			KieProject kieProject = createKieProject(kmoduleDeps);
			ResultsImpl messages = kieProject.verify();

			if (messages.hasMessages(ERROR)) {
				throw new MojoFailureException("Build failed!");
			}
			// else {
			// new KieMetaInfoBuilder(new DiskResourceStore(outputDirectory),
			// (InternalKieModule) kModule).writeKieModuleMetaInfo();
			// }
		} catch (Exception ex) {
			throw new MojoFailureException("Something bad happened!", ex);
		} finally {
			Thread.currentThread().setContextClassLoader(contextClassLoader);
		}

		getLog().info("KieModule successfully built!");
	}

	private KieProject createKieProject(List<InternalKieModule> kmoduleDeps) {
		KieServices ks = KieServices.Factory.get();
		KieRepository kr = ks.getRepository();
		InternalKieModule kModule = (InternalKieModule) kr.addKieModule(ks
				.getResources().newFileSystemResource(sourceFolder));

		for (InternalKieModule kmoduleDep : kmoduleDeps) {
			kModule.addKieDependency(kmoduleDep);
		}

		KieContainerImpl kContainer = (KieContainerImpl) ks
				.newKieContainer(kModule.getReleaseId());

		KieProject kieProject = kContainer.getKieProject();
		return kieProject;
	}

	private ClassLoader getProjectClassLoader(Set<URL> urls) {
		ClassLoader projectClassLoader = URLClassLoader.newInstance(urls
				.toArray(new URL[0]), Thread.currentThread()
				.getContextClassLoader());

		BPMN2ProcessFactory.loadProvider(projectClassLoader);
		DecisionTableFactory.loadProvider(projectClassLoader);
		ProcessBuilderFactory.loadProvider(projectClassLoader);
		PMMLCompilerFactory.loadProvider(projectClassLoader);

		return projectClassLoader;
	}

	private Set<URL> getKieModuleDependenciesAsUrls()
			throws DependencyResolutionRequiredException, MalformedURLException {
		Set<URL> urls = new HashSet<URL>();

		for (String element : project.getCompileClasspathElements()) {
			urls.add(new File(element).toURI().toURL());
		}

		for (Artifact artifact : project.getArtifacts()) {
			File file = artifact.getFile();

			if (file != null) {
				urls.add(file.toURI().toURL());
			}
		}

		urls.add(outputDirectory.toURI().toURL());

		return urls;
	}

	private List<InternalKieModule> getKieModuleDependencies() {
		List<InternalKieModule> kmoduleDeps = new ArrayList<InternalKieModule>();

		for (Artifact artifact : project.getArtifacts()) {
			File file = artifact.getFile();
			if (file != null) {
				KieModuleModel depModel = getDependencyKieModel(file);

				if (depModel != null) {
					ReleaseId releaseId = new ReleaseIdImpl(
							artifact.getGroupId(), artifact.getArtifactId(),
							artifact.getVersion());
					kmoduleDeps
							.add(new ZipKieModule(releaseId, depModel, file));
				}
			}
		}

		return kmoduleDeps;
	}

	private KieModuleModel getDependencyKieModel(File jar) {
		ZipFile zipFile = null;

		try {
			zipFile = new ZipFile(jar);
			ZipEntry zipEntry = zipFile
					.getEntry(KieModuleModelImpl.KMODULE_JAR_PATH);

			if (zipEntry != null) {
				KieModuleModel kieModuleModel = KieModuleModelImpl
						.fromXML(zipFile.getInputStream(zipEntry));
				setDefaultsforEmptyKieModule(kieModuleModel);
				return kieModuleModel;
			}
		} catch (Exception e) {
		} finally {
			if (zipFile != null) {
				try {
					zipFile.close();
				} catch (IOException e) {
				}
			}
		}

		return null;
	}
}
