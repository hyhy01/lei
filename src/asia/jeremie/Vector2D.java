/**
 *
 */
package asia.jeremie;

/**
 * 2D编号
 *
 * @author Jeremie
 */
public class Vector2D {

    public int x;
    public int y;

    /**
     * 构造函数
     */
    public Vector2D(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * 实现的比较函数
     * @param obj 对象
     * @return  是否相等
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Vector2D) {
            return this.x == ((Vector2D) obj).x && this.y == ((Vector2D) obj).y;
        }
        return super.equals(obj);
    }
}
