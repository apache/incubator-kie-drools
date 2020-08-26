import React from 'react';
import { evaluationStatus, evaluationStatusStrings } from '../../../types';
import {
  CheckCircleIcon,
  HourglassHalfIcon,
  MinusCircleIcon,
  ErrorCircleOIcon,
  FastForwardIcon
} from '@patternfly/react-icons';
import { Label } from '@patternfly/react-core';
import './EvaluationStatus.scss';

type EvaluationStatusProps = {
  status: evaluationStatusStrings;
};

const EvaluationStatus = (props: EvaluationStatusProps) => {
  const { status } = props;
  const label = evaluationStatus[status];
  let evaluationIcon;
  let evaluationColor;

  switch (status) {
    case 'EVALUATING':
      evaluationIcon = <HourglassHalfIcon />;
      evaluationColor = 'orange';
      break;
    case 'FAILED':
      evaluationIcon = <ErrorCircleOIcon />;
      evaluationColor = 'red';
      break;
    case 'SKIPPED':
      evaluationIcon = <FastForwardIcon />;
      evaluationColor = 'red';
      break;
    case 'NOT_EVALUATED':
      evaluationIcon = <MinusCircleIcon />;
      evaluationColor = 'red';
      break;
    case 'SUCCEEDED':
      evaluationIcon = <CheckCircleIcon />;
      evaluationColor = 'green';
      break;
  }

  return (
    <Label color={evaluationColor} icon={evaluationIcon}>
      <span>{label}</span>
    </Label>
  );
};

export default EvaluationStatus;
