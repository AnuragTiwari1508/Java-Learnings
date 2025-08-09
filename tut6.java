import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

// Person class for the game
class GamePerson {
    private int id;
    private int x, y;
    private Color color;
    private boolean isPredator;
    private boolean isAlive;
    private Random random;
    
    public GamePerson(int id, int x, int y) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.color = Color.BLUE;
        this.isPredator = false;
        this.isAlive = true;
        this.random = new Random();
    }
    
    // Getters and setters
    public int getId() { return id; }
    public int getX() { return x; }
    public int getY() { return y; }
    public Color getColor() { return color; }
    public boolean isPredator() { return isPredator; }
    public boolean isAlive() { return isAlive; }
    
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    public void setPredator(boolean predator) {
        this.isPredator = predator;
        this.color = predator ? Color.RED : Color.BLUE;
    }
    
    public void setAlive(boolean alive) {
        this.isAlive = alive;
        if (!alive) {
            this.color = Color.GRAY;
        }
    }
    
    // Move randomly within bounds
    public void moveRandomly(int maxX, int maxY) {
        if (!isAlive) return;
        
        int deltaX = random.nextInt(3) - 1; // -1, 0, or 1
        int deltaY = random.nextInt(3) - 1; // -1, 0, or 1
        
        x = Math.max(0, Math.min(maxX - 1, x + deltaX));
        y = Math.max(0, Math.min(maxY - 1, y + deltaY));
    }
    
    // Check if this person can catch another
    public boolean canCatch(GamePerson other) {
        return this.isPredator && other.isAlive && !other.isPredator &&
               Math.abs(this.x - other.x) <= 1 && Math.abs(this.y - other.y) <= 1;
    }
}

public class tut6 extends JFrame {
    private static final int GRID_SIZE = 10;
    private static final int CELL_SIZE = 50;
    private static final int MAX_PEOPLE = 5;
    
    private List<GamePerson> people;
    private JPanel gamePanel;
    private JTextField numberField;
    private JTextField predatorField;
    private JButton addPersonBtn;
    private JButton setPredatorBtn;
    private JButton startGameBtn;
    private JButton stopGameBtn;
    private JLabel statusLabel;
    private javax.swing.Timer gameTimer;
    private boolean gameRunning;
    private Random random;
    
    public tut6() {
        people = new ArrayList<>();
        random = new Random();
        gameRunning = false;
        initializeGUI();
        setupGameTimer();
    }
    
    private void initializeGUI() {
        setTitle("Predator Game - Green Grass Ground");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setSize(700, 700);
        setLocationRelativeTo(null);
        
        createTopPanel();
        createGamePanel();
        createBottomPanel();
        
        getContentPane().setBackground(new Color(34, 139, 34)); // Forest Green
    }
    
    private void createTopPanel() {
        JPanel topPanel = new JPanel(new FlowLayout());
        topPanel.setBackground(new Color(46, 125, 50));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel titleLabel = new JLabel("🌱 PREDATOR GAME ON GREEN GRASS 🌱");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        
        topPanel.add(titleLabel);
        add(topPanel, BorderLayout.NORTH);
    }
    
