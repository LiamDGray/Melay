import React, { Component } from "react";
import { View, Text } from "react-native";
import { Button } from "native-base";

export default class MainContainer extends Component {
  render() {
    return (
      // Try removing the `flex: 1` on the parent View.
      // The parent will not have dimensions, so the children can't expand.
      // What if you add `height: 300` instead of `flex: 1`?
      <View style={{ flex: 1 }}>
        <View style={{ flex: 1, backgroundColor: "powderblue" }}>
          <Button
            block
            light
            onPress={this._viewMoviesList.bind(
              this,
              "now_playing",
              "Now Playing"
            )}
          >
            <Text>Login</Text>
          </Button>
        </View>
        <View style={{ flex: 2, backgroundColor: "skyblue" }}>
          <Button
            block
            success
            onPress={this._viewMoviesList.bind(
              this,
              "now_playing",
              "Now Playing"
            )}
          >
            <Text>Transfer Conversations</Text>
          </Button>
        </View>
        <View style={{ flex: 3, backgroundColor: "steelblue" }}>
          <Button
            block
            primary
            onPress={this._viewMoviesList.bind(
              this,
              "now_playing",
              "Now Playing"
            )}
          >
            <Text>Transfer Conversations</Text>
          </Button>
        </View>
      </View>
    );
  }
}
