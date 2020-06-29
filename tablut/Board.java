package tablut;

import java.util.ArrayList;
import java.util.Stack;
import java.util.HashSet;
import java.util.HashMap;
import java.util.List;
import java.util.Formatter;

import static tablut.Piece.*;
import static tablut.Square.*;
import static tablut.Move.mv;


/** The state of a Tablut Game.
 *  @author Nathan Choi
 */
class Board {
    /** The number of squares on a side of the board. */
    static final int SIZE = 9;

    /** The throne (or castle) square and its four surrounding squares.. */
    static final Square THRONE = sq(4, 4),
            NTHRONE = sq(4, 5),
            STHRONE = sq(4, 3),
            WTHRONE = sq(3, 4),
            ETHRONE = sq(5, 4);

    /** Initial positions of attackers. */
    static final Square[] INITIAL_ATTACKERS = {
            sq(0, 3), sq(0, 4), sq(0, 5), sq(1, 4),
            sq(8, 3), sq(8, 4), sq(8, 5), sq(7, 4),
            sq(3, 0), sq(4, 0), sq(5, 0), sq(4, 1),
            sq(3, 8), sq(4, 8), sq(5, 8), sq(4, 7)
    };

    /** Initial positions of defenders of the king. */
    static final Square[] INITIAL_DEFENDERS = {
        NTHRONE, ETHRONE, STHRONE, WTHRONE,
        sq(4, 6), sq(4, 2), sq(2, 4),                  sq(6, 4)
    };

    /** Initializes a game board with SIZE squares on a side in the
     *  initial position. */
    Board() {
        init();
    }

    /** Initializes a copy of MODEL. */
    Board(Board model) {
        copy(model);
    }

    /** Copies MODEL into me. */
    void copy(Board model) {
        /** FIXME */
        if (model == this) {
            return;
        }
        init();
        _turn = model.turn();
        _winner = model.winner();
        _moveCount = model._moveCount;
        _repeated = model._repeated;
        for (HashMap.Entry<Square, Piece> each : model._position.entrySet()) {
            Square key = each.getKey();
            Piece val = each.getValue();
            _position.replace(key, val);
        }
        for (HashMap<Square, Piece> each : model._moveStack) {
            for (HashMap.Entry<Square, Piece> eachH : each.entrySet()) {
                Square key = eachH.getKey();
                Piece val = eachH.getValue();
                HashMap<Square, Piece> copy = new HashMap<>();
                _moveStack.push(copy);
            }
        }
        for (Piece each : model._turnStack) {
            _turnStack.push(each);
        }
    }

    /** Clears the board to the initial position. */
    void init() {
        /** FIXME */
        _position = new HashMap<>();
        _winner = null;
        _turn = BLACK;
        _moveCount = 0;
        _moveStack = new Stack<>();
        _turnStack = new Stack<>();

        for (Square attackerSq : INITIAL_ATTACKERS) {
            put(BLACK, attackerSq);
        }

        for (Square defenderSq : INITIAL_DEFENDERS) {
            put(WHITE, defenderSq);
        }

        put(KING, THRONE);

        for (int col = 0; col < 9; col++) {
            for (int row = 0; row < 9; row++) {
                if (get(col, row) == null) {
                    put(EMPTY, sq(col, row));
                }
            }
        }
    }

    /** Set the move limit to LIM.  It is an error if 2*LIM <= moveCount(). */
    /** @param n */
    void setMoveLimit(int n) {
        /** FIXME */
        _LIMIT = n;
    }

    /** Return a Piece representing whose move it is (WHITE or BLACK). */
    Piece turn() {
        return _turn;
    }

    /** Return the winner in the current position, or null if there is no winner
     *  yet. */
    Piece winner() {
        return _winner;
    }

    /** Returns true iff this is a win due to a repeated position. */
    boolean repeatedPosition() {
        return _repeated;
    }

    /** Record current position and set winner() next mover if the current
     *  position is a repeat. */
    private void checkRepeated() {
        /** FIXME */
        boolean foundRepeatedStack = false;
        int moveStackCounter = 0;
        for (HashMap<Square, Piece> eachStack : _moveStack) {
            int counter = 0;
            for (HashMap.Entry<Square, Piece> each : eachStack.entrySet()) {
                if (each.getValue() != _position.get(each.getKey())) {
                    break;
                }
                counter += 1;
                if (counter == BOARDSIZE && (_turnStack.get(moveStackCounter)
                        == _turn)) {
                    foundRepeatedStack = true;
                    _winner = _turn;
                }
            }
            if (foundRepeatedStack) {
                break;
            }
            moveStackCounter += 1;
        }
        _repeated = foundRepeatedStack;
    }

    /** Return the number of moves since the initial position that have not been
     *  undone. */
    int moveCount() {
        return _moveCount;
    }

