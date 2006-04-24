package org.drools.reteoo;
/*
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



import org.drools.FactHandle;

/*
 * InstrumentedWorkingMemoryImpl memory thats extends WorkingMemoryImpl and exposes some
 * package protected methods.
 */
public class InstrumentedWorkingMemoryImpl extends WorkingMemoryImpl {
    public InstrumentedWorkingMemoryImpl() {
        this( new RuleBaseImpl() );
    }

    public InstrumentedWorkingMemoryImpl(RuleBaseImpl ruleBase) {
        super( ruleBase );
    }

    /**
     * Associate an object with its handle.
     * 
     * @param handle
     *            The handle.
     * @param object
     *            The object.
     */
    public void putObject(FactHandle handle,
                            Object object) {
        super.putObject( handle,
                         object );
    }

    /**
     * Helper method to return FactHandles with a specific id
     */
    public FactHandle createFactHandle(int id) {
        return new FactHandleImpl( id );
    }
}