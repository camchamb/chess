package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static ui.EscapeSequences.*;

public class PrintBoard {

        // Board dimensions.
        private static final int BOARD_SIZE_IN_SQUARES = 8;
        private static final int SQUARE_SIZE_IN_PADDED_CHARS = 1;
        private static final int LINE_WIDTH_IN_PADDED_CHARS = 0;

        // Padded characters.

        private static ChessBoard board;


        public static void printBoard(ChessGame.TeamColor color) {
            var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

            board = new ChessGame().getBoard();

            out.print(ERASE_SCREEN);

            if (color == ChessGame.TeamColor.WHITE) {
                drawWhiteBoard(out);
            } else {
                drawBlackBoard(out);
            }
            out.println();

            out.print(RESET_BG_COLOR);
            out.print(RESET_TEXT_COLOR);
        }

        private static void drawBlackHeader(PrintStream out) {

            setBlack(out);

            String[] headers = { " h ", "  g ", "  f ", " e ", "  d ", " c ", "  b " , " a "};
            for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES; ++boardCol) {
                drawHeader(out, headers[boardCol]);

                if (boardCol < BOARD_SIZE_IN_SQUARES - 1) {
                    out.print(EMPTY.repeat(LINE_WIDTH_IN_PADDED_CHARS));
                }
            }

            out.println();
        }

    private static void drawWhiteHeader(PrintStream out) {

        setBlack(out);
        String[] headers = { " a ", "  b ", "  c ", " d ", "  e ", " f ", "  g " , " h "};
        for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES; ++boardCol) {
            drawHeader(out, headers[boardCol]);

            if (boardCol < BOARD_SIZE_IN_SQUARES - 1) {
                out.print(EMPTY.repeat(LINE_WIDTH_IN_PADDED_CHARS));
            }
        }

        out.println();
    }

        private static void drawHeader(PrintStream out, String headerText) {
            out.print(EMPTY.repeat(0));
            printHeaderText(out, headerText);
            out.print(EMPTY.repeat(0));
        }

        private static void printHeaderText(PrintStream out, String player) {
            out.print(SET_BG_COLOR_BLACK);
            out.print(SET_TEXT_COLOR_GREEN);

            out.print(player);

            setBlack(out);
        }

    private static void drawWhiteBoard(PrintStream out) {
        drawWhiteHeader(out);

        String[] rows = { " 1", " 2", " 3", " 4", " 5", " 6", " 7" , " 8"};

        for (int boardRow = 8; boardRow > 0; --boardRow) {
            for (int boardCol = 1; boardCol <= 8; ++boardCol) {

                if ((boardRow + boardCol) % 2 != 0) {
                    printWhiteSquare(out, board.getPiece(new ChessPosition(boardRow, boardCol)));
                } else {
                    printBlackSquare(out, board.getPiece(new ChessPosition(boardRow, boardCol)));
                }
            }
            out.print(SET_BG_COLOR_BLACK);
            out.print(SET_TEXT_COLOR_GREEN);

            out.print(rows[boardRow - 1]);

            setBlack(out);
            out.println();
        }
    }

    private static void drawBlackBoard(PrintStream out) {
        drawBlackHeader(out);
        String[] rows = { " 1", " 2", " 3", " 4", " 5", " 6", " 7" , " 8"};

        for (int boardRow = 1; boardRow <= BOARD_SIZE_IN_SQUARES; ++boardRow) {
            for (int boardCol = 8; boardCol > 0; --boardCol) {

                if ((boardRow + boardCol) % 2 != 0) {
                    printWhiteSquare(out, board.getPiece(new ChessPosition(boardRow, boardCol)));
                } else {
                    printBlackSquare(out, board.getPiece(new ChessPosition(boardRow, boardCol)));
                }
            }
            out.print(SET_BG_COLOR_BLACK);
            out.print(SET_TEXT_COLOR_GREEN);

            out.print(rows[boardRow - 1]);

            setBlack(out);
            out.println();
        }
    }

    private static void setWhite(PrintStream out) {
        out.print(SET_BG_COLOR_WHITE);
        out.print(SET_TEXT_COLOR_WHITE);
    }

    private static void setBlack(PrintStream out) {
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_BLACK);
    }

    private static void printWhiteSquare(PrintStream out, ChessPiece player) {
        out.print(SET_BG_COLOR_WHITE);
        out.print(SET_TEXT_COLOR_BLACK);

        out.print(toCharacter(player));

        setWhite(out);
    }

    private static void printBlackSquare(PrintStream out, ChessPiece player) {
        out.print(SET_BG_COLOR_LIGHT_GREY);
        out.print(SET_TEXT_COLOR_BLACK);
        out.print(toCharacter(player));

        setWhite(out);
    }

    private static String toCharacter(ChessPiece player) {
        if (player == null) {
            return EMPTY;
        }
        var color = player.getTeamColor();
        var type = player.getPieceType();

        return switch (color) {
            case WHITE -> whiteCharacter(type);
            case BLACK -> blackCharacter(type);
        };
    }

    private static String whiteCharacter(ChessPiece.PieceType type) {
        return switch (type) {
            case KING -> WHITE_KING;
            case QUEEN -> WHITE_QUEEN;
            case BISHOP -> WHITE_BISHOP;
            case KNIGHT -> WHITE_KNIGHT;
            case ROOK -> WHITE_ROOK;
            case PAWN -> WHITE_PAWN;
        };
    }

    private static String blackCharacter(ChessPiece.PieceType type) {
        return switch (type) {
            case KING -> BLACK_KING;
            case QUEEN -> BLACK_QUEEN;
            case BISHOP -> BLACK_BISHOP;
            case KNIGHT -> BLACK_KNIGHT;
            case ROOK -> BLACK_ROOK;
            case PAWN -> BLACK_PAWN;
        };
    }

}
