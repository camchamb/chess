package chess;

import java.util.Arrays;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard implements Cloneable {
    private ChessPiece[][] board = new ChessPiece[8][8];

    private ChessPosition whiteKing = null;
    private ChessPosition blackKing = null;

    public ChessPosition getWhiteKing() {
        return whiteKing;
    }

    public void setWhiteKing(ChessPosition whiteKing) {
        this.whiteKing = whiteKing;
    }

    public ChessPosition getBlackKing() {
        return blackKing;
    }

    public void setBlackKing(ChessPosition blackKing) {
        this.blackKing = blackKing;
    }

    public void setBoard(ChessPiece[][] board) {
        this.board = board.clone();
    }


    public ChessBoard() {
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

    @Override
    public ChessBoard clone() {
        try {
            ChessBoard clone = (ChessBoard) super.clone();
            ChessPiece[][] gameState = new ChessPiece[8][8];
            for (int i = 0; i < 8; i++) {
                gameState[i] = Arrays.copyOf(clone.getBoard()[i], 8);
            }
            clone.board = gameState;
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
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


    public ChessPiece[][] getBoard() {
        return board;
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        board = new ChessPiece[8][8];
        whiteKing = new ChessPosition(1, 5);
        blackKing = new ChessPosition(8, 5);

        for (int x = 1; x < 9; x++) {
            var whitePiece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
            var blackPiece = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
            addPiece(new ChessPosition(2, x), whitePiece);
            addPiece(new ChessPosition(7, x), blackPiece);
        }
        int y = 1;
        for (ChessGame.TeamColor color : ChessGame.TeamColor.values()) {
            var rookPiece = new ChessPiece(color, ChessPiece.PieceType.ROOK);
            var knightPiece = new ChessPiece(color, ChessPiece.PieceType.KNIGHT);
            var bishopPiece = new ChessPiece(color, ChessPiece.PieceType.BISHOP);
            var kingPiece = new ChessPiece(color, ChessPiece.PieceType.KING);
            var queenPiece = new ChessPiece(color, ChessPiece.PieceType.QUEEN);
            var rook2Piece = new ChessPiece(color, ChessPiece.PieceType.ROOK);
            var knight2Piece = new ChessPiece(color, ChessPiece.PieceType.KNIGHT);
            var bishop2Piece = new ChessPiece(color, ChessPiece.PieceType.BISHOP);
            addPiece(new ChessPosition(y, 1), rookPiece);
            addPiece(new ChessPosition(y, 2), knightPiece);
            addPiece(new ChessPosition(y, 3), bishopPiece);
            addPiece(new ChessPosition(y, 5), kingPiece);
            addPiece(new ChessPosition(y, 4), queenPiece);
            addPiece(new ChessPosition(y, 8), rook2Piece);
            addPiece(new ChessPosition(y, 7), knight2Piece);
            addPiece(new ChessPosition(y, 6), bishop2Piece);
            y += 7;
        }
    }


}
