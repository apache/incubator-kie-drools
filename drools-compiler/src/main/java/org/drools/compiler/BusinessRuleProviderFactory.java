package org.drools.compiler;

import org.drools.CheckedDroolsException;
import org.drools.util.ServiceRegistryImpl;

public class BusinessRuleProviderFactory {
    private static BusinessRuleProviderFactory instance = new BusinessRuleProviderFactory();
    private static BusinessRuleProvider        provider;

    private BusinessRuleProviderFactory() {
    }

    public static BusinessRuleProviderFactory getInstance() {
        return instance;
    }

    public BusinessRuleProvider getProvider() throws CheckedDroolsException {
        if ( null == provider ) loadProvider();
        return provider;
    }

    public static synchronized void setDecisionTableProvider(BusinessRuleProvider provider) {
        BusinessRuleProviderFactory.provider = provider;
    }

    private void loadProvider() throws CheckedDroolsException {
        ServiceRegistryImpl.getInstance().addDefault( BusinessRuleProvider.class,
                                                      "org.drools.ide.common.BusinessRuleProviderDefaultImpl" );
        setDecisionTableProvider( ServiceRegistryImpl.getInstance().get( BusinessRuleProvider.class ) );
    }

}
