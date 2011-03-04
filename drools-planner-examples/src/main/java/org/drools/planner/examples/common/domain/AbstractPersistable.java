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

package org.drools.planner.examples.common.domain;

import java.io.Serializable;

public abstract class AbstractPersistable implements Serializable {

    protected Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

// This part is currently commented out because it's probably a bad thing to mix identification with equality

//    public boolean equals(Object o) {
//        if (this == o) {
//            return true;
//        }
//        if (id == null || !(o instanceof AbstractPersistable)) {
//            return false;
//        } else {
//            AbstractPersistable other = (AbstractPersistable) o;
//            return id.equals(other.id);
//        }
//    }
//
//    public int hashCode() {
//        if (id == null) {
//            return super.hashCode();
//        } else {
//            return id.hashCode();
//        }
//    }

//    public int compareTo(AbstractPersistable abstractPersistable) {
//        return id.compareTo(abstractPersistable.id);
//    }

    public String toString() {
        return "[" + getClass().getName().replaceAll(".*\\.", "") + "-" + id + "]";
    }

}
