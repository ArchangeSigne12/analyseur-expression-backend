package com.tp.reconnaisseur.moteur;

import java.util.*;

/**
 * Convertit une expression VALIDE en notation postfixée (Shunting-yard).
 * La concaténation implicite est rendue explicite avec '.'.
 * Priorités : '.' (2) > '|' (1). Les opérateurs postfixes '*' et '+' s'appliquent directement.
 */
public final class ConvertisseurPostfixe {

    private ConvertisseurPostfixe() {
    }

    public static List<Character> versPostfixe(String regex) {
        List<Character> sortie = new ArrayList<>();
        Deque<Character> pile = new ArrayDeque<>();
        boolean operandePrecedent = false;

        for (char c : regex.toCharArray()) {
            switch (c) {
                case '(' -> {
                    if (operandePrecedent) {
                        empilerBinaire('.', sortie, pile);
                    }
                    pile.push('(');
                    operandePrecedent = false;
                }
                case ')' -> {
                    while (pile.peek() != '(') {
                        sortie.add(pile.pop());
                    }
                    pile.pop();
                    operandePrecedent = true;
                }
                case '*', '+' -> sortie.add(c);
                case '|', '.' -> {
                    empilerBinaire(c, sortie, pile);
                    operandePrecedent = false;
                }
                default -> {
                    if (operandePrecedent) {
                        empilerBinaire('.', sortie, pile);
                    }
                    sortie.add(c);
                    operandePrecedent = true;
                }
            }
        }
        while (!pile.isEmpty()) {
            sortie.add(pile.pop());
        }
        return sortie;
    }

    private static void empilerBinaire(char op, List<Character> sortie, Deque<Character> pile) {
        while (!pile.isEmpty() && pile.peek() != '(' && priorite(pile.peek()) >= priorite(op)) {
            sortie.add(pile.pop());
        }
        pile.push(op);
    }

    private static int priorite(char op) {
        return op == '.' ? 2 : 1;
    }
}
