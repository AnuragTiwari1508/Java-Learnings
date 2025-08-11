import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;
import java.util.*;
import java.util.List;

/**
 * Subway Surfer-like 3D Game
 * Features: 3D perspective, running character, obstacles, coins, power-ups
 * Controls: A/D or Left/Right arrows to move lanes, Space to jump, W/S for speed
 */
public class tut9 extends JPanel implements KeyListener, ActionListener {
    private static final int WINDOW_WIDTH = 1000;
    private static final int WINDOW_HEIGHT = 700;
    private static final int LANES = 3; // Left, Center, Right lanes
    private static final double CAMERA_HEIGHT = 2.0;
    private static final double CAMERA_DISTANCE = 8.0;
    
    // Game state
    private javax.swing.Timer gameTimer;
    private double gameSpeed = 0.2;
    private double distance = 0;
    private int score = 0;
    private int lives = 3;
    private boolean isGameOver = false;
    private boolean isJumping = false;
    private double jumpHeight = 0;
    private double jumpVelocity = 0;
    
    // Player state
    private int currentLane = 1; // 0=left, 1=center, 2=right
    private double playerX = 0;
    private double playerY = 0;
    private double playerZ = 0;
    private double targetX = 0;
    
    // Game objects
    private List<GameObject> obstacles;
    private List<GameObject> coins;
    private List<GameObject> powerUps;
    private List<RailTrack> tracks;
    
    // Input handling
    private Set<Integer> pressedKeys;
    
    public tut9() {
        setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);
        
        initializeGame();
        
