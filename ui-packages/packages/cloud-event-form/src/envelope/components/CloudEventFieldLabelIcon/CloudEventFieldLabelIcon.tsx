/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import React from 'react';
import HelpIcon from '@patternfly/react-icons/dist/esm/icons/help-icon';
import { Popover } from '@patternfly/react-core/dist/js/components/Popover';

export interface CloudEventFieldLabelIconProps {
  fieldId: string;
  helpMessage: string | JSX.Element;
  cloudEventHeader?: string;
}

const CloudEventFieldLabelIcon: React.FC<CloudEventFieldLabelIconProps> = ({
  fieldId,
  helpMessage,
  cloudEventHeader
}) => {
  return (
    <Popover
      id={`cloudEvent-form-${fieldId}-help`}
      bodyContent={
        <div>
          <div>{helpMessage}</div>
          {cloudEventHeader && (
            <div>
              The value will be set in the{' '}
              <span
                className={'pf-u-success-color-100'}
              >{`'${cloudEventHeader}'`}</span>{' '}
              header.
            </div>
          )}
        </div>
      }
    >
      <button
        type="button"
        aria-label={`More info for ${fieldId} field`}
        onClick={(e) => e.preventDefault()}
        className="pf-c-form__group-label-help"
      >
        <HelpIcon noVerticalAlign />
      </button>
    </Popover>
  );
};

export default CloudEventFieldLabelIcon;
