package org.drools.vsm.task.responseHandlers;

import org.drools.task.service.Command;
import org.drools.task.service.responsehandlers.BlockingAddTaskResponseHandler;
import org.drools.vsm.Message;
import org.drools.vsm.task.TaskClientMessageHandlerImpl.AddTaskMessageResponseHandler;

public class BlockingAddTaskMessageResponseHandler extends BlockingAddTaskResponseHandler implements AddTaskMessageResponseHandler {

	public void receive(Message message) {
		Command cmd = (Command) message.getPayload();
		Long taskId = (Long) cmd.getArguments().get(0);
		execute(taskId);
	}

}