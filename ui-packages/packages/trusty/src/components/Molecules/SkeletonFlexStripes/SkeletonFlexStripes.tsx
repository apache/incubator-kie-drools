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
import { Flex, FlexItem } from '@patternfly/react-core';
import SkeletonStripe from '../../Atoms/SkeletonStripe/SkeletonStripe';
import './SkeletonFlexStripes.scss';

type SkeletonFlexStripesProps = {
  stripesNumber: number;
  stripesWidth: string;
  stripesHeight: string;
  isPadded?: boolean;
};

const SkeletonFlexStripes = (props: SkeletonFlexStripesProps) => {
  const { stripesNumber, stripesWidth, stripesHeight, isPadded = true } = props;
  const stripes = [];
  const stripeStyle = {
    width: stripesWidth,
    height: stripesHeight
  };
  const className = `skeleton__flex-stripes ${
    isPadded ? 'skeleton__flex-stripes--padded' : ''
  }`;
  for (let i = 0; i < stripesNumber; i++) {
    stripes.push(
      <FlexItem
        key={`skeleton-${i}`}
        className={'skeleton__flex-stripes__item'}
      >
        <SkeletonStripe customStyle={stripeStyle} />
      </FlexItem>
    );
  }
  return <Flex className={className}>{stripes}</Flex>;
};

export default SkeletonFlexStripes;
