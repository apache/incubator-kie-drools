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

export default class ModelConversionTool {
  public static convertDateToString = (model: any, schema: any): any => {
    return ModelConversionTool.convertDates(model, schema, value =>
      value.toISOString()
    );
  };

  public static convertStringToDate = (model: any, schema: any): any => {
    return ModelConversionTool.convertDates(
      model,
      schema,
      value => new Date(value)
    );
  };

  private static convertDates = (
    model: any,
    schema: any,
    conversion: (value: any) => any
  ): any => {
    const obj: any = {};

    if (!model) {
      return obj;
    }

    if (!schema.properties) {
      return obj;
    }

    Object.keys(model).forEach(property => {
      const properties = schema.properties[property];

      const value = model[property];

      if (value === null) {
        return;
      }

      if (!properties) {
        obj[property] = value;
        return;
      }

      switch (properties.type) {
        case 'object':
          obj[property] = ModelConversionTool.convertDates(
            value,
            properties,
            conversion
          );
          break;
        case 'array':
          if (properties.items && properties.items.type === 'object') {
            obj[property] = value.map((item: any) =>
              ModelConversionTool.convertDates(
                item,
                properties.items,
                conversion
              )
            );
          } else {
            obj[property] = value;
          }
          break;
        case 'string':
          switch (properties.format) {
            case 'date-time':
            case 'date':
              obj[property] = conversion(value);
              break;
            default:
              obj[property] = value;
              break;
          }
          break;
        default:
          obj[property] = value;
          break;
      }
    });
    return obj;
  };
}
