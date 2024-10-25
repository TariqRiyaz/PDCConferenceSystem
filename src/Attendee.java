import java.util.ArrayList;
import java.util.List;

 // The Attendee class represents a person who attends sessions in the conference.
 // This class extends the Person class and implements additional functionality specific to attendees, such as managing the sessions they are registered for. (inheritance).
public class Attendee extends Person {
    // The email of the attendee
    private String email;             
    // A list of session names the attendee is registered for
    private List<String> sessionNames;

     //Constructs (constructor) a new Attendee with the specified name and email.
     // Argument No:1 The name of the attendee.
     //Argument No:2 The email of the attendee.
     
    public Attendee(String name, String email) {
        // Call the constructor of the Person class to set the name
        super(name);                    
        // Set the email for the attendee
        this.email = email;             
        // Initialize the list to store session names
        this.sessionNames = new ArrayList<>();  
    }

    // Returns the type of this person, specifically "Attendee".
    // This method overrides the abstract method in the Person class.
    
    @Override
    public String getType() {
        return "Attendee";
    }

    // Returns the email of the attendee.
    // This method overrides the abstract method in the Person class.
    @Override
    public String getEmail() {
        return email;
    }

    // Returns the list of session names the attendee is registered for.
    public List<String> getSessionNames() {
        return sessionNames;
    }

    // Adds a session name to the list of sessions this attendee is registered for.
    public void addSession(String sessionName) {
        this.sessionNames.add(sessionName);  // Add the session name to the list
    }

   // Provides a text format of the attendee's details, for file storage, easier management.
    @Override
    public String toTextFormat() {
        // Convert the list of session names to a single string, separated by semicolons
        String sessions = String.join(";", sessionNames);
        // Return the formatted string with type, name, email, and sessions
        return String.join(",", getType(), getName(), email, sessions);
    }
}
