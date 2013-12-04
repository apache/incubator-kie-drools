package org.drools.compiler;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Collection;

import org.drools.compiler.compiler.DrlParser;
import org.drools.compiler.compiler.DroolsParserException;
import org.drools.compiler.compiler.PackageBuilder;
import org.drools.compiler.compiler.PackageBuilderConfiguration;
import org.drools.compiler.integrationtests.SerializationHelper;
import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.drools.compiler.lang.descr.PackageDescr;
import org.drools.core.RuleBase;
import org.drools.core.RuleBaseConfiguration;
import org.drools.core.RuleBaseFactory;
import org.drools.core.common.InternalAgenda;
import org.drools.core.common.InternalRuleBase;
import org.drools.core.impl.KnowledgeBaseImpl;
import org.drools.core.runtime.rule.impl.AgendaImpl;
import org.junit.Assert;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.ReleaseId;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.conf.KieSessionOption;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderConfiguration;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.builder.conf.LanguageLevelOption;
import org.kie.internal.builder.conf.RuleEngineOption;
import org.kie.internal.definition.KnowledgePackage;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.kie.internal.runtime.StatelessKnowledgeSession;

import ch.qos.logback.classic.Level;

/**
 * This contains methods common to many of the tests in drools-compiler. </p>
 * The {@link #createKnowledgeSession(KnowledgeBase)} method has been made
 * common so that tests in drools-compiler can be reused (with persistence) in
 * drools-persistence-jpa.
 */
public class CommonTestMethodBase extends Assert {
	public static RuleEngineOption phreak = RuleEngineOption.PHREAK;

	// ***********************************************
	// METHODS TO BE REMOVED FOR 6.0.0

	protected RuleBase getRuleBase() throws Exception {
		return RuleBaseFactory.newRuleBase(RuleBase.RETEOO, null);
	}

	protected RuleBase getRuleBase(final RuleBaseConfiguration config) throws Exception {
		return RuleBaseFactory.newRuleBase(RuleBase.RETEOO, config);
	}

	protected RuleBase getSinglethreadRuleBase() throws Exception {
		RuleBaseConfiguration config = new RuleBaseConfiguration();
		config.setMultithreadEvaluation(false);
		return RuleBaseFactory.newRuleBase(RuleBase.RETEOO, config);
	}

	protected org.drools.core.rule.Package loadPackage(final String classPathResource) throws DroolsParserException, IOException {
		final PackageBuilder builder = new PackageBuilder();
		builder.addPackageFromDrl(new InputStreamReader(getClass().getResourceAsStream(classPathResource)));

		if (builder.hasErrors()) {
			fail(builder.getErrors().toString());
		}

		final org.drools.core.rule.Package pkg = builder.getPackage();
		return pkg;
	}

	protected RuleBase loadRuleBase(final Reader reader) throws IOException,
			DroolsParserException, Exception {
		final DrlParser parser = new DrlParser(LanguageLevelOption.DRL5);
		final PackageDescr packageDescr = parser.parse(reader);
		if (parser.hasErrors()) {
			fail("Error messages in parser, need to sort this our (or else collect error messages):\n"
					+ parser.getErrors());
		}
		// pre build the package
		final PackageBuilder builder = new PackageBuilder();
		builder.addPackage(packageDescr);

		if (builder.hasErrors()) {
			fail(builder.getErrors().toString());
		}

		org.drools.core.rule.Package pkg = builder.getPackage();
		pkg = SerializationHelper.serializeObject(pkg);

		// add the package to a rulebase
		RuleBase ruleBase = getSinglethreadRuleBase();
		ruleBase.addPackage(pkg);
		ruleBase = SerializationHelper.serializeObject(ruleBase);
		// load up the rulebase
		return ruleBase;
	}

	// END - METHODS TO BE REMOVED FOR 6.0.0
	// ****************************************************************************************

	protected StatefulKnowledgeSession createKnowledgeSession(KnowledgeBase kbase) {
		return kbase.newStatefulKnowledgeSession();
	}

	protected StatefulKnowledgeSession createKnowledgeSession(KnowledgeBase kbase, KieSessionOption option) {
		KieSessionConfiguration ksconf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
		ksconf.setOption(option);
		return kbase.newStatefulKnowledgeSession(ksconf, null);
	}

