package com.xhxj.jsongpttranslator;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        LinkedHashMap<String, String> before = new LinkedHashMap<>();
        before.put("1", "data");
        before.put("2", "data");
        before.put("3", "data");
        before.put("4", "data");
        before.put("5", "data");
        before.put("6", "data");

        LinkedHashMap<String, String> after = new LinkedHashMap<>();
        after.put("6", "data");
        after.put("3", "data");
        after.put("5", "data");

        String previousKey = null;
        for (String key : before.keySet()) {
            if (!after.containsKey(key)) {
                System.out.println("Missing key: " + key);
                if (previousKey != null) {
                    System.out.println("Previous key: " + previousKey);
                }
                String nextKey = getNextKey(before.keySet(), key);
                if (nextKey != null) {
                    System.out.println("Next key: " + nextKey);
                }
            }
            previousKey = key;
        }
    }

    private static String getNextKey(Set<String> keys, String currentKey) {
        Iterator<String> iterator = keys.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().equals(currentKey) && iterator.hasNext()) {
                return iterator.next();
            }
        }
        return null;
    }
}
