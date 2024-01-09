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
package org.kie.dmn.feel.lang.ast.forexpressioniterators;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Iterator;
import java.util.function.Supplier;

public class ForIteration {
    private String name;
    private Iterable values;

    private Supplier<Iterator> iteratorGenerator;
    private Iterator iterator;

    public ForIteration(String name, Iterable values) {
        this.name = name;
        this.values = values;
        this.iteratorGenerator = () -> this.values.iterator();
    }

    public ForIteration(String name, final BigDecimal start, final BigDecimal end) {
        this.name = name;
        this.iteratorGenerator = () -> new BigDecimalRangeIterator(start, end);
    }

    public ForIteration(String name, final LocalDate start, final LocalDate end) {
        this.name = name;
        this.iteratorGenerator = () -> new LocalDateRangeIterator(start, end);
    }

    public boolean hasNextValue() {
        if (iterator == null) {
            iterator = iteratorGenerator.get();
        }
        boolean hasValue = this.iterator.hasNext();
        if (!hasValue) {
            this.iterator = null;
        }
        return hasValue;
    }

    public Object getNextValue() {
        return iterator != null ? iterator.next() : null;
    }

    public String getName() {
        return name;
    }
}