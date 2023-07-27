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

import React from 'react';
import {
  TextContent,
  Text,
  TextVariants
} from '@patternfly/react-core/dist/js/components/Text';
import { Tooltip } from '@patternfly/react-core/dist/js/components/Tooltip';
import { Badge } from '@patternfly/react-core/dist/js/components/Badge';
import {
  OUIAProps,
  componentOuiaProps
} from '@kogito-apps/ouia-tools/dist/utils/OuiaUtils';

export interface ItemDescription {
  id: string;
  name: string;
  description?: string;
}

interface IOwnProps {
  itemDescription: ItemDescription;
}

export const ItemDescriptor: React.FC<IOwnProps & OUIAProps> = ({
  itemDescription,
  ouiaId,
  ouiaSafe
}) => {
  const tooltipContainerId = `kogito-consoles-tooltip-${
    itemDescription.id
  }-${Math.random()}`;

  const idStringModifier = (strId: string) => {
    return (
      <TextContent className="pf-u-display-inline">
        <Text component={TextVariants.small} className="pf-u-display-inline">
          {strId.substring(0, 5)}
        </Text>
      </TextContent>
    );
  };
  return (
    <>
      <Tooltip
        appendTo={() => document.getElementById(tooltipContainerId)}
        content={itemDescription.id}
        {...componentOuiaProps(ouiaId, 'item-descriptor', ouiaSafe)}
      >
        <span>
          {itemDescription.name}{' '}
          {itemDescription.description ? (
            <Badge>{itemDescription.description}</Badge>
          ) : (
            idStringModifier(itemDescription.id)
          )}
        </span>
      </Tooltip>
      <div id={tooltipContainerId}></div>
    </>
  );
};
