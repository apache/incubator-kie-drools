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

public class TextConsequence extends RuleComponent
    implements
    Consequence {

    private String text;

    public TextConsequence(VerifierRule rule) {
        super( rule );
    }

    @Override
    public String getPath() {
        return getRulePath() + "/consequence";
    }

    public ConsequenceType getConsequenceType() {
        return ConsequenceType.TEXT;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public VerifierComponentType getVerifierComponentType() {
        return VerifierComponentType.CONSEQUENCE;
    }

    public String toString() {
        return "TextConsequence: {\n" + text + "\n";
    }

}
