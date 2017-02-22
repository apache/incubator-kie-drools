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

package org.optaplanner.examples.examination.domain;

import java.util.List;
import java.util.Set;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.optaplanner.examples.common.domain.AbstractPersistable;

@XStreamAlias("Topic")
public class Topic extends AbstractPersistable {

    private int duration; // in minutes
    private List<Student> studentList;

    // Calculated during initialization, not modified during score calculation.
    private boolean frontLoadLarge;
    private Set<Topic> coincidenceTopicSet = null;

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public List<Student> getStudentList() {
        return studentList;
    }

    public void setStudentList(List<Student> studentList) {
        this.studentList = studentList;
    }

    public int getStudentSize() {
        return studentList.size();
    }

    public boolean isFrontLoadLarge() {
        return frontLoadLarge;
    }

    public void setFrontLoadLarge(boolean frontLoadLarge) {
        this.frontLoadLarge = frontLoadLarge;
    }

    public Set<Topic> getCoincidenceTopicSet() {
        return coincidenceTopicSet;
    }

    public void setCoincidenceTopicSet(Set<Topic> coincidenceTopicSet) {
        this.coincidenceTopicSet = coincidenceTopicSet;
    }

    public boolean hasCoincidenceTopic() {
        return coincidenceTopicSet != null;
    }

    @Override
    public String toString() {
        return Long.toString(id);
    }

}
