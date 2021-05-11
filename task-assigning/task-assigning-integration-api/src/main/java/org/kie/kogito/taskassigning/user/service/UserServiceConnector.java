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

package org.kie.kogito.taskassigning.user.service;

import java.util.List;

/**
 * User service connector implementations are loaded by CDI and must be qualified with the UserServiceConnectorQualifier
 * in order to configure the particular implementation name. This name is used for configuring the property
 * kogito.task-assigning.user-service-connector in cases where this connector is to be used.
 * 
 * e.g.
 * 
 * <pre>
 *  
 *     {@code
 *     &#64;ApplicationScoped
 *     &#64;UserServiceConnectorName("MyUserServiceConnector")
 *     public class MyUserServiceConnector implements UserServiceConnector {
 *          ......
 *     }
 *     }
 * </pre>
 */
public interface UserServiceConnector {

    /**
     * Invoked by the task assigning service as part of the initialization procedure and before any other method
     * is invoked.
     */
    void start();

    /**
     * @return the list of all users present in the external user system.
     */
    List<User> findAllUsers();

    /**
     * Get the user information corresponding the user identified by the id.
     * 
     * @param id a user identifier.
     * @return the User corresponding to the given identifier, null if no user was found.
     */
    User findUser(String id);

}
