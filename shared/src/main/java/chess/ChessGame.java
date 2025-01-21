package chess;

import java.util.Collection;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private TeamColor currentPlayer = TeamColor.WHITE;
    private ChessBoard board = new ChessBoard();

    public ChessGame() {
        board.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return currentPlayer;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        currentPlayer = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        if (board.getPiece(startPosition) == null) {return null;}
        return board.getPiece(startPosition).pieceMoves(board, startPosition);
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        System.out.println(board);
        System.out.println(move);
        var moves = validMoves(move.getStartPosition());
        System.out.println(moves);
        if (moves == null) throw new InvalidMoveException("Not a piece there");
        if (board.getPiece(move.getStartPosition()).getTeamColor() != currentPlayer) {
            throw new InvalidMoveException("Wrong Team");
        }
        if (!moves.contains(move)) {
            throw new InvalidMoveException("Not a valid move");
        }
        if (move.getPromotionPiece() != null) {
            ChessPiece piece = new ChessPiece(board.getPiece(move.getStartPosition()).getTeamColor(), move.getPromotionPiece());
            board.addPiece(move.getEndPosition(), piece);
            board.addPiece(move.getStartPosition(), null);
        } else {
        ChessPiece piece = board.getPiece(move.getStartPosition());
        board.addPiece(move.getEndPosition(), piece);
        board.addPiece(move.getStartPosition(), null);
        }
        if (currentPlayer.equals(TeamColor.WHITE)) {
            setTeamTurn(TeamColor.BLACK);
        } else {
            setTeamTurn(TeamColor.WHITE);
        }
    }


    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPosition;
        if (teamColor.equals(TeamColor.WHITE)) {
            kingPosition = board.getWhiteKing();
        } else {
            kingPosition = board.getBlackKing();
        }
        for (int x = 1; x < 9; x++) {
            for (int y = 1; y < 9; y++) {
                var position = new ChessPosition(y, x);
                if (board.getPiece(position) != null) {
                    var piece = board.getPiece(position);
                    var moves = piece.pieceMoves(board, position);
                        if (piece.getTeamColor() != teamColor
                                && moves.contains(new ChessMove(position, kingPosition, null))) {
                            return true;
                        }
                    }
                }
            }
        return false;
        }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = null;
        for (int x = 1; x < 9; x++) {
            for (int y = 1; y < 9; y++) {
                var position = new ChessPosition(y, x);
                var piece = board.getPiece(position);
                if (piece != null && piece.getPieceType().equals(ChessPiece.PieceType.KING)) {
                    if (piece.getTeamColor().equals(TeamColor.WHITE)) {
                        board.setWhiteKing(position);
                    } else {
                        board.setBlackKing(position);
                    }
                }
            }
        }
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }
}
