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

package org.drools.decisiontable;

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

public class Person {
    private String name;
    private String likes;
    private int    age;

    private char         sex;

    private boolean      alive;

    private String       status;

    public Person() {
    	
    }
    public Person(final String name) {
        this( name,
              "",
              0 );
    }

    public Person(final String name,
                  final String likes) {
        this( name,
              likes,
              0 );
    }

    public Person(final String name,
                  final String likes,
                  final int age) {
        this.name = name;
        this.likes = likes;
        this.age = age;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(final String status) {
        this.status = status;
    }

    public String getLikes() {
        return this.likes;
    }

    public String getName() {
        return this.name;
    }

    public int getAge() {
        return this.age;
    }

    public boolean isAlive() {
        return this.alive;
    }

    public void setAlive(final boolean alive) {
        this.alive = alive;
    }

    public char getSex() {
        return this.sex;
    }

    public void setSex(final char sex) {
        this.sex = sex;
    }

    public String toString() {
        return "[Person name='" + this.name + "']";
    }
}