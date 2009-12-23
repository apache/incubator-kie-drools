package org.drools.vsm.task.responseHandlers;

import org.drools.task.service.responsehandlers.BlockingTaskOperationResponseHandler;
import org.drools.vsm.Message;
import org.drools.vsm.task.TaskClientMessageHandlerImpl.TaskOperationMessageResponseHandler;

public class BlockingTaskOperationMessageResponseHandler extends BlockingTaskOperationResponseHandler implements TaskOperationMessageResponseHandler {

	public void receive(Message message) {
		setDone(true);
	}

}