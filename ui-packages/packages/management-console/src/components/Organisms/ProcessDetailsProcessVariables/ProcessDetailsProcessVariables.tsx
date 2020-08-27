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
import { OUIAProps, componentOuiaProps } from '@kogito-apps/common';
import { InfoCircleIcon } from '@patternfly/react-icons';
import './ProcessDetailsProcessVariables.css';

interface IOwnProps {
  displayLabel: boolean;
  displaySuccess: boolean;
  setDisplayLabel: (displayLabel: boolean) => void;
  setUpdateJson: (updateJson: (variableJson: object) => void) => void;
  updateJson: object;
}

const ProcessDetailsProcessVariables: React.FC<IOwnProps & OUIAProps> = ({
  displayLabel,
  displaySuccess,
  ouiaId,
  ouiaSafe,
  setDisplayLabel,
  setUpdateJson,
  updateJson
}) => {
  const onEdit = e => {
    setUpdateJson({ ...updateJson, ...e.updated_src });
    setDisplayLabel(true);
  };

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
            <ReactJson src={updateJson} name={false} onEdit={onEdit} />
          </div>
        </TextContent>
      </CardBody>
    </Card>
  );
};

export default React.memo(ProcessDetailsProcessVariables);
