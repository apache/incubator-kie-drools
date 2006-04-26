package org.drools.examples.manners;
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





import java.io.Serializable;

public final class Chosen
    implements
    Serializable {

    private final int    id;

    private final String guestName;

    private final Hobby  hobby;

    public Chosen(int id,
                  String guestName,
                  Hobby hobby) {
        this.id = id;
        this.guestName = guestName;
        this.hobby = hobby;
    }

    public final int getId() {
        return this.id;
    }

    public final String getGuestName() {
        return this.guestName;
    }

    public final Hobby getHobby() {
        return this.hobby;
    }

    public final String toString() {
        return "{Chosen id=" + this.id + ", name=" + this.guestName + ", hobbies=" + this.hobby + "}";
    }
}