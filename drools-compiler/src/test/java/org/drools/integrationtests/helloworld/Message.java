package org.drools.integrationtests.helloworld;

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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Message {

    private String  message;
    private List    list     = new ArrayList();
    private int     number   = 0;
    private Date    birthday = new Date();
    private boolean fired    = false;

    public boolean isFired() {
        return this.fired;
    }

    public void setFired(final boolean fired) {
        this.fired = fired;
    }

    public Date getBirthday() {
        return this.birthday;
    }

    public void setBirthday(final Date birthday) {
        this.birthday = birthday;
    }

    public int getNumber() {
        return this.number;
    }

    public void setNumber(final int number) {
        this.number = number;
    }

    public Message(final String msg) {
        this.message = msg;
    }

    public String getMessage() {
        return this.message;
    }

    public List getList() {
        return this.list;
    }

    public void setList(final List list) {
        this.list = list;
    }

    public void addToList(final String s) {
        this.list.add( s );
    }

}