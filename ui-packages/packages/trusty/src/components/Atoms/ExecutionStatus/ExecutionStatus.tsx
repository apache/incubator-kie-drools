import React from 'react';
import { CheckCircleIcon, ErrorCircleOIcon } from '@patternfly/react-icons';
import './ExecutionStatus.scss';
import {
  OUIAProps,
  componentOuiaProps
} from '@kogito-apps/ouia-tools/dist/utils/OuiaUtils';

type ExecutionStatusProps = {
  result: 'success' | 'failure';
};

const ExecutionStatus: React.FC<ExecutionStatusProps & OUIAProps> = ({
  result,
  ouiaId,
  ouiaSafe
}) => {
  return (
    <span
      className="execution-status"
      {...componentOuiaProps(ouiaId, 'execution-status', ouiaSafe)}
    >
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
