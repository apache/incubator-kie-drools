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
export const getWrapperAsync = async (
  component: ReactElement,
  name: string
): Promise<ReactWrapper> => {
  const wrapper = mount(component);
  // tslint:disable-next-line: await-promise
  await act(async () => {
    await wait(0);
  });
  const promise: Promise<ReactWrapper> = new Promise(resolve => {
    resolve(wrapper.update().find(name));
  });
  return promise;
};

/**
 * Wrapper used in snapshot testing to get rid of unnecessary wrappers of components.
 * Not only OUIA wrappers, but also Routers,...
 *
 * Not for use with Apollo's MockedProvider, use getWrapperAsync instead
 *
 * @param component The actual component with wrappers, e.g. <Router><MyGreatComponent /></Router>
 * @param name name of the component to be extracted as string, e.g. 'MyGreatComponent'
 */
export const getWrapper = (
  component: ReactElement,
  name: string
): ReactWrapper => {
  const wrapper = mount(component);
  return wrapper.update().find(name);
};

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
  if (!ouiaContext.isOuia) {
    return;
  }
  document.body.setAttribute('data-ouia-page-type', type);
  if (objectId) {
    document.body.setAttribute('data-ouia-page-object-id', objectId);
  }
  return () => {
    document.body.removeAttribute('data-ouia-page-type');
    if (objectId) {
      document.body.removeAttribute('data-ouia-page-object-id');
    }
  };
};

type OuiaId = number | string;

export interface OUIAProps {
  // If there is only one instance of the component on the page at once, it is OPTIONAL
  ouiaId?: OuiaId;
  // False if in animation
  ouiaSafe?: boolean;
}

/**
 * Function to set ouia attribute.
 * Usage:
 * <div
 *   {...ouiaAttribute(ouiaContext,'name','value')}
 * />
 * @param ouiaContext OUIAContext of the component wrapped with patternfly's withOuiaContext function.
 * @param name name of the attribute
 * @param value value of the attribute
 */
export const ouiaAttribute = (ouiaContext, name: string, value: any) => {
  if (ouiaContext.isOuia && value) {
    return { [name]: value };
  }
  return {};
};

/**
 * Function to set OUIA attributes on the component.
 *
 * Typical usage:
 * const MyComponent:React.FC<OUIAProps> = ({
 *   ouiaId,
 *   ouiaSafe
 * }) => {
 * return
 *   <OtherComponent {...componentOuiaProps(ouiaId, 'MyComponent', ouiaSafe)} >
 *   .
 *   .
 *   .
 *   </OtherComponent>
 * }
 *
 * @param ouiaId id of the component - a value passed as part of OUIAProps to the component
 * @param ouiaType type of the component - typically a string explicitly provided to this function call
 * @param isSafe boolean value indicating if the component is safe = is not doing any loading action. Default is true.
 */
export const componentOuiaProps = (
  ouiaId: OuiaId | null,
  ouiaType: string,
  isSafe: boolean = true
) => {
  return {
    ...(ouiaId && { 'data-ouia-component-id': ouiaId }),
    'data-ouia-component-type': ouiaType,
    'data-ouia-safe': isSafe
  };
};

/**
 * Function to set an data-ouia-component-id attribute.
 *
 * Typical usage:
 * <MyComponent>
 *   <Button {...attributeOuiaId('button-1')} />
 *   <Button {...attributeOuiaId('button-2')} />
 * </MyComponent>
 *
 * @param ouiaId id string value to be set as an id
 */
export const attributeOuiaId = (ouiaId: OuiaId) => {
  return ouiaAttribute({ isOuia: true }, 'data-ouia-component-id', ouiaId);
};
