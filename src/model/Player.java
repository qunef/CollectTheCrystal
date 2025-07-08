// Lokasi: src/model/Player.java
package model;

import java.awt.Rectangle;

public class Player {
    // TAMBAHAN: Enum untuk mendefinisikan arah yang mungkin
    public enum Direction {
        LEFT,
        RIGHT
    }

    private int x, y;
    private final int speed = 4;
    private final int width = 68;
    private final int height = 90;

    private Ball heldBall = null;

    // TAMBAHAN: Variabel untuk menyimpan arah hadap, default ke kanan
    private Direction facingDirection = Direction.RIGHT;

    public Player(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void move(boolean up, boolean down, boolean left, boolean right) {
        if (up) y -= speed;
        if (down) y += speed;
        if (left) x -= speed;
        if (right) x += speed;
    }

    public Rectangle getBounds() {
        int hitboxX = x - width / 2;
        int hitboxY = y - height / 2;
        return new Rectangle(hitboxX, hitboxY, width, height);
    }

    // --- Getter dan Setter untuk Arah ---
    public Direction getFacingDirection() {
        return facingDirection;
    }

    public void setFacingDirection(Direction direction) {
        this.facingDirection = direction;
    }

    // --- Sisa Metode Lainnya ---
    public void holdBall(Ball ball) { this.heldBall = ball; }
    public void releaseBall() { this.heldBall = null; }
    public Ball getHeldBall() { return this.heldBall; }
    public int getX() { return x; }
    public int getY() { return y; }
    public void setPosition(int x, int y) { this.x = x; this.y = y; }
}