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
import cloneDeep from 'lodash/cloneDeep';
import set from 'lodash/set';
import { SCHEMA_VERSION } from '../../../types/types';
import {
  Draft2019_09Validator,
  Draft7FormValidator,
  lookupValidator
} from '../FormValidator';

const schema = {
  type: 'object',
  properties: {
    name: { type: 'string' },
    lastName: { type: 'string' },
    age: { type: 'integer', minimum: 18 }
  },
  required: ['name', 'lastName']
};

const draft7SchemaWithRefs = {
  $schema: 'http://json-schema.org/draft-07/schema#',
  definitions: {
    Person: {
      type: 'object',
      properties: {
        name: { type: 'string' },
        lastName: { type: 'string' },
        age: { type: 'integer', minimum: 18 }
      }
    }
  },
  type: 'object',
  properties: {
    person: {
      allOf: [
        {
          $ref: '#/definitions/Person'
        }
      ]
    }
  }
};

describe('Model Validation  tests', () => {
  test('Test DRAFT_7 succesfully model validation', () => {
    const draft7Schema = cloneDeep(schema);
    set(draft7Schema, '$schema', SCHEMA_VERSION.DRAFT_7);
    const validator = lookupValidator(draft7Schema);

    const model = {
      name: 'John',
      lastName: 'Doe',
      age: 27
    };

    expect(validator).toBeInstanceOf(Draft7FormValidator);
    expect(validator.validate(model)).toBeUndefined();
  });

  test('Test DRAFT_7 model validation with errors', () => {
    const draft7Schema = cloneDeep(schema);
    set(draft7Schema, '$schema', SCHEMA_VERSION.DRAFT_7);
    const validator = lookupValidator(draft7Schema);

    const model = {
      age: 10
    };
    expect(validator).toBeInstanceOf(Draft7FormValidator);
    expect(validator.validate(model)).not.toBeUndefined();
  });

  test('Test DRAFT_7 with $ref succesfully model validation', () => {
    const validator = lookupValidator(draft7SchemaWithRefs);

    const model = {
      person: {
        name: 'John',
        lastName: 'Doe',
        age: 27
      }
    };

    expect(validator).toBeInstanceOf(Draft7FormValidator);
    expect(validator.validate(model)).toBeUndefined();
  });

  test('Test DRAFT_2019_09 model validation', () => {
    const draft2019Schema = cloneDeep(schema);
    set(draft2019Schema, '$schema', SCHEMA_VERSION.DRAFT_2019_09);
    const validator = lookupValidator(draft2019Schema);

    const model = {
      name: 'John',
      lastName: 'Doe',
      age: 27
    };

    expect(validator).toBeInstanceOf(Draft2019_09Validator);
    expect(validator.validate(model)).toBeUndefined();
  });

  test('Test wrong schema version', () => {
    const wrongSchema = cloneDeep(schema);
    const schemaVersion = 'wrong schema here';
    set(wrongSchema, '$schema', schemaVersion);

    const validator = lookupValidator(wrongSchema);

    const model = {
      name: 'John',
      lastName: 'Doe',
      age: 27
    };

    expect(validator).not.toBeInstanceOf(Draft7FormValidator);
    expect(validator).not.toBeInstanceOf(Draft2019_09Validator);
    expect(validator.validate(model)).toBeUndefined();
  });
});
