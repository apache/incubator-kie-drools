package org.drools.vsm.task.responseHandlers;

import org.drools.task.service.responsehandlers.BlockingDeleteAttachmentResponseHandler;
import org.drools.vsm.Message;
import org.drools.vsm.task.TaskClientMessageHandlerImpl.DeleteAttachmentMessageResponseHandler;

public class BlockingDeleteAttachmentMessageResponseHandler extends BlockingDeleteAttachmentResponseHandler implements DeleteAttachmentMessageResponseHandler {

	public void receive(Message message) {
		setDone(true);
	}

}