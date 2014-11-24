package es.upv.luimafus;

import java.util.*;


public class Map {
    private int[][] map;
    public int[][] drawMap;

    private List<Player> players = new ArrayList<>();
    private Collection<Attack> attacks = new ArrayList<>();
    public Player humanPlayer;
    private GameScreen gs;
    public Map(GameScreen s,int h, int w, double density) {
        gs = s;
        map = new int[h][w];
        generateMap(density);
        prepareMap();

    }

    public void generateMap(double density) {
        for (int i = 0; i < getHeight(); i++) {
            for (int j = 0; j < getWidth(); j++) {
                map[j][i] = 1;
            }
        }
        double area = getHeight() * getWidth() * density;
        area = Math.min(area, (getHeight() * getWidth()) - (getHeight() + getWidth()) * 2 + 4);
        double covered = 0;

        while (area > covered) {

            int iy = (int)(Math.random() * getWidth());
            int fy = 0;
            while (fy < iy)
                fy = (int)(Math.random() * getWidth());

            int ix = (int)(Math.random() * getHeight());
            int fx = 0;
            while (fx < ix)
                fx = (int)(Math.random() * getHeight());
            //TODO: i < f

            boolean overlaps = false;

            for (int i = ix; i < fx; i++) {
                for (int j = iy; j < fy; j++) {
                    if(map[j][i] == 0)
                        overlaps = true;
                }
            }

            if(overlaps)
                continue;
        //TODO: see join loops?
            for (int i = ix; i < fx; i++) {
                for (int j = iy; j < fy; j++) {
                    map[j][i] = 0;
                }
            }
            covered = 0;
            for (int i = 0; i < getHeight(); i++) {
                for (int j = 0; j < getWidth(); j++) {
                    if(map[j][i] == 0)
                        covered++;
                }
            }
        }
    }


    public void prepareMap() {
        drawMap = new int[getWidth()][getHeight()];

        for(int i = 0; i < getWidth(); i++)
            for(int j = 0; j < getHeight(); j++)
                drawMap[i][j] = gs.assets.floor.getIndex(Utils.getTile(map, i, j));


    }

    public void addPlayer(Player p) {
        players.add(p);
        humanPlayer = players.iterator().next();
    }

    public void addAttack(Attack a) {
        attacks.add(a);
    }

    public void updateState() {
        String res = "";
        String cell;
        Collections.sort(players);
        for (Player p : players) {
            p.updateArea();
            if(p.isBot())
                p.botMove(closestTo(p));
            else
                p.act();
        }

        for(Player p : players)
            res += p.getHP() + "\n";
        players.removeIf(Player::isDead);
        attacks.removeIf(Attack::isOver);
        //return res;
    }
    public void updateAttacks() {
        for (Attack a: attacks) {
            a.updatePos();
        }
        for (int i = 0; i < getHeight(); i++) {
            for (int j = 0; j < getWidth(); j++) {
                for (Attack a : attacks) {
                    if (!a.isOver() && a.getX() == j && a.getY() == i) {
                        if (map[j][i] == 1 && a.getDirection() != -1) {
                            a.kill();
                        }
                        for (Player p : players) {
                            if (p.getX() == a.getX() && p.getY() == a.getY() && p.getID() != a.getFather()) {
                                a.kill();
                                p.hit(a.getDamage());
                            }
                        }
                    }
                }
            }
        }
        players.removeIf(Player::isDead);
        attacks.removeIf(Attack::isOver);
    }

    public int getWidth() {
        return map.length;
    }

    public int getHeight() {
        return map[0].length;
    }

    public void movePlayer(Player p, int x, int y) {
        if(canMove(x, y))
            p.moveTo(x,y);
    }

    public boolean canMove(int x, int y) {
        for(Player p : players) {
            if(p.getX() == x && p.getY() == y)
                return false;
        }
        return !(x < 0 || y < 0 || x >= map.length || y >= map[0].length) && map[x][y] == 0;
    }

    public void act(int a) {
        for(Player p: players) {
            if (!p.isBot()) {
                p.setAction(a);
            }
        }
    }

    public Player nextPlayer() {
        return nextTo(humanPlayer);
    }
    private Player nextTo(Player p) {
        return players.get((players.indexOf(p)+1)%players.size());
    }

    public int getCell(int x, int y) {
        return map[x][y];
    }

    public Player closestTo(Player p) {
        Player g = null;
        int min = 99999;
        if(players.size() == 1)
            return null;
        for(Player e: players) {
            if(e != p) {
                int dist = Utils.trueDistance(e,p);
                if(dist < min) {
                    min = dist;
                    g = e;
                }
            }
        }
        return g;
    }

    public boolean haveAWinner() {
        return players.size() <= 1;
    }
    public String winner() {
        if(haveAWinner() && players.size() == 1) {
            String ID = "" + players.get(0).getID();
            players.clear();
            Player.reset();
            return ID;
        }
        else
            return "none";
    }

    public List<Player> getPlayers() {
        return players;
    }

    public Collection<Attack> getAttacks() {
        return attacks;
    }

    public boolean isFloor(int x, int y) {
        return map[x][y] == 0;
    }
}
