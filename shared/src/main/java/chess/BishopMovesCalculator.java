package chess;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BishopMovesCalculator {
    public static Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        //4 directions (diagonals)
        //loop to go through every square on every direction
        ArrayList<ChessMove> moves = new ArrayList<ChessMove>();
        int i = position.getRow();
        int j = position.getColumn();
        /*int tempi = i;
        int tempj = j;
        //-1 -1
        if (i != 1 || j != 1) {
            while (tempi > 1 && tempj > 1) {
                tempi--;
                tempj--;
                moves.add(new ChessMove(new ChessPosition(i, j), new ChessPosition(tempi, tempj), null));
            }
        }

        tempi = i;
        tempj = j;
        //-1 1
        if (i != 1 || j != 8) {
            while (tempi > 1 && tempj < 8) {
                tempi--;
                tempj++;
                moves.add(new ChessMove(new ChessPosition(i, j), new ChessPosition(tempi, tempj), null));
            }
        }

        tempi = i;
        tempj = j;
        //1 -1
        if (i != 8 || j != 1) {
            while (tempi < 8 && tempj > 1) {
                tempi++;
                tempj--;
                moves.add(new ChessMove(new ChessPosition(i, j), new ChessPosition(tempi, tempj), null));
            }
        }

        tempi = i;
        tempj = j;
        //1 1
        if (i != 8 || j != 8) {
            while (tempi < 8 && tempj < 8) {
                tempi++;
                tempj++;
                moves.add(new ChessMove(new ChessPosition(i, j), new ChessPosition(tempi, tempj), null));
            }
        }*/

        moves = addMoves(i, j, -1, -1, moves);
        moves = addMoves(i, j, -1, 1, moves);
        moves = addMoves(i, j, 1, -1, moves);
        moves = addMoves(i, j, 1, 1, moves);


        //stop if below 0 or above 7
        //add all moves that aren't to itself to the list
        return moves;
    }

    public static ArrayList<ChessMove> addMoves(int r, int c, int dr, int dc, ArrayList<ChessMove> m) {
        int startRow = r;
        int startCol = c;
        while(r > 1 && r < 8 && c > 1 && c < 8) {
            r = r + dr;
            c = c + dc;
            m.add(new ChessMove(new ChessPosition(startRow, startCol), new ChessPosition(r, c), null));
        }
        return m;
    }
}
