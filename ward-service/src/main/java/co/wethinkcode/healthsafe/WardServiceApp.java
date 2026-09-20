package co.wethinkcode.healthsafe;


import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;


import org.apache.activemq.ActiveMQConnectionFactory;
import jakarta.jms.*;
import com.fasterxml.jackson.databind.ObjectMapper;

import co.wethinkcode.healthsafe.mq.MqConfig;
import io.javalin.Javalin;

public class WardServiceApp {

    public static void main(String[] args) {

        Map<String, Ward> wardDirectory = new HashMap<>();
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:7030/wards"))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                ObjectMapper mapper = new ObjectMapper();
                Ward[] fetchedWards = mapper.readValue(response.body(), Ward[].class);

                for (Ward w : fetchedWards) {
                    wardDirectory.put(w.getWardId(), w);
                }
                System.out.println("Ward Service successfully loaded " + wardDirectory.size() + " wards.");
            } else {
                System.err.println("Failed to fetch from Ingestion Service. Status: " + response.statusCode());
            }
        } catch (Exception e) {
            System.err.println("Error connecting to Ingestion Service: " + e.getMessage());
        }


        Javalin app = Javalin.create().start(7031);

        app.get("/health", ctx -> ctx.result("OK"));

        app.get("/wards", ctx -> {
            ctx.json(wardDirectory.values());
        });

        app.get("/wards/{id}", ctx -> {
            String id = ctx.pathParam("id").toUpperCase();
            if (wardDirectory.containsKey(id)) {
                ctx.json(wardDirectory.get(id));
            } else {
                ctx.status(404).result("Ward not found");
            }
        });

        startMqSubscriber();
    }

    private static void startMqSubscriber() {
        try {
            ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(MqConfig.BROKER_URL);
            Connection connection = connectionFactory.createConnection();
            connection.start();

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination destination = session.createTopic(MqConfig.TOPIC);
            MessageConsumer consumer = session.createConsumer(destination);

            consumer.setMessageListener(new MessageListener() {
                @Override
                public void onMessage(Message message) {
                    if (message instanceof TextMessage) {
                        try {
                            String payload = ((TextMessage) message).getText();
                            System.out.println("[WardService MQ Consumer] Received staffing update: " + payload);
                        } catch (JMSException e) {
                            System.err.println("Failed to read MQ message: " + e.getMessage());
                        }
                    }
                }
            });

            System.out.println("Ward Service subscribed to topic: " + MqConfig.TOPIC);
        } catch (Exception e) {
            System.err.println("Failed to initialize MQ Subscriber: " + e.getMessage());
        }
    }

 
private static void publishEquipmentFailureEvent(String jsonPayload) {
    try {
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(MqConfig.BROKER_URL);
        Connection connection = connectionFactory.createConnection();
        connection.start();

        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        Destination destination = session.createQueue(MqConfig.QUEUE);
        MessageProducer producer = session.createProducer(destination);

        TextMessage message = session.createTextMessage(jsonPayload);
        producer.send(message);

        connection.close();
        System.out.println("[WardService MQ Producer] Published equipment failure to queue: " + MqConfig.QUEUE);
    } catch (Exception e) {
        System.err.println("Failed to publish equipment failure to ActiveMQ: " + e.getMessage());
    }
}
}









        // TODO (Provides lists of wards and departments.)
        // Add domain endpoints for ward-service here.



// MQ TODO: subscribes to ActiveMQ topic MqConfig.TOPIC at MqConfig.BROKER_URL (see co.wethinkcode.healthsafe.mq.MqConfig)
// MQ TODO: publishes to ActiveMQ queue MqConfig.QUEUE when it detects an equipment failure on one of its wards.
