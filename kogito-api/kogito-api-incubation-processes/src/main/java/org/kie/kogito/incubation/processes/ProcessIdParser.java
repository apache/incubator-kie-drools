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
package org.kie.kogito.incubation.processes;

import java.util.StringTokenizer;

import org.kie.kogito.incubation.common.LocalId;

/**
 * Utility class to parse a String into a process identifier of the given type.
 */
public final class ProcessIdParser {
    private ProcessIdParser() {

    }

    public static <T extends LocalId> T parse(String id, Class<T> expected) {
        StringTokenizer tok = new StringTokenizer(id, "/");
        if (!tok.hasMoreTokens())
            throwInvalid(id, "/");

        String processes = tok.nextToken();
        if (!processes.equals(LocalProcessId.PREFIX))
            throwInvalid(id, LocalProcessId.PREFIX);

        if (!tok.hasMoreTokens())
            throwInvalid(id, "a process id");
        String processId = tok.nextToken();

        LocalProcessId localProcessId = new LocalProcessId(processId);
        if (expected == localProcessId.getClass())
            return (T) localProcessId;
        if (!tok.hasMoreTokens())
            throwInvalid(id, expected.getName());

        String instances = tok.nextToken();
        if (!instances.equals(ProcessInstanceId.PREFIX))
            throwInvalid(id, ProcessInstanceId.PREFIX);
        if (!tok.hasMoreTokens())
            throwInvalid(id, "a process instance id");

        String processInstanceId = tok.nextToken();
        ProcessInstanceId instanceId = localProcessId.instances().get(processInstanceId);
        if (expected == instanceId.getClass())
            return (T) instanceId;
        if (!tok.hasMoreTokens())
            throwInvalid(id, expected.getName());

        String nextToken = tok.nextToken();

        if (nextToken.equals(TaskId.PREFIX)) {
            TaskIds taskIds = instanceId.tasks();
            if (expected == taskIds.getClass())
                return (T) taskIds;
            if (!tok.hasMoreTokens())
                throwInvalid(id, expected.getName());

            String task = tok.nextToken();
            TaskId taskId = instanceId.tasks().get(task);
            if (expected == taskId.getClass())
                return (T) taskId;
            if (!tok.hasMoreTokens() && expected.isInstance(taskId))
                return (T) taskId;

            String taskInstances = tok.nextToken();
            if (!taskInstances.equals(TaskInstanceId.PREFIX))
                throwInvalid(id, TaskInstanceId.PREFIX);
            if (!tok.hasMoreTokens())
                throwInvalid(id, "a task instance id");

            String taskInstance = tok.nextToken();
            TaskInstanceId taskInstanceId = taskId.instances().get(taskInstance);
            if (expected == taskInstanceId.getClass())
                return (T) taskInstanceId;
            if (!tok.hasMoreTokens())
                throwInvalid(id, expected.getName());

            String attachmentsOrComments = tok.nextToken();
            if (attachmentsOrComments.equals(AttachmentId.PREFIX)) {
                String attachment = tok.nextToken();
                AttachmentId attachmentId = taskInstanceId.attachments().get(attachment);
                if (expected == attachmentId.getClass())
                    return (T) attachmentId;
                if (expected.isInstance(attachmentId))
                    return (T) attachmentId;
                if (tok.hasMoreTokens())
                    throwInvalid(id, "End of URI");
            }
            if (attachmentsOrComments.equals(CommentId.PREFIX)) {
                String comment = tok.nextToken();
                CommentId commentId = taskInstanceId.comments().get(comment);
                if (expected == commentId.getClass())
                    return (T) commentId;
                if (expected.isInstance(commentId))
                    return (T) commentId;
                if (tok.hasMoreTokens())
                    throwInvalid(id, "End of URI");
            }

            throw new IllegalArgumentException("Invalid id " + id + "expected: " + expected.getName());

        }

        if (nextToken.equals(SignalId.PREFIX)) {
            if (!tok.hasMoreTokens())
                throwInvalid(id, expected.getName());

            String signal = tok.nextToken();
            SignalId signalId = instanceId.signals().get(signal);
            if (expected == signalId.getClass())
                return (T) signalId;
            if (tok.hasMoreTokens())
                throwInvalid(id, "End of URI");

            if (expected.isInstance(signalId))
                return (T) signalId;
        }

        throw new IllegalArgumentException("Invalid id " + id + "expected: " + expected.getName());

    }

    public static <T extends LocalId> T select(LocalId id, Class<T> expected) {
        // the proper way to do this is by "visiting" the structured value;
        // we are taking this as a shortcut for now
        return parse(id.asLocalUri().path(), expected);
    }

    private static void throwInvalid(String id, String expected) {
        throw new IllegalArgumentException("Invalid id " + id + "; expected: " + expected);
    }
}
