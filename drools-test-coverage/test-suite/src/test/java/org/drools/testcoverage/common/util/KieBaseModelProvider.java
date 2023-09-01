package org.drools.testcoverage.common.util;

import java.util.Optional;

import org.kie.api.KieBaseConfiguration;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.conf.KieBaseOption;

/**
 * Basic provider class for KieBaseModel instances.
 */
public interface KieBaseModelProvider {
    int IDENTITY = 1;
    int STREAM_MODE = 1 << 1;
    int ALPHA_NETWORK_COMPILER = 1 << 2;
    int IMMUTABLE = 1 << 3;

    KieBaseModel getKieBaseModel(KieModuleModel kieModuleModel);
    KieBaseConfiguration getKieBaseConfiguration();
    void setAdditionalKieBaseOptions(KieBaseOption... options);
    boolean isIdentity();
    boolean isStreamMode();
    Optional<Class<? extends KieBuilder.ProjectType>> getExecutableModelProjectClass();
    boolean useAlphaNetworkCompiler();
    boolean isImmutable();
}
