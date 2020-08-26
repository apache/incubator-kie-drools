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
  const className = isPadded ? 'skeleton skeleton--padded' : 'skeleton';
  for (let i = 0; i < stripesNumber; i++) {
    stripes.push(
      <FlexItem key={`skeleton-${i}`}>
        <SkeletonStripe customStyle={stripeStyle} />
      </FlexItem>
    );
  }
  return <Flex className={className}>{stripes}</Flex>;
};

export default SkeletonFlexStripes;
