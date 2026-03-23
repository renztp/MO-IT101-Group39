## MotorPH Payroll System (MO-IT-101-Group39)
A Java-based console application designed to automate payroll processing for MotorPH. 
This system handles employee data ingestion, attendance tracking, and tax/contribution calculations (SSS, PhilHealth, Pag-IBIG).

## Project Plan
https://docs.google.com/spreadsheets/d/1P6BQezzOpxIuYmR_D_XAEHd22ARuDTq56wPruFV9f4I/edit?gid=2134013708#gid=2134013708

## Owners
**Renz Pulvira** - lr.rpulvira@mmdc.mcl.edu.ph\
**Ma. Kristina Bordo** - lr.mkbordo@mmdc.mcl.edu.ph\
**Ma. Delmar Damot** - lr.mddamot@mmdc.mcl.edu.ph\
**Marciano Oblina** - lr.moblina@mmdc.mcl.edu.ph

## Tech Stack
**Language**: Java 20\
**Build Tool**: Maven\
**Testing**: JUnit5

## How to run the program
1. Clone the repo
```bash
git clone https://github.com/renztp/MO-IT101-Group39.git
```
2. Import project to intellij Idea
3. Run MOIT101Group39.java

## Program Flow: MotorPH Payroll System

The application follows a structured lifecycle from initialization to secure data processing. Below is the step-by-step execution flow:

### 1. Initialization Phase
* **System Startup:** The `main` method triggers `initializePayrollSystem()`.
* **Data Ingestion:** The system reads two primary CSV files using `readFile()`:
    * `MotorPH_Employee Data - Employee Details.csv`
    * `MotorPH_Employee Data - Attendance Record.csv`

### 2. Security & Authentication
* **Login Prompt:** The `loginUser()` method prompts for credentials via the console.
* **Role Identification:** **Username: `employee`** → Routes to the **Employee Portal**.
    * **Username: `payroll_staff`** → Routes to the **Staff Management Console**.
* **Validation:** If credentials fail or are null, the program terminates immediately to prevent unauthorized access.

### 3. Execution Paths (Role-Based)

#### A. Employee Portal (`processEmployee`)
1.  User enters their specific **Employee Number**.
2.  Displays basic profile information (Name, Birthday, etc.) on found User by their employee ID.

#### B. Payroll Staff Console (`processPayrollStaff`)
* **Option 1: Single Employee Record**
    * Staff enters an ID -> System calculates Gross, SSS, PhilHealth, Pag-IBIG, and Tax.
    * Prints a formatted payslip with all deductions and Net Salary.
* **Option 2: All-Employee Record**
    * The system iterates through all IDs in the `employees` map.
    * Calculates and prints records for every employee in the system.

### 4. Calculation Logic (The "Engine")
When a record is processed, the following sub-routines are called:
* **`parseTimeToDecimal`**: Converts "08:30" string formats into `8.5` double values for math.
* **`calculateTotalHoursWorked`**: Subtracts the mandatory 1-hour lunch break and applies the 8:05 AM grace period logic.
* **`calculateEmployeeRecord`**: The central hub that orchestrates:
    * **Gross Salary** (Hours $\times$ Rate).
    * **Government Deductions** (SSS, PhilHealth, Pag-IBIG).
    * **Withholding Tax** (Based on taxable income brackets).

### 5. Termination
* The user selects the "Exit" or "Logout" option.
* The `Scanner` resource is closed, and the program exits gracefully.