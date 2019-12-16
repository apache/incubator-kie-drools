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

package org.drools.model.datasources.impl;

import java.util.ArrayList;
import java.util.List;

import org.drools.model.datasources.DataSourceObserver;
import org.drools.model.datasources.Observable;

public class AbstractObservable implements Observable {

    private final List<DataSourceObserver> observers = new ArrayList<DataSourceObserver>();

    @Override
    public synchronized void addObserver(DataSourceObserver o ) {
        observers.add(o);
    }

    @Override
    public void deleteObserver(DataSourceObserver o ) {
        observers.remove(o);
    }

    protected synchronized void notifyInsert(Object obj) {
        for (DataSourceObserver observer : observers) {
            observer.objectInserted(obj);
        }
    }

    protected synchronized void notifyUpdate(Object obj) {
        for (DataSourceObserver observer : observers) {
            observer.objectUpdated(obj);
        }
    }
    protected synchronized void notifyDelete(Object obj) {
        for (DataSourceObserver observer : observers) {
            observer.objectDeleted(obj);
        }
    }
}
