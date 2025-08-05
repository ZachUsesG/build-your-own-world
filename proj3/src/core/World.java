package core;
import tileengine.TETile;
import tileengine.TERenderer;
import tileengine.Tileset;
import utils.RandomUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static tileengine.Tileset.WALL;

public class World {
        private static final int WIDTH = 60;
        private static final int HEIGHT = 30;

        public static void main(String[] args) {

            TERenderer ter = new TERenderer();
            ter.initialize(WIDTH, HEIGHT);

            TETile[][] world = new TETile[WIDTH][HEIGHT];
            for (int x = 0; x < WIDTH; x += 1) {
                for (int y = 0; y < HEIGHT; y += 1) {
                    world[x][y] = Tileset.NOTHING;
                }
            }
            long seed = 4979935467832411189L;
            Random random = new Random(seed);
            int minSize = 3;
            int maxSize = Math.min(WIDTH, HEIGHT) / 4;
            List<RoomLogistics> allRooms = new ArrayList<>();
            RoomLogistics previousRoom = null;
            for (int i = 0; i < 20; i++) {
                int size = RandomUtils.uniform(random, minSize, maxSize + 1);
                int x = RandomUtils.uniform(random, 1, WIDTH - size);
                int y = RandomUtils.uniform(random, 1, HEIGHT - size);
                boolean skipThisRoom = false;
                RoomLogistics newRoom = new RoomLogistics(x, y, size, size);
                for (RoomLogistics existingRoom : allRooms) {
                    if (newRoom.overlaps(existingRoom)) {
                        skipThisRoom = true;
                        break;
                    }
                }
                if (!skipThisRoom) {
                    drawInvertedPlusRoom(world, x, y, size);
                    allRooms.add(newRoom);
                    if (previousRoom != null) {
                        drawHallway(world, previousRoom, newRoom);
                    }
                    previousRoom = newRoom;
                }
            }
            for (int x = 1; x < WIDTH - 1; x++) {
                for (int y = 1; y < HEIGHT - 1; y++) {
                    if (world[x][y] == Tileset.NOTHING) {
                        boolean hasFloorNeighbor =
                                world[x+1][y] == Tileset.FLOOR || world[x-1][y] == Tileset.FLOOR ||
                                        world[x][y+1] == Tileset.FLOOR || world[x][y-1] == Tileset.FLOOR ||
                                        world[x+1][y+1] == Tileset.FLOOR || world[x-1][y+1] == Tileset.FLOOR ||
                                        world[x+1][y-1] == Tileset.FLOOR || world[x-1][y-1] == Tileset.FLOOR;

                        if (hasFloorNeighbor) {
                            world[x][y] = Tileset.WALL;
                        }
                    }
                }
            }
            ter.renderFrame(world);
        }
        public static void drawInvertedPlusRoom(TETile[][] world, int x, int y, int size) {
            for (int middleX = 0; middleX < size; middleX++) {
                for (int middleY = 0; middleY < size; middleY++) {
                    world[x + middleX][y + middleY] = Tileset.FLOOR;
                }
            }

            for (int topX = 0; topX < size; topX++) {
                for (int topY = 0; topY < size; topY++) {
                    world[x + topX][y + size] = WALL;
                }
            }

            for (int bottomX = 0; bottomX < size; bottomX++) {
                for (int bottomY = 0; bottomY < size; bottomY++) {
                    world[x + bottomX][y - 1] = WALL;
                }
            }

            for (int leftX = 0; leftX < size; leftX++) {
                for (int leftY = 0; leftY < size; leftY++) {
                    world[x - 1][y + leftY] = WALL;
                }
            }


            for (int rightX = 0; rightX < size; rightX++) {
                for (int rightY = 0; rightY < size; rightY++) {
                    world[x + size][y + rightY] = WALL;
                }
            }
        }
    public static void drawHallway(TETile[][] world, RoomLogistics r1, RoomLogistics r2) {
        int x1 = r1.centerX();
        int y1 = r1.centerY();
        int x2 = r2.centerX();
        int y2 = r2.centerY();
        int startX = Math.min(x1, x2);
        int endX = Math.max(x1, x2);
        int startY = Math.min(y1, y2);
        int endY = Math.max(y1, y2);

        for (int x = startX; x <= endX; x++) {
            world[x][y1] = Tileset.FLOOR;
        }
        for (int y = startY; y <= endY; y++) {
            world[x2][y] = Tileset.FLOOR;
        }
    }
    // @source
    // ChatGPT helped me debug wall placement logic in drawHallway regarding
    // where to put the walls for the loops in either main or drawHallway
    // The code structure and logic were written by me.
}

