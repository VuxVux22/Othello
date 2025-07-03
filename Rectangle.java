
public class Rectangle {
    protected Position position;
    protected int width;
    protected int height;

    public Rectangle(Position position, int width, int height) {
        this.position = position;
        this.width = width;
        this.height = height;
    }

    public Rectangle(int x, int y, int width, int height) {
        this(new Position(x,y),width,height);
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public Position getPosition() {
        return position;
    }

    public Position getCentre() {
        return new Position(position.x + width/2, position.y + height/2);
    }

    public boolean isPositionInside(Position targetPosition) {
        return targetPosition.x >= position.x && targetPosition.y >= position.y
                && targetPosition.x < position.x + width && targetPosition.y < position.y + height;
    }

    public boolean isIntersecting(Rectangle otherRectangle) {
        if(position.y + height < otherRectangle.position.y) return false;
        if(position.y > otherRectangle.position.y + otherRectangle.height) return false;
        if(position.x + width < otherRectangle.position.x) return false;
        if(position.x > otherRectangle.position.x + otherRectangle.width) return false;

        return true;
    }
}