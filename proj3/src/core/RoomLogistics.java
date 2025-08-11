package core;


public class RoomLogistics {
    private int x;
    private int y;
    private int width;
    private int height;
    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }


    public boolean contains(int px, int py) {
        return px >= x && px < x + width && py >= y && py < y + height;
    }
    public void moveBy(int dx, int dy) {
        this.x += dx;
        this.y += dy;
    }
    public int centerX() {
        int cx = x + width / 2;
        if (cx == x - 1 || cx == x + width) {
            cx += 1;
        }
        return cx;
    }

    public int centerY() {
        int cy = y + height / 2;
        if (cy == y - 1 || cy == y + height) {
            cy += 1;
        }
        return cy;
    }

    public RoomLogistics(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
    public boolean overlaps(RoomLogistics otherRoom) {
        return this.x - 1 + this.width + 2 > otherRoom.x - 1 &&
                otherRoom.x - 1 + otherRoom.width + 2 > this.x - 1 &&
                this.y - 1 + this.height + 2 > otherRoom.y - 1 &&
                otherRoom.y - 1 + otherRoom.height + 2 > this.y - 1;
    }

    public static void center() {}

    private Biome biome;
    public void setBiomeType(Biome b){ this.biome = b; }
    public Biome getBiomeType(){ return biome; }
    public String getBiomeName(){ return biome == null ? "" : biome.toString(); }
}
