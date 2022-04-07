/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.domain.variable.listener.support;

import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.variable.VariableListener;
import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.ShadowVariableDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.VariableDescriptor;
import org.optaplanner.core.impl.domain.variable.listener.SourcedVariableListener;
import org.optaplanner.core.impl.domain.variable.listener.support.violation.ShadowVariablesAssert;
import org.optaplanner.core.impl.domain.variable.supply.Demand;
import org.optaplanner.core.impl.domain.variable.supply.Supply;
import org.optaplanner.core.impl.domain.variable.supply.SupplyManager;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;

/**
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 */
public final class VariableListenerSupport<Solution_> implements SupplyManager<Solution_> {

    private final InnerScoreDirector<Solution_, ?> scoreDirector;
    private final NotifiableRegistry<Solution_> notifiableRegistry;
    private final Map<Demand<Solution_, ?>, Supply> supplyMap;

    private boolean notificationQueuesAreEmpty;
    private int nextGlobalOrder = 0;

    VariableListenerSupport(
            InnerScoreDirector<Solution_, ?> scoreDirector,
            NotifiableRegistry<Solution_> notifiableRegistry,
            Map<Demand<Solution_, ?>, Supply> supplyMap) {
        this.scoreDirector = scoreDirector;
        this.notifiableRegistry = notifiableRegistry;
        this.supplyMap = supplyMap;
    }

    public static <Solution_> VariableListenerSupport<Solution_> create(InnerScoreDirector<Solution_, ?> scoreDirector) {
        return new VariableListenerSupport<>(
                scoreDirector,
                new NotifiableRegistry<>(scoreDirector.getSolutionDescriptor()),
                new LinkedHashMap<>());
    }

    public void linkVariableListeners() {
        notificationQueuesAreEmpty = true;
        scoreDirector.getSolutionDescriptor().getEntityDescriptors().stream()
                .map(EntityDescriptor::getDeclaredShadowVariableDescriptors)
                .flatMap(Collection::stream)
                .filter(ShadowVariableDescriptor::hasVariableListener)
                .sorted(Comparator.comparingInt(ShadowVariableDescriptor::getGlobalShadowOrder))
                .forEach(this::processShadowVariableDescriptor);
    }

    private void processShadowVariableDescriptor(ShadowVariableDescriptor<Solution_> shadowVariableDescriptor) {
        VariableListener<Solution_, ?> variableListener = shadowVariableDescriptor.buildVariableListener(scoreDirector);
        if (variableListener instanceof Supply) {
            // Non-sourced variable listeners (ie. ones provided by the user) can never be a supply.
            supplyMap.put(shadowVariableDescriptor.getProvidedDemand(), (Supply) variableListener);
        }
        int globalOrder = shadowVariableDescriptor.getGlobalShadowOrder();
        notifiableRegistry.registerNotifiable(
                shadowVariableDescriptor.getSourceVariableDescriptorList(),
                new VariableListenerNotifiable<>(scoreDirector, variableListener, globalOrder));
        nextGlobalOrder = globalOrder + 1;
    }

    @Override
    public <Supply_ extends Supply> Supply_ demand(Demand<Solution_, Supply_> demand) {
        return (Supply_) supplyMap.computeIfAbsent(demand, this::createSupply);
    }

    private Supply createSupply(Demand<Solution_, ?> demand) {
        Supply supply = demand.createExternalizedSupply(scoreDirector);
        if (supply instanceof SourcedVariableListener) {
            SourcedVariableListener<Solution_, ?> variableListener = (SourcedVariableListener<Solution_, ?>) supply;
            // An external ScoreDirector can be created before the working solution is set
            if (scoreDirector.getWorkingSolution() != null) {
                variableListener.resetWorkingSolution(scoreDirector);
            }
            notifiableRegistry.registerNotifiable(
                    variableListener.getSourceVariableDescriptor(),
                    new VariableListenerNotifiable<>(scoreDirector, variableListener, nextGlobalOrder++));
        }
        return supply;
    }

    // ************************************************************************
    // Lifecycle methods
    // ************************************************************************

    public void resetWorkingSolution() {
        for (VariableListenerNotifiable<Solution_> notifiable : notifiableRegistry.getAll()) {
            notifiable.resetWorkingSolution();
        }
    }

    public void close() {
        for (VariableListenerNotifiable<Solution_> notifiable : notifiableRegistry.getAll()) {
            notifiable.closeVariableListener();
        }
    }