        gameTimer = new javax.swing.Timer(16, this); // ~60 FPS
        gameTimer.start();
    }
    
    private void initializeGame() {
        obstacles = new ArrayList<>();
        coins = new ArrayList<>();
        powerUps = new ArrayList<>();
        tracks = new ArrayList<>();
        pressedKeys = new HashSet<>();
        
        // Initialize railroad tracks
        for (int i = 0; i < 200; i++) {
            tracks.add(new RailTrack(i * 4.0));
        }
        
        // Generate initial game objects
        generateGameObjects();
    }
    
    private void generateGameObjects() {
        Random rand = new Random();
        
        // Generate obstacles ahead
        for (int i = 0; i < 50; i++) {
            double z = distance + 20 + i * 15 + rand.nextDouble() * 10;
            
            if (rand.nextDouble() < 0.3) { // 30% chance for obstacle
                int lane = rand.nextInt(LANES);
                double x = (lane - 1) * 3.0; // Convert lane to X position
                obstacles.add(new GameObject(x, 0, z, GameObjectType.OBSTACLE));
            }
            
            if (rand.nextDouble() < 0.4) { // 40% chance for coin
                int lane = rand.nextInt(LANES);
                double x = (lane - 1) * 3.0;
                coins.add(new GameObject(x, 1, z + 5, GameObjectType.COIN));
            }
            
            if (rand.nextDouble() < 0.1) { // 10% chance for power-up
                int lane = rand.nextInt(LANES);
                double x = (lane - 1) * 3.0;
                powerUps.add(new GameObject(x, 0.5, z + 8, GameObjectType.POWERUP));
            }
        }
    }
    
    private void updateGame() {
        if (isGameOver) return;
        
        // Update distance and speed
        distance += gameSpeed;
        gameSpeed += 0.001; // Gradually increase speed
        
        // Handle lane switching
        targetX = (currentLane - 1) * 3.0;
        if (Math.abs(playerX - targetX) > 0.1) {
            playerX += (targetX - playerX) * 0.2; // Smooth transition
        } else {
            playerX = targetX;
        }
        
        // Handle jumping
        if (isJumping) {
            jumpVelocity -= 0.5; // Gravity
            jumpHeight += jumpVelocity;
            if (jumpHeight <= 0) {
                jumpHeight = 0;
                jumpVelocity = 0;
                isJumping = false;
            }
        }
        playerY = jumpHeight;
        
        // Update player Z position (always moving forward)
        playerZ = distance;
        
        // Check collisions
        checkCollisions();
        
        // Clean up distant objects and generate new ones
        cleanupAndGenerate();
        
        // Update score based on distance
        score = (int)(distance * 10);
    }
    
    private void checkCollisions() {
        double playerRadius = 0.8;
        
        // Check obstacle collisions
        Iterator<GameObject> obstacleIter = obstacles.iterator();
        while (obstacleIter.hasNext()) {
            GameObject obstacle = obstacleIter.next();
            if (Math.abs(obstacle.x - playerX) < playerRadius &&
                Math.abs(obstacle.z - playerZ) < playerRadius &&
                playerY < 2.0) { // Not jumping high enough
                
                lives--;
                obstacleIter.remove();
                
                if (lives <= 0) {
                    isGameOver = true;
                }
                break;
            }
        }
        
        // Check coin collisions
        Iterator<GameObject> coinIter = coins.iterator();
        while (coinIter.hasNext()) {
            GameObject coin = coinIter.next();
            if (Math.abs(coin.x - playerX) < playerRadius &&
                Math.abs(coin.z - playerZ) < playerRadius + 2) {
                
                score += 50;
                coinIter.remove();
            }
        }
        
        // Check power-up collisions
        Iterator<GameObject> powerUpIter = powerUps.iterator();
        while (powerUpIter.hasNext()) {
            GameObject powerUp = powerUpIter.next();
            if (Math.abs(powerUp.x - playerX) < playerRadius &&
                Math.abs(powerUp.z - playerZ) < playerRadius + 2) {
                
                score += 100;
                gameSpeed *= 0.8; // Temporary speed boost
                powerUpIter.remove();
            }
        }
    }
    
    private void cleanupAndGenerate() {
        // Remove objects that are too far behind
        obstacles.removeIf(obj -> obj.z < distance - 20);
        coins.removeIf(obj -> obj.z < distance - 20);
        powerUps.removeIf(obj -> obj.z < distance - 20);
        
        // Generate new objects if needed
        if (obstacles.size() < 30) {
            generateGameObjects();
        }
        
        // Add new tracks if needed
        while (tracks.get(tracks.size() - 1).z < distance + 100) {
            tracks.add(new RailTrack(tracks.get(tracks.size() - 1).z + 4.0));
        }
        
        // Remove old tracks
        tracks.removeIf(track -> track.z < distance - 50);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Draw sky gradient
        GradientPaint skyGradient = new GradientPaint(0, 0, new Color(135, 206, 250), 
                                                     0, WINDOW_HEIGHT/2, new Color(25, 25, 112));
        g2d.setPaint(skyGradient);
        g2d.fillRect(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT/2);
        
        // Draw ground
        g2d.setColor(new Color(34, 139, 34));
        g2d.fillRect(0, WINDOW_HEIGHT/2, WINDOW_WIDTH, WINDOW_HEIGHT/2);
        
        // Draw railroad tracks
        g2d.setColor(Color.DARK_GRAY);
        for (RailTrack track : tracks) {
            drawTrack(g2d, track);
        }
        
        // Draw game objects
        drawGameObjects(g2d);
        
        // Draw player
        drawPlayer(g2d);
        
        // Draw UI
        drawUI(g2d);
        
        if (isGameOver) {
            drawGameOver(g2d);
        }
    }
    
    private void drawTrack(Graphics2D g2d, RailTrack track) {
        // Draw railway sleepers (cross ties)
        Point2D[] sleeper = new Point2D[4];
        sleeper[0] = project3D(-4, -0.1, track.z);
        sleeper[1] = project3D(4, -0.1, track.z);
        sleeper[2] = project3D(4, 0.1, track.z);
        sleeper[3] = project3D(-4, 0.1, track.z);
        
        if (sleeper[0] != null && sleeper[1] != null) {
            g2d.setColor(new Color(139, 69, 19)); // Brown
            drawQuad(g2d, sleeper);
        }
        
        // Draw rails
        for (int lane = 0; lane < LANES; lane++) {
            double railX = (lane - 1) * 3.0;
            Point2D rail1 = project3D(railX - 0.1, 0, track.z);
            Point2D rail2 = project3D(railX + 0.1, 0, track.z);
            Point2D rail3 = project3D(railX + 0.1, 0, track.z + 4);
            Point2D rail4 = project3D(railX - 0.1, 0, track.z + 4);
            
            if (rail1 != null && rail2 != null && rail3 != null && rail4 != null) {
                g2d.setColor(Color.LIGHT_GRAY);
                Point2D[] railQuad = {rail1, rail2, rail3, rail4};
                drawQuad(g2d, railQuad);
            }
        }
    }
    
    private void drawGameObjects(Graphics2D g2d) {
        // Draw obstacles
        g2d.setColor(Color.RED);
        for (GameObject obstacle : obstacles) {
            drawCube(g2d, obstacle.x, obstacle.y, obstacle.z, 1.5, 2.0, 1.5, Color.RED);
        }
        
        // Draw coins
        for (GameObject coin : coins) {
            drawCoin(g2d, coin.x, coin.y, coin.z);
        }
        
        // Draw power-ups
        for (GameObject powerUp : powerUps) {
            drawPowerUp(g2d, powerUp.x, powerUp.y, powerUp.z);
        }
    }
    
    private void drawPlayer(Graphics2D g2d) {
        // Draw player as a blue character
        drawCube(g2d, playerX, playerY + 1, playerZ, 0.8, 1.8, 0.8, Color.BLUE);
        
        // Draw player shadow on ground
        if (playerY > 0) {
            Point2D shadowCenter = project3D(playerX, 0, playerZ);
            if (shadowCenter != null) {
                g2d.setColor(new Color(0, 0, 0, 100));
                int shadowSize = (int)(20 / (1 + playerY * 0.5));
                g2d.fillOval((int)shadowCenter.getX() - shadowSize/2, 
                           (int)shadowCenter.getY() - shadowSize/2, 
                           shadowSize, shadowSize);
            }
        }
    }
    
    private void drawCube(Graphics2D g2d, double x, double y, double z, 
                         double width, double height, double depth, Color color) {
        // Project all cube vertices
        Point2D[] vertices = new Point2D[8];
        vertices[0] = project3D(x - width/2, y, z - depth/2);         // Bottom front left
        vertices[1] = project3D(x + width/2, y, z - depth/2);         // Bottom front right
        vertices[2] = project3D(x + width/2, y, z + depth/2);         // Bottom back right
        vertices[3] = project3D(x - width/2, y, z + depth/2);         // Bottom back left
        vertices[4] = project3D(x - width/2, y + height, z - depth/2); // Top front left
        vertices[5] = project3D(x + width/2, y + height, z - depth/2); // Top front right
        vertices[6] = project3D(x + width/2, y + height, z + depth/2); // Top back right
        vertices[7] = project3D(x - width/2, y + height, z + depth/2); // Top back left
        
        // Check if any vertices are visible
        boolean anyVisible = false;
        for (Point2D vertex : vertices) {
            if (vertex != null) {
                anyVisible = true;
                break;
            }
        }
        
        if (!anyVisible) return;
        
        // Draw cube faces with depth sorting
        g2d.setColor(color);
        
        // Draw faces (simplified - just visible faces)
        if (vertices[0] != null && vertices[1] != null && vertices[5] != null && vertices[4] != null) {
            Point2D[] frontFace = {vertices[0], vertices[1], vertices[5], vertices[4]};
            drawQuad(g2d, frontFace);
        }
        
        // Top face
        g2d.setColor(color.brighter());
        if (vertices[4] != null && vertices[5] != null && vertices[6] != null && vertices[7] != null) {
            Point2D[] topFace = {vertices[4], vertices[5], vertices[6], vertices[7]};
            drawQuad(g2d, topFace);
        }
        
        // Right face
        g2d.setColor(color.darker());
        if (vertices[1] != null && vertices[2] != null && vertices[6] != null && vertices[5] != null) {
            Point2D[] rightFace = {vertices[1], vertices[2], vertices[6], vertices[5]};
            drawQuad(g2d, rightFace);
        }
    }
    
    private void drawCoin(Graphics2D g2d, double x, double y, double z) {
        Point2D center = project3D(x, y, z);
        if (center != null) {
            // Animated spinning coin
            long time = System.currentTimeMillis();
            double rotation = (time % 2000) / 2000.0 * Math.PI * 2;
            
            g2d.setColor(Color.YELLOW);
            int size = (int)(15 / Math.max(1, Math.abs(z - playerZ) * 0.1));
            
            // Create spinning effect
            int width = (int)(size * Math.abs(Math.cos(rotation)));
            g2d.fillOval((int)center.getX() - width/2, (int)center.getY() - size/2, width, size);
            
            g2d.setColor(Color.ORANGE);
            g2d.drawOval((int)center.getX() - width/2, (int)center.getY() - size/2, width, size);
        }
    }
    
    private void drawPowerUp(Graphics2D g2d, double x, double y, double z) {
        Point2D center = project3D(x, y, z);
        if (center != null) {
            // Glowing power-up
            long time = System.currentTimeMillis();
            float glow = (float)(0.5 + 0.5 * Math.sin(time * 0.01));
            
            Color powerUpColor = new Color(1.0f, 0.5f, 1.0f, glow);
            g2d.setColor(powerUpColor);
            
            int size = (int)(20 / Math.max(1, Math.abs(z - playerZ) * 0.1));
            
            // Draw star shape
            int[] xPoints = new int[8];
            int[] yPoints = new int[8];
            for (int i = 0; i < 8; i++) {
                double angle = i * Math.PI / 4;
                double radius = (i % 2 == 0) ? size : size * 0.4;
                xPoints[i] = (int)(center.getX() + radius * Math.cos(angle));
                yPoints[i] = (int)(center.getY() + radius * Math.sin(angle));
            }
            g2d.fillPolygon(xPoints, yPoints, 8);
        }
    }
    
    private void drawQuad(Graphics2D g2d, Point2D[] vertices) {
        int[] xPoints = new int[4];
        int[] yPoints = new int[4];
        int validPoints = 0;
        
        for (int i = 0; i < 4; i++) {
            if (vertices[i] != null) {
                xPoints[validPoints] = (int)vertices[i].getX();
                yPoints[validPoints] = (int)vertices[i].getY();
                validPoints++;
            }
        }
        
        if (validPoints >= 3) {
            g2d.fillPolygon(xPoints, yPoints, validPoints);
        }
    }
    
    private Point2D project3D(double x, double y, double z) {
        // Translate relative to camera
        double relativeZ = z - playerZ;
        
        // Don't render if too close or too far
        if (relativeZ <= 0.1 || relativeZ > 100) {
            return null;
        }
        
        // Simple perspective projection
        double scale = CAMERA_DISTANCE / relativeZ;
        double screenX = WINDOW_WIDTH / 2 + (x - playerX) * scale * 50;
        double screenY = WINDOW_HEIGHT / 2 - (y - CAMERA_HEIGHT) * scale * 50;
        
        // Check if point is within screen bounds
        if (screenX < -100 || screenX > WINDOW_WIDTH + 100 || 
            screenY < -100 || screenY > WINDOW_HEIGHT + 100) {
            return null;
        }
        
        return new Point2D.Double(screenX, screenY);
    }
    
    private void drawUI(Graphics2D g2d) {
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 20));
        
        // Score
        g2d.drawString("Score: " + score, 20, 30);
        
        // Lives
        g2d.drawString("Lives: " + lives, 20, 60);
        
        // Speed indicator
        g2d.drawString("Speed: " + String.format("%.1f", gameSpeed * 100), 20, 90);
        
        // Distance
        g2d.drawString("Distance: " + String.format("%.0f", distance), 20, 120);
        
        // Controls
        g2d.setFont(new Font("Arial", Font.PLAIN, 12));
        g2d.drawString("Controls: A/D - Move, Space - Jump, W/S - Speed", WINDOW_WIDTH - 300, 30);
    }
    
    private void drawGameOver(Graphics2D g2d) {
        // Semi-transparent overlay
        g2d.setColor(new Color(0, 0, 0, 150));
        g2d.fillRect(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);
        
        // Game Over text
        g2d.setColor(Color.RED);
        g2d.setFont(new Font("Arial", Font.BOLD, 48));
        FontMetrics fm = g2d.getFontMetrics();
        String gameOverText = "GAME OVER";
        int textWidth = fm.stringWidth(gameOverText);
        g2d.drawString(gameOverText, (WINDOW_WIDTH - textWidth) / 2, WINDOW_HEIGHT / 2 - 50);
        
        // Final score
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 24));
        fm = g2d.getFontMetrics();
        String scoreText = "Final Score: " + score;
        textWidth = fm.stringWidth(scoreText);
        g2d.drawString(scoreText, (WINDOW_WIDTH - textWidth) / 2, WINDOW_HEIGHT / 2);
        
        // Restart instruction
        g2d.setFont(new Font("Arial", Font.PLAIN, 18));
        fm = g2d.getFontMetrics();
        String restartText = "Press R to Restart";
        textWidth = fm.stringWidth(restartText);
        g2d.drawString(restartText, (WINDOW_WIDTH - textWidth) / 2, WINDOW_HEIGHT / 2 + 50);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        updateGame();
        repaint();
    }
    
    @Override
    public void keyPressed(KeyEvent e) {
        pressedKeys.add(e.getKeyCode());
        
        int key = e.getKeyCode();
        
        // Lane switching
        if ((key == KeyEvent.VK_A || key == KeyEvent.VK_LEFT) && currentLane > 0) {
            currentLane--;
        }
        if ((key == KeyEvent.VK_D || key == KeyEvent.VK_RIGHT) && currentLane < LANES - 1) {
            currentLane++;
        }
        
        // Jumping
        if (key == KeyEvent.VK_SPACE && !isJumping) {
            isJumping = true;
            jumpVelocity = 8.0; // Initial jump velocity
        }
        
        // Speed control
        if (key == KeyEvent.VK_W) {
            gameSpeed = Math.min(gameSpeed * 1.5, 1.0);
        }
        if (key == KeyEvent.VK_S) {
            gameSpeed = Math.max(gameSpeed * 0.7, 0.05);
        }
        
        // Restart game
        if (key == KeyEvent.VK_R && isGameOver) {
            restartGame();
        }
    }
    
    @Override
    public void keyReleased(KeyEvent e) {
        pressedKeys.remove(e.getKeyCode());
    }
    
    @Override
    public void keyTyped(KeyEvent e) {}
    
    private void restartGame() {
        distance = 0;
        score = 0;
        lives = 3;
        gameSpeed = 0.2;
        isGameOver = false;
        isJumping = false;
        jumpHeight = 0;
        jumpVelocity = 0;
        currentLane = 1;
        playerX = 0;
        playerY = 0;
        playerZ = 0;
        
        obstacles.clear();
        coins.clear();
        powerUps.clear();
        tracks.clear();
        
        initializeGame();
    }
    
    // Game object classes
    static class GameObject {
        double x, y, z;
        GameObjectType type;
        
        GameObject(double x, double y, double z, GameObjectType type) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.type = type;
        }
    }
    
    enum GameObjectType {
        OBSTACLE, COIN, POWERUP
    }
    
    static class RailTrack {
        double z;
        
        RailTrack(double z) {
            this.z = z;
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Subway Surfer 3D - Java Edition");
            tut9 game = new tut9();
            
            frame.add(game);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            
            game.requestFocusInWindow();
        });
    }
}
