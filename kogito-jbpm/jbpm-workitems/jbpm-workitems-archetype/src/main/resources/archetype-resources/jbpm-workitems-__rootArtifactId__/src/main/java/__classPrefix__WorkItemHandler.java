package ${package};

import java.util.HashMap;
import java.util.Map;
import org.jbpm.process.workitem.core.AbstractLogOrThrowWorkItemHandler;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemManager;
import org.jbpm.process.workitem.core.util.Wid;
import org.jbpm.process.workitem.core.util.WidParameter;
import org.jbpm.process.workitem.core.util.WidResult;
import org.jbpm.process.workitem.core.util.WidMavenDepends;

@Wid(widfile="${classPrefix}Definitions.wid", name="${classPrefix}Definitions",
        displayName="${classPrefix}Definitions", icon="${classPrefix}Icon.png",
        defaultHandler="mvel: new ${package}.${classPrefix}WorkItemHandler()",
        parameters={
            @WidParameter(name="SampleParam"),
            @WidParameter(name="SampleParamTwo")
        },
        results={
            @WidResult(name="SampleResult")
        },
        mavenDepends={
            @WidMavenDepends(group="${groupId}", artifact="${rootArtifactId}", version="${version}")
        })
public class ${classPrefix}WorkItemHandler extends AbstractLogOrThrowWorkItemHandler {

    public void executeWorkItem(WorkItem workItem,
                                WorkItemManager manager) {
        try {

            // sample parameters
            String sampleParam = (String) workItem.getParameter("SampleParam");
            String sampleParamTwo = (String) workItem.getParameter("SampleParamTwo");

            // complete workitem impl...


            // return results
            String sampleResult = "sample result";
            Map<String, Object> results = new HashMap<String, Object>();
            results.put("SampleResult", sampleResult);


            manager.completeWorkItem(workItem.getId(), results);
        } catch(Throwable cause) {
            handleException(cause);
        }
    }

    @Override
    public void abortWorkItem(WorkItem workItem,
                              WorkItemManager manager) {
        // stub
    }
}


