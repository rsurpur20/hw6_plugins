package edu.cmu.cs.cs214.analyzer.framework;

import java.util.ArrayList;

public class Util {
    public static <T> String arrayListToString(ArrayList<T> list, boolean withQuotes) {
        if (list == null) {
            return "[]";
        }
        ArrayList<String> inner = new ArrayList<>();
        for (T element : list) {
            String s = element.toString();
            if (withQuotes) {
                s = "\"" + s + "\"";
            }
            inner.add(s);
        }
        return "[" + String.join(", ", inner) + "]";
    }

    public static <T> String arrayListToString(ArrayList<T> list) {
        return arrayListToString(list, false);
    }
}
