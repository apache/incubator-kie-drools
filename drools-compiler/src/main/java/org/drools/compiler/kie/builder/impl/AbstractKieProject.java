package org.drools.compiler.kie.builder.impl;

import static java.lang.Math.abs;
import static java.util.concurrent.Executors.newFixedThreadPool;
import static org.drools.compiler.kie.builder.impl.AbstractKieModule.buildKnowledgePackages;
import static org.drools.compiler.kie.builder.impl.AbstractKieModule.compileKieBase;

import java.text.DecimalFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;

import org.drools.compiler.commons.jci.stores.ResourceStore;
import org.drools.compiler.kie.builder.impl.KieMetaInfoBuilder.MetaInfos;
import org.drools.compiler.kproject.models.KieBaseModelImpl;
import org.drools.compiler.kproject.models.KieSessionModelImpl;
import org.kie.api.builder.Message.Level;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.builder.model.KieSessionModel;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractKieProject implements KieProject {

	private static final DecimalFormat DF = new DecimalFormat("#######.##");

	private static final Logger log = LoggerFactory.getLogger(KieProject.class);

	protected final Map<String, KieBaseModel> kBaseModels = new HashMap<String, KieBaseModel>();

	private KieBaseModel defaultKieBase = null;

	private KieSessionModel defaultKieSession = null;

	private KieSessionModel defaultStatelessKieSession = null;

	private Map<KieBaseModel, Set<String>> includesInKieBase = new HashMap<KieBaseModel, Set<String>>();

	protected final Map<String, KieSessionModel> kSessionModels = new HashMap<String, KieSessionModel>();

	public ResultsImpl verify() {
		ResultsImpl messages = new ResultsImpl();
		verify(messages);
		return messages;
	}

	public void verify(ResultsImpl messages) {
		for (KieBaseModel model : kBaseModels.values()) {
			buildKnowledgePackages((KieBaseModelImpl) model, this, messages);
		}
	}

	public ResultsImpl buildProject(InternalKieModule kModule, ResourceStore trgMfs,
			ResultsImpl messages, int numThreads) {
		Collection<KieBaseModel> kbases = kBaseModels.values();
		ExecutorService executor = newFixedThreadPool(numThreads);
		CompletionService<JobResult> compService = new ExecutorCompletionService<JobResult>(
				executor);

		for (KieBaseModel kbase : kbases) {
			compService.submit(new BuildJob(this, kbase, messages));
		}

		checkAllCompletedJobs(kModule, trgMfs, kbases, executor, compService);

		return messages;
	}

	public KieBaseModel getDefaultKieBaseModel() {
		return defaultKieBase;
	}

	public KieSessionModel getDefaultKieSession() {
		return defaultKieSession;
	}

	public KieSessionModel getDefaultStatelessKieSession() {
		return defaultStatelessKieSession;
	}

	public KieBaseModel getKieBaseModel(String kBaseName) {
		return kBaseModels.get(kBaseName);
	}

	public KieSessionModel getKieSessionModel(String kSessionName) {
		return kSessionModels.get(kSessionName);
	}

	protected void indexParts(Collection<InternalKieModule> kieModules,
			Map<String, InternalKieModule> kJarFromKBaseName) {
		for (InternalKieModule kJar : kieModules) {
			KieModuleModel kieProject = kJar.getKieModuleModel();
			for (KieBaseModel kieBaseModel : kieProject.getKieBaseModels()
					.values()) {
				if (kieBaseModel.isDefault()) {
					if (defaultKieBase == null) {
						defaultKieBase = kieBaseModel;
					} else {
						defaultKieBase = null;
						log.warn("Found more than one default KieBase: disabling all. KieBases will be accessible only by name");
					}
				}

				kBaseModels.put(kieBaseModel.getName(), kieBaseModel);
				// Should already be set, but just in case
				((KieBaseModelImpl) kieBaseModel).setKModule(kieProject);

				kJarFromKBaseName.put(kieBaseModel.getName(), kJar);
				for (KieSessionModel kieSessionModel : kieBaseModel
						.getKieSessionModels().values()) {
					if (kieSessionModel.isDefault()) {
						if (kieSessionModel.getType() == KieSessionModel.KieSessionType.STATEFUL) {
							if (defaultKieSession == null) {
								defaultKieSession = kieSessionModel;
							} else {
								defaultKieSession = null;
								log.warn("Found more than one defualt KieSession: disabling all. KieSessions will be accessible only by name");
							}
						} else {
							if (defaultStatelessKieSession == null) {
								defaultStatelessKieSession = kieSessionModel;
							} else {
								defaultStatelessKieSession = null;
								log.warn("Found more than one defualt StatelessKieSession: disabling all. StatelessKieSessions will be accessible only by name");
							}
						}
					}

					// Should already be set, but just in case
					((KieSessionModelImpl) kieSessionModel)
							.setKBase(kieBaseModel);
					kSessionModels.put(kieSessionModel.getName(),
							kieSessionModel);
				}
			}
		}
	}

	protected void cleanIndex() {
		kBaseModels.clear();
		kSessionModels.clear();
		includesInKieBase.clear();
		defaultKieBase = null;
		defaultKieSession = null;
		defaultStatelessKieSession = null;
	}

	public Set<String> getTransitiveIncludes(String kBaseName) {
		return getTransitiveIncludes(getKieBaseModel(kBaseName));
	}

	public Set<String> getTransitiveIncludes(KieBaseModel kBaseModel) {
		Set<String> includes = includesInKieBase.get(kBaseModel);
		if (includes == null) {
			includes = new HashSet<String>();
			getTransitiveIncludes(kBaseModel, includes);
			includesInKieBase.put(kBaseModel, includes);
		}
		return includes;
	}

	private void getTransitiveIncludes(KieBaseModel kBaseModel,
			Set<String> includes) {
		if (kBaseModel == null) {
			return;
		}
		Set<String> incs = ((KieBaseModelImpl) kBaseModel).getIncludes();
		if (incs != null && !incs.isEmpty()) {
			for (String inc : incs) {
				if (!includes.contains(inc)) {
					includes.add(inc);
					getTransitiveIncludes(getKieBaseModel(inc), includes);
				}
			}
		}
	}

	private void checkAllCompletedJobs(InternalKieModule kModule,
			ResourceStore trgMfs, Collection<KieBaseModel> kbases,
			ExecutorService executor, CompletionService<JobResult> compService) {
		try {
			int jobsSize = kbases.size();
			MetaInfos metaInfos = null;
			KieMetaInfoBuilder kMetaInfoBuilder = new KieMetaInfoBuilder(
					trgMfs, kModule);

			for (int i = 0; i < jobsSize; ++i) {
				JobResult jobResult = compService.take().get();
				checkJob(jobsSize, i, jobResult);

				metaInfos = kMetaInfoBuilder
						.writeKieModuleMetaInfoIncrementally(metaInfos,
								jobResult.kBuilder, jobResult.baseName);
			}
			
			kMetaInfoBuilder.writeKieModuleMetaInfo(metaInfos);
		} catch (Exception ex) {
			log.error("Something bad happened!", ex);
		} finally {
			log.info("ALL JOBS DONE!");
			executor.shutdownNow();
		}
	}

	private void checkJob(int jobsSize, int i, JobResult jobResult) {
		if (jobResult.messages.hasMessages(Level.ERROR)) {
			log.error("Compilation failed  (" + (i + 1) + "/" + jobsSize
					+ ") :: " + jobResult.baseName + " ("
					+ DF.format(jobResult.elapsedSeconds) + " sec.) :: \n"
					+ jobResult.messages.toString());
		} else if (jobResult.messages.hasMessages(Level.WARNING)) {
			log.error("Compilation failed  (" + (i + 1) + "/" + jobsSize
					+ ") :: " + jobResult.baseName + " ("
					+ DF.format(jobResult.elapsedSeconds) + " sec.) :: \n"
					+ jobResult.messages.toString());
		} else {
			log.error("Compilation completed  (" + (i + 1) + "/" + jobsSize
					+ ") :: " + jobResult.baseName + " ("
					+ DF.format(jobResult.elapsedSeconds) + " sec.)");
		}
	}

	private class BuildJob implements Callable<JobResult> {

		private final KieProject kproject;
		private final KieBaseModel kbase;
		private final ResultsImpl messages;

		public BuildJob(KieProject kproject, KieBaseModel kbase,
				ResultsImpl messages) {
			this.kproject = kproject;
			this.kbase = kbase;
			this.messages = messages;
		}

		@Override
		public JobResult call() throws Exception {
			log.info("Compiling :: " + kbase.getName());

			long startTime = System.nanoTime();
			KnowledgeBuilder kbuilder = compileKieBase(
					(KieBaseModelImpl) kbase, kproject, messages);
			long finishTime = System.nanoTime();

			if (kbuilder.hasErrors()) {
				for (KnowledgeBuilderError error : kbuilder.getErrors()) {
					messages.addMessage(error);
				}
			}

			double elapsedTime = (double) abs((finishTime - startTime))
					/ (double) 1000000000;

			return new JobResult(kbase.getName(), kbuilder, messages,
					elapsedTime);
		}

	}

	private class JobResult {

		private final String baseName;
		private final ResultsImpl messages;
		private final double elapsedSeconds;
		private final KnowledgeBuilder kBuilder;

		public JobResult(String baseName, KnowledgeBuilder kBuilder,
				ResultsImpl messages, double elapsedSeconds) {
			this.baseName = baseName;
			this.kBuilder = kBuilder;
			this.messages = messages;
			this.elapsedSeconds = elapsedSeconds;
		}

	}
}
