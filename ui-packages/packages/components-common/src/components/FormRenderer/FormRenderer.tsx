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
import React, { useImperativeHandle, useState } from 'react';
import JSONSchemaBridge from 'uniforms-bridge-json-schema';
import {
  AutoFields,
  AutoForm,
  ErrorsField
} from 'uniforms-patternfly/dist/es6';
import {
  componentOuiaProps,
  OUIAProps
} from '@kogito-apps/ouia-tools/dist/utils/OuiaUtils';
import { FormAction, lookupValidator, ModelConversionTool } from '../utils';
import { FormFooter } from '../FormFooter/FormFooter';
import '../styles.css';
import { FormRendererApi } from '../../types/types';
interface IOwnProps {
  formSchema: any;
  model?: any;
  onSubmit?: (data: any) => void;
  formActions?: FormAction[];
  readOnly: boolean;
}

export const FormRenderer = React.forwardRef<
  FormRendererApi,
  IOwnProps & OUIAProps
>(
  (
    { formSchema, model, onSubmit, formActions, readOnly, ouiaId, ouiaSafe },
    forwardedRef
  ) => {
    const validator = lookupValidator(formSchema);
    const [formApiRef, setFormApiRef] = useState(null);

    useImperativeHandle(
      forwardedRef,
      () => {
        return {
          doReset() {
            formApiRef.reset();
          }
        };
      },
      [formApiRef]
    );

    const bridge = new JSONSchemaBridge(formSchema, (formModel) => {
      // Converting back all the JS Dates into String before validating the model
      const newModel = ModelConversionTool.convertDateToString(
        formModel,
        formSchema
      );
      return validator.validate(newModel);
    });

    // Converting Dates that are in string format into JS Dates so they can be correctly bound to the uniforms DateField
    const formData = ModelConversionTool.convertStringToDate(model, formSchema);

    const submitFormData = (): void => {
      formApiRef.submit();
    };

    return (
      <React.Fragment>
        <AutoForm
          ref={(ref) => setFormApiRef(ref)}
          placeholder
          model={formData}
          disabled={readOnly}
          schema={bridge}
          showInlineError={true}
          onSubmit={(data) => onSubmit(data)}
          {...componentOuiaProps(ouiaId, 'form-renderer', ouiaSafe)}
        >
          <ErrorsField />
          <AutoFields />
        </AutoForm>
        <FormFooter
          actions={formActions}
          enabled={!readOnly}
          onSubmitForm={submitFormData}
        />
      </React.Fragment>
    );
  }
);
