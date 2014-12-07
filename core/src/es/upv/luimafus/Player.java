package es.upv.luimafus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;

public class Player implements Comparable<Player>{
    public static int STILL = -1;
    public static int UP = 0;
    public static int RIGHT = 1;
    public static int DOWN = 2;
    public static int LEFT = 3;

    public static int AUP = 4;
    public static int ARIGHT = 5;
    public static int ADOWN = 6;
    public static int ALEFT = 7;

    public static int AREA = 8;
    public static int n_players = 0;
    public static float difficulty;
    public int lastDir = 2;
    public String name;
    boolean playStep = false;
    private int x, lastX;
    private int y, lastY;
    private int lastAtt = 0; //turns
    private int action = -1;
    private int HP = 10;
    private int cHP = HP;
    private Area area;
    private AStar aStar;
    private boolean bot;
    private boolean netPlayer;
    private boolean serverPlayer;
    private int ID;
    private Preferences preferences;
    private Map GameMap;

    public Player(Map gameMap) {
        GameMap = gameMap;
        aStar = new AStar(GameMap);
        n_players++;
        ID = n_players - 1;
        area = new Area(GameMap);
    }

    //multiplayer client
    public Player(Map gameMap, int id, String n, int x, int y) {
        this(gameMap);
        ID = id;
        name = n;
        this.x = x;
        this.y = y;
        netPlayer = true;
    }

    //server player
    public Player(Map gameMap, String n) {
        this(gameMap);
        serverPlayer = true;
        name = n;
        setStartPos();
    }

    //single player
    public Player(Map gameMap, boolean isBot) {
        this(gameMap);
        preferences = Gdx.app.getPreferences("sp");
        bot = isBot;
        if(bot)
            name = "Bot #" + ID;
        else
            name = preferences.getString("Name", "Player");

        setStartPos();
    }

    public static void reset() {
        n_players = 0;
    }

    private void setStartPos() {

        while (true) {
            boolean hasPath = true;
            x = (int) (Math.random() * GameMap.getWidth());
            y = (int) (Math.random() * GameMap.getHeight());

            if (GameMap.getCell(x, y) == 0) {
                int dist = 99999;
                for (Player p : GameMap.getPlayers()) {
                    if (p != this) {
                        hasPath = hasPath && aStar.hasPath(this, p);
                        dist = Math.min(dist, Utils.distance(this, p));
                    }
                }
                if(hasPath && dist > 5)
                    break;
            }
        }
    }

    public void act() {

        //System.out.println(x + " " + y);
        switch (action) {
            case -1:
                break;
            case 0:
            case 1:
            case 2:
            case 3:
                lastDir = action;
                move(action);
                break;
            case 4:
            case 5:
            case 6:
            case 7:
                lastDir = action - 4;
                attack(action - 4);
                break;
            case 8:
                attack(-1);
                break;
        }
        action = -1;
        lastAtt--;
    }

    public void move(int m) {
        int pX = x;
        int pY = y;
            switch (m) {
                case 0:
                    pY--;
                    break;
                case 2:
                    pY++;
                    break;
                case 1:
                    pX++;
                    break;
                case 3:
                    pX--;
                    break;
            }

        if (!serverPlayer && moveTo(pX, pY) && action != -1) {

                float dist = Utils.fDistance(GameMap.humanPlayer.getX(), GameMap.humanPlayer.getY(), this.getX(), this.getY());
                dist *= dist;
                try {
                    float pan = (this.getX() - GameMap.humanPlayer.getX()) * 4 / (this.getX() + GameMap.humanPlayer.getX());
                    if (playStep) {
                        AssetManager.stepSound[MathUtils.random(0, 3)].play(8 / (dist + 15), 1, pan);
                    }
                    playStep = !playStep;
                } catch (ArithmeticException e) {
                    playStep = !playStep;
                }
        }
    }

    public void attack(int direction) {
        //positional sound!!
        float dist = 0;
        float pan = 0;
        if (!serverPlayer) {
            dist = Utils.fDistance(GameMap.humanPlayer.getX(), GameMap.humanPlayer.getY(), this.getX(), this.getY());
            dist *= dist;
        }
        try {
            if (!serverPlayer) {
                pan = (this.getX() - GameMap.humanPlayer.getX()) * 4 / (this.getX() + GameMap.humanPlayer.getX());
            }
            if (lastAtt <= 0) {
                if (direction == -1) {
                    if (area.isOver()) {
                        area = new Area(GameMap, x, y, ID);
                        cHP--;
                        lastAtt = 4;
                        //if(dist < 15)
                        if (!serverPlayer) {
                            AssetManager.areaSound.play(30 / (dist + 15), 1, pan);
                        }
                    }
                } else {
                    GameMap.addAttack(new Attack(x, y, direction, ID));
                    lastAtt = 3;
                        //if(dist < 15)
                    if (!serverPlayer) {
                        AssetManager.arrowSound.play(15 / (dist + 15), 1, pan);
                    }
                    }
                }
        } catch (ArithmeticException e) {

        }

    }

    public void botMove(Player b) {
        if(b != null) {
            lastAtt--;
            int dir = lastDir = aStar.getNext(this, b);
            if (Utils.hasDirectPath(GameMap,this,b) && Utils.dirToAttack(this,b) >= 0 && Utils.distance(this, b) <= 10) {
                if(MathUtils.randomBoolean(difficulty)) {
                    lastDir = Utils.dirToAttack(this, b);
                    attack(lastDir);
                }
                else
                    move(dir);
            }
            else if (cHP > 5 && Utils.trueDistance(this, b) <= 3) {
                if(MathUtils.randomBoolean(difficulty/2))
                    attack(-1);
                else
                    move(dir);
            }
            else
                move(dir);
        }
    }

    public boolean moveTo(int x, int y) {
        if(GameMap.canMove(x, y)) {
            //System.out.println("position changed!!!!!");
            this.x = x;
            this.y = y;
            return true;
        }
        else
            return false;
    }

    public boolean isBot() {
        return bot;
    }

    public boolean isNetPlayer() {
        return netPlayer;
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

    public int getcHP() {
        return cHP;
    }

    public void setcHP(int chp) {
        cHP = chp;
    }

    public int getAction() {
        return action;
    }

    public Player setAction(int a) {
        action = a;
        //System.out.println(name + " " + ID + " " + action + " " + cHP);
        return this;
    }

    public boolean isDead() {
        return cHP == 0;
    }

    public void hit(int damage) {
        cHP = Math.max(0, cHP-damage);
    }

    public Color getHPColor() {
        switch ((int)(getHP()*10)) {
            case 10:
            case 9:
                return Color.valueOf("55793a");
            case 8:
            case 7:
                return Color.valueOf("77853e");
            case 6:
            case 5:
                return Color.valueOf("e5cb50");
            case 4:
            case 3:
                return Color.valueOf("c06b3c");
            case 2:
            case 1:
            default:
                return Color.valueOf("b15032");
        }
    }

    public float drawPosX(float offset) {
        /*if(action != -1)
            return (x + ((x - lastX) * offset));
        else*/
        return x;
    }
    public float drawPosY(float offset) {
        /*if(action != -1)
            return (y + ((y - lastY) * offset));
        else*/
        return y;
    }


    @Override
    public int compareTo(Player o) {
        return getY()>o.getY()?1:getY()==o.getY()?0:-1;
    }
}
