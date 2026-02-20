package com.loantool.algorithms;

import com.loantool.models.Applicant;

import java.util.ArrayList;
import java.util.List;

public class MergeSorter {

    public List<Applicant> sort(List<Applicant> applicants) {
        if (applicants.size() <= 1) {
            return new ArrayList<>(applicants);
        }

        // Convert to array for sorting
        Applicant[] array = applicants.toArray(new Applicant[0]);
        mergeSort(array, 0, array.length - 1);

        List<Applicant> sorted = new ArrayList<>();
        for (Applicant applicant : array) {
            sorted.add(applicant);
        }

        return sorted;
    }

    private void mergeSort(Applicant[] array, int left, int right) {
        if (left < right) {
            int mid = left + (right - left) / 2;

            // Recursively sort both halves
            mergeSort(array, left, mid);
            mergeSort(array, mid + 1, right);

            // Merge the sorted halves
            merge(array, left, mid, right);
        }
    }

    private void merge(Applicant[] array, int left, int mid, int right) {
        // Create temporary arrays
        int n1 = mid - left + 1;
        int n2 = right - mid;

        Applicant[] leftArray = new Applicant[n1];
        Applicant[] rightArray = new Applicant[n2];

        // Copy data to temp arrays
        System.arraycopy(array, left, leftArray, 0, n1);
        System.arraycopy(array, mid + 1, rightArray, 0, n2);

        // Merge the temp arrays
        int i = 0, j = 0, k = left;

        while (i < n1 && j < n2) {
            if (leftArray[i].compareTo(rightArray[j]) <= 0) {
                array[k] = leftArray[i];
                i++;
            } else {
                array[k] = rightArray[j];
                j++;
            }
            k++;
        }

        // Copy remaining elements
        while (i < n1) {
            array[k] = leftArray[i];
            i++;
            k++;
        }

        while (j < n2) {
            array[k] = rightArray[j];
            j++;
            k++;
        }
    }

    // Time Complexity: O(n log n) - best, average, and worst case
    // Space Complexity: O(n) - based with new temporaray data structures
}