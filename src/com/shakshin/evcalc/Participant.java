package com.shakshin.evcalc;

import java.util.ArrayList;
import java.util.List;

public class Participant {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private List<String> usedExpences;

    public List<String> getUsedExpences() {
        if (usedExpences == null)
            usedExpences = new ArrayList<String>();
        return usedExpences;
    }

    public void setUsedExpences(List<String> usedExpences) {
        this.usedExpences = usedExpences;
    }

    public void printInfo() {
        System.out.println("Party name: " + name);
        System.out.println("Used expenses:");
        for (String ex : getUsedExpences())
            System.out.println("    " + ex);
    }

}
