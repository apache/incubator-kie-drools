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

package org.drools.example.cdi.scopes;

import javax.enterprise.inject.spi.PassivationCapable;
import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

public class MyBean implements PassivationCapable, Serializable {

    private static final AtomicInteger idCounter = new AtomicInteger(0);

    private final String id;

    public MyBean() {
        this.id = "MyBean[" + idCounter.getAndIncrement() + "]";
    }

    public String getId() {
        return id;
    }

    public String doSomething(String text){
        System.out.println("Doing Something with: "+text);
        return text + " processed!";
    }
}