    public void beforeEntityAdded(EntityDescriptor<Solution_> entityDescriptor, Object entity) {
        Collection<VariableListenerNotifiable<Solution_>> notifiables = notifiableRegistry.get(entityDescriptor);
        if (!notifiables.isEmpty()) {
            VariableListenerNotification notification = VariableListenerNotification.entityAdded(entity);
            for (VariableListenerNotifiable<Solution_> notifiable : notifiables) {
                notifiable.addNotification(notification);
            }
        }
        notificationQueuesAreEmpty = false;
    }

    public void afterEntityAdded(EntityDescriptor<Solution_> entityDescriptor, Object entity) {
        // beforeEntityAdded() has already added it to the notificationQueue
    }

    public void beforeVariableChanged(VariableDescriptor<Solution_> variableDescriptor, Object entity) {
        Collection<VariableListenerNotifiable<Solution_>> notifiables = notifiableRegistry.get(variableDescriptor);
        if (!notifiables.isEmpty()) {
            VariableListenerNotification notification = VariableListenerNotification.variableChanged(entity);
            for (VariableListenerNotifiable<Solution_> notifiable : notifiables) {
                notifiable.addNotification(notification);
            }
        }
        notificationQueuesAreEmpty = false;
    }

    public void afterVariableChanged(VariableDescriptor<Solution_> variableDescriptor, Object entity) {
        // beforeVariableChanged() has already added it to the notificationQueue
    }

    public void beforeEntityRemoved(EntityDescriptor<Solution_> entityDescriptor, Object entity) {
        Collection<VariableListenerNotifiable<Solution_>> notifiables = notifiableRegistry.get(entityDescriptor);
        if (!notifiables.isEmpty()) {
            VariableListenerNotification notification = VariableListenerNotification.entityRemoved(entity);
            for (VariableListenerNotifiable<Solution_> notifiable : notifiables) {
                notifiable.addNotification(notification);
            }
        }
        notificationQueuesAreEmpty = false;
    }

    public void afterEntityRemoved(EntityDescriptor<Solution_> entityDescriptor, Object entity) {
        // beforeEntityRemoved() has already added it to the notificationQueue
    }

    public void triggerVariableListenersInNotificationQueues() {
        for (VariableListenerNotifiable<Solution_> notifiable : notifiableRegistry.getAll()) {
            notifiable.triggerAllNotifications();
        }
        notificationQueuesAreEmpty = true;
    }

    /**
     * @return null if there are no violations
     */
    public String createShadowVariablesViolationMessage() {
        Solution_ workingSolution = scoreDirector.getWorkingSolution();
        ShadowVariablesAssert snapshot =
                ShadowVariablesAssert.takeSnapshot(scoreDirector.getSolutionDescriptor(), workingSolution);

        forceTriggerAllVariableListeners(workingSolution);

        final int SHADOW_VARIABLE_VIOLATION_DISPLAY_LIMIT = 3;
        return snapshot.createShadowVariablesViolationMessage(SHADOW_VARIABLE_VIOLATION_DISPLAY_LIMIT);
    }

    /**
     * Triggers all variable listeners even though the notification queue is empty. This is part of the shadow variables'
     * corruption detection.
     * <p>
     * To ensure each listener is triggered, an artificial notification is created for each genuine variable without
     * doing any change on the working solution. If everything works correctly, triggering listeners at this point must not
     * change any shadow variables either.
     *
     * @param workingSolution working solution
     */
    private void forceTriggerAllVariableListeners(Solution_ workingSolution) {
        scoreDirector.getSolutionDescriptor().visitAllEntities(workingSolution, this::simulateGenuineVariableChange);
        triggerVariableListenersInNotificationQueues();
    }

    private void simulateGenuineVariableChange(Object entity) {
        EntityDescriptor<Solution_> entityDescriptor = scoreDirector.getSolutionDescriptor()
                .findEntityDescriptorOrFail(entity.getClass());
        for (VariableDescriptor<Solution_> variableDescriptor : entityDescriptor.getGenuineVariableDescriptorList()) {
            beforeVariableChanged(variableDescriptor, entity);
            // No change
            afterVariableChanged(variableDescriptor, entity);
        }
    }

    public void assertNotificationQueuesAreEmpty() {
        if (!notificationQueuesAreEmpty) {
            throw new IllegalStateException("The notificationQueues might not be empty (" + notificationQueuesAreEmpty
                    + ") so any shadow variables might be stale so score calculation is unreliable.\n"
                    + "Maybe a " + ScoreDirector.class.getSimpleName() + ".before*() method was called"
                    + " without calling " + ScoreDirector.class.getSimpleName() + ".triggerVariableListeners(),"
                    + " before calling " + ScoreDirector.class.getSimpleName() + ".calculateScore().");
        }
    }
}
