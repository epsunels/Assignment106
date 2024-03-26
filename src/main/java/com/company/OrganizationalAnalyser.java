package com.company;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class OrganizationalAnalyser {
    private static final double LOWER_BOUND_PERCENTAGE = 0.2;
    private static final double UPPER_BOUND_PERCENTAGE = 0.5;
    private final Map<String, Employee> employeeMap = new HashMap<>();
    private TreeNode ceo;

    public record ReportingResult(List<String> salaryReports, List<String> reportingItems) {
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("Usage: java OrganizationalAnalyser <input-file-path>");
            System.exit(1);
        }
        String filePath = args[0];
        OrganizationalAnalyser analyser = new OrganizationalAnalyser();
        ReportingResult reportingResult = analyser.analyseOrganizationalInputFile(filePath);
        analyser.printReport(reportingResult);
    }

    public ReportingResult analyseOrganizationalInputFile(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        if (!Files.exists(path)) {
            throw new IllegalArgumentException("File does not exists: " + filePath);
        }
        readDataFromFile(filePath);

        return new ReportingResult(analyseSalaries(), analyseReportingLines());
    }

    private void readDataFromFile(String filename) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            createEmployeeMap(br, getColumnIndices(br));
        }
    }

    private List<String> analyseSalaries() {
        List<String> salaryReport = new ArrayList<>();
        Map<String, List<Employee>> managerEmployees = employeeMap.values().stream()
                .filter(e -> !e.getManagerId().isEmpty())
                .collect(Collectors.groupingBy(Employee::getManagerId));

        for (Map.Entry<String, List<Employee>> entry : managerEmployees.entrySet()) {
            List<Employee> employeesUnderManager = entry.getValue();
            int totalSalary = employeesUnderManager.stream().mapToInt(Employee::getSalary).sum();
            int averageSalary = totalSalary / employeesUnderManager.size();
            Employee manager = employeeMap.get(entry.getKey());

            int managerSalary = manager.getSalary();
            int lowerBound = (int) (averageSalary * LOWER_BOUND_PERCENTAGE);
            int upperBound = (int) (averageSalary * UPPER_BOUND_PERCENTAGE);
            if (managerSalary < lowerBound) {
                salaryReport.add(manager + " earns " + (lowerBound - managerSalary) + " less than the threshold");
            } else if (managerSalary > upperBound) {
                salaryReport.add(manager + " earns " + (managerSalary - upperBound) + " more than the threshold");
            }
        }
        return salaryReport;
    }


    private void constructTree() {
        Map<String, TreeNode> nodeMap = new HashMap<>();
        for (Employee employee : employeeMap.values()) {
            TreeNode node = new TreeNode(employee);
            nodeMap.put(employee.getId(), node);
        }

        for (Employee employee : employeeMap.values()) {
            TreeNode node = nodeMap.get(employee.getId());
            if (employee.getManagerId().isEmpty()) {
                ceo = node;
            } else {
                TreeNode managerNode = nodeMap.get(employee.getManagerId());
                if (managerNode != null) {
                    managerNode.addDirectReport(node);
                }
            }
        }
    }

    private List<String> analyseReportingLines() {
        constructTree();
        if (Objects.isNull(ceo)) {
            throw new RuntimeException("No CEO found!");
        }

        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(ceo);
        List<String> reportingLineReport = new ArrayList<>();
        while (!queue.isEmpty()) {
            TreeNode currentNode = queue.poll();
            Employee currentEmployee = currentNode.getEmployee();
            int depth = getDepth(currentNode);
            String reportingLineReportItem;
            if (depth > 4) {
                reportingLineReportItem = "Long reporting line for " + currentEmployee + " by " + (depth - 4) + " managers.";
                reportingLineReport.add(reportingLineReportItem);
            }

            for (TreeNode directReport : currentNode.getDirectReports()) {
                queue.offer(directReport);
            }
        }
        return reportingLineReport;
    }

    private int getDepth(TreeNode node) {
        int depth = 0;
        while (Objects.nonNull(node)) {
            node = node.getParent();
            depth++;
        }
        return depth;
    }

    private void printReport(ReportingResult reportingResult) {
        System.out.println(" # SALARY REPORT #");
        reportingResult.salaryReports().forEach(System.out::println);
        System.out.println(" # REPORTING_LINE REPORT #");
        reportingResult.reportingItems().forEach(System.out::println);
    }

    private Map<String, Integer> getColumnIndices(BufferedReader br) throws IOException {
        String line;
        Map<String, Integer> columnIndices = new HashMap<>();
        if (Objects.nonNull(line = br.readLine())) {
            String[] headers = line.split(",");
            List<String> requiredHeaders = Arrays.asList("Id", "firstName", "lastName", "salary", "managerId");

            //check all headers is set
            Set<String> headerSet = new HashSet<>(Arrays.asList(headers));
            if (!headerSet.containsAll(requiredHeaders)) {
                throw new RuntimeException("Required headers not found in the input");
            }

            //assign indices based on col header names
            for (int i = 0; i < headers.length; i++) {
                columnIndices.put(headers[i], i);
            }

        } else {
            throw new RuntimeException("Check file content!");
        }
        return columnIndices;
    }

    private void createEmployeeMap(BufferedReader br, Map<String, Integer> columnIndices) throws IOException {
        String line;
        int lineCount = 0;
        int noManagerEmployeeCount = 0;
        while ((line = br.readLine()) != null) {
            lineCount++;
            if (lineCount > 1000) {
                throw new RuntimeException("More than 1000 lines in the input file");
            }

            String[] data = line.split(",");
            String id = getColumnValue(data, columnIndices, "Id");
            String firstName = getColumnValue(data, columnIndices, "firstName");
            String lastName = getColumnValue(data, columnIndices, "lastName");
            String salaryStr = getColumnValue(data, columnIndices, "salary");
            String managerId = getColumnValue(data, columnIndices, "managerId");

            if (id.isEmpty() || firstName.isEmpty() || lastName.isEmpty() || salaryStr.isEmpty()) {
                throw new RuntimeException("Missing field for the employee at line " + lineCount);
            }

            if (managerId.isEmpty()) {
                noManagerEmployeeCount++;
                if (noManagerEmployeeCount > 1) {
                    throw new RuntimeException("There cant be more than one ceo in the given file!");
                }
            }
            int salary = Integer.parseInt(salaryStr);
            Employee employee = new Employee(id, firstName, lastName, salary, managerId);
            employeeMap.put(employee.getId(), employee);
        }
    }

    private String getColumnValue(String[] data, Map<String, Integer> columnIndices, String columnName) {
        Integer index = columnIndices.get(columnName);
        return Objects.nonNull(index) && index < data.length ? data[index] : "";
    }

}
