package org.optaplanner.core.impl.domain.variable.listener.support;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.variable.AbstractVariableListener;
import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.ListVariableDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.ShadowVariableDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.VariableDescriptor;
import org.optaplanner.core.impl.domain.variable.listener.SourcedVariableListener;
import org.optaplanner.core.impl.domain.variable.listener.VariableListenerWithSources;
import org.optaplanner.core.impl.domain.variable.listener.support.violation.ShadowVariablesAssert;
import org.optaplanner.core.impl.domain.variable.supply.Demand;
import org.optaplanner.core.impl.domain.variable.supply.Supply;
import org.optaplanner.core.impl.domain.variable.supply.SupplyManager;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;

/**
 * This class is not thread-safe.
 *
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 */
public final class VariableListenerSupport<Solution_> implements SupplyManager {

    public static <Solution_> VariableListenerSupport<Solution_> create(InnerScoreDirector<Solution_, ?> scoreDirector) {
        return new VariableListenerSupport<>(scoreDirector, new NotifiableRegistry<>(scoreDirector.getSolutionDescriptor()));
    }

    private final InnerScoreDirector<Solution_, ?> scoreDirector;
    private final NotifiableRegistry<Solution_> notifiableRegistry;
    // If thread-safety is ever required, the two collections before need to be updated together.
    private final Map<Demand<?>, Supply> supplyMap = new LinkedHashMap<>();
    private final Map<Demand<?>, Long> demandCounterMap = new HashMap<>();

    private boolean notificationQueuesAreEmpty = true;
    private int nextGlobalOrder = 0;

    VariableListenerSupport(InnerScoreDirector<Solution_, ?> scoreDirector, NotifiableRegistry<Solution_> notifiableRegistry) {
        this.scoreDirector = scoreDirector;
        this.notifiableRegistry = notifiableRegistry;
    }

    public void linkVariableListeners() {
        scoreDirector.getSolutionDescriptor().getEntityDescriptors().stream()
                .map(EntityDescriptor::getDeclaredShadowVariableDescriptors)
                .flatMap(Collection::stream)
                .filter(ShadowVariableDescriptor::hasVariableListener)
                .sorted(Comparator.comparingInt(ShadowVariableDescriptor::getGlobalShadowOrder))
                .forEach(this::processShadowVariableDescriptor);
    }

    private void processShadowVariableDescriptor(ShadowVariableDescriptor<Solution_> shadowVariableDescriptor) {
        for (VariableListenerWithSources<Solution_> listenerWithSources : shadowVariableDescriptor
                .buildVariableListeners(this)) {
            AbstractVariableListener<Solution_, Object> variableListener = listenerWithSources.getVariableListener();
            if (variableListener instanceof Supply) {
                // Non-sourced variable listeners (ie. ones provided by the user) can never be a supply.
                Demand<?> demand = shadowVariableDescriptor.getProvidedDemand();
                supplyMap.put(demand, (Supply) variableListener);
                demandCounterMap.put(demand, 1L);
            }
            int globalOrder = shadowVariableDescriptor.getGlobalShadowOrder();
            notifiableRegistry.registerNotifiable(
                    listenerWithSources.getSourceVariableDescriptors(),
                    AbstractNotifiable.buildNotifiable(scoreDirector, variableListener, globalOrder));
            nextGlobalOrder = globalOrder + 1;
        }
    }

    @Override
    public <Supply_ extends Supply> Supply_ demand(Demand<Supply_> demand) {
        long activeDemandCount = demandCounterMap.compute(demand, (key, count) -> count == null ? 1L : count + 1L);
        if (activeDemandCount == 1L) { // This is a new demand, create the supply.
            Supply_ supply = (Supply_) createSupply(demand);
            supplyMap.put(demand, supply);
            return supply;
        } else { // Return existing supply.
            return (Supply_) supplyMap.get(demand);
        }
    }

    private Supply createSupply(Demand<?> demand) {
        Supply supply = demand.createExternalizedSupply(this);
        if (supply instanceof SourcedVariableListener) {
            SourcedVariableListener<Solution_> variableListener = (SourcedVariableListener<Solution_>) supply;
            // An external ScoreDirector can be created before the working solution is set
            if (scoreDirector.getWorkingSolution() != null) {
                variableListener.resetWorkingSolution(scoreDirector);
            }
            notifiableRegistry.registerNotifiable(
                    variableListener.getSourceVariableDescriptor(),
                    AbstractNotifiable.buildNotifiable(scoreDirector, variableListener, nextGlobalOrder++));
        }
        return supply;
    }

