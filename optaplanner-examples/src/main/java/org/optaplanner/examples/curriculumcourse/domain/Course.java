/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.examples.curriculumcourse.domain;

import static java.util.Objects.requireNonNull;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.optaplanner.examples.common.domain.AbstractPersistable;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("Course")
public class Course extends AbstractPersistable {

    private String code;

    private Teacher teacher;
    private int lectureSize;
    private int minWorkingDaySize;

    private Set<Curriculum> curriculumSet;
    private int studentSize;

    public Course() {
    }

    public Course(int id, String code, Teacher teacher, int lectureSize, int studentSize, int minWorkingDaySize,
            Curriculum... curricula) {
        super(id);
        this.code = requireNonNull(code);
        this.teacher = requireNonNull(teacher);
        this.lectureSize = lectureSize;
        this.minWorkingDaySize = minWorkingDaySize;
        this.curriculumSet = Arrays.stream(curricula).collect(Collectors.toCollection(LinkedHashSet::new));
        this.studentSize = studentSize;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    public int getLectureSize() {
        return lectureSize;
    }

    public void setLectureSize(int lectureSize) {
        this.lectureSize = lectureSize;
    }

    public int getMinWorkingDaySize() {
        return minWorkingDaySize;
    }

    public void setMinWorkingDaySize(int minWorkingDaySize) {
        this.minWorkingDaySize = minWorkingDaySize;
    }

    public Set<Curriculum> getCurriculumSet() {
        return curriculumSet;
    }

    public void setCurriculumSet(Set<Curriculum> curriculumSet) {
        this.curriculumSet = curriculumSet;
    }

    public int getStudentSize() {
        return studentSize;
    }

    public void setStudentSize(int studentSize) {
        this.studentSize = studentSize;
    }

    @Override
    public String toString() {
        return code;
    }

}
