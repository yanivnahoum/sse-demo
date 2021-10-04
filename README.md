# SSE Demo

### Testing in the browser
1. Run app
2. Point your browser at http://localhost:8080/sse/index.html
3. Open the browser's console and view the events received from the server.

### Testing multiple clients
1. Run the app
2. In a terminal run `curl -v http://local.intl.att.com/sse/api/events?conferenceId=100&name=client1`
2. In a second terminal run `curl -v http://local.intl.att.com/sse/api/events?conferenceId=100&name=client2`