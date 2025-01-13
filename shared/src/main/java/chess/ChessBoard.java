package chess;

import java.util.Arrays;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    private ChessPiece[][] board = new ChessPiece[8][8];

    public ChessBoard() {
        
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        board[position.getRow()-1][position.getColumn()-1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return board[position.getRow()-1][position.getColumn()-1];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        board = new ChessPiece[8][8];
        for (int x = 1; x < 9; x++) {
            var whitePiece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
            var blackPiece = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
            addPiece(new ChessPosition(2, x), whitePiece);
            addPiece(new ChessPosition(7, x), blackPiece);
        }
        int y = 1;
        for (ChessGame.TeamColor color : ChessGame.TeamColor.values()) {
            var RookPiece = new ChessPiece(color, ChessPiece.PieceType.ROOK);
            var KnightPiece = new ChessPiece(color, ChessPiece.PieceType.KNIGHT);
            var BishopPiece = new ChessPiece(color, ChessPiece.PieceType.BISHOP);
            var KingPiece = new ChessPiece(color, ChessPiece.PieceType.KING);
            var QueenPiece = new ChessPiece(color, ChessPiece.PieceType.QUEEN);
            var Rook2Piece = new ChessPiece(color, ChessPiece.PieceType.ROOK);
            var Knight2Piece = new ChessPiece(color, ChessPiece.PieceType.KNIGHT);
            var Bishop2Piece = new ChessPiece(color, ChessPiece.PieceType.BISHOP);
            addPiece(new ChessPosition(y, 1), RookPiece);
            addPiece(new ChessPosition(y, 2), KnightPiece);
            addPiece(new ChessPosition(y, 3), BishopPiece);
            addPiece(new ChessPosition(y, 5), KingPiece);
            addPiece(new ChessPosition(y, 4), QueenPiece);
            addPiece(new ChessPosition(y, 8), Rook2Piece);
            addPiece(new ChessPosition(y, 7), Knight2Piece);
            addPiece(new ChessPosition(y, 6), Bishop2Piece);
            y += 7;
        }
    }

    @Override
    public String toString() {
        var builder = new StringBuilder();
        for (var item : board) {
            builder.append(Arrays.toString(item));
            builder.append(", ");
        }
        return builder.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(board, that.board);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(board);
    }
}
