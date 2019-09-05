/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.process.workitem;

/**
 * Top level of a policy that should be applied to work items.
 * Most of the cases it is used to restrict access or operations on
 * top of the work item.
 *
 * @param <T> type of the policy object to be used to react to it.
 */
public interface Policy<T> {

    /**
     * Actual type of policy data used to enforce this policy
     * @return policy data
     */
    T value();
}
