//The Speaker class represents a speaker in the conference.

public class Speaker extends Person {
    // The email of the speaker
    private String email;  
    // The topic that the speaker will present
    private String topic;  

    //Constructs a new Speaker with the specified name, email, and topic.
    public Speaker(String name, String email, String topic) {
        // Call the constructor of the Person class to set the name
        super(name);          
        this.email = email;
        this.topic = topic;
    }

    // Returns the type of this person, specifically "Speaker".
    @Override
    public String getType() {
        return "Speaker";
    }

    //Returns the email of the speaker.
    @Override
    public String getEmail() {
        return email;
    }

    //Returns the topic that the speaker will present.
    public String getTopic() {
        return topic;
    }

    // Provides a text format of the speaker's details for file storage.
    @Override
    public String toTextFormat() {
        return String.join(",", getType(), getName(), email, topic);
    }
}
