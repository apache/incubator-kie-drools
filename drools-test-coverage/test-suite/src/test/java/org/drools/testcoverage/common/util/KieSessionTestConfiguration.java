package org.drools.testcoverage.common.util;

import org.drools.core.ClockType;
import org.kie.api.KieServices;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieSessionModel;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.conf.ClockTypeOption;

/**
 * Represents various tested KieSession configurations.
 */
public enum KieSessionTestConfiguration implements KieSessionModelProvider {

    STATEFUL_REALTIME(true, false),
    STATEFUL_PSEUDO(true, true),
    STATELESS_REALTIME(false, false);

    public static final String KIE_SESSION_MODEL_NAME = "KieSessionModelName";

    private final boolean stateful;
    private final boolean pseudoClock;

    public boolean isStateful() {
        return stateful;
    }

    KieSessionTestConfiguration(final boolean stateful) {
        this.stateful = stateful;
        this.pseudoClock = false;
    }

    KieSessionTestConfiguration(final boolean stateful, final boolean pseudoClock) {
        this.stateful = stateful;
        this.pseudoClock = pseudoClock;
    }

    @Override
    public KieSessionModel getKieSessionModel(final KieBaseModel kieBaseModel) {
        final KieSessionModel kieSessionModel = kieBaseModel.newKieSessionModel(KIE_SESSION_MODEL_NAME);
        if (stateful) {
            kieSessionModel.setType(KieSessionModel.KieSessionType.STATEFUL);
        } else {
            kieSessionModel.setType(KieSessionModel.KieSessionType.STATELESS);
        }
        if (pseudoClock) {
            kieSessionModel.setClockType(ClockTypeOption.get(ClockType.PSEUDO_CLOCK.toString()));
        } else {
            kieSessionModel.setClockType(ClockTypeOption.get(ClockType.REALTIME_CLOCK.toString()));
        }
        kieSessionModel.setDefault(true);
        return kieSessionModel;
    }

    @Override
    public KieSessionConfiguration getKieSessionConfiguration() {
        final KieSessionConfiguration kieSessionConfiguration = KieServices.Factory.get().newKieSessionConfiguration();
        if (pseudoClock) {
            kieSessionConfiguration.setOption(ClockTypeOption.get(ClockType.PSEUDO_CLOCK.toString()));
        } else {
            kieSessionConfiguration.setOption(ClockTypeOption.get(ClockType.REALTIME_CLOCK.toString()));
        }
        return kieSessionConfiguration;
    }
}
