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
package org.kie.internal.definition.rule;

import java.util.Calendar;

import org.kie.api.definition.rule.Rule;
import org.kie.api.io.Resource;

/**
 * Internal Rule interface for runtime rule inspection.
 */
public interface InternalRule extends Rule {

    /**
     * Returns the {@link Resource} of this rule.
     *
     * @return the {@link Resource}.
     */
    Resource getResource();

    /**
     * Returns the dialect property of this rule.
     *
     * @return the dialect.
     */
    String getDialect();

    /**
     * Determine if this rule is internally consistent and valid. This will include checks to make sure the rules semantic components
     * (actions and predicates) are valid.
     *
     * No exception is thrown.
     * <p>
     * A <code>Rule</code> must include at least one parameter declaration and one condition.
     * </p>
     *
     * @return <code>true</code> if this rule is valid, else <code>false</code>.
     */
    boolean isValid();

    /**
     * Returns the value of the <code>salience</code> attribute.
     *
     * @return the <code>salience</code> property value.
     */
    int getSalienceValue();

    /**
     * Returns <code>true</code> if the rule uses dynamic salience, <code>false</code> otherwise.
     *
     * @return <code>true</code> if the rule uses dynamic salience, else <code>false</code>.
     */
    boolean isSalienceDynamic();

    /**
     * Returns the <code>agenda-group</code> rule property.
     *
     * @return the <code>agenda-group</code> property value.
     */
    String getAgendaGroup();


    /**
     * Returns the <code>no-loop</code> rule property.
     *
     * @return the <code>no-loop</code> property value.
     */
    boolean isNoLoop();

    /**
     * Returns the <code>auto-focus</code> rule property.
     *
     * @return the <code>auto-focus</code> property value.
     */
    boolean getAutoFocus();

    /**
     * Returns the <code>activation-group</code> rule property.
     *
     * @return the <code>activation-group</code> property value.
     */
    String getActivationGroup();

    /**
     * Returns the <code>ruleflow-group</code> rule property.
     *
     * @return the <code>ruleflow-group</code> property value.
     */
    String getRuleFlowGroup();

    /**
     * Returns the <code>lock-on-active</code> rule property.
     *
     * @return the <code>lock-on-active</code> property value.
     */
    boolean isLockOnActive();

    /**
     * Returns the <code>date-effective</code> rule property as a {@link Calendar}.
     *
     * @return the <code>date-effective</code> property value.
     */
    Calendar getDateEffective();

    /**
     * Returns the <code>date-expires</code> rule property as a {@link Calendar}.
     *
     * @return the <code>date-expires</code> property value.
     */
    Calendar getDateExpires();

    /**
     * Returns the fully qualified name of the rule (package + rule name)
     *
     * @return the fully qualified name of the rule
     */
    String getFullyQualifiedName();

    /**
     * Returns true if the rule is part of default agenda group. False otherwise
     * @return
     */
    boolean isMainAgendaGroup();
}
