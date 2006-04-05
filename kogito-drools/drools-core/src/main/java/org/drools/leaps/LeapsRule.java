package org.drools.leaps;

/*
 * Copyright 2006 Alexander Bagerman
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

import java.util.ArrayList;

import org.drools.common.ActivationQueue;
import org.drools.common.AgendaGroupImpl;
import org.drools.rule.EvalCondition;
import org.drools.rule.Rule;

/**
 * Wrapper class to drools generic rule to extract matching elements from it to
 * use during leaps iterations.
 * 
 * @author Alexander Bagerman
 * 
 */
class LeapsRule {
    Rule rule;

    final ColumnConstraints[] columnConstraints;

    final ColumnConstraints[] notColumnConstraints;

    final ColumnConstraints[] existsColumnConstraints;

    final EvalCondition[] evalConditions;

    boolean notColumnsPresent;

    boolean existsColumnsPresent;

    boolean evalCoditionsPresent;

    final Class[] existsNotsClasses;

    public LeapsRule(Rule rule, ArrayList columns, ArrayList notColumns,
            ArrayList existsColumns, ArrayList evalConditions) {
        this.rule = rule;
        this.columnConstraints = (ColumnConstraints[]) columns
                .toArray(new ColumnConstraints[0]);
        this.notColumnConstraints = (ColumnConstraints[]) notColumns
                .toArray(new ColumnConstraints[0]);
        this.existsColumnConstraints = (ColumnConstraints[]) existsColumns
                .toArray(new ColumnConstraints[0]);
        this.evalConditions = (EvalCondition[]) evalConditions
                .toArray(new EvalCondition[0]);
        this.notColumnsPresent = (this.notColumnConstraints.length != 0);
        this.existsColumnsPresent = (this.existsColumnConstraints.length != 0);
        this.evalCoditionsPresent = (this.evalConditions.length != 0);

        ArrayList classes = new ArrayList();
        for (int i = 0; i < this.notColumnConstraints.length; i++) {
            if (classes.contains(this.notColumnConstraints[i].getClassType())) {
                classes.add(this.notColumnConstraints[i].getClassType());
            }
        }
        for (int i = 0; i < this.existsColumnConstraints.length; i++) {
            if (!classes.contains(this.existsColumnConstraints[i]
                    .getClassType())) {
                classes.add(this.existsColumnConstraints[i].getClassType());
            }
        }

        this.existsNotsClasses = (Class[]) classes.toArray(new Class[0]);
    }

    Rule getRule() {
        return this.rule;
    }

    int getNumberOfColumns() {
        return this.columnConstraints.length;
    }

    int getNumberOfNotColumns() {
        return this.notColumnConstraints.length;
    }

    int getNumberOfExistsColumns() {
        return this.existsColumnConstraints.length;
    }

    int getNumberOfEvalConditions() {
        return this.evalConditions.length;
    }

    Class getColumnClassObjectTypeAtPosition(int idx) {
        return this.columnConstraints[idx].getClassType();
    }

    ColumnConstraints getColumnConstraintsAtPosition(int idx) {
        return this.columnConstraints[idx];
    }

    ColumnConstraints[] getNotColumnConstraints() {
        return this.notColumnConstraints;
    }

    ColumnConstraints[] getExistsColumnConstraints() {
        return this.existsColumnConstraints;
    }

    EvalCondition[] getEvalConditions() {
        return this.evalConditions;
    }

    boolean containsNotColumns() {
        return this.notColumnsPresent;
    }

    boolean containsExistsColumns() {
        return this.existsColumnsPresent;
    }

    boolean containsEvalConditions() {
        return this.evalCoditionsPresent;
    }

    public int hashCode() {
        return this.rule.hashCode();
    }

    public boolean equals(Object that) {
        return this == that;
    }

    Class[] getExistsNotColumnsClasses() {
        return this.existsNotsClasses;
    }

    /** 
     * to simulate terminal node memory we introduce 
     * TerminalNodeMemory type attributes here
     * 
     */
    private AgendaGroupImpl agendaGroup;

    private ActivationQueue lifo;

    public ActivationQueue getLifo() {
        return this.lifo;
    }

    public void setLifo(ActivationQueue lifo) {
        this.lifo = lifo;
    }

    public AgendaGroupImpl getAgendaGroup() {
        return this.agendaGroup;
    }

    public void setAgendaGroup(AgendaGroupImpl agendaGroup) {
        this.agendaGroup = agendaGroup;
    }

}
