/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.drools.workbench.models.guided.dtable.shared.model;

import java.util.List;

/**
 * A column than can perform diff with another column.
 * E.g.: Useful when querying the differences in a user edited column for update column event.
 * BZ-996944: The idea is to show the changed fields and its values in audit log events.
 */
public interface DiffColumn {

    /**
     * Analyze differences between two columns of same type.
     * @param otherColumn The column to compare with this one.
     * @return A list of fields and its values that have changed.
     */
    List<BaseColumnFieldDiff> diff( BaseColumn otherColumn );

}
