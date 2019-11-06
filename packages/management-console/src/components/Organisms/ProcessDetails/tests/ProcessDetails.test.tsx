import React from 'react';
import { shallow } from 'enzyme';
import ProcessDetails from '../ProcessDetails';

const props = {
    loading: false,
    data: {
        ProcessInstances: [{
            id: '',
            processId: '',
            state: '',
            parentProcessInstanceId: "e4448857-fa0c-403b-ad69-f0a353458b9d",
            endpoint: 'test',
            start: "2019-10-22T03:40:44.089Z",
            end: '2019-10-22T03:40:44.089Z',
        }]
    }
}

const props2 = {
    loading: true,
    data: {
        ProcessInstances: [{
            id: '2d962eef-45b8-48a9-ad4e-9cde0ad6af88',
            processId: 'hotelBooking',
            state: 'COMPLETED',
            parentProcessInstanceId: "e4448857-fa0c-403b-ad69-f0a353458b9d",
            endpoint: 'test',
            start: "2019-10-22T03:40:44.089Z",
            end: '2019-10-22T03:40:44.089Z',
        }]
    }
}


const props3 = {
    loading: true,
    data: {
        ProcessInstances: [{
            end: '2019-10-22T03:40:44.089Z',
        }]
    }
}
describe('Process Details component', () => {
    it('Snapshot tests', () => {
        const wrapper = shallow(<ProcessDetails {...props}/>);
        expect(wrapper).toMatchSnapshot();
      });
    it('Sample test case', () => {
        const wrapper = shallow(<ProcessDetails {...props2}/>);
        expect(wrapper.find('Text').at(1).prop('component')).toBe('p');
    });
   
})

