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

package org.drools.examples.buspass;

public class AdultBusPass extends BusPass {

    public AdultBusPass(Person person) {
        super(person);
    }

    @Override
    public String toString() {
        return "AdultBusPass{" +
               "person=" + getPerson() +
               '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }

        AdultBusPass that = (AdultBusPass) o;

        if (!getPerson().equals(that.getPerson())) { return false; }

        return true;
    }

    @Override
    public int hashCode() {
        return getPerson().hashCode();
    }
}
