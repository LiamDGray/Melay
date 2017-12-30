import React from "react";
import { StyleSheet, Text, View, AppRegistry, Platform } from "react-native";
//REDUX
import { Provider } from "react-redux";
//the store
import configureStore from "./src/lib/configureStore";
//actions
import { setPlatform, setVersion } from "./src/reducers/device/deviceActions";
import { setStore } from "./src/reducers/global/globalActions";
import { Navigation } from "react-native-navigation";

import pack from "./package";

var VERISON = pack.VERSION;
var PLATFORM = Platform.OS;

const styles = StyleSheet.create({
  tabBar: {
    height: 70
  }
});

function getInitialState() {
  const _initState = {
    auth: new AuthInitialState(),
    device: new DeviceInitialState().set("isMobile", true),
    global: new GlobalInitialState(),
    profile: new ProfileInitialState()
  };
  return _initState;
}

//register all the navigation screens
import { registerScreens } from "./src/containers/index";

registerScreens();

Navigation.startSingleScreenApp({
  screen: {
    screen: "Melay.Main", // unique ID registered with Navigation.registerScreen
    title: "Welcome", // title of the screen as appears in the nav bar (optional)
    navigatorStyle: {}, // override the navigator style for the screen, see "Styling the navigator" below (optional)
    navigatorButtons: {} // override the nav buttons for the screen, see "Adding buttons to the navigator" below (optional)
  },
  /*drawer: { // optional, add this if you want a side menu drawer in your app
    left: { // optional, define if you want a drawer from the left
      screen: 'example.FirstSideMenu', // unique ID registered with Navigation.registerScreen
      passProps: {}, // simple serializable object that will pass as props to all top screens (optional)
      disableOpenGesture: false, // can the drawer be opened with a swipe instead of button (optional, Android only)
      fixedWidth: 500, // a fixed width you want your left drawer to have (optional)
    },
    right: { // optional, define if you want a drawer from the right
      screen: 'example.SecondSideMenu', // unique ID registered with Navigation.registerScreen
      passProps: {} // simple serializable object that will pass as props to all top screens (optional)
      disableOpenGesture: false, // can the drawer be opened with a swipe instead of button (optional, Android only)
      fixedWidth: 500, // a fixed width you want your right drawer to have (optional)
    },
    style: { // ( iOS only )
      drawerShadow: true, // optional, add this if you want a side menu drawer shadow
      contentOverlayColor: 'rgba(0,0,0,0.25)', // optional, add this if you want a overlay color when drawer is open
      leftDrawerWidth: 50, // optional, add this if you want a define left drawer width (50=percent)
      rightDrawerWidth: 50 // optional, add this if you want a define right drawer width (50=percent)
    },
    type: 'MMDrawer', // optional, iOS only, types: 'TheSideBar', 'MMDrawer' default: 'MMDrawer'
    animationType: 'door', //optional, iOS only, for MMDrawer: 'door', 'parallax', 'slide', 'slide-and-scale'
                                        // for TheSideBar: 'airbnb', 'facebook', 'luvocracy','wunder-list'
    disableOpenGesture: false // optional, can the drawer, both right and left, be opened with a swipe instead of button
  },*/
  passProps: {}, // simple serializable object that will pass as props to all top screens (optional)
  animationType: "slide-down" // optional, add transition animation to root change: 'none', 'slide-down', 'fade'
});

/*export default class App extends React.Component {
  render() {
    const store = configureStore(getInitialState());

    store.dispatch(setPlatform(PLATFORM));
    store.dispatch(setVersion(VERSION));
    store.dispatch(setStore(store));

    Navigation.registerComponent('example.FirstTabScreen', () => FirstTabScreen, store, Provider);

    return (
      <Provider store={store}>
        <Router sceneStyle={{ backgroundColor: 'white' }}>
          <Stack key="root" hideNavBar>
            <Scene key="App" component={AppContainer} type="replace" initial title="App" />
          </Stack>
        </Router>
      </Provider>
    );
  }
}
*/
