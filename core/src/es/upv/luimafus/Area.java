package es.upv.luimafus;


public class Area {
    private int px;
    private int py;
    private int time;
    private int radius = 3;
    private int father;

    public Area () {
        time = -1;
    }

    public Area (int x, int y, int ID) {
        px = x;
        py = y;
        time = radius;
        father = ID;
    }

    public void updatePos() {
        if(--time >= 0) {
            for (int i = 0; i < Map.getHeight(); i++) {
                for (int j = 0; j < Map.getWidth(); j++) {
                    if (Utils.trueDistance(j, i, px, py) == radius - time)
                        Map.addAttack(new Attack(j, i, father, radius-time, radius));
                }
            }
        }
    }

    public boolean isOver() {
        return time <= 0;
    }


}
