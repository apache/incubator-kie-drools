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

import { DefaultFormValidator } from '../FormValidator';

const schema = {
  type: 'object',
  properties: {
    name: { type: 'string' },
    lastName: { type: 'string' },
    age: { type: 'integer', minimum: 18 }
  },
  required: ['name', 'lastName']
};

const validator = new DefaultFormValidator(schema);

describe('Model Conversion  tests', () => {
  test('Test succesfully model validation', () => {
    const model = {
      name: 'John',
      lastName: 'Doe',
      age: 27
    };
    expect(validator.validate(model)).toBeUndefined();
  });

  test('Test model validation with errors', () => {
    const model = {
      age: 10
    };
    expect(validator.validate(model)).not.toBeUndefined();
  });
});
