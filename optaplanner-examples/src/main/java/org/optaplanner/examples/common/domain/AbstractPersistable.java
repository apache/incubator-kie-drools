/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.examples.common.domain;

import java.io.Serializable;

import org.optaplanner.core.api.domain.lookup.PlanningId;

public abstract class AbstractPersistable implements Serializable {

    protected Long id;

    protected AbstractPersistable() {
    }

    protected AbstractPersistable(long id) {
        this.id = id;
    }

    @PlanningId
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
//            return getClass().equals(other.getClass()) && id.equals(other.id);
//        }
//    }
//
//    public int hashCode() {
//        if (id == null) {
//            return super.hashCode();
//        } else {
//            // A direct implementation (instead of HashCodeBuilder) to avoid dependencies
//            return (((17 * 37)
//                    + getClass().hashCode())) * 37
//                    + id.hashCode();
//        }
//    }

    @Override
    public String toString() {
        return getClass().getName().replaceAll(".*\\.", "") + "-" + id;
    }

}
