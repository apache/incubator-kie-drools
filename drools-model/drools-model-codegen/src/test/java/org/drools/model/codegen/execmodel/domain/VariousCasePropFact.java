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
package org.drools.model.codegen.execmodel.domain;

public class VariousCasePropFact {

    private String value; // lower only
    private String MyTarget; // upper + lower
    private String URL; // upper + upper
    private String 名前; // multibyte
    private String My名前; // upper + lower + multibyte
    public String MyPublicTarget; // public field : upper + lower

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getMyTarget() {
        return MyTarget;
    }

    public void setMyTarget(String myTarget) {
        MyTarget = myTarget;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String uRL) {
        URL = uRL;
    }

    public String get名前() {
        return 名前;
    }

    public void set名前(String 名前) {
        this.名前 = 名前;
    }

    public String getMy名前() {
        return My名前;
    }

    public void setMy名前(String my名前) {
        My名前 = my名前;
    }

}
