import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

class MOIT101Group39 {
    static Scanner scanner = new Scanner(System.in);

    static enum EmployeeFields {
        Id,
        LastName,
        FirstName,
        Birthday,
        Address,
        PhoneNumber,
        SSSNum,
        PhilhealthNum,
        TinNum,
        PagIbigNum,
        Status,
        Position,
        ImmediateSuperVisor,
        BasicSalary,
        RiceSubs,
        PhoneAllowance,
        ClothingAllowance,
        GrossSemiMonthlyRate,
        HourlyRate,
    }

    static HashMap<Integer, Map<EmployeeFields, Object>> employees = new HashMap<>();
    static HashMap<Integer, LinkedHashMap<String, List<Double>>> attendance = new HashMap<>();

    static Map<String, String> loginUser() {
        String[] validUsers = {"employee", "payroll_staff"};
        System.out.println("Enter username:");
        String username = scanner.nextLine();
        System.out.println("Enter password:");
        String password = scanner.nextLine();
        boolean isUserValid = Arrays.asList(validUsers).contains(username);
        if (!isUserValid) {
            System.out.println("Incorrect username and/or password.");
            return null;
        }

        if (!(password.equals("12345"))) {
            System.out.println("Incorrect username and/or password.");
            return null;
        }

        return Map.of(
                "username", username,
                "password", password);
    }

    static double calculateTotalHoursWorked(double timeIn, double timeOut) {
        double adjOut = Math.min(timeOut, 17.0);
        double adjIn = timeIn;
        if (timeIn <= 8.0833) {
            adjIn = 8.0;
        }
        adjIn = Math.max(adjIn, 8.0);
        return (adjOut - adjIn) - 1.0;
    }

    static double calculateGrossSalary(double totalHoursWorked, double HourlyRate) {
        return totalHoursWorked * HourlyRate;
    }

    static double calculateNetSalary(double grossSalary, double totalDeductions) {
        double deduction = grossSalary * totalDeductions;
        return deduction * grossSalary;
    }

