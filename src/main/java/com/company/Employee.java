package com.company;

import java.util.Objects;

public class Employee {
    private final String id;
    private final String firstName;
    private final String lastName;
    private final int salary;
    private final String managerId;

    public Employee(String id, String firstName, String lastName, int salary, String managerId) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.salary = salary;
        this.managerId = managerId;
    }

    public String getId() {
        return id;
    }

    public int getSalary() {
        return salary;
    }

    public String getManagerId() {
        return managerId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Employee employee = (Employee) o;
        return salary == employee.salary && Objects.equals(id, employee.id) && Objects.equals(firstName, employee.firstName)
                && Objects.equals(lastName, employee.lastName) && Objects.equals(managerId, employee.managerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, lastName, salary, managerId);
    }

    @Override
    public String toString() {
        return firstName + " " + lastName;
    }
}
