'use strict';

const port = process.env.PORT || 3030
const path = require('path');
const serveStatic = require('feathers').static;
const compress = require('compression');
const cors = require('cors');
const feathers = require('feathers');
const hooks = require('feathers-hooks');
const rest = require('feathers-rest');
const socketio = require('feathers-socketio');
const handler = require('feathers-errors/handler');
const bodyParser = require('body-parser');
var MongoClient = require('mongodb').MongoClient;
const service = require('feathers-mongodb');
const favicon = require('serve-favicon');

var databaseUri = process.env.DATABASE_URI || process.env.MONGODB_URI;

if (!databaseUri) {
  console.log('DATABASE_URI not specified, falling back to localhost.');
}


// Create a feathers instance.
const app = feathers()
  // Enable Socket.io
  .configure(socketio())
  // add files
  .configure(configuration(path.join(__dirname, '..')))
  // Enable REST services
  .configure(rest())
  //turn on CORS for fun
  .options('*', cors())
  .use(cors())
  // Turn on JSON parser for REST services
  .use(bodyParser.json())
  // Turn on URL-encoded parser for REST services
  .use(bodyParser.urlencoded({extended: true}));
  // favico
  app.use(favicon( path.join(app.get('public'), 'favicon.ico') ))
  // Serve static files
  .use('/', serveStatic( app.get('public') ));


const promise = new Promise(function(resolve) {
  // Connect to your MongoDB instance(s)
  MongoClient.connect(databaseUri || 'mongodb://localhost:27017/dev').then(function(db){
    // Connect to the db, create and register a Feathers service.
    app.use('/messages', service({
      Model: db.collection('messages'),
      paginate: {
        default: 2,
        max: 4
      }
    }));

    // A basic error handler, just like Express
    app.use(handler());

    // Start the server
    var server = app.listen(port);
    server.on('listening', function() {
      console.log("Feathers Message MongoDB service running on ${app.get('host')}:${port}");
      resolve(server);
    });
  }).catch(function(error){
    console.error(error);
  });
});

module.exports = promise;