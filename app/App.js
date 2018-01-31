import React from 'react';
import { Alert, Button, TextInput, StyleSheet, Text, View } from 'react-native';
import SharedPreferences from 'react-native-shared-preferences';
import { PermissionsAndroid } from 'react-native';

//https://facebook.github.io/react-native/docs/permissionsandroid.html

async function requestSMSPermission() {
  try {
    const granted = await PermissionsAndroid.request(
      PermissionsAndroid.PERMISSIONS.CAMERA,
      {
        'title': 'Cool Photo App Camera Permission',
        'message': 'Cool Photo App needs access to your camera ' +
                   'so you can take awesome pictures.'
      }
    )
    if (granted === PermissionsAndroid.RESULTS.GRANTED) {
      console.log("You can use the camera")
    } else {
      console.log("Camera permission denied")
    }
  } catch (err) {
    console.warn(err)
  }
}

export default class App extends React.Component {
  constructor(props) {
    super(props);
    this.state = {text: ''};
    this.onPressButton = this.onPressButton.bind(this);
    var self = this;

    console.log("Starting to get preferences");
    SharedPreferences.getItem("AZURE_CONN_STRING", function(Value){
      console.log("Retrieved preferences", Value);
      self.setState({text:Value});
    });
  }
  onPressButton(event ) {
    console.log("Pressed Button");
    Alert.alert('You set the settings to ' + this.state.text);
    SharedPreferences.setItem("AZURE_CONN_STRING",this.state.text);
    requestSMSPermission();
  }
  //TODO ask for permissions
  
  render() {
    return (
      <View style={styles.container}>
        <View style={[styles.header,styles.bgBlue]}>
          <Text>Melay {this.state.text}</Text>          
        </View>
        <View style={[styles.pane2, styles.bgGreen]}>
          <Text>Please Specify your connection string</Text>
          <TextInput
            style={{height: 40}}
            placeholder="your connection string"
            onChangeText={(text) => this.setState({text:text})}
            value={this.state.text}
          />
        </View>
        <View style={styles.pane}>
          <Text>Changes you make will automatically reload.</Text>
          <Text>Shake your phone to open the developer menu.</Text>
        </View>
        <View style={styles.pane2}>
        <Button
            onPress={this.onPressButton}
            title="Save Changes"
            color="#841584"
          />
          </View>
      </View>
    );
  }
}

const styles = StyleSheet.create({
  header : {
    height: 40,
  },
  container: {
    flex: 1,
    backgroundColor: '#fff',   
  },
  pane: {
    flex: 1
  },
  pane2: {
    flex: 2
  },
  pane3: {
    flex: 3
  },
  bgGreen:{
    backgroundColor: 'green',
  },
  bgBlue:{
    backgroundColor: 'powderblue',
  },
});
