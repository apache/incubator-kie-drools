import Ajv, { ValidateFunction } from 'ajv';

/**
 * Defines a basic Form Validator
 *
 * @interface
 */
export interface FormValidator {
  /**
   * Validates the given model
   * @param model The model to validate
   * @returns If there are validation errors it should return an error
   * containing the errors
   */
  validate(model: any): any | undefined;
}

/**
 * Implementation of a validator using AJV
 */
export class DefaultFormValidator implements FormValidator {
  readonly schema: any;
  readonly validator: ValidateFunction;

  constructor(schema: any) {
    this.schema = schema;

    this.validator = new Ajv({ allErrors: true, useDefaults: true }).compile(
      schema
    );
  }

  validate(model: any): any | undefined {
    this.validator(model);

    if (this.validator.errors && this.validator.errors.length) {
      return { details: this.validator.errors };
    }
  }
}
