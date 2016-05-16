package ru.ifmo.ctddev.podtelkin.mathlogic.parsers;

import ru.ifmo.ctddev.podtelkin.mathlogic.expression.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Created by vlad107 on 04.05.16.
 */
public class ExpressionParser {
    private String str;
    private int ptr;
    private int[] par;


    public ExpressionParser(String str) {
        this.str = str.replaceAll("\\s+", "");
    }
    public Expression parse() {
        par = new int[str.length()];
        Stack<Integer> stack = new Stack<>();
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == '(') {
                stack.push(i);
            } else if (str.charAt(i) == ')') {
                par[stack.pop()] = i;
            }
        }
        ptr = 0;
        Expression expr = parseExpr();
        if (ptr != str.length()) {
            throw new IllegalArgumentException("Couldn't parse expression. \n    Remaining part: " + str.substring(ptr));
        }
        return expr;
    }

    private Expression parseExpr() {
        Expression disj = parseDisj();
        if ((ptr + 1 < str.length()) && (str.substring(ptr, ptr + 2).equals("->"))) {
            ptr += 2;
            Expression expr = parseExpr();
            return new Implication(disj, expr);
        }
        return disj;
    }

    private Expression parseDisj() {
        Expression conj = parseConj();
        while ((ptr < str.length()) && (str.charAt(ptr) == '|')) {
            ++ptr;
            Expression conj2 = parseConj();
            conj = new Disjunction(conj, conj2);
        }
        return conj;
    }

    private Expression parseConj() {
        Expression unary = parseUnary();
        while ((ptr < str.length()) && (str.charAt(ptr) == '&')) {
            ++ptr;
            Expression conj = parseConj();
            unary = new Conjunction(unary, conj);
        }
        return unary;
    }

    private Expression parseUnary() {
        if ((ptr < str.length()) && (str.charAt(ptr) == '!')) {
            ptr += 1;
            Expression expr = parseUnary();
            return new Negative(expr);
        }
        if ((ptr < str.length()) && (str.charAt(ptr) == '@')) {
            ptr += 1;
            Variable var = (Variable)parseVariable();
            Expression unary = parseUnary();
            return new ForAll(var, unary);
        }
        if ((ptr < str.length()) && (str.charAt(ptr) == '?')) {
            ptr += 1;
            Variable var = (Variable)parseVariable();
            Expression unary = parseUnary();
            return new Exists(var, unary);
        }
        if ((ptr < str.length()) && (str.charAt(ptr) == '(')) {
            if (par[ptr] + 1 < str.length()) {
                char c = str.charAt(par[ptr] + 1);
                if ((c == '\'') || (c == '*') || (c == '+') || (c == '=')) {
                    return parsePredicate();
                }
            }
            ptr += 1;
            Expression expr = parseExpr();
            if ((ptr >= str.length()) || (str.charAt(ptr) != ')')) {
                throw new IllegalArgumentException("Brackets didn't match");
            }
            ptr += 1;
            return expr;
        }
        return parsePredicate();
    }

    private Expression parseVariable() {
        if ((ptr < str.length()) && ('a' <= str.charAt(ptr)) && (str.charAt(ptr) <= 'z')) {
            StringBuilder var = new StringBuilder();
            var.append(str.charAt(ptr));
            ++ptr;
            while ((ptr < str.length()) && ('0' <= str.charAt(ptr)) && (str.charAt(ptr) <= '9')) {
                var.append(str.charAt(ptr));
                ++ptr;
            }
            return new Variable(var.toString());
        }
        throw new IllegalArgumentException("Couldn't parse variable");
    }

    private Expression parsePredicate() {
        if ((ptr < str.length()) && ('A' <= str.charAt(ptr)) && (str.charAt(ptr) <= 'Z')) {
            StringBuilder name = new StringBuilder();
            name.append(str.charAt(ptr));
            ++ptr;
            while ((ptr < str.length()) && ('0' <= str.charAt(ptr)) && (str.charAt(ptr) <= '9')) {
                name.append(str.charAt(ptr));
                ++ptr;
            }
            List<Expression> args = new ArrayList<>();
            if ((ptr < str.length()) && (str.charAt(ptr) == '(')) {
                ++ptr;
                args.add(parseTerm());
                while ((ptr < str.length()) && (str.charAt(ptr) == ',')) {
                    ++ptr;
                    args.add(parseTerm());
                }
                if ((ptr >= str.length()) || (str.charAt(ptr) != ')')) {
                    throw new IllegalArgumentException("Brackets didn't match");
                }
                ++ptr;
            }
            return new Predicate(new Variable(name.toString()), args);
        }
        Expression leftTerm = parseTerm();
        if ((ptr >= str.length()) || (str.charAt(ptr) != '=')) {
            throw new IllegalArgumentException("Couldn't parse <term=term> predicate");
        }
        ++ptr;
        Expression rightTerm = parseTerm();
        return new Equal(leftTerm, rightTerm);
    }

    private Expression parseTerm() {
        Expression summand = parseSummand();
        if ((ptr < str.length()) && (str.charAt(ptr) == '+')) {
            ++ptr;
            Expression term = parseTerm();
            return new Plus(summand, term);
        }
        return summand;
    }

    private Expression parseSummand() {
        Expression mult = parseMult();
        if ((ptr < str.length()) && (str.charAt(ptr) == '*')) {
            ++ptr;
            Expression summand = parseSummand();
            return new Mult(mult, summand);
        }
        return mult;
    }

    private Expression parseMult() {
        Expression expr = parseMult2();
        while ((ptr < str.length()) && (str.charAt(ptr) == '\'')) {
            expr = new Next(expr);
            ++ptr;
        }
        return expr;
    }

    private Expression parseMult2() {
        if ((ptr < str.length()) && (str.charAt(ptr) == '(')) {
            ++ptr;
            Expression term = parseTerm();
            if ((ptr >= str.length()) || (str.charAt(ptr) != ')')) {
                throw new IllegalArgumentException("Brackets didn't match");
            }
            ++ptr;
            return term;
        }
        if ((ptr < str.length()) && ('a' <= str.charAt(ptr)) && (str.charAt(ptr) <= 'z')) {
            StringBuilder name = new StringBuilder();
            name.append(str.charAt(ptr));
            ++ptr;
            while ((ptr < str.length()) && ('0' <= str.charAt(ptr)) && (str.charAt(ptr) <= '9')) {
                name.append(str.charAt(ptr));
                ++ptr;
            }
            if ((ptr < str.length()) && (str.charAt(ptr) == '(')) {
                List<Expression> args = new ArrayList<>();
                ++ptr;
                args.add(parseTerm());
                while ((ptr < str.length()) && (str.charAt(ptr) == ',')) {
                    ++ptr;
                    args.add(parseTerm());
                }
                if ((ptr >= str.length()) || (str.charAt(ptr) != ')')) {
                    throw new IllegalArgumentException("Brackets didn't match");
                }
                ++ptr;
                return new Function(new Variable(name.toString()), args);
            } else {
                return new Variable(name.toString());
            }
        }
        if ((ptr < str.length()) && (str.charAt(ptr) == '0')) {
            Expression zero = new Zero();
            ++ptr;
            return zero;
        }
        throw new IllegalArgumentException("Couldn't parse the expression");
    }
}
