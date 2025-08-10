import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

// 3D Point class
class Point3D {
    double x, y, z;
    
    Point3D(double x, double y, double z) {
        this.x = x; this.y = y; this.z = z;
    }
    
    Point3D add(Point3D other) {
        return new Point3D(x + other.x, y + other.y, z + other.z);
    }
    
    Point3D subtract(Point3D other) {
        return new Point3D(x - other.x, y - other.y, z - other.z);
    }
    
    Point3D multiply(double scalar) {
        return new Point3D(x * scalar, y * scalar, z * scalar);
    }
}

// 3D Line class
class Line3D {
    Point3D start, end;
    Color color;
    
    Line3D(Point3D start, Point3D end, Color color) {
        this.start = start; this.end = end; this.color = color;
    }
}

// Person class
class Person3D {
    Point3D position;
    Color color;
    String name;
    double size;
    
    Person3D(Point3D position, Color color, String name, double size) {
        this.position = position;
        this.color = color;
        this.name = name;
        this.size = size;
    }
    
    void move(double dx, double dy, double dz) {
        position = position.add(new Point3D(dx, dy, dz));
        
        // Boundary constraints
        position.x = Math.max(-8, Math.min(8, position.x));
        position.z = Math.max(-8, Math.min(8, position.z));
        position.y = Math.max(0, Math.min(8, position.y));
    }
}

public class tut8 extends JFrame {
    private static final int WIDTH = 1000;
    private static final int HEIGHT = 700;
    
    // Camera parameters
    private double cameraX = 0, cameraY = 5, cameraZ = 15;
    private double rotationX = 0, rotationY = 0;
    
    // Scene objects
    private List<Line3D> roomLines;
    private List<Person3D> people;
    
    // UI components
    private JPanel renderPanel;
    private JScrollBar horizontalRotation, verticalRotation;
    private JLabel statusLabel;
    private Person3D selectedPerson;
    
    public tut8() {
        initializeScene();
        initializeGUI();
        setupKeyBindings();
    }
    
    private void initializeScene() {
        roomLines = new ArrayList<>();
        people = new ArrayList<>();
        
        createTwoStoryRoom();
        createStairs();
        createTable();
        createPeople();
    }
    
    private void createTwoStoryRoom() {
        // Ground floor walls
        addRoomBox(-10, 0, -10, 10, 4, 10, new Color(150, 75, 0)); // Brown walls
        
        // Second floor walls
        addRoomBox(-10, 4, -10, 10, 8, 10, new Color(100, 150, 200)); // Blue walls
        
        // Ground floor
        addFloor(-10, 0, -10, 10, 10, new Color(139, 69, 19));
        
        // First floor ceiling / Second floor floor
        addFloor(-10, 4, -10, 10, 10, new Color(160, 82, 45));
        
        // Second floor ceiling
        addFloor(-10, 8, -10, 10, 10, new Color(105, 105, 105));
        
        // Windows on ground floor
        addWindow(-10, 1, -5, -10, 3, -3, new Color(173, 216, 230));
        addWindow(10, 1, -5, 10, 3, -3, new Color(173, 216, 230));
        
        // Windows on second floor
        addWindow(-10, 5, -5, -10, 7, -3, new Color(173, 216, 230));
        addWindow(10, 5, -5, 10, 7, -3, new Color(173, 216, 230));
    }
    
    private void createStairs() {
        // Stair steps (from ground to first floor)
        for (int i = 0; i < 8; i++) {
            double y = i * 0.5;
            double z = 5 - i * 0.5;
            
            // Step surface
            roomLines.add(new Line3D(
                new Point3D(-2, y, z),
                new Point3D(2, y, z),
                new Color(101, 67, 33)
            ));
            roomLines.add(new Line3D(
                new Point3D(2, y, z),
                new Point3D(2, y, z - 0.5),
                new Color(101, 67, 33)
            ));
            roomLines.add(new Line3D(
                new Point3D(2, y, z - 0.5),
                new Point3D(-2, y, z - 0.5),
                new Color(101, 67, 33)
            ));
            roomLines.add(new Line3D(
                new Point3D(-2, y, z - 0.5),
                new Point3D(-2, y, z),
                new Color(101, 67, 33)
            ));
            
            // Step riser
            roomLines.add(new Line3D(
                new Point3D(-2, y, z - 0.5),
                new Point3D(-2, y + 0.5, z - 0.5),
                new Color(139, 69, 19)
            ));
            roomLines.add(new Line3D(
                new Point3D(2, y, z - 0.5),
                new Point3D(2, y + 0.5, z - 0.5),
                new Color(139, 69, 19)
            ));
        }
        
        // Handrails
        for (int i = 0; i < 8; i++) {
            double y = i * 0.5 + 1;
            double z = 5 - i * 0.5;
            
            roomLines.add(new Line3D(
                new Point3D(-2.5, y, z),
                new Point3D(-2.5, y + 0.5, z - 0.5),
                new Color(101, 67, 33)
            ));
            roomLines.add(new Line3D(
                new Point3D(2.5, y, z),
                new Point3D(2.5, y + 0.5, z - 0.5),
                new Color(101, 67, 33)
            ));
        }
    }
    
