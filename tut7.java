import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

// 3D Point class
class Point3D {
    double x, y, z;
    
    public Point3D(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    // Rotate around Y axis (horizontal rotation)
    public Point3D rotateY(double angle) {
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        return new Point3D(
            x * cos - z * sin,
            y,
            x * sin + z * cos
        );
    }
    
    // Rotate around X axis (vertical rotation)
    public Point3D rotateX(double angle) {
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        return new Point3D(
            x,
            y * cos - z * sin,
            y * sin + z * cos
        );
    }
    
    // Project to 2D screen coordinates
    public Point project(int screenWidth, int screenHeight, double distance) {
        double factor = distance / (distance + z);
        int screenX = (int) (screenWidth / 2 + x * factor);
        int screenY = (int) (screenHeight / 2 - y * factor);
        return new Point(screenX, screenY);
    }
}

// 3D Face class for drawing surfaces
class Face3D {
    Point3D[] vertices;
    Color color;
    boolean isFloor;
    
    public Face3D(Point3D[] vertices, Color color, boolean isFloor) {
        this.vertices = vertices;
        this.color = color;
        this.isFloor = isFloor;
    }
    
    // Calculate average Z for depth sorting
    public double getAverageZ() {
        double sum = 0;
        for (Point3D vertex : vertices) {
            sum += vertex.z;
        }
        return sum / vertices.length;
    }
}

public class tut7 extends JFrame {
    private static final int WINDOW_WIDTH = 800;
    private static final int WINDOW_HEIGHT = 600;
    private static final double PROJECTION_DISTANCE = 500;
    
    private JPanel canvas;
    private JScrollBar horizontalRotation;
    private JScrollBar verticalRotation;
    private JLabel infoLabel;
    
    private double rotationY = 0; // Horizontal rotation
    private double rotationX = 0; // Vertical rotation
    
    private java.util.List<Face3D> roomFaces;
    private java.util.List<Face3D> tableFaces;
    
    public tut7() {
        initializeRoom();
        initializeGUI();
    }
    
    private void initializeRoom() {
        roomFaces = new ArrayList<>();
        tableFaces = new ArrayList<>();
        
        // Create room (walls, floor, ceiling)
        createRoom();
        
        // Create table
        createTable();
    }
    
    private void createRoom() {
        double roomSize = 200;
        double roomHeight = 150;
        
        // Floor (with wood texture color)
        Point3D[] floor = {
            new Point3D(-roomSize, -roomHeight, -roomSize),
            new Point3D(roomSize, -roomHeight, -roomSize),
            new Point3D(roomSize, -roomHeight, roomSize),
            new Point3D(-roomSize, -roomHeight, roomSize)
        };
        roomFaces.add(new Face3D(floor, new Color(139, 69, 19), true)); // Brown wood floor
        
        // Ceiling
        Point3D[] ceiling = {
            new Point3D(-roomSize, roomHeight, -roomSize),
            new Point3D(-roomSize, roomHeight, roomSize),
            new Point3D(roomSize, roomHeight, roomSize),
            new Point3D(roomSize, roomHeight, -roomSize)
        };
        roomFaces.add(new Face3D(ceiling, new Color(245, 245, 220), false)); // Beige ceiling
        
        // Back wall
        Point3D[] backWall = {
            new Point3D(-roomSize, -roomHeight, -roomSize),
            new Point3D(-roomSize, roomHeight, -roomSize),
            new Point3D(roomSize, roomHeight, -roomSize),
            new Point3D(roomSize, -roomHeight, -roomSize)
        };
        roomFaces.add(new Face3D(backWall, new Color(176, 196, 222), false)); // Light blue wall
        
        // Left wall
        Point3D[] leftWall = {
            new Point3D(-roomSize, -roomHeight, -roomSize),
            new Point3D(-roomSize, -roomHeight, roomSize),
            new Point3D(-roomSize, roomHeight, roomSize),
            new Point3D(-roomSize, roomHeight, -roomSize)
        };
        roomFaces.add(new Face3D(leftWall, new Color(221, 160, 221), false)); // Plum wall
        
        // Right wall (with window for sunlight)
        Point3D[] rightWall = {
            new Point3D(roomSize, -roomHeight, -roomSize),
            new Point3D(roomSize, roomHeight, -roomSize),
            new Point3D(roomSize, roomHeight, roomSize),
            new Point3D(roomSize, -roomHeight, roomSize)
        };
        roomFaces.add(new Face3D(rightWall, new Color(255, 228, 181), false)); // Moccasin wall
    }
    
