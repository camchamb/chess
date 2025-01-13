package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PieceMovesCalculator {

    private final ChessPiece.PieceType type;
    private final ChessGame.TeamColor pieceColor;
    private final ChessBoard board;
    private final ChessPosition myPosition;

    public PieceMovesCalculator(ChessPiece.PieceType type, ChessGame.TeamColor pieceColor, ChessBoard board, ChessPosition myPosition) {
        this.type = type;
        this.pieceColor = pieceColor;
        this.board = board;
        this.myPosition = myPosition;
    }
    public Collection<ChessMove> pieceMoves() {
        ArrayList<ChessMove> moves = new ArrayList<>();
        if (this.type.equals(ChessPiece.PieceType.ROOK)) {
            moves = (ArrayList<ChessMove>) rookMove(moves);
        }
        if (this.type.equals(ChessPiece.PieceType.BISHOP)) {
//            moves = bishopMove();
        }
        if (this.type.equals(ChessPiece.PieceType.KING)) {
//            moves = kingMove();
        }
        if (this.type.equals(ChessPiece.PieceType.KING)) {
//            moves = kingMove();
        }
        return moves;
    }

    private boolean addSpace(Collection<ChessMove> moves, int y, int x) {
        var position = new ChessPosition(y, x);
        if (board.getPiece(position) == null) {
            moves.add(new ChessMove(this.myPosition, position, this.type));
            return true;
        } else {
            if (board.getPiece(position).getTeamColor().equals(pieceColor)) {
                return false;
            } else {
                moves.add(new ChessMove(this.myPosition, position, this.type));
                return false;
            }
        }
    }

//    private Collection<ChessMove> kingMove() {};
//
//    private Collection<ChessMove> bishopMove() {
//        var moves = new ArrayList<>();
//    }

    private Collection<ChessMove> rookMove(Collection<ChessMove> moves) {
            for (int y = myPosition.getRow()-1; y > 0; y--) {
                if (!addSpace(moves, y, this.myPosition.getColumn())) {
                    break;
                }
            }
            for (int y = myPosition.getRow()+1; y <= 8; y++) {
                if (!addSpace(moves, y, this.myPosition.getColumn())) {
                    break;
                }
            }
            for (int x = myPosition.getColumn()+1; x <= 8; x++) {
                if (!addSpace(moves, this.myPosition.getRow(), x)) {
                    break;
                }
            }
            for (int x = myPosition.getColumn()-1; x > 0; x--) {
                if (!addSpace(moves, this.myPosition.getRow(), x)) {
                    break;
                }
            }
            return moves;
        }

}
