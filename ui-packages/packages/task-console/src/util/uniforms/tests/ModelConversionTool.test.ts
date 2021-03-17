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
import ModelConversionTool from '../ModelConversionTool';

const schema = {
  type: 'object',
  properties: {
    name: { type: 'string' },
    lastName: { type: 'string' },
    married: { type: 'boolean' },
    age: { type: 'integer' },
    date: {
      type: 'string',
      format: 'date-time'
    },
    nested: {
      type: 'object',
      properties: {
        date: {
          type: 'string',
          format: 'date-time'
        }
      }
    },
    children: {
      type: 'array',
      items: {
        type: 'object',
        properties: {
          date: {
            type: 'string',
            format: 'date-time'
          }
        }
      }
    }
  }
};

const currentDate = new Date();
const currentStrDate = currentDate.toISOString();

function getModel(dateValue: Date | string): any {
  return {
    name: 'John',
    lastName: 'Doe',
    fullName: 'Doe, John',
    married: false,
    age: 27,
    date: dateValue,
    nested: {
      date: dateValue
    },
    children: [
      {
        date: dateValue
      },
      {
        date: dateValue
      },
      {
        date: dateValue
      }
    ]
  };
}

function testModel(
  originalDateValue: Date | string,
  convertedDateValue: string | Date,
  conversion: (model, schema) => any
) {
  const model = getModel(originalDateValue);

  const convertedModel = conversion(model, schema);

  expect(convertedModel.name).toStrictEqual(model.name);
  expect(convertedModel.lastName).toStrictEqual(model.lastName);
  expect(convertedModel.fullName).toStrictEqual(model.fullName);
  expect(convertedModel.married).toStrictEqual(model.married);
  expect(convertedModel.age).toStrictEqual(model.age);
  expect(convertedModel.date).toStrictEqual(convertedDateValue);

  expect(convertedModel.nested).not.toBeNull();
  expect(convertedModel.nested.date).toStrictEqual(convertedDateValue);

  expect(convertedModel.children).not.toBeNull();
  expect(convertedModel.children).toHaveLength(3);
  convertedModel.children.forEach(child => {
    expect(child).not.toBeNull();
    expect(child.date).toStrictEqual(convertedDateValue);
  });
}

describe('Model Conversion  tests', () => {
  it('String to dates conversion', () => {
    testModel(currentStrDate, currentDate, (model, formSchema) =>
      ModelConversionTool.convertStringToDate(model, formSchema)
    );
  });

  it('Dates to strings conversion', () => {
    testModel(currentDate, currentStrDate, (model, formSchema) =>
      ModelConversionTool.convertDateToString(model, formSchema)
    );
  });

  it('Empty schema conversion', () => {
    const model = getModel(currentDate);
    const formSchema = _.cloneDeep(schema);
    delete formSchema.properties;

    const result = ModelConversionTool.convertDateToString(model, formSchema);

    expect(result).not.toBeNull();
  });
});
