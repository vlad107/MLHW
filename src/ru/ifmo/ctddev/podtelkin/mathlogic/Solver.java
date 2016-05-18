package ru.ifmo.ctddev.podtelkin.mathlogic;

import ru.ifmo.ctddev.podtelkin.mathlogic.solvers.HW4Solver;
import ru.ifmo.ctddev.podtelkin.mathlogic.solvers.HW5Solver;
import ru.ifmo.ctddev.podtelkin.mathlogic.solvers.HW8Solver;

/**
 * Created by vlad107 on 04.05.16.
 *
 */

public class Solver {
    public static void main(String[] args) {
        if (args.length != 3) {
            System.err.println("Usage: Solver <homework number> <input file> <output file>");
            return;
        }
        switch (args[0]) {
            case "4":
                (new HW4Solver(args[1], args[2])).run();
                break;
            case "5":
                (new HW5Solver(args[1], args[2])).run();
                break;
            case "8":
                (new HW8Solver(args[1], args[2])).run();
                break;
        }
    }
}
