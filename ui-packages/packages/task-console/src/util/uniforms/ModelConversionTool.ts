export default class ModelConversionTool {
  public static convertDateToString = (model, schema): any => {
    return ModelConversionTool.convertDates(model, schema, value =>
      value.toISOString()
    );
  };

  public static convertStringToDate = (model, schema): any => {
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
    const obj = {};

    if (model) {
      Object.keys(model).forEach(property => {
        const properties = schema.properties[property];

        const value = model[property];

        if (value != null) {
          if (properties) {
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
                  obj[property] = value.map(item =>
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
                if (properties.format === 'date-time') {
                  obj[property] = conversion(value);
                } else {
                  obj[property] = value;
                }
                break;
              default:
                obj[property] = value;
                break;
            }
          } else {
            obj[property] = value;
          }
        }
      });
    }
    return obj;
  };
}
