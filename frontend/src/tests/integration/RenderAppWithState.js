import React from 'react';
import { Provider } from 'react-redux';
import { createStore } from 'redux';
import App from '../../components/App/App';
import reducers from '../../reducers';

//Possible to insert wanted state in store for integration tests
export default function renderAppWithState(state) {
  const store = createStore(reducers, state);
  const wrapper = mount(
    <Provider store={store}>
      <App />
    </Provider>
  );
  return [store, wrapper];
}
