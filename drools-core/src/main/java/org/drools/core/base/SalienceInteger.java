/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.core.base;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.drools.core.common.ReteEvaluator;
import org.drools.core.spi.Activation;
import org.drools.core.spi.Salience;
import org.kie.api.definition.rule.Rule;

public class SalienceInteger
    implements
    Salience, Externalizable {

    private static final long serialVersionUID = 510l;

    public static final Salience DEFAULT_SALIENCE = new SalienceInteger( DEFAULT_SALIENCE_VALUE );

    private int value;

    public SalienceInteger() {
    }

    public SalienceInteger(int value) {
        this.value = value;
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        value   = in.readInt();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(value);
    }

    public int getValue(final Activation activation,
                        final Rule rule,
                        final ReteEvaluator reteEvaluator) {
        return this.value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public boolean isDefault() {
        return getValue() == DEFAULT_SALIENCE.getValue();
    }

    public String toString() {
        return String.valueOf( this.value );
    }

    public boolean isDynamic() {
        return false;
    }

}
