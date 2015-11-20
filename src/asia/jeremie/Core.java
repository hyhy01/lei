/**
 *
 */
package asia.jeremie;

import java.util.LinkedList;
import java.util.ArrayList;

/**
 * 核心算法
 *
 * @author Jeremie
 *         <p/>
 *         TODO 实现新的生成算法
 *         TODO 实现自动插旗算法
 *         TODO 实现翻转提示算法
 */
public class Core {

    /**
     * 地雷矩阵
     * 第一个参数y
     * 第二个参数x
     */
    public Flag[][] data;

    /**
     * 蒙板矩阵
     * 第一个参数y
     * 第二个参数x
     * true 翻开
     * false 未翻开
     */
    public boolean[][] mask;

    /**
     * 标旗层 实验性 暂时只实现了自动标旗
     * TODO 应在泛洪算法中处理冲突问题
     * TODO 在未来的自动点开算法中使用
     */
    public boolean[][] flag;

    /**
     * 总大小x
     */
    private int lx;
    /**
     * 总大小y
     */
    private int ly;

    /**
     * 总雷数
     */
    private int Bi;

    /**
     * 剩余方块数量
     */
    private int Mi;

    /**
     * 小旗数量
     */
    private int Fi;

    /**
     * 是否要求可解
     */
    private boolean Solubility;

    /**
     * 小旗矩阵
     * true插上	false去除
     */
    public boolean[][] sf;

    /**
     * 已经初始化
     */
    private boolean areCreate = false;

    /**
     *
     */
    public Core() {
    }

    /**
     * 初始化
     *
     * @param lx         长
     * @param ly         宽
     * @param iB         雷数
     * @param solubility 是否要求可解
     * @return 创建成功
     */
    public boolean initial(int lx, int ly, int iB, boolean solubility) {
        this.lx = lx;
        this.ly = ly;
        this.Bi = iB;
        this.Mi = lx * ly;
        this.Solubility = solubility;
        if (lx * ly < iB) {        // 超过雷数限制
            return false;
        }
        if (lx < 2 || ly < 2) {        // 过小
            return false;
        }
        if (solubility) {
            if (lx * ly - 9 < iB) {     // 在要求可解的情况下  至少要有9个空格
                return false;
            }
        }
        // 初始化数组
        data = new Flag[ly][lx];
        mask = new boolean[ly][lx];
        flag = new boolean[ly][lx];
        sf = new boolean[ly][lx];
        for (int y = 0; y < data.length; y++) {        // y
            for (int x = 0; x < data[y].length; x++) {        // x
                data[y][x] = Flag.F0;
                mask[y][x] = false;
                flag[y][x] = false;
                sf[y][x] = false;
            }
        }
        return true;
    }

    /**
     * 创建地雷
     * 参数为不可为地雷的坐标
     *
     * @param nx 点击坐标x
     * @param ny 点击坐标y
     * @return true   雷生成失败为false
     */
    public boolean create(int nx, int ny) {

        // 生成雷
        if (!CreateBoom(nx, ny)) {
            return false;
        }

        // 计算数字
        for (int y = 0; y < data.length; y++) {        // y
            for (int x = 0; x < data[y].length; x++) {        // x
                if (data[y][x] != Flag.Boom) {
                    int i = 0;

                    if (x - 1 >= 0) {
                        i += isBoom(x - 1, y);    // 左
                        if (y - 1 >= 0) {
                            i += isBoom(x - 1, y - 1);    // 左上
                        }
                        if (y + 1 < ly) {
                            i += isBoom(x - 1, y + 1);    // 左下
                        }
                    }

                    if (y - 1 >= 0) {
                        i += isBoom(x, y - 1);    // 上
                    }
                    if (y + 1 < ly) {
                        i += isBoom(x, y + 1);    // 下
                    }

                    if (x + 1 < lx) {
                        i += isBoom(x + 1, y);    // 右
                        if (y - 1 >= 0) {
                            i += isBoom(x + 1, y - 1);    // 右上
                        }
                        if (y + 1 < ly) {
                            i += isBoom(x + 1, y + 1);    // 右下
                        }
                    }

                    setF(x, y, i);
                }
            }
        }


        areCreate = true;        // 已经初始化
        return true;
    }


