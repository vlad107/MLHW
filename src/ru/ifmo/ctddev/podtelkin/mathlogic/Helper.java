package ru.ifmo.ctddev.podtelkin.mathlogic;

import java.util.ArrayList;
import java.util.List;
import ru.ifmo.ctddev.podtelkin.mathlogic.expression.Expression;
import ru.ifmo.ctddev.podtelkin.mathlogic.parsers.ExpressionParser;

/**
 * Created by vlad107 on 04.05.16.
 */
public class Helper {
    public static List<Expression> generateAxioms() {
        List<Expression> axioms = new ArrayList<>();
        axioms.add((new ExpressionParser("A->B->A")).parse()); //0
        axioms.add((new ExpressionParser("(A->B)->(A->B->C)->(A->C)")).parse());//1
        axioms.add((new ExpressionParser("A->A|B")).parse());
        axioms.add((new ExpressionParser("B->A|B")).parse());
        axioms.add((new ExpressionParser("(A->C)->(B->C)->(A|B->C)")).parse());
        axioms.add((new ExpressionParser("A&B->A")).parse());
        axioms.add((new ExpressionParser("A&B->B")).parse());
        axioms.add((new ExpressionParser("A->B->A&B")).parse());
        axioms.add((new ExpressionParser("(A->B)->(A->!B)->!A")).parse());
        axioms.add((new ExpressionParser("!!A->A")).parse());

        axioms.add((new ExpressionParser("(a=b)->(a'=b')")).parse());
        axioms.add((new ExpressionParser("(a=b)->(a=c)->(b=c)")).parse());
        axioms.add((new ExpressionParser("(a'=b')->(a=b)")).parse());
        axioms.add((new ExpressionParser("!(a'=0)").parse()));
        axioms.add((new ExpressionParser("a+b'=(a+b)'").parse()));
        axioms.add((new ExpressionParser("a+0=a").parse()));
        axioms.add((new ExpressionParser("a*0=0").parse()));
        axioms.add((new ExpressionParser("a*b'=a*b+a").parse()));

//        axioms.add((new ExpressionParser("(@x A)->A(y)")).parse());
//        axioms.add((new ExpressionParser("A(y)->(?x A)")).parse());
//        axioms.add((new ExpressionParser("A[x:=0] & @x (A -> A[x:=x']) -> A").parse()));
        return axioms;
    }
}
