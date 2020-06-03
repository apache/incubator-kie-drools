import { ReactElement } from 'react';
import { mount, ReactWrapper } from 'enzyme';
import wait from 'waait';
import { act } from 'react-dom/test-utils';

/**
 * Wrapper used in asynchronous snapshot tests to wait for the asynchronous action to complete and get rid
 * of unnecessary wrappers of components in resulting snapshots.
 *
 * For use with MockedProvider mainly.
 *
 * Typical usage:
 * it('test case', async () => {
 *   const wrapper = await getWrapperAsync(
 *     <MockedProvider>
 *       <MyComponent />
 *     </MockedProvider>
 *     , 'MyComponent')
 *   expect(wrapper).toMatchSnapshot()
 * })
 *
 * @param component The actual component with wrappers, e.g. <MockedProvider mocks=mockedData><MyGreatComponent /></MockedProvider>
 * @param name name of the component to be extracted as string, e.g. 'MyGreatComponent'
 */
export const getWrapperAsync = async (component: ReactElement, name: string): Promise<ReactWrapper> => {
  const wrapper = mount(component);
  // tslint:disable-next-line: await-promise
  await act(async () => {
    await wait(0)
  })
  const promise: Promise<ReactWrapper> = new Promise(resolve => { resolve(wrapper.update().find(name)) })
  return promise
}

/**
 * Wrapper used in snapshot testing to get rid of unnecessary wrappers of components.
 * Not only OUIA wrappers, but also Routers,...
 *
 * Not for use with Apollo's MockedProvider, use getWrapperAsync instead
 *
 * @param component The actual component with wrappers, e.g. <Router><MyGreatComponent /></Router>
 * @param name name of the component to be extracted as string, e.g. 'MyGreatComponent'
 */
export const getWrapper = (component: ReactElement, name: string): ReactWrapper => {
  const wrapper = mount(component);
  return wrapper.update().find(name)
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
 * />
 * @param ouiaContext OUIAContext of the component wrapped with patternfly's withOuiaContext function.
 * @param name name of the attribute
 * @param value value of the attribute
 */
export const ouiaAttribute = (ouiaContext, name: string, value: string) => {
  return (ouiaContext.isOuia && { [name]: value })
}

/**
 * Function to set OUIA attributes on the component. For use in components that extends on InjectedOUIAProps
 * and are wrapped by withOuiaContext() into Higher-order component.
 *
 * Typical usage:
 * const MyComponent:React.FC<InjectedOuiaProps> = ({
 *   ouiaContext,
 *   ouiaId
 * }) => {
 * return
 *   <OtherComponent {...componentOuiaProps(ouiaContext, ouiaId, 'MyComponent', !isLoading())} >
 *   .
 *   .
 *   .
 *   </OtherComponent>
 * }
 *
 * @param ouiaContext ouiaContext provided by the higher-order component wrapper
 * @param ouiaId id that is being passed to this component via the context (not to be set explicitly)
 * @param ouiaType type of the component - typically a string explicitly provided to this function call
 * @param isSafe boolean value indicating if the component is safe = is not doing any loading action.
 */
export const componentOuiaProps = (
  ouiaContext,
  ouiaId,
  ouiaType,
  isSafe?
) => {
  return ouiaContext.isOuia && {
    'data-ouia-component-type': ouiaType,
    'data-ouia-component-id': ouiaId || ouiaContext.ouiaId,
    'data-ouia-safe': (isSafe) ? true : false
  }
};

/**
 * Function to set an data-ouia-component-id attribute if OUIA is enabled (either by default or in browser local storage `ouia:enabled`=true)
 *
 * Typical usage:
 * <MyComponent>
 *   <Button {...attributeOuiaId(ouiaContext, 'button-1')} />
 *   <Button {...attributeOuiaId(ouiaContext, 'button-2')} />
 * </MyComponent>
 *
 * @param ouiaContext ouiaContext provided by the higher-order component wrapper
 * @param ouiaId id string value to be set as an id
 */
export const attributeOuiaId = (
  ouiaContext,
  ouiaId: string
) => {
  return ouiaAttribute(ouiaContext, 'ouiaId', ouiaId)
}
