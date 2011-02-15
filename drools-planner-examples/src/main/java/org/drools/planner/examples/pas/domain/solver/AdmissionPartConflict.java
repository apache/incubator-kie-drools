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

package org.drools.planner.examples.pas.domain.solver;

import java.io.Serializable;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.drools.planner.examples.pas.domain.AdmissionPart;

/**
 * Calculated during initialization, not modified during score calculation.
 */
public class AdmissionPartConflict implements Serializable {

    private AdmissionPart leftAdmissionPart;
    private AdmissionPart rightAdmissionPart;
    private int nightSize;

    public AdmissionPartConflict(AdmissionPart leftAdmissionPart, AdmissionPart rightAdmissionPart, int nightSize) {
        this.leftAdmissionPart = leftAdmissionPart;
        this.rightAdmissionPart = rightAdmissionPart;
        this.nightSize = nightSize;
    }

    public AdmissionPart getLeftAdmissionPart() {
        return leftAdmissionPart;
    }

    public void setLeftAdmissionPart(AdmissionPart leftAdmissionPart) {
        this.leftAdmissionPart = leftAdmissionPart;
    }

    public AdmissionPart getRightAdmissionPart() {
        return rightAdmissionPart;
    }

    public void setRightAdmissionPart(AdmissionPart rightAdmissionPart) {
        this.rightAdmissionPart = rightAdmissionPart;
    }

    public int getNightSize() {
        return nightSize;
    }

    public void setNightSize(int nightSize) {
        this.nightSize = nightSize;
    }

    public int compareTo(AdmissionPartConflict other) {
        return new CompareToBuilder()
                .append(leftAdmissionPart, other.leftAdmissionPart)
                .append(rightAdmissionPart, other.rightAdmissionPart)
                .toComparison();
    }

    @Override
    public String toString() {
        return leftAdmissionPart + " & " + rightAdmissionPart + " = " + nightSize;
    }

    public boolean isDifferentGender() {
        return leftAdmissionPart.getPatient().getGender() != rightAdmissionPart.getPatient().getGender();
    }

}