    private void createTable() {
        double tableWidth = 60;
        double tableLength = 40;
        double tableHeight = 30;
        double legHeight = 25;
        double legThickness = 3;
        
        // Table top
        Point3D[] tableTop = {
            new Point3D(-tableWidth/2, -tableHeight, -tableLength/2),
            new Point3D(tableWidth/2, -tableHeight, -tableLength/2),
            new Point3D(tableWidth/2, -tableHeight, tableLength/2),
            new Point3D(-tableWidth/2, -tableHeight, tableLength/2)
        };
        tableFaces.add(new Face3D(tableTop, new Color(160, 82, 45), false)); // Saddle brown table
        
        // Table legs (4 legs)
        double legOffsetX = tableWidth/2 - legThickness;
        double legOffsetZ = tableLength/2 - legThickness;
        
        // Front left leg
        createTableLeg(-legOffsetX, -legOffsetZ, legThickness, legHeight, tableHeight);
        // Front right leg
        createTableLeg(legOffsetX, -legOffsetZ, legThickness, legHeight, tableHeight);
        // Back left leg
        createTableLeg(-legOffsetX, legOffsetZ, legThickness, legHeight, tableHeight);
        // Back right leg
        createTableLeg(legOffsetX, legOffsetZ, legThickness, legHeight, tableHeight);
    }
    
    private void createTableLeg(double x, double z, double thickness, double height, double tableHeight) {
        // Each leg is a rectangular prism
        double legTop = -tableHeight;
        double legBottom = -tableHeight - height;
        
        // Front face
        Point3D[] front = {
            new Point3D(x - thickness/2, legTop, z - thickness/2),
            new Point3D(x + thickness/2, legTop, z - thickness/2),
            new Point3D(x + thickness/2, legBottom, z - thickness/2),
            new Point3D(x - thickness/2, legBottom, z - thickness/2)
        };
        tableFaces.add(new Face3D(front, new Color(101, 67, 33), false)); // Dark brown leg
        
        // Back face
        Point3D[] back = {
            new Point3D(x + thickness/2, legTop, z + thickness/2),
            new Point3D(x - thickness/2, legTop, z + thickness/2),
            new Point3D(x - thickness/2, legBottom, z + thickness/2),
            new Point3D(x + thickness/2, legBottom, z + thickness/2)
        };
        tableFaces.add(new Face3D(back, new Color(101, 67, 33), false));
        
        // Left face
        Point3D[] left = {
            new Point3D(x - thickness/2, legTop, z + thickness/2),
            new Point3D(x - thickness/2, legTop, z - thickness/2),
            new Point3D(x - thickness/2, legBottom, z - thickness/2),
            new Point3D(x - thickness/2, legBottom, z + thickness/2)
        };
        tableFaces.add(new Face3D(left, new Color(101, 67, 33), false));
        
        // Right face
        Point3D[] right = {
            new Point3D(x + thickness/2, legTop, z - thickness/2),
            new Point3D(x + thickness/2, legTop, z + thickness/2),
            new Point3D(x + thickness/2, legBottom, z + thickness/2),
            new Point3D(x + thickness/2, legBottom, z - thickness/2)
        };
        tableFaces.add(new Face3D(right, new Color(101, 67, 33), false));
    }
    
    private void initializeGUI() {
        setTitle("3D Room with Table and Sunlight");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT + 100);
        setLocationRelativeTo(null);
        
