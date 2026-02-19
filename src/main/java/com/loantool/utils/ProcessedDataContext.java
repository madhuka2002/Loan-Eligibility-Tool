package com.loantool.utils;

import com.loantool.datastructures.ApplicantBST;
import com.loantool.datastructures.DecisionLinkedList;
import com.loantool.models.Applicant;
import com.loantool.models.LoanDecision;

import java.util.List;

/**
 * Holds the latest processed data including BST and Linked List structures.
 * Shared across panels for BST queries and Linked List audit trail.
 */
public class ProcessedDataContext {
    private static ProcessedDataContext instance;
    private ApplicantBST applicantBST;
    private DecisionLinkedList decisionLinkedList;
    private List<LoanDecision> lastDecisions;

    private ProcessedDataContext() {}

    public static synchronized ProcessedDataContext getInstance() {
        if (instance == null) {
            instance = new ProcessedDataContext();
        }
        return instance;
    }

    public void setProcessedData(List<Applicant> sortedApplicants, List<LoanDecision> decisions) {
        this.lastDecisions = decisions;

        // Build BST from applicants (by risk score)
        applicantBST = new ApplicantBST();
        for (Applicant a : sortedApplicants) {
            applicantBST.insert(a);
        }

        // Build Linked List from decisions (chronological audit trail)
        decisionLinkedList = new DecisionLinkedList();
        decisionLinkedList.addAll(decisions);
    }

    public void setFromLoadedDecisions(List<LoanDecision> decisions) {
        this.lastDecisions = decisions;
        decisionLinkedList = new DecisionLinkedList();
        decisionLinkedList.addAll(decisions);

        // Build BST from applicants in decisions
        applicantBST = new ApplicantBST();
        for (LoanDecision d : decisions) {
            applicantBST.insert(d.getApplicant());
        }
    }

    public ApplicantBST getApplicantBST() {
        return applicantBST;
    }

    public DecisionLinkedList getDecisionLinkedList() {
        return decisionLinkedList;
    }

    public List<LoanDecision> getLastDecisions() {
        return lastDecisions;
    }

    public boolean hasData() {
        return lastDecisions != null && !lastDecisions.isEmpty();
    }
}