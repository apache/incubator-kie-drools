import { Card, CardBody, CardHeader, Title } from '@patternfly/react-core';
import React from 'react';
import { url } from './Url';
import { OUIAProps, componentOuiaProps } from '@kogito-apps/common';

const ProcessDetailsProcessDiagram: React.FC<OUIAProps> = ({
  ouiaId,
  ouiaSafe
}) => {
  return (
    <Card {...componentOuiaProps(ouiaId, 'process-diagram', ouiaSafe)}>
      <CardHeader>
        <Title headingLevel="h3" size="xl">
          Process Diagram
        </Title>
      </CardHeader>
      <CardBody>
        <img src={url} />
      </CardBody>
    </Card>
  );
};

export default ProcessDetailsProcessDiagram;
