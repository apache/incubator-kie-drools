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

import _ from 'lodash';
import {
  generateFormData,
  parseTaskSchema,
  readSchemaAssignments
} from '../TaskFormDataUtils';
import { SCHEMA_VERSION } from '@kogito-apps/components-common';

const userTask = {
  id: '45a73767-5da3-49bf-9c40-d533c3e77ef3',
  description: undefined,
  name: 'VisaApplication',
  priority: '1',
  processInstanceId: '9ae7ce3b-d49c-4f35-b843-8ac3d22fa427',
  processId: 'travels',
  rootProcessInstanceId: undefined,
  rootProcessId: undefined,
  state: 'Ready',
  actualOwner: 'john',
  adminGroups: [],
  adminUsers: [],
  completed: undefined,
  started: new Date('2020-02-19T11:11:56.282Z'),
  excludedUsers: [],
  potentialGroups: [],
  potentialUsers: [],
  inputs: '{}',
  outputs: '{}',
  referenceName: 'Apply for visa (Empty Form)',
  lastUpdate: new Date('2020-02-19T11:11:56.282Z'),
  endpoint:
    'http://localhost:4000/travels/9ae7ce3b-d49c-4f35-b843-8ac3d22fa427/VisaApplication/45a73767-5da3-49bf-9c40-d533c3e77ef3'
};

describe('TaskFormDataUtils tests', () => {
  it('parseTaskSchema for regular schema', () => {
    const testSchema = {
      $schema: SCHEMA_VERSION.DRAFT_2019_09,
      type: 'object',
      properties: {
        name: { type: 'string', input: true },
        lastName: { type: 'string', input: true },
        job: { type: 'string', input: true, output: true },
        address: { type: 'string', output: true }
      },
      phases: ['complete', 'skip']
    };

    const parsedSchema = parseTaskSchema(testSchema);

    expect(parsedSchema.assignments.inputs).toHaveLength(3);
    expect(parsedSchema.assignments.inputs).toContain('name');
    expect(parsedSchema.assignments.inputs).toContain('lastName');
    expect(parsedSchema.assignments.inputs).toContain('job');

    expect(parsedSchema.assignments.outputs).toHaveLength(2);
    expect(parsedSchema.assignments.outputs).toContain('job');
    expect(parsedSchema.assignments.outputs).toContain('address');

    expect(
      _.get(parsedSchema.schema, 'properties.name.uniforms.disabled')
    ).toBeTruthy();
    expect(
      _.get(parsedSchema.schema, 'properties.lastName.uniforms.disabled')
    ).toBeTruthy();

    expect(_.has(parsedSchema.schema, 'properties.job.uniforms')).toBeFalsy();
    expect(
      _.has(parsedSchema.schema, 'properties.address.uniforms')
    ).toBeFalsy();
  });

  it('parseTaskSchema for DRAFT_7 schema', () => {
    const testSchema = {
      $schema: SCHEMA_VERSION.DRAFT_7,
      definitions: {
        Address: {
          type: 'object',
          properties: {
            date: { type: 'string', format: 'date-time' },
            street: { type: 'string' }
          }
        }
      },
      type: 'object',
      properties: {
        name: { type: 'string', input: true },
        address: {
          allOf: [{ $ref: '#/definitions/Address' }, { input: true }]
        },
        officeAddress: {
          allOf: [
            { $ref: '#/definitions/Address' },
            { input: true },
            { output: true }
          ]
        },
        extraAddress: {
          allOf: [{ $ref: '#/definitions/Address' }, { output: true }]
        }
      },
      phases: ['complete', 'skip']
    };

    const parsedSchema = parseTaskSchema(testSchema);
    expect(parsedSchema.assignments.inputs).toHaveLength(3);
    expect(parsedSchema.assignments.inputs).toContain('name');
    expect(parsedSchema.assignments.inputs).toContain('address');
    expect(parsedSchema.assignments.inputs).toContain('officeAddress');

    expect(parsedSchema.assignments.outputs).toHaveLength(2);
    expect(parsedSchema.assignments.outputs).toContain('officeAddress');
    expect(parsedSchema.assignments.outputs).toContain('extraAddress');

    expect(
      _.get(parsedSchema.schema, 'properties.name.uniforms.disabled')
    ).toBeTruthy();
    expect(
      _.get(parsedSchema.schema, 'properties.address.uniforms.disabled')
    ).toBeTruthy();

    expect(
      _.has(parsedSchema.schema, 'properties.officeAddress.uniforms')
    ).toBeFalsy();
    expect(
      _.has(parsedSchema.schema, 'properties.extraAddress.uniforms')
    ).toBeFalsy();
  });

  it('readSchemaAssignments', () => {
    const testSchema = {
      type: 'object',
      properties: {
        name: { type: 'string', input: true },
        lastName: { type: 'string', input: true },
        job: { type: 'string', input: true, output: true },
        address: { type: 'string', output: true }
      }
    };

    const assignments = readSchemaAssignments(testSchema);

    expect(assignments.inputs).toHaveLength(3);
    expect(assignments.inputs).toContain('name');
    expect(assignments.inputs).toContain('lastName');
    expect(assignments.inputs).toContain('job');

    expect(assignments.outputs).toHaveLength(2);
    expect(assignments.outputs).toContain('job');
    expect(assignments.outputs).toContain('address');

    expect(_.get(testSchema, 'properties.name.uniforms.disabled')).toBeTruthy();
    expect(
      _.get(testSchema, 'properties.lastName.uniforms.disabled')
    ).toBeTruthy();

    expect(_.has(testSchema, 'properties.job.uniforms')).toBeFalsy();
    expect(_.has(testSchema, 'properties.address.uniforms')).toBeFalsy();
  });

  it('generateFormData with inputs', () => {
    const inputs = {
      person: {
        name: 'Jon',
        lastName: 'Snow'
      }
    };
    const testTask = _.cloneDeep(userTask);

    testTask.inputs = JSON.stringify(inputs);

    const data = generateFormData(testTask);

    expect(_.get(data, 'person.name')).toStrictEqual('Jon');
    expect(_.get(data, 'person.lastName')).toStrictEqual('Snow');
  });

  it('generateFormData with inputs/outputs', () => {
    const inputs = {
      person: {
        name: 'Jon',
        lastName: 'Snow'
      }
    };

    const outputs = {
      person: {
        job: 'hero'
      },
      address: {
        city: 'Winterfell'
      }
    };
    const testTask = _.cloneDeep(userTask);

    testTask.inputs = JSON.stringify(inputs);
    testTask.outputs = JSON.stringify(outputs);

    const data = generateFormData(testTask);

    expect(_.get(data, 'person.name')).toStrictEqual('Jon');
    expect(_.get(data, 'person.lastName')).toStrictEqual('Snow');
    expect(_.get(data, 'person.job')).toStrictEqual('hero');
    expect(_.get(data, 'address.city')).toStrictEqual('Winterfell');
  });
});
