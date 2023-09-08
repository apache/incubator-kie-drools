/**
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
package org.kie.dmn.trisotech.core.util;

import org.kie.dmn.api.core.DMNMessageType;
import org.kie.dmn.core.util.Msg.Message1;
import org.kie.dmn.core.util.Msg.Message2;

public final class Msg {
    public static final Message2 MISSING_EXPRESSION_FOR_CONDITION                    = new Message2( DMNMessageType.MISSING_EXPRESSION, "Missing %s expression for Conditional node '%s'" );
    public static final Message2 MISSING_EXPRESSION_FOR_ITERATOR                     = new Message2( DMNMessageType.MISSING_EXPRESSION, "Missing %s expression for Iterator node '%s'" );
    public static final Message2 MISSING_EXPRESSION_FOR_FILTER                       = new Message2( DMNMessageType.MISSING_EXPRESSION, "Missing %s expression for Filter node '%s'" );
    public static final Message1 UNABLE_TO_RETRIEVE_PMML_RESULT                      = new Message1( DMNMessageType.INVOCATION_ERROR, "Unable to retrieve result from PMML model '%s'" );
    public static final Message2 CONDITION_RESULT_NOT_BOOLEAN                        = new Message2( DMNMessageType.ERROR_EVAL_NODE, "The if condition on node %s returned a non boolean result: '%s'" );
    public static final Message1 IN_RESULT_NULL                                      = new Message1( DMNMessageType.ERROR_EVAL_NODE, "The in condition on node %s returned null.");
    public static final Message2 INDEX_OUT_OF_BOUND                                  = new Message2( DMNMessageType.ERROR_EVAL_NODE, "Index out of bound: list of %s elements, index %s; will evaluate as FEEL null");

    private Msg() {
        // Constructing instances is not allowed for this class
    }
}
