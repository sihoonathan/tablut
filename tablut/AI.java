package tablut;


import java.util.Random;

import static java.lang.Math.*;

import static tablut.Square.sq;
import static tablut.Board.THRONE;
import static tablut.Piece.*;

/** A Player that automatically generates moves.
 *  @author Nathan Choi
 */
class AI extends Player {

    /** A position-score magnitude indicating a win (for white if positive,
     *  black if negative). */
    private static final int WINNING_VALUE = Integer.MAX_VALUE - 20;
    /** A position-score magnitude indicating a forced win in a subsequent
     *  move.  This differs from WINNING_VALUE to avoid putting off wins. */
    private static final int WILL_WIN_VALUE = Integer.MAX_VALUE - 40;
    /** A magnitude greater than a normal value. */
    private static final int INFTY = Integer.MAX_VALUE;

    /** A new AI with no piece or controller (intended to produce
     *  a template). */
    AI() {
        this(null, null);
    }

    /** A new AI playing PIECE under control of CONTROLLER. */
    AI(Piece piece, Controller controller) {
        super(piece, controller);
    }

    @Override
    Player create(Piece piece, Controller controller) {
        return new AI(piece, controller);
    }

    @Override
    String myMove() {
        /** FIXME */
        Square b = sq(3, 4);
        Square a = THRONE;
        return findMove().toString();
    }

    @Override
    boolean isManual() {
        return false;
    }

    /** Return a move for me from the current position, assuming there
     *  is a move. */
    private Move findMove() {
        /** FIXME */
        Random rand = new Random();
        Board copyBoard = new Board(board());
        if (copyBoard.turn() == WHITE) {
            _lastFoundMove = copyBoard.legalMoves(WHITE).
                    get(rand.nextInt(copyBoard.legalMoves(WHITE).size()));
            for (Move mv : copyBoard.legalMoves(KING)) {
                if (copyBoard.isLegal(mv)) {
                    copyBoard.makeMove(mv);
                }
                if (copyBoard.winner() == WHITE) {
                    _lastFoundMove = mv;
                    break;
                }
                copyBoard.undo();
            }
        } else if (copyBoard.turn() == BLACK) {
            _lastFoundMove = copyBoard.legalMoves(BLACK).
                    get(rand.nextInt(copyBoard.legalMoves(BLACK).size()));
            for (Move mv : copyBoard.legalMoves(BLACK)) {
                if (copyBoard.isLegal(mv)) {
                    copyBoard.makeMove(mv);
                }
                if (copyBoard.winner() == BLACK) {
                    _lastFoundMove = mv;
                    break;
                }
                copyBoard.undo();
            }
        }
        return _lastFoundMove;
    }

    /** The move found by the last call to one of the ...FindMove methods
     *  below. */
    private Move _lastFoundMove;

    /** Find a move from position BOARD and return its value, recording
     *  the move found in _lastFoundMove iff SAVEMOVE. The move
     *  should have maximal value or have value > BETA if SENSE==1,
     *  and minimal value or value < ALPHA if SENSE==-1. Searches up to
     *  DEPTH levels.  Searching at level 0 simply returns a static estimate
     *  of the board value and does not set _lastMoveFound. */
    private int findMove(Board board, int depth, boolean saveMove,
                         int sense, int alpha, int beta) {
        /** FIXME */
        return 0;
    }

    /** Return a heuristically determined maximum search depth
     *  based on characteristics of BOARD. */
    private static int maxDepth(Board board) {
        /** FIXME? */
        return 1;
    }

    /** Return a heuristic value for BOARD. */
    private int staticScore(Board board) {
        /** FIXME */
        return 0;
    }

    /** FIXME: More here. */

}
