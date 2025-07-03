import java.awt.*;


public class GridCell extends Rectangle {
    private int cellState;

    private boolean highlight;

    public GridCell(Position position, int width, int height) {
        super(position, width, height);
        reset();
    }

    public void reset() {
        cellState = 0;
        highlight = false;
    }

    public void setCellState(int newState) {
        this.cellState = newState;
    }

    public int getCellState() {
        return cellState;
    }

    public void setHighlight(boolean highlight) {
        this.highlight = highlight;
    }

    public void paint(Graphics g) {
        if(highlight) {
            g.setColor(new Color(100, 180, 255, 120));
            g.fillRect(position.x, position.y, width, height);
        }

        if(cellState == 0) return;

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setColor(cellState == 1 ? Color.BLACK : Color.WHITE);

        int border = 3;
        g2d.fillOval(position.x + border, position.y + border, width - 2 * border, height - 2 * border);
        g2d.setColor(cellState == 1 ? Color.WHITE : Color.BLACK);
        g2d.drawOval(position.x + border, position.y + border, width - 2 * border, height - 2 * border);


        g2d.setColor(cellState == 1 ? Color.BLACK : Color.WHITE);
        g2d.fillOval(position.x, position.y, width, height);
    }
}