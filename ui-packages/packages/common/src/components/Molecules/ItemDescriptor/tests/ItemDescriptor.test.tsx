import React from 'react';
import { shallow } from 'enzyme';
import ItemDescriptor from '../ItemDescriptor';

const item1 = {
  id: 'a1e139d5-4e77-48c9-84ae-34578e904e5a',
  name: 'HotelBooking',
  description: 'T1234HotelBooking01'
};

const item2 = {
  id: 'a1e139d5-4e77-48c9-84ae-34578e904e5a',
  name: 'HotelBooking'
};
describe('ItemDescriptor component tests', () => {
  it('snapshot testing for business key available', () => {
    const wrapper = shallow(<ItemDescriptor itemDescription={item1} />);
    expect(wrapper).toMatchSnapshot();
  });
  it('snapshot testing for business key null', () => {
    const wrapper = shallow(<ItemDescriptor itemDescription={item2} />);
    expect(wrapper).toMatchSnapshot();
  });
});
