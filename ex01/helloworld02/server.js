const http = require('http');

const port = 80;
const server = http.createServer((request, response) => {
    response.writeHead(200, {
        "Content-Type": "text/html"
    });
    response.end("Hello, World\n");
});

server.listen(port, () => {
    const addr = server.address();
    const bind = typeof addr === 'string' ? 'pipe ' + addr : 'port ' + addr.port;
    console.log('Listening on ' + bind);
});