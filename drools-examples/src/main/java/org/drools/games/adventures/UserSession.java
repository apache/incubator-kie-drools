/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.games.adventures;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.drools.games.adventures.model.Character;

import org.kie.api.runtime.Channel;

public class UserSession {
    private int                  id;
    private Map<String, Channel> channels;
    private Character            character;

    private static AtomicInteger counter = new AtomicInteger();

    public UserSession() {
        this.id = counter.getAndIncrement();
        this.channels = new HashMap<String, Channel>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Character getCharacter() {
        return character;
    }

    public void setCharacter(Character character) {
        this.character = character;
    }

    public Map<String, Channel> getChannels() {
        return channels;
    }
    
    public void setChannels(Map<String, Channel> channels) {
        this.channels = channels;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        UserSession other = (UserSession) obj;
        if ( id != other.id ) return false;
        return true;
    }

    @Override
    public String toString() {
        return "UserSession [id=" + id + ", channels=" + channels + "]";
    }

}
