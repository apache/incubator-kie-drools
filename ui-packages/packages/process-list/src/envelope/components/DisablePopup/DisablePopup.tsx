/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import React, { ReactElement } from 'react';
import { Tooltip } from '@patternfly/react-core/dist/js/components/Tooltip';
import { ProcessInstance } from '@kogito-apps/management-console-shared/dist/types';
import {
  componentOuiaProps,
  OUIAProps
} from '@kogito-apps/ouia-tools/dist/utils/OuiaUtils';

interface DisablePopupProps {
  processInstanceData: ProcessInstance;
  component: ReactElement;
}

const DisablePopup: React.FC<DisablePopupProps & OUIAProps> = ({
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
      id="disable-popup-tooltip"
      {...componentOuiaProps(ouiaId, 'disable-popup', ouiaSafe)}
    >
      {component}
    </Tooltip>
  );
};

export default DisablePopup;
