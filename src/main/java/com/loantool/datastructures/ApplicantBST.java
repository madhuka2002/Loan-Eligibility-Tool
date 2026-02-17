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

   
}