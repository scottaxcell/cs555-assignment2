package cs555.pastry.node.discovery;

import java.util.ArrayList;
import java.util.List;

public class NicknameFactory {
    private static int idx = 0;
    private static final List<String> nicknames = new ArrayList();

    static {
        nicknames.add("Foxy Lady");
        nicknames.add("Mad Max");
        nicknames.add("Goose");
        nicknames.add("Skunk");
        nicknames.add("Doctor");
        nicknames.add("French Fry");
        nicknames.add("Chica");
        nicknames.add("Rubber");
        nicknames.add("Sherlock");
        nicknames.add("Chubs");
        nicknames.add("Dum Dum");
        nicknames.add("Babe");
        nicknames.add("Daria");
        nicknames.add("Diet Coke");
        nicknames.add("Teeny");
        nicknames.add("Dots");
        nicknames.add("Numbers");
        nicknames.add("Nerd");
        nicknames.add("Lovey");
        nicknames.add("Donuts");
        nicknames.add("Eagle");
        nicknames.add("Goon");
        nicknames.add("Gummi Bear");
        nicknames.add("Bubblegum");
        nicknames.add("Halfmast");
        nicknames.add("Biffle");
        nicknames.add("Joker");
        nicknames.add("Dilly Dally");
        nicknames.add("Big Guy");
        nicknames.add("Backbone");
        nicknames.add("Beanpole");
        nicknames.add("Salt");
        nicknames.add("Fattykins");
        nicknames.add("Apple");
        nicknames.add("Cruella");
        nicknames.add("Cookie");
        nicknames.add("Headlights");
        nicknames.add("Skinny Jeans");
        nicknames.add("Lil Girl");
        nicknames.add("Boomer");
        nicknames.add("Smirk");
        nicknames.add("Freak");
        nicknames.add("Bridge");
        nicknames.add("Juicy");
        nicknames.add("Slim");
        nicknames.add("Dumbledore");
        nicknames.add("Rainbow");
        nicknames.add("Psycho");
        nicknames.add("Weiner");
        nicknames.add("Jackrabbit");
    }

    public static String getNickname() {
        return nicknames.get(idx++);
    }
}