    /**
     * 雷生成算法
     *
     * @param nx 点击坐标x
     * @param ny 点击坐标y
     * @return 生成是否成功
     */
    private boolean CreateBoom(int nx, int ny) {
        if (!this.Solubility) {     // 不要求可解  使用原生成算法
            ArrayList<Vector2D> vd = new ArrayList<Vector2D>();
            for (int y = 0; y < data.length; y++) {    // y
                for (int x = 0; x < data[y].length; x++) {        // x
                    if (x != nx || y != ny) {        // 不是指定项
                        vd.add(new Vector2D(x, y));
                    }
                }
            }
            if (vd.size() < lx * ly - 1) {
                System.err.println("vd.size() < lx*ly-1");
                System.err.println("vd.size():" + vd.size());
                return false;
            }
            if (vd.size() == 0) {
                System.err.println("vd.size() == 0");
                return false;
            }
            for (int i = 0; i < Bi; i++) {
                int a = random(vd.size() - 1);
                data[vd.get(a).y][vd.get(a).x] = Flag.Boom;
                vd.remove(a);
            }
            return true;
        }
        // 要求可解  使用新生成算法

        // 算法思想
        // 要保证可解必须要保证在八联通下只有一个联通域
        // 且第一次点击时一定要在空白位置（无数字）
        // 所以简单地生成算法是
        // 先在点击位置放置9空位
        // 再不断在空位边缘随机放置空位
        // 即可简单保证八联通性

        // 首先将所有位置设为boom
        for (int y = 0; y < data.length; y++) {        // y
            for (int x = 0; x < data[y].length; x++) {        // x
                data[y][x] = Flag.Boom;
            }
        }

        // 计算剩余空位数量
        int leftempty = this.lx * this.ly - this.Bi;

        // 边缘数组
        ArrayList<Vector2D> bd = new ArrayList<Vector2D>();
        // 之后每放置一个空位就要将周围的非空位放入数组

        // 放置初始的9块空位   因为可能在边缘   所以放一个才-1计数
        if (isInBound(nx, ny)) {
            // 点击位
            data[ny][nx] = Flag.F0;
            --leftempty;
        } else {
            System.err.println("点击位无效");
            return false;  // 点击位无效
        }
        // 初始序列
        ArrayList<Vector2D> sd = GetAroundV2D(nx, ny);


        // 遍历处理初始8格并添加边缘 且清除自身
        for (Vector2D vsd : sd) {
            // 设置
            data[vsd.y][vsd.x] = Flag.F0;
            --leftempty;
            // 移除
            bd = RemoveObjFromV2D(bd, vsd);
            // 添加周边  非出界  是boom
//            bd.addAll(RemoveNoBoomFromV2D(RemoveOutOfBoundFromV2D(GetAroundV2D(vsd.x, vsd.y))));
            NoDoubleAppend(bd, RemoveNoBoomFromV2D(RemoveOutOfBoundFromV2D(GetAroundV2D(vsd.x, vsd.y))));
        }

        // 从周边数组中生成空位
        while (!bd.isEmpty() && leftempty > 0) {
            // 直接在边缘上随机选一个  直到满足
            int r = random(bd.size() - 1);
            Vector2D t = bd.get(r);
            data[t.y][t.x] = Flag.F0;
            --leftempty;
            bd.remove(r);
            NoDoubleAppend(bd, RemoveNoBoomFromV2D(RemoveOutOfBoundFromV2D(GetAroundV2D(t.x, t.y))));
        }

        if (leftempty != 0) {
            System.err.println("leftempty != 0\tleftempty:" + leftempty);
            return false;
        }


        return true;
    }

    /**
     * 获取周围有效坐标
     *
     * @param x 原点x
     * @param y 原点y
     * @return 返回坐标数组
     */
    private ArrayList<Vector2D> GetAroundV2D(int x, int y) {
        ArrayList<Vector2D> temp = new ArrayList<Vector2D>();
        temp.add(new Vector2D(x - 1, y - 1));
        temp.add(new Vector2D(x - 1, y));
        temp.add(new Vector2D(x - 1, y + 1));

        temp.add(new Vector2D(x, y - 1));
        temp.add(new Vector2D(x, y + 1));

        temp.add(new Vector2D(x + 1, y - 1));
        temp.add(new Vector2D(x + 1, y));
        temp.add(new Vector2D(x + 1, y + 1));
        return RemoveOutOfBoundFromV2D(temp);
    }

