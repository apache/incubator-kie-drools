package org.optaplanner.examples.pas.domain.solver;

import static java.util.Comparator.comparing;
import static java.util.Comparator.comparingInt;
import static java.util.Comparator.nullsFirst;

import java.util.Collections;
import java.util.Comparator;

import org.optaplanner.examples.pas.domain.Bed;
import org.optaplanner.examples.pas.domain.Department;
import org.optaplanner.examples.pas.domain.Room;

public class BedStrengthComparator implements Comparator<Bed> {

    private static final Comparator<Integer> NULLSAFE_INTEGER_COMPARATOR = nullsFirst(Integer::compareTo);
    private static final Comparator<Department> DEPARTMENT_COMPARATOR = comparing(
            (Department department) -> department.getMinimumAge() == null) // null minimumAge is stronger
            .thenComparing(department -> department.getMaximumAge() == null) // null maximumAge is stronger
            .thenComparing(Department::getMinimumAge, Collections.reverseOrder(NULLSAFE_INTEGER_COMPARATOR)) // Descending, low minimumAge is stronger
            .thenComparing(Department::getMaximumAge, NULLSAFE_INTEGER_COMPARATOR); // High maximumAge is stronger
    private static final Comparator<Room> ROOM_COMPARATOR = comparingInt((Room room) -> room.getRoomEquipmentList().size())
            .thenComparingInt(room -> room.getRoomSpecialismList().size())
            .thenComparingInt(room -> -room.getCapacity()); // Descending (smaller rooms are stronger)
    private static final Comparator<Bed> COMPARATOR = comparing((Bed bed) -> bed.getRoom().getDepartment(),
            DEPARTMENT_COMPARATOR)
            .thenComparing(Bed::getRoom, ROOM_COMPARATOR)
            .thenComparingLong(Bed::getId);

    @Override
    public int compare(Bed a, Bed b) {

        if (a == null) {
            if (b == null) {
                return 0;
            }
            return -1;
        } else if (b == null) {
            return 1;
        }
        return COMPARATOR.compare(a, b);
    }
}
