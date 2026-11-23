package dao.mongo;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class MongoConnection {
    private static MongoDatabase database;

    private MongoConnection() {
    }

    public static synchronized MongoDatabase getInstance() {
        if (database == null) {
            String uri = System.getenv().getOrDefault("FILM_MONGO_URI",
                    "mongodb://127.0.0.1:27017/?serverSelectionTimeoutMS=5000");
            String nom = System.getenv().getOrDefault("FILM_MONGO_DATABASE", "Filmographie");
            MongoClient client = MongoClients.create(uri);
            Runtime.getRuntime().addShutdownHook(new Thread(client::close));
            database = client.getDatabase(nom);
        }
        return database;
    }
}
