import React, { useCallback } from 'react';
import axios from 'axios';
import JSONSchemaBridge from 'uniforms-bridge-json-schema';
import { AutoFields, AutoForm, ErrorsField } from 'uniforms-patternfly';
import FormFooter from '../../Atoms/FormFooter/FormFooter';
import {
  FormActionDescription,
  FormDescription
} from '../../../model/FormDescription';
import { TaskInfo } from '../../../model/TaskInfo';
import ModelConversionTool from '../../../util/uniforms/ModelConversionTool';
import { DefaultFormValidator } from '../../../util/uniforms/FormValidator';

interface IOwnProps {
  taskInfo: TaskInfo;
  form: FormDescription;
  model?: any;
  successCallback?: (result: string) => void;
  errorCallback?: (errorMessage: string) => void;
}

const FormRenderer: React.FC<IOwnProps> = ({
  taskInfo,
  form,
  model,
  successCallback,
  errorCallback
}) => {
  const validator = new DefaultFormValidator(form.schema);

  const bridge = new JSONSchemaBridge(form.schema, formModel => {
    // Converting back all the JS Dates into String before validating the model
    const newModel = ModelConversionTool.convertDateToString(
      formModel,
      form.schema
    );
    return validator.validate(newModel);
  });

  // Converting Dates that are in string format into JS Dates so they can be correctly bound to the uniforms DateField
  const formData = ModelConversionTool.convertStringToDate(model, form.schema);

  let selectedAction;

  const notifySuccess = (text: string) => {
    if (successCallback) {
      successCallback(text);
    }
  };

  const notifyError = (formAction: FormActionDescription, text?: string) => {
    if (errorCallback) {
      const message = text
        ? text
        : `Unexpected error executing '${formAction.name}'`;
      errorCallback(message);
    }
  };

  const submitForm = useCallback(
    async (formModel: any, formAction: FormActionDescription) => {
      try {
        const data = {};

        let endpoint = taskInfo.getTaskEndPoint();

        if (formAction.phase) {
          endpoint += '?phase=' + formAction.phase;
        }

        formAction.outputs.forEach(output => {
          if (formModel[output]) {
            data[output] = formModel[output];
          }
        });

        const response = await axios.post(endpoint, data, {
          headers: {
            'Content-Type': 'application/json',
            Accept: 'application/json',
            crossorigin: 'true',
            'Access-Control-Allow-Origin': '*'
          }
        });

        if (response.status === 200) {
          if (response.data) {
            notifySuccess(response.data);
          } else {
            notifyError(formAction);
          }
        } else {
          notifyError(formAction, response.data);
        }
      } catch (e) {
        const message = e.response ? e.response.data : e.message;
        notifyError(formAction, message);
      }
    },
    []
  );

  let formActions = [];

  // Adding actions if the task isn't completed
  if (taskInfo.task.state !== 'Completed' && form.actions) {
    formActions = form.actions.map(action => {
      return {
        name: action.name,
        primary: action.primary,
        onActionClick: () => (selectedAction = action)
      };
    });
  }

  return (
    <AutoForm
      placeholder
      model={formData}
      disabled={formActions.length === 0}
      schema={bridge}
      showInlineError={true}
      onSubmit={formModel => submitForm(formModel, selectedAction)}
    >
      <ErrorsField />
      <AutoFields />
      <FormFooter actions={formActions} />
    </AutoForm>
  );
};

export default FormRenderer;