    @Override
    public <Supply_ extends Supply> boolean cancel(Demand<Supply_> demand) {
        Long result = demandCounterMap.computeIfPresent(demand, (key, count) -> Objects.equals(count, 1L) ? null : count - 1L);
        if (result != null) {
            return true;
        }
        return supplyMap.remove(demand) != null;
    }

    @Override
    public <Supply_ extends Supply> long getActiveCount(Demand<Supply_> demand) {
        return demandCounterMap.getOrDefault(demand, 0L);
    }

    // ************************************************************************
    // Lifecycle methods
    // ************************************************************************

    public void resetWorkingSolution() {
        for (Notifiable notifiable : notifiableRegistry.getAll()) {
            notifiable.resetWorkingSolution();
        }
    }

    public void close() {
        for (Notifiable notifiable : notifiableRegistry.getAll()) {
            notifiable.closeVariableListener();
        }
    }

    public void beforeEntityAdded(EntityDescriptor<Solution_> entityDescriptor, Object entity) {
        Collection<EntityNotifiable<Solution_>> notifiables = notifiableRegistry.get(entityDescriptor);
        if (!notifiables.isEmpty()) {
            EntityNotification<Solution_> notification = Notification.entityAdded(entity);
            for (EntityNotifiable<Solution_> notifiable : notifiables) {
                notifiable.notifyBefore(notification);
            }
            notificationQueuesAreEmpty = false;
        }
    }

    public void beforeEntityRemoved(EntityDescriptor<Solution_> entityDescriptor, Object entity) {
        Collection<EntityNotifiable<Solution_>> notifiables = notifiableRegistry.get(entityDescriptor);
        if (!notifiables.isEmpty()) {
            EntityNotification<Solution_> notification = Notification.entityRemoved(entity);
            for (EntityNotifiable<Solution_> notifiable : notifiables) {
                notifiable.notifyBefore(notification);
            }
            notificationQueuesAreEmpty = false;
        }
    }

    public void beforeVariableChanged(VariableDescriptor<Solution_> variableDescriptor, Object entity) {
        Collection<VariableListenerNotifiable<Solution_>> notifiables = notifiableRegistry.get(variableDescriptor);
        if (!notifiables.isEmpty()) {
            BasicVariableNotification<Solution_> notification = Notification.variableChanged(entity);
            for (VariableListenerNotifiable<Solution_> notifiable : notifiables) {
                notifiable.notifyBefore(notification);
            }
            notificationQueuesAreEmpty = false;
        }
    }

    public void afterElementUnassigned(ListVariableDescriptor<Solution_> variableDescriptor, Object element) {
        Collection<ListVariableListenerNotifiable<Solution_>> notifiables = notifiableRegistry.get(variableDescriptor);
        if (!notifiables.isEmpty()) {
            ListVariableNotification<Solution_> notification = Notification.elementUnassigned(element);
            for (ListVariableListenerNotifiable<Solution_> notifiable : notifiables) {
                notifiable.notifyAfter(notification);
            }
            notificationQueuesAreEmpty = false;
        }
    }

    public void beforeListVariableChanged(ListVariableDescriptor<Solution_> variableDescriptor, Object entity, int fromIndex,
            int toIndex) {
        Collection<ListVariableListenerNotifiable<Solution_>> notifiables = notifiableRegistry.get(variableDescriptor);
        if (!notifiables.isEmpty()) {
            ListVariableNotification<Solution_> notification = Notification.listVariableChanged(entity, fromIndex, toIndex);
            for (ListVariableListenerNotifiable<Solution_> notifiable : notifiables) {
                notifiable.notifyBefore(notification);
            }
            notificationQueuesAreEmpty = false;
        }
    }

    public void afterListVariableChanged(ListVariableDescriptor<Solution_> variableDescriptor, Object entity, int fromIndex,
            int toIndex) {
        Collection<ListVariableListenerNotifiable<Solution_>> notifiables = notifiableRegistry.get(variableDescriptor);
        if (!notifiables.isEmpty()) {
            ListVariableNotification<Solution_> notification = Notification.listVariableChanged(entity, fromIndex, toIndex);
            for (ListVariableListenerNotifiable<Solution_> notifiable : notifiables) {
                notifiable.notifyAfter(notification);
            }
            notificationQueuesAreEmpty = false;
        }
    }

    public void triggerVariableListenersInNotificationQueues() {
        for (Notifiable notifiable : notifiableRegistry.getAll()) {
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
            // No change
            beforeVariableChanged(variableDescriptor, entity);
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
