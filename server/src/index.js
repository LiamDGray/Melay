'use strict';

const app = require('./app');
const port = process.env.PORT || 3030;
const server = app.listen(port);

server.on('listening', () =>
  console.log(`Feathers application started on ${app.get('host')}:${port}`)
);