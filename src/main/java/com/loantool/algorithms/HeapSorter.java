package com.loantool.algorithms;

import com.loantool.datastructures.ApplicantMaxHeap;
import com.loantool.models.Applicant;

import java.util.ArrayList;
import java.util.List;

//Heap Sort algorithm - uses Max Heap to sort applicants by risk score (descending)
public class HeapSorter {

    public List<Applicant> sort(List<Applicant> applicants) {
        if (applicants.size() <= 1) {
            return new ArrayList<>(applicants);
        }

        ApplicantMaxHeap heap = new ApplicantMaxHeap(applicants);
        List<Applicant> sorted = new ArrayList<>();

        while (!heap.isEmpty()) {
            sorted.add(heap.extractMax());
        }

        return sorted;
    }

    // Get top K applicants by risk score using heap
    public List<Applicant> getTopK(List<Applicant> applicants, int k) {
        if (applicants.isEmpty() || k <= 0) {
            return new ArrayList<>();
        }
        ApplicantMaxHeap heap = new ApplicantMaxHeap(applicants);
        return heap.extractTopK(Math.min(k, applicants.size()));
    }
}
