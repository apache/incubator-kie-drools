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
  Card,
  CardBody,
  EmptyState,
  EmptyStateBody,
  EmptyStateIcon,
  Title
} from '@patternfly/react-core';
import { ExclamationCircleIcon } from '@patternfly/react-icons';

type ExplanationErrorProps = {
  statusDetail?: string;
};

const ExplanationError = ({ statusDetail }: ExplanationErrorProps) => {
  return (
    <Card>
      <CardBody>
        <EmptyState>
          <EmptyStateIcon icon={ExclamationCircleIcon} color="#C9190B" />
          <Title headingLevel="h4" size="lg">
            Explanation Error
          </Title>
          <EmptyStateBody>
            <p>
              There was an error calculating explanation information for this
              execution.
            </p>
            {statusDetail && (
              <p>
                Error Message:{' '}
                <span className="explanation-error-detail">{statusDetail}</span>
              </p>
            )}
          </EmptyStateBody>
        </EmptyState>
      </CardBody>
    </Card>
  );
};

export default ExplanationError;
