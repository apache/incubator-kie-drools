/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

import React from 'react';
import Moment from 'react-moment';
import { DataTableColumn } from '@kogito-apps/components-common/dist/components/DataTable';
import { FormInfo } from '../../../api/FormsListEnvelopeApi';
import { Label } from '@patternfly/react-core';

export const getFormTypeLabel = (value) => {
  switch (value) {
    case 'HTML':
      return <Label variant="outline">HTML</Label>;
    case 'TSX':
      return <Label variant="outline">REACT</Label>;
    /* istanbul ignore next */
    default:
      return value;
  }
};

export const getFormNameColumn = (
  selectForm: (formData: FormInfo) => void
): DataTableColumn => {
  return {
    label: 'Name',
    path: 'name',
    bodyCellTransformer: (cellValue, rowForm: FormInfo) => {
      return (
        <a onClick={() => selectForm(rowForm)}>
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

export const getFormTypeColumn = (): DataTableColumn => {
  return {
    label: 'Type',
    path: 'type',
    bodyCellTransformer: (cellValue) => getFormTypeLabel(cellValue),
    isSortable: true
  };
};
