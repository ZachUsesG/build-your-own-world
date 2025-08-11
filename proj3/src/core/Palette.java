package core;

import tileengine.TETile;
import java.awt.Color;

    public final class Palette {
        private Palette() {}
        public static TETile tile(char c, Color fg, Color bg, String name) {
            return new TETile(c, fg, bg, name, 1);
        }
        public static final TETile CORR_H  = tile('─', gray(160), gray(12), "floor-corr-h");
        public static final TETile CORR_V  = tile('│', gray(160), gray(12), "floor-corr-v");
        public static final TETile CORR_NE = tile('└', gray(160), gray(12), "floor-corr-ne");
        public static final TETile CORR_NW = tile('┘', gray(160), gray(12), "floor-corr-nw");
        public static final TETile CORR_SE = tile('┌', gray(160), gray(12), "floor-corr-se");
        public static final TETile CORR_SW = tile('┐', gray(160), gray(12), "floor-corr-sw");
        public static final TETile CORR_TN = tile('┴', gray(180), gray(12), "floor-corr-t-n");
        public static final TETile CORR_TE = tile('├', gray(180), gray(12), "floor-corr-t-e");
        public static final TETile CORR_TS = tile('┬', gray(180), gray(12), "floor-corr-t-s");
        public static final TETile CORR_TW = tile('┤', gray(180), gray(12), "floor-corr-t-w");
        public static final TETile CORR_X  = tile('┼', gray(200), gray(12), "floor-corr-x");
        public static final TETile CORR_END= tile('•', gray(160), gray(12), "floor-corr-end");

        public static final TETile AVATAR  = tile('✹', new Color(255,215,0), Color.BLACK, "you");

        private static Color gray(int v){ return new Color(v,v,v); }
    }

