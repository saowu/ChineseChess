package chess;

import java.util.Arrays;

/**
 * 在该项目中，规定从左上角开始为(0,0)，水平向右方向为Y轴正方向，用列或Y表示，垂直向下方向为X轴正方向，用行或X表示
 *
 * @author THDMI
 * @version 0.0.1
 * @date 2022/01/30
 */
public class RuleDefined {

    /**
     * 当前棋手阵营（红方起手）
     */
    private short flagPlayerCurrent = Const.FLAG_R;

    /**
     * 棋盘棋子位置和标识
     */
    public static short[][] chessboard = new short[][]{
            {0x14, 0x15, 0x13, 0x12, 0x11, 0x12, 0x13, 0x15, 0x14},
            {0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00},
            {0x00, 0x16, 0x00, 0x00, 0x00, 0x00, 0x00, 0x16, 0x00},
            {0x17, 0x00, 0x17, 0x00, 0x17, 0x00, 0x17, 0x00, 0x17},
            {0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00},
            {0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00},
            {0x07, 0x00, 0x07, 0x00, 0x07, 0x00, 0x07, 0x00, 0x07},
            {0x00, 0x06, 0x00, 0x00, 0x00, 0x00, 0x00, 0x06, 0x00},
            {0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00},
            {0x04, 0x05, 0x03, 0x02, 0x01, 0x02, 0x03, 0x05, 0x04}
    };

    /**
     * 返回当前棋手阵营标识
     *
     * @return 当前阵营标识
     */
    public short getFlagPlayer() {
        return this.flagPlayerCurrent;
    }

    /**
     * 交换并返回当前阵营标识
     */
    public void exchangeFlagPlayer() {
        if (Const.FLAG_R == this.flagPlayerCurrent) {
            this.flagPlayerCurrent = Const.FLAG_B;
        } else if (Const.FLAG_B == this.flagPlayerCurrent) {
            this.flagPlayerCurrent = Const.FLAG_R;
        }
    }


    /**
     * 获取某个阵营的将帅位置，如果提供阵营不存在，则有可能引发数组下标越界异常
     *
     * @param flagPlayer 阵营标识
     * @return 该阵营将帅位置的行和列（X、Y）
     */
    public byte[] getPosGeneral(short flagPlayer) {
        if (Const.FLAG_R == flagPlayer) {
            for (byte i = Const.RANGE_SUDOKU[1 + 1][0]; i < Const.RANGE_SUDOKU[1 + 1][1] + 1; ++i) {
                for (byte j = Const.RANGE_SUDOKU[0][0]; j < Const.RANGE_SUDOKU[0][1] + 1; ++j) {
                    if (Const.PIECE_R[0] == chessboard[i][j]) {
                        return new byte[]{i, j};
                    }
                }
            }
        } else if (Const.FLAG_B == flagPlayer) {
            for (byte i = Const.RANGE_SUDOKU[1][0]; i < Const.RANGE_SUDOKU[1][1] + 1; ++i) {
                for (byte j = Const.RANGE_SUDOKU[0][0]; j < Const.RANGE_SUDOKU[0][1] + 1; ++j) {
                    if (Const.PIECE_B[0] == chessboard[i][j]) {
                        return new byte[]{i, j};
                    }
                }
            }
        }
        return null;
    }

