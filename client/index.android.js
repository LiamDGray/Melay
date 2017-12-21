/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 * @flow
 */
import { AppRegistry } from "react-native";
import React, { Component } from "react";
import { Platform, StyleSheet, Text, View } from "react-native";
import { Container, Header, Spinner, Body, Title, Button } from "native-base";
import { Azure } from "./src/api/azure";
import { SmsAndroid } from "react-native-get-sms-android";

import { DeviceEventEmitter } from "react-native";

global.Buffer = global.Buffer || require('buffer');

DeviceEventEmitter.addListener("sms_onDelivery", msg => {
  console.log(msg);
});

export const sendSMS = (phoneNumber, message) => {
  SmsAndroid.autoSend(
    phoneNumber,
    message,
    fail => {
      console.log("Failed with this error: " + fail);
    },
    success => {
      console.log("SMS sent successfully");
    }
  );
};

/* List SMS messages matching the filter */
const filter = {
  box: "inbox" // 'inbox' (default), 'sent', 'draft', 'outbox', 'failed', 'queued', and '' for all
  // the next 4 filters should NOT be used together, they are OR-ed so pick one
  //read: 0, // 0 for unread SMS, 1 for SMS already read
  //_id: 1234, // specify the msg id
  //address: "+1888------", // sender's phone number
  //body: "How are you", // content to match
  // the next 2 filters can be used for pagination
  //indexFrom: 0, // start from index 0
  //maxCount: 10 // count of SMS to return each time
};

export const loadSMSMessages = () => {
  SmsAndroid.list(
    JSON.stringify(filter),
    fail => {
      console.log("Failed with this error: " + fail);
    },
    (count, smsList) => {
      console.log("Count: ", count);
      console.log("List: ", smsList);
      var arr = JSON.parse(smsList);

      arr.forEach(function(object) {
        console.log("Object: " + obj);
        console.log("-->" + obj.date);
        console.log("-->" + obj.body);
      });
    }
  );
};

export const uploadSMSMessages = () => {
  Azure.init("Connection string");

  loadSMSMessages();
};
/* 
Each sms will be represents by a JSON object represented below

{
  "_id": 1234,
  "thread_id": 3,
  "address": "2900",
  "person": -1,
  "date": 1365053816196,
  "date_sent": 0,
  "protocol": 0,
  "read": 1,
  "status": -1,
  "type": 1,
  "body": "Hello There, I am an SMS",
  "service_center": "+60162999922",
  "locked": 0,
  "error_code": -1,
  "sub_id": -1,
  "seen": 1,
  "deletable": 0,
  "sim_slot": 0,
  "hidden": 0,
  "app_id": 0,
  "msg_id": 0,
  "reserved": 0,
  "pri": 0,
  "teleservice_id": 0,
  "svc_cmd": 0,
  "roam_pending": 0,
  "spam_report": 0,
  "secret_mode": 0,
  "safe_message": 0,
  "favorite": 0
}

*/

const instructions =
  "Press R twice to reload,\n" + "Shift+F10 or shake for dev menu";

export class App extends Component<{}> {
  render() {
    return (
      <Container>
        <View style={styles.container}>
          <Header>
            <Body>
              <Title>Universal React</Title>
            </Body>
          </Header>
          <Text style={styles.welcome}>Welcome to React Native!</Text>
          <Text style={styles.instructions}>
            To get started, edit App.windows.js
          </Text>
          <Text style={styles.instructions}>{instructions}</Text>
          <Button>Do a sync</Button>
        </View>
      </Container>
    );
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: "center",
    alignItems: "center",
    backgroundColor: "#F5FCFF"
  },
  welcome: {
    fontSize: 20,
    textAlign: "center",
    margin: 10
  },
  instructions: {
    textAlign: "center",
    color: "#333333",
    marginBottom: 5
  }
});

AppRegistry.registerComponent("melay", () => App);

/*import { AppRegistry } from 'react-native';
import App from './src/native';

AppRegistry.registerComponent('melay', () => App);
*/
