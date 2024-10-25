// The Person class is an abstract class for different types of people, like attendees and speakers.
// It implements IPerson and provides common methods for handling a person's name.

public abstract class Person implements IPerson {
    // The name of the person
    private String name;  

    //Constructs a new Person with the specified name.
    public Person(String name) {
        this.name = name;
    }

   //Returns the name of the person.
    @Override
    public String getName() {
        return name;
    }
}
