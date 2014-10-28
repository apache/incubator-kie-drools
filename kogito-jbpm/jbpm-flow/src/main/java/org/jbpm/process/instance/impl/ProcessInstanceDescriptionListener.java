package org.jbpm.process.instance.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.drools.core.util.MVELSafeHelper;
import org.jbpm.workflow.instance.impl.ProcessInstanceResolverFactory;
import org.kie.api.definition.process.Process;
import org.kie.api.event.process.DefaultProcessEventListener;
import org.kie.api.event.process.ProcessStartedEvent;
import org.kie.api.runtime.process.WorkflowProcessInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProcessInstanceDescriptionListener extends DefaultProcessEventListener {

    protected static final Pattern PARAMETER_MATCHER = Pattern.compile("#\\{([\\S&&[^\\}]]+)\\}", Pattern.DOTALL);
    private static final Logger logger = LoggerFactory.getLogger(ProcessInstanceDescriptionListener.class);

    public void beforeProcessStarted(ProcessStartedEvent event) {
		ProcessInstanceImpl processInstance = (ProcessInstanceImpl) event.getProcessInstance();
		Process process = processInstance.getProcess();
		if (process != null) {
			Object metaData = process.getMetaData().get("customDescription");
			if (metaData instanceof String) {
				String description = (String) metaData;
                Map<String, String> replacements = new HashMap<String, String>();
                Matcher matcher = PARAMETER_MATCHER.matcher(description);
                while (matcher.find()) {
                    String paramName = matcher.group(1);
                    if (replacements.get(paramName) == null) {
						try {
							String value = (String) MVELSafeHelper.getEvaluator().eval(paramName,
								new ProcessInstanceResolverFactory(((WorkflowProcessInstance) processInstance)));
							replacements.put(paramName, value);
		                } catch (Throwable t) {
		                    logger.error("Could not resolve customDescription, parameter " + paramName, t);
		                    logger.error("Continuing without setting description.");
		                }
                    }
                }
                for (Map.Entry<String, String> replacement: replacements.entrySet()) {
                	description = description.replace("#{" + replacement.getKey() + "}", replacement.getValue());
                }
				processInstance.setDescription(description);
			} else {
				processInstance.setDescription(process.getName());
			}
		}
	}

}
