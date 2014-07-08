package org.drools.compiler.kie.builder.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.compiler.commons.jci.stores.ResourceStore;
import org.drools.compiler.compiler.PackageRegistry;
import org.drools.compiler.kie.builder.impl.KieModuleCache.KModuleCache;
import org.drools.compiler.kproject.models.KieModuleModelImpl;
import org.drools.core.factmodel.ClassDefinition;
import org.drools.core.rule.JavaDialectRuntimeData;
import org.drools.core.rule.KieModuleMetaInfo;
import org.drools.core.rule.TypeDeclaration;
import org.drools.core.rule.TypeMetaInfo;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.definition.KiePackage;
import org.kie.api.definition.rule.Rule;
import org.kie.api.definition.type.FactType;
import org.kie.internal.builder.KnowledgeBuilder;

import com.google.protobuf.ByteString;

public class KieMetaInfoBuilder {

	private final ResourceStore trgMfs;
	private final InternalKieModule kModule;

	public KieMetaInfoBuilder(ResourceStore trgMfs, InternalKieModule kModule) {
		this.trgMfs = trgMfs;
		this.kModule = kModule;
	}

	public void writeKieModuleMetaInfo() {
		KieModuleMetaInfo info = generateKieModuleMetaInfo();
		trgMfs.write(KieModuleModelImpl.KMODULE_INFO_JAR_PATH, info
				.marshallMetaInfos().getBytes(), true);
	}

	public void writeKieModuleMetaInfo(MetaInfos metaInfos) {
		KieModuleMetaInfo moduleMetaInfo = new KieModuleMetaInfo(
				metaInfos.typeInfos, metaInfos.rulesPerPackage);
		trgMfs.write(KieModuleModelImpl.KMODULE_INFO_JAR_PATH, moduleMetaInfo
				.marshallMetaInfos().getBytes(), true);
	}

	public MetaInfos writeKieModuleMetaInfoIncrementally(MetaInfos oldMetaInfo,
			KnowledgeBuilder kBuilder, String kieBaseName) {
		MetaInfos newMetaInfo = getMetaInfos(kBuilder);

		newMetaInfo.kModuleCache = null;

		return newMetaInfo.merge(oldMetaInfo);
	}

	private KieModuleMetaInfo generateKieModuleMetaInfo() {
		// TODO: I think this method is wrong because it is only inspecting
		// packages that are included in at least one kbase, but I believe
		// it should inspect all packages, even if not included in
		// any kbase, as they could be included in the future
		Map<String, TypeMetaInfo> typeInfos = new HashMap<String, TypeMetaInfo>();
		Map<String, Set<String>> rulesPerPackage = new HashMap<String, Set<String>>();

		KieModuleModel kieModuleModel = kModule.getKieModuleModel();

		for (String kieBaseName : kieModuleModel.getKieBaseModels().keySet()) {
			KnowledgeBuilderImpl kBuilder = (KnowledgeBuilderImpl) kModule
					.getKnowledgeBuilderForKieBase(kieBaseName);
			MetaInfos metaInfos = getMetaInfos(kBuilder);

			typeInfos.putAll(metaInfos.typeInfos);
			rulesPerPackage.putAll(metaInfos.rulesPerPackage);

			writeCompilationDataToTrg(metaInfos.kModuleCache, kieBaseName);
		}

		return new KieModuleMetaInfo(typeInfos, rulesPerPackage);
	}

