import React from 'react';
import { CheckCircleIcon, ErrorCircleOIcon } from '@patternfly/react-icons';
import './ExecutionStatus.scss';

type ExecutionStatusProps = {
  result: 'success' | 'failure';
};

const ExecutionStatus = (props: ExecutionStatusProps) => {
  const { result } = props;
  return (
    <span className="execution-status">
      {result === 'success' && (
        <>
          <CheckCircleIcon
            className={
              'execution-status__badge execution-status__badge--success'
            }
          />
          <span>Completed</span>
        </>
      )}
      {result === 'failure' && (
        <>
          <ErrorCircleOIcon
            className={'execution-status__badge execution-status__badge--error'}
          />
          <span>Error</span>
        </>
      )}
    </span>
  );
};

export default ExecutionStatus;
