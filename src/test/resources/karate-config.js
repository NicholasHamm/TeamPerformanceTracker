function fn() {
  var port = karate.properties['local.server.port'];
  karate.log('local.server.port:', port);

  if (!port) {
	karate.fail('local.server.port is not set – backend not started or port not injected');
	}

  var config = {
    baseUrl: 'http://localhost:' + port
  };

  karate.configure('headers', { Accept: 'application/json' });
  karate.configure('followRedirects', false);

  karate.log('baseUrl:', config.baseUrl);
  return config;
}