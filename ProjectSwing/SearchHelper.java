/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ProjectSwing;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.function.Function;

public class SearchHelper {
    // DSA 
    public static int binarySearch(int[] arr, int low, int high, int target) {
        if (low > high) {
            return -1;
        }
        int mid = low + (high - low) / 2;
        if (arr[mid] == target) {
            return mid;
        } else if (arr[mid] > target) {
            return binarySearch(arr, low, mid - 1, target);
        } else {
            return binarySearch(arr, mid + 1, high, target);
        }
    }

    public static int firstMatch(String[] keys, int low, int high, String target) {
        if (low > high) {
            return low;
        }
        int mid = low + (high - low) / 2;
        if (keys[mid].compareTo(target) >= 0) {
            return firstMatch(keys, low, mid - 1, target);  
        } else {
            return firstMatch(keys, mid + 1, high, target);  
        }
    }

    public static void insertionSort(int[] array, int n, int[] unsorted){
        int[] unsort = new int[n];            
        for (int i = 0; i < n; i++) {
            unsort[i] = unsorted[i];
        }
        for (int i = 0; i < n; i++) {
            int insertIndex = i;
            int currentValue = array[i];
            int j = i-1;

            while(j >= 0 && array[j]> currentValue){
                array[j+1] = array[j];
                insertIndex = j;
                j--;
            }
            array[insertIndex] = currentValue;
        }
    }

    public static <T> void insertionSort(ArrayList<T> list, Comparator<T> order){
        for (int i = 0; i < list.size(); i++) {
            int insertIndex = i;
            T currentValue = list.get(i);
            int j = i - 1;

            while (j >= 0 && order.compare(list.get(j), currentValue) > 0) {
                list.set(j + 1, list.get(j));
                insertIndex = j;
                j--;
            }
            list.set(insertIndex, currentValue);
        }
    }


    public static int parseId(String text, String prefix){
        try{
            return Integer.parseInt(text.trim().toUpperCase().replace(prefix, ""));
        }catch(NumberFormatException e){
            return -1;
        }
    }
    
    public static <T> boolean isSorted(ArrayList<T> list, Comparator<T> order){
        for (int i = 1; i < list.size(); i++) {
            if (order.compare(list.get(i - 1), list.get(i)) > 0) {
                return false;   // two neighbours are in the wrong order
            }
        }
        return true;
    }

    // Selection sort: for each position, find the smallest of the rest and swap it in
    public static <T> void selectionSort(ArrayList<T> list, Comparator<T> order){
        for (int i = 0; i < list.size() - 1; i++) {
            int minIndex = i;
            for (int j = i + 1; j < list.size(); j++) {
                if (order.compare(list.get(j), list.get(minIndex)) < 0) {
                    minIndex = j;
                }
            }
            if (minIndex != i) {
                T temp = list.get(i);
                list.set(i, list.get(minIndex));
                list.set(minIndex, temp);
            }
        }
    }

   
    public static <T> ArrayList<T> search(ArrayList<T> list, String keyword, Function<T, String>... fields){
        String word = keyword.trim().toLowerCase();
        if (word.isEmpty()) {
            return new ArrayList<>(list);
        }
        ArrayList<T> found = new ArrayList<>();
        for (Function<T, String> field : fields) {
            ArrayList<T> sorted = new ArrayList<>(list);
            sorted.sort((a, b) -> field.apply(a).toLowerCase().compareTo(field.apply(b).toLowerCase()));
            String[] keys = new String[sorted.size()];
            for (int i = 0; i < keys.length; i++) {
                keys[i] = field.apply(sorted.get(i)).toLowerCase();
            }
            int start = firstMatch(keys, 0, keys.length - 1, word);
            while (start < keys.length && keys[start].startsWith(word)) {
                if (!found.contains(sorted.get(start))) {
                    found.add(sorted.get(start));
                }
                start++;
            }
        }
        ArrayList<T> ordered = new ArrayList<>();
        for (T item : list) {
            if (found.contains(item)) {
                ordered.add(item);
            }
        }
        return ordered;
    }
}