    private void createTable() {
        // Table on ground floor
        addTable(-3, 0.8, -3, 2, 0.8, 1.5, new Color(160, 82, 45));
        
        // Table on second floor
        addTable(3, 4.8, 3, 2, 0.8, 1.5, new Color(160, 82, 45));
    }
    
    private void createPeople() {
        // Person on ground floor (controllable with WASD)
        people.add(new Person3D(new Point3D(-5, 0, -5), Color.BLUE, "Ground Person", 1.8));
        
        // Person on second floor (controllable with Arrow keys)
        people.add(new Person3D(new Point3D(5, 4, 5), Color.RED, "Upper Person", 1.8));
        
        selectedPerson = people.get(0); // Default selection
    }
    
    private void addRoomBox(double x1, double y1, double z1, double x2, double y2, double z2, Color color) {
        // Bottom face
        roomLines.add(new Line3D(new Point3D(x1, y1, z1), new Point3D(x2, y1, z1), color));
        roomLines.add(new Line3D(new Point3D(x2, y1, z1), new Point3D(x2, y1, z2), color));
        roomLines.add(new Line3D(new Point3D(x2, y1, z2), new Point3D(x1, y1, z2), color));
        roomLines.add(new Line3D(new Point3D(x1, y1, z2), new Point3D(x1, y1, z1), color));
        
        // Vertical edges (walls)
        roomLines.add(new Line3D(new Point3D(x1, y1, z1), new Point3D(x1, y2, z1), color));
        roomLines.add(new Line3D(new Point3D(x2, y1, z1), new Point3D(x2, y2, z1), color));
        roomLines.add(new Line3D(new Point3D(x2, y1, z2), new Point3D(x2, y2, z2), color));
        roomLines.add(new Line3D(new Point3D(x1, y1, z2), new Point3D(x1, y2, z2), color));
        
        // Top face
        roomLines.add(new Line3D(new Point3D(x1, y2, z1), new Point3D(x2, y2, z1), color));
        roomLines.add(new Line3D(new Point3D(x2, y2, z1), new Point3D(x2, y2, z2), color));
        roomLines.add(new Line3D(new Point3D(x2, y2, z2), new Point3D(x1, y2, z2), color));
        roomLines.add(new Line3D(new Point3D(x1, y2, z2), new Point3D(x1, y2, z1), color));
    }
    
    private void addFloor(double x1, double y, double z1, double x2, double z2, Color color) {
        // Floor grid
        for (int i = (int)x1; i <= x2; i += 2) {
            roomLines.add(new Line3D(new Point3D(i, y, z1), new Point3D(i, y, z2), color));
        }
        for (int j = (int)z1; j <= z2; j += 2) {
            roomLines.add(new Line3D(new Point3D(x1, y, j), new Point3D(x2, y, j), color));
        }
    }
    
    private void addWindow(double x1, double y1, double z1, double x2, double y2, double z2, Color color) {
        roomLines.add(new Line3D(new Point3D(x1, y1, z1), new Point3D(x2, y1, z2), color));
        roomLines.add(new Line3D(new Point3D(x2, y1, z2), new Point3D(x2, y2, z2), color));
        roomLines.add(new Line3D(new Point3D(x2, y2, z2), new Point3D(x1, y2, z1), color));
        roomLines.add(new Line3D(new Point3D(x1, y2, z1), new Point3D(x1, y1, z1), color));
        
        // Window cross
        roomLines.add(new Line3D(new Point3D(x1, (y1+y2)/2, z1), new Point3D(x2, (y1+y2)/2, z2), color));
        roomLines.add(new Line3D(new Point3D(x1, y1, (z1+z2)/2), new Point3D(x2, y2, (z1+z2)/2), color));
    }
    
    private void addTable(double x, double y, double z, double w, double h, double d, Color color) {
        // Table top
        roomLines.add(new Line3D(new Point3D(x, y, z), new Point3D(x+w, y, z), color));
        roomLines.add(new Line3D(new Point3D(x+w, y, z), new Point3D(x+w, y, z+d), color));
        roomLines.add(new Line3D(new Point3D(x+w, y, z+d), new Point3D(x, y, z+d), color));
        roomLines.add(new Line3D(new Point3D(x, y, z+d), new Point3D(x, y, z), color));
        
        // Table legs
        roomLines.add(new Line3D(new Point3D(x, y-h, z), new Point3D(x, y, z), color));
        roomLines.add(new Line3D(new Point3D(x+w, y-h, z), new Point3D(x+w, y, z), color));
        roomLines.add(new Line3D(new Point3D(x+w, y-h, z+d), new Point3D(x+w, y, z+d), color));
        roomLines.add(new Line3D(new Point3D(x, y-h, z+d), new Point3D(x, y, z+d), color));
    }
    
