package ru.ifmo.ctddev.podtelkin.mathlogic.solvers;

import ru.ifmo.ctddev.podtelkin.mathlogic.expression.Expression;
import ru.ifmo.ctddev.podtelkin.mathlogic.kripke.Kripke;
import ru.ifmo.ctddev.podtelkin.mathlogic.kripke.KripkeBuilder;
import ru.ifmo.ctddev.podtelkin.mathlogic.parsers.ExpressionParser;

import java.io.*;

/**
 * Created by vlad107 on 10.05.16.
 */
public class HW5Solver {
    private final String inputFile;
    private final String outputFile;

    public HW5Solver(String inputFile, String outputFile) {
        this.inputFile = inputFile;
        this.outputFile = outputFile;
    }

    public void run() {
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            String formula = reader.readLine();
            Expression expr = (new ExpressionParser(formula)).parse();
            Kripke model = (new KripkeBuilder(expr)).tryToBuild();
            if (model == null) {
                writer.write("Формула общезначима");
            } else {
                writer.write(model.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
