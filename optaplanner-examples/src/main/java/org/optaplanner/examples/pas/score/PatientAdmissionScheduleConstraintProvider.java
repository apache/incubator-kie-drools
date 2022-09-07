package org.optaplanner.examples.pas.score;

import static org.optaplanner.core.api.score.stream.Joiners.equal;
import static org.optaplanner.core.api.score.stream.Joiners.filtering;
import static org.optaplanner.core.api.score.stream.Joiners.greaterThan;
import static org.optaplanner.core.api.score.stream.Joiners.lessThan;

import java.util.function.Function;

import org.optaplanner.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.examples.pas.domain.BedDesignation;
import org.optaplanner.examples.pas.domain.Department;
import org.optaplanner.examples.pas.domain.DepartmentSpecialism;
import org.optaplanner.examples.pas.domain.Gender;
import org.optaplanner.examples.pas.domain.GenderLimitation;
import org.optaplanner.examples.pas.domain.PreferredPatientEquipment;
import org.optaplanner.examples.pas.domain.RequiredPatientEquipment;
import org.optaplanner.examples.pas.domain.RoomEquipment;
import org.optaplanner.examples.pas.domain.RoomSpecialism;

/*
 * This is constraints for Hospital Bed Planning
 * They are based on patientAdmissionScheduleConstraints.drl
 * Planning Entity: BedDesignation
 * Planning Variable: Bed(nullable) - would not be prefiltered on uninitialized solutions
 * Bed is nullable so in case you need to access it members check that planning value bed is not null
 */

public class PatientAdmissionScheduleConstraintProvider implements ConstraintProvider {

