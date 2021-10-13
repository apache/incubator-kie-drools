/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.kie.api.runtime.rule;

import org.kie.api.definition.rule.Rule;

public interface PropagationContext {
    public static final int INSERTION     = 0;
    public static final int DELETION      = 1;
    public static final int MODIFICATION  = 2;
    public static final int RULE_ADDITION = 3;
    public static final int RULE_REMOVAL  = 4;
    public static final int EXPIRATION    = 5;

    public static final String[] typeDescr = new String[] {
                                                           "INSERTION",
                                                           "DELETION",
                                                           "MODIFICATION",
                                                           "RULE_ADDITION",
                                                           "RULE_REMOVAL",
                                                           "EXPIRATION"
    };

    long getPropagationNumber();

    /**
     * The rule that caused the working memory action that created this PropagationContext.
     *
     * If this working memory action was done from java this is null.
     *
     * @return rule that caused the working memory action
     */
    Rule getRule();

    /**
     * @return fact handle that was inserted, updated or retracted that created the PropagationContext
     */
    FactHandle getFactHandle();

    /**
     * The PropagationContextType, numbers may change between Drools versions. Or we may eventually switch this to an enum.
     * INSERTION     = 0;
     * DELETION    = 1;
     * MODIFICATION  = 2;
     * RULE_ADDITION = 3;
     * RULE_REMOVAL  = 4;
     * EXPIRATION    = 5;
     *
     * @return type of the propagation context
     */
    int getType();
}
