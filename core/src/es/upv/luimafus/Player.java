package es.upv.luimafus;

public class Player {
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

    private Area area = new Area();

    private AStar aStar = new AStar();
    private boolean bot;

    private int ID;

    private static int n_players = 0;

    public Player(boolean b) {
        bot = b;
        setStartPos();
        n_players++;
        ID = n_players - 1;
    }

    private void setStartPos() {
        while (true) {
            x = (int) (Math.random() * Map.getWidth());
            y = (int) (Math.random() * Map.getHeight());
            if (Map.getCell(x, y) == 0) {

                int dist = 99999;
                for (Player p : Map.getPlayers())
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
        Map.movePlayer(this, pX, pY);
    }

    public void attack(int direction) {
        if(lastAtt <= 0) {
            if (direction == -1) {
                if (area.isOver()) {
                    area = new Area(x, y, ID);
                    cHP--;
                }
            }
            else
                Map.addAttack(new Attack(x, y, direction, ID));
            lastAtt = 4;
        }

    }

    public void botMove(Player b) {
        if(b != null) {
            lastAtt--;
            int dir = aStar.getNext(this, b);
            if (Utils.dirToAttack(b, this) >= 0 && Utils.distance(this, b) <= 10)
                attack(Utils.dirToAttack(b, this));
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
            if(best[i] < min && Map.canMove(getX() + Utils.dirToSumX(i), getY() + Utils.dirToSumY(i))) {
                min = best[i];
                dir = i;
            }

        if(min <= 2)
            return -1;
        else
            return dir;
    }

    public void moveTo(int x, int y) {
        if(Map.canMove(x, y)) {
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
}