    static List<List<String>> readFile(String filePath) {
        List<List<String>> table = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            int x = 0;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                if (!(x == 0)) {
                    List<String> employee = Arrays.asList(values);
                    table.add(employee);
                }
                x++;
            }
        } catch (IOException e) {
            System.out.println("Error I/o error occured: " + e.getMessage() + "\nTerminating the program");
        }
        return table;
    }

    static void processEmployeeDetailsFile(List<List<String>> employeeDetailsTable) {
        for (int employeeDetailsRow = 0; employeeDetailsRow < employeeDetailsTable.size(); employeeDetailsRow++) {
            int employeeId = Integer.parseInt(employeeDetailsTable.get(employeeDetailsRow).getFirst());
            Map<EmployeeFields, Object> employeeObj = Map.ofEntries(
                    Map.entry(EmployeeFields.Id, employeeId),
                    Map.entry(EmployeeFields.LastName, employeeDetailsTable.get(employeeDetailsRow).get(1)),
                    Map.entry(EmployeeFields.FirstName, employeeDetailsTable.get(employeeDetailsRow).get(2)),
                    Map.entry(EmployeeFields.Birthday, employeeDetailsTable.get(employeeDetailsRow).get(3)),
                    Map.entry(EmployeeFields.Address, employeeDetailsTable.get(employeeDetailsRow).get(4)),
                    Map.entry(EmployeeFields.PhoneNumber, employeeDetailsTable.get(employeeDetailsRow).get(5)),
                    Map.entry(EmployeeFields.SSSNum, employeeDetailsTable.get(employeeDetailsRow).get(6)),
                    Map.entry(EmployeeFields.PhilhealthNum, employeeDetailsTable.get(employeeDetailsRow).get(7)),
                    Map.entry(EmployeeFields.TinNum, employeeDetailsTable.get(employeeDetailsRow).get(8)),
                    Map.entry(EmployeeFields.PagIbigNum, employeeDetailsTable.get(employeeDetailsRow).get(9)),
                    Map.entry(EmployeeFields.Status, employeeDetailsTable.get(employeeDetailsRow).get(10)),
                    Map.entry(EmployeeFields.Position, employeeDetailsTable.get(employeeDetailsRow).get(11)),
                    Map.entry(EmployeeFields.ImmediateSuperVisor, employeeDetailsTable.get(employeeDetailsRow).get(12)),
                    Map.entry(EmployeeFields.BasicSalary, employeeDetailsTable.get(employeeDetailsRow).get(13)),
                    Map.entry(EmployeeFields.RiceSubs, employeeDetailsTable.get(employeeDetailsRow).get(14)),
                    Map.entry(EmployeeFields.PhoneAllowance, employeeDetailsTable.get(employeeDetailsRow).get(15)),
                    Map.entry(EmployeeFields.ClothingAllowance, employeeDetailsTable.get(employeeDetailsRow).get(16)),
                    Map.entry(EmployeeFields.GrossSemiMonthlyRate,
                            employeeDetailsTable.get(employeeDetailsRow).get(17)),
                    Map.entry(EmployeeFields.HourlyRate, employeeDetailsTable.get(employeeDetailsRow).get(18)));
            employees.put(employeeId, employeeObj);
        }
    }

    static void processAttendanceFile(List<List<String>> attendanceTable) {
        for (int attendanceRow = 0; attendanceRow < attendanceTable.size(); attendanceRow++) {
            List<String> employee = attendanceTable.get(attendanceRow);
            int employeeId = Integer.parseInt(employee.getFirst());
            String attendanceDate = employee.get(3);
            Double timeIn = Double.parseDouble(employee.get(4).replace(":", "."));
            Double timeOut = Double.parseDouble(employee.get(5).replace(":", "."));

            if (attendance.containsKey(employeeId)) {
                attendance.get(employeeId).put(attendanceDate, List.of(timeIn, timeOut));
            } else {
                attendance.put(employeeId, new LinkedHashMap<>(Map.of(
                        attendanceDate, List.of(timeIn, timeOut))));
            }
        }
    }

    static void processEmployee() {
        System.out.println("""
                1. Enter your employee number
                2. Exit the program
                """);
        int userChoice = scanner.nextInt();
        if (userChoice == 2)
            return;
        if (userChoice > 2) {
            System.out.println("Input was out of range on the available choices");
            return;
        }
        System.out.println("Enter employee number:");
        int employeeNumber = scanner.nextInt();
        Map<EmployeeFields, Object> employee = employees.get(employeeNumber);
        System.out.printf("""
                        Employee Number: %s
                        Employee Name: %s %s
                        Birthday: %s
                        """, employeeNumber, employee.get(EmployeeFields.FirstName), employee.get(EmployeeFields.LastName),
                employee.get(EmployeeFields.Birthday));
    }

    static void processPayrollStaff() {
        System.out.println("""
                1. Process payroll
                2. Exit the program
                """);
        int userChoice = scanner.nextInt();
        if (userChoice == 2)
            return;
        if (userChoice > 2) {
            System.out.println("Input was out of range on the available choices");
            return;
        }

        System.out.println("""
                1. One employee
                2. All employees
                3. Exit the program
                """);

        userChoice = scanner.nextInt();
        if (userChoice == 3)
            return;
        if (userChoice > 3) {
            System.out.println("Input was out of range based on available choices");
            return;
        }

        if (userChoice == 1) {
            System.out.println("Enter the employee number: ");
            int employeeNumber = scanner.nextInt();
            Map<EmployeeFields, Object> selectedEmployee = employees.get(employeeNumber);
            if (selectedEmployee == null) {
                System.out.println("Employee number does not exist.");
                return;
            }

            System.out.printf("""
                            Employee #: %s
                            Employee Name: %s %s
                            Birthday: %s
                            """,
                    selectedEmployee.get(EmployeeFields.Id),
                    selectedEmployee.get(EmployeeFields.FirstName),
                    selectedEmployee.get(EmployeeFields.LastName),
                    selectedEmployee.get(EmployeeFields.Birthday));

            double totalHoursWorked = 0;
            String[] monthsDisplay = {"June", "July", "August", "September", "October", "November", "December"};

            int lastCutoff = -1;
            int lastMonth = 0;
            for (String recordDate : attendance.get(selectedEmployee.get(EmployeeFields.Id)).keySet()) {
                int month = Integer.parseInt(recordDate.split("/")[0]);
                int day = Integer.parseInt(recordDate.split("/")[1]);
                double timeIn = attendance.get(selectedEmployee.get(EmployeeFields.Id)).get(recordDate).get(0);
                double timeOut = attendance.get(selectedEmployee.get(EmployeeFields.Id)).get(recordDate).get(1);
                int currentCutoff = (day <= 15) ? 1 : 2;

                if (lastCutoff != -1 && (currentCutoff != lastCutoff || month != lastMonth)) {
                    System.out.printf("""
                            %s
                            %s Cutoff
                            From: %s to %s
                            totalHoursWorked: %.2f
                            """, monthsDisplay[lastMonth - 6], (currentCutoff != 1 ? "First" : "Second"), (currentCutoff != 1) ? "1" : "16", (currentCutoff != 1) ? "15" : "30", totalHoursWorked);
                    totalHoursWorked = 0;
                }

                totalHoursWorked += calculateTotalHoursWorked(timeIn, timeOut);
                lastCutoff = currentCutoff;
                lastMonth = month;
            }

            System.out.printf("""
                            December
                            Second Cutoff
                            From: 16 to 30
                            totalHoursWorked: %.2f
                            """, totalHoursWorked);
        }
    }

    static void initializePayrollSystem(Map<String, String> loggedInUser) throws IOException {
        List<List<String>> attendanceRecordTable = readFile("src/assets/MotorPH_Employee Data - Attendance Record.csv");
        List<List<String>> employeeDetailsTable = readFile("src/assets/MotorPH_Employee Data - Employee Details.csv");
        processAttendanceFile(attendanceRecordTable);
        processEmployeeDetailsFile(employeeDetailsTable);
    }

    public static void main(String[] args) {
        double totalHoursWorked = calculateTotalHoursWorked(7.50, 17.3);
        try {
            Map<String, String> loggedInUser = loginUser();
            if (loggedInUser == null) {
                System.out.println("Exiting program...");
                return;
            }
            initializePayrollSystem(loggedInUser);

            if (loggedInUser.get("username").equals("employee")) {
                processEmployee();
            } else {
                processPayrollStaff();
            }

            System.out.println("Exiting program...");
            scanner.close();
        } catch (Exception e) {
            System.out.println(e.getMessage() + "\nTerminating the program...");
            scanner.close();
            return;
        }
    }
}