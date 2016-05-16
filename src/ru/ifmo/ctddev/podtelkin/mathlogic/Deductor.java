package ru.ifmo.ctddev.podtelkin.mathlogic;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Exchanger;

import ru.ifmo.ctddev.podtelkin.mathlogic.annotation.*;
import ru.ifmo.ctddev.podtelkin.mathlogic.exceptions.ProofException;
import ru.ifmo.ctddev.podtelkin.mathlogic.exceptions.SubstitutionException;
import ru.ifmo.ctddev.podtelkin.mathlogic.expression.*;
import ru.ifmo.ctddev.podtelkin.mathlogic.parsers.ProofParser;

/**
 * Created by vlad107 on 05.05.16.
 */
public class Deductor {
    private final List<Expression> axioms = Helper.generateAxioms();

    private final Expression A;
    private final Expression B;
    private final List<Expression> hypotheses;
    private final List<Expression> outline;
    private final List<Annotation> annotations;

    public Deductor(Expression a, Expression b, List<Expression> hypotheses, List<Expression> outline) throws ProofException, SubstitutionException {
        A = a;
        B = b;
        this.hypotheses = hypotheses;
        this.outline = outline;
        annotations = (new ProofParser(a, b, hypotheses, outline)).annotate();
    }

    public List<Expression> makeProofFromOutline() {
        List<Expression> proof = new ArrayList<>();
        if (A == null) return outline;
        for (int i = 0; i < outline.size(); i++) {
            Expression formula = outline.get(i);
            if (formula.toString().equals(A.toString())) {
                proof.add(new Implication(A, new Implication(A, A)));                      // A -> A -> A
                proof.add(new Implication(A, new Implication(new Implication(A, A), A)));  // A -> (A -> A) -> A
                proof.add(new Implication(new Implication(A, new Implication(A, A)),
                        new Implication(new Implication(A, new Implication(new Implication(A, A), A)),
                                new Implication(A, A))));                                  // (A -> A -> A) -> ((A -> (A -> A) -> A) -> (A -> A))
                proof.add(new Implication(new Implication(A, new Implication(new Implication(A, A), A)),
                        new Implication(A, A)));                                           // (A -> (A -> A) -> A) -> (A -> A)
                proof.add(new Implication(A, A));                                          // A -> A
                continue;
            }
            if ((annotations.get(i) instanceof Axiom) || (annotations.get(i) instanceof Hypothesis)) {
                addImplAxiom(proof, A, formula);
                continue;
            }
//            System.err.println(hypotheses);
            if (annotations.get(i) instanceof ModusPonens) {
                int gammaIndex = ((ModusPonens) annotations.get(i)).getGammaIndex();
                int alphaIndex = ((ModusPonens) annotations.get(i)).getAlphaIndex();
//                System.err.println(alphaIndex + ", " + gammaIndex);
                Expression gammaA;
                if (gammaIndex < 0) {
                    proof.add(hypotheses.get(-gammaIndex - 1));
                    addImplAxiom(proof, A, hypotheses.get(-gammaIndex - 1));
                    gammaA = new Implication(A, hypotheses.get(-gammaIndex - 1));
                } else {
                    gammaA = new Implication(A, outline.get(gammaIndex - 1));
                }
                Expression alphaA;
                if (alphaIndex < 0) {
                    addImplAxiom(proof, A, hypotheses.get(-alphaIndex - 1));
                    alphaA = new Implication(A, hypotheses.get(-alphaIndex - 1));
                } else {
                    alphaA = new Implication(A, outline.get(alphaIndex - 1));
                }
                Expression betaA = new Implication(A, outline.get(i));
//                System.err.println(alphaA);
//                System.err.println(betaA);
//                System.err.println(gammaA);
                proof.add(new Implication(alphaA, new Implication(gammaA, betaA)));
                proof.add(new Implication(gammaA, betaA));
                proof.add(betaA);
                continue;
            }
            if (annotations.get(i) instanceof ExistsRule) {
                // A -> psi -> phi
                Expression provedFormula = new Implication(A, outline.get(((ExistsRule) annotations.get(i)).getIndex() - 1));
                Expression psi = ((Implication) ((Implication) provedFormula).getB()).getA();
                Expression phi = ((Implication) ((Implication) provedFormula).getB()).getB();
                Variable x = (Variable) ((Exists) ((Implication) formula).getA()).getVar();
                addSwapFirstTwo(proof, A, psi, phi);
                proof.add(new Implication(new Exists(x, psi), new Implication(A, phi)));
                addSwapFirstTwo(proof, new Exists(x, psi), A, phi);
                continue;
            }
            if (annotations.get(i) instanceof ForAllRule) {
                // A -> psi -> phi
                Expression provedFormula = new Implication(A, outline.get(((ForAllRule) annotations.get(i)).getIndex() - 1));
                Expression psi = ((Implication) ((Implication)provedFormula).getB()).getA();
                Expression phi = ((Implication) ((Implication)provedFormula).getB()).getB();
                Variable x = (Variable) ((ForAll) ((Implication) formula).getB()).getVar();
                addConj(proof, A, psi, phi);
                proof.add(new Implication(new Conjunction(A, psi), new ForAll(x, phi)));
                removeConj(proof, new Conjunction(A, psi), new ForAll(x, phi));
                continue;
            }
            assert(false);
        }
        return proof;
    }

