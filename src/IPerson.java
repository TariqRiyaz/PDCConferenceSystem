// The IPerson interface defines the rules for person-related entities like Attendees and Speakers.
// Used CHATGPT for understanding person interface to use to show case polymorphism and abstraction.
// Defines the structure for person-related entities like Attendees and Speakers.
public interface IPerson {
    String getName();       // Retrieves the person's name
    String getEmail();      // Retrieves the person's email
    String getType();       // Retrieves the type, e.g., "Attendee" or "Speaker"
    String toTextFormat();  // Provides a text representation of the person's details
}

