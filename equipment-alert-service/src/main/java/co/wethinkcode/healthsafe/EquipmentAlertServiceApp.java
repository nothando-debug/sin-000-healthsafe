package co.wethinkcode.healthsafe;

import org.apache.activemq.ActiveMQConnectionFactory;

import co.wethinkcode.healthsafe.mq.MqConfig;
import io.javalin.Javalin;

import jakarta.jms.*;
public class EquipmentAlertServiceApp {

    public static void main(String[] args) {
        Javalin app = Javalin.create().start(7034);

        app.get("/health", ctx -> ctx.result("OK"));

        startQueueConsumer();
        

        // TODO (Uses a Queue to guarantee delivery of critical medical equipment failure alerts.)
        // Mechanism: ActiveMQ Queue (guaranteed delivery)
    }

    private static void startQueueConsumer() {
        try {
            ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(MqConfig.BROKER_URL);
            Connection connection = connectionFactory.createConnection();
            connection.start();

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            
            Destination destination = session.createQueue(MqConfig.QUEUE);
            MessageConsumer consumer = session.createConsumer(destination);

            consumer.setMessageListener(new MessageListener() {
                @Override
                public void onMessage(Message message) {
                    if (message instanceof TextMessage) {
                        try {
                            String failurePayload = ((TextMessage) message).getText();
                            
                            System.out.println("\n--------------------------------------------------");
                            System.out.println("[EQUIPMENT ALERT SERVICE] MAINTENANCE ALERT RECEIVED!");
                            System.out.println("Payload: " + failurePayload);
                            System.out.println("--------------------------------------------------\n");

                        } catch (JMSException e) {
                            System.err.println("Error reading message from failure queue: " + e.getMessage());
                        }
                    }
                }
            });

            System.out.println("Equipment Alert Service successfully subscribed to queue: " + MqConfig.QUEUE);

        } catch (Exception e) {
            System.err.println("Failed to start MQ Queue Consumer: " + e.getMessage());
        }
    }
}

// MQ TODO: consumes ActiveMQ queue MqConfig.QUEUE at MqConfig.BROKER_URL (see co.wethinkcode.healthsafe.mq.MqConfig)
// Producer: ward-service publishes here when it detects an equipment failure on one of its wards.
