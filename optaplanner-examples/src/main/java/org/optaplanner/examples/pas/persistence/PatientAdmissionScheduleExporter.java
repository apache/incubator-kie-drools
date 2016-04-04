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

package org.optaplanner.examples.pas.persistence;

import java.io.IOException;
import java.util.Collections;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.optaplanner.examples.common.persistence.AbstractTxtSolutionExporter;
import org.optaplanner.examples.pas.domain.BedDesignation;
import org.optaplanner.examples.pas.domain.Patient;
import org.optaplanner.examples.pas.domain.PatientAdmissionSchedule;

public class PatientAdmissionScheduleExporter extends AbstractTxtSolutionExporter<PatientAdmissionSchedule> {

    public static void main(String[] args) {
        new PatientAdmissionScheduleExporter().convertAll();
    }

    public PatientAdmissionScheduleExporter() {
        super(new PatientAdmissionScheduleDao());
    }

    @Override
    public TxtOutputBuilder<PatientAdmissionSchedule> createTxtOutputBuilder() {
        return new PatientAdmissionScheduleOutputBuilder();
    }

    public static class PatientAdmissionScheduleOutputBuilder extends TxtOutputBuilder<PatientAdmissionSchedule> {

        @Override
        public void writeSolution() throws IOException {
            Collections.sort(solution.getBedDesignationList(), (a, b) -> new CompareToBuilder()
                    .append(a.getAdmissionPart(), b.getAdmissionPart())
                    .append(a.getBed(), b.getBed())
                    .append(a.getId(), b.getId())
                    .toComparison());
            for (Patient patient : solution.getPatientList()) {
                bufferedWriter.write(Long.toString(patient.getId()));
                for (BedDesignation bedDesignation : solution.getBedDesignationList()) {
                    if (bedDesignation.getPatient().equals(patient)) {
                        for (int i = 0; i < bedDesignation.getAdmissionPart().getNightCount(); i++) {
                            bufferedWriter.write(" " + Long.toString(bedDesignation.getBed().getId()));
                        }
                    }
                }
                bufferedWriter.write("\n");
            }
        }
    }

}
