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

import { Form, FormDetailsDriver } from '../../../api';

export const formContent: Form = {
  source: {
    'source-content':
      '<div><div class="form-check"> <input type="checkbox" id="uniforms-0001-0001" name="approve" class="form-check-input" /> <label class="form-check-label" for="uniforms-0001-0001">Approve</label> </div> <fieldset disabled> <legend>Candidate</legend> <div> <div class="form-group"> <label for="uniforms-0001-0004">Email</label> <input type="text" id="uniforms-0001-0004" name="candidate.email" class="form-control" disabled value="" /> </div> <div class="form-group"> <label for="uniforms-0001-0005">Name</label> <input type="text" id="uniforms-0001-0005" name="candidate.name" class="form-control" disabled value="" /> </div> <div class="form-group"> <label for="uniforms-0001-0007">Salary</label> <input type="number" class="form-control" id="uniforms-0001-0007" name="candidate.salary" disabled step="1" value="" /> </div> <div class="form-group"> <label for="uniforms-0001-0008">Skills</label> <input type="text" id="uniforms-0001-0008" name="candidate.skills" class="form-control" disabled value="" /> </div> </div> </fieldset> </div>'
  },
  name: 'form1',
  formConfiguration: {
    schema:
      '{"$schema":"http://json-schema.org/draft-07/schema#","type":"object","properties":{"approve":{"type":"boolean","output":true},"candidate":{"type":"object","properties":{"email":{"type":"string"},"name":{"type":"string"},"salary":{"type":"integer"},"skills":{"type":"string"}},"input":true}}}',
    resources: {
      scripts: {},
      styles: {}
    }
  }
};
export class MockedFormDetailsDriver implements FormDetailsDriver {
  getFormContent(): Promise<Form> {
    return Promise.resolve(formContent);
  }
}
