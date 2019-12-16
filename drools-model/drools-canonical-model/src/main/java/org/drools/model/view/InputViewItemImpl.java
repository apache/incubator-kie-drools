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

package org.drools.model.view;

import org.drools.model.Variable;

public class InputViewItemImpl<T> implements InputViewItem<T> {
    private final Variable<T> var;
    private String[] watchedProps;

    public InputViewItemImpl( Variable<T> var ) {
        this.var = var;
    }

    @Override
    public Variable getFirstVariable() {
        return var;
    }

    @Override
    public Variable<?>[] getVariables() {
        return new Variable[] { var };
    }

    @Override
    public InputViewItemImpl<T> watch(String... props) {
        this.watchedProps = props;
        return this;
    }

    public String[] getWatchedProps() {
        return watchedProps;
    }
}
