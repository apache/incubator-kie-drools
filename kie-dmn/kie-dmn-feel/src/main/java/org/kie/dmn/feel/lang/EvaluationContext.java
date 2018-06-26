/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.dmn.feel.lang;

import java.util.Collection;
import java.util.Map;
import java.util.function.Supplier;

import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.api.feel.runtime.events.FEELEvent;
import org.kie.dmn.api.feel.runtime.events.FEELEventListener;

public interface EvaluationContext {

    void enterFrame();

    void exitFrame();

    EvaluationContext current();

    void setValue(String name, Object value );

    Object getValue(String name );

    Object getValue(String[] name );

    boolean isDefined( String name );

    boolean isDefined( String[] name );

    Map<String, Object> getAllValues();

    DMNRuntime getDMNRuntime();

    ClassLoader getRootClassLoader();

    void notifyEvt(Supplier<FEELEvent> event);


    Collection<FEELEventListener> getListeners();

    void setRootObject(Object v);

    Object getRootObject();

}
