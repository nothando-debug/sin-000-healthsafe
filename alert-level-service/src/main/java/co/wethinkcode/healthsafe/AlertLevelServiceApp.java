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



        // TODO (Tracks the hospital Emergency Status (0-8, 8 = full Code Blue).)
        // Add domain endpoints for alert-level-service here.
    }
}
