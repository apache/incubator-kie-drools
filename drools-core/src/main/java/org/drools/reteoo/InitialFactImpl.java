/**
 * Copyright 2005 JBoss Inc
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

package org.drools.reteoo;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.drools.InitialFact;

/**
 * We dont want users to be able to instantiate InitialFact so we expose it as
 * an interface and make the class and its constructor package protected
 *
 * @author <a href="mailto:mark.proctor@jboss.com">Mark Proctor</a>
 * @author <a href="mailto:bob@werken.com">Bob McWhirter</a>
 *
 */
public final class InitialFactImpl
    implements
    InitialFact,
    Externalizable {
    private static final InitialFact INSTANCE = new InitialFactImpl();

    private final int                hashCode = "InitialFactImpl".hashCode();

    public static InitialFact getInstance() {
        return InitialFactImpl.INSTANCE;
    }

    public InitialFactImpl() {
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
    }

    public void writeExternal(ObjectOutput out) throws IOException {

    }

    public int hashCode() {
        return this.hashCode;
    }

    public boolean equals(final Object object) {
        if ( this == object ) {
            return true;
        }

        if ( object == null || !(object instanceof InitialFactImpl) ) {
            return false;
        }

        return true;
    }
}
