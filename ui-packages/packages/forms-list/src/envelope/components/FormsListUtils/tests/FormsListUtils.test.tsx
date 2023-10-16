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
import { FormType } from '../../../../api/FormsListEnvelopeApi';
import {
  getDateColumn,
  getFormNameColumn,
  getFormTypeColumn,
  getFormTypeLabel
} from '../FormsListUtils';

describe('forms list utils tests', () => {
  it('get form name column', () => {
    const result = getFormNameColumn(jest.fn());
    expect(result.label).toEqual('Name');
    expect(result.path).toEqual('name');
  });
  it('get date column', () => {
    const result = getDateColumn('lastModified', 'Last Modified');
    expect(result.label).toEqual('Last Modified');
    expect(result.path).toEqual('lastModified');
  });
  it('get form type', () => {
    const result = getFormTypeColumn();
    expect(result.label).toEqual('Type');
    expect(result.path).toEqual('type');
  });
  it('get form lable', () => {
    const result1 = getFormTypeLabel(FormType.HTML);
    expect(result1.props.children).toEqual(FormType.HTML);
    const result2 = getFormTypeLabel(FormType.TSX);
    expect(result2.props.children).toEqual('REACT');
  });
});