    private void createGamePanel() {
        gamePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawGame(g);
            }
        };
        
        gamePanel.setBackground(new Color(76, 175, 80)); // Light Green Grass
        gamePanel.setPreferredSize(new Dimension(GRID_SIZE * CELL_SIZE, GRID_SIZE * CELL_SIZE));
        gamePanel.setBorder(BorderFactory.createLineBorder(new Color(27, 94, 32), 3));
        
        add(gamePanel, BorderLayout.CENTER);
    }
    
    private void createBottomPanel() {
        JPanel bottomPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        bottomPanel.setBackground(new Color(46, 125, 50));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Control panel 1: Add person
        JPanel controlPanel1 = new JPanel(new FlowLayout());
        controlPanel1.setBackground(new Color(46, 125, 50));
        
        JLabel addLabel = new JLabel("Enter number to add person (1-" + MAX_PEOPLE + "):");
        addLabel.setForeground(Color.WHITE);
        addLabel.setFont(new Font("Arial", Font.BOLD, 12));
        
        numberField = new JTextField(5);
        addPersonBtn = new JButton("Add Person");
        addPersonBtn.setBackground(new Color(76, 175, 80));
        addPersonBtn.setForeground(Color.WHITE);
        addPersonBtn.addActionListener(e -> addPerson());
        
        controlPanel1.add(addLabel);
        controlPanel1.add(numberField);
        controlPanel1.add(addPersonBtn);
        
        // Control panel 2: Set predator
        JPanel controlPanel2 = new JPanel(new FlowLayout());
        controlPanel2.setBackground(new Color(46, 125, 50));
        
        JLabel predLabel = new JLabel("Enter person number to make predator:");
        predLabel.setForeground(Color.WHITE);
        predLabel.setFont(new Font("Arial", Font.BOLD, 12));
        
        predatorField = new JTextField(5);
        setPredatorBtn = new JButton("Set Predator");
        setPredatorBtn.setBackground(new Color(244, 67, 54));
        setPredatorBtn.setForeground(Color.WHITE);
        setPredatorBtn.addActionListener(e -> setPredator());
        
        controlPanel2.add(predLabel);
        controlPanel2.add(predatorField);
        controlPanel2.add(setPredatorBtn);
        
        // Control panel 3: Game controls
        JPanel controlPanel3 = new JPanel(new FlowLayout());
        controlPanel3.setBackground(new Color(46, 125, 50));
        
        startGameBtn = new JButton("Start Game");
        startGameBtn.setBackground(new Color(76, 175, 80));
        startGameBtn.setForeground(Color.WHITE);
        startGameBtn.addActionListener(e -> startGame());
        
        stopGameBtn = new JButton("Stop Game");
        stopGameBtn.setBackground(new Color(244, 67, 54));
        stopGameBtn.setForeground(Color.WHITE);
        stopGameBtn.addActionListener(e -> stopGame());
        
        statusLabel = new JLabel("Add people to start the game!");
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        controlPanel3.add(startGameBtn);
        controlPanel3.add(stopGameBtn);
        controlPanel3.add(statusLabel);
        
        bottomPanel.add(controlPanel1);
        bottomPanel.add(controlPanel2);
        bottomPanel.add(controlPanel3);
        
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    private void drawGame(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Draw grid lines (grass pattern)
        g2d.setColor(new Color(56, 142, 60));
        for (int i = 0; i <= GRID_SIZE; i++) {
            g2d.drawLine(i * CELL_SIZE, 0, i * CELL_SIZE, GRID_SIZE * CELL_SIZE);
            g2d.drawLine(0, i * CELL_SIZE, GRID_SIZE * CELL_SIZE, i * CELL_SIZE);
        }
        
        // Draw grass texture
        g2d.setColor(new Color(102, 187, 106));
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                if ((i + j) % 2 == 0) {
                    g2d.fillRect(i * CELL_SIZE, j * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                }
            }
        }
        
        // Draw people
        for (GamePerson person : people) {
            int centerX = person.getX() * CELL_SIZE + CELL_SIZE / 2;
            int centerY = person.getY() * CELL_SIZE + CELL_SIZE / 2;
            int radius = 15;
            
            // Draw person as circle
            g2d.setColor(person.getColor());
            g2d.fillOval(centerX - radius, centerY - radius, radius * 2, radius * 2);
            
            // Draw border
            g2d.setColor(Color.BLACK);
            g2d.drawOval(centerX - radius, centerY - radius, radius * 2, radius * 2);
            
            // Draw person number
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 12));
            String id = String.valueOf(person.getId());
            FontMetrics fm = g2d.getFontMetrics();
            int textX = centerX - fm.stringWidth(id) / 2;
            int textY = centerY + fm.getAscent() / 2;
            g2d.drawString(id, textX, textY);
        }
        
        // Draw legend
        drawLegend(g2d);
    }
    
    private void drawLegend(Graphics2D g2d) {
        int legendX = 10;
        int legendY = 10;
        
        g2d.setColor(new Color(0, 0, 0, 150));
        g2d.fillRect(legendX - 5, legendY - 5, 200, 80);
        
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 12));
        g2d.drawString("Legend:", legendX, legendY + 15);
        
        // Blue circle for normal person
        g2d.setColor(Color.BLUE);
        g2d.fillOval(legendX, legendY + 20, 15, 15);
        g2d.setColor(Color.WHITE);
        g2d.drawString("Normal Person", legendX + 20, legendY + 32);
        
        // Red circle for predator
        g2d.setColor(Color.RED);
        g2d.fillOval(legendX, legendY + 40, 15, 15);
        g2d.setColor(Color.WHITE);
        g2d.drawString("Predator", legendX + 20, legendY + 52);
        
        // Gray circle for caught
        g2d.setColor(Color.GRAY);
        g2d.fillOval(legendX, legendY + 60, 15, 15);
        g2d.setColor(Color.WHITE);
        g2d.drawString("Caught", legendX + 20, legendY + 72);
    }
    
    private void addPerson() {
        try {
            int number = Integer.parseInt(numberField.getText().trim());
            
            if (number < 1 || number > MAX_PEOPLE) {
                JOptionPane.showMessageDialog(this, "Please enter a number between 1 and " + MAX_PEOPLE + "!");
                return;
            }
            
            // Check if person already exists
            for (GamePerson person : people) {
                if (person.getId() == number) {
                    JOptionPane.showMessageDialog(this, "Person " + number + " already exists!");
                    return;
                }
            }
            
            // Add person at random position
            int x = random.nextInt(GRID_SIZE);
            int y = random.nextInt(GRID_SIZE);
            
            GamePerson newPerson = new GamePerson(number, x, y);
            people.add(newPerson);
            
            numberField.setText("");
            updateStatus("Person " + number + " added at position (" + x + ", " + y + ")");
            gamePanel.repaint();
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number!");
        }
    }
    
    private void setPredator() {
        try {
            int predatorId = Integer.parseInt(predatorField.getText().trim());
            
            GamePerson predator = null;
            for (GamePerson person : people) {
                if (person.getId() == predatorId) {
                    predator = person;
                    break;
                }
            }
            
            if (predator == null) {
                JOptionPane.showMessageDialog(this, "Person " + predatorId + " not found!");
                return;
            }
            
            // Reset all predators
            for (GamePerson person : people) {
                person.setPredator(false);
            }
            
            // Set new predator
            predator.setPredator(true);
            predatorField.setText("");
            updateStatus("Person " + predatorId + " is now the predator!");
            gamePanel.repaint();
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number!");
        }
    }
    
    private void setupGameTimer() {
        gameTimer = new javax.swing.Timer(500, e -> {
            if (gameRunning) {
                updateGame();
                gamePanel.repaint();
            }
        });
    }
    
    private void startGame() {
        if (people.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Add some people first!");
            return;
        }
        
        boolean hasPredator = people.stream().anyMatch(GamePerson::isPredator);
        if (!hasPredator) {
            JOptionPane.showMessageDialog(this, "Set a predator first!");
            return;
        }
        
        gameRunning = true;
        gameTimer.start();
        updateStatus("Game started! People are moving...");
    }
    
    private void stopGame() {
        gameRunning = false;
        gameTimer.stop();
        updateStatus("Game stopped!");
    }
    
    private void updateGame() {
        // Move all people randomly
        for (GamePerson person : people) {
            person.moveRandomly(GRID_SIZE, GRID_SIZE);
        }
        
        // Check for catches
        for (GamePerson predator : people) {
            if (predator.isPredator() && predator.isAlive()) {
                for (GamePerson victim : people) {
                    if (predator.canCatch(victim)) {
                        victim.setAlive(false);
                        updateStatus("Person " + predator.getId() + " caught person " + victim.getId() + "!");
                    }
                }
            }
        }
        
        // Check win condition
        long aliveCount = people.stream().filter(p -> p.isAlive() && !p.isPredator()).count();
        if (aliveCount == 0) {
            stopGame();
            updateStatus("Game Over! Predator caught everyone!");
            JOptionPane.showMessageDialog(this, "Game Over! Predator wins!");
        }
    }
    
    private void updateStatus(String message) {
        statusLabel.setText(message);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new tut6().setVisible(true);
        });
    }
}
