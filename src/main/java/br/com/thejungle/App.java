package br.com.thejungle;

import java.util.Scanner;

public class App {
    public static boolean isAlive = true;
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int turno = 1;

        System.out.println("Bem-vindo ao Última Fronteira!"); // [cite: 1, 256]

        // Inicialização do personagem e ambiente (a ser implementado)
        // Exemplo: Personagem jogador = new Personagem("Explorador");
        // Exemplo: Ambiente ambienteAtual = new AmbienteFloresta();

        while (isAlive) {
            System.out.println("\n--- Turno " + turno + " ---");

            // 1. Fase de Início [cite: 135, 390]
            System.out.println("\n-- Fase de Início --");
            // Exibir status do personagem (vida, fome, sede, energia, sanidade, inventário) [cite: 135, 390]
            // Atualizar ambiente e condições climáticas [cite: 136, 391]
            // Exibir resumo do turno anterior [cite: 136, 391]
            System.out.println("Status do Personagem: (a ser implementado)");
            System.out.println("Condições Atuais: (a ser implementado)");

            // 2. Fase de Ação [cite: 137, 392]
            System.out.println("\n-- Fase de Ação --");
            System.out.println("Escolha sua ação:");
            System.out.println("1. Explorar");
            System.out.println("2. Descansar");
            System.out.println("3. Ver Inventário");
            System.out.println("4. Sair do Jogo");
            // Outras ações: coletar recursos, construir, etc.

            String escolha = scanner.nextLine();
            // Processar escolha do jogador (algumas ações consomem energia) [cite: 138, 393]

            if ("4".equals(escolha) || "sair".equalsIgnoreCase(escolha)) {
                System.out.println("Saindo do jogo...");
                isAlive = false;
                continue;
            }
            System.out.println("Ação escolhida: " + escolha + " (lógica a ser implementada)");


            // 3. Fase de Evento Aleatório [cite: 139, 394]
            System.out.println("\n-- Fase de Evento Aleatório --");
            // Verificar se um evento aleatório será acionado [cite: 139, 394]
            // Se ocorrer, executar o evento e aplicar seus efeitos [cite: 140, 395]
            System.out.println("Verificando eventos... (a ser implementado)");


            // 4. Fase de Manutenção [cite: 141, 396]
            System.out.println("\n-- Fase de Manutenção --");
            // Ajustar atributos como fome, sede e sanidade [cite: 141, 396]
            // Recursos do ambiente podem se esgotar ou se regenerar [cite: 142, 397]
            System.out.println("Atualizando status e ambiente... (a ser implementado)");

            // Avançar para o próximo ciclo [cite: 142, 397]
            turno++;

            // Condições de derrota (a serem implementadas) [cite: 146, 401]
            // Ex: if (jogador.getVida() <= 0) { jogoRodando = false; System.out.println("Game Over!"); }
        }

        scanner.close();
        System.out.println("Obrigado por jogar Última Fronteira!");
    }
}