    /** Return location of the king. */
    Square kingPosition() {
        /** FIXME */
        Square king = null;
        for (HashMap.Entry<Square, Piece> each : _position.entrySet()) {
            Square key = each.getKey();
            Piece val = each.getValue();
            if (val == KING) {
                king = key;
                break;
            }
        }
        return king;
    }

    /** Return the contents the square at S. */
    final Piece get(Square s) {
        return get(s.col(), s.row());
    }

    /** Return the contents of the square at (COL, ROW), where
     *  0 <= COL, ROW <= 9. */
    final Piece get(int col, int row) {
        /** FIXME */
        if (_position.containsKey(sq(col, row))) {
            return _position.get(sq(col, row));
        }
        return null;
    }

    /** Return the contents of the square at COL ROW. */
    final Piece get(char col, char row) {
        return get(col - 'a', row - '1');
    }

    /** Set square S to P. */
    final void put(Piece p, Square s) {
        /** FIXME */
        _position.put(s, p);
    }

    /** Set square S to P and record for undoing. */
    final void revPut(Piece p, Square s) {
        /** FIXME */
        HashMap<Square, Piece> temp = new HashMap<>();
        for (HashMap.Entry<Square, Piece> each : _position.entrySet()) {
            Square sq = sq(each.getKey().index());
            Piece piece = each.getValue();
            temp.put(sq, piece);
        }
        _moveStack.add(temp);
        _turnStack.add(_turn);
        put(p, s);
    }

    /** Set square COL ROW to P. */
    final void put(Piece p, char col, char row) {
        put(p, sq(col - 'a', row - '1'));
    }

