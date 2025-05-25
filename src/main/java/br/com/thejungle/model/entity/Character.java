package br.com.thejungle.model.entity;

import java.util.Set;
public abstract class Character {
    private String name;
    private float hunger;
    private float thirst;
    private float energy;
    private float sanity;

    private Inventory inventory;
    private float currentWeight;
    private float maxCarryWeight;

    private float attackDamage;
    private float speed;

    private Set<Trait> traits;

    private Ambient currentAmbient;


}
