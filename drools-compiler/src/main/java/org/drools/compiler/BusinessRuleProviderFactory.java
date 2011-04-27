/*
 * Copyright 2011 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

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

    public static synchronized void setBusinessRuleProvider(BusinessRuleProvider provider) {
        BusinessRuleProviderFactory.provider = provider;
    }

    private void loadProvider() throws CheckedDroolsException {
        ServiceRegistryImpl.getInstance().addDefault( BusinessRuleProvider.class,
                                                      "org.drools.ide.common.BusinessRuleProviderDefaultImpl" );
        setBusinessRuleProvider( ServiceRegistryImpl.getInstance().get( BusinessRuleProvider.class ) );
    }

}