    private void addImplAxiom(List<Expression> proof, Expression A, Expression B) {
        proof.add(new Implication(B, new Implication(A, B)));
        proof.add(B);
        proof.add(new Implication(A, B));
    }

    private void removeConj(List<Expression> proof, Conjunction AB, Expression C) {
        // A&B -> C
        // A -> B -> C
        List<Expression> subProof = new ArrayList<>();
        Expression A = AB.getA();
        Expression B = AB.getB();
        subProof.add(A);
        subProof.add(B);
        subProof.add(new Implication(A, new Implication(B, new Conjunction(A, B))));
        subProof.add(new Implication(B, new Conjunction(A, B)));
        subProof.add(new Conjunction(A, B));
        subProof.add(new Implication(new Conjunction(A, B), C));
        subProof.add(C);
        try {
            List<Expression> hypotheses = new ArrayList<>();
            hypotheses.add(new Implication(new Conjunction(A, B), C));
            hypotheses.add(A);
            subProof = (new Deductor(B, C, hypotheses, subProof)).makeProofFromOutline();
            hypotheses.remove(hypotheses.size() - 1);
            subProof = (new Deductor(A, new Implication(B, C), hypotheses, subProof)).makeProofFromOutline();
            proof.addAll(subProof);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    private void addConj(List<Expression> proof, Expression A, Expression B, Expression C) {
        List<Expression> subProof = new ArrayList<>();
        subProof.add(new Conjunction(A, B));
        subProof.add(new Implication(new Conjunction(A, B), A));
        subProof.add(A);
        subProof.add(new Implication(new Conjunction(A, B), B));
        subProof.add(B);
        subProof.add(new Implication(A, new Implication(B, C)));
        subProof.add(new Implication(B, C));
        subProof.add(C);
        try {
            List<Expression> hypotheses = new ArrayList<>();
            hypotheses.add(new Implication(A, new Implication(B, C)));
            subProof = (new Deductor(new Conjunction(A, B), C, hypotheses, subProof)).makeProofFromOutline();
//            hypotheses.clear();
//            subProof = (new Deductor(new Implication(A, new Implication(B, C)), new Implication(new Conjunction(A, B), C),
//                    hypotheses, subProof)).makeProofFromOutline();
            proof.addAll(subProof);
//            proof.add(new Implication(new Conjunction(A, B), C));
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
    }


    private void addSwapFirstTwo(List<Expression> proof, Expression A, Expression B, Expression C) {
        List<Expression> subProof = new ArrayList<>();
        subProof.add(A);
        subProof.add(B);
        subProof.add(new Implication(A, new Implication(B, C)));
        subProof.add(new Implication(B, C));
        subProof.add(C);
        try {
            List<Expression> hypotheses = new ArrayList<>();
            hypotheses.add(new Implication(A, new Implication(B, C)));
            hypotheses.add(B);
            subProof = (new Deductor(A, C, hypotheses, subProof)).makeProofFromOutline();
            hypotheses.remove(hypotheses.size() - 1);
            subProof = (new Deductor(B, new Implication(A, C), hypotheses, subProof)).makeProofFromOutline();
            proof.addAll(subProof);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
    }
}