        // Create canvas for 3D rendering
        canvas = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                render3D(g);
            }
        };
        canvas.setBackground(new Color(135, 206, 235)); // Sky blue background
        canvas.setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
        
        // Create control panel
        JPanel controlPanel = new JPanel(new GridLayout(3, 1));
        controlPanel.setBackground(new Color(70, 70, 70));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Horizontal rotation control
        JPanel hPanel = new JPanel(new FlowLayout());
        hPanel.setBackground(new Color(70, 70, 70));
        JLabel hLabel = new JLabel("Horizontal View:");
        hLabel.setForeground(Color.WHITE);
        hLabel.setFont(new Font("Arial", Font.BOLD, 12));
        
        horizontalRotation = new JScrollBar(JScrollBar.HORIZONTAL, 0, 10, -180, 180);
        horizontalRotation.addAdjustmentListener(e -> {
            rotationY = Math.toRadians(horizontalRotation.getValue());
            canvas.repaint();
        });
        
        hPanel.add(hLabel);
        hPanel.add(horizontalRotation);
        
        // Vertical rotation control
        JPanel vPanel = new JPanel(new FlowLayout());
        vPanel.setBackground(new Color(70, 70, 70));
        JLabel vLabel = new JLabel("Vertical View:");
        vLabel.setForeground(Color.WHITE);
        vLabel.setFont(new Font("Arial", Font.BOLD, 12));
        
        verticalRotation = new JScrollBar(JScrollBar.HORIZONTAL, 0, 10, -90, 90);
        verticalRotation.addAdjustmentListener(e -> {
            rotationX = Math.toRadians(verticalRotation.getValue());
            canvas.repaint();
        });
        
        vPanel.add(vLabel);
        vPanel.add(verticalRotation);
        
        // Info panel
        JPanel infoPanel = new JPanel(new FlowLayout());
        infoPanel.setBackground(new Color(70, 70, 70));
        infoLabel = new JLabel("🏠 3D Room Explorer - Use scrollbars to look around! 🌞");
        infoLabel.setForeground(Color.YELLOW);
        infoLabel.setFont(new Font("Arial", Font.BOLD, 14));
        infoPanel.add(infoLabel);
        
        controlPanel.add(hPanel);
        controlPanel.add(vPanel);
        controlPanel.add(infoPanel);
        
        add(canvas, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);
        
        // Initial render
        canvas.repaint();
    }
    
    private void render3D(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Clear canvas
        g2d.setColor(new Color(135, 206, 235)); // Sky blue
        g2d.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        
        // Combine all faces for depth sorting
        java.util.List<Face3D> allFaces = new ArrayList<>();
        allFaces.addAll(roomFaces);
        allFaces.addAll(tableFaces);
        
        // Transform and sort faces by depth
        java.util.List<TransformedFace> transformedFaces = new ArrayList<>();
        
        for (Face3D face : allFaces) {
            Point3D[] transformedVertices = new Point3D[face.vertices.length];
            for (int i = 0; i < face.vertices.length; i++) {
                Point3D vertex = face.vertices[i];
                // Apply rotations
                vertex = vertex.rotateY(rotationY);
                vertex = vertex.rotateX(rotationX);
                transformedVertices[i] = vertex;
            }
            
            TransformedFace tFace = new TransformedFace(transformedVertices, face.color, face.isFloor);
            transformedFaces.add(tFace);
        }
        
        // Sort by average Z (back to front)
        transformedFaces.sort((a, b) -> Double.compare(a.getAverageZ(), b.getAverageZ()));
        
        // Draw faces
        for (TransformedFace face : transformedFaces) {
            drawFace(g2d, face);
        }
        
        // Draw sunlight effect
        drawSunlight(g2d);
    }
    
    private void drawFace(Graphics2D g2d, TransformedFace face) {
        Point[] screenPoints = new Point[face.vertices.length];
        int[] xPoints = new int[face.vertices.length];
        int[] yPoints = new int[face.vertices.length];
        
        for (int i = 0; i < face.vertices.length; i++) {
            screenPoints[i] = face.vertices[i].project(canvas.getWidth(), canvas.getHeight(), PROJECTION_DISTANCE);
            xPoints[i] = screenPoints[i].x;
            yPoints[i] = screenPoints[i].y;
        }
        
        // Apply sunlight shading
        Color shadedColor = applySunlightShading(face.color, face.isFloor);
        g2d.setColor(shadedColor);
        g2d.fillPolygon(xPoints, yPoints, face.vertices.length);
        
        // Draw edges
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(1));
        g2d.drawPolygon(xPoints, yPoints, face.vertices.length);
    }
    
    private Color applySunlightShading(Color baseColor, boolean isFloor) {
        // Simulate sunlight coming from upper right
        double lightIntensity = isFloor ? 0.8 : 0.6; // Floor gets more light
        
        int r = (int) Math.min(255, baseColor.getRed() * lightIntensity + 50);
        int g = (int) Math.min(255, baseColor.getGreen() * lightIntensity + 50);
        int b = (int) Math.min(255, baseColor.getBlue() * lightIntensity + 30);
        
        return new Color(r, g, b);
    }
    
    private void drawSunlight(Graphics2D g2d) {
        // Draw sunlight rays effect
        g2d.setColor(new Color(255, 255, 0, 50)); // Semi-transparent yellow
        
        int centerX = canvas.getWidth() - 100;
        int centerY = 100;
        
        for (int i = 0; i < 8; i++) {
            double angle = i * Math.PI / 4;
            int x2 = centerX + (int) (60 * Math.cos(angle));
            int y2 = centerY + (int) (60 * Math.sin(angle));
            
            g2d.setStroke(new BasicStroke(3));
            g2d.drawLine(centerX, centerY, x2, y2);
        }
        
        // Draw sun
        g2d.setColor(new Color(255, 255, 0, 100));
        g2d.fillOval(centerX - 15, centerY - 15, 30, 30);
    }
    
    // Helper class for transformed faces
    class TransformedFace {
        Point3D[] vertices;
        Color color;
        boolean isFloor;
        
        public TransformedFace(Point3D[] vertices, Color color, boolean isFloor) {
            this.vertices = vertices;
            this.color = color;
            this.isFloor = isFloor;
        }
        
        public double getAverageZ() {
            double sum = 0;
            for (Point3D vertex : vertices) {
                sum += vertex.z;
            }
            return sum / vertices.length;
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new tut7().setVisible(true);
        });
    }
}
