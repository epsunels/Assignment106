package com.company;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.TreeSet;

public class TreeNode {
    private final Employee employee;

    private final List<TreeNode> directReports;

    private TreeNode parent;

    public TreeNode(Employee employee) {
        this.employee = employee;
        this.directReports = new ArrayList<>();
    }

    public void addDirectReport (TreeNode directReport){
        directReports.add(directReport);
        directReport.setParent(this);
    }

    public Employee getEmployee() {
        return employee;
    }

    public List<TreeNode> getDirectReports() {
        return directReports;
    }

    public TreeNode getParent() {
        return parent;
    }

    public void setParent(TreeNode parent) {
        this.parent = parent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TreeNode treeNode = (TreeNode) o;
        return Objects.equals(employee, treeNode.employee);
    }

    @Override
    public int hashCode() {
        return Objects.hash(employee);
    }
}
