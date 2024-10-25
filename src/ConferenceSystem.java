import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Manages the participants of a conference, including adding, retrieving, loading, and saving them.
// Participants are stored in a Map with their email as the key for easy lookup.
public class ConferenceSystem {
    // Stores participants by email use of person interface
    private Map<String, IPerson> participants;  
    // Handles file operations
    private FileManager fileManager;            
    // Path to file for file storage of data
    private String filePath;                    

    //Initializes the ConferenceSystem with a specified file path.
    public ConferenceSystem(String filePath) {
        // Initialize the participants map
        this.participants = new HashMap<>();     
        // Initialize the FileManager
        this.fileManager = new FileManager();    
        // Set the file path
        this.filePath = filePath;                
    }

    //Adds a participant to the system and saves their information to the file.
    public void addParticipant(IPerson person) {
        // Add to the map by email for easier access later use cases
        participants.put(person.getEmail(), person);  
        // Save participant to file
        fileManager.writeToFile(person, filePath);    
    }

   //Retrieves a participant from the system by their email.
    public IPerson getParticipantByEmail(String email) {
        return participants.get(email);  
    }

    // Returns a list of all participants stored in the system.
    public List<IPerson> getAllParticipants() {
        return new ArrayList<>(participants.values());
    }

    // Loads participants from the file (Stored with participant data) into the system.
    public void loadParticipants() {
        List<IPerson> loadedParticipants = fileManager.readFromFile(filePath);
        for (IPerson person : loadedParticipants) {
            participants.put(person.getEmail(), person);
        }
    }

    //Saves participant's to the file
    public void saveParticipants() {
        for (IPerson person : participants.values()) {
            fileManager.writeToFile(person, filePath);
        }
    }

    //Removes all participants from file and map
    public void clearParticipants() {
        participants.clear();               
        fileManager.clearFile(filePath);
    }
}
