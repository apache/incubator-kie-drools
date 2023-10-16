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
import { FormFilter, FormInfo, FormsListDriver, FormType } from '../../../api';

export const formList: FormInfo[] = [
  {
    name: 'form1',
    type: FormType.TSX,
    lastModified: new Date('2020-07-11T18:30:00.000Z')
  },
  {
    name: 'form2',
    type: FormType.HTML,
    lastModified: new Date('2020-07-11T18:30:00.000Z')
  }
];
export class MockedFormsListDriver implements FormsListDriver {
  getFormFilter(): Promise<FormFilter> {
    return Promise.resolve({ formNames: [] });
  }
  applyFilter(formFilter: FormFilter): Promise<void> {
    return Promise.resolve();
  }
  getFormsQuery(): Promise<FormInfo[]> {
    return Promise.resolve(formList);
  }
  openForm(formData: FormInfo): Promise<void> {
    return Promise.resolve();
  }
}
