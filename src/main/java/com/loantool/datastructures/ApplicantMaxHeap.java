package com.loantool.datastructures;

import com.loantool.models.Applicant;

import java.util.ArrayList;
import java.util.List;

public class ApplicantMaxHeap {
    private final List<Applicant> heap;
    private int size;

    public ApplicantMaxHeap() {
        this.heap = new ArrayList<>();
        this.size = 0;
    }

    public ApplicantMaxHeap(List<Applicant> applicants) {
        this();
        for (Applicant a : applicants) {
            insert(a);
        }
    }

    public void insert(Applicant applicant) {
        if (size >= heap.size()) {
            heap.add(applicant);
        } else {
            heap.set(size, applicant);
        }
        size++;
        heapifyUp(size - 1);
    }

    public Applicant extractMax() {
        if (size == 0)
            return null;
        Applicant max = heap.get(0);
        heap.set(0, heap.get(size - 1));
        size--;
        if (size > 0) {
            heapifyDown(0);
        }
        return max;
    }

    public Applicant peekMax() {
        return size > 0 ? heap.get(0) : null;
    }

    private void heapifyUp(int index) {
        while (index > 0) {
            int parent = (index - 1) / 2;
            if (heap.get(index).getRiskScore() <= heap.get(parent).getRiskScore()) {
                break;
            }
            swap(index, parent);
            index = parent;
        }
    }

    private void heapifyDown(int index) {
        while (true) {
            int left = 2 * index + 1;
            int right = 2 * index + 2;
            int largest = index;

            if (left < size && heap.get(left).getRiskScore() > heap.get(largest).getRiskScore()) {
                largest = left;
            }
            if (right < size && heap.get(right).getRiskScore() > heap.get(largest).getRiskScore()) {
                largest = right;
            }
            if (largest == index)
                break;

            swap(index, largest);
            index = largest;
        }
    }

    private void swap(int i, int j) {
        Applicant temp = heap.get(i);
        heap.set(i, heap.get(j));
        heap.set(j, temp);
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    // Build heap from list.
    public void buildHeap(List<Applicant> applicants) {
        heap.clear();
        heap.addAll(applicants);
        size = applicants.size();
        for (int i = size / 2 - 1; i >= 0; i--) {
            heapifyDown(i);
        }
    }

    // Extract top K applicants by risk score
    public List<Applicant> extractTopK(int k) {
        List<Applicant> topK = new ArrayList<>();
        int count = Math.min(k, size);
        for (int i = 0; i < count; i++) {
            topK.add(extractMax());
        }
        return topK;
    }
}