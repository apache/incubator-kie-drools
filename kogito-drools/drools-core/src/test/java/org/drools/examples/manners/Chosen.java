/*
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

package org.drools.examples.manners;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class Chosen
    implements
    Externalizable {

    /**
     *
     */
    private static final long serialVersionUID = 510l;

    private int               id;

    private String            guestName;

    private Hobby             hobby;

    public Chosen() {

    }

    public Chosen(final int id,
                  final String guestName,
                  final Hobby hobby) {
        this.id = id;
        this.guestName = guestName;
        this.hobby = hobby;
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        id  = in.readInt();
        guestName   = (String)in.readObject();
        hobby       = (Hobby)in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(id);
        out.writeObject(guestName);
        out.writeObject(hobby);
    }
    
    public int getId() {
        return this.id;
    }

    public String getGuestName() {
        return this.guestName;
    }

    public Hobby getHobby() {
        return this.hobby;
    }

    public String toString() {
        return "{Chosen id=" + this.id + ", name=" + this.guestName + ", hobbies=" + this.hobby + "}";
    }
}
