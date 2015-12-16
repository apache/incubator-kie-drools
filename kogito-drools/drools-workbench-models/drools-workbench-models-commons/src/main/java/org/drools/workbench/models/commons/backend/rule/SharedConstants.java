/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.models.commons.backend.rule;

import java.io.Serializable;

/**
 * Shared resources
 */
public class SharedConstants
        implements
        Serializable {

    //This is a near magical constant! Any Operators that can accept parameters (only CEP at the time of writing) 
    //need to specify an implementation of "org.drools.ide.common.server.util.OperatorParameterBuilder" to construct
    //the necessary DRL from the parameters. This is the key name for the applicable class amongst the Parameters.
    public static final String OPERATOR_PARAMETER_GENERATOR = "org.drools.workbench.models.commons.backend.rule.operatorParameterGenerator";

}
