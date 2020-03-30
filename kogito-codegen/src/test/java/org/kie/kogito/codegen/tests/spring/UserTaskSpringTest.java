/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.codegen.tests.spring;

import java.lang.annotation.Annotation;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.kie.kogito.codegen.tests.UserTaskTest;
import org.springframework.web.bind.annotation.PostMapping;

@Disabled
public class UserTaskSpringTest extends UserTaskTest {

    public UserTaskSpringTest() {
        withSpringContext(true);
    }

    private class Dummy {

        @PostMapping(produces = "application/json", consumes = "application/json", value = "/{id}/FirstTask" +
                "/{workItemId}")
        void post1() {

        }

        @PostMapping(produces = "application/json", consumes = "application/json", value = "/{id}/SecondTask/{workItemId}")
        void post2() {

        }
    }

    @Test
    @Override
    public void testRESTApiForUserTasks() throws Exception {
        Annotation firstTask = Dummy.class.getDeclaredMethod("post1").getAnnotation(PostMapping.class);
        Annotation secondTask = Dummy.class.getDeclaredMethod("post2").getAnnotation(PostMapping.class);
        testRESTApiForUserTasks("PostMapping", firstTask, secondTask);
    }
}
