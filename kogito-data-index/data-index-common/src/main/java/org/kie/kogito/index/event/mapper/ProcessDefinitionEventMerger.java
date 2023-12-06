package org.kie.kogito.index.event.mapper;

import org.kie.kogito.event.process.ProcessDefinitionDataEvent;
import org.kie.kogito.index.model.ProcessDefinition;

public interface ProcessDefinitionEventMerger extends Merger<ProcessDefinitionDataEvent, ProcessDefinition> {
}
