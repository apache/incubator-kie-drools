import React from 'react';
import { shallow } from 'enzyme';
import ProcessDetailsProcessVariables from '../ProcessDetailsProcessVariables';

const props = {
  loading: true,
  data: {
    ProcessInstances: []
  }
};

const props2 = {
  loading: false,
  data: {
    ProcessInstances: [
      {
        variables:
          '{"trip":{"begin":"2019-10-22T22:00:00Z[UTC]","city":"Berlin","country":"Germany","end":"2019-10-30T22:00:00Z[UTC]","visaRequired":false},"hotel":{"address":{"city":"Berlin","country":"Germany","street":"street","zipCode":"12345"},"bookingNumber":"XX-012345","name":"Perfect hotel","phone":"09876543"},"traveller":{"address":{"city":"Karkow","country":"Poland","street":"palna","zipCode":"200300"},"email":"rob@redhat.com","firstName":"Rob","lastName":"Rob","nationality":"Polish"}}'
      }
    ]
  }
};
describe('ProcessVariables component tests', () => {
  it('snapshot testing without variables', () => {
    const wrapper = shallow(<ProcessDetailsProcessVariables {...props} />);
    expect(wrapper).toMatchSnapshot();
  });
  it('snapshot testing with variables', () => {
    const wrapper = shallow(<ProcessDetailsProcessVariables {...props2} />);
    expect(wrapper).toMatchSnapshot();
  });
});
