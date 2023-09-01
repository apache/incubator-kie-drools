package org.drools.core.process;

import java.util.Collection;

import org.drools.core.ClassObjectFilter;
import org.kie.api.runtime.KieRuntime;
import org.kie.api.runtime.process.CaseAssignment;
import org.kie.api.runtime.process.CaseData;

public class ProcessContext extends AbstractProcessContext {
    
    public ProcessContext(KieRuntime kruntime) {
        super(kruntime);
    }
    public CaseData getCaseData() {

        Collection<? extends Object> objects = getKieRuntime().getObjects(new ClassObjectFilter(CaseData.class));
        if (objects.size() == 0) {
            return null;
        }

        return (CaseData) objects.iterator().next();
    }

    public CaseAssignment getCaseAssignment() {
        Collection<? extends Object> objects = getKieRuntime().getObjects(new ClassObjectFilter(CaseAssignment.class));
        if (objects.size() == 0) {
            return null;
        }

        return (CaseAssignment) objects.iterator().next();
    }
}
