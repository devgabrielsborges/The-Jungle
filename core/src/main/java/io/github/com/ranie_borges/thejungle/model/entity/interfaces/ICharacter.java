package io.github.com.ranie_borges.thejungle.model.entity.interfaces;

     import io.github.com.ranie_borges.thejungle.model.entity.Creature;
     import io.github.com.ranie_borges.thejungle.model.entity.Item;

     public interface ICharacter {
         /**
          * Ataca uma criatura com o dano especificado
          *
          * @param attackDamage A quantidade de dano a ser infligida
          * @param creature A criatura alvo
          * @return true se o ataque foi bem-sucedido, false caso contrário
          */
         boolean attack(double attackDamage, Creature creature);  // retorna se o ataque foi bem-sucedido ou falhou

         /**
          * Tenta evitar uma luta com base na sorte e outros fatores
          *
          * @param hasTraitLucky Se o personagem tem o traço de sorte
          * @return true se a luta foi evitada com sucesso, false caso contrário
          */
         boolean avoidFight(boolean hasTraitLucky);   // se o jogador tem Trait.LUCKY, ele pode evitar a luta

         /**
          * Coleta um item do ambiente se as condições permitirem
          *
          * @param nearbyItem O item disponível nas proximidades
          * @param isInventoryFull Se o inventário do personagem está cheio
          */
         void collectItem(Item nearbyItem, boolean isInventoryFull);

         /**
          * Consome um item bebível para repor a sede
          *
          * @param hasDrinkableItem Se o personagem tem um item bebível
          */
         void drink(boolean hasDrinkableItem);

         /**
          * Usa um item do inventário
          *
          * @param item O item a ser usado
          */
         void useItem(Item item);
     }
