/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.api.event.kiebase;

import java.util.EventListener;

public interface KieBaseEventListener
    extends
    EventListener {
    /**
     * Method called before a new package is added to the {@link org.kie.api.KieBase}
     * @param event
     */
    void beforeKiePackageAdded(BeforeKiePackageAddedEvent event);

    /**
     * Method called after a new package is added to the {@link org.kie.api.KieBase}
     * @param event
     */
    void afterKiePackageAdded(AfterKiePackageAddedEvent event);

    /**
     * Method called before a package is removed from the {@link org.kie.api.KieBase}
     * @param event
     */
    void beforeKiePackageRemoved(BeforeKiePackageRemovedEvent event);

    /**
     * Method called after a package is removed from the {@link org.kie.api.KieBase}
     * @param event
     */
    void afterKiePackageRemoved(AfterKiePackageRemovedEvent event);

    /**
     * Method called before a {@link org.kie.api.KieBase} is locked
     * @param event
     */
    void beforeKieBaseLocked(BeforeKieBaseLockedEvent event);

    /**
     * Method called after a {@link org.kie.api.KieBase} is locked
     * @param event
     */
    void afterKieBaseLocked(AfterKieBaseLockedEvent event);

    /**
     * Method called before a {@link org.kie.api.KieBase} is unlocked
     * @param event
     */
    void beforeKieBaseUnlocked(BeforeKieBaseUnlockedEvent event);

    /**
     * Method called after a {@link org.kie.api.KieBase} is unlocked
     * @param event
     */
    void afterKieBaseUnlocked(AfterKieBaseUnlockedEvent event);

    /**
     * Method called before a new rule is added to the {@link org.kie.api.KieBase}
     * @param event
     */
    void beforeRuleAdded(BeforeRuleAddedEvent event);

    /**
     * Method called after a new rule is added to the {@link org.kie.api.KieBase}
     * @param event
     */
    void afterRuleAdded(AfterRuleAddedEvent event);

    /**
     * Method called before a rule is removed from the {@link org.kie.api.KieBase}
     * @param event
     */
    void beforeRuleRemoved(BeforeRuleRemovedEvent event);

    /**
     * Method called after a rule is removed from the {@link org.kie.api.KieBase}
     * @param event
     */
    void afterRuleRemoved(AfterRuleRemovedEvent event);

    /**
     * Method called before a function is removed from the {@link org.kie.api.KieBase}
     * @param event
     */
    void beforeFunctionRemoved(BeforeFunctionRemovedEvent event);

    /**
     * Method called after a function is removed from the rule base
     * @param event
     */
    void afterFunctionRemoved(AfterFunctionRemovedEvent event);

    /**
     * Method called before a process is removed from the {@link org.kie.api.KieBase}
     * @param event
     */
    void beforeProcessAdded(BeforeProcessAddedEvent event);

    /**
     * Method called after a function is removed from the {@link org.kie.api.KieBase}
     * @param event
     */
    void afterProcessAdded(AfterProcessAddedEvent event);

    /**
     * Method called before a function is removed from the {@link org.kie.api.KieBase}
     * @param event
     */
    void beforeProcessRemoved(BeforeProcessRemovedEvent event);

    /**
     * Method called after a function is removed from the {@link org.kie.api.KieBase}
     * @param event
     */
    void afterProcessRemoved(AfterProcessRemovedEvent event);
}
