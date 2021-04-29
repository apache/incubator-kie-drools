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
