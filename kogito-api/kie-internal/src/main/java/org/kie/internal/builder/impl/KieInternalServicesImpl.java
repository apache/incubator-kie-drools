package org.kie.internal.builder.impl;

import org.kie.internal.KieInternalServices;
import org.kie.internal.utils.ServiceRegistryImpl;
import org.kie.internal.process.CorrelationKeyFactory;

public class KieInternalServicesImpl implements KieInternalServices {

    @Override
    public CorrelationKeyFactory newCorrelationKeyFactory() {
        return ServiceRegistryImpl.getInstance().get( CorrelationKeyFactory.class );
    }



}
