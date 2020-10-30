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

import java.io.InputStream;

import org.kie.api.KieBase;
import org.kie.api.internal.utils.ServiceRegistry;
import org.kie.internal.builder.ScoreCardConfiguration;

public class ScoreCardFactory {

    private static class LazyHolder {
        private static final ScoreCardProvider provider = ServiceRegistry.getService( ScoreCardProvider.class );
    }

    public static String loadFromInputStream(InputStream is, ScoreCardConfiguration configuration) {
        return getScoreCardProvider().loadFromInputStream( is, configuration );
    }
    
    public static String getPMMLStringFromInputStream(InputStream is, ScoreCardConfiguration configuration) {
    	return getScoreCardProvider().getPMMLStringFromInputStream(is, configuration);
    }
    
    public static KieBase getKieBaseFromInputStream(InputStream is, ScoreCardConfiguration configuration) {
    	return getScoreCardProvider().getKieBaseFromInputStream(is, configuration);
    }
    
    public static ScoreCardProvider getScoreCardProvider() {
        return LazyHolder.provider;
    }
}
