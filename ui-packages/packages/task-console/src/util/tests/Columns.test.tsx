/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

import Columns from '../Columns';

describe('Columns testing', () => {
  it('Test default column', () => {
    const column = Columns.getDefaultColumn('path', 'Label');

    expect(column).not.toBeNull();
    expect(column.path).toBe('path');
    expect(column.label).toBe('Label');
    expect(column.bodyCellTransformer).toBeUndefined();
  });

  it('Test task description column', async () => {
    const column = Columns.getDateColumn('path', 'Date Column');

    expect(column).not.toBeNull();
    expect(column.path).toBe('path');
    expect(column.label).toBe('Date Column');
    expect(column.bodyCellTransformer).not.toBeUndefined();
  });

  it('Test task description column', async () => {
    const column = Columns.getTaskDescriptionColumn();

    expect(column).not.toBeNull();
    expect(column.path).toBe('referenceName');
    expect(column.label).toBe('Name');
    expect(column.bodyCellTransformer).not.toBeUndefined();
  });

  it('Test task state column', async () => {
    const column = Columns.getTaskStateColumn();

    expect(column).not.toBeNull();
    expect(column.path).toBe('state');
    expect(column.label).toBe('State');
    expect(column.bodyCellTransformer).not.toBeUndefined();
  });
});
