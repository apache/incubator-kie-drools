package org.jbpm.task.service.jms;

import java.util.HashMap;
import java.util.Map;

import org.drools.task.service.ResponseHandler;
import org.jbpm.task.service.BaseHandler;

public class BaseJMSHandler implements BaseHandler {

	protected Map<Integer, ResponseHandler> responseHandlers;

	public BaseJMSHandler() {
		this.responseHandlers = new HashMap<Integer, ResponseHandler>();
	}

	public void addResponseHandler(int id, ResponseHandler responseHandler) {
		this.responseHandlers.put(Integer.valueOf(id), responseHandler);
	}
}