    /**
     * 判断当前棋子移动是否可能对对方进行将军
     *
     * @param posX       当前棋子行坐标
     * @param posY       当前棋子列坐标
     * @param flagPlayer 当前棋子所属阵营
     * @return 是否能够将对方的军（是true、否false）
     */
    public boolean isCheckmate(byte posX, byte posY, short flagPlayer) {
        // 优先判断两方将帅是否在同一直线且在两者之间没有棋子阻隔
        byte[] posGeneralR = this.getPosGeneral(Const.FLAG_R);
        byte[] posGeneralB = this.getPosGeneral(Const.FLAG_B);
        if (null == posGeneralR || null == posGeneralB) {
            return true;
        }
        if (posGeneralR[1] == posGeneralB[1] && 0 == this.getHinderCount(posGeneralB[0], posGeneralR[0], posGeneralB[1], Const.MOVE_VERTICAL)) {
            return true;
        }
        if (Const.FLAG_R == flagPlayer) {
            byte[] posGeneralOpposite = this.getPosGeneral(Const.FLAG_B);
            if (null == posGeneralOpposite) {
                return true;
            }
            return this.isPermitRuleMove(posX, posY, posGeneralOpposite[0], posGeneralOpposite[1], Const.FLAG_R);
        } else if (Const.FLAG_B == flagPlayer) {
            byte[] posGeneralOpposite = this.getPosGeneral(Const.FLAG_R);
            if (null == posGeneralOpposite) {
                return true;
            }
            return this.isPermitRuleMove(posX, posY, posGeneralOpposite[0], posGeneralOpposite[1], Const.FLAG_B);
        }
        throw new IndexOutOfBoundsException("该阵营标识不存在，请提供正确的阵营标识。");
    }

    /**
     * 根据选取的棋子和目标位置进行对应的位移操作判断是否符合移动规则
     *
     * @param posSrcX    己方棋子所在行坐标
     * @param posSrcY    己方棋子所在列坐标
     * @param posDstX    目标位置行坐标吧
     * @param posDstY    目标位置列坐标
     * @param flagPlayer 当前回合玩家阵营标识
     * @return 移动棋子是否符合规则（符合true、不符合false）
     */
    public boolean isPermitRuleMove(byte posSrcX, byte posSrcY, byte posDstX, byte posDstY, short flagPlayer) {
        if (!this.isOverRange(posSrcX, posSrcY, Const.RANGE_CHESSBOARD[1], Const.RANGE_CHESSBOARD[0])) {
            // 如果初始位置选取超出了棋盘区域则不可移动
            return false;
        } else if (!this.isOverRange(posDstX, posDstY, Const.RANGE_CHESSBOARD[1], Const.RANGE_CHESSBOARD[0])) {
            // 如果目标位置选取超出了棋盘区域则不可移动
            return false;
        }
        if (!this.isSelfCamp(flagPlayer, posSrcX, posSrcY) || this.isSelfCamp(flagPlayer, posDstX, posDstY)) {
            // 如果源起始棋子不是己方，或者目标位置棋子还是己方的，均不可移动
            return false;
        }
        switch (chessboard[posSrcX][posSrcY]) {
            case 0x01:
            case 0x11:
                return this.ruleGeneral(posSrcX, posSrcY, posDstX, posDstY, flagPlayer);
            case 0x02:
            case 0x12:
                return this.ruleLifeguard(posSrcX, posSrcY, posDstX, posDstY, flagPlayer);
            case 0x03:
            case 0x13:
                return this.rulePrimeMinister(posSrcX, posSrcY, posDstX, posDstY, flagPlayer);
            case 0x04:
            case 0x14:
                return this.ruleChariot(posSrcX, posSrcY, posDstX, posDstY);
            case 0x05:
            case 0x15:
                return this.ruleCavalry(posSrcX, posSrcY, posDstX, posDstY);
            case 0x06:
            case 0x16:
                return this.ruleBattery(posSrcX, posSrcY, posDstX, posDstY);
            case 0x07:
            case 0x17:
                return this.ruleInfantry(posSrcX, posSrcY, posDstX, posDstY, flagPlayer);
            default:
        }
        return false;
    }

    /**
     * 移动棋子至目标位置
     *
     * @param posSrcX 当前位置行坐标X
     * @param posSrcY 当前位置列坐标Y
     * @param posDstX 目标位置行坐标X
     * @param posDstY 目标位置列坐标Y
     */
    public void moveToPos(byte posSrcX, byte posSrcY, byte posDstX, byte posDstY) {
        chessboard[posDstX][posDstY] = chessboard[posSrcX][posSrcY];
        chessboard[posSrcX][posSrcY] = Const.BLOCK;
    }

