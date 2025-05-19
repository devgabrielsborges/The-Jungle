
package io.github.com.ranie_borges.thejungle.lwjgl3;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import io.github.com.ranie_borges.thejungle.core.Main;

/** Launches the desktop (LWJGL3) application. */
public class Lwjgl3Launcher {
    public static void main(String[] args) {
        if (StartupHelper.startNewJvmIfRequired()) return; // This handles macOS support and helps on Windows.
        createApplication();

        System.setProperty("org.lwjgl.opengl.Display.allowSoftwareOpenGL", "true");
    }

    private static void createApplication() {
        new Lwjgl3Application(new Main(), getDefaultConfiguration());
    }

    private static Lwjgl3ApplicationConfiguration getDefaultConfiguration() {
        Lwjgl3ApplicationConfiguration configuration = new Lwjgl3ApplicationConfiguration();
        configuration.setTitle("The Jungle");
        //// Vsync limits the frames per second to what your hardware can display, and helps eliminate
        //// screen tearing. This setting doesn't always wor«k on Linux, so the line after is a safeguard.
        configuration.useVsync(true);

        configuration.setFullscreenMode(Lwjgl3ApplicationConfiguration.getDisplayMode());
        // Definir FPS fixo em vez de usar a taxa de atualização do monitor
        configuration.setForegroundFPS(60);

        // Permitir redimensionamento da janela
        configuration.setResizable(true);

        // Configuração explícita de versão OpenGL para melhorar compatibilidade
        configuration.setOpenGLEmulation(Lwjgl3ApplicationConfiguration.GLEmulation.GL30, 3, 2);

        configuration.setWindowIcon("libgdx128.png", "libgdx64.png", "libgdx32.png", "libgdx16.png");
        return configuration;
    }
}
