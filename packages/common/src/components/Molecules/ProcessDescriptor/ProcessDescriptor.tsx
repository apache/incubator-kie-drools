import React from 'react';
import {
  Tooltip,
  Badge,
  TextContent,
  Text,
  TextVariants
} from '@patternfly/react-core';
import { GraphQL } from '../../../graphql/types';
import ProcessInstance = GraphQL.ProcessInstance;

interface IOwnProps {
  processInstanceData: Pick<
    ProcessInstance,
    'id' | 'processName' | 'businessKey'
  >;
}
const ProcessDescriptor: React.FC<IOwnProps> = ({ processInstanceData }) => {
  const idStringModifier = (strId: string) => {
    return (
      <TextContent className="pf-u-display-inline">
        <Text component={TextVariants.small} className="pf-u-display-inline">
          {strId.substring(0, 5)}
        </Text>
      </TextContent>
    );
  };
  return (
    <>
      <Tooltip content={processInstanceData.id}>
        <span>
          {processInstanceData.processName}{' '}
          {processInstanceData.businessKey ? (
            <Badge>{processInstanceData.businessKey}</Badge>
          ) : (
            idStringModifier(processInstanceData.id)
          )}
        </span>
      </Tooltip>
    </>
  );
};

export default ProcessDescriptor;
