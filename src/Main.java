import java.util.List;
import java.util.Scanner;
import java.util.NoSuchElementException;
import java.lang.IllegalStateException;

// The Main class is the entry point of the Conference Management System.
// It provides a CLI for users to register as attendees, view sessions, and submit feedback.
public class Main {
    public static void main(String[] args) {
        // Path to the file storing participant data
        String filePath = "conference_data.txt";  
        // Initialize the conference system
        ConferenceSystem conferenceSystem = new ConferenceSystem(filePath);  
        // Load existing participants from the file
        conferenceSystem.loadParticipants();  
        
        Scanner scanner = new Scanner(System.in);  
        boolean running = true;
        
        try {
            while (running) {
                displayMenu(); 
                String choiceInput = scanner.nextLine();

                int choice = -1;
                //Error handling
                try {
                    choice = Integer.parseInt(choiceInput);
                } catch (NumberFormatException e) {
                    System.out.println("\n*** Invalid input. Please enter a number corresponding to your choice. ***\n");
                    continue;
                }

                // Process the user's choice using case
                switch (choice) {
                    case 1:
                        clearScreen();
                        registerAttendee(scanner, conferenceSystem);  // Handle attendee registration
                        break;
                    case 2:
                        clearScreen();
                        viewSessions();  // Display the list of available sessions
                        break;
                    case 3:
                        clearScreen();
                        submitFeedback(scanner, conferenceSystem);  // Handle feedback submission
                        break;
                    case 4:
                        running = false;  // Exit the program
                        System.out.println("\n*** Exiting... ***\n");
                        break;
                    default:
                        System.out.println("\n*** Invalid choice. Please try again. ***\n");  // Handle invalid menu choices
                }
            }
        } catch (NoSuchElementException | IllegalStateException e) {
            System.out.println("\n*** Program interrupted! Exiting... ***\n");  // Handle unexpected interruptions
        } finally {
            scanner.close();  // Close the scanner to release resources
        }
    }

   //Display conference management system contents
    private static void displayMenu() {
        System.out.println("\n==========================================");
        System.out.println("Welcome to the Conference Management System!");
        System.out.println("==========================================");
        System.out.println("1. Register as an Attendee");
        System.out.println("2. View Sessions");
        System.out.println("3. Submit Feedback");
        System.out.println("4. Exit");
        System.out.print("Enter your choice: ");
    }

    private static void clearScreen() {
        System.out.println("\n==========================================\n");
    }

    //Handles registering an attendee.
    private static void registerAttendee(Scanner scanner, ConferenceSystem conferenceSystem) {
        System.out.println("** Register as an Attendee **");
        System.out.print("Enter your name: ");
        // Get attendee's name
        String name = scanner.nextLine();  
        System.out.print("Enter your email: ");
        // Get attendee's email
        String email = scanner.nextLine();  

        System.out.println("\nAvailable Sessions:");
        // Retrieve the list of available sessions
        List<Session> sessions = Session.getAllSessions();  
        for (int i = 0; i < sessions.size(); i++) {
            System.out.println((i + 1) + ". " + sessions.get(i).getTitle() + " by " + sessions.get(i).getSpeaker());
        }

        System.out.print("\nChoose sessions to attend (enter numbers separated by commas): ");
        // Get user's session choices (multiple possible)
        String sessionChoices = scanner.nextLine();  
        // Split the input into individual session numbers
        String[] sessionNumbers = sessionChoices.split(",");  
        // Create a new Attendee object
        Attendee attendee = new Attendee(name, email);  

        // Register the attendee for the selected sessions
        for (String sessionNumber : sessionNumbers) {
            try {
                int sessionIndex = Integer.parseInt(sessionNumber.trim()) - 1;
                if (sessionIndex >= 0 && sessionIndex < sessions.size()) {
                    Session selectedSession = sessions.get(sessionIndex);
                    attendee.addSession(selectedSession.getTitle());  // Add the session to the attendee's list
                    System.out.println("\n*** Registered for session: " + selectedSession.getTitle() + " ***\n");
                } else {
                    System.out.println("\n*** Invalid session number: " + sessionNumber + " ***\n");
                }
            } catch (NumberFormatException e) {
                System.out.println("\n*** Invalid input: " + sessionNumber + ". Please enter valid session numbers. ***\n");
            }
        }

        // Add the attendee to the system
        conferenceSystem.addParticipant(attendee);  
    }

    // Displays a list of all available sessions.
    private static void viewSessions() {
        System.out.println("** View Available Sessions **");
        // Retrieve the list of available sessions
        List<Session> sessions = Session.getAllSessions();  
        System.out.println("\nAvailable Sessions:");
        for (Session session : sessions) {
            System.out.println(session.getTitle() + " by " + session.getSpeaker());
        }
        System.out.println("\n*** End of Session List ***\n");
    }

    //Handles submitting feedback for a registered session. 
    private static void submitFeedback(Scanner scanner, ConferenceSystem conferenceSystem) {
        System.out.println("** Submit Feedback **");
        System.out.print("Enter your email: ");
        String email = scanner.nextLine();

        // Checks if attendee exists therefore not everyone can submit feedback
        IPerson person = conferenceSystem.getParticipantByEmail(email);
        if (person == null || !(person instanceof Attendee)) {
            System.out.println("\n*** Attendee not found. Please register first. ***\n");
            return;
        }

        Attendee attendee = (Attendee) person;
        List<String> sessions = attendee.getSessionNames();

        if (sessions.isEmpty()) {
            System.out.println("\n*** No sessions registered for this attendee. ***\n");
            return;
        }

        System.out.println("\nRegistered Sessions:");
        for (int i = 0; i < sessions.size(); i++) {
            System.out.println((i + 1) + ". " + sessions.get(i));
        }

        // Prompt the user to select a session for feedback
        int sessionChoice = -1;
        while (true) {
            System.out.print("Enter the session number you want to provide feedback for: ");
            String input = scanner.nextLine();

            try {
                sessionChoice = Integer.parseInt(input);
                if (sessionChoice >= 1 && sessionChoice <= sessions.size()) {
                    break;  // Valid input, exit the loop
                } else {
                    System.out.println("\n*** Invalid session number. Please try again. ***\n");
                }
            } catch (NumberFormatException e) {
                System.out.println("\n*** Invalid input. Please enter a number corresponding to the session. ***\n");
            }
        }

        String selectedSession = sessions.get(sessionChoice - 1);

        System.out.print("Enter your feedback for the session \"" + selectedSession + "\": ");
        String feedbackText = scanner.nextLine();

        // Format the feedback to include the session name and attendee details for easier format and readability
        String formattedFeedback = String.format(
            "Attendee: %s\nEmail: %s\nSession: %s\nFeedback: %s\n\n",
            attendee.getName(), attendee.getEmail(), selectedSession, feedbackText
        );

        // Save the feedback to a file
        Feedback feedback = new Feedback(formattedFeedback);
        feedback.saveToFile("feedback.txt");
        System.out.println("\n*** Thank you for your feedback on the session: " + selectedSession + " ***\n");
    }    
}
