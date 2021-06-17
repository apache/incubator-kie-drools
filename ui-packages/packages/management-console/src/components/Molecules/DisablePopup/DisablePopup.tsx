import React, { ReactElement } from 'react';
import { Tooltip } from '@patternfly/react-core';
import { GraphQL } from '@kogito-apps/common';
import { OUIAProps, componentOuiaProps } from '@kogito-apps/ouia-tools';
import ProcessInstance = GraphQL.ProcessInstance;

interface IOwnProps {
  processInstanceData: ProcessInstance;
  component: ReactElement;
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
      {...componentOuiaProps(ouiaId, 'disable-popup', ouiaSafe)}
    >
      {component}
    </Tooltip>
  );
};

export default DisablePopup;
