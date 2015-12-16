/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

package org.drools.compiler;

public class YoungestFather {
    
    private Father man;

    public YoungestFather(Father man) {
        this.man = man;
    }

    public Father getMan() {
        return man;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o instanceof YoungestFather) {
            YoungestFather other = (YoungestFather) o;
            return man == other.man;
        } else {
            return false;
        }
    }

    public int hashCode() {
        return man == null ? 0 : man.hashCode();
    }

    @Override
    public String toString() {
        return "YoungestFather [man=" + man + "]";
    }
    
    

}
