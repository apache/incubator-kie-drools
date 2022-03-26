/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.kogito.mail;

import java.util.Arrays;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.kie.kogito.event.process.UserTaskDeadlineDataEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.mailer.Mail;
import io.quarkus.mailer.reactive.ReactiveMailer;

@ApplicationScoped
public class QuarkusMailSender {

    private static final Logger logger = LoggerFactory.getLogger(QuarkusMailSender.class);
    @Inject
    private ReactiveMailer mailer;

    @Incoming("kogito-deadline-consumer")
    public void onDeadline(UserTaskDeadlineDataEvent event) {
        MailInfo mailInfo = MailInfo.of(event.getData());
        logger.info("Sending e-mail {}", mailInfo);
        Mail message = new Mail();
        if (mailInfo.to() != null) {
            message.setTo(Arrays.asList(mailInfo.to()));
        }
        if (mailInfo.from() != null) {
            message.setFrom(mailInfo.from());
        }
        if (mailInfo.replyTo() != null) {
            message.setReplyTo(mailInfo.replyTo());
        }
        if (mailInfo.subject() != null) {
            message.setSubject(mailInfo.subject());
        }
        if (mailInfo.body() != null) {
            message.setText(mailInfo.body());
        }
        mailer.send(message).subscribe().with(this::handleCompleted, this::handleFailure);
    }

    private void handleCompleted(Void v) {
        logger.info("Mail sent");
    }

    private void handleFailure(Throwable e) {
        logger.error("Exception sending mail ", e);
    }
}
