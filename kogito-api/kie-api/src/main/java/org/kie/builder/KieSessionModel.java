package org.kie.builder;

import org.kie.runtime.conf.ClockTypeOption;

import java.util.List;

public interface KieSessionModel {

    String getName();

    KieSessionModel setName(String name);

    String getType();

    KieSessionModel setType(String type);

    ClockTypeOption getClockType();

    KieSessionModel setClockType(ClockTypeOption clockType);

    ListenerModel newListenerModel(String type, ListenerModel.Kind kind);

    List<ListenerModel> getListenerModels();

    WorkItemHandlerModel newWorkItemHandelerModel(String type);

    List<WorkItemHandlerModel> getWorkItemHandelerModels();
    
    public static enum KieSessionType {
        Stateful, Statless;
    }
}