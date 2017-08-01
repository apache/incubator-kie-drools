/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.test.listener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.kie.api.event.rule.DefaultRuleRuntimeEventListener;
import org.kie.api.event.rule.ObjectDeletedEvent;
import org.kie.api.event.rule.ObjectInsertedEvent;
import org.kie.api.event.rule.ObjectUpdatedEvent;

public class TrackingRuleRuntimeEventListener extends DefaultRuleRuntimeEventListener {

    private List<Object> objects = new CopyOnWriteArrayList<Object>();
    private List<Object> inserted = new ArrayList<Object>();
    private List<Object> updated = new ArrayList<Object>();
    private List<Object> retracted = new ArrayList<Object>();

    @Override
    public void objectInserted(ObjectInsertedEvent event) {
        objects.add(event.getObject());
        inserted.add(event.getObject());
    }

    @Override
    public void objectDeleted(ObjectDeletedEvent event) {
        objects.remove(event.getOldObject());
        retracted.add(event.getOldObject());
    }

    @Override
    public void objectUpdated(ObjectUpdatedEvent event) {
        objects.set(objects.indexOf(event.getOldObject()), event.getObject());
        updated.add(event.getOldObject());
    }

    public boolean containsObject(Object o) {
        return objects.contains(o);
    }

    public boolean hasObjects() {
        return objects.size() > 0;
    }

    public int objectCount() {
        return objects.size();
    }

    public void clear() {
        objects.clear();
        inserted.clear();
        updated.clear();
        retracted.clear();
    }

    public boolean wasInserted(Object o) {
        return inserted.contains(o);
    }

    public boolean wasUpdated(Object o) {
        return updated.contains(o);
    }

    public boolean wasRetracted(Object o) {
        return retracted.contains(o);
    }

}
