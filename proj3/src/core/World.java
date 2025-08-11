package core;

import core.Corridor;
import core.Biome;
import tileengine.TETile;
import tileengine.TERenderer;
import tileengine.Tileset;
import utils.RandomUtils;
import edu.princeton.cs.algs4.StdDraw;

import java.awt.Color;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class World {
    private static final int WIDTH = 120, HEIGHT = 80, VIEW_W = 60, VIEW_H = 30;
    private static final TETile FLOOR  = new TETile('.', new Color(180,180,180), new Color(12,12,12), "floor", 1);
    private static final TETile WALL   = Tileset.WALL;
    private static final TETile AVATAR = new TETile('✹', new Color(255,215,0), Color.BLACK, "you", 1);
    private static int clamp(int v, int lo, int hi) {
        return Math.max(lo, Math.min(hi, v));
    }
    private static boolean inBounds(int x, int y) {
        return x >= 0 && x < WIDTH && y >= 0 && y < HEIGHT;
    }
    private static boolean walk(TETile t) {
        return t != null && t != Tileset.NOTHING && t != WALL;
    }
    private static int manh(Room a, Room b) {
        return Math.abs(a.cx() - b.cx()) + Math.abs(a.cy() - b.cy());
    }
    private static boolean isCorridor(TETile t) { return t == FLOOR; }
    private static boolean isRoomFloor(TETile t) { return t != null && t != Tileset.NOTHING && t != WALL && t != FLOOR; }
    private static void thinDoubleWide(TETile[][] w) {
        int W = w.length, H = w[0].length;
        for (int y = 1; y < H - 2; y++) {
            for (int x = 1; x < W - 1; x++) {
                if (w[x][y] == FLOOR && w[x][y + 1] == FLOOR && w[x][y - 1] != FLOOR && w[x][y + 2] != FLOOR) {
                    w[x][y + 1] = Tileset.NOTHING;
                }
            }
        }
        for (int y = 1; y < H - 1; y++) {
            for (int x = 1; x < W - 2; x++) {
                if (w[x][y] == FLOOR && w[x + 1][y] == FLOOR && w[x - 1][y] != FLOOR && w[x + 2][y] != FLOOR) {
                    w[x + 1][y] = Tileset.NOTHING;
                }
            }
        }
    }
    private static class Room {
        int x, y, w, h;
        Biome b;

        Room(int x, int y, int w, int h, Biome b) {
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
            this.b = b;
        }

        boolean overlaps(Room o) {
            return x - 1 + w + 2 > o.x && o.x - 1 + o.w + 2 > x
                    && y - 1 + h + 2 > o.y && o.y - 1 + o.h + 2 > y;
        }

        int cx() {
            return x + w / 2;
        }

        int cy() {
            return y + h / 2;
        }

        boolean contains(int px, int py) {
            return px >= x && px < x + w && py >= y && py < y + h;
        }
    }
    private static void fill(TETile[][] w, int x0, int y0, int x1, int y1, TETile t) {
        for (int x = x0; x <= x1; x++)
            for (int y = y0; y <= y1; y++)
                if (inBounds(x, y)) w[x][y] = t;
    }

    private static void roomBox(TETile[][] w, Room r) {
        fill(w, r.x, r.y, r.x + r.w - 1, r.y + r.h - 1, FLOOR);
        for (int x = r.x - 1; x <= r.x + r.w; x++) {
            if (inBounds(x, r.y - 1) && w[x][r.y - 1] == Tileset.NOTHING) w[x][r.y - 1] = WALL;
            if (inBounds(x, r.y + r.h) && w[x][r.y + r.h] == Tileset.NOTHING) w[x][r.y + r.h] = WALL;
        }
        for (int y = r.y - 1; y <= r.y + r.h; y++) {
            if (inBounds(r.x - 1, y) && w[r.x - 1][y] == Tileset.NOTHING) w[r.x - 1][y] = WALL;
            if (inBounds(r.x + r.w, y) && w[r.x + r.w][y] == Tileset.NOTHING) w[r.x + r.w][y] = WALL;
        }
    }

    private static void corridorL(TETile[][] w, int x1, int y1, int x2, int y2) {
        int sx = (x2 >= x1) ? 1 : -1;
        for (int x = x1; x != x2; x += sx) if (inBounds(x, y1)) w[x][y1] = FLOOR;
        int sy = (y2 >= y1) ? 1 : -1;
        for (int y = y1; y != y2; y += sy) if (inBounds(x2, y)) w[x2][y] = FLOOR;
        if (inBounds(x2, y2)) w[x2][y2] = FLOOR;
    }

    private static void rebuildWalls(TETile[][] w) {
        for (int x = 0; x < WIDTH; x++)
            for (int y = 0; y < HEIGHT; y++)
                if (w[x][y] == WALL) w[x][y] = Tileset.NOTHING;

        for (int x = 0; x < WIDTH; x++)
            for (int y = 0; y < HEIGHT; y++) {
                if (w[x][y] != Tileset.NOTHING) continue;
                if ((x > 0 && walk(w[x - 1][y])) || (x + 1 < WIDTH && walk(w[x + 1][y]))
                        || (y > 0 && walk(w[x][y - 1])) || (y + 1 < HEIGHT && walk(w[x][y + 1]))) {
                    w[x][y] = WALL;
                }
            }
    }

    private static void skinRooms(TETile[][] w, List<Room> rooms, Random r) {
        for (Room rm : rooms) {
            Biome b = rm.b;
            for (int x = rm.x; x < rm.x + rm.w; x++) {
                for (int y = rm.y; y < rm.y + rm.h; y++) {
                    if (w[x][y] != FLOOR) continue;
                    w[x][y] = b.base;
                    if (b.accents != null && r.nextDouble() < b.p) {
                        TETile c = b.accents[r.nextInt(b.accents.length)];
                        w[x][y] = c;
                        if (r.nextDouble() < b.cluster) {
                            int[][] d = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
                            int[] k = d[r.nextInt(d.length)];
                            int nx = x + k[0], ny = y + k[1];
                            if (nx >= rm.x && nx < rm.x + rm.w && ny >= rm.y && ny < rm.y + rm.h && w[nx][ny] == b.base) {
                                w[nx][ny] = c;
                            }
                        }
                    }
                }
            }
        }
    }
    private static void skinCorridors(TETile[][] w) {
        boolean[][] c = new boolean[WIDTH][HEIGHT];
        for (int x = 0; x < WIDTH; x++)
            for (int y = 0; y < HEIGHT; y++)
                c[x][y] = (w[x][y] == FLOOR);

        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                if (!c[x][y]) continue;
                boolean n = (y + 1 < HEIGHT) && c[x][y + 1];
                boolean e = (x + 1 < WIDTH)  && c[x + 1][y];
                boolean s = (y - 1 >= 0)     && c[x][y - 1];
                boolean wL= (x - 1 >= 0)     && c[x - 1][y];
                w[x][y] = Corridor.tile(n, e, s, wL);
            }
        }
    }

    private static int corrDeg(TETile[][] w, int x, int y) {
        int d = 0;
        if (x > 0        && isCorridor(w[x-1][y])) d++;
        if (x+1 < WIDTH  && isCorridor(w[x+1][y])) d++;
        if (y > 0        && isCorridor(w[x][y-1])) d++;
        if (y+1 < HEIGHT && isCorridor(w[x][y+1])) d++;
        return d;
    }
    private static boolean isDoorCell(TETile[][] w, int x, int y) {
        if (w[x][y] != FLOOR) return false;
        boolean roomAdj = (x > 0 && isRoomFloor(w[x - 1][y]))
                || (x + 1 < WIDTH && isRoomFloor(w[x + 1][y]))
                || (y > 0 && isRoomFloor(w[x][y - 1]))
                || (y + 1 < HEIGHT && isRoomFloor(w[x][y + 1]));
        boolean corrAdj = (x > 0 && isCorridor(w[x - 1][y]))
                || (x + 1 < WIDTH && isCorridor(w[x + 1][y]))
                || (y > 0 && isCorridor(w[x][y - 1]))
                || (y + 1 < HEIGHT && isCorridor(w[x][y + 1]));
        return roomAdj && corrAdj;
    }

    private static void pruneStubsNearDoors(TETile[][] w) {
        boolean ch;
        do {
            ch = false;
            for (int x = 1; x < WIDTH - 1; x++) {
                for (int y = 1; y < HEIGHT - 1; y++) {
                    if (!isCorridor(w[x][y])) continue;
                    int d = corrDeg(w, x, y);
                    if (d == 0) {
                        w[x][y] = Tileset.NOTHING;
                        ch = true;
                        continue;
                    }
                    if (d == 1) {
                        boolean nearDoor = isDoorCell(w, x - 1, y) || isDoorCell(w, x + 1, y)
                                || isDoorCell(w, x, y - 1) || isDoorCell(w, x, y + 1);
                        if (nearDoor) {
                            w[x][y] = Tileset.NOTHING;
                            ch = true;
                        }
                    }
                }
            }
        } while (ch);
    }
    private static void punchDeadEnds(TETile[][] w) {
        int[] dx = {1, -1, 0, 0}, dy = {0, 0, 1, -1};
        for (int y = 1; y < HEIGHT - 1; y++) {
            for (int x = 1; x < WIDTH - 1; x++) {
                if (!isCorridor(w[x][y]) || corrDeg(w, x, y) != 1) continue;
                for (int k = 0; k < 4; k++) {
                    int wx1 = x + dx[k], wy1 = y + dy[k];
                    int wx2 = x + 2 * dx[k], wy2 = y + 2 * dy[k];
                    int rf  = x + 3 * dx[k], rfy = y + 3 * dy[k];
                    if (inBounds(wx2, wy2) && w[wx1][wy1] == WALL && isRoomFloor(w[wx2][wy2])) {
                        w[wx1][wy1] = FLOOR;
                        break;
                    }
                    if (inBounds(rf, rfy) && w[wx1][wy1] == WALL && w[wx2][wy2] == WALL && isRoomFloor(w[rf][rfy])) {
                        w[wx1][wy1] = FLOOR;
                        w[wx2][wy2] = FLOOR;
                        break;
                    }
                }
            }
        }
    }

    private static void openDeadEndForward(TETile[][] w, int x, int y) {
        int[] f = forwardDirOfDeadEnd(w, x, y);
        int fx = f[0], fy = f[1];
        if (fx == 0 && fy == 0) return;

        int cx = x + fx, cy = y + fy;
        if (!inBounds(cx, cy) || isCorridor(w[cx][cy])) return;
        final int MAX_STEPS = Math.max(WIDTH, HEIGHT);
        int steps = 0;
        while (inBounds(cx, cy) && steps < MAX_STEPS && (w[cx][cy] == Tileset.NOTHING || w[cx][cy] == WALL)) {
            w[cx][cy] = FLOOR;
            cx += fx; cy += fy; steps++;
        }
        if (!inBounds(cx, cy)) return;
        if (isRoomInterior(w[cx][cy])) {
            int px = cx - fx, py = cy - fy;
            if (inBounds(px, py) && w[px][py] == WALL) w[px][py] = FLOOR;
        }
    }
    private static void openAllDeadEndsToRooms(TETile[][] w) {
        for (int y = 1; y < HEIGHT - 1; y++) {
            for (int x = 1; x < WIDTH - 1; x++) {
                if (isCorridor(w[x][y])) openDeadEndForward(w, x, y);
            }
        }
    }

    private static void extendDeadEndForward(TETile[][] w, int x, int y) {
        if (!isCorridor(w[x][y]) || corrDeg(w, x, y) != 1) return;
        int bx = 0, by = 0;
        if (x > 0        && isCorridor(w[x-1][y])) { bx = -1; by = 0; }
        if (x+1 < WIDTH  && isCorridor(w[x+1][y])) { bx = +1; by = 0; }
        if (y > 0        && isCorridor(w[x][y-1])) { bx = 0;  by = -1; }
        if (y+1 < HEIGHT && isCorridor(w[x][y+1])) { bx = 0;  by = +1; }
        int fx = -bx, fy = -by;
        if (fx == 0 && fy == 0) return;
        int cx = x + fx, cy = y + fy;
        if (!inBounds(cx, cy)) return;
        if (w[cx][cy] != WALL) return;
        int ex = cx, ey = cy;
        while (inBounds(ex, ey) && w[ex][ey] == WALL) { ex += fx; ey += fy; }
        if (!inBounds(ex, ey)) return;
        if (!isRoomInterior(w[ex][ey])) return;
        int kx = cx, ky = cy;
        while (kx != ex || ky != ey) {
            if (inBounds(kx, ky) && w[kx][ky] == WALL) w[kx][ky] = FLOOR;
            kx += fx; ky += fy;
        }
    }

    private static void openDeadEndsAtRoomEnds(TETile[][] w) {
        for (int y = 1; y < HEIGHT - 1; y++) {
            for (int x = 1; x < WIDTH - 1; x++) {
                if (isCorridor(w[x][y]) && corrDeg(w, x, y) == 1) {
                    extendDeadEndForward(w, x, y);
                }
            }
        }
    }

    private static boolean corridorOutside(TETile[][] w, int wx, int wy, int dx, int dy) {
        int x = wx + dx, y = wy + dy;
        for (int steps = 0; steps < 2; steps++) {
            if (!inBounds(x, y)) return false;
            if (isCorridor(w[x][y])) return true;
            if (w[x][y] != WALL) return false;
            x += dx; y += dy;
        }
        return false;
    }

    private static int corrX(TETile[][] w, int wx, int wy, int dx) {
        int x = wx + dx;
        while (inBounds(x, wy) && w[x][wy] == WALL) x += dx;
        return inBounds(x, wy) ? x : wx;
    }

    private static int corrY(TETile[][] w, int wx, int wy, int dy) {
        int y = wy + dy;
        while (inBounds(wx, y) && w[wx][y] == WALL) y += dy;
        return inBounds(wx, y) ? y : wy;
    }

    private static void openBridge(TETile[][] w, int wx, int wy, int dx, int dy) {
        if (inBounds(wx, wy) && w[wx][wy] == WALL) w[wx][wy] = FLOOR;
        int nx = wx + dx, ny = wy + dy;
        if (inBounds(nx, ny) && w[nx][ny] == WALL) w[nx][ny] = FLOOR;
    }

    private static void punchDoorsByRoomSides(TETile[][] w, List<Room> rooms) {
        for (Room r : rooms) {
            int wx = r.x + r.w;
            for (int y = r.y; y <= r.y + r.h - 1; ) {
                boolean adj = inBounds(wx, y) && w[wx][y] == WALL
                        && inBounds(wx - 1, y) && isRoomFloor(w[wx - 1][y])
                        && corridorOutside(w, wx, y, +1, 0);
                if (!adj) { y++; continue; }

                int ys = y;
                while (y <= r.y + r.h - 1
                        && inBounds(wx, y) && w[wx][y] == WALL
                        && inBounds(wx - 1, y) && isRoomFloor(w[wx - 1][y])
                        && corridorOutside(w, wx, y, +1, 0)) y++;
                int ye = y - 1;

                if (runHasDoor(w, wx, ys, ye)) continue;

                int doorY = -1;
                for (int ty = ys; ty <= ye; ty++) {
                    int cx = corrX(w, wx, ty, +1);
                    if (inBounds(cx, ty) && isCorridor(w[cx][ty]) &&
                            isStubFacing(w, cx, ty, +1, 0)) {
                        doorY = ty; break;
                    }
                }
                if (doorY != -1) openBridge(w, wx, doorY, +1, 0);
            }
            wx = r.x - 1;
            for (int y = r.y; y <= r.y + r.h - 1; ) {
                boolean adj = inBounds(wx, y) && w[wx][y] == WALL
                        && inBounds(wx + 1, y) && isRoomFloor(w[wx + 1][y])
                        && corridorOutside(w, wx, y, -1, 0);
                if (!adj) { y++; continue; }

                int ys = y;
                while (y <= r.y + r.h - 1
                        && inBounds(wx, y) && w[wx][y] == WALL
                        && inBounds(wx + 1, y) && isRoomFloor(w[wx + 1][y])
                        && corridorOutside(w, wx, y, -1, 0)) y++;
                int ye = y - 1;

                if (runHasDoor(w, wx, ys, ye)) continue;

                int doorY = -1;
                for (int ty = ys; ty <= ye; ty++) {
                    int cx = corrX(w, wx, ty, -1);
                    if (inBounds(cx, ty) && isCorridor(w[cx][ty]) &&
                            isStubFacing(w, cx, ty, -1, 0)) {
                        doorY = ty; break;
                    }
                }
                if (doorY != -1) openBridge(w, wx, doorY, -1, 0);
            }
            int wy = r.y + r.h;
            for (int x = r.x; x <= r.x + r.w - 1; ) {
                boolean adj = inBounds(x, wy) && w[x][wy] == WALL
                        && inBounds(x, wy - 1) && isRoomFloor(w[x][wy - 1])
                        && corridorOutside(w, x, wy, 0, +1);
                if (!adj) { x++; continue; }

                int xs = x;
                while (x <= r.x + r.w - 1
                        && inBounds(x, wy) && w[x][wy] == WALL
                        && inBounds(x, wy - 1) && isRoomFloor(w[x][wy - 1])
                        && corridorOutside(w, x, wy, 0, +1)) x++;
                int xe = x - 1;

                if (runHasDoorX(w, wy, xs, xe)) continue;

                int doorX = -1;
                for (int tx = xs; tx <= xe; tx++) {
                    int cy = corrY(w, tx, wy, +1);
                    if (inBounds(tx, cy) && isCorridor(w[tx][cy]) &&
                            isStubFacing(w, tx, cy, 0, +1)) {
                        doorX = tx; break;
                    }
                }
                if (doorX != -1) openBridge(w, doorX, wy, 0, +1);
            }
            wy = r.y - 1;
            for (int x = r.x; x <= r.x + r.w - 1; ) {
                boolean adj = inBounds(x, wy) && w[x][wy] == WALL
                        && inBounds(x, wy + 1) && isRoomFloor(w[x][wy + 1])
                        && corridorOutside(w, x, wy, 0, -1);
                if (!adj) { x++; continue; }

                int xs = x;
                while (x <= r.x + r.w - 1
                        && inBounds(x, wy) && w[x][wy] == WALL
                        && inBounds(x, wy + 1) && isRoomFloor(w[x][wy + 1])
                        && corridorOutside(w, x, wy, 0, -1)) x++;
                int xe = x - 1;

                if (runHasDoorX(w, wy, xs, xe)) continue;

                int doorX = -1;
                for (int tx = xs; tx <= xe; tx++) {
                    int cy = corrY(w, tx, wy, -1);
                    if (inBounds(tx, cy) && isCorridor(w[tx][cy]) &&
                            isStubFacing(w, tx, cy, 0, -1)) {
                        doorX = tx; break;
                    }
                }
                if (doorX != -1) openBridge(w, doorX, wy, 0, -1);
            }
        }
    }

    private static boolean isStubFacing(TETile[][] w, int x, int y, int fx, int fy) {
        if (!isCorridor(w[x][y])) return false;
        int d=0, nx=0, ny=0;
        if (x>0        && isCorridor(w[x-1][y])) { d++; nx=x-1; ny=y; }
        if (x+1<WIDTH  && isCorridor(w[x+1][y])) { d++; nx=x+1; ny=y; }
        if (y>0        && isCorridor(w[x][y-1])) { d++; nx=x;   ny=y-1; }
        if (y+1<HEIGHT && isCorridor(w[x][y+1])) { d++; nx=x;   ny=y+1; }
        return d==1 && nx==x+fx && ny==y+fy;
    }

    private static boolean runHasDoor(TETile[][] w, int wx, int ys, int ye) {
        for (int y=ys; y<=ye; y++)
            if (inBounds(wx, y) && w[wx][y]==FLOOR && isDoorCell(w, wx, y)) return true;
        return false;
    }
    private static boolean runHasDoorX(TETile[][] w, int wy, int xs, int xe) {
        for (int x=xs; x<=xe; x++)
            if (inBounds(x, wy) && w[x][wy]==FLOOR && isDoorCell(w, x, wy)) return true;
        return false;
    }

    private static void bfsFromCorridors(TETile[][] w, int[][] dist, int[][] px, int[][] py) {
        for (int x = 0; x < WIDTH; x++)
            for (int y = 0; y < HEIGHT; y++) {
                dist[x][y] = Integer.MAX_VALUE; px[x][y] = -1; py[x][y] = -1;
            }
        ArrayDeque<int[]> q = new ArrayDeque<>();
        for (int x = 0; x < WIDTH; x++)
            for (int y = 0; y < HEIGHT; y++)
                if (isCorridor(w[x][y])) { dist[x][y] = 0; q.add(new int[]{x,y}); }

        int[] dx = {1,-1,0,0}, dy = {0,0,1,-1};
        while (!q.isEmpty()) {
            int[] p = q.poll(); int x = p[0], y = p[1];
            for (int k = 0; k < 4; k++) {
                int nx = x + dx[k], ny = y + dy[k];
                if (!inBounds(nx, ny)) continue;
                if (isRoomFloor(w[nx][ny])) continue;
                if (dist[nx][ny] != Integer.MAX_VALUE) continue;
                dist[nx][ny] = dist[x][y] + 1; px[nx][ny] = x; py[nx][ny] = y;
                q.add(new int[]{nx, ny});
            }
        }
    }
    private static void connectUnreachableRooms(TETile[][] w, List<Room> rooms) {
        int[][] dist = new int[WIDTH][HEIGHT];
        int[][] px   = new int[WIDTH][HEIGHT];
        int[][] py   = new int[WIDTH][HEIGHT];
        bfsFromCorridors(w, dist, px, py);

        for (Room r : rooms) {
            if (roomHasDoor(w, r)) continue;

            int best = Integer.MAX_VALUE, bx = -1, by = -1, sdx = 0, sdy = 0;

            int wx = r.x + r.w;
            for (int y = r.y; y <= r.y + r.h - 1; y++)
                if (inBounds(wx, y) && dist[wx][y] < best) { best = dist[wx][y]; bx = wx; by = y; sdx = +1; sdy = 0; }
            wx = r.x - 1;
            for (int y = r.y; y <= r.y + r.h - 1; y++)
                if (inBounds(wx, y) && dist[wx][y] < best) { best = dist[wx][y]; bx = wx; by = y; sdx = -1; sdy = 0; }
            int wy = r.y + r.h;
            for (int x = r.x; x <= r.x + r.w - 1; x++)
                if (inBounds(x, wy) && dist[x][wy] < best) { best = dist[x][wy]; bx = x; by = wy; sdx = 0; sdy = +1; }
            wy = r.y - 1;
            for (int x = r.x; x <= r.x + r.w - 1; x++)
                if (inBounds(x, wy) && dist[x][wy] < best) { best = dist[x][wy]; bx = x; by = wy; sdx = 0; sdy = -1; }

            if (best == Integer.MAX_VALUE) continue;
            openBridge(w, bx, by, sdx, sdy);
            int cx = bx, cy = by;
            while (dist[cx][cy] > 0) {
                if (w[cx][cy] == Tileset.NOTHING || w[cx][cy] == WALL) w[cx][cy] = FLOOR;
                int nx = px[cx][cy], ny = py[cx][cy];
                if (nx == -1) break;
                cx = nx; cy = ny;
            }
        }
    }
    private static int[] centerOn(int ax,int ay){
        int xOff=clamp(ax-VIEW_W/2,0,WIDTH-VIEW_W), yOff=clamp(ay-VIEW_H/2,0,HEIGHT-VIEW_H);
        return new int[]{xOff,yOff};
    }
    private static boolean roomHasDoor(TETile[][] w, Room r) {
        int xR = r.x + r.w, xL = r.x - 1, yT = r.y + r.h, yB = r.y - 1;
        for (int y = r.y; y <= r.y + r.h - 1; y++) {
            if (inBounds(xR, y) && w[xR][y] == FLOOR && isDoorCell(w, xR, y)) return true;
            if (inBounds(xL, y) && w[xL][y] == FLOOR && isDoorCell(w, xL, y)) return true;
        }
        for (int x = r.x; x <= r.x + r.w - 1; x++) {
            if (inBounds(x, yT) && w[x][yT] == FLOOR && isDoorCell(w, x, yT)) return true;
            if (inBounds(x, yB) && w[x][yB] == FLOOR && isDoorCell(w, x, yB)) return true;
        }
        return false;
    }
    private static int[] nearestCorridorTo(TETile[][] w, int x, int y) {
        int bestD = Integer.MAX_VALUE, bx = -1, by = -1;
        for (int ix = 0; ix < WIDTH; ix++) {
            for (int iy = 0; iy < HEIGHT; iy++) {
                if (isCorridor(w[ix][iy])) {
                    int d = Math.abs(ix - x) + Math.abs(iy - y);
                    if (d < bestD) { bestD = d; bx = ix; by = iy; }
                }
            }
        }
        return new int[]{bx, by};
    }
    private static void connectRoomToCorridor(TETile[][] w, Room r, int tx, int ty) {
        int wx = r.x - 1, wy = clamp(ty, r.y, r.y + r.h - 1);  int dx = -1, dy = 0;
        int bestX = wx, bestY = wy; int bestDist = Math.abs(wx - tx) + Math.abs(wy - ty);
        wx = r.x + r.w; wy = clamp(ty, r.y, r.y + r.h - 1);
        int dist = Math.abs(wx - tx) + Math.abs(wy - ty);
        if (dist < bestDist) { bestDist = dist; bestX = wx; bestY = wy; dx = +1; dy = 0; }
        wx = clamp(tx, r.x, r.x + r.w - 1); wy = r.y - 1;
        dist = Math.abs(wx - tx) + Math.abs(wy - ty);
        if (dist < bestDist) { bestDist = dist; bestX = wx; bestY = wy; dx = 0; dy = -1; }
        wx = clamp(tx, r.x, r.x + r.w - 1); wy = r.y + r.h;
        dist = Math.abs(wx - tx) + Math.abs(wy - ty);
        if (dist < bestDist) { bestDist = dist; bestX = wx; bestY = wy; dx = 0; dy = +1; }
        openBridge(w, bestX, bestY, dx, dy);
        int sx = bestX + dx, sy = bestY + dy;
        corridorL(w, sx, sy, tx, ty);
    }
    private static void ensureAllRoomsReachable(TETile[][] w, List<Room> rooms) {
        for (Room r : rooms) {
            if (roomHasDoor(w, r)) continue;

            int[] target = nearestCorridorTo(w, r.cx(), r.cy());
            int tx = target[0], ty = target[1];
            if (tx == -1) continue;

            connectRoomToCorridor(w, r, tx, ty);
        }
    }
    private static void generate(TETile[][] w,long seed){
        for (int x = 0; x < WIDTH; x++) for (int y = 0; y < HEIGHT; y++) w[x][y] = Tileset.NOTHING;

        Random rW = new Random(seed);
        List<Room> rooms = new ArrayList<>();
        boolean mosaicLandscape = rW.nextDouble() < 0.15;
        Biome worldBiome = mosaicLandscape ? null : Biome.random(rW);
        int TRIES=45;
        while(TRIES-- > 0){
            int rw=RandomUtils.uniform(rW,6,14), rh=RandomUtils.uniform(rW,6,14);
            int rx=RandomUtils.uniform(rW,2,WIDTH-rw-2), ry=RandomUtils.uniform(rW,2,HEIGHT-rh-2);
            Room cand = new Room(
                    rx, ry, rw, rh,
                    mosaicLandscape ? Biome.random(rW) : worldBiome
            );            boolean ok=true; for(Room o:rooms){ if(cand.overlaps(o)){ok=false;break;} }
            if(!ok) continue; rooms.add(cand); roomBox(w,cand);
        }

        int n=rooms.size();
        if(n>=2){
            final int K=2; boolean[][] allow=new boolean[n][n];
            for(int i=0;i<n;i++){
                Integer[] ord = new Integer[n - 1];
                for (int p = 0, q = 0; p < n; p++) if (p != i) ord[q++] = p;

                final Room ri = rooms.get(i);
                Arrays.sort(ord, Comparator.comparingInt(j -> manh(ri, rooms.get(j))));

                for (int k = 0; k < Math.min(K, ord.length); k++) {
                    int j = ord[k];
                    allow[i][j] = allow[j][i] = true;
                }
            }
            boolean[] vis=new boolean[n]; vis[0]=true; int visC=1; boolean[][] used=new boolean[n][n];
            PriorityQueue<int[]> pq=new PriorityQueue<>(Comparator.comparingInt(a->a[0]));
            for(int j=1;j<n;j++) if(allow[0][j]) pq.add(new int[]{manh(rooms.get(0),rooms.get(j)),0,j});
            while(visC<n&&!pq.isEmpty()){
                int[] e=pq.poll(); int to=e[2]; if(vis[to]) continue; int from=e[1];
                corridorL(w,rooms.get(from).cx(),rooms.get(from).cy(),rooms.get(to).cx(),rooms.get(to).cy());
                used[from][to]=used[to][from]=true; vis[to]=true; visC++;
                for(int j=0;j<n;j++) if(!vis[j]&&allow[to][j]) pq.add(new int[]{manh(rooms.get(to),rooms.get(j)),to,j});
            }
            int[] degs=new int[n]; for(int i=0;i<n;i++) for(int j=0;j<n;j++) if(used[i][j]) degs[i]++;
            for(int i=0;i<n;i++){
                if(degs[i]!=1) continue;
                ArrayList<int[]> cand=new ArrayList<>();
                for(int j=0;j<n;j++){ if(j==i||used[i][j]) continue; int d=manh(rooms.get(i),rooms.get(j)); int pref=allow[i][j]?0:1; cand.add(new int[]{pref,d,j}); }
                cand.sort(Comparator.comparingInt((int[]a)->a[0]).thenComparingInt(a->a[1]));
                int added=0;
                for(int[] c:cand){ if(added==2||degs[i]>=3) break; int j=c[2];
                    corridorL(w,rooms.get(i).cx(),rooms.get(i).cy(),rooms.get(j).cx(),rooms.get(j).cy());
                    used[i][j]=used[j][i]=true; degs[i]++; degs[j]++; added++;
                }
            }
            Random r2=new Random(seed*31L+7);
            final double EXTRA_P=0.18;
            for(int i=0;i<n;i++) for(int j=i+1;j<n;j++) if(allow[i][j]&&r2.nextDouble()<EXTRA_P)
                corridorL(w,rooms.get(i).cx(),rooms.get(i).cy(),rooms.get(j).cx(),rooms.get(j).cy());
        }

        rebuildWalls(w);
        skinRooms(w, rooms, new Random(seed ^ 0x9E3779B97F4A7C15L));
        thinDoubleWide(w);
        punchDeadEnds(w);
        resolveAllDeadEnds(w);
        punchDoorsByRoomSides(w, rooms);
        pruneStubsNearDoors(w);
        connectUnreachableRooms(w, rooms);
        ensureAllRoomsReachable(w, rooms);
        rebuildWalls(w);
        skinCorridors(w);
    }
    public static void worldGenerate(long seed,StringBuilder hist){ worldGenerate(seed,hist,null); }
    public static void worldGenerate(long seed,StringBuilder hist,String preload){
        TERenderer ter=new TERenderer(); ter.initialize(VIEW_W,VIEW_H,0,0);
        TETile[][] w=new TETile[WIDTH][HEIGHT]; generate(w,seed);
        int ax=1,ay=1; outer:for(int y=0;y<HEIGHT;y++) for(int x=0;x<WIDTH;x++) if(walk(w[x][y])){ax=x;ay=y;break outer;}
        TETile under=w[ax][ay]; w[ax][ay]=AVATAR;

        int[] off=centerOn(ax,ay); int xOff=off[0], yOff=off[1]; ter.setOffset(-xOff,-yOff);
        if(preload!=null) for(char c:preload.toCharArray()){
            int dx=0,dy=0; if(c=='w')dy=+1; else if(c=='s')dy=-1; else if(c=='a')dx=-1; else if(c=='d')dx=+1; else continue;
            int nx=ax+dx,ny=ay+dy; if(inBounds(nx,ny)&&walk(w[nx][ny])){
                w[ax][ay]=under; under=w[nx][ny]; ax=nx; ay=ny; w[ax][ay]=AVATAR;
                off=centerOn(ax,ay); xOff=off[0]; yOff=off[1]; ter.setOffset(-xOff,-yOff); hist.append(c);
            }
        }

        boolean pending=false;
        while(true){
            if(StdDraw.hasNextKeyTyped()){
                char k=StdDraw.nextKeyTyped();
                if(k==':'){pending=true; hist.append(':'); continue;}
                if(pending){
                    if(k=='q'||k=='Q'){
                        hist.append('q');
                        try(FileWriter fw=new FileWriter("save.txt")){fw.write(hist.toString());}catch(IOException ignored){}
                        System.exit(0);
                    }
                    pending=false; continue;
                }
                char c=Character.toLowerCase(k);
                if(c=='w'||c=='a'||c=='s'||c=='d'){
                    int dx=0,dy=0; if(c=='w')dy=+1; if(c=='s')dy=-1; if(c=='a')dx=-1; if(c=='d')dx=+1;
                    int nx=ax+dx,ny=ay+dy; if(inBounds(nx,ny)&&walk(w[nx][ny])){
                        w[ax][ay]=under; under=w[nx][ny]; ax=nx; ay=ny; w[ax][ay]=AVATAR;
                        off=centerOn(ax,ay); xOff=off[0]; yOff=off[1]; ter.setOffset(-xOff,-yOff); hist.append(c);
                    }
                }
            }
            StdDraw.clear(Color.BLACK);
            ter.drawTiles(w);
            StdDraw.setPenColor(Color.BLACK);
            StdDraw.filledRectangle(VIEW_W/2.0,VIEW_H-0.5,VIEW_W/2.0,0.6);
            StdDraw.setPenColor(Color.WHITE);
            int mx=clamp((int)Math.floor(StdDraw.mouseX())+xOff,0,WIDTH-1);
            int my=clamp((int)Math.floor(StdDraw.mouseY())+yOff,0,HEIGHT-1);
            StdDraw.textLeft(1,VIEW_H-1,w[mx][my].description());
            StdDraw.show(); StdDraw.pause(15);
        }
    }
    private static boolean isRoomInterior(TETile t) {
        return t != null && t != Tileset.NOTHING && t != WALL && t != FLOOR;
    }

    private static int[] forwardDirOfDeadEnd(TETile[][] w, int x, int y) {
        if (!isCorridor(w[x][y])) return new int[]{0,0};
        int deg = 0, bx = 0, by = 0;
        if (x > 0        && isCorridor(w[x-1][y])) { deg++; bx = -1; by = 0; }
        if (x+1 < WIDTH  && isCorridor(w[x+1][y])) { deg++; bx = +1; by = 0; }
        if (y > 0        && isCorridor(w[x][y-1])) { deg++; bx = 0;  by = -1; }
        if (y+1 < HEIGHT && isCorridor(w[x][y+1])) { deg++; bx = 0;  by = +1; }
        if (deg != 1) return new int[]{0,0};
        return new int[]{-bx, -by};
    }

    private static void backtrimStub(TETile[][] w, int x, int y) {
        int cx = x, cy = y;
        while (inBounds(cx, cy) && isCorridor(w[cx][cy]) && corrDeg(w, cx, cy) <= 1 && !isDoorCell(w, cx, cy)) {
            w[cx][cy] = Tileset.NOTHING;
            if (cx > 0        && isCorridor(w[cx-1][cy])) { cx = cx - 1; continue; }
            if (cx+1 < WIDTH  && isCorridor(w[cx+1][cy])) { cx = cx + 1; continue; }
            if (cy > 0        && isCorridor(w[cx][cy-1])) { cy = cy - 1; continue; }
            if (cy+1 < HEIGHT && isCorridor(w[cx][cy+1])) { cy = cy + 1; continue; }
            break;
        }
    }

    private static void resolveDeadEnd(TETile[][] w, int x, int y) {
        int[] f = forwardDirOfDeadEnd(w, x, y);
        int fx = f[0], fy = f[1];
        if (fx == 0 && fy == 0) return;
        int sx = x + fx, sy = y + fy;
        if (!inBounds(sx, sy)) {
            backtrimStub(w, x, y);
            return;
        }
        if (isCorridor(w[sx][sy])) return;
        final int MAX_STEPS = Math.max(WIDTH, HEIGHT);
        int cx = sx, cy = sy, steps = 0;
        while (inBounds(cx, cy) && steps < MAX_STEPS &&
                (w[cx][cy] == Tileset.NOTHING || w[cx][cy] == WALL)) {
            cx += fx; cy += fy; steps++;
        }
        if (!inBounds(cx, cy)) {
            backtrimStub(w, x, y);
            return;
        }

        if (isRoomInterior(w[cx][cy])) {
            int kx = sx, ky = sy;
            while (kx != cx || ky != cy) {
                if (w[kx][ky] == Tileset.NOTHING || w[kx][ky] == WALL) w[kx][ky] = FLOOR;
                kx += fx; ky += fy;
            }
            int bx = cx - fx, by = cy - fy;
            if (inBounds(bx, by) && w[bx][by] == WALL) w[bx][by] = FLOOR;
        } else {
            backtrimStub(w, x, y);
        }
    }

    private static void resolveAllDeadEnds(TETile[][] w) {
        for (int y = 1; y < HEIGHT - 1; y++) {
            for (int x = 1; x < WIDTH - 1; x++) {
                if (isCorridor(w[x][y]) && corrDeg(w, x, y) == 1) {
                    resolveDeadEnd(w, x, y);
                }
            }
        }
    }
    // @source
    // ChatGPT was used occasionally to suggest small refinements to existing
    // code, help debug corridor extension, wall thinning, and landscape mode randomization.

}
