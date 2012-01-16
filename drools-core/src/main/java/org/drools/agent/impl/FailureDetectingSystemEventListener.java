/*
 * Copyright 2011 JBoss Inc
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

package org.drools.agent.impl;

import java.util.ArrayList;
import java.util.List;

import org.drools.SystemEventListener;

public class FailureDetectingSystemEventListener
    implements SystemEventListener {

    private List<Throwable> exceptionList = new ArrayList<Throwable>();

    public void debug(String message) {
    }
    
    public void debug(String message,
                      Object object) {
    }

    public void exception(String message, Throwable e) {
        // Note that if none of the code checks the exceptionList, the exception is effectively eaten
        exceptionList.add(e);
    }

    public void exception(Throwable e) {
        // Note that if none of the code checks the exceptionList, the exception is effectively eaten
        exceptionList.add(e);
    }

    public void info(String message) {
    }
    
    public void info(String message,
                     Object object) {
    }

    public void warning(String message) {
    }

    public void warning(String message,
                        Object object) {
    }

    public boolean isSuccessful() {
        return exceptionList.isEmpty();
    }

    public List<Throwable> getExceptionList() {
        return exceptionList;
    }

}
