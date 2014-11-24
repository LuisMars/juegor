package es.upv.luimafus;

import com.badlogic.gdx.graphics.Color;

public class Player implements Comparable<Player>{
    public static int UP = 0;
    public static int RIGHT = 1;
    public static int DOWN = 2;
    public static int LEFT = 3;

    public static int AUP = 4;
    public static int ARIGHT = 5;
    public static int ADOWN = 6;
    public static int ALEFT = 7;

    public static int AREA = 8;

    private int x;
    private int y;

    private int lastAtt = 0; //turns

    private int action;

    private int HP = 10;
    private int cHP = HP;

    private Area area;

    private AStar aStar;
    private boolean bot;

    private int ID;

    private static int n_players = 0;

    private Map GameMap;
    public Player(Map gameMap, boolean isBot) {
        GameMap = gameMap;
        bot = isBot;
        setStartPos();
        n_players++;
        ID = n_players - 1;
        aStar = new AStar(GameMap);
        area = new Area(GameMap);
    }

    private void setStartPos() {
        while (true) {
            x = (int) (Math.random() * GameMap.getWidth());
            y = (int) (Math.random() * GameMap.getHeight());
            if (GameMap.getCell(x, y) == 0) {

                int dist = 99999;
                for (Player p : GameMap.getPlayers())
                    dist = Math.min(dist,Utils.distance(this, p));
                if(dist > 5)
                    break;
            }
        }
    }

    public void setAction(int a) {
        action = a;
    }

    public void act() {
        switch (action) {
            case 0:
            case 1:
            case 2:
            case 3: {
                move(action);
                break;
            }
            case 4:
            case 5:
            case 6:
            case 7: {
                attack(action-4);
                break;
            }
            case 8: {
                attack(-1);
                break;
            }
        }
        action = -1;
        lastAtt--;
    }

    public void move(int m) {
        int pX = x;
        int pY = y;

        switch (m) {
            case 0: {
                pY--;
                break;
            }
            case 2: {
                pY++;
                break;
            }
            case 1: {
                pX++;
                break;
            }
            case 3: {
                pX--;
                break;
            }
        }
        GameMap.movePlayer(this, pX, pY);
    }

    public void attack(int direction) {
        if(lastAtt <= 0) {
            if (direction == -1) {
                if (area.isOver()) {
                    area = new Area(GameMap, x, y, ID);
                    cHP--;
                    lastAtt = 4;
                }
            }
            else {
                GameMap.addAttack(new Attack(x, y, direction, ID));
                lastAtt = 2;
            }
        }

    }

    public void botMove(Player b) {
        if(b != null) {
            lastAtt--;
            int dir = aStar.getNext(this, b);
            if (Utils.hasDirectPath(GameMap,this,b) && Utils.dirToAttack(this,b) >= 0 && Utils.distance(this, b) <= 10)
                attack(Utils.dirToAttack(this,b));
            else if (cHP > 5 && Utils.trueDistance(this, b) <= 3)
                attack(-1);
            else
                move(dir);
        }
    }

    public void botMoveOld(Player p) {
        lastAtt--;

        int dir = botMove(getX(), getY(), p);
        if (p.getX() != getX() && p.getY() != getY()) {
            if (cHP > 5 && lastAtt <= 0 && Utils.trueDistance(p,this) <= 3)
                    attack(-1);
            else
                moveTo(getX() + Utils.dirToSumX(dir), getY() + Utils.dirToSumY(dir));
        }
        else if(Utils.trueDistance(p,this) <= 10)
            attack(dir);
        else
            moveTo(getX() + Utils.dirToSumX(dir), getY() + Utils.dirToSumY(dir));

    }
    private int botMove(int pX, int pY, Player p) {
        int[] best = new int[4];
        for(int i = 0; i < best.length; i++)
            best[i] = Utils.distance(getX() + Utils.dirToSumX(i), getY() + Utils.dirToSumY(i), p.getX(), p.getY());
        int min = 99999999;
        int dir = -1;
        for(int i = 0; i < best.length; i++)
            if(best[i] < min && GameMap.canMove(getX() + Utils.dirToSumX(i), getY() + Utils.dirToSumY(i))) {
                min = best[i];
                dir = i;
            }

        if(min <= 2)
            return -1;
        else
            return dir;
    }

    public void moveTo(int x, int y) {
        if(GameMap.canMove(x, y)) {
            this.x = x;
            this.y = y;
        }
    }

    public boolean isBot() {
        return bot;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getID() {
        return ID;
    }

    public void updateArea() {
        area.updatePos();
    }

    public double getHP() {
        return cHP*1.0/HP;
    }

    public boolean isDead() {
        return cHP == 0;
    }

    public void hit(int damage) {
        cHP = Math.max(0, cHP-damage);
    }

    public static void reset() {
        n_players = 0;
    }

    public Color getHPColor() {
        switch ((int)(getHP()*10)) {
            case 10:
                return Color.valueOf("345d1e");
            case 9:
                return Color.valueOf("647f2c");
            case 8:
                return Color.valueOf("979f3d");
            case 7:
                return Color.valueOf("d8c266");
            case 6:
                return Color.valueOf("bf7b3f");
            case 5:
                return Color.valueOf("9b6141");
            case 4:
                return Color.valueOf("904b36");
            case 3:
                return Color.valueOf("904b36");
            case 2:
            case 1:
            default:
                return Color.valueOf("7a3333");
        }
    }


    @Override
    public int compareTo(Player o) {
        return getY()>o.getY()?1:getY()==o.getY()?0:-1;
    }
}
