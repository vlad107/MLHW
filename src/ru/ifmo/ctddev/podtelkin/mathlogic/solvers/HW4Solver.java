package ru.ifmo.ctddev.podtelkin.mathlogic.solvers;

import ru.ifmo.ctddev.podtelkin.mathlogic.Deductor;
import ru.ifmo.ctddev.podtelkin.mathlogic.annotation.Annotation;
import ru.ifmo.ctddev.podtelkin.mathlogic.exceptions.ProofException;
import ru.ifmo.ctddev.podtelkin.mathlogic.exceptions.SubstitutionException;
import ru.ifmo.ctddev.podtelkin.mathlogic.expression.Expression;
import ru.ifmo.ctddev.podtelkin.mathlogic.expression.Implication;
import ru.ifmo.ctddev.podtelkin.mathlogic.parsers.ExpressionParser;
import ru.ifmo.ctddev.podtelkin.mathlogic.parsers.HeaderParser;
import ru.ifmo.ctddev.podtelkin.mathlogic.parsers.ProofParser;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by vlad107 on 10.05.16.
 */
public class HW4Solver {
    private final String inputFile;
    private final String outputFile;

    public HW4Solver(String inputFile, String outputFile) {
        this.inputFile = inputFile;
        this.outputFile = outputFile;
    }

    public void run() {
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            String header = reader.readLine();
            HeaderParser headerParser = new HeaderParser(header);
            headerParser.parse();
            Expression A = headerParser.getA();
            Expression B = headerParser.getB();
            List<Expression> hypotheses = headerParser.getHypotheses();
            List<Expression> outline = new ArrayList<>();
            String curLine;
            while ((curLine = reader.readLine()) != null) {
                if (!curLine.isEmpty()) {
                    outline.add((new ExpressionParser(curLine)).parse());
                }
            }
            try {
                List<Annotation> annotations = (new ProofParser(A, B, hypotheses, outline)).annotate();
                for (Expression anOutline : outline) writer.write(anOutline.toString() + "\n");
//                List<Expression> proof =  (new Deductor(A, B, hypotheses, outline)).makeProofFromOutline();
//                for (Expression expr : proof) writer.write(expr.toString() + "\n");
//                Expression AB = (A == null ? B : new Implication(A, B));
//                ProofParser proofParser = new ProofParser(null, AB, hypotheses, proof);
//                proofParser.setMustBeHypothesis(true);
//                List<Annotation> annotations = proofParser.annotate();
                for (Annotation annotation : annotations) System.err.println(annotation.toString());
            } catch (ProofException | SubstitutionException e) {
                System.out.println(e.getMessage());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
