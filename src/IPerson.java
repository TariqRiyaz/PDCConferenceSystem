// The IPerson interface defines the rules for person-related entities like Attendees and Speakers.
// Used CHATGPT for understanding person interface to use to show case polymorphism and abstraction.
public interface IPerson {

    //Retrieves the name of the person.
    String getName();

    //Retrieves the email of the person.
    String getEmail();

    //Retrieves the type of the person, such as "Attendee" or "Speaker".
    String getType();

   //Provides a text representation of the person's details.
    String toTextFormat();
}
