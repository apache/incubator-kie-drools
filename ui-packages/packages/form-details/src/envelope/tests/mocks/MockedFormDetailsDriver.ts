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
import { Form, FormContent, FormDetailsDriver } from '../../../api';

export const formContent: Form = {
  formInfo: {
    name: 'form1',
    type: 'HTML' as any,
    lastModified: new Date('2020-07-11T18:30:00.000Z')
  },
  configuration: {
    schema:
      '{"$schema":"http://json-schema.org/draft-07/schema#","type":"object","properties":{"approve":{"type":"boolean","output":true},"candidate":{"type":"object","properties":{"email":{"type":"string"},"name":{"type":"string"},"salary":{"type":"integer"},"skills":{"type":"string"}},"input":true}}}',
    resources: {
      scripts: {},
      styles: {}
    }
  },
  source: 'html source code'
};
export class MockedFormDetailsDriver implements FormDetailsDriver {
  getFormContent(): Promise<Form> {
    return Promise.resolve(formContent);
  }

  saveFormContent(formName: string, content: FormContent) {
    return;
  }
}
