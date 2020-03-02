/*
 * Naming.java
 * Author: Seokjin Yoon
 * Created Date: 2020-03-02
 */

package com.thunder_cut.ux;

import java.util.concurrent.ThreadLocalRandom;

public class Naming {
    private static final String[] adjectives = {
            "Warty", "Hoary", "Breezy", "Dapper", "Edgy", "Feisty", "Gutsy", "Hardy",
            "Intrepid", "Jaunty", "Karmic", "Lucid", "Maverick", "Natty", "Precise", "Raring",
            "Saucy", "Trusty", "Vivid", "Wily", "Zesty", "Artful", "Bionic", "Cosmic",
            "Disco", "Focal"
    };
    private static final String[] nouns = {
            "Alice", "Bob", "Carol", "Carlos", "Charlie", "Chuck", "Craig", "Dave",
            "Dan", "David", "Eve", "Faythe", "Grace", "Heidi", "Issac", "Ivan",
            "Justin", "Mallory", "Matilda", "Oscar", "Olivia", "Peggy", "Plod", "Sybil",
            "Steve", "Trudy", "Trent", "Victor", "Walter", "Wendy", "Zoe"
    };

    private Naming() {
        throw new AssertionError();
    }

    public static String generateName() {
        String adjective = adjectives[ThreadLocalRandom.current().nextInt(adjectives.length)];
        String noun = nouns[ThreadLocalRandom.current().nextInt(nouns.length)];
        return adjective + " " + noun;
    }
}
