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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.kie.kogito.services.event.impl.UserTaskDeadlineEventBody;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MailInfoTest {

    @Test
    public void testMailInfo() {
        Map<String, Object> notification = new HashMap<>();
        notification.put(MailInfo.SUBJECT_PROPERTY, "${inputs.name}");
        notification.put(MailInfo.BODY_PROPERTY, "My name for process ${processInstanceId} is ${inputs.name}");
        notification.put(MailInfo.FROM_PROPERTY, "javierito");
        notification.put(MailInfo.TO_PROPERTY, "javierito@doesnotexist.com,fulanito@doesnotexist.com");
        notification.put(MailInfo.REPLY_TO_PROPERTY, "javierito@doesnotexist.com");
        MailInfo mailInfo = MailInfo.of(UserTaskDeadlineEventBody.create("1", notification).inputs(Collections
                .singletonMap("name", "Javierito")).processInstanceId("1").build());
        assertEquals("Javierito", mailInfo.subject());
        assertEquals("My name for process 1 is Javierito", mailInfo.body());
        assertEquals("javierito", mailInfo.from());
        assertEquals("javierito@doesnotexist.com", mailInfo.replyTo());
        assertArrayEquals(new String[] { "javierito@doesnotexist.com", "fulanito@doesnotexist.com" }, mailInfo.to());
    }
}
