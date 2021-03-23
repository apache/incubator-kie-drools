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
package org.kie.kogito.taskassigning.core.model.solver;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.kie.kogito.taskassigning.core.model.ChainElement;
import org.kie.kogito.taskassigning.core.model.Task;
import org.kie.kogito.taskassigning.core.model.TaskAssignment;
import org.kie.kogito.taskassigning.core.model.User;

import static org.kie.kogito.taskassigning.core.model.ModelConstants.IS_NOT_DUMMY_TASK_ASSIGNMENT;

public class TaskHelper {

    private TaskHelper() {
    }

    /**
     * @return true if the user is a potential owner for the given task. This basically means that the user can be
     *         assigned to the given task, false in any other case.
     */
    public static boolean isPotentialOwner(Task task, User user) {
        if (task.getExcludedUsers().contains(user.getId())) {
            return false;
        }
        //user appears directly in the list of potential users.
        if (task.getPotentialUsers().contains(user.getId())) {
            return true;
        }
        //the user has at least one of the enabled groups for executing the task.
        return user.getGroups().stream()
                .anyMatch(group -> task.getPotentialGroups().contains(group.getId()));
    }

    /**
     * Calculates if a given user has all the label values that are declared for the task in the label with name labelName.
     * 
     * @param task a task instance for the evaluation.
     * @param user a user instance for the evaluation.
     * @param labelName name of the label for the calculation.
     * @return true if the user.getLabelValues("labelName") set "contains" the task.getLabelValues("labelName") set,
     *         false in any other case.
     */
    public static boolean hasAllLabels(Task task, User user, String labelName) {
        Collection<?> taskLabelValues = attributeAsCollection(task.getAttributes().get(labelName));
        Collection<?> userLabelValues = attributeAsCollection(user.getAttributes().get(labelName));
        return userLabelValues.containsAll(taskLabelValues);
    }

    /**
     * Calculates the number labels in the user label value set that are contained in the task label value set for the
     * label labelName.
     * 
     * @param task a task instance for the calculation.
     * @param user a task instance for the calculation.
     * @param labelName name of the label for the calculation.
     * @return the number of elements in the intersection between the task.getLabelValues("labelName") and the
     *         user.getLabelValues("labelName") sets.
     */
    public static int countMatchingLabels(Task task, User user, String labelName) {
        final Collection<?> taskLabelValues = attributeAsCollection(task.getAttributes().get(labelName));
        final Collection<?> userLabelValues = attributeAsCollection(user.getAttributes().get(labelName));
        return Math.toIntExact(userLabelValues.stream().filter(taskLabelValues::contains).count());
    }

    /**
     * Gets the list of task assignments linked to a ChainElement.
     * 
     * @param element a ChainElement instance for the evaluation.
     * @return a list with the tasks linked to the taskAssignment.
     */
    public static List<TaskAssignment> extractTaskAssignments(ChainElement element) {
        return extractTaskAssignments(element, testedElement -> true);
    }

    /**
     * Gets the list of task assignments linked to a ChainElement.
     * 
     * @param element a ChainElement instance for the evaluation.
     * @param predicate a predicate for filtering the tasks that will be included in the result.
     * @return a list with the tasks linked to the taskAssignment that verifies the filtering predicate.
     */
    public static List<TaskAssignment> extractTaskAssignments(ChainElement element, Predicate<TaskAssignment> predicate) {
        final List<TaskAssignment> result = new ArrayList<>();
        TaskAssignment nextTaskAssignment = element != null ? element.getNextElement() : null;
        while (nextTaskAssignment != null) {
            if (predicate.test(nextTaskAssignment)) {
                result.add(nextTaskAssignment);
            }
            nextTaskAssignment = nextTaskAssignment.getNextElement();
        }
        return result;
    }

    /**
     * Indicates if a user has pinned tasks.
     * 
     * @param user a user instance to check.
     * @return true if the user has pinned tasks false any other case.
     */
    public static boolean hasPinnedTasks(ChainElement user) {
        return !extractTaskAssignments(user, TaskAssignment::isPinned).isEmpty();
    }

    public static List<TaskAssignment> filterNonDummyAssignments(List<TaskAssignment> taskAssignments) {
        return taskAssignments.stream()
                .filter(IS_NOT_DUMMY_TASK_ASSIGNMENT)
                .collect(Collectors.toList());
    }

    private static Collection<?> attributeAsCollection(Object attribute) {
        if (attribute == null) {
            return Collections.emptySet();
        }
        if (attribute instanceof Collection) {
            return (Collection) attribute;
        }
        return Collections.singleton(attribute);
    }
}
