import { Card, CardBody, CardHeader, Title } from '@patternfly/react-core';
import React from 'react';
import { url } from './Url';

const ProcessDetailsProcessDiagram = () => {
  return (
    <Card>
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
