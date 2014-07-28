/*
 * Copyright 2011 JBoss Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package org.drools.compiler.compiler;

import org.kie.internal.utils.ServiceRegistryImpl;

public class PMMLCompilerFactory {

    private static final String PROVIDER_CLASS = "org.drools.pmml.pmml_4_2.PMML4Compiler";

    private static PMMLCompiler provider;

    public static synchronized PMMLCompiler getPMMLCompiler() {
        if ( provider == null ) {
            loadProvider();
        }
        return provider;
    }

    public static void setProvider(PMMLCompiler provider) {
        PMMLCompilerFactory.provider = provider;
    }

    private static void loadProvider() {
        ServiceRegistryImpl.getInstance().addDefault( PMMLCompiler.class,  PROVIDER_CLASS );
        setProvider(ServiceRegistryImpl.getInstance().get(PMMLCompiler.class));
    }

    public static synchronized void loadProvider(ClassLoader cl) {
        if (provider == null) {
            try {
                provider = (PMMLCompiler)Class.forName(PROVIDER_CLASS, true, cl).newInstance();
            } catch (Exception e) { }
        }
    }
}
