package ru.ifmo.ctddev.podtelkin.mathlogic.parsers;

import ru.ifmo.ctddev.podtelkin.mathlogic.expression.Expression;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.concurrent.SynchronousQueue;

/**
 * Created by vlad107 on 04.05.16.
 */
public class HeaderParser {
    private String header;
    private List<Expression> hypotheses;
    private Expression A;
    private Expression B;

    public HeaderParser(String header) {
        this.header = header;
    }

    public void parse() {
        header = header.replaceAll("\\s+", "");
        String hypothesesStr = header.substring(0, header.indexOf("|-"));
        B = (new ExpressionParser(header.substring(header.indexOf("|-") + 2))).parse();
        List<String> hypothesesList = new ArrayList<>();
        StringBuilder curExpression = new StringBuilder();
        int bracketBalance = 0;
        for (int i = 0; i < hypothesesStr.length(); i++) {
            if ((hypothesesStr.charAt(i) == ',') && (bracketBalance == 0)) {
                hypothesesList.add(curExpression.toString());
                curExpression.setLength(0);
                continue;
            }
            curExpression.append(hypothesesStr.charAt(i));
            if (hypothesesStr.charAt(i) == '(') {
                ++bracketBalance;
            } else if (hypothesesStr.charAt(i) == ')') {
                --bracketBalance;
            }
        }
        if (curExpression.length() != 0) hypothesesList.add(curExpression.toString());
        if (hypothesesList.isEmpty()) {
//            throw new IllegalArgumentException("Must be at least one formula in header");
            A = null;
        } else {
            A = (new ExpressionParser(hypothesesList.get(hypothesesList.size() - 1))).parse();
            hypothesesList.remove(hypothesesList.size() - 1);
        }
        hypotheses = new ArrayList<>();
        for (String hypothesis : hypothesesList) {
            hypotheses.add((new ExpressionParser(hypothesis)).parse());
        }
    }

    public Expression getA() {
        return A;
    }

    public Expression getB() {
        return B;
    }

    public List<Expression> getHypotheses() {
        return hypotheses;
    }
}
