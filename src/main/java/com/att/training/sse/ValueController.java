package com.att.training.sse;

import lombok.ToString;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

import static java.util.stream.Collectors.toUnmodifiableList;

@Slf4j
@RestController
@RequestMapping("api")
public class ValueController {

    private final Map<Integer, Deque<Client>> idToClients = new ConcurrentHashMap<>();
    private final Random random = new Random();

    @GetMapping(path = "events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter sse(@RequestParam String name, @RequestParam int conferenceId) {
        Client client = buildClient(name, conferenceId);
        idToClients.computeIfAbsent(conferenceId, key -> new ConcurrentLinkedDeque<>())
                .add(client);
        return client.getEmitter();
    }

    private Client buildClient(String name, int conferenceId) {
        long timeout = Duration.ofMinutes(1).toMillis();
        var emitter = new SseEmitter(timeout);
        var client = new Client(name, conferenceId, emitter);
        emitter.onCompletion(() -> removeClient(client));
        emitter.onError(ex -> {
            log.warn("Error in the emitter for client {}", client, ex);
            removeClient(client);
        });
        return client;
    }

    @Scheduled(fixedRateString = "PT1S")
    void emitEvents() {
        List<Client> allClients = idToClients.values()
                .stream()
                .flatMap(Deque::stream)
                .collect(toUnmodifiableList());
        int value = random.nextInt(101);
        for (var client : allClients) {
            sendToClient(client, value);
        }
    }

    private void sendToClient(Client client, int value) {
        try {
            client.getEmitter().send(value);
        } catch (IOException ignored) {
            // Handled by servlet container
        } catch (Exception e) {
            log.warn("sendToClient - sse emitter error:", e);
            removeClient(client);
        }

    }

    private void removeClient(Client client) {
        var clients = idToClients.get(client.getConferenceId());
        if (clients != null) {
            boolean removed = clients.remove(client);
            log.info("removeClient - removed {}: {}", client, removed);
        }
    }
}

@Value
class Client {
    String name;
    int conferenceId;
    @ToString.Exclude
    SseEmitter emitter;
    Instant createdDate = Instant.now();
}
