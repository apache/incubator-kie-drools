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

package org.drools.games.adventures.model;

import org.kie.api.definition.type.Position;
import org.kie.api.definition.type.PropertyReactive;

@PropertyReactive
public class SearchCommand extends Command {
    @Position(1)
    private Thing     thing;

    public SearchCommand() {

    }

    public SearchCommand(Thing thing) {
        this.thing = thing;
    }

    public Thing getThing() {
        return thing;
    }

    public void setThing(Thing thing) {
        this.thing = thing;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }

        SearchCommand that = (SearchCommand) o;

        return thing != null ? thing.equals(that.thing) : that.thing == null;

    }

    @Override
    public int hashCode() {
        return thing != null ? thing.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "SearchCommand{" +
               ", thing=" + thing +
               '}';
    }
}
