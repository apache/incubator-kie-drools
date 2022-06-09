package org.optaplanner.examples.pas.optional.solver.move.factory;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.optaplanner.core.impl.heuristic.selector.move.factory.MoveListFactory;
import org.optaplanner.examples.pas.domain.BedDesignation;
import org.optaplanner.examples.pas.domain.PatientAdmissionSchedule;
import org.optaplanner.examples.pas.optional.solver.move.BedDesignationSwapMove;

public class BedDesignationSwapMoveFactory implements MoveListFactory<PatientAdmissionSchedule> {

    @Override
    public List<BedDesignationSwapMove> createMoveList(PatientAdmissionSchedule patientAdmissionSchedule) {
        List<BedDesignation> bedDesignationList = patientAdmissionSchedule.getBedDesignationList();
        List<BedDesignationSwapMove> moveList = new ArrayList<>();
        for (ListIterator<BedDesignation> leftIt = bedDesignationList.listIterator(); leftIt.hasNext();) {
            BedDesignation leftBedDesignation = leftIt.next();
            for (ListIterator<BedDesignation> rightIt = bedDesignationList.listIterator(leftIt.nextIndex()); rightIt
                    .hasNext();) {
                BedDesignation rightBedDesignation = rightIt.next();
                if (leftBedDesignation.getAdmissionPart().calculateSameNightCount(rightBedDesignation.getAdmissionPart()) > 0) {
                    moveList.add(new BedDesignationSwapMove(leftBedDesignation, rightBedDesignation));
                }
            }
        }
        return moveList;
    }

}
