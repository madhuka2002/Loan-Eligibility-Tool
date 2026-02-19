package com.loantool.datastructures;

import com.loantool.models.Applicant;

import java.util.ArrayList;
import java.util.List;

/**
 * Binary Search Tree for storing applicants by risk score.
 * Enables O(log n) insert and range queries for applicants within a risk score range.
 */
public class ApplicantBST {
    private BSTNode root;
    private int size;

    private static class BSTNode {
        Applicant applicant;
        BSTNode left;
        BSTNode right;

        BSTNode(Applicant applicant) {
            this.applicant = applicant;
        }
    }

    public void insert(Applicant applicant) {
        root = insertRec(root, applicant);
    }

    private BSTNode insertRec(BSTNode node, Applicant applicant) {
        if (node == null) {
            size++;
            return new BSTNode(applicant);
        }
        // Key = risk score (higher score = better). We store descending order.
        double score = applicant.getRiskScore();
        double nodeScore = node.applicant.getRiskScore();

        if (score > nodeScore) {
            node.left = insertRec(node.left, applicant);
        } else if (score < nodeScore) {
            node.right = insertRec(node.right, applicant);
        } else {
            // Equal scores: go right to maintain insertion order
            node.right = insertRec(node.right, applicant);
        }
        return node;
    }

    /**
     * Find applicants within risk score range [minScore, maxScore].
     * Uses in-order traversal to collect matching nodes.
     */
    public List<Applicant> findInRange(double minScore, double maxScore) {
        List<Applicant> result = new ArrayList<>();
        findInRangeRec(root, minScore, maxScore, result);
        return result;
    }

    private void findInRangeRec(BSTNode node, double minScore, double maxScore, List<Applicant> result) {
        if (node == null) return;

        double score = node.applicant.getRiskScore();
        if (score >= minScore && score <= maxScore) {
            result.add(node.applicant);
        }
        if (score > minScore) {
            findInRangeRec(node.left, minScore, maxScore, result);
        }
        if (score < maxScore) {
            findInRangeRec(node.right, minScore, maxScore, result);
        }
    }

    /**
     * Get all applicants in descending risk score order (highest first).
     */
    public List<Applicant> getAllDescending() {
        List<Applicant> result = new ArrayList<>();
        inOrderDescending(root, result);
        return result;
    }

    private void inOrderDescending(BSTNode node, List<Applicant> result) {
        if (node == null) return;
        inOrderDescending(node.left, result);  // Left has higher scores
        result.add(node.applicant);
        inOrderDescending(node.right, result); // Right has lower scores
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return root == null;
    }

    public void clear() {
        root = null;
        size = 0;
    }
}