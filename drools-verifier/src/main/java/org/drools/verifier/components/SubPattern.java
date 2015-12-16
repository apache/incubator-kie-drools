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

package org.drools.verifier.components;

import java.util.HashSet;
import java.util.Set;

/**
 * Instance of this class represents a possible combination of Constraints under
 * one Pattern. Each possibility returns true if all the Constraints in the
 * combination are true.
 */
public class SubPattern extends PatternComponent
    implements
    Possibility {

    private static final long     serialVersionUID = 510l;

    private final Pattern         pattern;

    private Set<PatternComponent> items            = new HashSet<PatternComponent>();

    public SubPattern(Pattern pattern,
                      int orderNumber) {
        super( pattern );
        this.pattern = pattern;
        this.setOrderNumber( orderNumber );
    }

    @Override
    public String getPath() {
        return String.format( "%s/subPattern[%s]",
                              getPatternPath(),
                              getOrderNumber() );
    }

    public String getSourcePath() {
        return pattern.getSourcePath();
    }

    public VerifierComponentType getSourceType() {
        return pattern.getSourceType();
    }

    public String getName() {
        return pattern.getName();
    }

    public String getObjectTypePath() {
        return pattern.getObjectTypePath();
    }

    public boolean isPatternNot() {
        return pattern.isPatternNot();
    }

    public boolean isPatternExists() {
        return pattern.isPatternExists();
    }

    public boolean isPatternForall() {
        return pattern.isPatternForall();
    }

    public Pattern getPattern() {
        return pattern;
    }

    public Set<PatternComponent> getItems() {
        return items;
    }

    public int getAmountOfItems() {
        return items.size();
    }

    public void add(PatternComponent patternComponent) {
        items.add( patternComponent );
    }

    @Override
    public String toString() {
        return "SubPattern from rule: " + getRuleName() + ", amount of items:" + items.size();
    }

    public VerifierComponentType getVerifierComponentType() {
        return VerifierComponentType.SUB_PATTERN;
    }

}
