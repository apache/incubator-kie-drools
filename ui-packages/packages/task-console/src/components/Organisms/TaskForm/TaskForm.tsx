import React, { useContext, useEffect, useState } from 'react';

import { TaskInfo } from '../../../model/TaskInfo';
import FormRenderer from '../../Molecules/FormRenderer/FormRenderer';
import { FormDescription } from '../../../model/FormDescription';
import TaskConsoleContext, {
  IContext
} from '../../../context/TaskConsoleContext/TaskConsoleContext';

import { isEmpty } from 'lodash';
import axios from 'axios';
import {
  KogitoEmptyState,
  KogitoEmptyStateType,
  KogitoSpinner
} from '@kogito-apps/common';
import {
  BaseSizes,
  Button,
  Modal,
  Text,
  TextContent,
  Title,
  TitleLevel
} from '@patternfly/react-core';

interface IOwnProps {
  taskInfo?: TaskInfo;
  successCallback?: () => void;
  errorCallback?: () => void;
}

interface AlertMessage {
  message: string;
  callback: () => void;
}

const TaskForm: React.FC<IOwnProps> = ({
  taskInfo,
  successCallback,
  errorCallback
}) => {
  // tslint:disable: no-floating-promises
  const context: IContext<TaskInfo> = useContext(TaskConsoleContext);
  const [alertMessage, setAlertMessage]: [AlertMessage, any] = useState(null);
  const [loading, setLoading] = useState(true);
  const [userTaskInfo, setUserTaskInfo]: [TaskInfo, any] = useState(null);
  const [taskFormDescription, setTaskFormDescription]: [
    FormDescription,
    any
  ] = useState(null);

  if (!userTaskInfo) {
    if (taskInfo) {
      setUserTaskInfo(taskInfo);
    } else {
      if (context.getActiveItem()) {
        setUserTaskInfo(context.getActiveItem());
      }
    }
  }

  useEffect(() => {
    loadForm();
  }, [userTaskInfo]);

  const loadForm = () => {
    if (userTaskInfo) {
      axios
        .get(userTaskInfo.getTaskEndPoint() + '/form', {
          headers: {
            'Content-Type': 'application/json',
            Accept: 'application/json',
            crossorigin: 'true',
            'Access-Control-Allow-Origin': '*'
          }
        })
        .then(res => {
          if (res.status === 200) {
            setTaskFormDescription(res.data);
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

  if (userTaskInfo) {
    if (loading) {
      return (
        <KogitoSpinner
          spinnerText={'Loading form for task: ' + userTaskInfo.task.name}
        />
      );
    }

    if (taskFormDescription) {
      const outputs = JSON.parse(userTaskInfo.task.outputs);
      const formData = !isEmpty(outputs)
        ? outputs
        : JSON.parse(userTaskInfo.task.inputs);

      return (
        <React.Fragment>
          {alertMessage && (
            <Modal
              isSmall={true}
              title=""
              header={
                <Title headingLevel={TitleLevel.h1} size={BaseSizes['2xl']}>
                  Executing Task
                </Title>
              }
              isOpen={true}
              onClose={alertMessage.callback}
              actions={[
                <Button
                  key="confirm-selection"
                  variant="primary"
                  onClick={alertMessage.callback}
                >
                  OK
                </Button>
              ]}
              isFooterLeftAligned={false}
            >
              <TextContent>
                <Text>{alertMessage.message}</Text>
              </TextContent>
            </Modal>
          )}
          <FormRenderer
            taskInfo={taskInfo}
            form={taskFormDescription}
            model={formData}
            successCallback={result =>
              showAlertMessage(result, successCallback)
            }
            errorCallback={result => showAlertMessage(result, errorCallback)}
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
