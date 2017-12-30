import { Navigation } from 'react-native-navigation';

import MainContainer from './Main';

// register all screens of the app (including internal ones)
export function registerScreens() {
  Navigation.registerComponent('Melay.Main', () => MainContainer);
  //Navigation.registerComponent('example.SecondTabScreen', () => SecondTabScreen);
  //Navigation.registerComponent('example.PushedScreen', () => PushedScreen);
}