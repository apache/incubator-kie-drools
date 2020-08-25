import {
  Card,
  CardBody,
  CardHeader,
  TextContent,
  Title
} from '@patternfly/react-core';
import React from 'react';
import ReactJson from 'react-json-view';
import { GraphQL, OUIAProps, componentOuiaProps } from '@kogito-apps/common';
import ProcessInstance = GraphQL.ProcessInstance;

interface IOwnProps {
  data: {
    ProcessInstances?: Pick<ProcessInstance, 'variables'>[];
  };
}

const ProcessDetailsProcessVariables: React.FC<IOwnProps & OUIAProps> = ({
  data,
  ouiaId,
  ouiaSafe
}) => {
  return (
    <Card {...componentOuiaProps(ouiaId, 'process-variables', ouiaSafe)}>
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
