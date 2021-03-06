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
    public int HP = 10;
    boolean playStep = false;
    private int x, lastX;
    private int y, lastY;
    private int lastAtt = 0; //turns
    private int action = -1;
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
    public Player(Map gameMap, int id, String n, int x, int y, int chp) {
        this(gameMap);
        ID = id;
        name = n;
        cHP = chp;
        this.x = x;
        this.y = y;
        netPlayer = true;
    }

    //server player
    public Player(Map gameMap, String n) {
        this(gameMap);
        serverPlayer = true;
        name = n;
        setStartPos(true);
    }

    //single player
    public Player(Map gameMap, boolean isBot) {
        this(gameMap);
        preferences = Gdx.app.getPreferences("sp");
        bot = isBot;
        if(bot)
            name = "Bot #" + ID;
        else
            name = preferences.getString("name", "Player");

        setStartPos(false);
    }

    public static void reset() {
        n_players = 0;
    }

    public void restart() {
        setStartPos(true);
        cHP = HP;
    }

    private void setStartPos(boolean fast) {

        while (true) {
            boolean hasPath = true;
            x = (int) (Math.random() * GameMap.getWidth());
            y = (int) (Math.random() * GameMap.getHeight());

            if (GameMap.getCell(x, y) == 0) {

                int dist = 99999;
                if (fast) {
                    for (Player p : GameMap.getPlayers()) {
                        if (p != this) {
                            hasPath = hasPath && aStar.hasPath(this, p);
                            dist = Math.min(dist, Utils.distance(this, p));
                        }
                    }
                }
                if(hasPath && dist > 5)
                    break;
            }
        }
    }

    public void act() {
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
                attack(lastDir, false);
                break;
            case 8:
                attack(-1, false);
                break;
        }
        //action = -1;
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
        boolean canMove = moveTo(pX, pY);
        if (action != -1)
            playSteps(canMove);
    }

    private void playSteps(boolean canMove) {
        if (!serverPlayer && canMove) {

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


    public void attack(int direction, boolean fake) {
        //positional sound!!
        float dist = 0;
        float pan = 0;
        if (direction == -2)
            return;
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
                        area = new Area(GameMap, x, y, ID, fake);
                        if (!fake)
                            cHP--;
                        lastAtt = 4;
                        //if(dist < 15)
                        if (!serverPlayer) {
                            AssetManager.areaSound.play(30 / (dist + 15), 1, pan);
                        }
                    }
                } else {
                    GameMap.addAttack(new Attack(x, y, direction, ID, fake));
                    lastAtt = 3;
                        //if(dist < 15)
                    if (!serverPlayer) {
                        AssetManager.arrowSound[MathUtils.random(0, 1)].play(15 / (dist + 15), 1, pan);
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
                    attack(lastDir, false);
                }
                else
                    move(dir);
            }
            else if (cHP > 5 && Utils.trueDistance(this, b) <= 3) {
                if(MathUtils.randomBoolean(difficulty/2))
                    attack(-1, false);
                else
                    move(dir);
            }
            else
                move(dir);
        }
    }

    public void updateState(int x, int y, int lastDir, int cHP, int att) {
        //System.out.println(x + " " + y + " " + cHP);
        if (!isDead()) {
            if (this.x != x && this.y != y)
                playSteps(true);
            this.x = x;
            this.y = y;
            this.lastDir = lastDir;
            this.cHP = cHP;
            attack(att, true);
        }
    }

    public boolean moveTo(int x, int y) {
        if(GameMap.canMove(x, y)) {
            if (this.x != x && this.y != y)
                playSteps(true);
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
        if (!isDead())
            action = a;
        return this;
    }

    public int getAttack() {
        if (action == 8)
            return -1;
        else if (action >= 4) {
            return action - 4;
        } else
            return -2;
    }

    public boolean isDead() {
        return cHP <= 0;
    }

    public void hit(int damage) {
        cHP = Math.max(0, cHP-damage);
    }

    public Color getHPColor() {
        switch ((int)(getHP()*10)) {
            case 10:
            case 9:
                return Color.valueOf("6abe30");
            case 8:
            case 7:
                return Color.valueOf("99e550");
            case 6:
            case 5:
                return Color.valueOf("fbf236");
            case 4:
            case 3:
                return Color.valueOf("df7126");
            case 2:
            case 1:
            default:
                return Color.valueOf("ac3232");
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