    /**
     * 无重复追加
     *
     * @param v1 数组1
     * @param v2 数组2
     * @return 新数组
     */
    private ArrayList<Vector2D> NoDoubleAppend(ArrayList<Vector2D> v1, ArrayList<Vector2D> v2) {
        // 移除重复元素
        for (Vector2D d : v1) {
            v2 = RemoveObjFromV2D(v2, d);
        }
        v1.addAll(v2);  //合并
        return v1;
    }

    /**
     * 从数组中移除超区元素
     *
     * @param v2d 数组
     * @return 返回移除后的数组
     */
    private ArrayList<Vector2D> RemoveOutOfBoundFromV2D(ArrayList<Vector2D> v2d) {
        int i = 0;
        while (i < v2d.size()) {
            if (!isInBound(v2d.get(i).x, v2d.get(i).y)) {
                v2d.remove(i);
            } else {
                ++i;
            }
        }
        return v2d;
    }

    /**
     * 从数组中移除非boom的元素
     *
     * @param v2d 数组
     * @return 返回移除后的数组
     */
    private ArrayList<Vector2D> RemoveNoBoomFromV2D(ArrayList<Vector2D> v2d) {
        int i = 0;
        while (i < v2d.size()) {
            if (data[v2d.get(i).y][v2d.get(i).x] != Flag.Boom) {
                v2d.remove(i);
            } else {
                ++i;
            }
        }
        return v2d;
    }

    /**
     * 从ArrayList<Vector2D>中移除指定元素
     *
     * @param v2d 数组
     * @param obj 元素
     * @return 返回移除后的数组
     */
    private ArrayList<Vector2D> RemoveObjFromV2D(ArrayList<Vector2D> v2d, Vector2D obj) {
        for (int i = 0; i < v2d.size(); i++) {
            if (v2d.get(i).equals(obj)) {
                v2d.remove(i);
                break;
            }
        }
        return v2d;
    }

    /**
     * 自动根据数字状态标旗
     */
    public void autoFlag() {
        for (int y = 0; y < data.length; y++) {
            for (int x = 0; x < data[y].length; x++) {
                flag[y][x] = false;
            }
        }
        for (int y = 0; y < data.length; y++) {
            for (int x = 0; x < data[y].length; x++) {
                if (mask[y][x]) {
                    if (data[y][x] != Flag.Boom && data[y][x] != Flag.F0) {
                        ArrayList<Vector2D> vd = RemoveOutOfBoundFromV2D(GetAroundV2D(x, y));
                        int m = 0;
                        while (vd.size() > m) {
                            if (mask[vd.get(m).y][vd.get(m).x]) {
                                vd.remove(m);
                            } else {
                                ++m;
                            }
                        }
                        if (data[y][x].toInt() == vd.size() && !vd.isEmpty()) {
                            for (Vector2D d : vd) {
                                flag[d.y][d.x] = true;
                            }
                        }
                    }
                }
            }
        }
    }


    /**
     * 工具函数    检查坐标是不是在整个矩阵内
     *
     * @param x 坐标x
     * @param y 坐标y
     * @return 有效性
     */
    private boolean isInBound(int x, int y) {
        return x >= 0 && y >= 0 && x < this.lx && y < this.ly;
    }


    /**
     * 点击
     *
     * @param x 点击坐标x
     * @param y 点击坐标y
     * @return 实际内容
     */
    public Flag Hit(int x, int y) {
        if (!areCreate) {        // 未初始化
            create(x, y);
        }
        FloodFill(x, y);
        return data[y][x];
    }


    /**
     * 随机数生成器
     * 已测试此随机生成器的均匀性
     *
     * @param max 随机数最大值
     * @return 结果取值范围 0~max
     */
    public static int random(int max) {
        return (int) (Math.random() * (max + 1));        // (max+1)	使得结果取值范围 0~max
    }

    /**
     * isBoom
     *
     * @param x 坐标x
     * @param y 坐标y
     * @return true 1		false 0
     */
    private int isBoom(int x, int y) {
        if (data[y][x] == Flag.Boom) {
            return 1;
        } else {
            return 0;
        }
    }

