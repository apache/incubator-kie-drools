import React from 'react';
import JSONSchemaBridge from 'uniforms-bridge-json-schema';
import { AutoFields, AutoForm, ErrorsField } from 'uniforms-patternfly';
import FormFooter from '../../Atoms/FormFooter/FormFooter';
import ModelConversionTool from '../../../util/uniforms/ModelConversionTool';
import { DefaultFormValidator } from '../../../util/uniforms/FormValidator';
import { IFormSubmitHandler } from '../../../util/uniforms/FormSubmitHandler/FormSubmitHandler';
import { FormSchema } from '../../../util/uniforms/FormSchema';

interface IOwnProps {
  formSchema: FormSchema;
  model?: any;
  readOnly: boolean;
  formSubmitHandler: IFormSubmitHandler;
}

const FormRenderer: React.FC<IOwnProps> = ({
  formSchema,
  model,
  readOnly,
  formSubmitHandler
}) => {
  const validator = new DefaultFormValidator(formSchema);

  const bridge = new JSONSchemaBridge(formSchema, formModel => {
    // Converting back all the JS Dates into String before validating the model
    const newModel = ModelConversionTool.convertDateToString(
      formModel,
      formSchema
    );
    return validator.validate(newModel);
  });

  // Converting Dates that are in string format into JS Dates so they can be correctly bound to the uniforms DateField
  const formData = ModelConversionTool.convertStringToDate(model, formSchema);

  return (
    <AutoForm
      placeholder
      model={formData}
      disabled={readOnly || formSubmitHandler.getActions().length === 0}
      schema={bridge}
      showInlineError={true}
      onSubmit={formModel => formSubmitHandler.doSubmit(formModel)}
    >
      <ErrorsField />
      <AutoFields />
      {!readOnly && <FormFooter actions={formSubmitHandler.getActions()} />}
    </AutoForm>
  );
};

export default FormRenderer;