    private void initializeGUI() {
        setTitle("3D Two-Story Room with Stairs and People");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null);
        
        createRenderPanel();
        createControls();
        
        getContentPane().setBackground(Color.BLACK);
        setFocusable(true);
    }
    
    private void createRenderPanel() {
        renderPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                render3D(g);
            }
        };
        renderPanel.setBackground(new Color(25, 25, 112)); // Midnight blue
        renderPanel.setPreferredSize(new Dimension(WIDTH, HEIGHT - 100));
        
        add(renderPanel, BorderLayout.CENTER);
    }
    
    private void createControls() {
        JPanel controlPanel = new JPanel(new GridLayout(3, 1));
        controlPanel.setBackground(new Color(64, 64, 64));
        
        // Rotation controls
        JPanel rotationPanel = new JPanel(new FlowLayout());
        rotationPanel.setBackground(new Color(64, 64, 64));
        
        JLabel hLabel = new JLabel("Horizontal View:");
        hLabel.setForeground(Color.WHITE);
        horizontalRotation = new JScrollBar(JScrollBar.HORIZONTAL, 0, 10, -180, 180);
        horizontalRotation.addAdjustmentListener(e -> {
            rotationY = Math.toRadians(horizontalRotation.getValue());
            renderPanel.repaint();
        });
        
        JLabel vLabel = new JLabel("Vertical View:");
        vLabel.setForeground(Color.WHITE);
        verticalRotation = new JScrollBar(JScrollBar.HORIZONTAL, 0, 10, -90, 90);
        verticalRotation.addAdjustmentListener(e -> {
            rotationX = Math.toRadians(verticalRotation.getValue());
            renderPanel.repaint();
        });
        
        rotationPanel.add(hLabel);
        rotationPanel.add(horizontalRotation);
        rotationPanel.add(vLabel);
        rotationPanel.add(verticalRotation);
        
        // Person selection
        JPanel personPanel = new JPanel(new FlowLayout());
        personPanel.setBackground(new Color(64, 64, 64));
        
        JLabel personLabel = new JLabel("Select Person:");
        personLabel.setForeground(Color.WHITE);
        
        JButton groundPersonBtn = new JButton("Ground Person (WASD)");
        groundPersonBtn.setBackground(Color.BLUE);
        groundPersonBtn.setForeground(Color.WHITE);
        groundPersonBtn.addActionListener(e -> {
            selectedPerson = people.get(0);
            updateStatus("Selected: " + selectedPerson.name + " (Use WASD to move)");
        });
        
        JButton upperPersonBtn = new JButton("Upper Person (Arrows)");
        upperPersonBtn.setBackground(Color.RED);
        upperPersonBtn.setForeground(Color.WHITE);
        upperPersonBtn.addActionListener(e -> {
            selectedPerson = people.get(1);
            updateStatus("Selected: " + selectedPerson.name + " (Use Arrow keys to move)");
        });
        
        personPanel.add(personLabel);
        personPanel.add(groundPersonBtn);
        personPanel.add(upperPersonBtn);
        
        // Status panel
        statusLabel = new JLabel("3D Two-Story Room | Selected: " + selectedPerson.name + " | Use WASD/Arrows to move people");
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        controlPanel.add(rotationPanel);
        controlPanel.add(personPanel);
        controlPanel.add(statusLabel);
        
        add(controlPanel, BorderLayout.SOUTH);
    }
    
    private void setupKeyBindings() {
        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                double moveSpeed = 0.5;
                
                switch (e.getKeyCode()) {
                    // WASD for ground person
                    case KeyEvent.VK_W:
                        if (selectedPerson == people.get(0)) {
                            selectedPerson.move(0, 0, -moveSpeed);
                        }
                        break;
                    case KeyEvent.VK_S:
                        if (selectedPerson == people.get(0)) {
                            selectedPerson.move(0, 0, moveSpeed);
                        }
                        break;
                    case KeyEvent.VK_A:
                        if (selectedPerson == people.get(0)) {
                            selectedPerson.move(-moveSpeed, 0, 0);
                        }
                        break;
                    case KeyEvent.VK_D:
                        if (selectedPerson == people.get(0)) {
                            selectedPerson.move(moveSpeed, 0, 0);
                        }
                        break;
                    
                    // Arrow keys for upper person
                    case KeyEvent.VK_UP:
                        if (selectedPerson == people.get(1)) {
                            selectedPerson.move(0, 0, -moveSpeed);
                        }
                        break;
                    case KeyEvent.VK_DOWN:
                        if (selectedPerson == people.get(1)) {
                            selectedPerson.move(0, 0, moveSpeed);
                        }
                        break;
                    case KeyEvent.VK_LEFT:
                        if (selectedPerson == people.get(1)) {
                            selectedPerson.move(-moveSpeed, 0, 0);
                        }
                        break;
                    case KeyEvent.VK_RIGHT:
                        if (selectedPerson == people.get(1)) {
                            selectedPerson.move(moveSpeed, 0, 0);
                        }
                        break;
                    
                    // Vertical movement
                    case KeyEvent.VK_Q: // Move up
                        selectedPerson.move(0, moveSpeed, 0);
                        break;
                    case KeyEvent.VK_E: // Move down
                        selectedPerson.move(0, -moveSpeed, 0);
                        break;
                }
                
                renderPanel.repaint();
                updateStatus("Moving " + selectedPerson.name + " - Position: (" + 
                    String.format("%.1f", selectedPerson.position.x) + ", " +
                    String.format("%.1f", selectedPerson.position.y) + ", " +
                    String.format("%.1f", selectedPerson.position.z) + ")");
            }
        });
    }
    
    private void render3D(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        
        // Draw room
        for (Line3D line : roomLines) {
            Point projected1 = project3D(line.start, centerX, centerY);
            Point projected2 = project3D(line.end, centerX, centerY);
            
            g2d.setColor(line.color);
            g2d.setStroke(new BasicStroke(2));
            g2d.drawLine(projected1.x, projected1.y, projected2.x, projected2.y);
        }
        
        // Draw people
        for (Person3D person : people) {
            drawPerson(g2d, person, centerX, centerY);
        }
        
        // Draw sunlight effect
        drawSunlight(g2d, centerX, centerY);
    }
    
    private void drawPerson(Graphics2D g2d, Person3D person, int centerX, int centerY) {
        // Person body
        Point3D head = new Point3D(person.position.x, person.position.y + person.size, person.position.z);
        Point3D feet = person.position;
        
        Point projectedHead = project3D(head, centerX, centerY);
        Point projectedFeet = project3D(feet, centerX, centerY);
        
        // Draw person as a line with circle head
        g2d.setColor(person.color);
        g2d.setStroke(new BasicStroke(4));
        g2d.drawLine(projectedFeet.x, projectedFeet.y, projectedHead.x, projectedHead.y);
        
        // Head
        g2d.fillOval(projectedHead.x - 8, projectedHead.y - 8, 16, 16);
        
        // Name label
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 12));
        g2d.drawString(person.name, projectedHead.x - 30, projectedHead.y - 15);
        
        // Selection indicator
        if (person == selectedPerson) {
            g2d.setColor(Color.YELLOW);
            g2d.setStroke(new BasicStroke(2));
            g2d.drawOval(projectedHead.x - 12, projectedHead.y - 12, 24, 24);
        }
    }
    
    private void drawSunlight(Graphics2D g2d, int centerX, int centerY) {
        // Sunlight rays through windows
        g2d.setColor(new Color(255, 255, 0, 50));
        for (int i = 0; i < 5; i++) {
            Point3D lightStart = new Point3D(-10, 6, -4 + i);
            Point3D lightEnd = new Point3D(-5, 1, -2 + i);
            
            Point start = project3D(lightStart, centerX, centerY);
            Point end = project3D(lightEnd, centerX, centerY);
            
            g2d.setStroke(new BasicStroke(3));
            g2d.drawLine(start.x, start.y, end.x, end.y);
        }
    }
    
    private Point project3D(Point3D point3d, int centerX, int centerY) {
        // Apply rotation
        double cosX = Math.cos(rotationX), sinX = Math.sin(rotationX);
        double cosY = Math.cos(rotationY), sinY = Math.sin(rotationY);
        
        // Translate relative to camera
        double x = point3d.x - cameraX;
        double y = point3d.y - cameraY;
        double z = point3d.z - cameraZ;
        
        // Rotate around Y axis (horizontal)
        double tempX = x * cosY - z * sinY;
        double tempZ = x * sinY + z * cosY;
        x = tempX;
        z = tempZ;
        
        // Rotate around X axis (vertical)
        double tempY = y * cosX - z * sinX;
        tempZ = y * sinX + z * cosX;
        y = tempY;
        z = tempZ;
        
        // Perspective projection
        double perspective = 400;
        double scale = perspective / (perspective + z);
        
        int screenX = (int) (centerX + x * scale * 20);
        int screenY = (int) (centerY - y * scale * 20);
        
        return new Point(screenX, screenY);
    }
    
    private void updateStatus(String message) {
        statusLabel.setText(message);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new tut8().setVisible(true);
        });
    }
}
