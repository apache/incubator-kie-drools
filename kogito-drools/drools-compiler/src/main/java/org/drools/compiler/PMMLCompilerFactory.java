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

package org.drools.compiler;

import org.drools.builder.DecisionTableConfiguration;
import org.drools.util.ServiceRegistryImpl;

import java.io.InputStream;

public class PMMLCompilerFactory {

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
        ServiceRegistryImpl.getInstance().addDefault( PMMLCompiler.class,  "org.drools.pmml_4_0.PMML4Compiler" );
        setProvider(ServiceRegistryImpl.getInstance().get(PMMLCompiler.class));
    }
}
