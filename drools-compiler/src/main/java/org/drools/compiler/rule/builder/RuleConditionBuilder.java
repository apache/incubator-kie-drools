/*
 * Copyright 2006 JBoss Inc
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

package org.drools.compiler.rule.builder;

import org.drools.compiler.lang.descr.BaseDescr;
import org.drools.core.rule.Pattern;
import org.drools.core.rule.RuleConditionElement;

/**
 * An interface to define classes capable of building
 * specific conditional elements.
 */
public interface RuleConditionBuilder<T extends BaseDescr> extends EngineElementBuilder {

    public RuleConditionElement build(final RuleBuildContext context,
                                      final T descr);
    
    public RuleConditionElement build(final RuleBuildContext context,
                                      final T descr,
                                      final Pattern prefixPattern);

}
