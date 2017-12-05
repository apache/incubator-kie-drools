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

package org.optaplanner.examples.nurserostering.persistence;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

import org.jdom.Element;
import org.optaplanner.examples.common.persistence.AbstractXmlSolutionExporter;
import org.optaplanner.examples.common.persistence.SolutionConverter;
import org.optaplanner.examples.nurserostering.app.NurseRosteringApp;
import org.optaplanner.examples.nurserostering.domain.NurseRoster;
import org.optaplanner.examples.nurserostering.domain.Shift;
import org.optaplanner.examples.nurserostering.domain.ShiftAssignment;

public class NurseRosteringExporter extends AbstractXmlSolutionExporter<NurseRoster> {

    public static void main(String[] args) {
        SolutionConverter<NurseRoster> converter = SolutionConverter.createExportConverter(
                NurseRosteringApp.DATA_DIR_NAME, NurseRoster.class, new NurseRosteringExporter());
        converter.convertAll();
    }

    @Override
    public XmlOutputBuilder<NurseRoster> createXmlOutputBuilder() {
        return new NurseRosteringOutputBuilder();
    }

    public static class NurseRosteringOutputBuilder extends XmlOutputBuilder<NurseRoster> {

        private NurseRoster nurseRoster;

        @Override
        public void setSolution(NurseRoster solution) {
            nurseRoster = solution;
        }

        @Override
        public void writeSolution() throws IOException {
            Element solutionElement = new Element("Solution");
            document.setRootElement(solutionElement);

            Element schedulingPeriodIDElement = new Element("SchedulingPeriodID");
            schedulingPeriodIDElement.setText(nurseRoster.getCode());
            solutionElement.addContent(schedulingPeriodIDElement);

            Element competitorElement = new Element("Competitor");
            competitorElement.setText("Geoffrey De Smet with OptaPlanner");
            solutionElement.addContent(competitorElement);

            Element softConstraintsPenaltyElement = new Element("SoftConstraintsPenalty");
            softConstraintsPenaltyElement.setText(Integer.toString(nurseRoster.getScore().getSoftScore()));
            solutionElement.addContent(softConstraintsPenaltyElement);

            for (ShiftAssignment shiftAssignment : nurseRoster.getShiftAssignmentList()) {
                Shift shift = shiftAssignment.getShift();
                if (shift != null) {
                    Element assignmentElement = new Element("Assignment");
                    solutionElement.addContent(assignmentElement);

                    Element dateElement = new Element("Date");
                    dateElement.setText(shift.getShiftDate().getDate().format(DateTimeFormatter.ISO_DATE));
                    assignmentElement.addContent(dateElement);

                    Element employeeElement = new Element("Employee");
                    employeeElement.setText(shiftAssignment.getEmployee().getCode());
                    assignmentElement.addContent(employeeElement);

                    Element shiftTypeElement = new Element("ShiftType");
                    shiftTypeElement.setText(shift.getShiftType().getCode());
                    assignmentElement.addContent(shiftTypeElement);
                }
            }
        }
    }

}
