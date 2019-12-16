/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.drools.model.constraints;

import org.drools.model.Constraint;

public abstract class AbstractConstraint implements Constraint {

    public OrConstraints or(Constraint constraint) {
        return new OrConstraints(this, constraint);
    }

    public MultipleConstraints with(Constraint constraint) {
        return new MultipleConstraints(this, constraint);
    }

    public AndConstraints and(Constraint constraint) {
        return new AndConstraints(this, constraint);
    }
}