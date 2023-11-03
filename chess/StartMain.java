package chess;

import java.util.Scanner;

/**
 * @author THDMI
 * @version 0.0.1
 * @date 2022/01/28
 */
public class StartMain {
    public static void main(String[] args) {
        while (true) {
            PieceRule pieceRule = new PieceRule();
            Scanner sc = new Scanner(System.in);
            while (true) {
                Const.printPiece(RuleDefined.chessboard, pieceRule.getFlagPlayer());
                if (null == pieceRule.getPosGeneral(pieceRule.getFlagPlayer())) {
                    System.err.println("游戏结束！是否重开？");
                    String choice = sc.next();
                    if ("Y".equals(choice) || "y".equals(choice) || "Yes".equals(choice) || "yes".equals(choice)) {
                        break;
                    }
                    System.exit(0);
                }
                String palyer = Const.PIECE_FORMAT.getProperty(String.valueOf(pieceRule.getFlagPlayer()));
                System.out.format("当前阵营：%s\t", "红".equals(palyer) ? "\033[31m" + palyer + "方\033[0m" : palyer + "方");

                System.out.print("请输入行棋术语（車1进1 ）：");
                String move = sc.next();
                if (move.length() != 4) {
                    System.out.println("====================输入不合法，请重新输入！====================");
                    continue;
                }
                short relative = 1;
                String piece = move.substring(0, 1);
                short pieceY = -1;
                if ("前".equals(piece)) {
                    piece = move.substring(1, 2);
                } else if ("后".equals(piece)) {
                    relative = 2;
                    piece = move.substring(1, 2);
                } else {
                    pieceY = Short.parseShort(move.substring(1, 2));
                }
                String operate = move.substring(2, 3);
                short number = Short.parseShort(move.substring(3, 4));
                pieceRule.moveCoordinate(relative, piece, pieceY, operate, number);
            }
        }
    }
}
