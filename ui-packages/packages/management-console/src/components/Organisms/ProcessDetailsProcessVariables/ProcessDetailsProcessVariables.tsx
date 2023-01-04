import {
  Card,
  CardBody,
  CardHeader,
  TextContent,
  Title,
  Label
} from '@patternfly/react-core';
import React from 'react';
import ReactJson from 'react-json-view';
import { GraphQL } from '@kogito-apps/common';
import { OUIAProps, componentOuiaProps } from '@kogito-apps/ouia-tools';
import { InfoCircleIcon } from '@patternfly/react-icons';
import './ProcessDetailsProcessVariables.css';

interface IOwnProps {
  displayLabel: boolean;
  displaySuccess: boolean;
  setDisplayLabel: (displayLabel: boolean) => void;
  setUpdateJson: (
    updateJson: (variableJson: Record<string, unknown>) => void
  ) => void;
  updateJson: Record<string, unknown>;
  processInstance: { __typename?: 'ProcessInstance' } & Pick<
    GraphQL.ProcessInstance,
    'state'
  >;
}

const ProcessDetailsProcessVariables: React.FC<IOwnProps & OUIAProps> = ({
  displayLabel,
  displaySuccess,
  ouiaId,
  ouiaSafe,
  setDisplayLabel,
  setUpdateJson,
  updateJson,
  processInstance
}) => {
  const handleVariablesChange = (e) => {
    setUpdateJson({ ...updateJson, ...e.updated_src });
    setDisplayLabel(true);
  };
  const checkProcessStatus =
    processInstance.state === GraphQL.ProcessInstanceState.Completed ||
    processInstance.state === GraphQL.ProcessInstanceState.Aborted
      ? false
      : handleVariablesChange;

  return (
    <Card {...componentOuiaProps(ouiaId, 'process-variables', ouiaSafe)}>
      <CardHeader>
        <Title headingLevel="h3" size="xl">
          Process Variables
        </Title>
        {displayLabel && (
          <Label color="orange" icon={<InfoCircleIcon />}>
            {' '}
            Changes are not saved yet
          </Label>
        )}
        <Label
          color="green"
          icon={<InfoCircleIcon />}
          className={
            displaySuccess
              ? 'kogito-management-console--variables__label-fadeIn'
              : 'kogito-management-console--variables__label-fadeOut'
          }
        >
          {' '}
          Changes are saved
        </Label>
      </CardHeader>
      <CardBody>
        <TextContent>
          <div>
            <ReactJson
              src={updateJson}
              name={false}
              onEdit={checkProcessStatus}
              onAdd={checkProcessStatus}
              onDelete={checkProcessStatus}
            />
          </div>
        </TextContent>
      </CardBody>
    </Card>
  );
};

export default React.memo(ProcessDetailsProcessVariables);
