package es.upv.luimafus;


public class Area {
    private int px;
    private int py;
    private int time = -1;
    private int radius = 3;
    private int father;
    private boolean fake;
    private Map GameMap;

    public Area (Map GameMap) {
        this.GameMap = GameMap;
        time = -1;
    }

    public Area(Map GameMap, int x, int y, int ID, boolean fake) {
        this.GameMap = GameMap;
        px = x;
        py = y;
        time = radius;
        father = ID;
        this.fake = fake;
    }

    public void updatePos() {
        if(--time >= 0) {
            for (int i = 0; i < GameMap.getHeight(); i++) {
                for (int j = 0; j < GameMap.getWidth(); j++) {
                    if (Utils.trueDistance(j, i, px, py) == radius - time)
                        GameMap.addAttack(new Attack(j, i, father, radius - time, radius, fake));
                }
            }
        }
    }

    public boolean isOver() {
        return time <= 0;
    }


}
