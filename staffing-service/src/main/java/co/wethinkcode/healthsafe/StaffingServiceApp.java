package co.wethinkcode.healthsafe;

import io.javalin.Javalin;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import co.wethinkcode.healthsafe.mq.MqConfig;
import org.apache.activemq.ActiveMQConnectionFactory;
import jakarta.jms.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

public class StaffingServiceApp {

    public static void main(String[] args) {
        Javalin app = Javalin.create().start(7033);
        ObjectMapper mapper = new ObjectMapper();
        HttpClient client = HttpClient.newHttpClient();

        app.get("/health", ctx -> ctx.result("OK"));


        app.get("/staffing/{wardId}", ctx -> {
            String wardId = ctx.pathParam("wardId").toUpperCase();

            try {
              //caling wardservice
                HttpRequest wardRequest = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:7031/wards/" + wardId))
                        .GET()
                        .build();
                HttpResponse<String> wardResponse = client.send(wardRequest, HttpResponse.BodyHandlers.ofString());

                if (wardResponse.statusCode() == 404) {
                    ctx.status(404).result("Cannot compute schedule: Ward " + wardId + " does not exist.");
                    return;
                }

                JsonNode wardJson = mapper.readTree(wardResponse.body());


                Integer beds = null;
                if (wardJson.hasNonNull("bedsAvailable")) {
                    beds = wardJson.get("bedsAvailable").asInt();
                }


                //alert service
                HttpRequest alertRequest = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:7032/alert-level"))
                        .GET()
                        .build();
                HttpResponse<String> alertResponse = client.send(alertRequest, HttpResponse.BodyHandlers.ofString());

                JsonNode alertJson = mapper.readTree(alertResponse.body());
                int alertLevel = alertJson.get("level").asInt();


                //scheduling
                int baseDoctors;
                if (beds != null) {
                    baseDoctors = Math.max(1, beds / 10);
                } else {
                    baseDoctors = 1;
                }

                int totalDoctorsNeeded = baseDoctors + alertLevel;


                Map<String, Object> scheduleMap= (Map.of(
                        "wardId", wardId,
                        "bedsAvailable", beds != null ? beds : "UNKNOWN",
                        "currentEmergencyLevel", alertLevel,
                        "doctorsOnCall", totalDoctorsNeeded
                ));

                publishStaffingEvent(mapper.writeValueAsString(scheduleMap));

                ctx.json(scheduleMap);

            } catch (Exception e) {
                ctx.status(500).result("Internal Server Error: Could not connect to downstream services. " + e.getMessage());
            }
        });

        // TODO (Provides on-call schedules for doctors based on ward and status.)
        // Add domain endpoints for staffing-service here.
    }
    private static void publishStaffingEvent(String jsonPayload) {
        try {
            ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(MqConfig.BROKER_URL);
            Connection connection = connectionFactory.createConnection();
            connection.start();

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination destination = session.createTopic(MqConfig.TOPIC);
            MessageProducer producer = session.createProducer(destination);

            TextMessage message = session.createTextMessage(jsonPayload);
            producer.send(message);

            connection.close();
            System.out.println("Published staffing event to ActiveMQ topic: " + MqConfig.TOPIC);
        } catch (Exception e) {
            System.err.println("Failed to publish message to ActiveMQ: " + e.getMessage());
        }
    }
}

// MQ TODO: publishes to ActiveMQ topic MqConfig.TOPIC at MqConfig.BROKER_URL (see co.wethinkcode.healthsafe.mq.MqConfig)
