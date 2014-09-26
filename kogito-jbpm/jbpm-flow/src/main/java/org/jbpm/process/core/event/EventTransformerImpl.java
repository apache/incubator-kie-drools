package org.jbpm.process.core.event;

import java.util.Collections;

import org.jbpm.process.core.impl.DataTransformerRegistry;
import org.jbpm.workflow.core.node.Transformation;
import org.kie.api.runtime.process.DataTransformer;

public class EventTransformerImpl implements EventTransformer {
	
	private Transformation transformation;
	private String name;
	
	public EventTransformerImpl(Transformation transformation) {
		if (transformation != null) {
			this.transformation = transformation;
			this.name = transformation.getSource();
			
			if (this.name == null) {
				this.name = "event";
			}
		}
	}

	@Override
	public Object transformEvent(Object event) {
		if (event == null || transformation == null) {
			return event;
		}
		DataTransformer transformer = DataTransformerRegistry.get().find(transformation.getLanguage());
    	if (transformer != null) {
    		Object parameterValue = transformer.transform(transformation.getCompiledExpression(), Collections.singletonMap(name, event));
    		return parameterValue;
    	}
		return event;
	}

}
