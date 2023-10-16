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
import { Gallery, GalleryItem } from '@patternfly/react-core';
import { v4 as uuid } from 'uuid';
import SkeletonStripe from '../../Atoms/SkeletonStripe/SkeletonStripe';
import './SkeletonCards.scss';

type SkeletonCardProps = {
  quantity: number;
};

const SkeletonCards = (props: SkeletonCardProps) => {
  const { quantity } = props;
  const cards = [];
  for (let i = 0; i < quantity; i++) {
    cards.push(
      <GalleryItem key={uuid()}>
        <div className="skeleton-cards__card">
          <SkeletonStripe
            key={uuid()}
            customStyle={{ width: '90%', height: 25, marginBottom: '20px' }}
          />
          <SkeletonStripe
            key={uuid()}
            customStyle={{ width: '60%', height: 20 }}
          />
        </div>
      </GalleryItem>
    );
  }

  return (
    <Gallery className="skeleton-cards" hasGutter>
      {cards}
    </Gallery>
  );
};

export default SkeletonCards;
