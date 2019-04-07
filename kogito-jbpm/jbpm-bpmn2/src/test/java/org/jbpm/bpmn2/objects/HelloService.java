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

package org.jbpm.bpmn2.objects;

public class HelloService {
	
	public static String VALIDATE_STRING = null;
    
    public String hello(String name) {
        return "Hello " + name + "!";
    }
    
    public String helloEcho(String name) {
        return name;
    }
    
    public String validate(String value) {
    	if (VALIDATE_STRING != null) {
    		if (!VALIDATE_STRING.equals(value)) {
    			throw new RuntimeException("Value does not match expected string: " + value);
    		}
    	}
    	return value;
    }

    public String helloException(String name) {
        throw new RuntimeException("Hello Exception " + name + "!");
    }
}
