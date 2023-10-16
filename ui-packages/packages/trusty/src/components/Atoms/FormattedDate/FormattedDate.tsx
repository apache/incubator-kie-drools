/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
import React from 'react';
import { differenceInDays, format, formatDistanceToNow } from 'date-fns';
import { Tooltip } from '@patternfly/react-core';
import { TooltipProps } from '@patternfly/react-core/dist/js/components/Tooltip/Tooltip';

type FormattedDateProps = {
  date: string;
  preposition?: boolean;
  position?: TooltipProps['position'];
  fullDateAndTime?: boolean;
};

const FormattedDate = (props: FormattedDateProps) => {
  const {
    date,
    preposition = false,
    position = 'auto',
    fullDateAndTime = false
  } = props;
  const difference = differenceInDays(new Date(date), Date.now());
  const fullFormattedDate = format(new Date(date), 'PPpp');
  let formattedDate;

  if (difference === 0) {
    formattedDate = `${formatDistanceToNow(new Date(date))} ago`;
  } else {
    const prefix = preposition ? 'on ' : '';
    formattedDate = `${prefix}${format(new Date(date), 'PP')}`;
  }

  return (
    <>
      {fullDateAndTime ? (
        <span>{fullFormattedDate}</span>
      ) : (
        <Tooltip
          content={fullFormattedDate}
          entryDelay={200}
          exitDelay={100}
          position={position}
        >
          <span>{formattedDate}</span>
        </Tooltip>
      )}
    </>
  );
};

export default FormattedDate;
