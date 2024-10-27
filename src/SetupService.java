public class SetupService {

    //Setting up database initialization and defualt populization
    public static void initializeAndPopulateDatabase() {
        DatabaseManager.initializeDatabase();
        if (!DatabaseManager.isSessionTablePopulated()) {
            System.out.println("Populating database with default sessions...");
            DatabaseManager.populateDefaultSessions();
        }
        DatabaseManager.displayAllSessions();
    }
}
