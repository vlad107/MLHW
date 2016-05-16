package ru.ifmo.ctddev.podtelkin.mathlogic.parsers;

import com.sun.istack.internal.Pool;
import javafx.util.Pair;
import ru.ifmo.ctddev.podtelkin.mathlogic.Helper;
import ru.ifmo.ctddev.podtelkin.mathlogic.annotation.*;
import ru.ifmo.ctddev.podtelkin.mathlogic.exceptions.ArithmeticMatcherException;
import ru.ifmo.ctddev.podtelkin.mathlogic.exceptions.ProofException;
import ru.ifmo.ctddev.podtelkin.mathlogic.exceptions.SubstitutionException;
import ru.ifmo.ctddev.podtelkin.mathlogic.expression.*;

import java.util.*;

/**
 * Created by vlad107 on 04.05.16.
 *
 */
public class ProofParser {
    private static final List<Expression> axioms = Helper.generateAxioms();
    private static final int BASE_AXIOMS_CNT = 10;
    private static final int ARITHMETIC_AXIOMS_CNT = 8;

    private boolean mustBeHypothesis;
    private final Expression A;
    private final Expression B;
    private final Map<Integer, Integer> hypotheses;
    private final List<Expression> hypothesesExpr;
    private final List<Expression> outline;
    private final Map<String, String> freeVariablesFromHypotheses;

    public ProofParser(Expression a, Expression b, List<Expression> hypotheses, List<Expression> outline) {
        hypothesesExpr = hypotheses;
        mustBeHypothesis = false;
        A = a;
        B = b;
        this.outline = outline;
        this.hypotheses = new HashMap<>();
        for (int i = 0; i < hypotheses.size(); i++) {
            this.hypotheses.put(hypotheses.get(i).hashCode(), i + 1);
        }
        this.freeVariablesFromHypotheses = new HashMap<>();
//        for (Expression hypothesis : hypotheses) {
//            Set<String> curFreeVariables = hypothesis.getFreeVariables();
//            for (String freeVar : curFreeVariables) freeVariablesFromHypotheses.put(freeVar, hypothesis.toString());
//        }
        if (A != null) {
            Set<String> curFreeVariables = A.getFreeVariables(false);
            for (String freeVar : curFreeVariables) freeVariablesFromHypotheses.put(freeVar, A.toString());
        }
    }

    public void setMustBeHypothesis(boolean mustBeHypothesis) {
        this.mustBeHypothesis = mustBeHypothesis;
    }

    public List<Annotation> annotate() throws ProofException, SubstitutionException {
        List<Annotation> annotations = new ArrayList<>();
        int curLine = 0;
        Map<String, Integer> proved = new HashMap<>();
        Map<String, Pair<String, Integer>> needProve = new HashMap<>();
        Map<String, Pair<Integer, Integer>> availableMP = new HashMap<>();
        for (int i = 0; i < hypothesesExpr.size(); i++) {
            Expression expr = hypothesesExpr.get(i);
            update(proved, needProve, availableMP, expr, -1 - i);
        }
        if (mustBeHypothesis) {
            proved.clear();
            needProve.clear();
            availableMP.clear();
        }
        String strA = (A == null ? null : A.toString());
        for (Expression anOutline : outline) {
            ++curLine;

            // check hypothesis
            int hashCode = anOutline.hashCode();
            if (hypotheses.containsKey(hashCode)) {
                annotations.add(new Hypothesis(hypotheses.get(hashCode)));
                update(proved, needProve, availableMP, anOutline, curLine);
                continue;
            }

            if ((A != null) && (anOutline.toString().equals(strA))) {
                annotations.add(new Hypothesis(0));
                update(proved, needProve, availableMP, anOutline, curLine);
                continue;
            }

            // check axiom
            try {
                int index = axiomIndex(anOutline);
                if (index != -1) {
                    annotations.add(new Axiom(index));
                    update(proved, needProve, availableMP, anOutline, curLine);
                    continue;
                }
            } catch (SubstitutionException e) {
                String error = e.getMessage();
                throw new ProofException("Вывод некорректен начиная с формулы номер " + curLine + ": " + error + ".");
            }

            // check Modus Ponens
            if (availableMP.containsKey(anOutline.toString())) {
                Pair<Integer, Integer> pair = availableMP.get(anOutline.toString());
                annotations.add(new ModusPonens(pair.getValue(), pair.getKey()));
                update(proved, needProve, availableMP, anOutline, curLine);
                continue;
            }

            // check forAll rule
            if (anOutline instanceof Implication) {
                Expression phi = ((Implication) anOutline).getA();
                Expression psi = ((Implication) anOutline).getB();
                if (psi instanceof ForAll) {
                    Variable x = (Variable) ((ForAll) psi).getVar();
                    psi = ((ForAll) psi).getExpr();
                    String fromExpr = (new Implication(phi, psi)).toString();
                    if (proved.containsKey(fromExpr)) {
                        if (phi.isFreeVariable(x.toString())) {
                            throw new ProofException("Вывод некорректен начиная с формулы номер " + curLine + ": " +
                                    "переменная " + x.toString() + " входит свободно в формулу " + phi.toString() + ".");
                        }
                        checkForFree(curLine, x.toString(), "правило");
                        annotations.add(new ForAllRule(proved.get(fromExpr)));
                        update(proved, needProve, availableMP, anOutline, curLine);
                        continue;
                    }
                }
            }

            // check Exists rule
            if (anOutline instanceof Implication) {
                Expression psi = ((Implication) anOutline).getA();
                Expression phi = ((Implication) anOutline).getB();
                if (psi instanceof Exists) {
                    Variable x = (Variable) ((Exists) psi).getVar();
                    psi = ((Exists) psi).getExpr();
                    String fromExpr = (new Implication(psi, phi)).toString();
                    if (proved.containsKey(fromExpr)) {
                        if (phi.isFreeVariable(x.toString())) {
                            throw new ProofException("Вывод некорректен начиная с формулы номер " + curLine + ": " +
                                    "переменная " + x.toString() + " входит свободно в формулу " + phi.toString() + ".");
                        }
                        checkForFree(curLine, x.toString(), "правило");
                        annotations.add(new ExistsRule(proved.get(fromExpr)));
                        update(proved, needProve, availableMP, anOutline, curLine);
                        continue;
                    }
                }
            }
            throw new ProofException("Вывод некорректен начиная с формулы номер " + curLine + ".");
        }
        return annotations;
    }

