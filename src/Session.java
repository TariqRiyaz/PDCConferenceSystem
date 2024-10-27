// Represents a session in the conference, including its title and speaker.
public class Session {
    private String title;
    private String speaker;

    // Constructs a new Session with the specified title and speaker.
    public Session(String title, String speaker) {
        this.title = title;
        this.speaker = speaker;
    }

    public String getTitle() {
        return title;
    }

    public String getSpeaker() {
        return speaker;
    }
}
