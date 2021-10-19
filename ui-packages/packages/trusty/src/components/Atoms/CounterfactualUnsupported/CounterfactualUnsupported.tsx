import React from 'react';
import {
  EmptyState,
  EmptyStateBody,
  EmptyStateIcon,
  EmptyStateVariant,
  Title
} from '@patternfly/react-core';
import { InfoCircleIcon } from '@patternfly/react-icons';
import { CFSupportMessage } from '../../../types';

type CounterfactualUnsupportedProps = {
  messages?: CFSupportMessage[];
};

const CounterfactualUnsupported = (props: CounterfactualUnsupportedProps) => {
  const { messages } = props;

  return (
    <EmptyState variant={EmptyStateVariant.full}>
      <EmptyStateIcon icon={InfoCircleIcon} />
      <Title headingLevel="h1">Counterfactuals cannot be generated</Title>
      <EmptyStateBody>
        {messages.map(message => (
          <p key={message.id} data-ouia-component-id={`${message.id}`}>
            {message.message}
          </p>
        ))}
      </EmptyStateBody>
    </EmptyState>
  );
};

export default CounterfactualUnsupported;
