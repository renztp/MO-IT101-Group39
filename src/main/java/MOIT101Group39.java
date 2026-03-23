import java.io.*;
import java.nio.file.*;
import java.util.*;

class MOIT101Group39 {
    static Scanner scanner = new Scanner(System.in);

    enum EmployeeFields {
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
        System.out.println("\n--- MotorPH Security ---");
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

    static double parseTimeToDecimal(String timeStr) {
        if (timeStr == null || !timeStr.contains(":")) {
            return 0.0;
        }
        try {
            // Split the string into hours and minutes
            String[] parts = timeStr.split(":");
            double hours = Double.parseDouble(parts[0]);
            double minutes = Double.parseDouble(parts[1]);

            // ACCURACY FIX: Divide minutes by 60 to get the true decimal.
            // Example: 8:45 becomes 8 + (45/60) = 8.75
            return hours + (minutes / 60.0);
        } catch (NumberFormatException e) {
            return 0.0;
        }
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

    public static double calculateSSS(double grossSalary) {
        double msc;
        if (grossSalary < 5250) {
            msc = 5000;
        } else if (grossSalary >= 34750) {
            msc = 35000;
        } else {
            msc = Math.floor((grossSalary - 250) / 500) * 500 + 500;
        }
        return msc * 0.05;
    }

    static double calculatePagIbig(double grossSalary) {
        double contribution;
        if (grossSalary <= 1500) {
            contribution = grossSalary * 0.01;
        } else {
            contribution = grossSalary * 0.02;
        }
        return contribution;
    }

    public static double calculatePhilHealth(double grossSalary) {
        double premium = grossSalary * 0.03;
        if (grossSalary <= 10000) {
            premium = 300;
        } else if (grossSalary >= 60000) {
            premium = 1800;
        }
        return premium / 2;
    }

    static double calculateWithholdingTax(double taxableIncome) {
        if (taxableIncome <= 20833) {
            return 0;
        } else if (taxableIncome <= 33332) {
            return (taxableIncome - 20833) * 0.15;
        } else if (taxableIncome <= 66666) {
            return (taxableIncome - 33333) * 0.20;
        } else if (taxableIncome <= 166666) {
            return (taxableIncome - 66667) * 0.25;
        } else if (taxableIncome <= 666666) {
            return (taxableIncome - 166667) * 0.30;
        } else {
            return (taxableIncome - 666667) * 0.35;
        }
    }

    public static List<List<String>> readFile(String filePath) {
        List<List<String>> table = new ArrayList<>();
        Path path = Paths.get(filePath);
        try (BufferedReader br = new BufferedReader(Files.newBufferedReader(path))) {
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
        for (List<String> strings : employeeDetailsTable) {
            int employeeId = Integer.parseInt(strings.getFirst());
            Map<EmployeeFields, Object> employeeObj = Map.ofEntries(
                    Map.entry(EmployeeFields.Id, employeeId),
                    Map.entry(EmployeeFields.LastName, strings.get(1)),
                    Map.entry(EmployeeFields.FirstName, strings.get(2)),
                    Map.entry(EmployeeFields.Birthday, strings.get(3)),
                    Map.entry(EmployeeFields.Address, strings.get(4)),
                    Map.entry(EmployeeFields.PhoneNumber, strings.get(5)),
                    Map.entry(EmployeeFields.SSSNum, strings.get(6)),
                    Map.entry(EmployeeFields.PhilhealthNum, strings.get(7)),
                    Map.entry(EmployeeFields.TinNum, strings.get(8)),
                    Map.entry(EmployeeFields.PagIbigNum, strings.get(9)),
                    Map.entry(EmployeeFields.Status, strings.get(10)),
                    Map.entry(EmployeeFields.Position, strings.get(11)),
                    Map.entry(EmployeeFields.ImmediateSuperVisor, strings.get(12)),
                    Map.entry(EmployeeFields.BasicSalary, strings.get(13)),
                    Map.entry(EmployeeFields.RiceSubs, strings.get(14)),
                    Map.entry(EmployeeFields.PhoneAllowance, strings.get(15)),
                    Map.entry(EmployeeFields.ClothingAllowance, strings.get(16)),
                    Map.entry(EmployeeFields.GrossSemiMonthlyRate,
                            strings.get(17)),
                    Map.entry(EmployeeFields.HourlyRate, strings.get(18)));
            employees.put(employeeId, employeeObj);
        }
    }

    static void processAttendanceFile(List<List<String>> attendanceTable) {
        // loop from 0 to size ng attendanceTable e.g 1000
        for (List<String> employee : attendanceTable) {
            // declare arrays of strings
            // get employeeId from "employee" tapos convert from "String" to "Int"
            int employeeId = Integer.parseInt(employee.getFirst());
            // get attendance date from "employee"
            String attendanceDate = employee.get(3);
            // replace "8:05" to "8.05" tapos convert from String to Double
            Double timeIn = parseTimeToDecimal(employee.get(4));
            Double timeOut = parseTimeToDecimal(employee.get(5));

            if (attendance.containsKey(employeeId)) {
                attendance.get(employeeId).put(attendanceDate, List.of(timeIn, timeOut));
            } else {
                attendance.put(employeeId, new LinkedHashMap<>(Map.of(
                        attendanceDate, List.of(timeIn, timeOut))));
            }
        }
    }

    static void processEmployee() {
        System.out.println("=".repeat(40));
        System.out.println("MOTORPH EMPLOYEE CONTROL");
        System.out.println("=".repeat(40));
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

    static void printEmployeeCutoff(int currentCutoff, String monthDisplay, Map<String, Double> employeeRecordResult, double totalHoursWorked) {
        if(currentCutoff != 1) {
            System.out.printf("""
                       %s
                       %S
                       >>> %s Cutoff <<<
                       From: %s to %s
                       Total hours worked: %.2f
                       Gross Salary: ₱%,.2f
                       Net Salary: ₱%,.2f
                       """,
                    "=".repeat(40),
                    monthDisplay,
                    "First",
                    "1",
                    "15",
                    totalHoursWorked,
                    employeeRecordResult.get("calculatedGrossSalary"),
                    employeeRecordResult.get("netSalary")
            );
        } else {
            System.out.printf("""
                       %s
                       >>> %s Cutoff <<<
                       From: %s to %s
                       Total hours worked: %.2f
                       Gross Salary: ₱%,.2f
                       Net Salary: ₱%,.2f
                       """,
                    "",
                    "Second",
                    "16",
                    "30",
                    totalHoursWorked,
                    employeeRecordResult.get("calculatedGrossSalary"),
                    employeeRecordResult.get("netSalary")
            );

            System.out.printf("""
                    Each Deduction:
                        SSS: %,.0f
                        Philhealth: %,.0f
                        Pag-Ibig: %,.0f
                        Tax: %,.0f
                    """,
                    employeeRecordResult.get("sss"),
                    employeeRecordResult.get("philHealth"),
                    employeeRecordResult.get("pagIbig"),
                    employeeRecordResult.get("witholdingTax"));
        }
    }

    static void calculateEmployeeRecord(Map<String, Double> employeeRecord, double totalHoursWorked, double hourlyRate) {
        employeeRecord.put("calculatedGrossSalary", calculateGrossSalary(totalHoursWorked, hourlyRate));
        employeeRecord.put("pagIbig", calculatePagIbig(employeeRecord.get("calculatedGrossSalary")));
        employeeRecord.put("philHealth", calculatePhilHealth(employeeRecord.get("calculatedGrossSalary")));
        employeeRecord.put("sss", calculateSSS(employeeRecord.get("calculatedGrossSalary")));
        employeeRecord.put("totalGovDeductions", employeeRecord.get("sss") + employeeRecord.get("pagIbig") + employeeRecord.get("philHealth"));
        employeeRecord.put("taxableIncome", employeeRecord.get("calculatedGrossSalary") - employeeRecord.get("totalGovDeductions"));
        employeeRecord.put("total", employeeRecord.get("calculatedGrossSalary") - employeeRecord.get("totalGovDeductions"));
        employeeRecord.put("witholdingTax", calculateWithholdingTax(employeeRecord.get("taxableIncome")));
        employeeRecord.put("totalDeductions", employeeRecord.get("totalGovDeductions") + employeeRecord.get("witholdingTax"));
    }

    static void processEmployeeRecord(Map<EmployeeFields, Object> selectedEmployee) {
        System.out.printf("""
                            Employee #: %s
                            Employee Name: %s %s
                            Birthday: %s
                            """,
                selectedEmployee.get(EmployeeFields.Id),
                selectedEmployee.get(EmployeeFields.FirstName),
                selectedEmployee.get(EmployeeFields.LastName),
                selectedEmployee.get(EmployeeFields.Birthday));

        Map<String, Double> employeeRecordResult = new HashMap<>();
        double totalHoursWorked = 0;
        String[] monthsDisplay = {"June", "July", "August", "September", "October", "November", "December"};
        int lastCutoff = -1;
        int lastMonth = 0;
        double employeeHourlyRate = Double.parseDouble(selectedEmployee.get(EmployeeFields.HourlyRate).toString());
        for (String recordDate : attendance.get(selectedEmployee.get(EmployeeFields.Id)).keySet()) {
            int month = Integer.parseInt(recordDate.split("/")[0]);
            int day = Integer.parseInt(recordDate.split("/")[1]);
            double timeIn = attendance.get(selectedEmployee.get(EmployeeFields.Id)).get(recordDate).get(0);
            double timeOut = attendance.get(selectedEmployee.get(EmployeeFields.Id)).get(recordDate).get(1);
            int currentCutoff = (day <= 15) ? 1 : 2;

            if (lastCutoff != -1 && (currentCutoff != lastCutoff || month != lastMonth)) {
                calculateEmployeeRecord(employeeRecordResult, totalHoursWorked, employeeHourlyRate);
                if (lastCutoff == 1) {
                    employeeRecordResult.put("netSalary", employeeRecordResult.get("calculatedGrossSalary"));
                } else {
                    employeeRecordResult.put("netSalary", employeeRecordResult.get("calculatedGrossSalary") - employeeRecordResult.get("totalDeductions"));
                }
                printEmployeeCutoff(currentCutoff, monthsDisplay[lastMonth - 6], employeeRecordResult, totalHoursWorked);

                totalHoursWorked = 0;
            }

            totalHoursWorked += calculateTotalHoursWorked(timeIn, timeOut);
            lastCutoff = currentCutoff;
            lastMonth = month;
        }
        printEmployeeCutoff(1, monthsDisplay[lastMonth - 6], employeeRecordResult, totalHoursWorked);
    }

    static void processAllEmployeeRecords() {
        for(int employeeId : employees.keySet()) {
            Map<EmployeeFields, Object> currentEmployee = employees.get(employeeId);
            System.out.println("/".repeat(40));
            processEmployeeRecord(currentEmployee);
            System.out.println("\\".repeat(40));
            System.out.println("\n");
        }
    }

    static void processPayrollStaff() {
        boolean active = true;
        while(active) {
            System.out.println("\n" + "=".repeat(40));
            System.out.println("MOTORPH PAYROLL STAFF CONTROL");
            System.out.println("=".repeat(40));
            System.out.println("1. Process Single Employee Payslip");
            System.out.println("2. Generate All-Employee Summary");
            System.out.println("3. Logout & Return to Main");
            System.out.print("Select Option: ");

            try {
                int choice = scanner.nextInt();
                switch(choice) {
                    case 1:
                        System.out.println("Enter the employee number: ");
                        int employeeNumber = scanner.nextInt();
                        Map<EmployeeFields, Object> selectedEmployee = employees.get(employeeNumber);
                        if (selectedEmployee == null) {
                            System.out.println("Employee number does not exist.");
                            return;
                        }

                        processEmployeeRecord(selectedEmployee);
                        active = false;
                        break;
                    case 2:
                        processAllEmployeeRecords();
                        active = false;
                        break;
                    case 3:
                        active = false;
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } catch(InputMismatchException e) {
                System.out.println("Error: Please enter a numeric value.");
                scanner.next(); // Clear buffer
            }
        }
    }

    static void initializePayrollSystem() {
        List<List<String>> attendanceRecordTable = readFile("src/main/resources/MotorPH_Employee Data - Attendance Record.csv");
        List<List<String>> employeeDetailsTable = readFile("src/main/resources/MotorPH_Employee Data - Employee Details.csv");
        processAttendanceFile(attendanceRecordTable);
        processEmployeeDetailsFile(employeeDetailsTable);
    }

    static void main(String[] args) {
        try {
            Map<String, String> loggedInUser = loginUser();
            if (loggedInUser == null) {
                System.out.println("Exiting program...");
                return;
            }
            initializePayrollSystem();

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
        }
    }
}