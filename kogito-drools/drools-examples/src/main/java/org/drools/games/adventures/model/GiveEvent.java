/*
 * Copyright 2015 JBoss Inc
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

package org.drools.games.adventures.model;

import org.kie.api.definition.type.Position;
import org.drools.games.adventures.model.Character;
import org.kie.api.definition.type.PropertyReactive;

@PropertyReactive
public class GiveEvent extends GameEvent {
    @Position(0)
    private Character giver;

    @Position(1)
    private Thing     thing;

    @Position(2)
    private Character receiver;

    public GiveEvent(Character giver, Thing thing, Character receiver) {
        this.giver = giver;
        this.thing = thing;
        this.receiver = receiver;
    }

    public Character getGiver() {
        return giver;
    }

    public void setGiver(Character giver) {
        this.giver = giver;
    }

    public Thing getThing() {
        return thing;
    }

    public void setThing(Thing thing) {
        this.thing = thing;
    }

    public Character getReceiver() {
        return receiver;
    }

    public void setReceiver(Character receiver) {
        this.receiver = receiver;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }

        GiveEvent giveEvent = (GiveEvent) o;

        if (!giver.equals(giveEvent.giver)) { return false; }
        if (!receiver.equals(giveEvent.receiver)) { return false; }
        if (!thing.equals(giveEvent.thing)) { return false; }

        return true;
    }

    @Override
    public int hashCode() {
        int result = giver.hashCode();
        result = 31 * result + thing.hashCode();
        result = 31 * result + receiver.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "GiveEvent{" +
               "receiver=" + receiver +
               ", thing=" + thing +
               ", giver=" + giver +
               '}';
    }
}
