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

package org.drools.compiler;

public class DomainObjectHolder {

    DomainObject[] objects = new DomainObject[3];

    public DomainObjectHolder(){

    objects[0] = new DomainObject();
    objects[0].setMessage("Message1");
    objects[0].setValue(1);
    objects[0].setValue2(2);

    objects[1] = new DomainObject();
    objects[1].setMessage("Message2");
    objects[1].setValue(3);
    objects[1].setValue2(4);

    objects[2] = new DomainObject();
    objects[2].setMessage("Message3");
    objects[2].setValue(5);
    objects[2].setValue2(6);
    }

    public DomainObject[] getObjects(){
    return objects;
    }

    }
