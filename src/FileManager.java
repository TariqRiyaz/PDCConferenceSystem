import java.io.*;
import java.util.ArrayList;
import java.util.List;

//File Manager class handles all the file operations required for the conference management system.
public class FileManager {

    //Writes the text format of an IPerson object to the specified file.
    public void writeToFile(IPerson person, String filePath) {
        try {
            File file = new File(filePath);

            // Create the file if it doesn't exist
            if (!file.exists()) {
                file.createNewFile();
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
                writer.write(person.toTextFormat());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Reads data from the specified file and returns a list of IPerson objects.
    public List<IPerson> readFromFile(String filePath) {
        List<IPerson> persons = new ArrayList<>();
        File file = new File(filePath);

        // If the file doesn't exist, returns IPerson object list.
        if (!file.exists()) {
            return persons;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Split the line into parts, based on established text format.
                String[] parts = line.split(",");
                String type = parts[0];
                String name = parts[1];
                String email = parts[2];
                String sessionOrTopic = parts[3];

                // Depending on the type if (Attendee), create an appropriate object
                if (type.equals("Attendee")) {
                    Attendee attendee = new Attendee(name, email);

                    // Handle multiple sessions by splitting on semicolon pattern
                    String[] sessionNames = sessionOrTopic.split(";");
                    for (String sessionName : sessionNames) {
                        attendee.addSession(sessionName.trim());
                    }
                    persons.add(attendee);

                // Depending on the type if (Speaker), create an appropriate object
                } else if (type.equals("Speaker")) {
                    persons.add(new Speaker(name, email, sessionOrTopic));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return persons;
    }

    //clear the file contents
    public void clearFile(String filePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, false))) {
            writer.write("");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
