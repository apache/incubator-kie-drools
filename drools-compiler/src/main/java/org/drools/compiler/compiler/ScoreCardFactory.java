/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.compiler.compiler;

import org.kie.internal.builder.ScoreCardConfiguration;
import org.kie.internal.utils.ServiceRegistryImpl;

import java.io.InputStream;

public class ScoreCardFactory {
    private static ScoreCardProvider provider;

    public static String loadFromInputStream(InputStream is, ScoreCardConfiguration configuration) {
        return getScoreCardProvider().loadFromInputStream( is, configuration );
    }
    
    public static synchronized void setScoreCardProvider(ScoreCardProvider provider) {
        ScoreCardFactory.provider = provider;
    }
    
    public static synchronized ScoreCardProvider getScoreCardProvider() {
        if ( provider == null ) {
            loadProvider();
        }
        return provider;
    }
    
    private static void loadProvider() {
        ServiceRegistryImpl.getInstance().addDefault( ScoreCardProvider.class,  "org.drools.scorecards.ScoreCardProviderImpl" );
        setScoreCardProvider(ServiceRegistryImpl.getInstance().get( ScoreCardProvider.class ) );
    }
}
