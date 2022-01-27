/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
package org.drools.ruleunits.impl;

import java.util.ArrayList;
import java.util.List;

import org.drools.ruleunits.api.DataProcessor;
import org.drools.ruleunits.api.DataStream;

public class ListDataStream<T> implements DataStream<T> {

    private final ArrayList<T> values = new ArrayList<>();
    private final List<DataProcessor> subscribers = new ArrayList<>();

    @SafeVarargs
    public static <T> ListDataStream<T> create(T... ts) {
        ListDataStream<T> stream = new ListDataStream<>();
        for (T t : ts) {
            stream.append(t);
        }
        return stream;
    }

    @Override
    public void append(T t) {
        values.add(t);
        subscribers.forEach(s -> s.insert(t));
    }

    @Override
    public void subscribe(DataProcessor subscriber) {
        subscribers.add(subscriber);
        values.forEach(subscriber::insert);
    }

}
