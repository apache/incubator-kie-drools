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
import React from 'react';
import Moment from 'react-moment';
import { DataTableColumn } from '@kogito-apps/components-common/dist/components/DataTable';
import { CustomDashboardInfo } from '../../../api/CustomDashboardListEnvelopeApi';

export const getDashboardNameColumn = (
  selectDashboard: (customDashboardInfo: CustomDashboardInfo) => void
): DataTableColumn => {
  return {
    label: 'Name',
    path: 'name',
    bodyCellTransformer: (cellValue, rowDashboard: CustomDashboardInfo) => {
      return (
        <a onClick={() => selectDashboard(rowDashboard)}>
          <strong>{cellValue}</strong>
        </a>
      );
    },
    isSortable: true
  };
};

export const getDateColumn = (
  columnPath: string,
  columnLabel: string
): DataTableColumn => {
  return {
    label: columnLabel,
    path: columnPath,
    bodyCellTransformer: (value) => (
      <Moment fromNow>{new Date(`${value}`)}</Moment>
    ),
    isSortable: true
  };
};
