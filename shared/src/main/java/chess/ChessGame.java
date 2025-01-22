package chess;

import java.util.ArrayList;
import java.util.Arrays;
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
        if (this.board.getPiece(startPosition) == null) {return null;}
        var piece = this.board.getPiece(startPosition);
        var moves = piece.pieceMoves(this.board, startPosition);
//        ChessPiece[][] gameState = Arrays.copyOf(board.getBoard(), board.getBoard().length);

//        ChessPiece[][] gameState = new ChessPiece[8][8];
//        for (int i = 0; i < 8; i++) {
//            gameState[i] = Arrays.copyOf(board.getBoard()[i], 8);
//        }
        ArrayList<ChessMove> newMoves = new ArrayList<>();
        for (var move : moves) {
            ChessPiece[][] gameState = new ChessPiece[8][8];
            for (int i = 0; i < 8; i++) {
                gameState[i] = Arrays.copyOf(board.getBoard()[i], 8);
            }
            ChessBoard newBoard = new ChessBoard();
            newBoard.setBoard(Arrays.copyOf(gameState, gameState.length));
            movePiece(newBoard, move);
            if (!inCheckHelper(newBoard, piece.getTeamColor())) {
                newMoves.add(move);
            }
        }
    return newMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        var moves = validMoves(move.getStartPosition());
        if (moves == null || moves.isEmpty()) throw new InvalidMoveException("Not a piece there");
        if (board.getPiece(move.getStartPosition()).getTeamColor() != currentPlayer) {
            throw new InvalidMoveException("Wrong Team");
        }
        if (!moves.contains(move)) {
            throw new InvalidMoveException("Not a valid move");
        }
        movePiece(this.board, move);
        if (currentPlayer.equals(TeamColor.WHITE)) {
            setTeamTurn(TeamColor.BLACK);
        } else {
            setTeamTurn(TeamColor.WHITE);
        }
    }


    private void movePiece(ChessBoard gameState, ChessMove move) {
        if (move.getPromotionPiece() != null) {
            ChessPiece piece = new ChessPiece(gameState.getPiece(move.getStartPosition()).getTeamColor(), move.getPromotionPiece());
            gameState.addPiece(move.getEndPosition(), piece);
            gameState.addPiece(move.getStartPosition(), null);
        } else {
            ChessPiece piece = gameState.getPiece(move.getStartPosition());
            if (piece.getPieceType().equals(ChessPiece.PieceType.KING)) {
                if (piece.getTeamColor().equals(TeamColor.WHITE)) {
                    gameState.setWhiteKing(move.getEndPosition());
                } else {
                    gameState.setBlackKing(move.getEndPosition());
                }
            }
            gameState.addPiece(move.getEndPosition(), piece);
            gameState.addPiece(move.getStartPosition(), null);
        }
    }


    private boolean inCheckHelper(ChessBoard board, TeamColor teamColor) {
        setKings(board);
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
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        return inCheckHelper(this.board, teamColor);
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
        if (isInCheck(teamColor)) return false;
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */

    public void setKings(ChessBoard board) {
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
    }


    public void setBoard(ChessBoard board) {
        this.board = null;
        setKings(board);
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
