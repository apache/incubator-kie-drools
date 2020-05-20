import Moment from 'react-moment';
import React, {useCallback, useState, useEffect} from 'react';
import {
  Alert,
  AlertActionCloseButton,
  Button,
  DataListAction,
  DataListCell,
  DataListItem,
  DataListItemCells,
  DataListItemRow,
} from '@patternfly/react-core';
import {useGetProcessInstanceByIdLazyQuery} from '../../../graphql/types';
import axios from "axios";

/* tslint:disable:no-string-literal */

interface IUserTaskInstance {
  id: string;
  description: string;
  name: string;
  priority: string;
  processInstanceId: string;
  processId: string;
  rootProcessInstanceId,
  rootProcessId,
  state: string;
  actualOwner: string;
  adminGroups: string;
  adminUsers: string;
  completed: boolean;
  started: string;
  excludedUsers: string;
  potentialGroups: string;
  potentialUsers: string;
  inputs: string;
  outputs: string;
  referenceName: string;
}

export interface IOwnProps {
  id: number;
  userTaskInstanceData: IUserTaskInstance;
}

const DataListItemComponent: React.FC<IOwnProps> = ({
  userTaskInstanceData
}) => {
  const [isPiLoaded, setPiLoaded] = useState(false);
  const [alertVisible, setAlertVisible] = useState(false);
  const [alertTitle, setAlertTitle] = useState('');
  const [alertType, setAlertType] = useState(null);
  const [alertMessage, setAlertMessage] = useState('');

  const [getProcessInstance, {loading,data}] = useGetProcessInstanceByIdLazyQuery({
    fetchPolicy: 'network-only'
  });

  const handleExecuteTask = useCallback(
    async (_taskID, _taskReferenceName, _processID, _instanceID, _endpoint) => {
      const taskId = userTaskInstanceData.id;
      const taskReferenceName = userTaskInstanceData.referenceName;
      const processId = userTaskInstanceData.processId;
      const processInstanceId = userTaskInstanceData.processInstanceId;

      try {
         // @ts-ignore
        const result = await axios.post(`${_endpoint}/${processId}/${processInstanceId}/${taskReferenceName}/${taskId}`,
          {},
          {
            headers: {
              'Content-Type': 'application/json',
              'Accept': 'application/json',
              'crossorigin': 'true',
              'Access-Control-Allow-Origin': '*'
            }
          });
        setAlertTitle('Executing task');
        setAlertType('success');
        setAlertMessage(
          'Task has successfully executed.' + `${_endpoint}/${processId}/${processInstanceId}/${taskReferenceName}/${taskId}`
        );
        setAlertVisible(true);
      } catch (error) {
        setAlertTitle('Executing task');
        setAlertType('danger');
        setAlertMessage(
          'Task execution failed. Message: ' + `${_endpoint}/${processId}/${processInstanceId}/${taskReferenceName}/${taskId}` +
          JSON.stringify(error.message)
        );
        setAlertVisible(true);
      }
    },
    []
  );

  const closeAlert = () => {
    setAlertVisible(false);
  };

  if (!isPiLoaded && userTaskInstanceData.state === 'Ready') {
    getProcessInstance({
      variables: {
        id: userTaskInstanceData.processInstanceId
      }
    });
    setPiLoaded(true)
  }

  useEffect(() => {
    if (!loading && data !== undefined) {
      setPiLoaded(true);
    }
  }, [data]);

  return (
    <React.Fragment>
      {alertVisible && (
        <Alert
          variant={alertType}
          title={alertTitle}
          action={<AlertActionCloseButton onClose={() => closeAlert()}/>}
        >
          {alertMessage}
        </Alert>
      )}
      <DataListItem
        aria-labelledby="kie-datalist-item"
      >
        <DataListItemRow>
          <DataListItemCells
            dataListCells={[
              <DataListCell key={1}>
                {userTaskInstanceData.name}
              </DataListCell>,
              <DataListCell key={2}>
                {userTaskInstanceData.started ? (
                  <Moment fromNow>{new Date(`${userTaskInstanceData.started}`)}</Moment>
                ) : (
                  ''
                )}
              </DataListCell>,
              <DataListCell key={3}>{userTaskInstanceData.processId}</DataListCell>,
              <DataListCell key={4}>{userTaskInstanceData.processInstanceId}</DataListCell>,
              <DataListCell key={5}>{userTaskInstanceData.state}</DataListCell>
            ]}
          />

          <DataListAction
            aria-labelledby="kie-datalist-item kie-datalist-action"
            id="kie-datalist-action"
            aria-label="Actions"
          >
            <Button variant="secondary"
                    isDisabled={!isPiLoaded}
                    onClick={() =>
                      handleExecuteTask(
                        userTaskInstanceData.id,
                        userTaskInstanceData.referenceName,
                        userTaskInstanceData.processId,
                        userTaskInstanceData.processInstanceId,
                        data.ProcessInstances[0].endpoint
                      )
                    }
            > Complete </Button>
          </DataListAction>
        </DataListItemRow>
      </DataListItem>
    </React.Fragment>
  );
};

export default DataListItemComponent;
