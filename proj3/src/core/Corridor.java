package core;
import tileengine.TETile;
import static core.Palette.*;

public final class Corridor {
    private Corridor(){}

    public static TETile tile(boolean n, boolean e, boolean s, boolean w){
        int deg = (n?1:0)+(e?1:0)+(s?1:0)+(w?1:0);
        if (deg == 0) return CORR_END;
        if (deg == 1) return (n||s)? CORR_V : CORR_H;
        if (deg == 2) {
            if (n && s) return CORR_V;
            if (e && w) return CORR_H;
            if (n && e) return CORR_NE;
            if (e && s) return CORR_SE;
            if (s && w) return CORR_SW;
            return CORR_NW;
        }
        if (deg == 3) {
            if (!n) return CORR_TS;
            if (!e) return CORR_TW;
            if (!s) return CORR_TN;
            return CORR_TE;
        }
        return CORR_X;
    }
}