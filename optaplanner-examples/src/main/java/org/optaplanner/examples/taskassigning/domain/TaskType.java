/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.examples.taskassigning.domain;

import java.util.ArrayList;
import java.util.List;

import org.optaplanner.examples.common.domain.AbstractPersistable;
import org.optaplanner.examples.common.swingui.components.Labeled;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("TaTaskType")
public class TaskType extends AbstractPersistable implements Labeled {

    private String code;
    private String title;
    private int baseDuration; // In minutes

    private List<Skill> requiredSkillList;

    public TaskType() {
    }

    public TaskType(long id, String code, String title, int baseDuration) {
        super(id);
        this.code = code;
        this.title = title;
        this.baseDuration = baseDuration;
        requiredSkillList = new ArrayList<>();
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getBaseDuration() {
        return baseDuration;
    }

    public void setBaseDuration(int baseDuration) {
        this.baseDuration = baseDuration;
    }

    public List<Skill> getRequiredSkillList() {
        return requiredSkillList;
    }

    public void setRequiredSkillList(List<Skill> requiredSkillList) {
        this.requiredSkillList = requiredSkillList;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    @Override
    public String getLabel() {
        return title;
    }

    @Override
    public String toString() {
        return code;
    }

}
