import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;

// 3D Geometry helper classes
class Point3D {
    double x, y, z;
    
    Point3D(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
}

class Vector3D {
    double x, y, z;
    
    Vector3D(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    Vector3D normalize() {
        double length = Math.sqrt(x*x + y*y + z*z);
        return new Vector3D(x/length, y/length, z/length);
    }
    
    double dot(Vector3D other) {
        return x*other.x + y*other.y + z*other.z;
    }
    
    Vector3D cross(Vector3D other) {
        return new Vector3D(
            y*other.z - z*other.y,
            z*other.x - x*other.z,
            x*other.y - y*other.x
        );
    }
}

class Triangle3D {
    Point3D p1, p2, p3;
    
    Triangle3D(Point3D p1, Point3D p2, Point3D p3) {
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;
    }
}

public class tut10 extends JPanel implements MouseMotionListener, KeyListener {
    private static final int WINDOW_WIDTH = 1200;
    private static final int WINDOW_HEIGHT = 800;
    private static final double CAMERA_DISTANCE = 10;
    
    // 3D Points for human model
    private ArrayList<Point3D> bodyPoints;
    private ArrayList<Triangle3D> bodyTriangles;
    private double rotationX = 0;
    private double rotationY = 0;
    private double rotationZ = 0;
    private Point prevMouse = null;
    
    // Character position and movement
    private double characterX = 0;
    private double characterY = 0;
    private double characterZ = 0;
    private double walkingAnimation = 0;
    private boolean isWalking = false;
    
    public tut10() {
        setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
        setBackground(Color.BLACK);
        addMouseMotionListener(this);
        addKeyListener(this);
        setFocusable(true);
        
        initializeHumanModel();
        
        // Start animation timer
        javax.swing.Timer timer = new javax.swing.Timer(16, e -> {
            updateAnimation();
            repaint();
        });
        timer.start();
    }
    
    private void initializeHumanModel() {
        bodyPoints = new ArrayList<>();
        bodyTriangles = new ArrayList<>();
        
        // Head
        createSphere(0, 2.5, 0, 0.4, 12, 12); // Head sphere
        
        // Torso
        createTorso(0, 1.3, 0, 0.8, 1.5);
        
        // Arms
        createLimb(-0.8, 1.8, 0, -1.2, 1.0, 0, 0.15); // Left upper arm
        createLimb(-1.2, 1.0, 0, -1.2, 0.2, 0, 0.12); // Left lower arm
        createLimb(0.8, 1.8, 0, 1.2, 1.0, 0, 0.15);  // Right upper arm
        createLimb(1.2, 1.0, 0, 1.2, 0.2, 0, 0.12);  // Right lower arm
        
        // Legs
        createLimb(-0.3, 0.5, 0, -0.3, -0.5, 0, 0.2);  // Left upper leg
        createLimb(-0.3, -0.5, 0, -0.3, -1.5, 0, 0.15); // Left lower leg
        createLimb(0.3, 0.5, 0, 0.3, -0.5, 0, 0.2);   // Right upper leg
        createLimb(0.3, -0.5, 0, 0.3, -1.5, 0, 0.15);  // Right lower leg
        
        // Feet
        createBox(-0.3, -1.6, 0.1, 0.2, 0.1, 0.4); // Left foot
        createBox(0.3, -1.6, 0.1, 0.2, 0.1, 0.4);  // Right foot
        
        // Hands
        createSphere(-1.2, 0.1, 0, 0.12, 8, 8); // Left hand
        createSphere(1.2, 0.1, 0, 0.12, 8, 8);  // Right hand
    }
    
    private void createSphere(double centerX, double centerY, double centerZ, 
                            double radius, int latitudes, int longitudes) {
        ArrayList<Point3D> spherePoints = new ArrayList<>();
        
        // Generate sphere points
        for (int lat = 0; lat <= latitudes; lat++) {
            double theta = lat * Math.PI / latitudes;
            double sinTheta = Math.sin(theta);
            double cosTheta = Math.cos(theta);
            
            for (int lon = 0; lon <= longitudes; lon++) {
                double phi = lon * 2 * Math.PI / longitudes;
                double sinPhi = Math.sin(phi);
                double cosPhi = Math.cos(phi);
                
                double x = centerX + radius * sinTheta * cosPhi;
                double y = centerY + radius * cosTheta;
                double z = centerZ + radius * sinTheta * sinPhi;
                
                spherePoints.add(new Point3D(x, y, z));
            }
        }
        
        // Create triangles
        for (int lat = 0; lat < latitudes; lat++) {
            for (int lon = 0; lon < longitudes; lon++) {
                int current = lat * (longitudes + 1) + lon;
                int next = current + 1;
                int below = current + (longitudes + 1);
                int belowNext = below + 1;
                
                bodyTriangles.add(new Triangle3D(
                    spherePoints.get(current),
                    spherePoints.get(next),
                    spherePoints.get(below)
                ));
                
                bodyTriangles.add(new Triangle3D(
                    spherePoints.get(next),
                    spherePoints.get(belowNext),
                    spherePoints.get(below)
                ));
            }
        }
        
        bodyPoints.addAll(spherePoints);
    }
    
