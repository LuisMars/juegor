package es.upv.luimafus;

public class Attack {
    protected int x;
    protected int y;
    public int time;
    private int maxTime;
    protected int direction;
    protected int damage;
    protected int father;
    protected int radius;

    public Attack(int x, int y, int ID, int r, int tr) {
        this.x = x;
        this.y = y;
        this.father = ID;
        direction = -1;
        maxTime = tr;
        time = maxTime;
        damage = 2;
        radius = r;
    }

    public Attack(int x, int y, int dir, int ID) {

        this.x = x;
        this.y = y;
        this.father = ID;
        direction = dir;
        maxTime = 10;
        time = maxTime;
        damage = 1;

    }

    public void updatePos() {
        if(--time >= 0 && direction != -1) {
            switch (direction) {
                case 0: {
                    y--;
                    break;
                }
                case 1: {
                    x++;
                    break;
                }
                case 2: {
                    y++;
                    break;
                }
                case 3: {
                    x--;
                    break;
                }
            }
        }
    }

    public char getID() {
        if(direction == -1)
            return ',';
        else if(direction%2 == 0)
            return '|';
        else
            return '-';
    }

    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }

    public void kill() {
        time = -1;
    }

    public boolean isOver() {
        return time < 0;
    }

    public int getDamage() {
        return damage;
    }

    public int getFather() {
        return father;
    }

    public int getDirection() {
        return direction;
    }

    public int getTime(int l) {
        return Math.max(0,l-1-(int)((l)*(time*1.0/maxTime)));
    }
}
