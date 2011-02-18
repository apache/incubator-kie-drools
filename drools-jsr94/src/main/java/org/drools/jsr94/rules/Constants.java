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

package org.drools.jsr94.rules;

/**
 * This class provides a list of constant values which can be used as keys in
 * the various property maps that are passed around between the javax.rules and
 * javax.rules.admin objects.
 * <p/>
 * This helps us enforce proper key values at compile time and eliminate the
 * risk of spelling errors.
 */
public final class Constants {
    /** Private constructor because all public access is static. */
    private Constants() {
        // hidden constructor
    }

    /** <code>RuleExecutionSet</code> name constant. */
    public static final String RES_NAME        = "javax.rules.admin.RuleExecutionSet.name";

    /** <code>RuleExecutionSet</code> description constant. */
    public static final String RES_DESCRIPTION = "javax.rules.admin.RuleExecutionSet.description";
    
    public static final String RES_SOURCE = "javax.rules.admin.RuleExecutionSet.source";
    
    public static final String RES_SOURCE_TYPE_XML = "javax.rules.admin.RuleExecutionSet.source.xml";
    
    public static final String RES_SOURCE_TYPE_DECISION_TABLE = "javax.rules.admin.RuleExecutionSet.source.decisiontable";
    
    public static final String RES_DSL = "javax.rules.admin.RuleExecutionSet.dsl";
           
    /** <code>RuleExecutionSet</code> rulebase config constant. */
    public static final String RES_RULEBASE_CONFIG = "javax.rules.admin.RuleExecutionSet.ruleBaseConfiguration";
    
    /** <code>RuleExecutionSet</code> package builder config constant. */
    public static final String RES_PACKAGEBUILDER_CONFIG = "javax.rules.admin.RuleExecutionSet.packageBuilderConfiguration";
}
