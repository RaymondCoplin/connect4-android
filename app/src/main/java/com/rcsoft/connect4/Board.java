package com.rcsoft.connect4;

public class Board {

    private Cell[][] cells;
    private Player currentPlayer;

    private Board(int rows, int cols, Player currentPlayer) {
        this.cells = new Cell[rows][cols];
        this.currentPlayer = currentPlayer;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public void fillCell(Cell cell){
        cells[cell.getRow()][cell.getCol()] = cell;
    }

    public boolean verifyBoard(int row, int col) {

        int count = 0;

        // Horizontal Check
        for (int j = 0; j < 4 ; j++ ){
            for (int i = 1; i < 7; i++){
                if(this.cells[i][j] == null || this.cells[i][j].isFree()){
                    continue;
                }
                if (this.cells[i][j].getPlayer().getId() == currentPlayer.getId() &&
                        this.cells[i][j+1] != null && this.cells[i][j+1].getPlayer().getId() == currentPlayer.getId() &&
                        this.cells[i][j+2] != null && this.cells[i][j+2].getPlayer().getId() == currentPlayer.getId() &&
                        this.cells[i][j+3] != null && this.cells[i][j+3].getPlayer().getId() == currentPlayer.getId()){
                    return true;
                }
            }
        }

        // Vertical Check
        for (int i = 1; i < 4; i++ ){
            for (int j = 0; j< 7; j++){
                if(this.cells[i][j] == null || this.cells[i][j].isFree()){
                    continue;
                }
                if (this.cells[i][j].getPlayer().getId() == currentPlayer.getId() &&
                        this.cells[i+1][j] != null && this.cells[i+1][j].getPlayer().getId() == currentPlayer.getId() &&
                        this.cells[i+2][j] != null && this.cells[i+2][j].getPlayer().getId() == currentPlayer.getId() &&
                        this.cells[i+3][j] != null && this.cells[i+3][j].getPlayer().getId() == currentPlayer.getId()){
                    return true;
                }
            }
        }

        // Ascending Diagonal Check
        for (int i=3; i<7; i++){
            for (int j=0; j<4; j++){
                if(this.cells[i][j] == null || this.cells[i][j].isFree()){
                    continue;
                }
                if (this.cells[i][j].getPlayer().getId() == currentPlayer.getId() &&
                        this.cells[i-1][j+1] != null && this.cells[i-1][j+1].getPlayer().getId() == currentPlayer.getId() &&
                        this.cells[i-2][j+2] != null && this.cells[i-2][j+2].getPlayer().getId() == currentPlayer.getId() &&
                        this.cells[i-3][j+3] != null && this.cells[i-3][j+3].getPlayer().getId() == currentPlayer.getId())
                    return true;
            }
        }
        // descendingDiagonalCheck
        for (int i=3; i<7; i++){
            for (int j=3; j<7; j++){
                if(this.cells[i][j] == null || this.cells[i][j].isFree()){
                    continue;
                }
                if (this.cells[i][j].getPlayer().getId() == currentPlayer.getId() &&
                        this.cells[i-1][j-1] != null && this.cells[i-1][j-1].getPlayer().getId() == currentPlayer.getId() &&
                        this.cells[i-2][j-2] != null && this.cells[i-2][j-2].getPlayer().getId() == currentPlayer.getId() &&
                        this.cells[i-3][j-3] != null && this.cells[i-3][j-3].getPlayer().getId() == currentPlayer.getId())
                    return true;
            }
        }

        return false;
    }

    public void newGame(int rows, int cols){
        cells = new Cell[rows][cols];
    }

    public static class Builder {
        private int rows;
        private int cols;
        private Player currentPlayer;

        public Builder setRows(int rows){
            this.rows = rows;
            return this;
        }

        public Builder setCols(int cols){
            this.cols = cols;
            return this;
        }

        public Builder setCurrentPlayer(Player player){
            this.currentPlayer = player;
            return this;
        }

        public Board build() {
            return new Board(rows, cols, currentPlayer);
        }
    }
}
