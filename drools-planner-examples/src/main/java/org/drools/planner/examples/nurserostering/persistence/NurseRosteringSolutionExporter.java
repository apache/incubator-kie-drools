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

package org.drools.planner.examples.nurserostering.persistence;

import java.io.IOException;

import org.drools.planner.core.solution.Solution;
import org.drools.planner.examples.common.persistence.AbstractXmlSolutionExporter;
import org.drools.planner.examples.nurserostering.domain.Assignment;
import org.drools.planner.examples.nurserostering.domain.NurseRoster;
import org.drools.planner.examples.nurserostering.domain.Shift;
import org.jdom.Element;

public class NurseRosteringSolutionExporter extends AbstractXmlSolutionExporter {

    public static void main(String[] args) {
        new NurseRosteringSolutionExporter().convertAll();
    }

    public NurseRosteringSolutionExporter() {
        super(new NurseRosteringDaoImpl());
    }

    public XmlOutputBuilder createXmlOutputBuilder() {
        return new NurseRosteringOutputBuilder();
    }

    public class NurseRosteringOutputBuilder extends XmlOutputBuilder {

        private NurseRoster nurseRoster;

        public void setSolution(Solution solution) {
            nurseRoster = (NurseRoster) solution;
        }

        public void writeSolution() throws IOException {
            Element solutionElement = new Element("Solution");
            document.setRootElement(solutionElement);

            Element schedulingPeriodIDElement = new Element("SchedulingPeriodID");
            schedulingPeriodIDElement.setText(nurseRoster.getCode());
            solutionElement.addContent(schedulingPeriodIDElement);

            Element competitorElement = new Element("Competitor");
            competitorElement.setText("Geoffrey De Smet with Drools Planner");
            solutionElement.addContent(competitorElement);

            Element softConstraintsPenaltyElement = new Element("SoftConstraintsPenalty");
            softConstraintsPenaltyElement.setText(Integer.toString(nurseRoster.getScore().getSoftScore()));
            solutionElement.addContent(softConstraintsPenaltyElement);

            for (Assignment assignment : nurseRoster.getAssignmentList()) {
                Shift shift = assignment.getShift();
                if (shift != null) {
                    Element assignmentElement = new Element("Assignment");
                    solutionElement.addContent(assignmentElement);

                    Element dateElement = new Element("Date");
                    dateElement.setText(shift.getShiftDate().getDateString());
                    assignmentElement.addContent(dateElement);

                    Element employeeElement = new Element("Employee");
                    employeeElement.setText(assignment.getEmployee().getCode());
                    assignmentElement.addContent(employeeElement);

                    Element shiftTypeElement = new Element("ShiftType");
                    shiftTypeElement.setText(shift.getShiftType().getCode());
                    assignmentElement.addContent(shiftTypeElement);
                }
            }
        }
    }

}
