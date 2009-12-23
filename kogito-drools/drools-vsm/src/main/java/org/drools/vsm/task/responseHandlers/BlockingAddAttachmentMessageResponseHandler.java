package org.drools.vsm.task.responseHandlers;

import org.drools.task.service.Command;
import org.drools.task.service.responsehandlers.BlockingAddAttachmentResponseHandler;
import org.drools.vsm.Message;
import org.drools.vsm.task.TaskClientMessageHandlerImpl.AddAttachmentMessageResponseHandler;

public class BlockingAddAttachmentMessageResponseHandler extends BlockingAddAttachmentResponseHandler implements AddAttachmentMessageResponseHandler {

	public void receive(Message message) {
		Command cmd = (Command) message.getPayload();
		Long attachmentId = (Long) cmd.getArguments().get(0);
		Long contentId = (Long) cmd.getArguments().get(1);
		execute(attachmentId, contentId);
	}

}