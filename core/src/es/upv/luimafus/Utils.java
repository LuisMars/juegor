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
    public static float fDistance(int x, int y, int px, int py) {
        return (float)Math.sqrt((x-px)*(x-px)+(y-py)*(y-py));
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
            if (y > py)
                return 0;
            else if (y < py)
                return 2;
        }
        else if(y == py) {
            if(x < px)
                return 1;
            else if (x > px)
                return 3;
        }
        return -1;
    }

    public static boolean hasNeighbours(Map GameMap,int x, int y) {
        try {
            return GameMap.getCell(x+1,y) == 1 ||
                    GameMap.getCell(x-1,y) == 1 ||
                    GameMap.getCell(x,y+1) == 1 ||
                    GameMap.getCell(x,y-1) == 1;
        }
        catch (ArrayIndexOutOfBoundsException e) {
            return true;
        }
    }

    public static String getTile(int[][] map, int x, int y) {
        String res = "";
        if(map[x][y] == 0) {

            res+= checkTile(map, x, y-1);
            res+= checkTile(map, x+1, y);
            res+= checkTile(map, x, y+1);
            res+= checkTile(map, x-1, y);
            res+= checkTile(map, x-1, y-1);
            res+= checkTile(map, x+1, y-1);
            res+= checkTile(map, x+1, y+1);
            res+= checkTile(map, x-1, y+1);
            return res;
        }
        else {
            if(checkTile(map, x, y+1) == '0')
                if(checkTile(map, x+1, y+1) == '0' && checkTile(map, x-1, y+1) == '0')
                    return "topB";
                else
                    return "topA";

            return "11111111";
        }
    }

    private static char checkTile(int[][] map, int x, int y) {
        try {
            return map[x][y] == 0 ? '0': '1';
        } catch (ArrayIndexOutOfBoundsException e) {
            return '1';
        }
    }

    public static int dirToAttack(Player a, Player b) {
        if(a.getX() == b.getX() || a.getY() == b.getY())
            return posToDir(a.getX(),a.getY(),b.getX(),b.getY());
        return -1;
    }

    public static boolean hasDirectPath(Map GameMap, Player a, Player b) {
        int dir = posToDir(a.getX(),a.getY(),b.getX(),b.getY());
        switch (dir) {
            case 0:
                for(int y = a.getY(); y >= b.getY(); y--)
                    if(!GameMap.isFloor(a.getX(),y))
                        return false;
            case 2:
                for(int y = a.getY(); y <= b.getY(); y++)
                    if(!GameMap.isFloor(a.getX(),y))
                        return false;
            case 3:
                for(int x = a.getX(); x >= b.getX(); x--)
                    if(!GameMap.isFloor(x,b.getY()))
                        return false;
            case 1:
                for(int x = a.getX(); x <= b.getX(); x++)
                    if(!GameMap.isFloor(x,b.getY()))
                        return false;
        }
        return true;



    }
}
