/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

import React from 'react';
import JSONSchemaBridge from 'uniforms-bridge-json-schema';
import { AutoFields, AutoForm, ErrorsField } from 'uniforms-patternfly';
import FormFooter from '../../Atoms/FormFooter/FormFooter';
import ModelConversionTool from '../../../util/uniforms/ModelConversionTool';
import { DefaultFormValidator } from '../../../util/uniforms/FormValidator';
import { FormSubmitHandler } from '../../../util/uniforms/FormSubmitHandler/FormSubmitHandler';
import { componentOuiaProps, OUIAProps } from '@kogito-apps/common';

interface IOwnProps {
  formSchema: any;
  model?: any;
  readOnly: boolean;
  formSubmitHandler: FormSubmitHandler;
}

const FormRenderer: React.FC<IOwnProps & OUIAProps> = ({
  formSchema,
  model,
  readOnly,
  formSubmitHandler,
  ouiaId,
  ouiaSafe
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
      {...componentOuiaProps(ouiaId, 'form-renderer', ouiaSafe)}
    >
      <ErrorsField />
      <AutoFields />
      {!readOnly && <FormFooter actions={formSubmitHandler.getActions()} />}
    </AutoForm>
  );
};

export default FormRenderer;
