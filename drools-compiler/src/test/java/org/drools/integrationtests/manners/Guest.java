/*
 * Copyright 2005 JBoss Inc
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

package org.drools.integrationtests.manners;

import java.io.Serializable;

public class Guest implements Serializable {
    private String name;

    private Sex sex;

    private Hobby hobby;

    public Guest() {

    }

    public Guest(String name, Sex sex, Hobby hobby) {
        this.name = name;
        this.sex = sex;
        this.hobby = hobby;
    }

    public String getName() {
        return this.name;
    }

    public Hobby getHobby() {
        return this.hobby;
    }

    public Sex getSex() {
        return this.sex;
    }

    public String toString() {
        return "[Guest name=" + this.name + ", sex=" + this.sex + ", hobbies="
                + this.hobby + "]";
    }
}
