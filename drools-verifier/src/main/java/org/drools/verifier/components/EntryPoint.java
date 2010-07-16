/**
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

package org.drools.verifier.components;

/**
 * 
 * @author Toni Rikkola
 */
public class EntryPoint extends VerifierComponentSource {

    private String entryPointName;

    @Override
    public String getPath() {
        return String.format( "source[@type=%s @entryPointName=%s]",
                              getVerifierComponentType().getType(),
                              getEntryPointName() );
    }

    public VerifierComponentType getVerifierComponentType() {
        return VerifierComponentType.ENTRY_POINT_DESCR;
    }

    public void setEntryPointName(String entryPointName) {
        this.entryPointName = entryPointName;
    }

    public String getEntryPointName() {
        return entryPointName;
    }
}
