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
import React, { useEffect, useMemo, useRef, useState } from 'react';
import { Progress, ProgressSize } from '@patternfly/react-core';

type CounterfactualProgressBarProps = {
  maxRunningTimeSeconds: number;
};

const CounterfactualProgressBar = (props: CounterfactualProgressBarProps) => {
  const { maxRunningTimeSeconds } = props;

  const [value, setValue] = useState(0);
  const [timeLimit, setTimeLimit] = useState(-1);
  const intervalID = useRef(null);

  useEffect(() => {
    if (value === 0 && intervalID.current === null) {
      intervalID.current = window.setInterval(() => {
        setValue((prev) => prev + 1);
      }, 1000);
    }
    if (value === timeLimit) {
      clearInterval(intervalID.current);
    }
  }, [value, intervalID]);

  useEffect(() => {
    setTimeLimit(maxRunningTimeSeconds);
  }, [maxRunningTimeSeconds]);

  const label = useMemo(() => {
    if (timeLimit) {
      return timeLimit - value !== 0
        ? `${timeLimit - value} seconds remaining`
        : 'Wrapping up';
    }
    return `Pending...`;
  }, [value]);

  return (
    <Progress
      value={(value * 100) / timeLimit}
      title="Calculating..."
      size={ProgressSize.sm}
      style={{ width: 400 }}
      label={label}
    />
  );
};

export default CounterfactualProgressBar;
