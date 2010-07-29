/**
 * Copyright 2010 JBoss Inc
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

package org.drools.lang.descr;

/** This represents a function call - as in calling a Drools function. 
 * eg: functionName(argument list)
 */
public class FunctionCallDescr extends DeclarativeInvokerDescr {

    private static final long serialVersionUID = 510l;
    private String name;
    private String arguments;

    public FunctionCallDescr(final String name) {
        this.name = name;
    }

    public String getArguments() {
        return this.arguments;
    }

    public void setArguments(final String arguments) {
        this.arguments = arguments;
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String toString() {
        return this.name + this.arguments;
    }

}
