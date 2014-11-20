package es.upv.luimafus;

/**
 * Created by Luis on 10/11/2014.
 */
public class Utils {
    public static int trueDistance(int x, int y, int px, int py) {
        return (int)Math.round(Math.sqrt((x-px)*(x-px)+(y-py)*(y-py)));
    }
    public static int distance(int x, int y, int px, int py) {
        return Math.abs(x-px)+Math.abs(y-py);
    }
    public static int distance(Player a, Player b) {
        return distance(a.getX(),a.getY(),b.getX(),b.getY());
    }
    public static int distance(int x, int y, Player a) {
        return trueDistance(x,y,a.getX(),a.getY());
    }
    public static int trueDistance(Player a, Player b) {
        return distance(a.getX(),a.getY(),b.getX(),b.getY());
    }
    public static boolean canReach(Player a, Player b) {
        return false;
    }

    public static int dirToSumX (int pos) {
        if(pos == 3)
            return -1;
        else if(pos == 1)
            return 1;
        else return 0;
    }
    public static int dirToSumY (int pos) {
        if(pos == 0)
            return -1;
        else if (pos == 2)
            return 1;
        else
            return 0;
    }

    public static int posToDir(int x, int y, int px, int py) {
        if(x == px) {
            if (y < py)
                return 0;
            else if (y > py)
                return 2;
        }
        else if(y == py) {
            if(x > px)
                return 1;
            else if (x < px)
                return 3;
        }
        return -1;
    }

    public static boolean hasNeighbours(int x, int y) {
        try {
            return Map.getCell(x+1,y) == 1 ||
                    Map.getCell(x-1,y) == 1 ||
                    Map.getCell(x,y+1) == 1 ||
                    Map.getCell(x,y-1) == 1;
        }
        catch (ArrayIndexOutOfBoundsException e) {
            return true;
        }
    }

    public static int dirToAttack(Player a, Player b) {
        if(a.getX() == b.getX() || a.getY() == b.getY())
            return posToDir(a.getX(),a.getY(),b.getX(),b.getY());
        return -1;
    }
}
