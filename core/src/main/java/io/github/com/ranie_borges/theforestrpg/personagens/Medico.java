package io.github.com.ranie_borges.theforestrpg.personagens;

import io.github.com.ranie_borges.theforestrpg.Player;

public class Medico extends Player {
    private double attackDamage;
    private double defenseStatus;

    public Medico(String name, double height, double gpa) {
        setName(name);
        setHeight(height);
        setGpa(gpa);
    }

    public void curar() {
        System.out.println("Curei o mano");
    }
}
