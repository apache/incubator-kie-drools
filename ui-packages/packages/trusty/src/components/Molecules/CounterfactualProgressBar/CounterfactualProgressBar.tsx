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
        setValue(prev => prev + 1);
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
