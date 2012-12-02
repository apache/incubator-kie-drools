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

    ListenerModel newListenerModel(String type);

    List<ListenerModel> getListenerModels();

    WorkItemHandelerModel newWorkItemHandelerModel(String type);

    List<WorkItemHandelerModel> getWorkItemHandelerModels();
    
    public static enum KieSessionType {
        Stateful, Statless;
    }
}