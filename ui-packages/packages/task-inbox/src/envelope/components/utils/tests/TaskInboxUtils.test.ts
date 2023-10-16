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
import {
  getDateColumn,
  getDefaultColumn,
  getTaskDescriptionColumn,
  getTaskStateColumn
} from '../TaskInboxUtils';

describe('Utils tests', () => {
  it('Test default column', () => {
    const column = getDefaultColumn('path', 'Label', true);

    expect(column).not.toBeNull();
    expect(column.path).toBe('path');
    expect(column.label).toBe('Label');
    expect(column.bodyCellTransformer).toBeUndefined();
  });

  it('Test task description column', () => {
    const column = getDateColumn('path', 'Date Column');

    expect(column).not.toBeNull();
    expect(column.path).toBe('path');
    expect(column.label).toBe('Date Column');
    expect(column.bodyCellTransformer).not.toBeUndefined();
  });

  it('Test task description column', () => {
    const column = getTaskDescriptionColumn(jest.fn());

    expect(column).not.toBeNull();
    expect(column.path).toBe('referenceName');
    expect(column.label).toBe('Name');
    expect(column.bodyCellTransformer).not.toBeUndefined();
  });

  it('Test task state column', () => {
    const column = getTaskStateColumn();

    expect(column).not.toBeNull();
    expect(column.path).toBe('state');
    expect(column.label).toBe('Status');
    expect(column.bodyCellTransformer).not.toBeUndefined();
  });
});
