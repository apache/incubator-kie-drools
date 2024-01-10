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
package org.kie.api.builder.model;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.kie.api.conf.BetaRangeIndexOption;
import org.kie.api.conf.DeclarativeAgendaOption;
import org.kie.api.conf.EqualityBehaviorOption;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.conf.KieBaseMutabilityOption;
import org.kie.api.conf.PrototypesOption;
import org.kie.api.conf.SequentialOption;
import org.kie.api.conf.SessionsPoolOption;

/**
 * KieBaseModel is a model allowing to programmatically define a KieBase
 * @see org.kie.api.KieBase
 */
public interface KieBaseModel {

    /**
     * Creates a new KieSessionModel with the given name and adds it to this KieBaseModel
     * @param name The name of the new KieSessionModel to be created
     * @return The new KieSessionModel
     */
    KieSessionModel newKieSessionModel(String name);

    /**
     * Removes the KieSessionModel with the give name from this KieBaseModel
     * @param qName The name of the KieSessionModel to be removed
     */
    KieBaseModel removeKieSessionModel(String qName);

    /**
     * Returns all the KieSessionModel defined in this KieBaseModel mapped by their names
     */
    Map<String, KieSessionModel> getKieSessionModels();

    /**
     * Includes the resources of the KieBase with the given name in this KieBaseModel
     */
    KieBaseModel addInclude(String kBaseName);

    /**
     * Remove the inclusion of the KieBase with the given name
     */
    KieBaseModel removeInclude(String kBaseName);

    /**
     * Returns the name of the KieBase defined by this KieBaseModel
     */
    String getName();

    /**
     * Returns the ordered list of all the package patterns used to define the set of resources that have
     * to be included in the KieBase. If this list is empty, "*" is assumed by default, meaning that
     * all the resources stored under a folder with the same name of this KieBaseModel will be
     * included in the compiled KieBase regardless of the package they belong to.
     *
     * The list of package patterns is ordered and earlier patterns are applied before later patterns.
     * For example, if you specify "org.foo.*,!org.foo.impl" the second pattern has no effect since all org.foo
     * packages have already been selected by the first pattern. Instead, you should specify "!org.foo.impl,org.foo.*",
     * which will export all org.foo packages except org.foo.impl.
     */
    List<String> getPackages();

    /**
     * Returns all KieBases included by this one
     */
    Set<String> getIncludes();

    /**
     * Adds a package (pattern) to the list of the packages defining the set of resources
     * that have to be included in the KieBase.
     */
    KieBaseModel addPackage(String pkg);

    /**
     * Removes a package (pattern) from the list of the packages defining the set of resources
     * that have to be included in the KieBase.
     */
    KieBaseModel removePackage(String pkg);

    /**
     * Returns the EqualityBehavior of this KieBaseModel
     */
    EqualityBehaviorOption getEqualsBehavior();

    /**
     * Sets the EqualityBehavior for this KieBaseModel.
     * Default is EqualityBehaviorOption.IDENTITY
     */
    KieBaseModel setEqualsBehavior(EqualityBehaviorOption equalsBehaviour);

    /**
     * Sets the PrototypesOption for this KieBaseModel.
     * Default is PrototypesOption.DISABLED
     */
    PrototypesOption getPrototypes();

    /**
     * Returns the PrototypesOption of this KieBaseModel
     */
    KieBaseModel setPrototypes(PrototypesOption prototypes);

    /**
     * Returns the KieBaseMutabilityOption of this KieBaseModel
     */
    KieBaseMutabilityOption getMutability();

    /**
     * Sets the KieBaseMutabilityOption for this KieBaseModel.
     * Default is KieBaseMutabilityOption.ALLOWED
     */
    KieBaseModel setMutability( KieBaseMutabilityOption mutability );

    /**
     * Returns the session pool configuration of this KieBaseModel
     */
    SessionsPoolOption getSessionsPool();

    /**
     * Sets the SessionPoolOption used by the KieBase to create new sessions
     * Default is SessionPoolOption.NO
     */
    KieBaseModel setSessionsPool( SessionsPoolOption sessionPool );

    /**
     * Returns the EventProcessingMode of this KieBaseModel
     */
    EventProcessingOption getEventProcessingMode();

    /**
     * Sets the EventProcessingOption for this KieBaseModel
     * Default is EventProcessingOption.CLOUD
     */
    KieBaseModel setEventProcessingMode(EventProcessingOption eventProcessingMode);

    /**
     * Returns the DeclarativeAgendaOption of this KieBaseModel
     */
    DeclarativeAgendaOption getDeclarativeAgenda();

    /**
     * Sets the DeclarativeAgendaOption for this KieBaseModel
     * Default is DeclarativeAgendaOption.DISABLED
     */
    KieBaseModel setDeclarativeAgenda(DeclarativeAgendaOption declarativeAgenda);

    /**
     * Returns the BetaRangeIndexOption of this KieBaseModel
     */
    BetaRangeIndexOption getBetaRangeIndexOption();

    /**
     * Sets the BetaRangeIndexOption for this KieBaseModel
     * Default is BetaRangeIndexOption.DISABLED
     */
    KieBaseModel setBetaRangeIndexOption(BetaRangeIndexOption betaRangeIndexOption);

    /**
     * Returns the SequentialOption of this KieBaseModel
     */
    SequentialOption getSequential();

    /**
     * Sets the SequentialOption for this KieBaseModel
     * Default is SequentialOption.NO
     */
    KieBaseModel setSequential(SequentialOption sequential);

    /**
     * Sets the CDI scope for this KieBaseModel
     */
    KieBaseModel setScope(String scope);

    /**
     * Returns the CDI scope of this KieBaseModel
     * Default is jakarta.enterprise.context.ApplicationScoped
     */
    String getScope();

    /**
     * Returns the list of all RuleTemplateModels defined for this KieBaseModel
     */
    List<RuleTemplateModel> getRuleTemplates();

    /**
     * Defines a new RuleTemplateModel for this KieBaseModel
     */
    KieBaseModel addRuleTemplate(String dtable, String template, int row, int col);

    /**
     * Returns true if this KieBaseModel is the default one
     */
    boolean isDefault();

    /**
     * Sets the KieBase generated from this KieBaseModel as the default one,
     * i.e. the one that can be loaded from the KieContainer without having to pass its name.
     * Note that only one default KieBaseModel is allowed in a given KieContainer so if more than
     * one is found (maybe because a given KieContainer includes many KieModules) a warning is emitted
     * and all the defaults are disabled so all the KieBases will be accessible only by name
     */
    KieBaseModel setDefault(boolean isDefault);
}
