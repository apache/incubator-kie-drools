package org.kie.internal.builder.impl;

import org.kie.api.internal.utils.KieService;
import org.kie.internal.KieInternalServices;
import org.kie.internal.process.CorrelationKeyFactory;

public class KieInternalServicesImpl implements KieInternalServices {

    @Override
    public CorrelationKeyFactory newCorrelationKeyFactory() {
        return KieService.load(CorrelationKeyFactory.class);
    }



}
