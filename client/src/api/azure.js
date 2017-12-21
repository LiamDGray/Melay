
let tableService = false;
let signedIn = false;

const init = (endpoint: string, connectionString: string): Boolean => {
  Console.log("Connecting to Azure with " + connectionString);
  signedIn = true;
  //createTableService(endpoint, connectionString);
  return signedIn;
};

const setMessage = (conversation: string, id: string, message): Boolean => {
  if (!signedIn) return false;

  return true;
};

const getConversation = (conversation: string, options: Object): Object => {
  if (!signedIn) return {};
  return {};
};

const getMessage = (conversation: string, id: string): Object => {
  if (!signedIn) return {};
  return {};
};

export default (AzureStorage = {
  Init: init,
  SetMessage: setMessage
});
