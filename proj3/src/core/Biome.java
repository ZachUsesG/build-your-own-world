package core;

import tileengine.TETile;
import java.awt.Color;
import static core.Palette.tile;

public enum Biome {
    SUNFLOWER(':', new Color(210,190,40), new Color(18,28,18),
            new TETile[]{
                    tile('✿', new Color(240,210,60),  new Color(18,28,18), "sunflower A"),
                    tile('❁', new Color(255,225,80),  new Color(18,28,18), "sunflower B"),
                    tile('✾', new Color(250,230,90),  new Color(18,28,18), "sunflower C"),
            }, 0.14, 0.20),

    FLOWER('.', new Color(220,110,160), new Color(20,26,20),
            new TETile[]{
                    tile('❀', new Color(255,160,200), new Color(20,26,20), "flower A"),
                    tile('✿', new Color(240,150,190), new Color(20,26,20), "flower B"),
                    tile('❁', new Color(255,180,210), new Color(20,26,20), "flower C"),
            }, 0.16, 0.15),

    FOREST(',', new Color(95,160,95), new Color(15,20,15),
            new TETile[]{
                    tile('☘', new Color(140,200,120), new Color(15,20,15), "forest A"),
                    tile('❦', new Color(120,185,110), new Color(15,20,15), "forest B"),
                    tile('’',  new Color(160,205,120), new Color(15,20,15), "forest sprout"),
            }, 0.12, 0.25),

    DESERT('`', new Color(220,190,120), new Color(30,25,10),
            new TETile[]{
                    tile('·', new Color(235,205,140), new Color(30,25,10), "grain"),
                    tile('•', new Color(225,195,120), new Color(30,25,10), "pebble"),
                    tile('░', new Color(240,210,150), new Color(30,25,10), "shade"),
            }, 0.10, 0.18),

    TUNDRA('·', new Color(210,230,240), new Color(10,15,20),
            new TETile[]{
                    tile('❄', new Color(225,240,255), new Color(10,15,20), "ice"),
                    tile('⋄', new Color(210,230,245), new Color(10,15,20), "crystal"),
                    tile('o',  new Color(170,175,185), new Color(10,15,20), "rock"),
            }, 0.12, 0.30),

    CAVERN('^', new Color(170,170,190), new Color(10,10,15),
            new TETile[]{
                    tile('▲', new Color(180,180,195), new Color(10,10,15), "peak"),
                    tile('◆', new Color(165,165,185), new Color(10,10,15), "gem"),
                    tile('◇', new Color(185,185,205), new Color(10,10,15), "gem2"),
            }, 0.10, 0.22);

    public final TETile base;
    public final TETile[] accents;
    public final double p, cluster;

    Biome(char baseGlyph, Color fg, Color bg, TETile[] accents, double p, double cluster) {
        this.base = tile(baseGlyph, fg, bg, "floor-" + name().toLowerCase());
        this.accents = accents; this.p = p; this.cluster = cluster;
    }

    public static Biome random(java.util.Random r){
        Biome[] v = values();
        return v[r.nextInt(v.length)];
    }
}