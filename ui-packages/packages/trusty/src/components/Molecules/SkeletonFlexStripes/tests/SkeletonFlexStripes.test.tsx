import React from 'react';
import { shallow } from 'enzyme';
import SkeletonFlexStripes from '../SkeletonFlexStripes';

describe('SkeletonFlexStripes', () => {
  test('renders a list of three stripes', () => {
    const wrapper = shallow(
      <SkeletonFlexStripes
        stripesNumber={3}
        stripesWidth="200px"
        stripesHeight="1em"
      />
    );

    expect(wrapper).toMatchSnapshot();
    expect(wrapper.prop('className')).toMatch('skeleton__flex-stripes');
    expect(wrapper.find('SkeletonStripe')).toHaveLength(3);
    expect(
      wrapper
        .find('SkeletonStripe')
        .at(0)
        .prop('customStyle')
    ).toStrictEqual({
      width: '200px',
      height: '1em'
    });
  });

  test('renders a padded list of stripes', () => {
    const wrapper = shallow(
      <SkeletonFlexStripes
        stripesNumber={2}
        stripesWidth="200px"
        stripesHeight="1em"
        isPadded
      />
    );

    expect(wrapper).toMatchSnapshot();
    expect(wrapper.prop('className')).toMatch(
      'skeleton__flex-stripes skeleton__flex-stripes--padded'
    );
  });
});
