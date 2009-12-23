package org.drools.vsm.task.responseHandlers;

import java.util.List;

import org.drools.task.query.TaskSummary;
import org.drools.task.service.Command;
import org.drools.task.service.responsehandlers.BlockingTaskSummaryResponseHandler;
import org.drools.vsm.Message;
import org.drools.vsm.task.TaskClientMessageHandlerImpl.TaskSummaryMessageResponseHandler;

public class BlockingTaskSummaryMessageResponseHandler extends BlockingTaskSummaryResponseHandler implements TaskSummaryMessageResponseHandler {

	public void receive(Message message) {
		Command cmd = (Command) message.getPayload();
		List<TaskSummary> results = (List<TaskSummary>) cmd.getArguments().get(0);
		execute(results);
	}

}

