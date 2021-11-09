/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 *
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.modelcompiler.constraints;

import org.drools.model.functions.PredicateInformation;

import static org.drools.core.util.MessageUtils.formatConstraintErrorMessage;

public class ConstraintEvaluationException extends RuntimeException {

    private static final long serialVersionUID = 7880877148568087603L;

    public ConstraintEvaluationException(PredicateInformation predicateInformation, Throwable cause) {
        super(formatConstraintErrorMessage(predicateInformation.getStringConstraint(), predicateInformation.getRuleNameMap(), predicateInformation.isMoreThanMaxRuleDefs()), cause);
    }
}
