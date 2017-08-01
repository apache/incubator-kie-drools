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

package org.jbpm.remote.ejb.test;

public class ProcessDefinitions {

    public static final String SCRIPT_TASK = "org.jboss.qa.bpms.ScriptTask";
    public static final String SCRIPT_TASK_TWO_VARIABLES = "org.jboss.qa.bpms.ScriptTaskTwoVariables";
    public static final String SIGNAL = "org.jboss.qa.bpms.IntermediateSignalProcess";
    public static final String HUMAN_TASK = "org.jboss.qa.bpms.HumanTask";
    public static final String HUMAN_TASK_WITH_FORM = "org.jboss.qa.bpms.HumanTaskWithForm";
    public static final String HUMAN_TASK_WITH_OWN_TYPE = "org.jboss.qa.bpms.HumanTaskWithOwnType";
    public static final String HUMAN_TASK_WITH_DIFFERENT_TYPES = "org.jboss.qa.bpms.HumanTaskWithDifferentTypes";
    public static final String PROCESS_WITH_UNDERSCORE_IN = "org.jboss.qa.bpms.BZ_994509";
    public static final String WEB_SERVICE_WORK_ITEM = "org.jboss.qa.bpms.WebServiceWorkItem";
    public static final String REST_WORK_ITEM = "org.jboss.qa.bpms.RestWorkItem";
    public static final String OBJECT_VARIABLE = "org.jboss.qa.bpms.ObjectVariableProcess";
    public static final String CUSTOM_VARIABLE = "org.jboss.qa.bpms.CustomVariableProcess";

    private ProcessDefinitions() {
    }

}