/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.verifier.api.reporting;

import java.util.Set;

public class ImpossibleMatchIssue
        extends Issue {

    private String fieldFactType;
    private String fieldName;
    private String conflictedItem;
    private String conflictingItem;
    private String ruleId;

    public ImpossibleMatchIssue() {
    }

    public ImpossibleMatchIssue(final Severity severity,
                                final CheckType checkType,
                                final String ruleId,
                                final String fieldFactType,
                                final String fieldName,
                                final String conflictedItem,
                                final String conflictingItem,
                                final Set<Integer> rowNumbers) {
        super(severity,
              checkType,
              rowNumbers
        );

        this.ruleId = ruleId;
        this.fieldFactType = fieldFactType;
        this.fieldName = fieldName;
        this.conflictedItem = conflictedItem;
        this.conflictingItem = conflictingItem;
    }

    public void setFieldFactType(final String fieldFactType) {
        this.fieldFactType = fieldFactType;
    }

    public void setFieldName(final String fieldName) {
        this.fieldName = fieldName;
    }

    public void setConflictedItem(final String conflictedItem) {
        this.conflictedItem = conflictedItem;
    }

    public void setConflictingItem(final String conflictingItem) {
        this.conflictingItem = conflictingItem;
    }

    public void setRuleId(final String ruleId) {
        this.ruleId = ruleId;
    }

    public String getFieldFactType() {
        return fieldFactType;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getConflictedItem() {
        return conflictedItem;
    }

    public String getConflictingItem() {
        return conflictingItem;
    }

    public String getRuleId() {
        return ruleId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        ImpossibleMatchIssue that = (ImpossibleMatchIssue) o;

        if (fieldFactType != null ? !fieldFactType.equals(that.fieldFactType) : that.fieldFactType != null) {
            return false;
        }
        if (fieldName != null ? !fieldName.equals(that.fieldName) : that.fieldName != null) {
            return false;
        }
        if (conflictedItem != null ? !conflictedItem.equals(that.conflictedItem) : that.conflictedItem != null) {
            return false;
        }
        if (conflictingItem != null ? !conflictingItem.equals(that.conflictingItem) : that.conflictingItem != null) {
            return false;
        }
        return ruleId != null ? ruleId.equals(that.ruleId) : that.ruleId == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (fieldFactType != null ? ~~fieldFactType.hashCode() : 0);
        result = 31 * result + (fieldName != null ? ~~fieldName.hashCode() : 0);
        result = 31 * result + (conflictedItem != null ? ~~conflictedItem.hashCode() : 0);
        result = 31 * result + (conflictingItem != null ? ~~conflictingItem.hashCode() : 0);
        result = 31 * result + (ruleId != null ? ~~ruleId.hashCode() : 0);
        return result;
    }
}
