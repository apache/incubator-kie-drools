/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
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
        {messages.map((message) => (
          <p key={message.id} data-ouia-component-id={`${message.id}`}>
            {message.message}
          </p>
        ))}
      </EmptyStateBody>
    </EmptyState>
  );
};

export default CounterfactualUnsupported;
