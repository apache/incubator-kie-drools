import { ReactElement } from 'react';
import { mount } from 'enzyme';
import { act } from 'react-dom/test-utils';

/** 
 * ugly workaround for the mount function in getWrapper to wait until component does all the hooks and effects
 */
const waitForComponentToPaint = async (wrapper: any) => {
  await act(async () => {
    await new Promise(resolve => setTimeout(resolve, 0));
    wrapper.update();
  });
};

/**
 * Wrapper used in snapshot testing to get rid of unnecessary wrappers of components.
 * Not only OUIA wrappers, but also Routers, MockedProvider,...
 * @param component The actual component with wrappers, e.g. <Router><MyGreatComponent /></Router>
 * @param name name of the component to be extracted as string, e.g. 'MyGreatComponent'
 */
export const getWrapper = (component: ReactElement, name: string) => {
  const wrapper = mount(component)
  waitForComponentToPaint(wrapper)
  return wrapper.find(name)
}

/**
 * Function to set OUIA:Page Page Type and Page Object Id attributes in page body.
 * @param ouiaContext OUIAContext of the component wrapped with patternfly's withOuiaContext function.
 * @param type string value to be set as Page Type
 * @param objectId string value to be set as Page Object Id
 */
export const ouiaPageTypeAndObjectId = (
  ouiaContext,
  type: string,
  objectId?: string
): (() => void) => {
  if (!ouiaContext.isOuia) return;
  document.body.setAttribute('data-ouia-page-type', type)
  if (objectId) document.body.setAttribute('data-ouia-page-object-id', objectId)
  return () => {
    document.body.removeAttribute('data-ouia-page-type')
    if (objectId) document.body.removeAttribute('data-ouia-page-object-id')
  }
};

/**
 * Function to set ouia attribute - only when OUIA is enabled.
 * Usage:
 * <div
 *   {...ouiaAttribute(ouiaContext,'name','value')}
 * @param ouiaContext OUIAContext of the component wrapped with patternfly's withOuiaContext function.
 * @param name name of the attribute
 * @param value value of the attribute
 */
export const ouiaAttribute = (ouiaContext, name: string, value: string) => {
  return (ouiaContext.isOuia && {[name]:value})
}