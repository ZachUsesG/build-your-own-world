package core;

import edu.princeton.cs.algs4.StdDraw;
import java.awt.*;
import java.util.Random;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;

public class Menu {
    private int width;
    private int height;
    private Random rand;

    private static class SaveData {
        long seed;
        String actions;
        String raw;
    }
    private SaveData readSave() {
        try {
            String raw = new String(Files.readAllBytes(Paths.get("save.txt")), StandardCharsets.UTF_8).trim();
            if (raw.isEmpty()) return null;
            String cleaned = raw.replaceAll(":?q\\s*$", "");
            java.util.regex.Matcher m = java.util.regex.Pattern
                    .compile(".*?n(\\d+)s(.*)", java.util.regex.Pattern.CASE_INSENSITIVE)
                    .matcher(cleaned);
            if (!m.matches()) return null;

            SaveData sd = new SaveData();
            sd.seed = Long.parseLong(m.group(1));
            sd.actions = m.group(2).toLowerCase();
            sd.raw = cleaned;
            return sd;
        } catch (Exception e) {
            return null;
        }
    }
    public static void main(String[] args) {
        Menu game = new Menu(60, 30);
        game.drawMainMenu();
        game.waitForMenuInput();
    }

    public Menu(int width, int height) {
        this.width = width;
        this.height = height;

        StdDraw.setCanvasSize(this.width * 16, this.height * 16);
        StdDraw.setXscale(0, this.width);
        StdDraw.setYscale(0, this.height);
        StdDraw.clear(Color.BLACK);
        StdDraw.enableDoubleBuffering();
    }

    public void drawMainMenu() {
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        Font fontBig = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(fontBig);

        StdDraw.text(this.width / 2.0, this.height * 0.75, "CS61B: BYOW");
        StdDraw.text(this.width / 2.0, this.height * 0.55, "(N) New Game");
        StdDraw.text(this.width / 2.0, this.height * 0.45, "(L) Load Game");
        StdDraw.text(this.width / 2.0, this.height * 0.35, "(Q) Quit");


        StdDraw.show();
    }

    public void drawFrame(String s) {
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        Font fontBig = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(fontBig);
        StdDraw.text(this.width / 2, this.height / 2, s);
        StdDraw.show();
    }

    public void waitForMenuInput() {
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char key = Character.toLowerCase(StdDraw.nextKeyTyped());

                if (key == 'n') {
                    while (StdDraw.hasNextKeyTyped()) {
                        StdDraw.nextKeyTyped();
                    }
                    StringBuilder history = new StringBuilder();
                    history.append('n');

                    String seedDigits = promptSeedDigits();
                    if (seedDigits.isEmpty()) {
                        while (StdDraw.hasNextKeyTyped()) {
                            StdDraw.nextKeyTyped();
                        }
                        drawMainMenu();
                        continue;
                    }
                    history.append(seedDigits).append('s');

                    long seed = Long.parseLong(seedDigits);
                    drawFrame("Generating world with seed: " + seed);
                    World.worldGenerate(seed, history);
                    break;

                } else if (key == 'l') {
                    SaveData sd = readSave();
                    if (sd == null) {
                        drawFrame("No valid save found.");
                        StdDraw.pause(900);
                        drawMainMenu();
                        continue;
                    }
                    StringBuilder history = new StringBuilder(sd.raw);
                    drawFrame("Loading...");
                    World.worldGenerate(sd.seed, history, sd.actions);
                    break;

                } else if (key == 'q') {
                    System.exit(0);
                }
                StdDraw.pause(15);
            }
        }
    }

    private String promptSeedDigits() {
        drawFrame("Enter Seed:  (S=start, Q=cancel)");
        StringBuilder digits = new StringBuilder();
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char c = StdDraw.nextKeyTyped();
                if (c == 'q' || c == 'Q') {
                    return "";
                } else if (c == 's' || c == 'S') {
                    break;
                } else if (Character.isDigit(c)) {
                    digits.append(c);
                    drawFrame("Enter Seed: " + digits + "  (S=start, Q=cancel)");
                }
            }
            StdDraw.pause(15);
        }
        if (digits.length() == 0) {
            return promptSeedDigits();
        }
        return digits.toString();
    }

    public long promptSeedInput() {
        drawFrame("Enter Seed:  (S=start, Q=cancel)");

        StringBuilder waitArray = new StringBuilder();
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char c = StdDraw.nextKeyTyped();

                if (c == 'q' || c == 'Q') {
                    return -1L;
                }
                if (c == 's' || c == 'S') {
                    break;
                }
                if (Character.isDigit(c)) {
                    waitArray.append(c);
                    drawFrame("Enter Seed: " + waitArray.toString() + "  (S=start, Q=cancel)");
                }
            }
            StdDraw.pause(15);
        }

        if (waitArray.length() == 0) {
            return promptSeedInput();
        }

        return Long.parseLong(waitArray.toString());
    }
}


