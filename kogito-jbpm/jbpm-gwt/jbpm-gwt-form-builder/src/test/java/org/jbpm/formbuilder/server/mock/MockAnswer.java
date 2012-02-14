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
package org.jbpm.formbuilder.server.mock;

import java.util.HashMap;
import java.util.Map;

import org.easymock.EasyMock;
import org.easymock.IAnswer;

public class MockAnswer implements IAnswer<Integer> {

    private final Map<String, String> responses;
    private final Map<String, Integer> statuses;
    private final Throwable exception;
    
    public MockAnswer(Throwable exception, Map<String, Integer> statuses) {
        this(new HashMap<String, String>(), statuses, exception);
    }
    
    public MockAnswer(Map<String, String> responses, Throwable exception) {
        this(responses, new HashMap<String, Integer>(), exception);
    }
    
    public MockAnswer(Map<String, Integer> statuses) {
        this(new HashMap<String, String>(), statuses, null);
    }
    
    public MockAnswer(Map<String, String> responses, Map<String, Integer> statuses, Throwable exception) {
        this.responses = responses;
        this.statuses = statuses;
        this.exception = exception;
    }
    
    @Override
    public Integer answer() throws Throwable {
        Object[] params = EasyMock.getCurrentArguments();
        if (params[0] instanceof MockGetMethod) {
            MockGetMethod method = (MockGetMethod) params[0];
            String key = "GET " + method.getURI().toString();
            if (responses.containsKey(key)) {
                method.setResponseBodyAsString(responses.get(key));
            } else if (statuses.containsKey(key)) {
                method.setStatusCode(statuses.get(key));
                return statuses.get(key);
            } else if (exception != null) throw exception;
            return 200;
        } else if (params[0] instanceof MockPostMethod) {
            MockPostMethod method = (MockPostMethod) params[0];
            String key = "POST " + method.getURI().toString();
            if (responses.containsKey(key)) {
                method.setResponseBodyAsString(responses.get(key));
            } else if (statuses.containsKey(key)) {
                method.setStatusCode(statuses.get(key));
                return statuses.get(key);
            } else if (exception != null) throw exception;
            return 201;
        } else if (params[0] instanceof MockPutMethod) {
            MockPutMethod method = (MockPutMethod) params[0];
            String key = "PUT " + method.getURI().toString();
            if (responses.containsKey(key)) {
                method.setResponseBodyAsString(responses.get(key));
            } else if (statuses.containsKey(key)) {
                method.setStatusCode(statuses.get(key));
                return statuses.get(key);
            } else if (exception != null) throw exception;
            return 201;
        } else if (params[0] instanceof MockDeleteMethod) {
            MockDeleteMethod method = (MockDeleteMethod) params[0];
            String key = "DELETE " + method.getURI().toString();
            if (responses.containsKey(key)) {
                method.setResponseBodyAsString(responses.get(key));
            } else if (statuses.containsKey(key)) {
                method.setStatusCode(statuses.get(key));
                return statuses.get(key);
            } else if (exception != null) throw exception;
            return 201;
        } else {
            throw new IllegalArgumentException("params[0] shouldn't be of type " + params[0]);
        }
    }
}