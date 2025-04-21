
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

abstract class Employee{
    private String name;
    private int id;

    public Employee(String name, int id){
        this.name = name;
        this.id = id;
    }
    public String getName(){
        return name;
    }
    public int getId(){
        return id;
    }
    public abstract double calculateSalary();
    
    @Override
    public String toString(){
        return "Employee [Id = "+id+", Name = "+name+", Salary = "+calculateSalary()+"]";
    } 
}

class FullTimeEmployee extends Employee{
    private double monthlySalary;
    public FullTimeEmployee(String name, int id, double monthlySalary){
        super( name, id);
        this.monthlySalary = monthlySalary;    
    }

    @Override
    public double calculateSalary(){
        return monthlySalary;
    }

}

class  PartTimeEmployee extends Employee{
    private double hourlyRate;
    private double hourWorked;
    public PartTimeEmployee(String name, int id, double hourlyRate, double hourWorked){
        super(name, id);
        this.hourlyRate = hourlyRate;
        this.hourWorked = hourWorked;
    }

    public double getRate(){
        return hourlyRate;
    }
    public double getHours(){
        return hourWorked;
    }
    @Override
    public double calculateSalary(){
        return hourWorked * hourlyRate;
    }

}

class PayrollSystem{
    Connection connection;
    Scanner sc;
    public PayrollSystem(Connection connection, Scanner sc){
        this.connection = connection;
        this.sc = sc;
    }

