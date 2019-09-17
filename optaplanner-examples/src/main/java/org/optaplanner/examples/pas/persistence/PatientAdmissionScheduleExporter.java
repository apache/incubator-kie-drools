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
import java.util.Comparator;

import org.optaplanner.examples.common.persistence.AbstractTxtSolutionExporter;
import org.optaplanner.examples.common.persistence.SolutionConverter;
import org.optaplanner.examples.pas.app.PatientAdmissionScheduleApp;
import org.optaplanner.examples.pas.domain.AdmissionPart;
import org.optaplanner.examples.pas.domain.Bed;
import org.optaplanner.examples.pas.domain.BedDesignation;
import org.optaplanner.examples.pas.domain.Patient;
import org.optaplanner.examples.pas.domain.PatientAdmissionSchedule;

import static java.util.Comparator.comparing;
import static java.util.Comparator.comparingLong;

public class PatientAdmissionScheduleExporter extends AbstractTxtSolutionExporter<PatientAdmissionSchedule> {

    public static void main(String[] args) {
        SolutionConverter<PatientAdmissionSchedule> converter = SolutionConverter.createExportConverter(
                PatientAdmissionScheduleApp.DATA_DIR_NAME, PatientAdmissionSchedule.class, new PatientAdmissionScheduleExporter());
        converter.convertAll();
    }

    @Override
    public TxtOutputBuilder<PatientAdmissionSchedule> createTxtOutputBuilder() {
        return new PatientAdmissionScheduleOutputBuilder();
    }

    public static class PatientAdmissionScheduleOutputBuilder extends TxtOutputBuilder<PatientAdmissionSchedule> {

        private static final Comparator<BedDesignation> COMPARATOR =
                comparing(BedDesignation::getAdmissionPart, comparingLong(AdmissionPart::getId))
                    .thenComparing(BedDesignation::getBed, comparingLong(Bed::getId))
                    .thenComparingLong(BedDesignation::getId);

        @Override
        public void writeSolution() throws IOException {
            Collections.sort(solution.getBedDesignationList(), COMPARATOR);
            for (Patient patient : solution.getPatientList()) {
                bufferedWriter.write(Long.toString(patient.getId()));
                for (BedDesignation bedDesignation : solution.getBedDesignationList()) {
                    if (bedDesignation.getPatient().equals(patient)) {
                        for (int i = 0; i < bedDesignation.getAdmissionPart().getNightCount(); i++) {
                            bufferedWriter.write(" " + bedDesignation.getBed().getId());
                        }
                    }
                }
                bufferedWriter.write("\n");
            }
        }
    }

}
