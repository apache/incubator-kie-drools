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

public abstract class PatternComponent extends RuleComponent {

    private String patternName;
    private int    patternOrderNumber;

    public PatternComponent(Pattern pattern) {
        this( pattern.getPackageName(),
              pattern.getRuleName(),
              pattern.getName(),
              pattern.getOrderNumber() );

    }

    PatternComponent(String packageName,
                     String ruleName,
                     String patternName,
                     int patternOrderNumber) {
        super( packageName,
               ruleName );

        this.patternName = patternName;
        this.patternOrderNumber = patternOrderNumber;
    }

    public String getPatternPath() {
        return String.format( "%s/ruleComponent[@type=%s @orderNumber=%s]",
                              getRulePath(),
                              VerifierComponentType.PATTERN.getType(),
                              patternOrderNumber );
    }

    @Override
    public String getPath() {
        return String.format( "%s/patternComponent[%s]",
                              getPatternPath(),
                              getOrderNumber() );
    }

    public String getPatternName() {
        return patternName;
    }

    public int getPatternOrderNumber() {
        return patternOrderNumber;
    }

}
