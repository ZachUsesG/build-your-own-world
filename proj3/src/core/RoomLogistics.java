package core;


public class RoomLogistics {
    private int x;
    private int y;
    private int width;
    private int height;
    public int centerX() {
        return x + width / 2;
    }

    public int centerY() {
        return y + height / 2;
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
}
