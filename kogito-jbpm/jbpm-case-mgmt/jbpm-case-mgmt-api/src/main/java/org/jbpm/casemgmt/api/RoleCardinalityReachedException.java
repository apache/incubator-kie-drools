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

package org.jbpm.casemgmt.api;

/**
 * Thrown when assigning entities (user or group) to role instance where assigned already
 * entities reached defined cardinality. 
 *
 */
public class RoleCardinalityReachedException extends RuntimeException {

    private static final long serialVersionUID = -6105558767536810447L;

    public RoleCardinalityReachedException(String message, Throwable cause) {
        super(message, cause);
    }

    public RoleCardinalityReachedException(String message) {
        super(message);
    }

}
