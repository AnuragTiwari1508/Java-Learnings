import java.util.Scanner;

public class tut3 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("Program to find the smaller number between two numbers");
        System.out.println("=====================================================");
        
        // Take first number from user
        System.out.print("Enter the first number: ");
        int num1 = scanner.nextInt();
        
        // Take second number from user
        System.out.print("Enter the second number: ");
        int num2 = scanner.nextInt();
        
        // Compare and find the smaller number
        if (num1 < num2) {
            System.out.println("The smaller number is: " + num1);
        } else if (num2 < num1) {
            System.out.println("The smaller number is: " + num2);
        } else {
            System.out.println("Both numbers are equal: " + num1);
        }
        
        // Close scanner
        scanner.close();
    }
}
