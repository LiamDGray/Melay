/* The actions to support the SMS actions
*/
"use strict";
/**
 * ## Imports
 *
 * The actions for profile
 */
const { SYNC_MESSAGES } = require("../../lib/constants").default;

/**
 * BackendFactory - base class for server implementation
 * AppAuthToken for localStorage sessionToken access
 */
const BackendFactory = require("../../lib/BackendFactory").default;
import { appAuthToken } from "../../lib/AppAuthToken";

import { SMSEngine } from "../../lib/SMSEngine";

/**
 * ## retreiving profile actions
 */
export function syncMessagesRequest() {
  return {
    type: SYNC_MESSAGES
  };
}

export function syncMessagesSuccess() {
  return {
    type: SYNC_MESSAGES_SUCCESS
  };
}

export function syncMessagesFailure() {
    return {
      type: SYNC_MESSAGES_FAILURE
    };
  }

/**
 * ## State actions
 * controls which form is displayed to the user
 * as in login, register, logout or reset password
 */
export function syncMessages(sessionToken) {
  return dispatch => {
    dispatch(syncMessagesRequest());
    // store or get a sessionToken
    return appAuthToken
      .getSessionToken(sessionToken)
      .then(token => {
        return SMSEngine.engine.syncMessages(BackendFactory(token));
      })
      .then(json => {
        dispatch(getProfileSuccess(json));
      })
      .catch(error => {
        dispatch(getProfileFailure(error));
      });
  };
}
