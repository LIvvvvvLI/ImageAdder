package picture;

public interface Place {
    /**
     * 在父布局中放置节点。
     * @param x 节点在父布局中的横坐标
     * @param y 节点在父布局中的纵坐标
     * @return 放置成功返回 true，否则返回 false
     */
    boolean place(double x, double y);
}
