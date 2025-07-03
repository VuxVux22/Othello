import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.*;


public class GamePanel extends JPanel implements MouseListener {
    public enum GameState {WTurn,BTurn,Draw,WWins,BWins}

    private static final int PANEL_HEIGHT = 700;
    private static final int PANEL_WIDTH = 600;

    private GameGrid gameGrid;
    private GameState gameState;
    private String gameStateStr;

    private SimpleAI aiBehaviour;

    public GamePanel() {
        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        setBackground(new Color(50, 150, 80));

        gameGrid = new GameGrid(new Position(0,0), PANEL_WIDTH, PANEL_HEIGHT-100, 8, 8);
        setGameState(GameState.BTurn);
        chooseAIType();
        addMouseListener(this);
    }

    public void paint(Graphics g) {
        super.paint(g);
        gameGrid.paint(g);
        drawGameState(g);
        drawPieceCounts(g);
    }

    public void restart() {
        gameGrid.reset();
        setGameState(GameState.BTurn);
    }

    public void handleInput(int keyCode) {
        if(keyCode == KeyEvent.VK_ESCAPE) {
            System.exit(0);
        } else if(keyCode == KeyEvent.VK_R) {
            restart();
            repaint();
        } else if(keyCode == KeyEvent.VK_A) {
            chooseAIType();
        }
    }

    private void playTurn(Position gridPosition) {
        if(!gameGrid.isValidMove(gridPosition)) {
            return;
        } else if(gameState == GameState.BTurn) {
            gameGrid.playMove(gridPosition, 1);
            setGameState(GameState.WTurn);
        } else if(gameState == GameState.WTurn) {
            gameGrid.playMove(gridPosition, 2);
            setGameState(GameState.BTurn);
        }
    }

    private void setGameState(GameState newState) {
        gameState = newState;
        switch (gameState) {
            case WTurn:
                if(gameGrid.getAllValidMoves().size() > 0) {
                    gameStateStr = "Lượt người chơi Trắng";
                } else {
                    gameGrid.updateValidMoves(1);
                    if(gameGrid.getAllValidMoves().size() > 0) {
                        setGameState(GameState.BTurn);
                    } else {
                        testForEndGame(false);
                    }
                }
                break;
            case BTurn:
                if(gameGrid.getAllValidMoves().size() > 0) {
                    gameStateStr = "Lượt người chơi Đen";
                } else {
                    gameGrid.updateValidMoves(2);
                    if(gameGrid.getAllValidMoves().size() > 0) {
                        setGameState(GameState.WTurn);
                    } else {
                        testForEndGame(false);
                    }
                }
                 break;
            case WWins: gameStateStr = "Người chơi Trắng Thắng! Bấm R."; break;
            case BWins: gameStateStr = "Người chơi Đen Thắng! Bấm R."; break;
            case Draw: gameStateStr = "Hòa! Bấm R."; break;
        }
    }

    private void testForEndGame(boolean stillValidMoves) {
        int gameResult = gameGrid.getWinner(stillValidMoves);
        if(gameResult == 1) {
            setGameState(GameState.BWins);
        } else if(gameResult == 2) {
            setGameState(GameState.WWins);
        } else if(gameResult == 3) {
            setGameState(GameState.Draw);
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if(gameState == GameState.WTurn || gameState == GameState.BTurn) {
            Position gridPosition = gameGrid.convertMouseToGridPosition(new Position(e.getX(), e.getY()));
            playTurn(gridPosition);
            testForEndGame(true);

            while(gameState == GameState.WTurn && aiBehaviour != null) {
                playTurn(aiBehaviour.chooseMove());
                testForEndGame(true);
            }
        }

        repaint();
    }

    private void drawGameState(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Segoe UI", Font.BOLD, 30));
        int strWidth = g.getFontMetrics().stringWidth(gameStateStr);
        g.drawString(gameStateStr, PANEL_WIDTH/2-strWidth/2, PANEL_HEIGHT-40);
    }

    private void drawPieceCounts(Graphics g) {
        int[] counts = gameGrid.getPieceCounts();
        int blackPieces = counts[1];
        int whitePieces = counts[2];

        String blackCountStr = "Quân Đen: " + blackPieces;
        String whiteCountStr = "Quân Trắng: " + whitePieces;

        g.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        g.setColor(Color.WHITE);

        FontMetrics fm = g.getFontMetrics();
        int blackStrWidth = fm.stringWidth(blackCountStr);
        int whiteStrWidth = fm.stringWidth(whiteCountStr);

        int yPos = PANEL_HEIGHT - 10;

        g.drawString(blackCountStr, PANEL_WIDTH/2 - blackStrWidth - 50, yPos);

        g.drawString(whiteCountStr, PANEL_WIDTH/2 + 50, yPos);
    }

    private void chooseAIType() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Chọn Chế Độ Chơi", true);
        dialog.setLayout(new BorderLayout(15, 15));
        dialog.setResizable(false);
        dialog.getContentPane().setBackground(new Color(60, 179, 113));

        JLabel titleLabel = new JLabel("CHỌN CHẾ ĐỘ CHƠI", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        dialog.add(titleLabel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 20, 20));
        buttonPanel.setBackground(new Color(60, 179, 113));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 30, 30, 30));

        JButton pvpButton = new JButton("Người chơi vs Người chơi");
        pvpButton.setFont(new Font("Segoe UI", Font.PLAIN, 24));
        pvpButton.setBackground(new Color(46, 139, 87));
        pvpButton.setForeground(Color.WHITE);
        pvpButton.setFocusPainted(false);
        pvpButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(30, 90, 50), 2),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)));
        pvpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                aiBehaviour = null;
                dialog.dispose();
            }
        });
        buttonPanel.add(pvpButton);

        JButton pvaButton = new JButton("Người chơi vs Máy");
        pvaButton.setFont(new Font("Segoe UI", Font.PLAIN, 24));
        pvaButton.setBackground(new Color(46, 139, 87));
        pvaButton.setForeground(Color.WHITE);
        pvaButton.setFocusPainted(false);
        pvaButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(30, 90, 50), 2),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)));
        pvaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                aiBehaviour = new SimpleAI(gameGrid);
                dialog.dispose();
            }
        });
        buttonPanel.add(pvaButton);

        dialog.add(buttonPanel, BorderLayout.CENTER);

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        dialog.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                System.exit(0);
            }
        });
        dialog.setVisible(true);
    }

    @Override
    public void mouseClicked(MouseEvent e) {}
    @Override
    public void mouseReleased(MouseEvent e) {}
    @Override
    public void mouseEntered(MouseEvent e) {}
    @Override
    public void mouseExited(MouseEvent e) {}
}