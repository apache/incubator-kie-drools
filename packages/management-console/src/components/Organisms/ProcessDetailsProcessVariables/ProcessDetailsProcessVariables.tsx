import {
  Card,
  CardBody,
  CardHeader,
  TextContent,
  Title
} from '@patternfly/react-core';
import React from 'react';
import ReactJson from 'react-json-view';

const ProcessDetailsProcessVariables = ({ data }) => {
  return (
    <Card>
      <CardHeader>
        <Title headingLevel="h3" size="xl">
          Process Variables
        </Title>
      </CardHeader>
      <CardBody>
        <TextContent>
          {data.ProcessInstances.map((item, index) => {
            return (
              <div key={index}>
                <ReactJson src={JSON.parse(item.variables)} />
              </div>
            );
          })}
        </TextContent>
      </CardBody>
    </Card>
  );
};

export default ProcessDetailsProcessVariables;
