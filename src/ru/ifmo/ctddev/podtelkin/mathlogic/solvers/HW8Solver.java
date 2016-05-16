package ru.ifmo.ctddev.podtelkin.mathlogic.solvers;

import com.sun.deploy.util.Waiter;
import com.sun.tools.internal.ws.wscompile.WsgenTool;
import com.sun.xml.internal.xsom.impl.parser.PatcherManager;
import ru.ifmo.ctddev.podtelkin.mathlogic.exceptions.ArithmeticMatcherException;
import ru.ifmo.ctddev.podtelkin.mathlogic.expression.Expression;
import ru.ifmo.ctddev.podtelkin.mathlogic.kripke.Kripke;
import ru.ifmo.ctddev.podtelkin.mathlogic.kripke.KripkeBuilder;
import ru.ifmo.ctddev.podtelkin.mathlogic.ordinals.Ordinal;
import ru.ifmo.ctddev.podtelkin.mathlogic.parsers.ExpressionParser;
import ru.ifmo.ctddev.podtelkin.mathlogic.parsers.OrdinalParser;

import java.io.*;

/**
 * Created by vlad107 on 10.05.16.
 */
public class HW8Solver {
    private final String inputFile;
    private final String outputFile;

    public HW8Solver(String inputFile, String outputFile) {
        this.inputFile = inputFile;
        this.outputFile = outputFile;
    }

    public void run() {
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            String header = reader.readLine();
            String ordinalA = header.substring(0, header.indexOf("="));
            String ordinalB = header.substring(header.indexOf("=") + 1);
            Ordinal A = (new OrdinalParser(ordinalA)).parse();
            Ordinal B = (new OrdinalParser(ordinalB)).parse();
            if (Ordinal.cmpO(A, B) == 0) {
                writer.write("Равны\n");
                System.err.println("eq");
            } else {
                writer.write("Не равны\n");
                System.err.println("not eq");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ArithmeticMatcherException e) {
            e.printStackTrace();
        }
    }
}
