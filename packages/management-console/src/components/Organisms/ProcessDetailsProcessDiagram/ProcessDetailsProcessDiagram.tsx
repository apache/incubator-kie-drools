import { Card, CardBody, CardHeader } from '@patternfly/react-core';
import React from 'react';
import { url } from './Url';

const ProcessDetailsProcessDiagram = () => {
  return (
    <Card style={{ overflowX: 'auto', overflowY: 'auto' }}>
      <CardHeader>Process Diagram</CardHeader>
      <CardBody>
        <img src={url} />
      </CardBody>
    </Card>
  );
};

export default ProcessDetailsProcessDiagram;