    private void createTorso(double centerX, double centerY, double centerZ,
                           double width, double height) {
        // Create torso as a tapered cylinder
        int segments = 12;
        ArrayList<Point3D> upperPoints = new ArrayList<>();
        ArrayList<Point3D> lowerPoints = new ArrayList<>();
        
        for (int i = 0; i <= segments; i++) {
            double angle = i * 2 * Math.PI / segments;
            double cos = Math.cos(angle);
            double sin = Math.sin(angle);
            
            // Upper torso (wider)
            upperPoints.add(new Point3D(
                centerX + width * cos,
                centerY + height/2,
                centerZ + width * sin
            ));
            
            // Lower torso (narrower)
            lowerPoints.add(new Point3D(
                centerX + (width * 0.7) * cos,
                centerY - height/2,
                centerZ + (width * 0.7) * sin
            ));
        }
        
        // Create triangles
        for (int i = 0; i < segments; i++) {
            bodyTriangles.add(new Triangle3D(
                upperPoints.get(i),
                upperPoints.get(i + 1),
                lowerPoints.get(i)
            ));
            
            bodyTriangles.add(new Triangle3D(
                upperPoints.get(i + 1),
                lowerPoints.get(i + 1),
                lowerPoints.get(i)
            ));
        }
        
        bodyPoints.addAll(upperPoints);
        bodyPoints.addAll(lowerPoints);
    }
    
    private void createLimb(double x1, double y1, double z1,
                          double x2, double y2, double z2, double radius) {
        int segments = 8;
        ArrayList<Point3D> upperRing = new ArrayList<>();
        ArrayList<Point3D> lowerRing = new ArrayList<>();
        
        // Calculate limb direction
        double dx = x2 - x1;
        double dy = y2 - y1;
        double dz = z2 - z1;
        
        // Create rings of points at each end
        for (int i = 0; i <= segments; i++) {
            double angle = i * 2 * Math.PI / segments;
            double cos = Math.cos(angle);
            double sin = Math.sin(angle);
            
            // Upper ring
            upperRing.add(new Point3D(
                x1 + radius * cos,
                y1 + radius * sin,
                z1
            ));
            
            // Lower ring
            lowerRing.add(new Point3D(
                x2 + radius * cos,
                y2 + radius * sin,
                z2
            ));
        }
        
        // Create triangles between rings
        for (int i = 0; i < segments; i++) {
            bodyTriangles.add(new Triangle3D(
                upperRing.get(i),
                upperRing.get(i + 1),
                lowerRing.get(i)
            ));
            
            bodyTriangles.add(new Triangle3D(
                upperRing.get(i + 1),
                lowerRing.get(i + 1),
                lowerRing.get(i)
            ));
        }
        
        bodyPoints.addAll(upperRing);
        bodyPoints.addAll(lowerRing);
    }
    
    private void createBox(double x, double y, double z, 
                         double width, double height, double depth) {
        Point3D[] corners = new Point3D[8];
        
        // Create 8 corners of the box
        corners[0] = new Point3D(x - width/2, y - height/2, z - depth/2);
        corners[1] = new Point3D(x + width/2, y - height/2, z - depth/2);
        corners[2] = new Point3D(x + width/2, y + height/2, z - depth/2);
        corners[3] = new Point3D(x - width/2, y + height/2, z - depth/2);
        corners[4] = new Point3D(x - width/2, y - height/2, z + depth/2);
        corners[5] = new Point3D(x + width/2, y - height/2, z + depth/2);
        corners[6] = new Point3D(x + width/2, y + height/2, z + depth/2);
        corners[7] = new Point3D(x - width/2, y + height/2, z + depth/2);
        
        // Front face
        bodyTriangles.add(new Triangle3D(corners[0], corners[1], corners[2]));
        bodyTriangles.add(new Triangle3D(corners[0], corners[2], corners[3]));
        
        // Back face
        bodyTriangles.add(new Triangle3D(corners[5], corners[4], corners[7]));
        bodyTriangles.add(new Triangle3D(corners[5], corners[7], corners[6]));
        
        // Top face
        bodyTriangles.add(new Triangle3D(corners[3], corners[2], corners[6]));
        bodyTriangles.add(new Triangle3D(corners[3], corners[6], corners[7]));
        
        // Bottom face
        bodyTriangles.add(new Triangle3D(corners[4], corners[5], corners[1]));
        bodyTriangles.add(new Triangle3D(corners[4], corners[1], corners[0]));
        
        // Left face
        bodyTriangles.add(new Triangle3D(corners[4], corners[0], corners[3]));
        bodyTriangles.add(new Triangle3D(corners[4], corners[3], corners[7]));
        
        // Right face
        bodyTriangles.add(new Triangle3D(corners[1], corners[5], corners[6]));
        bodyTriangles.add(new Triangle3D(corners[1], corners[6], corners[2]));
        
        Collections.addAll(bodyPoints, corners);
    }
    
