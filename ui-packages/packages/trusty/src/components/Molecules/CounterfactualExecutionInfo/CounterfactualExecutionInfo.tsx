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
import React, { useEffect, useMemo, useState } from 'react';
import { Badge, FlexItem } from '@patternfly/react-core';
import { v4 as uuid } from 'uuid';
import FormattedDate from '../../Atoms/FormattedDate/FormattedDate';
import './CounterfactualExecutionInfo.scss';

type CounterfactualExecutionInfoProps = {
  resultsCount: number;
};

const CounterfactualExecutionInfo = (
  props: CounterfactualExecutionInfoProps
) => {
  const { resultsCount } = props;
  const executionDate = useMemo(() => new Date().toISOString(), []);
  const [id, setId] = useState(uuid());

  useEffect(() => {
    const timer = setInterval(() => {
      setId(uuid());
    }, 61000);
    return () => {
      clearInterval(timer);
    };
  }, []);

  return (
    <>
      <FlexItem>
        <span className="cf-execution-info">
          <span className="cf-execution-info__label">Completed</span>
          <span key={id}>
            <FormattedDate date={executionDate} />
          </span>
        </span>
      </FlexItem>
      <FlexItem>
        <span className="cf-execution-info cf-execution-info__results">
          <span className="cf-execution-info__label">Total Results</span>
          <Badge>{resultsCount}</Badge>
        </span>
      </FlexItem>
    </>
  );
};

export default CounterfactualExecutionInfo;
