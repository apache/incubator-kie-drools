package org.optaplanner.examples.pas.score;

import java.util.function.BiFunction;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.examples.common.score.AbstractConstraintProviderTest;
import org.optaplanner.examples.common.score.ConstraintProviderTest;
import org.optaplanner.examples.pas.domain.AdmissionPart;
import org.optaplanner.examples.pas.domain.Bed;
import org.optaplanner.examples.pas.domain.BedDesignation;
import org.optaplanner.examples.pas.domain.Department;
import org.optaplanner.examples.pas.domain.DepartmentSpecialism;
import org.optaplanner.examples.pas.domain.Equipment;
import org.optaplanner.examples.pas.domain.Gender;
import org.optaplanner.examples.pas.domain.GenderLimitation;
import org.optaplanner.examples.pas.domain.Night;
import org.optaplanner.examples.pas.domain.Patient;
import org.optaplanner.examples.pas.domain.PatientAdmissionSchedule;
import org.optaplanner.examples.pas.domain.PreferredPatientEquipment;
import org.optaplanner.examples.pas.domain.RequiredPatientEquipment;
import org.optaplanner.examples.pas.domain.Room;
import org.optaplanner.examples.pas.domain.RoomEquipment;
import org.optaplanner.examples.pas.domain.RoomSpecialism;
import org.optaplanner.examples.pas.domain.Specialism;
import org.optaplanner.test.api.score.stream.ConstraintVerifier;

