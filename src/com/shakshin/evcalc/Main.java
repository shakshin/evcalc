package com.shakshin.evcalc;

public class Main {

    public static void main(String[] args) {
        try {

            Cmdline.process(args);

        } finally {
            try {
                Config.get().write();
            } catch (Exception e) {
                System.err.println("Can not write datafile: " + e.getMessage());
                e.printStackTrace(System.err);
            }
        }
    }
}
