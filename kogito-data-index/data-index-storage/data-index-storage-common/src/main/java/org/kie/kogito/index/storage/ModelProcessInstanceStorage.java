package org.kie.kogito.index.storage;

import org.kie.kogito.event.process.ProcessInstanceDataEvent;
import org.kie.kogito.event.process.ProcessInstanceErrorDataEvent;
import org.kie.kogito.event.process.ProcessInstanceNodeDataEvent;
import org.kie.kogito.event.process.ProcessInstanceSLADataEvent;
import org.kie.kogito.event.process.ProcessInstanceStateDataEvent;
import org.kie.kogito.event.process.ProcessInstanceVariableDataEvent;
import org.kie.kogito.index.model.ProcessInstance;
import org.kie.kogito.index.storage.merger.ProcessInstanceErrorDataEventMerger;
import org.kie.kogito.index.storage.merger.ProcessInstanceEventMerger;
import org.kie.kogito.index.storage.merger.ProcessInstanceNodeDataEventMerger;
import org.kie.kogito.index.storage.merger.ProcessInstanceSLADataEventMerger;
import org.kie.kogito.index.storage.merger.ProcessInstanceStateDataEventMerger;
import org.kie.kogito.index.storage.merger.ProcessInstanceVariableDataEventMerger;
import org.kie.kogito.persistence.api.Storage;

public class ModelProcessInstanceStorage extends ModelStorageFetcher<ProcessInstance> implements ProcessInstanceStorage {
    private final ProcessInstanceErrorDataEventMerger errorMerger = new ProcessInstanceErrorDataEventMerger();
    private final ProcessInstanceNodeDataEventMerger nodeMerger = new ProcessInstanceNodeDataEventMerger();
    private final ProcessInstanceSLADataEventMerger slaMerger = new ProcessInstanceSLADataEventMerger();
    private final ProcessInstanceStateDataEventMerger stateMerger = new ProcessInstanceStateDataEventMerger();
    private final ProcessInstanceVariableDataEventMerger variableMerger = new ProcessInstanceVariableDataEventMerger();

    public ModelProcessInstanceStorage(Storage<String, ProcessInstance> storage) {
        super(storage);
    }

    @Override
    public void indexError(ProcessInstanceErrorDataEvent event) {
        index(event, errorMerger);
    }

    @Override
    public void indexNode(ProcessInstanceNodeDataEvent event) {
        index(event, nodeMerger);
    }

    @Override
    public void indexSLA(ProcessInstanceSLADataEvent event) {
        index(event, slaMerger);
    }

    @Override
    public void indexState(ProcessInstanceStateDataEvent event) {
        index(event, stateMerger);
    }

    @Override
    public void indexVariable(ProcessInstanceVariableDataEvent event) {
        index(event, variableMerger);
    }

    private <T extends ProcessInstanceDataEvent<?>> void index(T event, ProcessInstanceEventMerger merger) {
        ProcessInstance processInstance = storage.get(event.getKogitoProcessInstanceId());
        if (processInstance == null) {
            processInstance = new ProcessInstance();
            processInstance.setId(event.getKogitoProcessInstanceId());
            processInstance.setProcessId(event.getKogitoProcessId());
        }
        storage.put(event.getKogitoProcessInstanceId(), merger.merge(processInstance, event));
    }
}
