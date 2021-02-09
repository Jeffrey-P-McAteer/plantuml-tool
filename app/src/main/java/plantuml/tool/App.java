/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package plantuml.tool;

// Java standard lib dependencies
import java.util.*;
import java.io.*;

// JavaFX dependencies
import javafx.application.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.*;

import javafx.scene.web.*;

// 3rd-party dependencies
import org.fxmisc.richtext.*;
import org.fxmisc.flowless.VirtualizedScrollPane;

public class App {

    public static class GUI extends Application {


        // This data is shared between the GUI and bg_t_action.
        // bg_t_action may change this data every 5 seconds.
        public static File plantuml_src_f = new File(
          System.getenv().getOrDefault("PLANTUML_SRC_F", "plantuml-src.txt")
        );

        public static String plantuml_src_s = "";
        public static boolean plantuml_src_changed_from_bg = false; // written ONCE by bg_t_action, read by GUI
        public static boolean plantuml_src_changed_from_gui = false; // written by GUI, read by bg_t_action

        public static String graph_svg_s = "";
        public static boolean graph_svg_changed = true; // written by bg_t_action, read by GUI

        @Override
        public void start(Stage primaryStage) {
            Thread bg_thread = new Thread(GUI::gui_bg_t_updater);
            bg_thread.start();

            SplitPane root = new SplitPane();

            root.getItems().addAll(
              build_src_editor(),
              build_gui_editor()
            );

            Scene scene = new Scene(root, 600, 400);

            primaryStage.setScene(scene);
            primaryStage.setTitle("PlantUML Tool");

            primaryStage.show();
        }

        private static CodeArea codeArea = null;
        public Node build_src_editor() {
            GUI.codeArea = new CodeArea();

            return new VirtualizedScrollPane<>(GUI.codeArea);
        }

        private static WebEngine webEngine = null;
        public Node build_gui_editor() {
            WebView webView = new WebView();
            GUI.webEngine = webView.getEngine();

            return webView;
        }


        // Reads data from bg_t_action and pushed it into the GUI.
        // Also reads GUI data and pushes to bg_t_action.
        public static void gui_bg_t_updater() {
            while (!App.bg_t_exit_f) {
                delay_ms(2_500l);
                Platform.runLater(() -> {
                  if (GUI.codeArea != null) {
                    // Has the BG thread pushed new content?
                    if (GUI.plantuml_src_changed_from_bg) {
                      GUI.codeArea.replaceText​(GUI.plantuml_src_s);
                      GUI.plantuml_src_changed_from_bg = false;
                    }

                    // Has the user written new PlantUML?
                    if (!GUI.plantuml_src_s.equals(GUI.codeArea.getText())) {
                      GUI.plantuml_src_s = GUI.codeArea.getText();
                      GUI.plantuml_src_changed_from_gui = true;
                    }

                  }
                });
            }
        }

    }

    // Set to true to exit the background thread
    public static boolean bg_t_exit_f = false;

    public static void bg_t_action() {


        while (!bg_t_exit_f) {
            delay_ms(5_000l);

            // If buffer from GUI is empty, fill using text from plantuml_src_f or default
            if (GUI.plantuml_src_s == null || GUI.plantuml_src_s.length() < 1) {
              GUI.plantuml_src_s = DEFAULT_PLANTUML;
              GUI.plantuml_src_changed_from_bg = true;
            }

            // Save buffer from GUI into plantuml_src_f
            if (GUI.plantuml_src_changed_from_gui) {
              System.err.println("TODO save: "+GUI.plantuml_src_s);
            }


            // Use PlantUML lib to convert buffer from GUI into .svg
            GUI.graph_svg_changed = true;

        }
    }

    public static void main(String[] args) throws InterruptedException {
        // Spawn bg thread to save 
        Thread bg_t = new Thread(App::bg_t_action);
        bg_t.start();

        // Run FX GUI on main thread
        Application.launch(GUI.class, args);

        // Exit
        bg_t_exit_f = true;
        bg_t.join();
    }

    /// Large-ish resources are put at the bottom so as to not clutter business logic upstairs.

    public static void delay_ms(long ms) {
      try { Thread.sleep(ms); }
      catch (InterruptedException ie) { ie.printStackTrace(); }
    }

    public static final String DEFAULT_PLANTUML = String.join(System.lineSeparator(),
      "@startuml",
      "",
      "skinparam nodestep 10",
      "",
      "class Alpha {",
        "  - {static} id: int",
        "  ==Friend Accessors==",
        "  - friend: Beta",
        "  + setFriend(Beta): void",
        "  + getFriend(): Beta",
        "  ==",
        "  - count: int",
        "  - getID(): int",
        "  + operateOn(Gamma): void",
      "}",
      "",
      "class Beta {",
      "  + doWork(): void",
      "}",
      "",
      "class Gamma {",
      "  - alphas: List<Alpha>",
      "  + addAlpha(Alpha): void",
      "  + delAlpha(Alpha): void",
      "}",
      "",
      "Alpha::Beta \"0..1\" o-- Beta",
      "Gamma::alphas \"*\" o-- Alpha",
      " ",
      "@enduml"
    );

}

