package org.optaplanner.examples.pas.domain.solver;

import static java.util.Comparator.comparing;
import static java.util.Comparator.comparingInt;

import java.util.Comparator;

import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionSorterWeightFactory;
import org.optaplanner.examples.pas.domain.BedDesignation;
import org.optaplanner.examples.pas.domain.PatientAdmissionSchedule;
import org.optaplanner.examples.pas.domain.Room;

public class BedDesignationDifficultyWeightFactory
        implements SelectionSorterWeightFactory<PatientAdmissionSchedule, BedDesignation> {

    @Override
    public BedDesignationDifficultyWeight createSorterWeight(PatientAdmissionSchedule schedule, BedDesignation bedDesignation) {
        int hardDisallowedCount = 0;
        int softDisallowedCount = 0;
        for (Room room : schedule.getRoomList()) {
            hardDisallowedCount += (room.countHardDisallowedAdmissionPart(bedDesignation.getAdmissionPart())
                    * room.getCapacity());
            softDisallowedCount += (room.countSoftDisallowedAdmissionPart(bedDesignation.getAdmissionPart())
                    * room.getCapacity());
        }
        return new BedDesignationDifficultyWeight(bedDesignation, hardDisallowedCount, softDisallowedCount);
    }

    public static class BedDesignationDifficultyWeight implements Comparable<BedDesignationDifficultyWeight> {

        private static final Comparator<BedDesignationDifficultyWeight> COMPARATOR = comparingInt(
                (BedDesignationDifficultyWeight weight) -> weight.requiredEquipmentCount * weight.nightCount)
                .thenComparingInt(weight -> weight.hardDisallowedCount * weight.nightCount)
                .thenComparingInt(weight -> weight.nightCount)
                .thenComparingInt(weight -> weight.softDisallowedCount * weight.nightCount)
                // Descending (earlier nights are more difficult) // TODO probably because less occupancy
                .thenComparingInt(weight -> -weight.bedDesignation.getAdmissionPart().getFirstNight().getIndex())
                .thenComparing(weight -> weight.bedDesignation, comparing(BedDesignation::getId));
        private final BedDesignation bedDesignation;
        private int requiredEquipmentCount;
        private int nightCount;
        private int hardDisallowedCount;
        private int softDisallowedCount;

        public BedDesignationDifficultyWeight(BedDesignation bedDesignation,
                int hardDisallowedCount, int softDisallowedCount) {
            this.bedDesignation = bedDesignation;
            this.requiredEquipmentCount = bedDesignation.getPatient().getRequiredPatientEquipmentList().size();
            this.nightCount = bedDesignation.getAdmissionPart().getNightCount();
            this.hardDisallowedCount = hardDisallowedCount;
            this.softDisallowedCount = softDisallowedCount;
        }

        @Override
        public int compareTo(BedDesignationDifficultyWeight other) {
            return COMPARATOR.compare(this, other);
        }
    }
}
