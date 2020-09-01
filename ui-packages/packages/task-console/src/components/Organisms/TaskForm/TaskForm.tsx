import React, { useContext, useEffect, useState } from 'react';
import axios from 'axios';
import {
  KogitoEmptyState,
  KogitoEmptyStateType,
  KogitoSpinner,
  GraphQL
} from '@kogito-apps/common';
import TaskConsoleContext, {
  IContext
} from '../../../context/TaskConsoleContext/TaskConsoleContext';
import FormNotification from '../../Atoms/FormNotification/FormNotification';
import FormRenderer from '../../Molecules/FormRenderer/FormRenderer';
import { TaskFormSubmitHandler } from '../../../util/uniforms/TaskFormSubmitHandler/TaskFormSubmitHandler';
import { FormSchema } from '../../../util/uniforms/FormSchema';
import UserTaskInstance = GraphQL.UserTaskInstance;
import { getTaskSchemaEndPoint } from '../../../util/Utils';

interface IOwnProps {
  userTaskInstance?: UserTaskInstance;
  successCallback?: () => void;
  errorCallback?: () => void;
}

interface AlertMessage {
  message: string;
  callback: () => void;
}

const TaskForm: React.FC<IOwnProps> = ({
  userTaskInstance,
  successCallback,
  errorCallback
}) => {
  // tslint:disable: no-floating-promises
  const context: IContext<UserTaskInstance> = useContext(TaskConsoleContext);
  const [alertMessage, setAlertMessage]: [AlertMessage, any] = useState(null);
  const [loading, setLoading] = useState(true);
  const [stateUserTask, setStateUserTask]: [UserTaskInstance, any] = useState(
    null
  );
  const [taskFormSchema, setTaskFormSchema]: [FormSchema, any] = useState(null);

  if (!stateUserTask) {
    if (userTaskInstance) {
      setStateUserTask(userTaskInstance);
    } else {
      if (context.getActiveItem()) {
        setStateUserTask(context.getActiveItem());
      }
    }
  }

  useEffect(() => {
    loadForm();
  }, [stateUserTask]);

  const loadForm = () => {
    if (stateUserTask) {
      const endpoint = getTaskSchemaEndPoint(stateUserTask);

      axios
        .get(endpoint, {
          headers: {
            'Content-Type': 'application/json',
            Accept: 'application/json',
            crossorigin: 'true',
            'Access-Control-Allow-Origin': '*'
          }
        })
        .then(res => {
          if (res.status === 200) {
            setTaskFormSchema(res.data);
          }
          setLoading(false);
        })
        .catch(er => {
          setLoading(false);
        });
    }
  };

  const showAlertMessage = (submitMessage: string, submitCallback?: any) => {
    setAlertMessage({
      message: submitMessage,
      callback: () => {
        setAlertMessage(null);
        if (submitCallback) {
          submitCallback();
        }
      }
    });
  };

  if (stateUserTask) {
    if (loading) {
      return (
        <KogitoSpinner
          spinnerText={'Loading form for task: ' + stateUserTask.name}
        />
      );
    }

    if (taskFormSchema) {
      const notifySuccess = (phase: string) => {
        const message = `Task '${userTaskInstance.id}' successfully transitioned to phase '${phase}'.`;
        showAlertMessage(message, successCallback);
      };

      const notifyError = (phase: string, error?: string) => {
        let message = `Task '${userTaskInstance.id}' couldn't transition to phase '${phase}'.`;

        if (error) {
          message += ` Error: '${error}'`;
        }

        showAlertMessage(message, errorCallback);
      };

      const formSubmitHandler = new TaskFormSubmitHandler(
        stateUserTask,
        taskFormSchema,
        context.getUser(),
        phase => notifySuccess(phase),
        (phase, errorMessage) => notifyError(phase, errorMessage)
      );

      const formData = JSON.parse(stateUserTask.inputs);

      return (
        <React.Fragment>
          {alertMessage && (
            <FormNotification
              message={alertMessage.message}
              closeAction={alertMessage.callback}
              ouiaId={'user-task-' + userTaskInstance.id}
            />
          )}
          <FormRenderer
            formSchema={taskFormSchema}
            model={formData}
            formSubmitHandler={formSubmitHandler}
          />
        </React.Fragment>
      );
    }
  }

  return (
    <KogitoEmptyState
      type={KogitoEmptyStateType.Info}
      title="No form to show"
      body="Cannot find form"
    />
  );
};

export default TaskForm;
