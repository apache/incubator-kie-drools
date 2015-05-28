package org.jbpm.runtime.manager.impl.mapper;

import java.util.List;

import org.kie.internal.runtime.manager.Mapper;


public abstract class InternalMapper implements Mapper {

    public abstract List<String> findContextIdForEvent(String eventType, String ownerId);
}