	protected StatefulKnowledgeSession createKnowledgeSession(KnowledgeBase kbase, KieSessionConfiguration ksconf) {
		return kbase.newStatefulKnowledgeSession(ksconf, null);
	}

	protected StatefulKnowledgeSession createKnowledgeSession(KnowledgeBase kbase, KieSessionConfiguration ksconf, Environment env) {
		return kbase.newStatefulKnowledgeSession(ksconf, env);
	}

	protected StatelessKnowledgeSession createStatelessKnowledgeSession(KnowledgeBase kbase) {
		return kbase.newStatelessKnowledgeSession();
	}

	protected KnowledgeBase loadKnowledgeBaseFromString(String... drlContentStrings) {
		return loadKnowledgeBaseFromString(null, null, phreak,
				drlContentStrings);
	}

	protected KnowledgeBase loadKnowledgeBaseFromString(RuleEngineOption phreak, String... drlContentStrings) {
		return loadKnowledgeBaseFromString(null, null, phreak,
				drlContentStrings);
	}

	protected KnowledgeBase loadKnowledgeBaseFromString(KnowledgeBuilderConfiguration config, String... drlContentStrings) {
		return loadKnowledgeBaseFromString(config, null, phreak,
				drlContentStrings);
	}

	protected KnowledgeBase loadKnowledgeBaseFromString(
			KieBaseConfiguration kBaseConfig, String... drlContentStrings) {
		return loadKnowledgeBaseFromString(null, kBaseConfig, phreak,
				drlContentStrings);
	}

	protected KnowledgeBase loadKnowledgeBaseFromString( KnowledgeBuilderConfiguration config, KieBaseConfiguration kBaseConfig, RuleEngineOption phreak, String... drlContentStrings) {
		KnowledgeBuilder kbuilder = config == null ? KnowledgeBuilderFactory.newKnowledgeBuilder() : KnowledgeBuilderFactory.newKnowledgeBuilder(config);
		for (String drlContentString : drlContentStrings) {
			kbuilder.add(ResourceFactory.newByteArrayResource(drlContentString
					.getBytes()), ResourceType.DRL);
		}

		if (kbuilder.hasErrors()) {
			fail(kbuilder.getErrors().toString());
		}
		if (kBaseConfig == null) {
			kBaseConfig = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
		}
		kBaseConfig.setOption(phreak);
		KnowledgeBase kbase = kBaseConfig == null ? KnowledgeBaseFactory.newKnowledgeBase() : KnowledgeBaseFactory.newKnowledgeBase(kBaseConfig);
		kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
		return kbase;
	}

