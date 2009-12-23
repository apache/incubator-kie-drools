package org.drools.vsm.task.responseHandlers;

import org.drools.task.service.responsehandlers.BlockingDeleteCommentResponseHandler;
import org.drools.vsm.Message;
import org.drools.vsm.task.TaskClientMessageHandlerImpl.DeleteCommentMessageResponseHandler;

public class BlockingDeleteCommentMessageResponseHandler extends BlockingDeleteCommentResponseHandler implements DeleteCommentMessageResponseHandler {

	public void receive(Message message) {
		setIsDone(true);
	}

}