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
export class ModelConversionTool {
  public static convertDateToString = (
    model: any,
    schema: Record<string, any>
  ): any => {
    return doConvert(model, schema, (value) => value.toISOString());
  };

  public static convertStringToDate = (
    model: any,
    schema: Record<string, any>
  ): any => {
    return doConvert(model, schema, (value) => new Date(value));
  };
}

interface ContextInitArgs {
  defintions: Record<string, any>;
  rootSchema: Record<string, any>;
  rootModel: any;
  conversion: (value: any) => any;
}
class ConversionContext {
  public readonly definitions: Record<string, any>;
  public readonly rootSchema: Record<string, any>;
  public readonly rootModel: any;
  public readonly convert: (value: any) => any;

  constructor(args: ContextInitArgs) {
    this.definitions = args.defintions;
    this.rootSchema = args.rootSchema;
    this.rootModel = args.rootModel;
    this.convert = args.conversion;
  }

  public lookupDefinition(ref: string): Record<string, any> | undefined {
    if (!this.definitions) {
      return undefined;
    }
    const index = ref.lastIndexOf('/');

    if (index === -1) {
      return undefined;
    }

    return this.definitions[ref.substring(index + 1)];
  }
}

function doConvert(
  model: any,
  schema: Record<string, any>,
  conversion: (value: any) => any
): any {
  const ctx: ConversionContext = new ConversionContext({
    rootSchema: schema,
    rootModel: model,
    defintions: schema.definitions || schema.$defs,
    conversion
  });

  return convertModel(model, schema, ctx);
}

function convertModel(
  model: any,
  schema: Record<string, any>,
  ctx: ConversionContext
) {
  const obj: any = {};

  if (!model) {
    return obj;
  }

  if (!schema.properties) {
    return obj;
  }

  Object.keys(model).forEach((propertyName) => {
    const property = schema.properties[propertyName];

    const value = model[propertyName];

    if (value === null) {
      return;
    }

    if (!property) {
      obj[propertyName] = value;
      return;
    }

    const props = lookupSchemaPropertyProps(property, ctx);

    switch (props.type) {
      case 'object':
        obj[propertyName] = convertModel(value, props, ctx);
        break;
      case 'array':
        if (property.items) {
          const itemProps = lookupSchemaPropertyProps(props.items, ctx);
          if (itemProps.type === 'object') {
            obj[propertyName] = value.map((item: any) =>
              convertModel(item, itemProps, ctx)
            );
          } else {
            obj[propertyName] = value;
          }
        } else {
          obj[propertyName] = value;
        }
        break;
      case 'string':
        switch (props.format) {
          case 'date-time':
          case 'date':
            obj[propertyName] = ctx.convert(value);
            break;
          default:
            obj[propertyName] = value;
            break;
        }
        break;
      default:
        obj[propertyName] = value;
        break;
    }
  });
  return obj;
}

function lookupSchemaPropertyProps(property: any, ctx: ConversionContext) {
  if (property['$ref']) {
    return ctx.lookupDefinition(property['$ref']) || property;
  }

  if (property['allOf']) {
    const allOf: [] = property.allOf;

    const refItem = allOf.find((item) => item['$ref']);
    if (refItem) {
      return ctx.lookupDefinition(refItem['$ref']);
    }
  }
  return property;
}
