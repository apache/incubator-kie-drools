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

import { getColumn, getActionColumn } from '../ProcessDefinitionListUtils';

describe('Utils tests', () => {
  it('Test default column', () => {
    const column = getColumn('path', 'Label');

    expect(column).not.toBeNull();
    expect(column.path).toBe('path');
    expect(column.label).toBe('Label');
    expect(column.bodyCellTransformer).not.toBeUndefined();
  });

  it('Test action column', () => {
    const column = getActionColumn(jest.fn(), 'Workflow');

    expect(column).not.toBeNull();
    expect(column.path).toBe('actions');
    expect(column.label).toBe('Actions');
    expect(column.bodyCellTransformer).not.toBeUndefined();
  });
});
