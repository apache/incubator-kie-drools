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
package org.drools.model.constraints;

import java.util.ArrayList;
import java.util.List;

import org.drools.model.Constraint;
import org.drools.model.Variable;
import org.drools.model.impl.ModelComponent;

import static java.util.stream.Collectors.toList;

public class MultipleConstraints extends AbstractConstraint implements ModelComponent {

    private final List<Constraint> constraints;

    public MultipleConstraints( List<Constraint> constraints) {
        this.constraints = constraints;
    }

    public MultipleConstraints( Constraint... constraints) {
        this.constraints = new ArrayList<>();
        for (Constraint constraint : constraints) {
            with(constraint);
        }
    }

    @Override
    public MultipleConstraints with( Constraint constraint) {
        constraints.add(constraint);
        return this;
    }

    @Override
    public List<Constraint> getChildren() {
        return constraints;
    }

    @Override
    public Type getType() {
        return Type.MULTIPLE;
    }

    @Override
    public boolean isEqualTo( ModelComponent o ) {
        if ( this == o ) return true;
        if ( !(o instanceof MultipleConstraints) ) return false;

        MultipleConstraints that = ( MultipleConstraints ) o;

        return ModelComponent.areEqualInModel( constraints, that.constraints );
    }

    @Override
    public Constraint negate() {
        if (constraints.size() == 1) {
            return new MultipleConstraints(constraints.get(0).negate());
        }
        OrConstraints or = new OrConstraints();
        for (Constraint constraint : constraints) {
            or.or( constraint.negate() );
        }
        return or;
    }

    @Override
    public MultipleConstraints replaceVariable( Variable oldVar, Variable newVar ) {
        return new MultipleConstraints( constraints.stream().map( c -> c.replaceVariable( oldVar, newVar ) ).collect( toList() ) );
    }

    @Override
    public String toString() {
        return "MultipleConstraints (constraints: " + constraints + ")";
    }

    @Override
    public MultipleConstraints cloneConstraint() {
        return new MultipleConstraints(new ArrayList<>(constraints));
    }
}