    private void update(Map<String, Integer> proved, Map<String, Pair<String, Integer>> needProve, Map<String, Pair<Integer, Integer>> availableMP, Expression anOutline, int curLine) {
        String anOutlineStr = anOutline.toString();
        if (needProve.containsKey(anOutlineStr)) {
            Pair<String, Integer> pair = needProve.get(anOutlineStr);
            availableMP.put(pair.getKey(), new Pair<>(pair.getValue(), curLine));
            needProve.remove(anOutlineStr);
        }
        if (anOutline instanceof Implication) {
            Expression alpha = ((Implication) anOutline).getA();
            Expression beta = ((Implication) anOutline).getB();
            if (!availableMP.containsKey(beta.toString())) {
                if (proved.containsKey(alpha.toString())) {
                    availableMP.put(beta.toString(), new Pair<>(curLine, proved.get(alpha.toString())));
                }
                needProve.put(alpha.toString(), new Pair<>(beta.toString(), curLine));
            }
        }

        proved.put(anOutline.toString(), curLine);
    }


    private void checkForFree(int curLine, String var, String type) throws ProofException {
        if (freeVariablesFromHypotheses.containsKey(var)) {
            throw new ProofException("Вывод некорректен начиная с формулы номер " + curLine + ": " +
            "используется " + type + " с квантором по переменной " + var + ", входящей свободно в допущение "
                    + freeVariablesFromHypotheses.get(var) + ".");
        }

    }

    int axiomIndex(Expression expr) throws SubstitutionException {
        for (int i = 0; i < BASE_AXIOMS_CNT; i++) {
            if (expr.matchSchema(axioms.get(i))) {
                return i;
            }
        }
        for (int i = BASE_AXIOMS_CNT; i < BASE_AXIOMS_CNT + ARITHMETIC_AXIOMS_CNT; i++) {
            if (axioms.get(i).toString().equals(expr.toString())) return i;
//            if (expr.matchSchema(axioms.get(i))) {
//                return i;
//            }
        }
        if (isAxiomForAll(expr)) return BASE_AXIOMS_CNT + ARITHMETIC_AXIOMS_CNT;
        if (isAxiomExistance(expr)) return BASE_AXIOMS_CNT + ARITHMETIC_AXIOMS_CNT + 1;
        if (isAxiomInduction(expr)) return BASE_AXIOMS_CNT + ARITHMETIC_AXIOMS_CNT + 2;
        return -1;
    }

    boolean isAxiomForAll(Expression expr) throws SubstitutionException {
        // (for all x A(x)) -> A(y)
        if (!(expr instanceof Implication)) return false;
        Expression A = ((Implication)expr).getA();
        Expression B = ((Implication)expr).getB();
        if (!(A instanceof ForAll)) return false;
        Variable x = (Variable) ((ForAll) A).getVar();
        A = ((ForAll) A).getExpr();
        try {
            Pair<String, Expression> pair = A.arithmeticMatcher(x.toString(), B);
            if (pair != null) A.substituteVariable(pair.getKey(), pair.getValue());
            return true;
        } catch (ArithmeticMatcherException e) {
            return false;
        } catch (SubstitutionException e) {
            throw new SubstitutionException(e.getX(), A.toString(), e.getA());
        }
    }

    boolean isAxiomExistance(Expression expr) throws SubstitutionException {
        // A(y) -> (exists x A(x))
        if (!(expr instanceof Implication)) return false;
        Expression A = ((Implication)expr).getA();
        Expression B = ((Implication)expr).getB();
        if (!(B instanceof Exists)) return false;
        Variable x = (Variable) ((Exists) B).getVar();
        B = ((Exists) B).getExpr();
        try {
            Pair<String, Expression> pair = B.arithmeticMatcher(x.toString(), A);
            if (pair != null) B.substituteVariable(pair.getKey(), pair.getValue());
            return true;
        } catch (ArithmeticMatcherException e) {
            return false;
        } catch (SubstitutionException e) {
            throw new SubstitutionException(e.getX(), A.toString(), e.getA());
        }
    }

    boolean isAxiomInduction(Expression expr) throws SubstitutionException {
        if (!(expr instanceof Implication)) return false;
        Expression A = ((Implication) expr).getA();
        Expression B = ((Implication) expr).getB();
        if (!(A instanceof Conjunction)) return false;
        Expression base = ((Conjunction) A).getA();
        Expression step = ((Conjunction) A).getB();
        if (!(step instanceof ForAll)) return false;
        Variable x = (Variable) ((ForAll) step).getVar();
        step = ((ForAll) step).getExpr();
        if (!(step instanceof Implication)) return false;
        Expression base0 = B.substituteVariable(x.toString(), new Zero());
        if (!base.toString().equals(base0.toString())) return false;
        Expression stepA = ((Implication) step).getA();
        Expression stepB = ((Implication) step).getB();
        if (!stepA.toString().equals(B.toString())) return false;
        return B.substituteVariable(x.toString(), new Next(x)).toString().equals(stepB.toString());
    }
}
