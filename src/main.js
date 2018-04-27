import Vue from 'nativescript-vue';

import Counter from './components/Counter';

import store from './store';

import './styles.scss';

// Uncommment the following to see NativeScript-Vue output logs
//Vue.config.silent = false;

new Vue({

  render: h => h(Counter),

  store,

}).$start();