class PatientAdmissionScheduleConstraintProviderTest
        extends AbstractConstraintProviderTest<PatientAdmissionScheduleConstraintProvider, PatientAdmissionSchedule> {

    private static final Night ZERO_NIGHT = new Night(0);
    private static final Night FIVE_NIGHT = new Night(5);

    private static final Specialism DEFAULT_SPECIALISM = new Specialism();

    private final ConstraintVerifier<PatientAdmissionScheduleConstraintProvider, PatientAdmissionSchedule> constraintVerifier =
            ConstraintVerifier
                    .build(new PatientAdmissionScheduleConstraintProvider(), PatientAdmissionSchedule.class,
                            BedDesignation.class);

    private static Stream<Arguments> genderLimitationsProvider() {
        return Stream.of(
                Arguments.of(Gender.FEMALE, GenderLimitation.MALE_ONLY,
                        (BiFunction<PatientAdmissionScheduleConstraintProvider, ConstraintFactory, Constraint>) PatientAdmissionScheduleConstraintProvider::femaleInMaleRoomConstraint),
                Arguments.of(Gender.MALE, GenderLimitation.FEMALE_ONLY,
                        (BiFunction<PatientAdmissionScheduleConstraintProvider, ConstraintFactory, Constraint>) PatientAdmissionScheduleConstraintProvider::maleInFemaleRoomConstraint));
    }

    private static Stream<Arguments> departmentAgeLimitationProvider() {
        Department adultDepartment = new Department(1L, "Adult department");
        adultDepartment.setMinimumAge(18);

        Department underageDepartment = new Department(2L, "Underage department");
        underageDepartment.setMaximumAge(18);

        return Stream.of(
                Arguments.of(adultDepartment, 5,
                        (BiFunction<PatientAdmissionScheduleConstraintProvider, ConstraintFactory, Constraint>) PatientAdmissionScheduleConstraintProvider::departmentMinimumAgeConstraint),
                Arguments.of(underageDepartment, 42,
                        (BiFunction<PatientAdmissionScheduleConstraintProvider, ConstraintFactory, Constraint>) PatientAdmissionScheduleConstraintProvider::departmentMaximumAgeConstraint));
    }

    // Not using @ConstraintProviderTest as it does not mix with custom parameters.
    @ParameterizedTest(name = "gender = {0}, limitation = {1}")
    @MethodSource("genderLimitationsProvider")
    void genderRoomLimitationConstraintTest(Gender gender, GenderLimitation genderLimitation,
            BiFunction<PatientAdmissionScheduleConstraintProvider, ConstraintFactory, Constraint> constraintFunction) {

        Room room = new Room();
        room.setGenderLimitation(genderLimitation);

        Bed bed = new Bed();
        bed.setRoom(room);

        Patient patient = new Patient();
        patient.setGender(gender);

        AdmissionPart genderAdmission = new AdmissionPart(0L, patient, ZERO_NIGHT, FIVE_NIGHT, DEFAULT_SPECIALISM);
        BedDesignation genderLimitationDesignation = new BedDesignation(0L, genderAdmission, bed);

        constraintVerifier.verifyThat(constraintFunction)
                .given(genderLimitationDesignation)
                .penalizesBy(6);
    }

    @ConstraintProviderTest
    void sameBedInSameNightConstraintTest(
            ConstraintVerifier<PatientAdmissionScheduleConstraintProvider, PatientAdmissionSchedule> constraintVerifier) {

        Patient patient = new Patient();
        Bed bed = new Bed();

        AdmissionPart admissionPart = new AdmissionPart(0L, patient, ZERO_NIGHT, FIVE_NIGHT, DEFAULT_SPECIALISM);
        BedDesignation designation = new BedDesignation(1L, admissionPart, bed);

        BedDesignation sameBedAndNightsDesignation = new BedDesignation(2L, admissionPart, bed);

        constraintVerifier.verifyThat(PatientAdmissionScheduleConstraintProvider::sameBedInSameNightConstraint)
                .given(designation, sameBedAndNightsDesignation)
                .penalizesBy(6);
    }

    // Not using @ConstraintProviderTest as it does not mix with custom parameters.
    @ParameterizedTest(name = "department = {0}, patientAge = {1}")
    @MethodSource("departmentAgeLimitationProvider")
    void departmentAgeLimitationConstraintTest(Department department, int patientAge,
            BiFunction<PatientAdmissionScheduleConstraintProvider, ConstraintFactory, Constraint> constraintFunction) {

        Room room = new Room();
        room.setDepartment(department);

        Patient patient = new Patient();
        patient.setAge(patientAge);

        Bed bed = new Bed();
        bed.setRoom(room);

        AdmissionPart admission = new AdmissionPart(0L, patient, ZERO_NIGHT, FIVE_NIGHT, DEFAULT_SPECIALISM);
        BedDesignation designation = new BedDesignation(0L, admission, bed);

        constraintVerifier.verifyThat(constraintFunction)
                .given(designation, department)
                .penalizesBy(6);
    }

    @ConstraintProviderTest
    void requiredPatientEquipmentConstraintTest(
            ConstraintVerifier<PatientAdmissionScheduleConstraintProvider, PatientAdmissionSchedule> constraintVerifier) {

        Patient patient = new Patient();
        Room room = new Room();

        Equipment equipment1 = new Equipment();
        Equipment equipment2 = new Equipment();

        Bed bed = new Bed();
        bed.setRoom(room);

        AdmissionPart admission = new AdmissionPart(0L, patient, ZERO_NIGHT, FIVE_NIGHT, DEFAULT_SPECIALISM);
        BedDesignation designation = new BedDesignation(0L, admission, bed);

        //ReqPatientEq1
        RequiredPatientEquipment requiredPatientEquipment1 = new RequiredPatientEquipment();
        requiredPatientEquipment1.setPatient(patient);
        requiredPatientEquipment1.setEquipment(equipment1);
        //ReqPatientEq2
        RequiredPatientEquipment requiredPatientEquipment2 = new RequiredPatientEquipment();
        requiredPatientEquipment2.setPatient(patient);
        requiredPatientEquipment2.setEquipment(equipment2);
        //RoomEquipment
        RoomEquipment roomEquipment = new RoomEquipment();
        roomEquipment.setEquipment(equipment2);
        roomEquipment.setRoom(room);

        constraintVerifier.verifyThat(PatientAdmissionScheduleConstraintProvider::requiredPatientEquipmentConstraint)
                .given(requiredPatientEquipment1, requiredPatientEquipment2, roomEquipment, designation)
                .penalizesBy(6);
    }

    @ConstraintProviderTest
    void differentGenderInSameGenderRoomInSameNightConstraintTest(
            ConstraintVerifier<PatientAdmissionScheduleConstraintProvider, PatientAdmissionSchedule> constraintVerifier) {

        Room room = new Room();
        room.setGenderLimitation(GenderLimitation.SAME_GENDER);

        //Assign female
        Patient female = new Patient();
        female.setGender(Gender.FEMALE);

        Bed bed1 = new Bed();
        bed1.setRoom(room);

        AdmissionPart admissionPartFemale = new AdmissionPart(0L, female, ZERO_NIGHT, FIVE_NIGHT, DEFAULT_SPECIALISM);
        BedDesignation bedDesignationFemale = new BedDesignation(1, admissionPartFemale, bed1);

        //Assign male
        Patient male = new Patient();
        male.setGender(Gender.MALE);

        Bed bed2 = new Bed();
        bed2.setRoom(room);

        AdmissionPart admissionPartMale = new AdmissionPart(1L, male, ZERO_NIGHT, FIVE_NIGHT, DEFAULT_SPECIALISM);
        BedDesignation bedDesignationMale = new BedDesignation(2, admissionPartMale, bed2);

        constraintVerifier
                .verifyThat(PatientAdmissionScheduleConstraintProvider::differentGenderInSameGenderRoomInSameNightConstraint)
                .given(bedDesignationFemale, bedDesignationMale)
                .penalizesBy(6);
    }

    @ConstraintProviderTest
    void assignEveryPatientToABedConstraintTest(
            ConstraintVerifier<PatientAdmissionScheduleConstraintProvider, PatientAdmissionSchedule> constraintVerifier) {

        Patient patient = new Patient();

        AdmissionPart admissionPart = new AdmissionPart(0L, patient, ZERO_NIGHT, FIVE_NIGHT, DEFAULT_SPECIALISM);
        BedDesignation bedUnassignedDesignation = new BedDesignation(2, admissionPart, null);

        constraintVerifier
                .verifyThat(PatientAdmissionScheduleConstraintProvider::assignEveryPatientToABedConstraint)
                .given(bedUnassignedDesignation)
                .penalizesBy(6);
    }

    @ConstraintProviderTest
    void preferredMaximumRoomCapacityConstraintTest(
            ConstraintVerifier<PatientAdmissionScheduleConstraintProvider, PatientAdmissionSchedule> constraintVerifier) {

        Patient patientWithRoomPreferences = new Patient();
        patientWithRoomPreferences.setPreferredMaximumRoomCapacity(3);

        Room room = new Room();
        room.setCapacity(6);

        Bed assignedBedInExceedCapacity = new Bed();
        assignedBedInExceedCapacity.setRoom(room);

        AdmissionPart admissionPart =
                new AdmissionPart(0L, patientWithRoomPreferences, ZERO_NIGHT, FIVE_NIGHT, DEFAULT_SPECIALISM);
        BedDesignation bedDesignation = new BedDesignation(0L, admissionPart, assignedBedInExceedCapacity);

        constraintVerifier
                .verifyThat(PatientAdmissionScheduleConstraintProvider::preferredMaximumRoomCapacityConstraint)
                .given(bedDesignation)
                .penalizesBy(6);
    }

    @ConstraintProviderTest
    void preferredPatientEquipmentConstraintTest(
            ConstraintVerifier<PatientAdmissionScheduleConstraintProvider, PatientAdmissionSchedule> constraintVerifier) {

        Patient patient = new Patient();

        Room room = new Room();

        Bed bed = new Bed();
        bed.setRoom(room);

        AdmissionPart admissionPart = new AdmissionPart(0L, patient, ZERO_NIGHT, FIVE_NIGHT, DEFAULT_SPECIALISM);
        BedDesignation bedDesignation = new BedDesignation(0L, admissionPart, bed);

        Equipment equipment1 = new Equipment();
        Equipment equipment2 = new Equipment();

        PreferredPatientEquipment preferredPatientEquipment1 = new PreferredPatientEquipment();
        preferredPatientEquipment1.setEquipment(equipment1);
        preferredPatientEquipment1.setPatient(patient);

        PreferredPatientEquipment preferredPatientEquipment2 = new PreferredPatientEquipment();
        preferredPatientEquipment2.setEquipment(equipment2);
        preferredPatientEquipment2.setPatient(patient);

        RoomEquipment roomEquippedOnlyByOneEq = new RoomEquipment();
        roomEquippedOnlyByOneEq.setEquipment(equipment2);
        roomEquippedOnlyByOneEq.setRoom(room);

        constraintVerifier.verifyThat(PatientAdmissionScheduleConstraintProvider::preferredPatientEquipmentConstraint)
                .given(preferredPatientEquipment1, preferredPatientEquipment2, roomEquippedOnlyByOneEq, bedDesignation)
                .penalizesBy(6);
    }

    @ConstraintProviderTest
    void departmentSpecialismConstraintTest(
            ConstraintVerifier<PatientAdmissionScheduleConstraintProvider, PatientAdmissionSchedule> constraintVerifier) {

        Patient patient = new Patient();

        Department department = new Department();

        Room roomInDep = new Room();
        roomInDep.setDepartment(department);

        Bed bedInRoomInDep = new Bed();
        bedInRoomInDep.setRoom(roomInDep);

        //Designation with 1st spec
        Specialism spec1 = new Specialism();

        AdmissionPart admissionPartSpec1 = new AdmissionPart(0L, patient, ZERO_NIGHT, FIVE_NIGHT, spec1);
        BedDesignation designationWithDepartmentSpecialism1 = new BedDesignation(0L, admissionPartSpec1, bedInRoomInDep);

        //Designation with 2nd spec
        Specialism spec2 = new Specialism();

        AdmissionPart admissionPartSpec2 = new AdmissionPart(1L, patient, ZERO_NIGHT, FIVE_NIGHT, spec2);
        BedDesignation designationWithDepartmentSpecialism2 = new BedDesignation(1L, admissionPartSpec2, bedInRoomInDep);

        DepartmentSpecialism departmentSpecialismWithOneSpec = new DepartmentSpecialism();
        departmentSpecialismWithOneSpec.setDepartment(department);
        departmentSpecialismWithOneSpec.setSpecialism(spec1);

        constraintVerifier.verifyThat(PatientAdmissionScheduleConstraintProvider::departmentSpecialismConstraint)
                .given(designationWithDepartmentSpecialism1, designationWithDepartmentSpecialism2,
                        departmentSpecialismWithOneSpec)
                .penalizesBy(6);
    }

    @ConstraintProviderTest
    void roomSpecialismConstraintTest(
            ConstraintVerifier<PatientAdmissionScheduleConstraintProvider, PatientAdmissionSchedule> constraintVerifier) {

        Patient patient = new Patient();

        Room roomInDep = new Room();
        Bed bedInDep = new Bed();
        bedInDep.setRoom(roomInDep);

        //Designation with 1st spec
        Specialism spec1 = new Specialism();
        AdmissionPart admissionPart = new AdmissionPart(0L, patient, ZERO_NIGHT, FIVE_NIGHT, spec1);
        BedDesignation designationWithRoomSpecialism1 = new BedDesignation(0L, admissionPart, bedInDep);

        //Designation with 2nd spec
        Specialism spec2 = new Specialism();
        AdmissionPart admissionPart2 = new AdmissionPart(1L, patient, ZERO_NIGHT, FIVE_NIGHT, spec2);
        BedDesignation designationWithRoomSpecialism2 = new BedDesignation(1L, admissionPart2, bedInDep);

        RoomSpecialism roomSpecialism = new RoomSpecialism();
        roomSpecialism.setRoom(roomInDep);
        roomSpecialism.setSpecialism(spec1);

        constraintVerifier.verifyThat(PatientAdmissionScheduleConstraintProvider::roomSpecialismNotExistsConstraint)
                .given(designationWithRoomSpecialism1, designationWithRoomSpecialism2, roomSpecialism)
                .penalizesBy(6);
    }

    @ConstraintProviderTest
    void roomSpecialismNotFirstPriorityConstraintConstraintTest(
            ConstraintVerifier<PatientAdmissionScheduleConstraintProvider, PatientAdmissionSchedule> constraintVerifier) {

        Patient patient = new Patient();

        Room roomInDep = new Room();
        Bed bedInDep = new Bed();

        bedInDep.setRoom(roomInDep);
        //Designation with 1st spec
        Specialism spec1 = new Specialism();
        AdmissionPart admissionPart1 = new AdmissionPart(0L, patient, ZERO_NIGHT, FIVE_NIGHT, spec1);
        BedDesignation designationWithRoomSpecialism1 = new BedDesignation(0L, admissionPart1, bedInDep);

        //Designation with 2nd spec
        Specialism spec2 = new Specialism();
        AdmissionPart admissionPart2 = new AdmissionPart(1L, patient, ZERO_NIGHT, FIVE_NIGHT, spec2);
        BedDesignation designationWithRoomSpecialism2 = new BedDesignation(1L, admissionPart2, bedInDep);

        RoomSpecialism roomSpecialism = new RoomSpecialism();
        roomSpecialism.setRoom(roomInDep);
        roomSpecialism.setSpecialism(spec1);
        roomSpecialism.setPriority(2);

        constraintVerifier.verifyThat(PatientAdmissionScheduleConstraintProvider::roomSpecialismNotFirstPriorityConstraint)
                .given(designationWithRoomSpecialism1, designationWithRoomSpecialism2, roomSpecialism)
                .penalizesBy(6);
    }

    @Override
    protected ConstraintVerifier<PatientAdmissionScheduleConstraintProvider, PatientAdmissionSchedule>
            createConstraintVerifier() {
        return ConstraintVerifier.build(new PatientAdmissionScheduleConstraintProvider(), PatientAdmissionSchedule.class,
                BedDesignation.class);
    }
}
