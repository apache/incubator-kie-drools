package org.drools.model.codegen.project;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import org.drools.codegen.common.DroolsModelBuildContext;
import org.drools.codegen.common.GeneratedFile;
import org.drools.codegen.common.GeneratedFileType;
import org.drools.drl.extensions.DecisionTableFactory;
import org.drools.model.codegen.execmodel.PackageModel;
import org.drools.model.codegen.execmodel.PackageModelWriter;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.io.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RuleCodegen {

    public static final GeneratedFileType RULE_TYPE = GeneratedFileType.of("RULE", GeneratedFileType.Category.SOURCE);

    public static final String GENERATOR_NAME = "rules";

    private static final Logger LOGGER = LoggerFactory.getLogger(RuleCodegen.class);

    private final DroolsModelBuildContext context;
    private final String name;

    public static RuleCodegen ofResources(DroolsModelBuildContext context, Collection<Resource> resources) {
        return new RuleCodegen(context, resources);
    }

    private final Collection<Resource> resources;
    private Collection<PackageModel> packageModels;
    private Collection<KieBaseModel> kmoduleKieBaseModels;

    private boolean hotReloadMode = false;
    private final boolean decisionTableSupported;


    private RuleCodegen(DroolsModelBuildContext context, Collection<Resource> resources) {
        Objects.requireNonNull(context, "context cannot be null");
        this.name = GENERATOR_NAME;
        this.context = context;
        this.resources = resources;
        this.decisionTableSupported = DecisionTableFactory.getDecisionTableProvider() != null;
    }

    public boolean isEmpty() {
        return resources.isEmpty();
    }

    protected Collection<GeneratedFile> internalGenerate() {

        DroolsModelBuilder droolsModelBuilder = new DroolsModelBuilder(context(), resources, decisionTableSupported, PackageModelWriter::new);

        droolsModelBuilder.build();
        Collection<GeneratedFile> generatedFiles = droolsModelBuilder.generateCanonicalModelSources();

        if (!droolsModelBuilder.hasRuleUnits()) {
            KieSessionModelBuilder kieSessionModelBuilder = new KieSessionModelBuilder(context(),
                    droolsModelBuilder.packageSources());
            generatedFiles.addAll(kieSessionModelBuilder.generate());
            this.kmoduleKieBaseModels = kieSessionModelBuilder.getKieBaseModels().values();
        }

        if (LOGGER.isDebugEnabled()) {
            generatedFiles.stream().forEach(genFile -> LOGGER.debug(genFile.toStringWithContent()));
        }
        this.packageModels = droolsModelBuilder.getPackageModels();

        return generatedFiles;
    }

    public boolean isEnabled() {
        return !isEmpty();
    }

    public DroolsModelBuildContext context() {
        return this.context;
    }

    public String name() {
        return name;
    }

    public final Collection<GeneratedFile> generate() {
        if (isEmpty()) {
            return Collections.emptySet();
        }
        return internalGenerate();
    }

    public RuleCodegen withHotReloadMode() {
        hotReloadMode = true;
        return this;
    }
    
    public Collection<PackageModel> getPackageModels() {
        return packageModels;
    }
    
    public Collection<KieBaseModel> getKmoduleKieBaseModels() {
        return kmoduleKieBaseModels;
    }
}
