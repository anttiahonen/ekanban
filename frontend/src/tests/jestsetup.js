import { shallow, render, mount } from 'enzyme';
global.shallow = shallow;
global.render = render;
global.mount = mount;

// Fail tests on any warning
console.error = message => {
  if (message.indexOf("Shallow rendered") !== -1)
    throw new Error(message);
};
