package com.tp.reconnaisseur.moteur;

import java.util.*;

/** Automate fini (AFN avec ε-transitions ou AFD). Les états sont numérotés 0..nbEtats-1. */
public record Automate(int nbEtats, int initial, Set<Integer> finaux, List<Transition> transitions) {

    /** Renumérote les états par parcours en largeur depuis l'état initial (l'initial devient q0). */
    public Automate renumeroteParParcours() {
        Map<Integer, List<Transition>> sortantes = new HashMap<>();
        for (Transition t : transitions) {
            sortantes.computeIfAbsent(t.de(), k -> new ArrayList<>()).add(t);
        }
        Comparator<Transition> ordre = Comparator.comparingInt((Transition t) -> t.symbole())
                .thenComparingInt(Transition::vers);

        Map<Integer, Integer> nouveau = new HashMap<>();
        Deque<Integer> file = new ArrayDeque<>();
        nouveau.put(initial, 0);
        file.add(initial);
        while (!file.isEmpty()) {
            int courant = file.poll();
            List<Transition> sortie = sortantes.getOrDefault(courant, List.of()).stream().sorted(ordre).toList();
            for (Transition t : sortie) {
                if (!nouveau.containsKey(t.vers())) {
                    nouveau.put(t.vers(), nouveau.size());
                    file.add(t.vers());
                }
            }
        }
        for (int i = 0; i < nbEtats; i++) {
            nouveau.putIfAbsent(i, nouveau.size());
        }

        List<Transition> nouvellesTransitions = transitions.stream()
                .map(t -> new Transition(nouveau.get(t.de()), t.symbole(), nouveau.get(t.vers())))
                .sorted(Comparator.comparingInt(Transition::de)
                        .thenComparingInt(t -> t.symbole())
                        .thenComparingInt(Transition::vers))
                .toList();
        Set<Integer> nouveauxFinaux = new TreeSet<>();
        for (int f : finaux) {
            nouveauxFinaux.add(nouveau.get(f));
        }
        return new Automate(nbEtats, 0, nouveauxFinaux, nouvellesTransitions);
    }
}
