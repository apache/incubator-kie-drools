import React from 'react';
import { Tooltip } from '@patternfly/react-core';
import { GraphQL, OUIAProps, componentOuiaProps } from '@kogito-apps/common';
import ProcessInstance = GraphQL.ProcessInstance;

interface IOwnProps {
  processInstanceData: ProcessInstance;
  component: any;
}

const DisablePopup: React.FC<IOwnProps & OUIAProps> = ({
  processInstanceData,
  component,
  ouiaId,
  ouiaSafe
}) => {
  let content = '';
  if (
    !processInstanceData.addons.includes('process-management') &&
    processInstanceData.serviceUrl === null
  ) {
    content =
      'Management add-on capability not enabled & missing the kogito.service.url property. Contact your administrator to set up.';
  } else if (
    processInstanceData.serviceUrl === null &&
    processInstanceData.addons.includes('process-management')
  ) {
    content =
      'This Kogito runtime is missing the kogito.service.url property. Contact your administrator to set up.';
  } else if (
    !processInstanceData.addons.includes('process-management') &&
    processInstanceData.serviceUrl !== null
  ) {
    content =
      'Management add-on capability not enabled. Contact your administrator to set up';
  }
  return (
    <Tooltip
      content={content}
      distance={-15}
      {...componentOuiaProps(ouiaId, 'disable-popup', ouiaSafe)}
    >
      {component}
    </Tooltip>
  );
};

export default DisablePopup;
