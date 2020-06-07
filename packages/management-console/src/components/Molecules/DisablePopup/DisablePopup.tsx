import React from 'react';
import { Tooltip } from '@patternfly/react-core';
import { GraphQL } from '@kogito-apps/common';
import ProcessInstance = GraphQL.ProcessInstance;

interface IOwnProps {
  processInstanceData: ProcessInstance;
  component: any;
}

const DisablePopup: React.FC<IOwnProps> = ({
  processInstanceData,
  component
}) => {
  if (
    !processInstanceData.addons.includes('process-management') &&
    processInstanceData.serviceUrl === null
  ) {
    return (
      <Tooltip
        content={
          'Management add-on capability not enabled & missing the kogito.service.url property. Contact your administrator to set up.'
        }
        distance={-15}
      >
        {component}
      </Tooltip>
    );
  } else if (
    processInstanceData.serviceUrl === null &&
    processInstanceData.addons.includes('process-management')
  ) {
    return (
      <Tooltip
        content={
          'This Kogito runtime is missing the kogito.service.url property. Contact your administrator to set up.'
        }
        distance={-15}
      >
        {component}
      </Tooltip>
    );
  } else if (
    !processInstanceData.addons.includes('process-management') &&
    processInstanceData.serviceUrl !== null
  ) {
    return (
      <Tooltip
        content={
          'Management add-on capability not enabled. Contact your administrator to set up'
        }
        distance={-15}
      >
        {component}
      </Tooltip>
    );
  } else {
    return (
      <Tooltip content={''} distance={-15}>
        {component}
      </Tooltip>
    );
  }
};

export default DisablePopup;
