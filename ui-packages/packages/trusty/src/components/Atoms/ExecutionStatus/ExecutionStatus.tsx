import React from 'react';
import { CheckCircleIcon, ErrorCircleOIcon } from '@patternfly/react-icons';
import './ExecutionStatus.scss';

type ExecutionStatusProps = {
  result: 'success' | 'failure';
};

const ExecutionStatus = (props: ExecutionStatusProps) => {
  const { result } = props;
  return (
    <>
      {result === 'success' && (
        <>
          <CheckCircleIcon
            className={'execution-status-badge execution-status-badge--success'}
          />
          <span>Completed</span>
        </>
      )}
      {result === 'failure' && (
        <>
          <ErrorCircleOIcon
            className={'execution-status-badge execution-status-badge--error'}
          />
          <span>Error</span>
        </>
      )}
    </>
  );
};

export default ExecutionStatus;
