package org.drools.testcoverage.common.util;

import java.util.Properties;

import org.kie.api.KieServices;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.io.Resource;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.conf.ClockTypeOption;

/**
 * Util class that provides various methods related to KieSession.
 */
public final class KieSessionUtil {


    public static KieSessionConfiguration getKieSessionConfigurationWithClock(final ClockTypeOption clockType,
            final Properties sessionProperties) {
        final KieSessionConfiguration conf = KieServices.Factory.get().newKieSessionConfiguration(sessionProperties);
        conf.setOption(clockType);
        return conf;
    }

    public static Session getKieSessionFromKieBaseModel(final String moduleGroupId, final KieBaseTestConfiguration kieBaseTestConfiguration,
                                                        final KieSessionTestConfiguration kieSessionTestConfiguration, final Resource... resources) {
        final KieModuleModel module = KieUtil.createKieModuleModel(kieBaseTestConfiguration.useAlphaNetworkCompiler());
        final KieBaseModel kieBaseModel = kieBaseTestConfiguration.getKieBaseModel(module);

        kieSessionTestConfiguration.getKieSessionModel(kieBaseModel);
        final KieModule kieModule = KieUtil.buildAndInstallKieModuleIntoRepo(kieBaseTestConfiguration, moduleGroupId, module, resources);

        return getDefaultKieSessionFromReleaseId(kieModule.getReleaseId(), kieSessionTestConfiguration.isStateful(), false);
    }

    public static Session getDefaultKieSessionFromReleaseId(final ReleaseId releaseId, final boolean stateful, final boolean persisted) {
        return getKieSessionFromReleaseIdByName(releaseId, null, stateful, persisted);
    }

    public static Session getKieSessionFromReleaseIdByName(final ReleaseId releaseId, final String name,
                                                           final boolean stateful, final boolean persisted) {
        final KieContainer container = KieServices.Factory.get().newKieContainer(releaseId);

        if (stateful) {
            return (name == null) ? new Session(container.newKieSession(), stateful, persisted) :
                                    new Session(container.newKieSession(name), stateful, persisted);
        } else {
            return (name == null) ? new Session(container.newStatelessKieSession(), stateful, persisted) :
                                    new Session(container.newStatelessKieSession(name), stateful, persisted);
        }
    }

    private KieSessionUtil() {
        // Creating instances of util classes should not be possible.
    }
}
