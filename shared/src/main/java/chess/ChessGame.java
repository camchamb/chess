package chess;

import java.util.ArrayList;
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
    private ChessPosition enPassantPosition = null;
    private boolean whiteCanCastleRight = true;
    private boolean blackCanCastleRight = true;
    private boolean whiteCanCastleLeft = true;
    private boolean blackCanCastleLeft = true;

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
        ArrayList<ChessMove> newMoves = new ArrayList<>();
        for (var move : moves) {
            var newBoard = board.clone();
            movePiece(newBoard, move);
            if (!inCheckHelper(newBoard, piece.getTeamColor())) {
                newMoves.add(move);
            }
        }
        var enPassant = enPassantMove(startPosition);
        if (enPassant != null) {newMoves.add(enPassant);}
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
        if (moves == null || moves.isEmpty()) {throw new InvalidMoveException("Not a piece there");}
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
        var piece = board.getPiece(move.getEndPosition());
        if (piece.getPieceType().equals(ChessPiece.PieceType.PAWN)) {
            if (didPawnJump(move)) {
                enPassantPosition = new ChessPosition(move.getEndPosition().getRow(), move.getEndPosition().getColumn());
            } else {
                enPassantPosition = null;}
        } else {
            enPassantPosition = null;}
        updateCastle(piece, move.getStartPosition());
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
            if (piece.getPieceType().equals(ChessPiece.PieceType.PAWN)
                    && move.getStartPosition().getColumn() != move.getEndPosition().getColumn()
                    && board.getPiece(move.getEndPosition()) == null) {
                var postition = new ChessPosition(move.getStartPosition().getRow(), move.getEndPosition().getColumn());
                gameState.addPiece(postition, null);
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
                            && (moves.contains(new ChessMove(position, kingPosition, null))
                            || moves.contains(new ChessMove(position, kingPosition, ChessPiece.PieceType.QUEEN))
                            || moves.contains(new ChessMove(position, kingPosition, ChessPiece.PieceType.ROOK))
                            || moves.contains(new ChessMove(position, kingPosition, ChessPiece.PieceType.BISHOP))
                            || moves.contains(new ChessMove(position, kingPosition, ChessPiece.PieceType.KNIGHT)))) {
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



    private boolean noMoves(ChessBoard board, TeamColor teamColor) {
        for (int x = 1; x < 9; x++) {
            for (int y = 1; y < 9; y++) {
                var position = new ChessPosition(y, x);
                var piece = board.getPiece(position);
                if (piece != null && piece.getTeamColor() == teamColor) {
                    var moves = validMoves(position);
                    if (!moves.isEmpty()) {
                        return false;
                    }
                }
            }
        }
        return true;
        }



    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if (!isInCheck(teamColor)) {return false;}
        return noMoves(board, teamColor);
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (isInCheck(teamColor)) {return false;}
        return noMoves(board, teamColor);
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = null;
        setKings(board);
        this.board = board;
    }


    private void setKings(ChessBoard board) {
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

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    private boolean didPawnJump (ChessMove move) {
        return Math.abs(move.getEndPosition().getRow() - move.getStartPosition().getRow()) == 2;
    }


    private ChessMove enPassantMove(ChessPosition startPosition) {
        if (enPassantPosition == null) {
            return null;
        }
        var piece = board.getPiece(startPosition);
        var pieceColor = piece.getTeamColor();
        if (piece.getPieceType() != ChessPiece.PieceType.PAWN) {
            return null;
        }
        ChessPosition endPosition = null;
        if (pieceColor.equals(TeamColor.WHITE)) {
            if (enPassantPosition.equals(new ChessPosition(startPosition.getRow(), startPosition.getColumn() + 1))) {
                endPosition = new ChessPosition(startPosition.getRow() + 1, startPosition.getColumn() + 1);
            }
            if (enPassantPosition.equals(new ChessPosition(startPosition.getRow(), startPosition.getColumn() - 1))) {
                endPosition = new ChessPosition(startPosition.getRow() + 1, startPosition.getColumn() - 1);
            }

        } else {
            if (enPassantPosition.equals(new ChessPosition(startPosition.getRow(), startPosition.getColumn() + 1))) {
                endPosition = new ChessPosition(startPosition.getRow() - 1, startPosition.getColumn() + 1);
            }
            if (enPassantPosition.equals(new ChessPosition(startPosition.getRow(), startPosition.getColumn() - 1))) {
                endPosition = new ChessPosition(startPosition.getRow() - 1, startPosition.getColumn() - 1);
            }
        }
        if (endPosition == null) {
            return null;
        }
        return new ChessMove(startPosition, endPosition, null);
    }


    private void updateCastle(ChessPiece piece, ChessPosition startPosition) {
        if (piece.getPieceType().equals(ChessPiece.PieceType.KING)) {
            if (piece.getTeamColor().equals(TeamColor.WHITE)) {
                whiteCanCastleRight = false;
                whiteCanCastleLeft = false;
            } else {
                blackCanCastleRight = false;
                blackCanCastleLeft = false;
            }
        } else if (piece.getPieceType().equals(ChessPiece.PieceType.ROOK)) {
            if (piece.getTeamColor().equals(TeamColor.WHITE)) {
                if (startPosition.equals(new ChessPosition(1, 1))) {
                    whiteCanCastleLeft = false;
                }
                if (startPosition.equals(new ChessPosition(1, 8))) {
                    whiteCanCastleRight = false;
                } else {
                    if (startPosition.equals(new ChessPosition(8, 1))) {
                        blackCanCastleLeft = false;
                    }
                    if (startPosition.equals(new ChessPosition(8, 8))) {
                        whiteCanCastleRight = false;
                    }
                }

            }
        }
    }

}
