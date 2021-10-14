import React from 'react';
import './CounterfactualUnsupportedBanner.scss';
import { Alert } from '@patternfly/react-core';

const CounterfactualUnsupportedBanner = () => {
  return (
    <Alert
      className="cf-unsupported-banner"
      variant="info"
      isInline={true}
      title="Counterfactuals is an experimental feature and does not currently support all types of model."
    />
  );
};

export default CounterfactualUnsupportedBanner;
