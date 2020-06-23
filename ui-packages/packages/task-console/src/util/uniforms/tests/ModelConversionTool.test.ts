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
  test('String to dates conversion', () => {
    testModel(currentStrDate, currentDate, (model, formSchema) =>
      ModelConversionTool.convertStringToDate(model, formSchema)
    );
  });

  test('Dates to strings conversion', () => {
    testModel(currentDate, currentStrDate, (model, formSchema) =>
      ModelConversionTool.convertDateToString(model, formSchema)
    );
  });
});