    private void updateAnimation() {
        if (isWalking) {
            walkingAnimation += 0.1;
            // Animate legs and arms while walking
            updateWalkingPose(walkingAnimation);
        }
    }
    
    private void updateWalkingPose(double angle) {
        // Update leg positions for walking animation
        double legSwing = Math.sin(angle) * 0.5;
        double armSwing = -Math.sin(angle) * 0.3;
        
        // Left leg
        updateLimbPosition(bodyPoints, 30, 35, legSwing);
        // Right leg
        updateLimbPosition(bodyPoints, 36, 41, -legSwing);
        // Left arm
        updateLimbPosition(bodyPoints, 12, 17, armSwing);
        // Right arm
        updateLimbPosition(bodyPoints, 18, 23, -armSwing);
    }
    
    private void updateLimbPosition(ArrayList<Point3D> points, 
                                  int startIndex, int endIndex, double angle) {
        // Rotate points around their base
        Point3D base = points.get(startIndex);
        for (int i = startIndex + 1; i <= endIndex; i++) {
            Point3D p = points.get(i);
            double dx = p.x - base.x;
            double dy = p.y - base.y;
            
            double rotatedX = dx * Math.cos(angle) - dy * Math.sin(angle);
            double rotatedY = dx * Math.sin(angle) + dy * Math.cos(angle);
            
            p.x = base.x + rotatedX;
            p.y = base.y + rotatedY;
        }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Set up lighting
        Color ambientColor = new Color(50, 50, 50);
        Color diffuseColor = new Color(200, 200, 200);
        Vector3D lightDirection = new Vector3D(-1, -1, -1).normalize();
        
        // Create copy of triangles for sorting
        ArrayList<Triangle3D> sortedTriangles = new ArrayList<>(bodyTriangles);
        
        // Transform all points
        ArrayList<Point2D> projectedPoints = new ArrayList<>();
        for (Point3D p : bodyPoints) {
            // Apply character position offset
            double x = p.x + characterX;
            double y = p.y + characterY;
            double z = p.z + characterZ;
            
            // Apply rotations
            double[] rotated = rotate(x, y, z, rotationX, rotationY, rotationZ);
            
            // Project to 2D
            Point2D projected = project(rotated[0], rotated[1], rotated[2]);
            projectedPoints.add(projected);
        }
        
        // Sort triangles back-to-front
        sortedTriangles.sort((t1, t2) -> {
            double z1 = (t1.p1.z + t1.p2.z + t1.p3.z) / 3;
            double z2 = (t2.p1.z + t2.p2.z + t2.p3.z) / 3;
            return Double.compare(z2, z1);
        });
        
        // Draw all triangles with shading
        for (Triangle3D triangle : sortedTriangles) {
            // Calculate normal vector for lighting
            Vector3D v1 = new Vector3D(
                triangle.p2.x - triangle.p1.x,
                triangle.p2.y - triangle.p1.y,
                triangle.p2.z - triangle.p1.z
            );
            
            Vector3D v2 = new Vector3D(
                triangle.p3.x - triangle.p1.x,
                triangle.p3.y - triangle.p1.y,
                triangle.p3.z - triangle.p1.z
            );
            
            Vector3D normal = v1.cross(v2).normalize();
            
            // Calculate lighting
            double diffuse = Math.max(0, normal.dot(lightDirection));
            
            // Combine ambient and diffuse lighting
            int red = (int)(ambientColor.getRed() + diffuse * diffuseColor.getRed());
            int green = (int)(ambientColor.getGreen() + diffuse * diffuseColor.getGreen());
            int blue = (int)(ambientColor.getBlue() + diffuse * diffuseColor.getBlue());
            
            red = Math.min(255, Math.max(0, red));
            green = Math.min(255, Math.max(0, green));
            blue = Math.min(255, Math.max(0, blue));
            
            g2d.setColor(new Color(red, green, blue));
            
            // Project and draw triangle
            Point2D p1 = project(triangle.p1.x + characterX, triangle.p1.y + characterY, triangle.p1.z + characterZ);
            Point2D p2 = project(triangle.p2.x + characterX, triangle.p2.y + characterY, triangle.p2.z + characterZ);
            Point2D p3 = project(triangle.p3.x + characterX, triangle.p3.y + characterY, triangle.p3.z + characterZ);
            
            int[] xPoints = {(int)p1.getX(), (int)p2.getX(), (int)p3.getX()};
            int[] yPoints = {(int)p1.getY(), (int)p2.getY(), (int)p3.getY()};
            
            g2d.fillPolygon(xPoints, yPoints, 3);
            g2d.setColor(Color.BLACK);
            g2d.drawPolygon(xPoints, yPoints, 3);
        }
        
        // Draw UI
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.PLAIN, 14));
        g2d.drawString("Use mouse to rotate. WASD to move. Space to jump.", 10, 20);
    }
    
