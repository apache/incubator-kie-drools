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
import _ from 'lodash';
import { ModelConversionTool } from '../ModelConversionTool';

const inlineSchema = {
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

const draft7schema = {
  definitions: {
    Nested: {
      type: 'object',
      properties: {
        date: {
          type: 'string',
          format: 'date-time'
        }
      }
    }
  },
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
      $ref: '#/definitions/Nested'
    },
    children: {
      type: 'array',
      items: {
        $ref: '#/definitions/Nested'
      }
    }
  }
};

const draft7AllOfschema = {
  definitions: {
    Nested: {
      type: 'object',
      properties: {
        date: {
          type: 'string',
          format: 'date-time'
        }
      }
    }
  },
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
      allOf: [
        {
          $ref: '#/definitions/Nested'
        },
        {
          input: true
        }
      ]
    },
    children: {
      type: 'array',
      items: {
        allOf: [
          {
            $ref: '#/definitions/Nested'
          },
          {
            input: true
          }
        ]
      }
    }
  }
};

const draft2019schema = {
  $defs: {
    Nested: {
      type: 'object',
      properties: {
        date: {
          type: 'string',
          format: 'date-time'
        }
      }
    }
  },
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
      $ref: '#/definitions/Nested'
    },
    children: {
      type: 'array',
      items: {
        $ref: '#/definitions/Nested'
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
  schema: any,
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
  convertedModel.children.forEach((child) => {
    expect(child).not.toBeNull();
    expect(child.date).toStrictEqual(convertedDateValue);
  });
}

describe('Model Conversion  tests', () => {
  describe('Inline Schema', () => {
    it('String to dates conversion', () => {
      testModel(
        currentStrDate,
        currentDate,
        inlineSchema,
        (model, formSchema) =>
          ModelConversionTool.convertStringToDate(model, formSchema)
      );
    });

    it('Dates to strings conversion', () => {
      testModel(
        currentDate,
        currentStrDate,
        inlineSchema,
        (model, formSchema) =>
          ModelConversionTool.convertDateToString(model, formSchema)
      );
    });

    it('Empty schema conversion', () => {
      const model = getModel(currentDate);
      const formSchema = _.cloneDeep(inlineSchema);
      delete formSchema.properties;

      const result = ModelConversionTool.convertDateToString(model, formSchema);

      expect(result).not.toBeNull();
    });
  });

  describe('Draft 7 schema', () => {
    it('String to dates conversion', () => {
      testModel(
        currentStrDate,
        currentDate,
        draft7schema,
        (model, formSchema) =>
          ModelConversionTool.convertStringToDate(model, formSchema)
      );

      testModel(
        currentStrDate,
        currentDate,
        draft7AllOfschema,
        (model, formSchema) =>
          ModelConversionTool.convertStringToDate(model, formSchema)
      );
    });

    it('Dates to strings conversion', () => {
      testModel(
        currentDate,
        currentStrDate,
        draft7schema,
        (model, draft7schema) =>
          ModelConversionTool.convertDateToString(model, draft7schema)
      );

      testModel(
        currentDate,
        currentStrDate,
        draft7AllOfschema,
        (model, draft7schema) =>
          ModelConversionTool.convertDateToString(model, draft7schema)
      );
    });

    it('Empty schema conversion', () => {
      const model = getModel(currentDate);
      const formSchema = _.cloneDeep(draft7schema);
      delete formSchema.properties;

      const result = ModelConversionTool.convertDateToString(
        model,
        draft7schema
      );

      expect(result).not.toBeNull();
    });
  });

  describe('Draft 2019 schema', () => {
    it('String to dates conversion', () => {
      testModel(
        currentStrDate,
        currentDate,
        draft2019schema,
        (model, formSchema) =>
          ModelConversionTool.convertStringToDate(model, formSchema)
      );
    });

    it('Dates to strings conversion', () => {
      testModel(
        currentDate,
        currentStrDate,
        draft2019schema,
        (model, draft7schema) =>
          ModelConversionTool.convertDateToString(model, draft7schema)
      );
    });

    it('Empty schema conversion', () => {
      const model = getModel(currentDate);
      const formSchema = _.cloneDeep(draft2019schema);
      delete formSchema.properties;

      const result = ModelConversionTool.convertDateToString(
        model,
        draft7schema
      );

      expect(result).not.toBeNull();
    });
  });
});