	private MetaInfos getMetaInfos(KnowledgeBuilder kBuilder) {
		Map<String, TypeMetaInfo> typeInfos = new HashMap<String, TypeMetaInfo>();
		Map<String, Set<String>> rulesPerPackage = new HashMap<String, Set<String>>();
		Map<String, PackageRegistry> pkgRegistryMap = ((KnowledgeBuilderImpl) kBuilder)
				.getPackageRegistry();

		KieModuleCache.KModuleCache.Builder _kmoduleCacheBuilder = createCacheBuilder();
		KieModuleCache.CompilationData.Builder _compData = createCompilationData();

		for (KiePackage kPkg : kBuilder.getKnowledgePackages()) {
			PackageRegistry pkgRegistry = pkgRegistryMap.get(kPkg.getName());
			JavaDialectRuntimeData runtimeData = (JavaDialectRuntimeData) pkgRegistry
					.getDialectRuntimeRegistry().getDialectData("java");

			List<String> types = new ArrayList<String>();

			for (FactType factType : kPkg.getFactTypes()) {
				String className = factType.getName();
				String internalName = className.replace('.', '/') + ".class";
				byte[] bytes = runtimeData.getBytecode(internalName);

				if (bytes != null) {
					trgMfs.write(internalName, bytes, true);
				}

				types.add(internalName);

				Class<?> typeClass = ((ClassDefinition) factType)
						.getDefinedClass();
				TypeDeclaration typeDeclaration = pkgRegistry.getPackage()
						.getTypeDeclaration(typeClass);
				if (typeDeclaration != null) {
					typeInfos.put(typeClass.getName(), new TypeMetaInfo(
							typeDeclaration));
				}
			}

			Set<String> rules = rulesPerPackage.get(kPkg.getName());

			if (rules == null) {
				rules = new HashSet<String>();
			}

			for (Rule rule : kPkg.getRules()) {
				if (!rules.contains(rule.getName())) {
					rules.add(rule.getName());
				}
			}

			if (!rules.isEmpty()) {
				rulesPerPackage.put(kPkg.getName(), rules);
			}

			addToCompilationData(_compData, runtimeData, types);
		}

		_kmoduleCacheBuilder.addCompilationData(_compData.build());

		return new MetaInfos(typeInfos, rulesPerPackage,
				_kmoduleCacheBuilder.build());
	}

	private KieModuleCache.KModuleCache.Builder createCacheBuilder() {
		return KieModuleCache.KModuleCache.newBuilder();
	}

	private KieModuleCache.CompilationData.Builder createCompilationData() {
		// Create compilation data cache
		return KieModuleCache.CompilationData.newBuilder().setDialect("java");
	}

	private void addToCompilationData(
			KieModuleCache.CompilationData.Builder _cdata,
			JavaDialectRuntimeData runtimeData, List<String> types) {
		for (Map.Entry<String, byte[]> entry : runtimeData.getStore()
				.entrySet()) {
			if (!types.contains(entry.getKey())) {
				KieModuleCache.CompDataEntry _entry = KieModuleCache.CompDataEntry
						.newBuilder().setId(entry.getKey())
						.setData(ByteString.copyFrom(entry.getValue())).build();
				_cdata.addEntry(_entry);
			}
		}
	}

	private void writeCompilationDataToTrg(
			KieModuleCache.KModuleCache _kmoduleCache, String kieBaseName) {
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			KieModuleCacheHelper.writeToStreamWithHeader(out, _kmoduleCache);
			String compilatonDataPath = "META-INF/"
					+ kieBaseName.replace('.', '/') + "/kbase.cache";
			trgMfs.write(compilatonDataPath, out.toByteArray(), true);
		} catch (IOException e) {
			// what to do here?
		}
	}

	public class MetaInfos {

		private final Map<String, TypeMetaInfo> typeInfos;
		private final Map<String, Set<String>> rulesPerPackage;
		private KModuleCache kModuleCache;

		public MetaInfos(Map<String, TypeMetaInfo> typeInfos,
				Map<String, Set<String>> rulesPerPackage,
				KModuleCache kModuleCache) {
			this.typeInfos = typeInfos;
			this.rulesPerPackage = rulesPerPackage;
			this.kModuleCache = kModuleCache;
		}

		public MetaInfos merge(MetaInfos old) {
			if (old != null) {
				this.typeInfos.putAll(old.typeInfos);
				this.rulesPerPackage.putAll(old.rulesPerPackage);
			}

			return this;
		}
	}
}