    /**
     * 计算移动的行坐标和列坐标距离并返回两者位移距离的绝对值
     *
     * @param posSrcX 源位置行坐标（第X行）
     * @param posSrcY 源位置列坐标（第Y列）
     * @param posDstX 目标位置行坐标（第X行）
     * @param posDstY 目标位置列坐标（第Y列）
     * @return int[] 存放移动距离的行坐标和列坐标的绝对值的二维数组 {x, y}
     */
    public byte[] calStepAbs(byte posSrcX, byte posSrcY, byte posDstX, byte posDstY) {
        return new byte[]{(byte) Math.abs(posDstX - posSrcX), (byte) Math.abs(posDstY - posSrcY)};
    }

    /**
     * 计算移动的行坐标和列坐标距离并返回两者位移距离
     *
     * @param posSrcX 源位置行坐标（第X行）
     * @param posSrcY 源位置列坐标（第Y列）
     * @param posDstX 目标位置行坐标（第X行）
     * @param posDstY 目标位置列坐标（第Y列）
     * @return int[] 存放移动距离的行坐标和列坐标的二维数组 {x, y}
     */
    public byte[] calStep(byte posSrcX, byte posSrcY, byte posDstX, byte posDstY) {
        return new byte[]{(byte) (posDstX - posSrcX), (byte) (posDstY - posSrcY)};
    }

    /**
     * 检测传入目标位置是否在一定范围内（含边缘）
     *
     * @param posX      传入位置X
     * @param posY      传入位置Y
     * @param posLimitX 位置限制范围检测（最小X，最大X）
     * @param posLimitY 位置限制范围检测（最小Y，最大Y）
     * @return 目标是否在范围内（是true、否false）
     */
    public boolean isOverRange(byte posX, byte posY, byte[] posLimitX, byte[] posLimitY) {
        return (posLimitX[0] <= posX && posX <= posLimitX[1] && posLimitY[0] <= posY && posY <= posLimitY[1]);
    }

    /**
     * 判断当前所指位置的棋子是否属于当前操作方阵营
     *
     * @param flagPlayer 当前操作方阵营
     * @param posPieceX  当前位置棋子的X坐标
     * @param posPieceY  当前位置棋子的Y坐标
     * @return 棋子是否属于当前操作方（是：true、否：false）
     */
    public boolean isSelfCamp(short flagPlayer, byte posPieceX, byte posPieceY) {
        if (Const.FLAG_R == flagPlayer) {
            return Arrays.binarySearch(Const.PIECE_R, this.chessboard[posPieceX][posPieceY]) >= 0;
        } else if (Const.FLAG_B == flagPlayer) {
            return Arrays.binarySearch(Const.PIECE_B, this.chessboard[posPieceX][posPieceY]) >= 0;
        }
        return false;
    }


    /**
     * 统计所设定直线路径上棋子数目必须保证输入的参数`posMin`和`posMax`关系是一小一大，否则抛出异常
     * 假设想计算 (4, 3) -> (0, 3) 路径上存在棋子数目：
     * 传入 posMin=0, posMax=4, posFix=3, dir=Const.MOVE_VERTICAL (false) 即可计算
     *
     * @param posMin 可变坐标较小的值
     * @param posMax 可变坐标较大的值
     * @param posFix 固定坐标的值
     * @param dir    方向，参考Const.java里的相关参数，true是水平方向，false是垂直方向
     * @return 路径上存在的棋子的数目
     */
    public byte getHinderCount(byte posMin, byte posMax, byte posFix, boolean dir) {
        byte countHinder = 0;
        for (int i = posMin + 1; i < posMax; ++i) {
            if (dir && Const.BLOCK != chessboard[posFix][i]) {
                // 水平移动
                ++countHinder;
            } else if (!dir && Const.BLOCK != chessboard[i][posFix]) {
                // 垂直移动
                ++countHinder;
            }
        }
        return countHinder;
    }

