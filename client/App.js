import React from "react";
import {
  StyleSheet,
  Text,
  View,
  AppRegistry
} from "react-native";
//router flux
import {Router, Scene} from 'react-native-router-flux'
//REDUX
import {Provider} from 'react-redux'
//the store
import configureStore from './lib/configureStore'
//actions
import {setPlatform, setVersion} from './reducers/device/deviceActions'
import {setStore} from './reducers/global/globalActions'

var VERISON = pack.VERSION

const styles = StyleSheet.create({
  tabBar: {
    height: 70
  }
})

function getInitialState() {
  const _initState = {
    auth: new AuthInitialState(),
    device: new DeviceInitialState().set("isMobile", true),
    global: new GlobalInitialState(),
    profile: new ProfileInitialState()
  };
  return _initState;
}

export default class App extends React.Component {
   

  render() {
    const store = configureStore(getInitialState());

    store.dispatch(setPlatform(platform));
    store.dispatch(setVersion(VERSION));
    store.dispatch(setStore(store));

    /*if (this.state.isLoading) {
      return (
        <View style={{flex: 1, paddingTop: 20}}>
          <ActivityIndicator />
        </View>
      );
    }

    return (
      <View style={{flex: 1, paddingTop: 20}}>
        <ListView
          dataSource={this.state.dataSource}
          renderRow={(rowData) => <Text>{rowData.title}, {rowData.releaseYear}</Text>}
        />
      </View>
    );*/

    return (
      <Provider store={store}>
        <Router>
          <Sceen key='root' hideNavBar>
          <Scene key='App'
                component={App}
                type='replace'
                initial />
          </Router>
      </Provider>
    );
  }
}

AppRegistry.registerComponent("Melay", () => App);