	protected KnowledgeBase loadKnowledgeBase(KnowledgeBuilderConfiguration kbuilderConf, KieBaseConfiguration kbaseConf, String... classPathResources) {
		Collection<KnowledgePackage> knowledgePackages = loadKnowledgePackages(kbuilderConf, classPathResources);

		if (kbaseConf == null) {
			kbaseConf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
		}
		kbaseConf.setOption(phreak);
		KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase(kbaseConf);
		kbase.addKnowledgePackages(knowledgePackages);
		try {
			kbase = SerializationHelper.serializeObject(kbase);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return kbase;
	}

	protected KnowledgeBase loadKnowledgeBase(PackageDescr descr) {
		return loadKnowledgeBase(null, null, descr);
	}

	protected KnowledgeBase loadKnowledgeBase(KnowledgeBuilderConfiguration kbuilderConf,KieBaseConfiguration kbaseConf, PackageDescr descr) {
		Collection<KnowledgePackage> knowledgePackages = loadKnowledgePackages(kbuilderConf, descr);

		if (kbaseConf == null) {
			kbaseConf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
		}
		kbaseConf.setOption(phreak);
		KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase(kbaseConf);
		kbase.addKnowledgePackages(knowledgePackages);
		try {
			kbase = SerializationHelper.serializeObject(kbase);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return kbase;
	}

	public Collection<KnowledgePackage> loadKnowledgePackages(String... classPathResources) {
		return loadKnowledgePackages(null, classPathResources);
	}

	public Collection<KnowledgePackage> loadKnowledgePackages(PackageDescr descr) {
		return loadKnowledgePackages(null, descr);
	}

	public Collection<KnowledgePackage> loadKnowledgePackages(KnowledgeBuilderConfiguration kbuilderConf, PackageDescr descr) {
		if (kbuilderConf == null) {
			kbuilderConf = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration();
		}
		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder(kbuilderConf);
		kbuilder.add(ResourceFactory.newDescrResource(descr), ResourceType.DESCR);
		if (kbuilder.hasErrors()) {
			fail(kbuilder.getErrors().toString());
		}
		Collection<KnowledgePackage> knowledgePackages = kbuilder.getKnowledgePackages();
		return knowledgePackages;
	}

    public Collection<KnowledgePackage> loadKnowledgePackages( KnowledgeBuilderConfiguration kbuilderConf, String... classPathResources) {
        return loadKnowledgePackages(kbuilderConf, true, classPathResources);
    }

	public Collection<KnowledgePackage> loadKnowledgePackages( KnowledgeBuilderConfiguration kbuilderConf, boolean serialize, String... classPathResources) {
		if (kbuilderConf == null) {
			kbuilderConf = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration();
		}

		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder(kbuilderConf);
		for (String classPathResource : classPathResources) {
			kbuilder.add(ResourceFactory.newClassPathResource(classPathResource, getClass()), ResourceType.DRL);
		}

		if (kbuilder.hasErrors()) {
			fail(kbuilder.getErrors().toString());
		}

		Collection<KnowledgePackage> knowledgePackages = null;
        if ( serialize ) {
            try {
                knowledgePackages = SerializationHelper.serializeObject(kbuilder.getKnowledgePackages(),  ((PackageBuilderConfiguration)kbuilderConf).getClassLoader() );
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            knowledgePackages = kbuilder.getKnowledgePackages();
        }
		return knowledgePackages;
	}

	public Collection<KnowledgePackage> loadKnowledgePackagesFromString(String... content) {
		return loadKnowledgePackagesFromString(null, content);
	}

	public Collection<KnowledgePackage> loadKnowledgePackagesFromString(KnowledgeBuilderConfiguration kbuilderConf, String... content) {
		if (kbuilderConf == null) {
			kbuilderConf = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration();
		}
		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder(kbuilderConf);
		for (String r : content) {
			kbuilder.add(ResourceFactory.newByteArrayResource(r.getBytes()),ResourceType.DRL);
		}
		if (kbuilder.hasErrors()) {
			fail(kbuilder.getErrors().toString());
		}
		Collection<KnowledgePackage> knowledgePackages = kbuilder.getKnowledgePackages();
		return knowledgePackages;
	}

	protected KnowledgeBase loadKnowledgeBase(KnowledgeBuilderConfiguration kbuilderConf,String... classPathResources) {
		return loadKnowledgeBase(kbuilderConf, null, classPathResources);
	}

	protected KnowledgeBase loadKnowledgeBase(KieBaseConfiguration kbaseConf, String... classPathResources) {
		return loadKnowledgeBase(null, kbaseConf, classPathResources);
	}


    protected KnowledgeBase getKnowledgeBase() {
        KieBaseConfiguration kBaseConfig = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kBaseConfig.setOption(phreak);
        return getKnowledgeBase(kBaseConfig);
    }

    protected KnowledgeBase getKnowledgeBase(KieBaseConfiguration kBaseConfig) {
        kBaseConfig.setOption(phreak);
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase(kBaseConfig);
        try {
            kbase = SerializationHelper.serializeObject(kbase, ((InternalRuleBase)((KnowledgeBaseImpl) kbase).ruleBase).getRootClassLoader() );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return kbase;
    }

	protected KnowledgeBase loadKnowledgeBase(String... classPathResources) {
		return loadKnowledgeBase(null, null, classPathResources);
	}

	protected InternalAgenda getInternalAgenda(StatefulKnowledgeSession session) {
		return ((AgendaImpl) session.getAgenda()).getAgenda();
	}

	public static byte[] createJar(KieServices ks, 
			                       ReleaseId releaseId,
			                       String... drls) {
		KieFileSystem kfs = ks.newKieFileSystem().generateAndWritePomXML(
				releaseId);
		for (int i = 0; i < drls.length; i++) {
			if (drls[i] != null) {
				kfs.write("src/main/resources/r" + i + ".drl", drls[i]);
			}
		}
		KieBuilder kb = ks.newKieBuilder(kfs).buildAll();
		assertFalse( kb.getResults().getMessages(org.kie.api.builder.Message.Level.ERROR).toString(), 
		        kb.getResults().hasMessages(org.kie.api.builder.Message.Level.ERROR) );
		InternalKieModule kieModule = (InternalKieModule) ks.getRepository()
				.getKieModule(releaseId);
		byte[] jar = kieModule.getBytes();
		return jar;
	}

	public static KieModule createAndDeployJar(KieServices ks, ReleaseId releaseId, String... drls) {
		byte[] jar = createJar(ks, releaseId, drls);

		// Deploy jar into the repository
		KieModule km = deployJarIntoRepository(ks, jar);
		return km;
	}

	public static KieModule createAndDeployJar(KieServices ks, String kmoduleContent, ReleaseId releaseId, Resource... resources) {
		byte[] jar = createJar(ks, kmoduleContent, releaseId, resources);

		KieModule km = deployJarIntoRepository(ks, jar);
		return km;
	}

	public static byte[] createJar(KieServices ks, String kmoduleContent, ReleaseId releaseId, Resource... resources) {
		KieFileSystem kfs = ks.newKieFileSystem().generateAndWritePomXML(releaseId).writeKModuleXML(kmoduleContent);
		for (int i = 0; i < resources.length; i++) {
			if (resources[i] != null) {
				kfs.write(resources[i]);
			}
		}
		ks.newKieBuilder(kfs).buildAll();
		InternalKieModule kieModule = (InternalKieModule) ks.getRepository()
				.getKieModule(releaseId);
		byte[] jar = kieModule.getBytes();
		return jar;
	}

	private static KieModule deployJarIntoRepository(KieServices ks, byte[] jar) {
		Resource jarRes = ks.getResources().newByteArrayResource(jar);
		KieModule km = ks.getRepository().addKieModule(jarRes);
		return km;
	}

    public static byte[] createKJar(KieServices ks,
                                    ReleaseId releaseId,
                                    Resource pom,
                                    Resource... resources) {
        KieFileSystem kfs = ks.newKieFileSystem();
        if( pom != null ) {
            kfs.write(pom);
        } else {
            kfs.generateAndWritePomXML(releaseId);
        }
        for (int i = 0; i < resources.length; i++) {
            if (resources[i] != null) {
                kfs.write(resources[i]);
            }
        }
        ks.newKieBuilder(kfs).buildAll();
        InternalKieModule kieModule = (InternalKieModule) ks.getRepository()
                .getKieModule(releaseId);
        byte[] jar = kieModule.getBytes();
        return jar;
    }

    public static byte[] createKJar(KieServices ks,
                                    ReleaseId releaseId,
                                    String pom,
                                    String... drls) {
        KieFileSystem kfs = ks.newKieFileSystem();
        if( pom != null ) {
            kfs.write("pom.xml", pom);
        } else {
            kfs.generateAndWritePomXML(releaseId);
        }
        for (int i = 0; i < drls.length; i++) {
            if (drls[i] != null) {
                kfs.write("src/main/resources/r" + i + ".drl", drls[i]);
            }
        }
        KieBuilder kb = ks.newKieBuilder(kfs).buildAll();
        if( kb.getResults().hasMessages( org.kie.api.builder.Message.Level.ERROR ) ) {
            for( org.kie.api.builder.Message result : kb.getResults().getMessages() ) {
                System.out.println(result.getText());
            }
            return null;
        }
        InternalKieModule kieModule = (InternalKieModule) ks.getRepository()
                .getKieModule(releaseId);
        byte[] jar = kieModule.getBytes();
        return jar;
    }

    public static KieModule deployJar(KieServices ks, byte[] jar) {
        // Deploy jar into the repository
        Resource jarRes = ks.getResources().newByteArrayResource(jar);
        KieModule km = ks.getRepository().addKieModule(jarRes);
        return km;
    }

}
