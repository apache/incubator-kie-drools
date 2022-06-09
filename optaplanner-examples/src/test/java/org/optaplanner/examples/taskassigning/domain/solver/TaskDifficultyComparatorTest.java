package org.optaplanner.examples.taskassigning.domain.solver;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.optaplanner.examples.taskassigning.domain.Priority;
import org.optaplanner.examples.taskassigning.domain.Skill;
import org.optaplanner.examples.taskassigning.domain.Task;
import org.optaplanner.examples.taskassigning.domain.TaskType;

class TaskDifficultyComparatorTest {

    @Test
    void compareTasks() {
        Skill skill1 = new Skill(1, "skill1");
        Skill skill2 = new Skill(2, "skill2");
        TaskType typeCritical = new TaskType(1, "Critical", "", 1);
        TaskType typeMajorHard = new TaskType(2, "Major-Skills2", "", 1); // 2 skills
        TaskType typeMajorMedium = new TaskType(1, "Major-Skills1", "", 1); // 1 skill
        TaskType typeMajorEasySlow = new TaskType(0, "Major-Skills0-Slow", "", 999); // 0 skills, slow
        TaskType typeMajorEasyQuick = new TaskType(0, "Major-Skills0-Quick", "", 100); // 0 skills, quick
        TaskType typeMinor = new TaskType(0, "Minor", "", 200);
        typeMajorHard.setRequiredSkillList(List.of(skill1, skill2));
        typeMajorMedium.setRequiredSkillList(List.of(skill1));

        // Priority decides first.
        Task critical = new Task(0, typeCritical, 1, null, 0, Priority.CRITICAL);
        // Then the number of required skills decides.
        Task majorHard = new Task(10, typeMajorHard, 2, null, 0, Priority.MAJOR);
        Task majorMedium = new Task(11, typeMajorMedium, 3, null, 0, Priority.MAJOR);
        // Then the base duration decides.
        Task majorEasySlow = new Task(20, typeMajorEasySlow, 1, null, 0, Priority.MAJOR);
        Task majorEasyQuick = new Task(21, typeMajorEasyQuick, 2, null, 0, Priority.MAJOR);
        // ID decides last.
        Task minor300 = new Task(300, typeMinor, 3, null, 0, Priority.MINOR);
        Task minor200 = new Task(200, typeMinor, 2, null, 0, Priority.MINOR);
        Task minor100 = new Task(100, typeMinor, 1, null, 0, Priority.MINOR);

        List<Task> decreasingDifficultyList = List.of(
                critical,
                majorHard,
                majorMedium,
                majorEasySlow,
                majorEasyQuick,
                minor300,
                minor300,
                minor200,
                minor100);

        // Pseudo-random order, minor300 appears twice.
        List<Task> input = Arrays.asList(
                majorEasySlow,
                minor100,
                majorMedium,
                minor300,
                majorHard,
                majorEasyQuick,
                minor200,
                critical,
                minor300);

        input.sort(TaskDifficultyComparator.DECREASING_DIFFICULTY_COMPARATOR);
        assertThat(input).containsExactlyElementsOf(decreasingDifficultyList);
        Collections.reverse(input);
        assertThat(input).isSortedAccordingTo(TaskDifficultyComparator.INCREASING_DIFFICULTY_COMPARATOR);
    }
}
