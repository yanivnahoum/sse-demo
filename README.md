# SSE Demo

### Testing in the browser
1. Run app
2. Point your browser at http://localhost:8080/sse/index.html
3. Open the browser's console and view the events received from the server.

### Testing multiple clients
1. Run the app
2. In a terminal run `curl -v http://localhost:8080/sse/api/events?conferenceId=101&name=client1`
3. In a second terminal run `curl -v http://localhost:8080/sse/api/events?conferenceId=101&name=client2`