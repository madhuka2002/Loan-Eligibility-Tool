package com.loantool.datastructures;

import java.util.*;

// Directed Graph representing the eligibility decision flow.
public class RulesFlowGraph {
    public enum RuleNode {
        START("Start"),
        INCOME_CHECK("Income Check"),
        DEBT_CHECK("Debt Ratio Check"),
        CREDIT_CHECK("Credit Score Check"),
        EMPLOYMENT_CHECK("Employment Check"),
        LOAN_AMOUNT_CHECK("Loan Amount Check"),
        ELIGIBLE("Eligible"),
        REJECTED("Rejected");

        private final String label;

        RuleNode(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }

    private final Map<RuleNode, List<RuleNode>> adjacencyList;

    public RulesFlowGraph() {
        adjacencyList = new EnumMap<>(RuleNode.class);
        for (RuleNode node : RuleNode.values()) {
            adjacencyList.put(node, new ArrayList<>());
        }
        buildDefaultFlow();
    }

    // Added secction to the decision tree to get FASLE
    private void buildDefaultFlow() {
        addEdge(RuleNode.START, RuleNode.INCOME_CHECK);
        addEdge(RuleNode.INCOME_CHECK, RuleNode.DEBT_CHECK);
        addEdge(RuleNode.INCOME_CHECK, RuleNode.REJECTED);
        addEdge(RuleNode.DEBT_CHECK, RuleNode.CREDIT_CHECK);
        addEdge(RuleNode.DEBT_CHECK, RuleNode.REJECTED);
        addEdge(RuleNode.CREDIT_CHECK, RuleNode.EMPLOYMENT_CHECK);
        addEdge(RuleNode.CREDIT_CHECK, RuleNode.REJECTED);
        addEdge(RuleNode.EMPLOYMENT_CHECK, RuleNode.LOAN_AMOUNT_CHECK);
        addEdge(RuleNode.EMPLOYMENT_CHECK, RuleNode.REJECTED);
        addEdge(RuleNode.LOAN_AMOUNT_CHECK, RuleNode.ELIGIBLE);
        addEdge(RuleNode.LOAN_AMOUNT_CHECK, RuleNode.REJECTED);
    }

    public void addEdge(RuleNode from, RuleNode to) {
        adjacencyList.get(from).add(to);
    }

    public List<RuleNode> getNeighbors(RuleNode node) {
        return new ArrayList<>(adjacencyList.get(node));
    }

    // BFS traversal from START to show the decision flow.
    public String bfsTraversal() {
        StringBuilder sb = new StringBuilder();
        sb.append("Decision Flow Graph (BFS from Start)\n");
        sb.append("====================================\n");

        Set<RuleNode> visited = new HashSet<>();
        Queue<RuleNode> queue = new LinkedList<>();
        queue.add(RuleNode.START);
        visited.add(RuleNode.START);

        int level = 0;
        while (!queue.isEmpty()) {
            int levelSize = queue.size();
            sb.append("Level ").append(level).append(": ");
            for (int i = 0; i < levelSize; i++) {
                RuleNode node = queue.poll();
                sb.append(node.getLabel());
                if (i < levelSize - 1)
                    sb.append(", ");

                for (RuleNode neighbor : getNeighbors(node)) {
                    if (!visited.contains(neighbor)) {
                        visited.add(neighbor);
                        queue.add(neighbor);
                    }
                }
            }
            sb.append("\n");
            level++;
        }
        return sb.toString();
    }

}
