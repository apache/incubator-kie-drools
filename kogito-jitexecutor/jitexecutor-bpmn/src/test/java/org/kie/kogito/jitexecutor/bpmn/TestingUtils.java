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
package org.kie.kogito.jitexecutor.bpmn;

public class TestingUtils {

    public static final String SINGLE_BPMN_FILE = "/SingleProcess.bpmn";
    public static final String MULTIPLE_BPMN_FILE = "/MultipleProcess.bpmn";

    public static final String INVALID_BPMN_FILE = "/InvalidModel.bpmn";

    public static final String UNPARSABLE_BPMN_FILE = "/UnparsableModel.bpmn";
    public static final String SINGLE_BPMN2_FILE = "/SingleProcess.bpmn2";
    public static final String MULTIPLE_BPMN2_FILE = "/MultipleProcess.bpmn2";
    public static final String SINGLE_INVALID_BPMN2_FILE = "/SingleInvalidModel.bpmn2";

    public static final String SINGLE_UNPARSABLE_BPMN2_FILE = "/SingleUnparsableModel.bpmn2";

    public static final String MULTIPLE_INVALID_BPMN2_FILE = "/MultipleInvalidModel.bpmn2";

    public static final String UNPARSABLE_BPMN2_FILE = "/UnparsableModel.bpmn2";

    public static String getFilePath(String fileName) {
        return "src/test/resources" + fileName;
    }

}