    private Point2D project(double x, double y, double z) {
        // Apply perspective projection
        double factor = CAMERA_DISTANCE / (z + CAMERA_DISTANCE + 8);
        double px = x * factor * 100 + WINDOW_WIDTH/2;
        double py = -y * factor * 100 + WINDOW_HEIGHT/2;
        return new Point2D.Double(px, py);
    }
    
    private double[] rotate(double x, double y, double z,
                          double angleX, double angleY, double angleZ) {
        // Rotate around X axis
        double newY = y * Math.cos(angleX) - z * Math.sin(angleX);
        double newZ = y * Math.sin(angleX) + z * Math.cos(angleX);
        y = newY;
        z = newZ;
        
        // Rotate around Y axis
        double newX = x * Math.cos(angleY) + z * Math.sin(angleY);
        newZ = -x * Math.sin(angleY) + z * Math.cos(angleY);
        x = newX;
        z = newZ;
        
        // Rotate around Z axis
        newX = x * Math.cos(angleZ) - y * Math.sin(angleZ);
        newY = x * Math.sin(angleZ) + y * Math.cos(angleZ);
        x = newX;
        y = newY;
        
        return new double[]{x, y, z};
    }
    
    @Override
    public void mouseDragged(MouseEvent e) {
        if (prevMouse != null) {
            double dx = e.getX() - prevMouse.x;
            double dy = e.getY() - prevMouse.y;
            
            rotationY += dx * 0.01;
            rotationX += dy * 0.01;
            
            // Limit vertical rotation
            rotationX = Math.max(-Math.PI/2, Math.min(Math.PI/2, rotationX));
        }
        prevMouse = e.getPoint();
    }
    
    @Override
    public void mouseMoved(MouseEvent e) {
        prevMouse = e.getPoint();
    }
    
    @Override
    public void keyPressed(KeyEvent e) {
        double moveSpeed = 0.1;
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W:
                characterZ -= moveSpeed * Math.cos(rotationY);
                characterX -= moveSpeed * Math.sin(rotationY);
                isWalking = true;
                break;
            case KeyEvent.VK_S:
                characterZ += moveSpeed * Math.cos(rotationY);
                characterX += moveSpeed * Math.sin(rotationY);
                isWalking = true;
                break;
            case KeyEvent.VK_A:
                characterX -= moveSpeed * Math.cos(rotationY);
                characterZ += moveSpeed * Math.sin(rotationY);
                isWalking = true;
                break;
            case KeyEvent.VK_D:
                characterX += moveSpeed * Math.cos(rotationY);
                characterZ -= moveSpeed * Math.sin(rotationY);
                isWalking = true;
                break;
            case KeyEvent.VK_SPACE:
                // Add jumping logic here if desired
                break;
        }
    }
    
    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W:
            case KeyEvent.VK_S:
            case KeyEvent.VK_A:
            case KeyEvent.VK_D:
                isWalking = false;
                break;
        }
    }
    
    @Override
    public void keyTyped(KeyEvent e) {}
    
    // Helper classes
    static class Point3D {
        double x, y, z;
        
        Point3D(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }
    
    static class Vector3D {
        double x, y, z;
        
        Vector3D(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
        
        Vector3D normalize() {
            double length = Math.sqrt(x*x + y*y + z*z);
            return new Vector3D(x/length, y/length, z/length);
        }
        
        double dot(Vector3D other) {
            return x*other.x + y*other.y + z*other.z;
        }
        
        Vector3D cross(Vector3D other) {
            return new Vector3D(
                y*other.z - z*other.y,
                z*other.x - x*other.z,
                x*other.y - y*other.x
            );
        }
    }
    
    static class Triangle3D {
        Point3D p1, p2, p3;
        
        Triangle3D(Point3D p1, Point3D p2, Point3D p3) {
            this.p1 = p1;
            this.p2 = p2;
            this.p3 = p3;
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("3D Human Model");
            tut10 panel = new tut10();
            
            frame.add(panel);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            panel.requestFocusInWindow();
        });
    }
}
