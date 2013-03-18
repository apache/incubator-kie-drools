package org.kie.api.builder.model;

import org.kie.api.runtime.conf.ClockTypeOption;

import java.util.List;

/**
 * KieSessionModel is a model allowing to programmatically define a KieSession
 * @see org.kie.api.runtime.KieSession
 */
public interface KieSessionModel {

    /**
     * Returns the name of the KieSession defined by this KieSessionModel
     */
    String getName();

    /**
     * Returns the type of this KieSessionModel
     */
    KieSessionType getType();

    /**
     * Sets the type for this KieSessionModel. Default is STATEFUL
     */
    KieSessionModel setType(KieSessionType type);

    /**
     * Returns the EqualityBehavior of this KieSessionModel
     */
    ClockTypeOption getClockType();

    /**
     * Sets the EqualityBehavior for this KieSessionModel. Default is realtime
     */
    KieSessionModel setClockType(ClockTypeOption clockType);

    /**
     * Creates a new ListenerModel of the given type (i.e. the name of the class implementing it)
     * and kind and add it to this KieSessionModel
     */
    ListenerModel newListenerModel(String type, ListenerModel.Kind kind);

    /**
     * Returns all the ListenerModels defined for this KieSessionModel
     */
    List<ListenerModel> getListenerModels();

    /**
     * Creates a new WorkItemHandlerModel of the given type (i.e. the name of the class implementing it)
     * and add it to this KieSessionModel
     */
    WorkItemHandlerModel newWorkItemHandlerModel(String type);

    /**
     * Returns all the WorkItemHandlerModels defined for this KieSessionModel
     */
    List<WorkItemHandlerModel> getWorkItemHandlerModels();

    /**
     * Sets the CDI scope for this KieSessionModel
     * Default is javax.enterprise.context.ApplicationScoped
     */
    KieSessionModel setScope(String scope);

    /**
     * Returns the CDI scope of this KieSessionModel
     */
    String getScope();

    /**
     * Returns true if this KieSessionModel is the default one
     */
    boolean isDefault();

    /**
     * Sets the KieSession generated from this KieSessionModel as the default one,
     * i.e. the one that can be loaded from the KieContainer without having to pass its name.
     * Note that only one defualt KieSessionModel of type STATEFUL and one of type STATELESS
     * are allowed in a given KieContainer so if more than one is found
     * (maybe because a given KieContainer includes many KieModules) a warning is emitted
     * and all the defaults are disabled so all the KieSessions will be accessible only by name
     */
    KieSessionModel setDefault(boolean isDefault);

    public static enum KieSessionType {
        STATEFUL, STATELESS;
    }
}
