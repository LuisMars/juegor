package es.upv.luimafus;

public class Attack {
    public int time;
    protected int x;
    protected int y;
    protected int direction;
    protected int damage;
    protected int father;
    protected int radius;
    protected int frame;
    private int maxTime;
    private boolean netAttack = false;
    //(dir, x, y, frame);

    public Attack(int x, int y, int ID, int r, int tr, boolean fake) {
        this.x = x;
        this.y = y;
        this.father = ID;
        direction = -1;
        maxTime = tr;
        time = maxTime;
        if (fake)
            damage = 0;
        else
            damage = 2;
        radius = r;
    }

    public Attack(int x, int y, int dir, int ID, boolean fake) {

        this.x = x;
        this.y = y;
        this.father = ID;
        direction = dir;
        maxTime = 10;
        time = maxTime;
        if (fake)
            damage = 0;
        else
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

    public int getID() {
        return direction;
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

    public int getTime() {
        if (netAttack && direction == -1)
            return frame;
        else
            return time;
    }
}