    @Override
    public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
        return new Constraint[] {
                sameBedInSameNightConstraint(constraintFactory),
                femaleInMaleRoomConstraint(constraintFactory),
                maleInFemaleRoomConstraint(constraintFactory),
                differentGenderInSameGenderRoomInSameNightConstraint(constraintFactory),
                departmentMinimumAgeConstraint(constraintFactory),
                departmentMaximumAgeConstraint(constraintFactory),
                requiredPatientEquipmentConstraint(constraintFactory),
                assignEveryPatientToABedConstraint(constraintFactory),
                preferredMaximumRoomCapacityConstraint(constraintFactory),
                departmentSpecialismConstraint(constraintFactory),
                roomSpecialismNotExistsConstraint(constraintFactory),
                roomSpecialismNotFirstPriorityConstraint(constraintFactory),
                preferredPatientEquipmentConstraint(constraintFactory)
        };
    }

    public Constraint sameBedInSameNightConstraint(ConstraintFactory constraintFactory) {
        return constraintFactory.forEachUniquePair(BedDesignation.class,
                equal(BedDesignation::getBed))
                .filter((left, right) -> left.getAdmissionPart().calculateSameNightCount(right.getAdmissionPart()) > 0)
                .penalize(HardMediumSoftScore.ofHard(1000),
                        (left, right) -> left.getAdmissionPart().calculateSameNightCount(right.getAdmissionPart()))
                .asConstraint("sameBedInSameNight");
    }

    public Constraint femaleInMaleRoomConstraint(ConstraintFactory constraintFactory) {
        return constraintFactory.forEachIncludingNullVars(BedDesignation.class)
                .filter(bd -> bd.getPatientGender() == Gender.FEMALE
                        && bd.getRoomGenderLimitation() == GenderLimitation.MALE_ONLY)
                .penalize(HardMediumSoftScore.ofHard(50), BedDesignation::getAdmissionPartNightCount)
                .asConstraint("femaleInMaleRoom");
    }

    public Constraint maleInFemaleRoomConstraint(ConstraintFactory constraintFactory) {
        return constraintFactory.forEachIncludingNullVars(BedDesignation.class)
                .filter(bd -> bd.getPatientGender() == Gender.MALE
                        && bd.getRoomGenderLimitation() == GenderLimitation.FEMALE_ONLY)
                .penalize(HardMediumSoftScore.ofHard(50), BedDesignation::getAdmissionPartNightCount)
                .asConstraint("maleInFemaleRoom");
    }

    public Constraint differentGenderInSameGenderRoomInSameNightConstraint(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(BedDesignation.class)
                .filter(bd -> bd.getRoomGenderLimitation() == GenderLimitation.SAME_GENDER)
                .join(constraintFactory.forEach(BedDesignation.class)
                        .filter(bd -> bd.getRoomGenderLimitation() == GenderLimitation.SAME_GENDER),
                        equal(BedDesignation::getRoom),
                        lessThan(BedDesignation::getId),
                        filtering((left, right) -> left.getPatient().getGender() != right.getPatient().getGender()
                                && left.getAdmissionPart().calculateSameNightCount(right.getAdmissionPart()) > 0))
                .penalize(HardMediumSoftScore.ofHard(1000),
                        (left, right) -> left.getAdmissionPart().calculateSameNightCount(right.getAdmissionPart()))
                .asConstraint("differentGenderInSameGenderRoomInSameNight");
    }

    public Constraint departmentMinimumAgeConstraint(ConstraintFactory constraintFactory) {
        return constraintFactory.forEachIncludingNullVars(Department.class)
                .filter(d -> d.getMinimumAge() != null)
                .join(constraintFactory.forEachIncludingNullVars(BedDesignation.class),
                        equal(Function.identity(), BedDesignation::getDepartment),
                        greaterThan(Department::getMinimumAge, BedDesignation::getPatientAge))
                .penalize(HardMediumSoftScore.ofHard(100),
                        (d, bd) -> bd.getAdmissionPartNightCount())
                .asConstraint("departmentMinimumAge");
    }

    public Constraint departmentMaximumAgeConstraint(ConstraintFactory constraintFactory) {
        return constraintFactory.forEachIncludingNullVars(Department.class)
                .filter(d -> d.getMaximumAge() != null)
                .join(constraintFactory.forEachIncludingNullVars(BedDesignation.class),
                        equal(Function.identity(), BedDesignation::getDepartment),
                        lessThan(Department::getMaximumAge, BedDesignation::getPatientAge))
                .penalize(HardMediumSoftScore.ofHard(100),
                        (d, bd) -> bd.getAdmissionPartNightCount())
                .asConstraint("departmentMaximumAge");
    }

    public Constraint requiredPatientEquipmentConstraint(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(RequiredPatientEquipment.class)
                .join(BedDesignation.class,
                        equal(RequiredPatientEquipment::getPatient, BedDesignation::getPatient))
                .ifNotExists(RoomEquipment.class,
                        equal((rpe, bd) -> bd.getRoom(), RoomEquipment::getRoom),
                        equal((rpe, bd) -> rpe.getEquipment(), RoomEquipment::getEquipment))
                .penalize(HardMediumSoftScore.ofHard(50),
                        (rpe, bd) -> bd.getAdmissionPartNightCount())
                .asConstraint("requiredPatientEquipment");
    }

    //Medium
    public Constraint assignEveryPatientToABedConstraint(ConstraintFactory constraintFactory) {
        return constraintFactory.forEachIncludingNullVars(BedDesignation.class)
                .filter(bd -> bd.getBed() == null)
                .penalize(HardMediumSoftScore.ONE_MEDIUM, BedDesignation::getAdmissionPartNightCount)
                .asConstraint("assignEveryPatientToABed");
    }

    //Soft
    public Constraint preferredMaximumRoomCapacityConstraint(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(BedDesignation.class)
                .filter(bd -> bd.getPatient().getPreferredMaximumRoomCapacity() != null
                        && bd.getPatient().getPreferredMaximumRoomCapacity() < bd.getRoom().getCapacity())
                .penalize(HardMediumSoftScore.ofSoft(8), BedDesignation::getAdmissionPartNightCount)
                .asConstraint("preferredMaximumRoomCapacity");
    }

    public Constraint departmentSpecialismConstraint(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(BedDesignation.class)
                .ifNotExists(DepartmentSpecialism.class,
                        equal(BedDesignation::getDepartment, DepartmentSpecialism::getDepartment),
                        equal(BedDesignation::getAdmissionPartSpecialism, DepartmentSpecialism::getSpecialism))
                .penalize(HardMediumSoftScore.ofSoft(10), BedDesignation::getAdmissionPartNightCount)
                .asConstraint("departmentSpecialism");
    }

    public Constraint roomSpecialismNotExistsConstraint(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(BedDesignation.class)
                .filter(bd -> bd.getAdmissionPartSpecialism() != null)
                .ifNotExists(RoomSpecialism.class,
                        equal(BedDesignation::getRoom, RoomSpecialism::getRoom),
                        equal(BedDesignation::getAdmissionPartSpecialism, RoomSpecialism::getSpecialism))
                .penalize(HardMediumSoftScore.ofSoft(20), BedDesignation::getAdmissionPartNightCount)
                .asConstraint("roomSpecialismNotExists");
    }

    public Constraint roomSpecialismNotFirstPriorityConstraint(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(BedDesignation.class)
                .filter(bd -> bd.getAdmissionPartSpecialism() != null)
                .join(constraintFactory.forEach(RoomSpecialism.class)
                        .filter(rs -> rs.getPriority() > 1),
                        equal(BedDesignation::getRoom, RoomSpecialism::getRoom),
                        equal(BedDesignation::getAdmissionPartSpecialism, RoomSpecialism::getSpecialism))
                .penalize(HardMediumSoftScore.ofSoft(10),
                        (bd, rs) -> (rs.getPriority() - 1) * bd.getAdmissionPartNightCount())
                .asConstraint("roomSpecialismNotFirstPriority");
    }

    public Constraint preferredPatientEquipmentConstraint(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(PreferredPatientEquipment.class)
                .join(BedDesignation.class,
                        equal(PreferredPatientEquipment::getPatient, BedDesignation::getPatient))
                .ifNotExists(RoomEquipment.class,
                        equal((re, bd) -> bd.getRoom(), RoomEquipment::getRoom),
                        equal((re, bd) -> re.getEquipment(), RoomEquipment::getEquipment))
                .penalize(HardMediumSoftScore.ofSoft(20),
                        (re, bd) -> bd.getAdmissionPartNightCount())
                .asConstraint("preferredPatientEquipment");
    }
}
