/*
 * Copyright 2005 JBoss Inc
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

package org.drools.modelcompiler;

import java.util.ArrayList;
import java.util.Collection;

public class Result {
    private Object value;

    public Result() {
        // empty constructor.
    }

    public Result( Object value ) {
        this.value = value;
    }

    public Object getValue() {
        return value;
    }

    public void setValue( Object value ) {
        this.value = value;
    }

    public void addValue( Object value ) {
        if (!(this.value instanceof Collection)) {
            this.value = new ArrayList<>();
        }
        ( (Collection) this.value ).add(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Result result = (Result) o;

        return value.equals(result.getValue());
    }
}
