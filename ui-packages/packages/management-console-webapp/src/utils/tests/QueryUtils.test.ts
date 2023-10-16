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
import { ProcessInstanceState } from '@kogito-apps/management-console-shared/dist/types';
import { buildProcessListWhereArgument } from '../QueryUtils';

describe('QueryUtils test', () => {
  it('buildWhereArgument', () => {
    const filtersWithoutBusinessKey = {
      status: [ProcessInstanceState.Active],
      businessKey: []
    };

    const filtersWithBusinessKey = {
      status: [ProcessInstanceState.Active],
      businessKey: ['GMR31']
    };
    const result1 = buildProcessListWhereArgument(filtersWithoutBusinessKey);
    const result2 = buildProcessListWhereArgument(filtersWithBusinessKey);
    expect(result1.or).toBe(undefined);
    expect(result2.or).toEqual([
      { businessKey: { like: filtersWithBusinessKey.businessKey[0] } }
    ]);
  });
});