    /**
     * 将帅移动规则
     *
     * @param posSrcX    源位置行坐标（第X行）
     * @param posSrcY    源位置列坐标（第Y列）
     * @param posDstX    目标位置行坐标（第X行）
     * @param posDstY    目标位置列坐标（第Y列）
     * @param flagPlayer 当前源位置棋子玩家阵营标识
     * @return 是否符合该棋子移动规则（是true、否false）
     */
    public boolean ruleGeneral(byte posSrcX, byte posSrcY, byte posDstX, byte posDstY, short flagPlayer) {
        return false;
    }

    /**
     * 士移动规则
     *
     * @param posSrcX    源位置行坐标（第X行）
     * @param posSrcY    源位置列坐标（第Y列）
     * @param posDstX    目标位置行坐标（第X行）
     * @param posDstY    目标位置列坐标（第Y列）
     * @param flagPlayer 当前源位置棋子玩家阵营标识
     * @return 是否符合该棋子移动规则（是true、否false）
     */
    public boolean ruleLifeguard(byte posSrcX, byte posSrcY, byte posDstX, byte posDstY, short flagPlayer) {
        return false;
    }

    /**
     * 相移动规则
     *
     * @param posSrcX    源位置行坐标（第X行）
     * @param posSrcY    源位置列坐标（第Y列）
     * @param posDstX    目标位置行坐标（第X行）
     * @param posDstY    目标位置列坐标（第Y列）
     * @param flagPlayer 当前源位置棋子玩家阵营标识
     * @return 是否符合该棋子移动规则（是true、否false）
     */
    public boolean rulePrimeMinister(byte posSrcX, byte posSrcY, byte posDstX, byte posDstY, short flagPlayer) {
        return false;
    }

    /**
     * 车移动规则
     *
     * @param posSrcX 源位置行坐标（第X行）
     * @param posSrcY 源位置列坐标（第Y列）
     * @param posDstX 目标位置行坐标（第X行）
     * @param posDstY 目标位置列坐标（第Y列）
     * @return 是否符合该棋子移动规则（是true、否false）
     */
    public boolean ruleChariot(byte posSrcX, byte posSrcY, byte posDstX, byte posDstY) {
        return false;
    }

    /**
     * 马移动规则
     *
     * @param posSrcX 源位置行坐标（第X行）
     * @param posSrcY 源位置列坐标（第Y列）
     * @param posDstX 目标位置行坐标（第X行）
     * @param posDstY 目标位置列坐标（第Y列）
     * @return 是否符合该棋子移动规则（是true、否false）
     */
    public boolean ruleCavalry(byte posSrcX, byte posSrcY, byte posDstX, byte posDstY) {
        return false;
    }

    /**
     * 炮移动规则
     *
     * @param posSrcX 源位置行坐标（第X行）
     * @param posSrcY 源位置列坐标（第Y列）
     * @param posDstX 目标位置行坐标（第X行）
     * @param posDstY 目标位置列坐标（第Y列）
     * @return 是否符合该棋子移动规则（是true、否false）
     */
    public boolean ruleBattery(byte posSrcX, byte posSrcY, byte posDstX, byte posDstY) {
        return false;
    }

    /**
     * 兵移动规则
     *
     * @param posSrcX    源位置行坐标（第X行）
     * @param posSrcY    源位置列坐标（第Y列）
     * @param posDstX    目标位置行坐标（第X行）
     * @param posDstY    目标位置列坐标（第Y列）
     * @param flagPlayer 当前源位置棋子玩家阵营标识
     * @return 是否符合该棋子移动规则（是true、否false）
     */
    public boolean ruleInfantry(byte posSrcX, byte posSrcY, byte posDstX, byte posDstY, short flagPlayer) {
        return false;
    }


