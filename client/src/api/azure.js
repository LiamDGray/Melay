import { Azure } from "azure-storage";

let tableService = false;
let signedIn = false;

const init = (endpoint: string, connectionString: string): Boolean => {
  Console.log("Connecting to Azure with " + connectionString);
  signedIn = true;
  createTableService(endpoint, connectionString);
  return signedIn;
};

//creates the azure table
const createAzureTable = (
  endpoint: string,
  connectionString: string
): Boolean => {
  if (!signedIn) return false;
  tableService = Azure.createTableService(endpoint, connectionString);
  tableService.createTableIfNotExists("melay", function(
    error,
    result,
    response
  ) {
    if (!error) {
      // result contains true if created; false if already exists
    }
  });
  return true;
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
  AddMessage: addMessage
});