    /** Return true iff FROM - TO is an unblocked rook move on the current
     *  board.  For this to be true, FROM-TO must be a rook move and the
     *  squares along it, other than FROM, must be empty. */
    boolean isUnblockedMove(Square from, Square to) {
        if (from.isRookMove(to)) {
            switch (from.direction(to)) {
            case 0:
                for (int i = from.row() + 1; i < to.row(); i++) {
                    if (_position.get(sq(from.col(), i)) != EMPTY) {
                        return false;
                    }
                }
                return true;
            case 2:
                for (int i = to.row() + 1; i < from.row(); i++) {
                    if (_position.get(sq(from.col(), i)) != EMPTY) {
                        return false;
                    }
                }
                return true;
            case 3:
                for (int i = to.col() + 1; i < from.col(); i++) {
                    if (_position.get(sq(i, from.row())) != EMPTY) {
                        return false;
                    }
                }
                return true;
            default:
                for (int i = from.col() + 1; i < to.col(); i++) {
                    if (_position.get(sq(i, from.row())) != EMPTY) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    /** Return true iff FROM is a valid starting square for a move. */
    boolean isLegal(Square from) {
        Piece track = get(from);
        return get(from) == _turn || (get(from) == KING && _turn == WHITE);
    }

    /** Return true iff FROM-TO is a valid move. */
    boolean isLegal(Square from, Square to) {
        /** FIXME */
        if (to == THRONE && (get(from) != KING)) {
            return false;
        }

        if (isLegal(from) && isUnblockedMove(from, to) && get(to) == EMPTY) {
            return true;
        }
        return false;
    }

    /** Return true iff MOVE is a legal move in the current
     *  position. */
    boolean isLegal(Move move) {
        return isLegal(move.from(), move.to());
    }


    /** Move FROM-TO, assuming this is a legal move. */
    void makeMove(Square from, Square to) {
        /** FIXME */
        assert isLegal(from, to);
        if (_winner == null) {
            if (get(from) == BLACK) {
                revPut(get(from), to);
                put(EMPTY, from);
                _turn = WHITE;
            }

            if (get(from) == KING || get(from) == WHITE) {
                revPut(get(from), to);
                put(EMPTY, from);
                _turn = BLACK;
            }
            _moveCount += 1;
            checkRepeated();
            kingReachedEdge();
            isCapturableBasic1(to);
            isCapturableBasic2(to);
            isCapturableBasic3(to);
            isCapturableBasic4(to);
            isCapturableFinal1(to);
            isCapturableFinal2(to);
            isCapturableFinal3(to);
            isCapturableFinal4(to);
            isCapturableBlack1(to);
            isCapturableBlack2(to);
            isCapturableBlack3(to);
            isCapturableBlack4(to);
            isCaptureKing(to);
            kingCaptured();
        }

    }

    /** Additional. */
    /** @param to */
    private void isCaptureKing(Square to) {
        for (HashMap.Entry<Square, Piece> each :_position.entrySet()) {
            Square key = each.getKey();
            Piece pi = each.getValue();
            if (key == THRONE && pi == KING) {
                if (Square.exists(key.col() + 1, key.row())
                        && Square.exists(key.col() - 1, key.row())
                        && Square.exists(key.col(), key.row() + 1)
                        && Square.exists(key.col(), key.row() - 1)) {
                    if (_position.get(sq(key.col() + 1, key.row()))
                            == BLACK
                            && _position.get(sq(key.col() - 1, key.row()))
                            == BLACK && _position.get(sq(key.col(),
                            key.row() + 1)) == BLACK && _position.
                            get(sq(key.col(), key.row() - 1))
                            == BLACK) {
                        revPut(EMPTY, key);
                    }
                }
            } else if (key != NTHRONE && key != STHRONE && key
                    != ETHRONE && key != WTHRONE && pi == KING) {
                if (Square.exists(key.col() + 1, key.row())
                        && Square.exists(key.col() - 1, key.row())) {
                    if (_position.get(sq(key.col() + 1, key.row()))
                            == BLACK && _position.get(sq(key.col() - 1,
                            key.row())) == BLACK) {
                        revPut(EMPTY, key);
                    }
                } else if (Square.exists(key.col(), key.row() + 1)
                        && Square.exists(key.col(), key.row() - 1)) {
                    if (_position.get(sq(key.col(), key.row() + 1))
                            == BLACK && _position.get(sq(key.col(),
                            key.row() - 1)) == BLACK) {
                        revPut(EMPTY, key);
                    }
                }
            }
        }
    }

    /** Addtional. */
    /** @param to */
    private void isCapturableFinal1(Square to) {
        if (Square.exists(to.col() - 1, to.row())) {
            Square toTakeOut = sq(to.col() - 1, to.row());
            boolean kingOnThrone = false;
            if (_position.get(toTakeOut) == WHITE) {
                if (Square.exists(toTakeOut.col() - 1, toTakeOut.row())
                        && Square.exists(toTakeOut.col() + 1,
                        toTakeOut.row()) && Square.exists(
                                toTakeOut.col(), toTakeOut.row() - 1)
                        && Square.exists(toTakeOut.col(),
                        toTakeOut.row() + 1)) {
                    Square one = sq(toTakeOut.col() - 1, toTakeOut.row());
                    Square two = sq(toTakeOut.col() + 1, toTakeOut.row());
                    Square three = sq(toTakeOut.col(), toTakeOut.row() - 1);
                    Square four = sq(toTakeOut.col(), toTakeOut.row() + 1);
                    List<Square> testing = new ArrayList<>();
                    for (Square each : testing) {
                        if (each == THRONE && _position.get(each) == KING) {
                            kingOnThrone = true;
                        }
                    }
                    boolean ready = false;
                    if (kingOnThrone) {
                        for (Square each : testing) {
                            if (each == THRONE || _position.get(each)
                                    == BLACK) {
                                ready = false;
                                break;
                            }
                        }
                    }
                    if (ready) {
                        if (one.isRookMove(two)) {
                            capture(one, two);
                        } else if (one.isRookMove(three)) {
                            capture(one, three);
                        } else if (one.isRookMove(four)) {
                            capture(one, four);
                        }
                    }
                }
            }
        }
    }

    /** Addtional. */
    /** @param to */
    private void isCapturableFinal2(Square to) {
        if (Square.exists(to.col() + 1, to.row())) {
            Square toTakeOut = sq(to.col() + 1, to.row());
            boolean kingOnThrone = false;
            if (_position.get(toTakeOut) == WHITE) {
                if (Square.exists(toTakeOut.col() - 1,
                        toTakeOut.row()) && Square.exists(
                                toTakeOut.col() + 1, toTakeOut.row())
                        && Square.exists(toTakeOut.col(), toTakeOut.row() - 1)
                        && Square.exists(toTakeOut.col(),
                        toTakeOut.row() + 1)) {
                    Square one = sq(toTakeOut.col() - 1, toTakeOut.row());
                    Square two = sq(toTakeOut.col() + 1, toTakeOut.row());
                    Square three = sq(toTakeOut.col(), toTakeOut.row() - 1);
                    Square four = sq(toTakeOut.col(), toTakeOut.row() + 1);
                    List<Square> testing = new ArrayList<>();
                    for (Square each : testing) {
                        if (each == THRONE && _position.get(each) == KING) {
                            kingOnThrone = true;
                        }
                    }
                    boolean ready = false;
                    if (kingOnThrone) {
                        for (Square each : testing) {
                            if (each == THRONE || _position.get(each)
                                    == BLACK) {
                                ready = false;
                                break;
                            }
                        }
                    }
                    if (ready) {
                        if (one.isRookMove(two)) {
                            capture(one, two);
                        } else if (one.isRookMove(three)) {
                            capture(one, three);
                        } else if (one.isRookMove(four)) {
                            capture(one, four);
                        }
                    }
                }
            }
        }
    }

    /** Addtional. */
    /** @param to */
    private void isCapturableFinal3(Square to) {
        if (Square.exists(to.col(), to.row() - 1)) {
            Square toTakeOut = sq(to.col(), to.row() - 1);
            boolean kingOnThrone = false;
            if (_position.get(toTakeOut) == WHITE) {
                if (Square.exists(toTakeOut.col() - 1, toTakeOut.row())
                        && Square.exists(toTakeOut.col() + 1,
                        toTakeOut.row()) && Square.exists(
                                toTakeOut.col(), toTakeOut.row() - 1)
                        && Square.exists(toTakeOut.col(),
                        toTakeOut.row() + 1)) {
                    Square one = sq(toTakeOut.col() - 1, toTakeOut.row());
                    Square two = sq(toTakeOut.col() + 1, toTakeOut.row());
                    Square three = sq(toTakeOut.col(), toTakeOut.row() - 1);
                    Square four = sq(toTakeOut.col(), toTakeOut.row() + 1);
                    List<Square> testing = new ArrayList<>();
                    for (Square each : testing) {
                        if (each == THRONE && _position.get(each) == KING) {
                            kingOnThrone = true;
                        }
                    }
                    boolean ready = false;
                    if (kingOnThrone) {
                        for (Square each : testing) {
                            if (each == THRONE || _position.get(each)
                                    == BLACK) {
                                ready = false;
                                break;
                            }
                        }
                    }
                    if (ready) {
                        if (one.isRookMove(two)) {
                            capture(one, two);
                        } else if (one.isRookMove(three)) {
                            capture(one, three);
                        } else if (one.isRookMove(four)) {
                            capture(one, four);
                        }
                    }
                }
            }
        }
    }

    /** Addtional. */
    /** @param to */
    private void isCapturableFinal4(Square to) {
        if (Square.exists(to.col(), to.row() + 1)) {
            Square toTakeOut = sq(to.col(), to.row() + 1);
            boolean kingOnThrone = false;
            if (_position.get(toTakeOut) == WHITE) {
                if (Square.exists(toTakeOut.col() - 1,
                        toTakeOut.row()) && Square.exists(
                                toTakeOut.col() + 1, toTakeOut.row())
                        && Square.exists(toTakeOut.col(),
                        toTakeOut.row() - 1)
                        && Square.exists(toTakeOut.col(),
                        toTakeOut.row() + 1)) {
                    Square one = sq(toTakeOut.col() - 1, toTakeOut.row());
                    Square two = sq(toTakeOut.col() + 1, toTakeOut.row());
                    Square three = sq(toTakeOut.col(), toTakeOut.row() - 1);
                    Square four = sq(toTakeOut.col(), toTakeOut.row() + 1);
                    List<Square> testing = new ArrayList<>();
                    for (Square each : testing) {
                        if (each == THRONE && _position.get(each) == KING) {
                            kingOnThrone = true;
                        }
                    }
                    boolean ready = false;
                    if (kingOnThrone) {
                        for (Square each : testing) {
                            if (each == THRONE || _position.get(each)
                                    == BLACK) {
                                ready = false;
                                break;
                            }
                        }
                    }
                    if (ready) {
                        if (one.isRookMove(two)) {
                            capture(one, two);
                        } else if (one.isRookMove(three)) {
                            capture(one, three);
                        } else if (one.isRookMove(four)) {
                            capture(one, four);
                        }
                    }
                }
            }
        }
    }

    /** Additional. */
    /** @param to */
    private void isCapturableBlack1(Square to) {
        if (Square.exists(to.col() - 1, to.row())) {
            Square toTakeOut = sq(to.col() - 1, to.row());
            if (_position.get(toTakeOut) == _position.get(to).opponent()) {
                if (Square.exists(toTakeOut.col() - 1, toTakeOut.row())) {
                    Square sq2 = sq(toTakeOut.col() - 1, toTakeOut.row());
                    if (_position.get(toTakeOut) == BLACK
                            && _position.get(sq2) == KING

                            && to.isRookMove(sq2)) {
                        capture(to, sq2);
                    }
                }
                if (Square.exists(toTakeOut.col() + 1, toTakeOut.row())) {
                    Square sq2 = sq(toTakeOut.col() + 1,
                            toTakeOut.row());
                    if (_position.get(toTakeOut) == BLACK
                            && _position.get(sq2) == KING

                            && to.isRookMove(sq2)) {
                        capture(to, sq2);
                    }
                }
                if (Square.exists(toTakeOut.col(), toTakeOut.row() - 1)) {
                    Square sq2 = sq(toTakeOut.col(), toTakeOut.row() - 1);
                    if (_position.get(toTakeOut) == BLACK
                            && _position.get(sq2) == KING
                            && to.isRookMove(sq2)) {
                        capture(to, sq2);
                    }
                }
                if (Square.exists(toTakeOut.col(), toTakeOut.row() + 1)) {
                    Square sq2 = sq(toTakeOut.col(), toTakeOut.row() + 1);
                    if (_position.get(toTakeOut) == BLACK
                            && _position.get(sq2) == KING

                            && to.isRookMove(sq2)) {
                        capture(to, sq2);
                    }
                }
            }
        }
    }

    /** Additional. */
    /** @param to */
    private void isCapturableBlack2(Square to) {
        if (Square.exists(to.col() + 1, to.row())) {
            Square toTakeOut = sq(to.col() + 1, to.row());
            if (_position.get(toTakeOut) == _position.get(to).opponent()) {
                if (Square.exists(toTakeOut.col() - 1, toTakeOut.row())) {
                    Square sq2 = sq(toTakeOut.col() - 1, toTakeOut.row());
                    if (_position.get(toTakeOut) == BLACK
                            && _position.get(sq2) == KING

                            && to.isRookMove(sq2)) {
                        capture(to, sq2);
                    }
                }
                if (Square.exists(toTakeOut.col() + 1, toTakeOut.row())) {
                    Square sq2 = sq(toTakeOut.col() + 1, toTakeOut.row());
                    if (_position.get(toTakeOut) == BLACK
                            && _position.get(sq2) == KING

                            && to.isRookMove(sq2)) {
                        capture(to, sq2);
                    }
                }
                if (Square.exists(toTakeOut.col(), toTakeOut.row() - 1)) {
                    Square sq2 = sq(toTakeOut.col(), toTakeOut.row() - 1);
                    if (_position.get(toTakeOut) == BLACK
                            && _position.get(sq2) == KING

                            && to.isRookMove(sq2)) {
                        capture(to, sq2);
                    }
                }
                if (Square.exists(toTakeOut.col(), toTakeOut.row() + 1)) {
                    Square sq2 = sq(toTakeOut.col(), toTakeOut.row() + 1);
                    if (_position.get(toTakeOut) == BLACK
                            && _position.get(sq2) == KING

                            && to.isRookMove(sq2)) {
                        capture(to, sq2);
                    }
                }
            }
        }
    }

    /** Additional. */
    /** @param to */
    private void isCapturableBlack3(Square to) {
        if (Square.exists(to.col(), to.row() - 1)) {
            Square toTakeOut = sq(to.col(), to.row() - 1);
            if (_position.get(toTakeOut) == _position.get(to).opponent()) {
                if (Square.exists(toTakeOut.col() - 1, toTakeOut.row())) {
                    Square sq2 = sq(toTakeOut.col() - 1, toTakeOut.row());
                    if (_position.get(toTakeOut) == BLACK
                            && _position.get(sq2) == KING
                            && to.isRookMove(sq2)) {
                        capture(to, sq2);
                    }
                }
                if (Square.exists(toTakeOut.col() + 1, toTakeOut.row())) {
                    Square sq2 = sq(toTakeOut.col() + 1, toTakeOut.row());
                    if (_position.get(toTakeOut) == BLACK
                            && _position.get(sq2) == KING
                            && to.isRookMove(sq2)) {
                        capture(to, sq2);
                    }
                }
                if (Square.exists(toTakeOut.col(), toTakeOut.row() - 1)) {
                    Square sq2 = sq(toTakeOut.col(), toTakeOut.row() - 1);
                    if (_position.get(toTakeOut) == BLACK
                            && _position.get(sq2) == KING
                            && to.isRookMove(sq2)) {
                        capture(to, sq2);
                    }
                }
                if (Square.exists(toTakeOut.col(), toTakeOut.row() + 1)) {
                    Square sq2 = sq(toTakeOut.col(), toTakeOut.row() + 1);
                    if (_position.get(toTakeOut) == BLACK
                            && _position.get(sq2) == KING
                            && to.isRookMove(sq2)) {
                        capture(to, sq2);
                    }
                }
            }
        }
    }

    /** Additional. */
    /** @param to */
    private void isCapturableBlack4(Square to) {
        if (Square.exists(to.col(), to.row() + 1)) {
            Square toTakeOut = sq(to.col(), to.row() + 1);
            if (_position.get(toTakeOut) == _position.get(to).opponent()) {
                if (Square.exists(toTakeOut.col() - 1, toTakeOut.row())) {
                    Square sq2 = sq(toTakeOut.col() - 1, toTakeOut.row());
                    if (_position.get(toTakeOut) == BLACK
                            && _position.get(sq2) == KING
                            && to.isRookMove(sq2)) {
                        capture(to, sq2);
                    }
                }
                if (Square.exists(toTakeOut.col() + 1, toTakeOut.row())) {
                    Square sq2 = sq(toTakeOut.col() + 1, toTakeOut.row());
                    if (_position.get(toTakeOut) == BLACK
                            && _position.get(sq2) == KING
                            && to.isRookMove(sq2)) {
                        capture(to, sq2);
                    }
                }
                if (Square.exists(toTakeOut.col(), toTakeOut.row() - 1)) {
                    Square sq2 = sq(toTakeOut.col(), toTakeOut.row() - 1);
                    if (_position.get(toTakeOut) == BLACK
                            && _position.get(sq2) == KING
                            && to.isRookMove(sq2)) {
                        capture(to, sq2);
                    }
                }
                if (Square.exists(toTakeOut.col(), toTakeOut.row() + 1)) {
                    Square sq2 = sq(toTakeOut.col(), toTakeOut.row() + 1);
                    if (_position.get(toTakeOut) == BLACK
                            && _position.get(sq2) == KING
                            && to.isRookMove(sq2)) {
                        capture(to, sq2);
                    }
                }
            }
        }
    }


    /** Additional. */
    /** @param to */
    private void isCapturableBasic1(Square to) {
        if (Square.exists(to.col() - 1, to.row())) {
            Square toTakeOut = sq(to.col() - 1, to.row());
            if (_position.get(toTakeOut) == _position.get(to).opponent()) {
                if (Square.exists(toTakeOut.col() - 1, toTakeOut.row())) {
                    Square sq2 = sq(toTakeOut.col() - 1, toTakeOut.row());
                    if (_position.get(toTakeOut).opponent()
                            == _position.get(sq2)
                            && to.isRookMove(sq2)) {
                        capture(to, sq2);
                    }
                    if (sq2 == THRONE && _position.get(sq2) == EMPTY) {
                        capture(to, sq2);
                    }
                }
                if (Square.exists(toTakeOut.col() + 1, toTakeOut.row())) {
                    Square sq2 = sq(toTakeOut.col() + 1,
                            toTakeOut.row());
                    if (_position.get(toTakeOut).opponent()
                            == _position.get(sq2)
                            && to.isRookMove(sq2)) {
                        capture(to, sq2);
                    }
                    if (sq2 == THRONE && _position.get(sq2) == EMPTY) {
                        capture(to, sq2);
                    }
                }
                if (Square.exists(toTakeOut.col(), toTakeOut.row() - 1)) {
                    Square sq2 = sq(toTakeOut.col(), toTakeOut.row() - 1);
                    if (_position.get(toTakeOut).opponent()
                            == _position.get(sq2)
                            && to.isRookMove(sq2)) {
                        capture(to, sq2);
                    }
                    if (sq2 == THRONE && _position.get(sq2) == EMPTY) {
                        capture(to, sq2);
                    }
                }
                if (Square.exists(toTakeOut.col(), toTakeOut.row() + 1)) {
                    Square sq2 = sq(toTakeOut.col(), toTakeOut.row() + 1);
                    if (_position.get(toTakeOut).opponent()
                            == _position.get(sq2)
                            && to.isRookMove(sq2)) {
                        capture(to, sq2);
                    }
                    if (sq2 == THRONE && _position.get(sq2) == EMPTY) {
                        capture(to, sq2);
                    }
                }
            }
        }
    }

    /** Additional. */
    /** @param to */
    private void isCapturableBasic2(Square to) {
        if (Square.exists(to.col() + 1, to.row())) {
            Square toTakeOut = sq(to.col() + 1, to.row());
            if (_position.get(toTakeOut) == _position.get(to).opponent()) {
                if (Square.exists(toTakeOut.col() - 1, toTakeOut.row())) {
                    Square sq2 = sq(toTakeOut.col() - 1, toTakeOut.row());
                    if (_position.get(toTakeOut).opponent()
                            == _position.get(sq2)
                            && to.isRookMove(sq2)) {
                        capture(to, sq2);
                    }
                    if (sq2 == THRONE && _position.get(sq2) == EMPTY) {
                        capture(to, sq2);
                    }
                }
                if (Square.exists(toTakeOut.col() + 1, toTakeOut.row())) {
                    Square sq2 = sq(toTakeOut.col() + 1, toTakeOut.row());
                    if (_position.get(toTakeOut).opponent()
                            == _position.get(sq2)
                            && to.isRookMove(sq2)) {
                        capture(to, sq2);
                    }
                    if (sq2 == THRONE && _position.get(sq2) == EMPTY) {
                        capture(to, sq2);
                    }
                }
                if (Square.exists(toTakeOut.col(), toTakeOut.row() - 1)) {
                    Square sq2 = sq(toTakeOut.col(), toTakeOut.row() - 1);
                    if (_position.get(toTakeOut).opponent()
                            == _position.get(sq2)
                            && to.isRookMove(sq2)) {
                        capture(to, sq2);
                    }
                    if (sq2 == THRONE && _position.get(sq2) == EMPTY) {
                        capture(to, sq2);
                    }
                }
                if (Square.exists(toTakeOut.col(), toTakeOut.row() + 1)) {
                    Square sq2 = sq(toTakeOut.col(), toTakeOut.row() + 1);
                    if (_position.get(toTakeOut).opponent()
                            == _position.get(sq2)
                            && to.isRookMove(sq2)) {
                        capture(to, sq2);
                    }
                    if (sq2 == THRONE && _position.get(sq2) == EMPTY) {
                        capture(to, sq2);
                    }
                }
            }
        }
    }

    /** Additional. */
    /** @param to */
    private void isCapturableBasic3(Square to) {
        if (Square.exists(to.col(), to.row() - 1)) {
            Square toTakeOut = sq(to.col(), to.row() - 1);
            if (_position.get(toTakeOut) == _position.get(to).opponent()) {
                if (Square.exists(toTakeOut.col() - 1, toTakeOut.row())) {
                    Square sq2 = sq(toTakeOut.col() - 1, toTakeOut.row());
                    if (_position.get(toTakeOut).opponent()
                            == _position.get(sq2)
                            && to.isRookMove(sq2)) {
                        capture(to, sq2);
                    }
                    if (sq2 == THRONE && _position.get(sq2) == EMPTY) {
                        capture(to, sq2);
                    }
                }
                if (Square.exists(toTakeOut.col() + 1, toTakeOut.row())) {
                    Square sq2 = sq(toTakeOut.col() + 1, toTakeOut.row());
                    if (_position.get(toTakeOut).opponent()
                            == _position.get(sq2)
                            && to.isRookMove(sq2)) {
                        capture(to, sq2);
                    }
                    if (sq2 == THRONE && _position.get(sq2) == EMPTY) {
                        capture(to, sq2);
                    }
                }
                if (Square.exists(toTakeOut.col(), toTakeOut.row() - 1)) {
                    Square sq2 = sq(toTakeOut.col(), toTakeOut.row() - 1);
                    if (_position.get(toTakeOut).opponent()
                            == _position.get(sq2)
                            && to.isRookMove(sq2)) {
                        capture(to, sq2);
                    }
                    if (sq2 == THRONE && _position.get(sq2) == EMPTY) {
                        capture(to, sq2);
                    }
                }
                if (Square.exists(toTakeOut.col(), toTakeOut.row() + 1)) {
                    Square sq2 = sq(toTakeOut.col(), toTakeOut.row() + 1);
                    if (_position.get(toTakeOut).opponent()
                            == _position.get(sq2)
                            && to.isRookMove(sq2)) {
                        capture(to, sq2);
                    }
                    if (sq2 == THRONE && _position.get(sq2) == EMPTY) {
                        capture(to, sq2);
                    }
                }
            }
        }
    }

    /** Additional. */
    /** @param to */
    private void isCapturableBasic4(Square to) {
        if (Square.exists(to.col(), to.row() + 1)) {
            Square toTakeOut = sq(to.col(), to.row() + 1);
            if (_position.get(toTakeOut) == _position.get(to).opponent()) {
                if (Square.exists(toTakeOut.col() - 1, toTakeOut.row())) {
                    Square sq2 = sq(toTakeOut.col() - 1, toTakeOut.row());
                    if (_position.get(toTakeOut).opponent()
                            == _position.get(sq2)
                            && to.isRookMove(sq2)) {
                        capture(to, sq2);
                    }
                    if (sq2 == THRONE && _position.get(sq2) == EMPTY) {
                        capture(to, sq2);
                    }
                }
                if (Square.exists(toTakeOut.col() + 1, toTakeOut.row())) {
                    Square sq2 = sq(toTakeOut.col() + 1, toTakeOut.row());
                    if (_position.get(toTakeOut).opponent()
                            == _position.get(sq2)
                            && to.isRookMove(sq2)) {
                        capture(to, sq2);
                    }
                    if (sq2 == THRONE && _position.get(sq2) == EMPTY) {
                        capture(to, sq2);
                    }
                }
                if (Square.exists(toTakeOut.col(), toTakeOut.row() - 1)) {
                    Square sq2 = sq(toTakeOut.col(), toTakeOut.row() - 1);
                    if (_position.get(toTakeOut).opponent()
                            == _position.get(sq2)
                            && to.isRookMove(sq2)) {
                        capture(to, sq2);
                    }
                    if (sq2 == THRONE && _position.get(sq2) == EMPTY) {
                        capture(to, sq2);
                    }
                }
                if (Square.exists(toTakeOut.col(), toTakeOut.row() + 1)) {
                    Square sq2 = sq(toTakeOut.col(), toTakeOut.row() + 1);
                    if (_position.get(toTakeOut).opponent()
                            == _position.get(sq2)
                            && to.isRookMove(sq2)) {
                        capture(to, sq2);
                    }
                    if (sq2 == THRONE && _position.get(sq2) == EMPTY) {
                        capture(to, sq2);
                    }
                }
            }
        }
    }



    /** Additional. */
    void kingReachedEdge() {
        for (HashMap.Entry<Square, Piece> each : _position.entrySet()) {
            Square sq = sq(each.getKey().index());
            Piece piece = each.getValue();
            if (piece == KING && sq.isEdge()) {
                _winner = WHITE;
            }
        }
    }


    /** Additional. */
    void kingCaptured() {
        boolean kingSeen = false;
        for (HashMap.Entry<Square, Piece> each : _position.entrySet()) {
            Square sq = sq(each.getKey().index());
            Piece piece = each.getValue();
            if (piece == KING) {
                kingSeen = true;
            }
        }
        if (!kingSeen && _winner == null) {
            _winner = BLACK;
        }
    }

    /** Move according to MOVE, assuming it is a legal move. */
    void makeMove(Move move) {
        makeMove(move.from(), move.to());
    }

    /** Capture the piece between SQ0 and SQ2, assuming a piece just moved to
     *  SQ0 and the necessary conditions are satisfied. */
    private void capture(Square sq0, Square sq2) {
        /** FIXME */
        if (sq0.col() == sq2.col()) {
            Square middle;
            if (sq0.row() > sq2.row()) {
                middle = sq(sq0.col(), sq0.row() - 1);
            } else {
                middle = sq(sq0.col(), sq0.row() + 1);
            }
            revPut(EMPTY, middle);
        } else {
            Square middle;
            if (sq0.col() > sq2.col()) {
                middle = sq(sq0.col() - 1, sq0.row());
            } else {
                middle = sq(sq0.col() + 1, sq0.row());
            }
            revPut(EMPTY, middle);
        }
    }

    /** Undo one move.  Has no effect on the initial board. */
    void undo() {
        if (_moveCount > 0) {
            _position = _moveStack.pop();
            _turn = _turnStack.pop();
            _moveCount -= 1;
        }
    }

    /** Remove record of current position in the set of positions encountered,
     *  unless it is a repeated position or we are at the first move. */
    private void undoPosition() {
        /** FIXME */
        _repeated = false;
    }

    /** Clear the undo stack and board-position counts. Does not modify the
     *  current position or win status. */
    void clearUndo() {
        /** FIXME */
        _moveStack = new Stack<>();
        _turnStack = new Stack<>();
    }

    /** Return a new mutable list of all legal moves on the current board for
     *  SIDE (ignoring whose turn it is at the moment). */
    List<Move> legalMoves(Piece side) {
        /** FIXME */

        HashSet<Square> pieceLocations = pieceLocations(side);
        List<Move> legalMoveList = new ArrayList<>();
        if (side == EMPTY) {
            return legalMoveList;
        }
        for (Square fromSq : pieceLocations) {
            for (HashMap.Entry<Square, Piece> each : _position.entrySet()) {
                Square toSq = each.getKey();
                Piece toSqPiece = each.getValue();

                if (isUnblockedMove(fromSq, toSq) && get(toSq) == EMPTY) {
                    if (get(fromSq) != KING && toSq != THRONE) {
                        legalMoveList.add(mv(fromSq, toSq));
                    }

                    if (get(fromSq) == KING) {
                        legalMoveList.add(mv(fromSq, toSq));
                    }
                }
            }
        }

        return legalMoveList;
    }

    /** Return true iff SIDE has a legal move. */
    boolean hasMove(Piece side) {
        /** FIXME */
        if (legalMoves(side).isEmpty()) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return toString(true);
    }

    /** Return a text representation of this Board.  If COORDINATES, then row
     *  and column designations are included along the left and bottom sides.
     */
    String toString(boolean coordinates) {
        Formatter out = new Formatter();
        for (int r = SIZE - 1; r >= 0; r -= 1) {
            if (coordinates) {
                out.format("%2d", r + 1);
            } else {
                out.format("  ");
            }
            for (int c = 0; c < SIZE; c += 1) {
                out.format(" %s", get(c, r));
            }
            out.format("%n");
        }
        if (coordinates) {
            out.format("  ");
            for (char c = 'a'; c <= 'i'; c += 1) {
                out.format(" %c", c);
            }
            out.format("%n");
        }
        return out.toString();
    }

    /** Return the locations of all pieces on SIDE. */
    private HashSet<Square> pieceLocations(Piece side) {
        /** FIXME */
        assert side != EMPTY;
        HashSet<Square> pieceLoc = new HashSet<>();
        for (HashMap.Entry<Square, Piece> each : _position.entrySet()) {
            Square key = each.getKey();
            Piece val = each.getValue();
            if (val == side) {
                pieceLoc.add(key);
            }
        }
        return pieceLoc;
    }

    /** Return the contents of _board in the order of SQUARE_LIST as a sequence
     *  of characters: the toString values of the current turn and Pieces. */
    String encodedBoard() {
        char[] result = new char[Square.SQUARE_LIST.size() + 1];
        result[0] = turn().toString().charAt(0);
        for (Square sq : SQUARE_LIST) {
            result[sq.index() + 1] = get(sq).toString().charAt(0);
        }
        return new String(result);
    }

    /** Additional. */
    /** @return a */
    public HashMap<Square, Piece> positionAccessor() {
        return _position;
    }

    /** Piece whose turn it is (WHITE or BLACK). */
    private Piece _turn;
    /** Cached value of winner on this board, or EMPTY if it has not been
     *  computed. */
    private Piece _winner;
    /** Number of (still undone) moves since initial position. */
    private int _moveCount;
    /** True when current board is a repeated position (ending the game). */
    private boolean _repeated;

    /** Additional. */
    private HashMap<Square, Piece> _position;
    /** Additional. */
    private Stack<HashMap<Square, Piece>> _moveStack;
    /** Additional. */
    private Stack<Piece> _turnStack;
    /** Additional. */
    private int _LIMIT;
    /** Additional. */
    private static final int BOARDSIZE = 81;


}
