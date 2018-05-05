package com.rcsoft.connect4;

public class Cell {
    private Boolean free;
    private int row;
    private int col;
    private Player player;

    public Cell(Boolean free, int row, int col, Player player) {
        this.free = free;
        this.row = row;
        this.col = col;
        this.player = player;
    }

    public Boolean isFree() {
        return free;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }
}