    /**
     * 设置标志
     *
     * @param x 坐标x
     * @param y 坐标y
     * @param n 标志n
     */
    private void setF(int x, int y, int n) {
        switch (n) {
            case 0:
                data[y][x] = Flag.F0;
                break;

            case 1:
                data[y][x] = Flag.F1;
                break;

            case 2:
                data[y][x] = Flag.F2;
                break;

            case 3:
                data[y][x] = Flag.F3;
                break;

            case 4:
                data[y][x] = Flag.F4;
                break;

            case 5:
                data[y][x] = Flag.F5;
                break;

            case 6:
                data[y][x] = Flag.F6;
                break;

            case 7:
                data[y][x] = Flag.F7;
                break;

            case 8:
                data[y][x] = Flag.F8;
                break;

            default:
                System.err.println("setF default");
                break;
        }
    }

    /**
     * 翻起操作     使用泛洪填充算法	flood fill
     *
     * @param hx 宽
     * @param hy 高
     */
    private Flag FloodFill(int hx, int hy) {
        LinkedList<Vector2D> stack = new LinkedList<Vector2D>();
        stack.push(new Vector2D(hx, hy));
        do {
            Vector2D v2d = stack.pop();
            int x = v2d.x;
            int y = v2d.y;
            if (!mask[y][x]) {
                if (isFlag(x, y)) {
                    setFlag(x, y);
                }
                mask[y][x] = true;    // 没有翻开就翻开
                --Mi;
                if (Flag.F0 == data[y][x]) {
                    // 是空	周围入栈
                    if (x - 1 >= 0) {
                        stack.push(new Vector2D(x - 1, y));
                        if (y - 1 >= 0) {
                            stack.push(new Vector2D(x - 1, y - 1));
                        }
                        if (y + 1 < ly) {
                            stack.push(new Vector2D(x - 1, y + 1));
                        }
                    }
                    if (x + 1 < lx) {
                        stack.push(new Vector2D(x + 1, y));
                        if (y - 1 >= 0) {
                            stack.push(new Vector2D(x + 1, y - 1));
                        }
                        if (y + 1 < ly) {
                            stack.push(new Vector2D(x + 1, y + 1));
                        }
                    }
                    if (y - 1 >= 0) {
                        stack.push(new Vector2D(x, y - 1));
                    }
                    if (y + 1 < ly) {
                        stack.push(new Vector2D(x, y + 1));
                    }
                }
            }
        } while (!stack.isEmpty());
        return data[hy][hx];
    }

    /**
     * 总雷数
     *
     * @return bi
     */
    public int getBi() {
        return Bi;
    }

    /**
     * 剩余方块数
     *
     * @return mi
     */
    public int getMi() {
        return Mi;
    }

    /**
     * 转换小旗
     *
     * @param x 坐标x
     * @param y 坐标y
     * @return 成功true        mask该位置为true翻开则false
     */
    public boolean setFlag(int x, int y) {
        return setFlag(x, y, (!sf[y][x]));    // 翻转
    }

    /**
     * 设置小旗
     *
     * @param x 坐标x
     * @param y 坐标y
     * @param f true插上 false去除
     * @return 成功true        mask该位置为true翻开则false
     */
    public boolean setFlag(int x, int y, boolean f) {
        if (mask[y][x]) {
            return false;
        } else {
            sf[y][x] = f;
            if (f) {
                ++Fi;
            } else {
                --Fi;
            }
//			System.out.println("Fi:" + Fi);
//
//			for (int ty = 0; ty < data.length; ty++) {		// y
//				for (int tx = 0; tx < data[ty].length; tx++) {		// x
//					System.out.print( sf[ty][tx]?"\t1":"\t" );
//				}
//				System.out.println();
//			}

            return true;
        }
    }

    /**
     * 检测小旗
     *
     * @param x 坐标x
     * @param y 坐标
     * @return 是否有flag
     */
    public boolean isFlag(int x, int y) {
        return sf[y][x];
    }

    /**
     * 获取小旗数量
     * 总雷数 - 小旗数量 = 剩余雷数
     *
     * @return fi
     */
    public int getFi() {
        return Fi;
    }

    /**
     * 是否已经全部解决
     *
     * @return 是否
     */
    public boolean wasSolubed() {
        return Bi >= Mi;
    }


}
