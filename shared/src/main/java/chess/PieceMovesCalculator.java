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
        if (this.type.equals(ChessPiece.PieceType.PAWN)) {
//            moves = (ArrayList<ChessMove>) pawnMove(moves);
        }
        if (this.type.equals(ChessPiece.PieceType.KING)) {
            moves = (ArrayList<ChessMove>) kingMove(moves);
        }
        if (this.type.equals(ChessPiece.PieceType.BISHOP)) {
            moves = (ArrayList<ChessMove>) bishopMove(moves);
        }
        if (this.type.equals(ChessPiece.PieceType.QUEEN)) {
            moves = (ArrayList<ChessMove>) rookMove(moves);
            moves = (ArrayList<ChessMove>) bishopMove(moves);
        }
        return moves;
    }

    private boolean addSpace(Collection<ChessMove> moves, int y, int x) {
        if (x > 8 || x < 1 || y < 1 || y >8) {return false;}
        var position = new ChessPosition(y, x);
        if (board.getPiece(position) == null) {
            moves.add(new ChessMove(this.myPosition, position, null));
            return true;
        } else {
            if (board.getPiece(position).getTeamColor().equals(pieceColor)) {
                return false;
            } else {
                moves.add(new ChessMove(this.myPosition, position, null));
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

    private Collection<ChessMove> kingMove(Collection<ChessMove> moves) {
        for (int y = myPosition.getRow()-1; y <= myPosition.getRow()+1; y++) {
            for (int x = myPosition.getColumn() - 1; x <= myPosition.getColumn() + 1; x++) {
                if (!addSpace(moves, y, x)) {
                }
            }
        }
        return moves;
    }

    private Collection<ChessMove> bishopMove(Collection<ChessMove> moves) {
        for (int i = 1; i <= 8; i++) {
            if (!addSpace(moves, myPosition.getRow()+i, myPosition.getColumn()-i)) {
                break;
            }
        }
        for (int i = 1; i <= 8; i++) {
            if (!addSpace(moves, myPosition.getRow()+i, myPosition.getColumn()+i)) {
                break;
            }
        }
        for (int i = 1; i <= 8; i++) {
            if (!addSpace(moves, myPosition.getRow()-i, myPosition.getColumn()-i)) {
                break;
            }
        }
        for (int i = 1; i <= 8; i++) {
            if (!addSpace(moves, myPosition.getRow()-i, myPosition.getColumn()+i)) {
                break;
            }
        }
        return moves;
    }


    private Collection<ChessMove> pawnMove(Collection<ChessMove> moves) {
        if (pieceColor.equals(ChessGame.TeamColor.WHITE)) {
            var position = new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn());
            if (board.getPiece(position) == null) {
                if (position.getRow() == 8) {
                    promotePawn(moves, position);
                } else if (myPosition.getRow() == 2) {
                    position = new ChessPosition(myPosition.getRow() + 2, myPosition.getColumn());
                    if (board.getPiece(position) == null) {
                        moves.add(new ChessMove(this.myPosition, position, null));
                    }
                }
            }
            position = new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() - 1);
            if (myPosition.getColumn() != 1 && board.getPiece(position) != null && board.getPiece(position).getTeamColor() != pieceColor) {
                moves.add(new ChessMove(this.myPosition, position, null));
            }
            position = new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() + 1);
            if (myPosition.getColumn() != 8 && board.getPiece(position) != null && board.getPiece(position).getTeamColor() != pieceColor) {
                moves.add(new ChessMove(this.myPosition, position, null));
            }

            return moves;
            }


        if (pieceColor.equals(ChessGame.TeamColor.BLACK)) {
            var position = new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn());
            if (board.getPiece(position) == null) {
                moves.add(new ChessMove(this.myPosition, position, null));
                if (myPosition.getRow() == 7) {
                    position = new ChessPosition(myPosition.getRow() - 2, myPosition.getColumn());
                    if (board.getPiece(position) == null) {
                        moves.add(new ChessMove(this.myPosition, position, null));
                    }
                }
            }
            position = new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() - 1);
            if (myPosition.getColumn() != 1 && board.getPiece(position) != null && board.getPiece(position).getTeamColor() != pieceColor) {
                moves.add(new ChessMove(this.myPosition, position, null));
            }
            position = new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() + 1);
            if (myPosition.getColumn() != 8 && board.getPiece(position) != null && board.getPiece(position).getTeamColor() != pieceColor) {
                moves.add(new ChessMove(this.myPosition, position, null));
            }

            return moves;
        } else {return moves;}
    }

    private void promotePawn(Collection<ChessMove> moves, ChessPosition position) {
        for (var piece : ChessPiece.PieceType.values()) {
            if (piece != ChessPiece.PieceType.KING && piece != ChessPiece.PieceType.PAWN) {
                moves.add(new ChessMove(this.myPosition, position, piece));
            }
        }
    }

}
