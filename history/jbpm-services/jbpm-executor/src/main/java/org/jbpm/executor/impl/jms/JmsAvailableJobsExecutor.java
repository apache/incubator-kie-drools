/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.executor.impl.jms;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.jbpm.executor.entities.RequestInfo;
import org.jbpm.executor.impl.AbstractAvailableJobsExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class JmsAvailableJobsExecutor extends AbstractAvailableJobsExecutor implements MessageListener {

    private static final Logger logger = LoggerFactory.getLogger(JmsAvailableJobsExecutor.class);
    @Override
    public void onMessage(Message message) {
        if (message instanceof TextMessage) {
            Long requestId = null;
            TextMessage textMessage = (TextMessage) message;
            try {
                String messageContent = textMessage.getText();
                logger.debug("received Text message with content {}", messageContent);
                requestId = Long.parseLong(messageContent);
                // message content is request id
                RequestInfo request = (RequestInfo) queryService.getRequestForProcessing(requestId);
                logger.debug("Found following job request for id {} : {}", requestId, request);
                if (request != null) {
                    logger.debug("Request with id {} is in valid state, processing...", requestId);
                    executeGivenJob(request);
                }
            } catch (JMSException e) {
                logger.error("JMS Error when processing job with id {} due to {}", requestId, e.getMessage(), e);
                throw new RuntimeException("Exception when receiving executor job request", e);
            } catch (Throwable e) {
                logger.error("Error when processing job with id {} due to {} will ack JMS message and let exeuctor retry it", requestId, e.getMessage(), e);
            }
        }
    }

}
