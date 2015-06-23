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

package org.drools.examples.birdsfly;

public class Rocket {
    private Bird bird;

    public Rocket(Bird bird) {
        this.bird = bird;
    }

    public Bird getBird() {
        return bird;
    }

    public void setBird(Bird bird) {
        this.bird = bird;
    }

    @Override
    public String toString() {
        return "Rocket{" +
               "bird=" + bird +
               '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }

        Rocket rocket = (Rocket) o;

        if (!bird.equals(rocket.bird)) { return false; }

        return true;
    }

    @Override
    public int hashCode() {
        return bird.hashCode();
    }
}
