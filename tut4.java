import java.util.Scanner;

// Person class to store individual details
class Person {
    private int number;
    private String name;
    private double salary;
    private String occupation;
    
    // Constructor
    public Person(int number, String name, double salary, String occupation) {
        this.number = number;
        this.name = name;
        this.salary = salary;
        this.occupation = occupation;
    }
    
    // Getter methods
    public int getNumber() { return number; }
    public String getName() { return name; }
    public double getSalary() { return salary; }
    public String getOccupation() { return occupation; }
    
    // Method to display person details in a formatted way
    public void displayDetails() {
        System.out.println("\n=======================================");
        System.out.println("        PERSON DETAILS - A" + number);
        System.out.println("=======================================");
        System.out.println("Number        : A" + number);
        System.out.println("Name          : " + name);
        System.out.println("Salary        : $" + String.format("%.2f", salary));
        System.out.println("Occupation    : " + occupation);
        System.out.println("=======================================");
    }
}

public class tut4 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("==============================================");
        System.out.println("    PERSON MANAGEMENT SYSTEM");
        System.out.println("==============================================");
        
        // Step 1: Get array size
        System.out.print("Enter the number of people: ");
        int size = scanner.nextInt();
        
        // Arrays to store data
        int[] numbers = new int[size];
        String[] names = new String[size];
        double[] salaries = new double[size];
        String[] occupations = new String[size];
        Person[] people = new Person[size];
        
        // Step 2: Get numbers from user
        System.out.println("\nEnter " + size + " numbers for people:");
        for (int i = 0; i < size; i++) {
            System.out.print("Enter number " + (i + 1) + ": ");
            numbers[i] = scanner.nextInt();
        }
        
        // Clear the buffer
        scanner.nextLine();
        
        // Step 3: Get names corresponding to each number
        System.out.println("\nEnter names for each number:");
        for (int i = 0; i < size; i++) {
            System.out.print("Enter name for A" + numbers[i] + ": ");
            names[i] = scanner.nextLine();
        }
        
        // Step 4: Get salaries for each person
        System.out.println("\nEnter salaries for each person:");
        for (int i = 0; i < size; i++) {
            System.out.print("Enter salary for " + names[i] + " (A" + numbers[i] + "): $");
            salaries[i] = scanner.nextDouble();
        }
        
        // Clear the buffer
        scanner.nextLine();
        
        // Step 5: Get occupations for each person
        System.out.println("\nEnter occupations for each person:");
        for (int i = 0; i < size; i++) {
            System.out.print("Enter occupation for " + names[i] + " (A" + numbers[i] + "): ");
            occupations[i] = scanner.nextLine();
        }
        
        // Step 6: Create Person objects
        for (int i = 0; i < size; i++) {
            people[i] = new Person(numbers[i], names[i], salaries[i], occupations[i]);
        }
        
        // Step 7: Display all people in table format
        System.out.println("\n==============================================");
        System.out.println("           ALL PEOPLE SUMMARY");
        System.out.println("==============================================");
        System.out.printf("%-8s %-15s %-12s %-15s%n", "ID", "Name", "Salary", "Occupation");
        System.out.println("----------------------------------------------");
        for (int i = 0; i < size; i++) {
            System.out.printf("A%-7d %-15s $%-11.2f %-15s%n", 
                people[i].getNumber(), 
                people[i].getName(), 
                people[i].getSalary(), 
                people[i].getOccupation());
        }
        System.out.println("----------------------------------------------");
        
        // Step 8: Search functionality
        while (true) {
            System.out.println("\n==============================================");
            System.out.println("Search for a person by their number:");
            System.out.print("Enter number to search (or -1 to exit): A");
            int searchNumber = scanner.nextInt();
            
            if (searchNumber == -1) {
                System.out.println("Thank you for using Person Management System!");
                break;
            }
            
            // Search for the person
            boolean found = false;
            for (int i = 0; i < size; i++) {
                if (people[i].getNumber() == searchNumber) {
                    people[i].displayDetails();
                    found = true;
                    break;
                }
            }
            
            if (!found) {
                System.out.println("❌ Person with number A" + searchNumber + " not found!");
                System.out.println("Available numbers are:");
                for (int i = 0; i < size; i++) {
                    System.out.print("A" + people[i].getNumber() + " ");
                }
                System.out.println();
            }
        }
        
        scanner.close();
    }
}
