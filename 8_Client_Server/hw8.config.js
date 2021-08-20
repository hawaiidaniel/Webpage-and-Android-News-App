module.exports = {
  apps : [{
    name        : "server",
    script      : "./server/bin/www",
    watch       : true,
  },
  {
	name        : 'client',
	script      : "./client/src/App.js",
    watch       : true,
  }]
}