    public void addPartTimeEmp(){
        try {
            String add_query = "INSERT INTO part_time_emp(id, name, dept, worked_hour, hourly_rate, salary) VALUES(?,?,?,?,?,?)";
            System.out.println("");
            sc.nextLine();
            System.out.print("Enter emp name: ");
            String empname = sc.nextLine();
            System.out.print("Enter emp dept: ");
            String empdept = sc.nextLine();
            System.out.print("Enter hourly rate: ");
            double hourlyRate = sc.nextDouble();
            System.out.print("Enter how many hours worked: ");
            double hourWorked = sc.nextDouble();
           
            int random4Digit = (int)(Math.random() * 9000) + 1000;
            PreparedStatement preparedStatement = connection.prepareStatement(add_query);
            preparedStatement.setInt(1, random4Digit);
            preparedStatement.setString(2, empname);
            preparedStatement.setString(3, empdept);
            preparedStatement.setDouble(4, hourWorked);
            preparedStatement.setDouble(5, hourlyRate);
            PartTimeEmployee partTimeEmployee = new  PartTimeEmployee(empname, random4Digit, hourlyRate, hourWorked);
            Double salary = partTimeEmployee.calculateSalary();
            preparedStatement.setDouble(5, salary);
            int rowEffected = preparedStatement.executeUpdate();
            if (rowEffected > 0) {
                System.out.println("PartTimeEmployee Added Successfully....");
            } else {
                System.out.println("PartTimeEmployee Add Failed....");
            }
            

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void addFullTimeEmp(){
        try {
            String add_query = "INSERT INTO full_time_emp(id,name, dept, salary) VALUES(?,?,?,?)";
            System.out.println("");
            sc.nextLine();
            System.out.print("Enter emp name: ");
            String empname = sc.nextLine();
            System.out.print("Enter emp dept: ");
            String empdept = sc.nextLine();
            System.out.print("Enter emp salary: ");
            double salary = sc.nextDouble();
           
            int random4Digit = (int)(Math.random() * 9000) + 1000;
            PreparedStatement preparedStatement = connection.prepareStatement(add_query);
            preparedStatement.setInt(1, random4Digit);
            preparedStatement.setString(2, empname);
            preparedStatement.setString(3, empdept);
            preparedStatement.setDouble(4, salary);
            int rowEffected = preparedStatement.executeUpdate();
            if (rowEffected > 0) {
                System.out.println("FullTimeEmployee Added Successfully....");
            } else {
                System.out.println("FullTimeEmployee Add Failed....");
            }
            

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
       
        
    }

    public void displayEmployee(){
       
        try {
            String query = "SELECT id, name, dept, NULL AS worked_hour, NULL AS hourly_rate, salary FROM full_time_emp UNION ALL SELECT * FROM part_time_emp";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet rs = preparedStatement.executeQuery();
            System.out.println("");
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String dept = rs.getString("dept");
                Double salary = rs.getDouble("salary");
                Double worked_hour = rs.getDouble("worked_hour");
                Double hourly_rate = rs.getDouble("hourly_rate");
                if (worked_hour!=0.0 && hourly_rate!=0.0) {
                    System.out.println("Employee Id: "+id);
                    System.out.println("Employee Name: "+name);
                    System.out.println("Employee Dept: "+dept);
                    System.out.println("Hour Worked: "+worked_hour);
                    System.out.println("Hourly Rate: "+hourly_rate);
                    System.out.println("Salary: "+salary);
                    System.out.println("Status: Part Time Employee");
                }else{
                    System.out.println("Employee Id: "+id);
                    System.out.println("Employee Name: "+name);
                    System.out.println("Employee Dept: "+dept);
                    System.out.println("Salary: "+salary);
                    System.out.println("Status: Full Time Employee");
                }
                System.out.println("--------------------------------------");

            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void removeEmployee(){
       try {
        String delete_query = "DELETE FROM full_time_emp WHERE id = ? UNION ALL DELETE FROM part_time_emp WHERE id = ? ";
        System.out.println("");
        System.out.print("Enter the emp id to remove: ");
        int delete_id = sc.nextInt();
        String deleteFullTime = "DELETE FROM full_time_emp WHERE id = ?";
        String deletePartTime = "DELETE FROM part_time_emp WHERE id = ?";

        PreparedStatement psFull = connection.prepareStatement(deleteFullTime);
        psFull.setInt(1, delete_id);
        int rowsFull = psFull.executeUpdate();

        PreparedStatement psPart = connection.prepareStatement(deletePartTime);
        psPart.setInt(1, delete_id);
        int rowsPart = psPart.executeUpdate();

        if (rowsFull > 0 || rowsPart > 0) {
            System.out.println("Employee deleted successfully.");
        } else {
            System.out.println("No employee found with the given ID.");
        }
       } catch (SQLException e) {
            System.out.println(e.getMessage());
       }
       
    }

    public void findEmployee() {
        try {
            String find_query = "SELECT id, name, dept, NULL AS worked_hour, NULL AS hourly_rate, salary FROM full_time_emp WHERE id = ? UNION ALL SELECT * FROM part_time_emp WHERE id = ? ";
            System.out.println("");
            System.out.print("Enter the emp id to find: ");
            int find_id = sc.nextInt();  
            PreparedStatement preparedStatement = connection.prepareStatement(find_query);
            preparedStatement.setInt(1, find_id);
            preparedStatement.setInt(2, find_id);
            ResultSet rs = preparedStatement.executeQuery();
                boolean found = false;
                while (rs.next()) {
                    found = true;
                    int id = rs.getInt("id");
                    String name = rs.getString("name");
                    String dept = rs.getString("dept");
                    Double salary = rs.getDouble("salary");
                    Double worked_hour = rs.getDouble("worked_hour");
                    Double hourly_rate = rs.getDouble("hourly_rate");
                    if (worked_hour!=0.0 && hourly_rate!=0.0) {
                        System.out.println("");
                        System.out.println("Employee Id: "+id);
                        System.out.println("Employee Name: "+name);
                        System.out.println("Employee Dept: "+dept);
                        System.out.println("Hour Worked: "+worked_hour);
                        System.out.println("Hourly Rate: "+hourly_rate);
                        System.out.println("Salary: "+salary);
                        System.out.println("Status: Part Time Employee");
                    }else{
                        System.out.println("");
                        System.out.println("Employee Id: "+id);
                        System.out.println("Employee Name: "+name);
                        System.out.println("Employee Dept: "+dept);
                        System.out.println("Salary: "+salary);
                        System.out.println("Status: Full Time Employee");
                    }  
                }
                if (!found) {
                    System.out.println("Employee doesn't exist to give id.");
                } 
                 
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
       
    }
    
    
}

public class App {
    private static final String url = "jdbc:mysql://localhost:3306/employee_payroll_system";
    private static final String username = "root";
    private static final String password= "Usama@123456";
    

    public static  void menu(Connection connection, Scanner sc){
        PayrollSystem system = new PayrollSystem(connection, sc);
        int choice = 100;
        while (choice!=0) {
        System.out.println("");
        System.out.println("--------------------------------------");
        System.out.println("-------Employee Payroll System--------");
        System.out.println("--------------------------------------");
        System.out.println("");
        System.out.println("1] Add Employee");
        System.out.println("2] Get Employee");
        System.out.println("3] Remove Employee");
        System.out.println("4] Find Employee");
        System.out.println("0] Exit");
        System.out.println("");
        System.out.print("Enter you choice: ");
        choice = sc.nextInt();
        switch (choice) {
            case 1 -> {
                System.out.println("");
                System.out.println("5] Add Full Time Employee");
                System.out.println("6] Add Part Time Employee");
                System.out.println("9] Back Main Manu");
                System.out.println("");
                System.out.print("Enter you choice: ");
                choice = sc.nextInt();
                if (choice == 5) {
                    system.addFullTimeEmp();
                }  else if (choice == 6) {
                    system.addPartTimeEmp();
                } else if (choice == 9) {
                    menu(connection, sc);
                }
                else{
                    System.out.println("Enter correct choice...");
                }
            }
            case 2 -> system.displayEmployee();
            case 3 -> system.removeEmployee();
            case 4 -> system.findEmployee();
            case 0 -> {
            }
            default -> System.out.println("Enter wrong choice....");
        }
    }
    }
    public static void main(String[] args) throws Exception { 
        
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("Driver loaded successfully.");
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            System.out.println("Connection established successfully.");
            Scanner sc = new  Scanner(System.in);
            menu(connection, sc);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
       
    }
}
