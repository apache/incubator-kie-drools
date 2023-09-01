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
package acme;

import org.kie.dmn.feel.lang.FEELProperty;
import org.kie.dmn.feel.lang.FEELType;

@FEELType
public class Person {
    private String fn;
    private String ln;
    private int age;
    
    public Person() {}
    
    public Person(String fn, String ln) {
        super();
        this.fn = fn;
        this.ln = ln;
    }

    public Person(String fn, String ln, int age) {
        this(fn, ln);
        this.setAge(age);
    }

    @FEELProperty("first name")
    public String getFN() {
        return fn;
    }
    
    public void setFN(String firstName) {
        this.fn = firstName;
    }
    
    @FEELProperty("last name")
    public String getLN() {
        return ln;
    }
    
    public void setLN(String lastName) {
        this.ln = lastName;   
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Person [fn=").append(fn).append(", ln=").append(ln).append("]");
        return builder.toString();
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
    
}
