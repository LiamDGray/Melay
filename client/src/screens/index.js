import { Navigation } from 'react-native-navigation';

import MainContainer from './Main';

// register all screens of the app (including internal ones)
export function registerScreens(store, Provider) {
  Navigation.registerComponent('Melay.Main', () => MainContainer, store, Provider);
  //Navigation.registerComponent('example.SecondTabScreen', () => SecondTabScreen);
  //Navigation.registerComponent('example.PushedScreen', () => PushedScreen);
}