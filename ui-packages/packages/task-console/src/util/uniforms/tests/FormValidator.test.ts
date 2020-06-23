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
