/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.agent;

import java.util.Properties;

import org.drools.RuleBase;

public class RuleBaseAgentCache {

    private static final RuleBaseAgentCache INSTANCE = new RuleBaseAgentCache();

    private RuleBaseAgentCache() {
    }

    public static RuleBaseAgentCache instance() {
        return INSTANCE;
    }

    /**
     * Return a rulebase by name.
     * This name may be the name of a pre configured rulebase, 
     * or the name of a config properties file to be found
     * on the classpath.
     */
    public RuleBase getRuleBase(String name) {
        throw new UnsupportedOperationException( "Not done yet !" );
    }

    /** 
     * Pass in a pre populated properties file.
     * It will then map this config to the given name for future use.
     * @return A RuleBase ready to go. 
     */
    public RuleBase configureRuleBase(String name,
                                      Properties props) {
        return null;
    }

}