    /**
     * 计算新坐标
     *
     * @param piece   车
     * @param oY      老列坐标
     * @param oX      老行坐标
     * @param operate 进
     * @param number  1
     */
    public void newCoordinate(String piece, short oY, short oX, String operate, short number) {
        short flag = (short) (Const.PIECE_MAP.get(piece) < 10 ? 1 : -1);
        byte posSrcX = (byte) oY;
        byte posSrcY = (byte) oX;
        byte posDstX = -1;
        byte posDstY = (byte) (flag < 0 ? (number - 1) : (9 - number));
        // 红方逻辑
        switch (piece) {
            case "马", "馬" -> {
                if (operate.equals("进")) {
                    if (Math.abs(posDstY - oX) == 2) {
                        // 横着走
                        posDstX = (byte) (oY - flag);
                    } else {
                        posDstX = (byte) (oY - flag * 2);
                    }
                } else if (operate.equals("退")) {
                    if (Math.abs(posDstY - oX) == 2) {
                        // 横着走
                        posDstX = (byte) (oY + flag);
                    } else {
                        posDstX = (byte) (oY + flag * 2);
                    }
                }
            }
            case "士", "仕" -> {
                if (operate.equals("进")) {
                    posDstX = (byte) (oY - flag);
                } else if (operate.equals("退")) {
                    posDstX = (byte) (oY + flag);
                }
            }
            case "象", "相" -> {
                if (operate.equals("进")) {
                    posDstX = (byte) (oY - flag * 2);
                } else if (operate.equals("退")) {
                    posDstX = (byte) (oY + flag * 2);
                }
            }
            default -> {
                if (operate.equals("进")) {
                    posDstX = (byte) (oY - flag * number);
                    posDstY = (byte) oX;
                } else if (operate.equals("退")) {
                    posDstX = (byte) (oY + flag * number);
                    posDstY = (byte) oX;
                } else {
                    posDstX = (byte) oY;
                }
            }
        }
        System.out.println(posDstX + "," + posDstY);
        if (isPermitRuleMove(posSrcX, posSrcY, posDstX, posDstY, getFlagPlayer())) {
            moveToPos(posSrcX, posSrcY, posDstX, posDstY);
            System.out.println("===========================移动成功！===========================");
            if (isCheckmate(posDstX, posDstY, getFlagPlayer())) {
                System.out.println("=============================将军！=============================");
            }
            exchangeFlagPlayer();
        } else {
            System.out.println("===========================无法移动！===========================");
        }
    }

    /**
     * 计算当前坐标
     * 例如 后车2进3
     *
     * @param relative 前1/后2(同列)
     * @param piece    车
     * @param pieceY   1
     * @param operate  进
     * @param number   1
     */
    public void moveCoordinate(short relative, String piece, short pieceY, String operate, short number) {
        Short tag = Const.PIECE_MAP.get(piece);
        if (pieceY < 0) {
            // 同列双子，遍历
            for (int i = 0; i < 10; i++) {
                for (int j = 0; j < 9; j++) {
                    if (RuleDefined.chessboard[i][j] == tag) {
                        pieceY = (short) (tag > 10 ? (j + 1) : (9 - j));
                        // 跳出循环
                        i = 1000;
                        break;
                    }
                }
            }
        }
        short oY;
        if (Const.PIECE_MAP.get(piece) < 10) {
            // 红方
            oY = (short) (9 - pieceY);
            for (short oX = 0; oX < 10; oX++) {
                if (RuleDefined.chessboard[oX][oY] == tag) {
                    System.out.println(oX + "," + oY);
                    if (relative-- == 1) {
                        newCoordinate(piece, oX, oY, operate, number);
                        return;
                    }
                }
            }
        }
        // 黑方
        oY = (short) (pieceY - 1);
        for (short oX = 9; oX > -1; oX--) {
            if (RuleDefined.chessboard[oX][oY] == tag) {
                System.out.println(oX + "," + oY);
                if (relative-- == 1) {
                    newCoordinate(piece, oX, oY, operate, number);
                    return;
                }
            }
        }
        System.out.println("找不到棋子，请重新输入！");
    }
}
