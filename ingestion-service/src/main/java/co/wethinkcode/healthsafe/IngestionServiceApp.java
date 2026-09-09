package co.wethinkcode.healthsafe;

import java.util.Map;

import io.javalin.Javalin;

public class IngestionServiceApp {

    public static void main(String[] args) {
        CsvDataLoader loader = new CsvDataLoader(); 
        Map<String, Ward> cleanWards = loader.loadWards("ingestion-service/src/main/resources/wards-outdated.csv");

        Javalin app = Javalin.create().start(7030);

        app.get("/health", ctx -> ctx.result("OK"));

        app.get("/wards", ctx -> {
            ctx.json(cleanWards.values()); 
        });

        app.get("/wards/{id}", ctx -> {
            String targetId = ctx.pathParam("id").toUpperCase();
            if (cleanWards.containsKey(targetId)) {
                ctx.json(cleanWards.get(targetId));
            } else {
                ctx.status(404).result("Ward not found");
            }
        });
    }





        




        // TODO: read and clean src/main/resources/wards-outdated.csv (wards, wings, specialist departments data —
        // trim whitespace, fix casing, normalize dates/booleans) and expose the
        // cleaned records here for the other services to consume.
    

}