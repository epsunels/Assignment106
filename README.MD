# ORGANIZATIONAL ANALYSER

#### The Organizational Analyser is a java program designed to analyse the organizational structure of a company based on employee data provided in a csv file.
#### It helps identify improvements in salary distribution and reporting line in the company.

---
## FEATURES

- Salary analysis: Detects  salary discrepancies and highlights managers who earn significantly less or more than the average salary of their direct subordinates.
- Reporting line analysis:  Identifies  employees who have more than four managers between them and the CEO, indicating potential inefficiencies in the organizational hierarchy.

---
## **The program performs the following tasks:**
1. Reads employee data from a CSV file.
2. Identifies managers who earn less or more than the expected salary thresholds (%20 - %50).
3. Identifies employees with reporting line that are too long(more than four).
4. Generates reports based on the analysis.

---
## Installation  
1. Clone Repository
`git clone https://github.com/epsunels/Assignment106.git 
`
2. Go to project root
`cd Assignment106
`
3. Ensure you have correct java on the PATH - JAVA-17
Exm: `export PATH="C:\Users\Serkan_Sunel\.jdks\corretto-17.0.10\bin":$PATH
`
4. Build project
`mvn clean package
`
5. Run test cases
`mvn test
`

---
## Usage  
1. Ensure  organizational data stored in a csv file with the format.
`Id,firstName,lastName,salary,managerId`

2. Run the organizational analyser with the path to your CSV file as an argument
`java -jar target/Assignment106-1.0-SNAPSHOT.jar path/to/your/file.csv`
Exm: `$ java -jar target/Assignment106-1.0-SNAPSHOT.jar C:\\Users\\Serkan_Sunel\\IdeaProjects\\demo\\Assignment106\\src\\main\\resources\\employees.csv
`
3. The Application will analyse the data and provide the insights such as salary discrepancies and long reporting lines.

---
## Sample File Content

Sample file can be  found under **_src/main/resources/employees.csv_**



