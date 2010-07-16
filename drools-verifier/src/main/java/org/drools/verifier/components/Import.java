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
public class Import extends PackageComponent {

    private String name;
    private String shortName;

    public Import(RulePackage rulePackage) {
        super( rulePackage );
    }

    @Override
    public String getPath() {
        return String.format( "%s/import[@name='%s']",
                              getPackagePath(),
                              getName() );
    }

    @Override
    public VerifierComponentType getVerifierComponentType() {
        return VerifierComponentType.IMPORT;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }
}
