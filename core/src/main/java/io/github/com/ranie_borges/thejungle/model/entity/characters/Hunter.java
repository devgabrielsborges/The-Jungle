package io.github.com.ranie_borges.thejungle.model.entity.characters;

                    import com.badlogic.gdx.utils.Array;
                    import io.github.com.ranie_borges.thejungle.model.entity.Character;
                    import io.github.com.ranie_borges.thejungle.model.entity.Creature;
                    import io.github.com.ranie_borges.thejungle.model.entity.Item;

                    public class Hunter extends Character<Item> {

                        protected Hunter(String name) {
                            super(name);
                            setLife(100);
                            setAttackDamage(12.0);
                            setInventoryInitialCapacity(5);
                        }

                        @Override
                        public void dropItem(Item item) {
                            getInventory().removeValue(item, true);
                            System.out.println(getName() + " dropped item: " + item.getName());
                        }

                        @Override
                        public boolean attack(double attackDamage, Creature creature) {
                            if (creature == null) return false;
                            double totalDamage = attackDamage + getAttackDamage();
                            System.out.println(getName() + " strikes " + creature.getName() + " with " + totalDamage + " damage.");

                            // Reduzir a vida da criatura baseado no dano
                            float creatureLife = creature.getLifeRatio();
                            creatureLife -= totalDamage;
                            creature.setLifeRatio(Math.max(0, creatureLife));

                            return creatureLife <= 0; // Retorna true se a criatura foi derrotada
                        }

                        @Override
                        public boolean avoidFight(boolean hasTraitLucky) {
                            boolean avoided = hasTraitLucky && Math.random() > 0.6;
                            System.out.println(getName() + (avoided ? " avoided" : " couldn't avoid") + " the fight.");
                            return avoided;
                        }

                        @Override
                        public void collectItem(Item nearbyItem, boolean isInventoryFull) {
                            if (nearbyItem != null && !isInventoryFull && getInventory().size < getInventoryInitialCapacity()) {
                                getInventory().add(nearbyItem);
                                System.out.println(getName() + " collected: " + nearbyItem.getName());
                            } else {
                                System.out.println(getName() + " couldn't collect the item.");
                            }
                        }

                        @Override
                        public void drink(boolean Drinkable) {
                            if (Drinkable) {
                                setLife(Math.min(getLife() + 5, 100));
                                System.out.println(getName() + " drank and recovered health. Current health: " + getLife());
                            } else {
                                System.out.println(getName() + " has nothing to drink.");
                            }
                        }

                        @Override
                        public void useItem(Item item) {
                            if (item != null && getInventory().contains(item, true)) {
                                item.useItem();
                                System.out.println(getName() + " used: " + item.getName());
                                getInventory().removeValue(item, true);
                            } else {
                                System.out.println(getName() + " doesn't have the item: " + (item != null ? item.getName() : "null"));
                            }
                        }
                    }
