/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.kogito.mongodb.utils;

public class DocumentConstants {

    public static final String VARIABLE = "variable";
    public static final String VALUE = "value";
    public static final String DOCUMENT_ID = "_id";
    public static final String PROCESS_INSTANCE_ID = "id";
    public static final String PROCESS_BUSINESS_KEY = "businessKey";
    public static final String PROCESS_INSTANCE_ID_INDEX = "index_process_instance_id";
    public static final String PROCESS_BUSINESS_KEY_INDEX = "index_process_instance_business_key";
    public static final String STRATEGIES = "strategies";
    public static final String NAME = "name";
    public static final String PROCESS_INSTANCE = "processInstance";
    public static final String DOCUMENT_MARSHALLING_ERROR_MSG = "Error while marshalling process instance with id as document : ";
    public static final String DOCUMENT_UNMARSHALLING_ERROR_MSG = "Error while unmarshalling document for process instance with id : ";

    private DocumentConstants() {
    }
}
