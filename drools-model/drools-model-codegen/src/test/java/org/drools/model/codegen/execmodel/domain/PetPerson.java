/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.model.codegen.execmodel.domain;

import java.util.HashMap;
import java.util.Map;

public class PetPerson extends Person {

    Map<String, Pet> pets;

    public PetPerson() {
        super();
        pets = new HashMap<String, Pet>();
    }

    public PetPerson(String name) {
        super(name);
        pets = new HashMap<String, Pet>();
    }

    public Map<String, Pet> getPets() {
        return pets;
    }

    public void setPets(Map<String, Pet> pets) {
        this.pets = pets;
    }

    public void addPet(String name, Pet p) {
        pets.put(name, p);
    }

    public void removePet(String name) {
        pets.remove(name);
    }

    public void clearPets() {
        pets.clear();
    }

    public Pet getPet(String name) {
        return pets.get(name);
    }
}
