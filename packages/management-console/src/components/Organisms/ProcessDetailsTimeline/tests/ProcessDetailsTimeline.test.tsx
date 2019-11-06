import React from 'react';
import { shallow } from 'enzyme';
import ProcessDetailsTimeline from '../ProcessDetailsTimeline';

const props = {
    loading: false,
    data: [{
        nodes: [{
            name: "Book flight",
            definitionId: "ServiceTask_1",
            id: "2f588da5-a323-4111-9017-3093ef9319d1",
            enter: "2019-10-22T04:43:01.144Z",
            exit: "2019-10-22T04:43:01.144Z",
            type: "WorkItemNode"
        }]
    }]
}


const props2 = {
    loading: true,
    data: [{}]
}

const props3 = {
    loading: false,
    data: [{
        nodes: [{
            name: "Book flight",
            definitionId: "ServiceTask_1",
            id: "2f588da5-a323-4111-9017-3093ef9319d1",
            enter: "2019-10-22T04:43:01.144Z",
            exit: "2019-10-22T04:43:01.144Z",
            type: "HumanTaskNode"
        }]
    }]
}
describe('Process Details Timeline component', () => {
    it('Sample test case', () => {
        const wrapper = shallow(<ProcessDetailsTimeline {...props}/>);
        expect(wrapper).toMatchSnapshot();
        expect(wrapper.find('.circle').length).toEqual(1);
      });
    it('Sample test case', () => {
        const wrapper = shallow(<ProcessDetailsTimeline {...props2}/>);
        expect(wrapper.find('.circle').length).toEqual(0);
        
    });
    it('test human task', () => {
        const wrapper = shallow(<ProcessDetailsTimeline {...props3}/>);
        expect(wrapper).toMatchSnapshot();
        
    });
})

