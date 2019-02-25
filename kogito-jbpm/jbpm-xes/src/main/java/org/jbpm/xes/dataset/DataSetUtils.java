/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.xes.dataset;

import java.util.Date;

import org.dashbuilder.dataset.DataSet;

public final class DataSetUtils {

    private DataSetUtils() {
    }

    public static String getColumnStringValue(DataSet dataSet, String columnId, int index) {
        Object value = dataSet.getValueAt(index, columnId);
        return value != null ? value.toString() : null;
    }

    public static Date getColumnDateValue(DataSet dataSet, String columnId, int index) {
        try {
            return (Date) dataSet.getValueAt(index, columnId);
        } catch (Exception e) {
            return null;
        }
    }

    public static Integer getColumnIntValue(DataSet dataSet, String columnId, int index) {
        Object value = dataSet.getValueAt(index, columnId);
        if (value == null) {
            return null;
        }
        return value instanceof Double ? ((Double) value).intValue() : Integer.parseInt(value.toString());
    }

    public static Long getColumnLongValue(DataSet currentDataSet, String columnId, int index) {
        Object value = currentDataSet.getValueAt(index, columnId);
        if (value == null) {
            return null;
        }
        return value instanceof Double ? ((Double) value).longValue() : Long.parseLong(value.toString());
    }
}
