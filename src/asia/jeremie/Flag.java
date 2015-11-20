/**
 *
 */
package asia.jeremie;


/**
 * 标志枚举类
 *
 * @author Jeremie
 */
public enum Flag {
    Boom(-1), F0(0), F1(1), F2(2), F3(3), F4(4), F5(5), F6(6), F7(7), F8(8);

    int i;

    private Flag(int i) {
        this.i = i;
    }

    public Integer toInt() {
        return i;
    }

//	/**
//	 * 状态码
//	 * Init 初始状态,Flag 小旗,Boom 地雷,F0,F1,F2,F3,F4,F5,F6,F7,F8
//	 * @author Jeremie
//	 *
//	 */
//	public enum Statuse {
//		Init,Flag,NoFlag,BFlag,Boom,F0,F1,F2,F3,F4,F5,F6,F7,F8;
//	}

//	/**
//	 * Flag状态转换到按键状态
//	 * @return 新状态
//	 */
//	public Statuse toBStatuse() {
//		switch (i) {
//		case -1:
//			return Statuse.Boom;
////			break;
//
//		case 0:
//			return Statuse.F0;
////			break;
//
//		case 1:
//			return Statuse.F1;
////			break;
//
//		case 2:
//			return Statuse.F2;
////			break;
//
//		case 3:
//			return Statuse.F3;
////			break;
//
//		case 4:
//			return Statuse.F4;
////			break;
//
//		case 5:
//			return Statuse.F5;
////			break;
//
//		case 6:
//			return Statuse.F6;
////			break;
//
//		case 7:
//			return Statuse.F7;
////			break;
//
//		case 8:
//			return Statuse.F8;
////			break;
//
//		default:
//			throw new RuntimeException("Disign Wrong.");
////			break;
//		}
//	}

}
