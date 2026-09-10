package co.wethinkcode.healthsafe;

import io.javalin.Javalin;
import java.util.*;
public class AlertLevelServiceApp {

    private static int emergencyStatus = 0;

    public static void main(String[] args) {
        Javalin app = Javalin.create().start(7032);

        app.get("/health", ctx -> ctx.result("OK"));

        app.get("/alert-level", ctx -> {
            ctx.json(Map.of("level", emergencyStatus));
        });

        app.post("/alert-level/{level}", ctx -> {
            try {
                // Extract the number from the URL path
                int newLevel = Integer.parseInt(ctx.pathParam("level"));

                // Enforce the business rule: must be between 0 and 8
                if (newLevel >= 0 && newLevel <= 8) {
                    emergencyStatus = newLevel;
                    ctx.result("Emergency Status successfully updated to: " + emergencyStatus);
                } else {
                    ctx.status(400).result("Invalid level. Must be between 0 and 8.");
                }
            } catch (NumberFormatException e) {
                // Catch if someone types /alert-level/five
                ctx.status(400).result("Level must be a valid number.");
            }
        });





        // TODO (Tracks the hospital Emergency Status (0-8, 8 = full Code Blue).)
        // Add domain endpoints for alert-level-service here.
    }
}
