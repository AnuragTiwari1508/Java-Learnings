import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AnuragResume3DEnhanced extends JFrame {
    private JPanel contentPanel;
    private CardLayout cardLayout;
    private javax.swing.Timer animationTimer;
    private float time = 0;
    private int currentSection = 0;
    private Random random = new Random();
    
    // 3D Graphics variables
    private BufferedImage offscreenBuffer;
    private Graphics2D offscreenGraphics;
    
    // Particle system
    private List<Particle> particles = new ArrayList<>();
    
    // Colors
    private Color[] neonColors = {
        new Color(0, 255, 255),    // Cyan
        new Color(255, 0, 255),    // Magenta  
        new Color(255, 255, 0),    // Yellow
        new Color(0, 255, 0),      // Green
        new Color(255, 100, 255),  // Pink
        new Color(100, 255, 255)   // Light Blue
    };
    
    public AnuragResume3DEnhanced() {
        setupUI();
        initializeParticles();
        startAnimations();
    }
    
    private void setupUI() {
        setTitle("Anurag Tiwari - Ultimate 3D Resume Experience");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true); // Full immersive experience
        
        // Create double-buffered graphics
        GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment()
            .getDefaultScreenDevice().getDefaultConfiguration();
        
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                paintMainContent((Graphics2D) g);
            }
        };
        contentPanel.setBackground(Color.BLACK);
        
        // Add sections
        contentPanel.add(createSection("HOME"), "HOME");
        contentPanel.add(createSection("ABOUT"), "ABOUT");
        contentPanel.add(createSection("SKILLS"), "SKILLS");
        contentPanel.add(createSection("PROJECTS"), "PROJECTS");
        contentPanel.add(createSection("EXPERIENCE"), "EXPERIENCE");
        contentPanel.add(createSection("ACHIEVEMENTS"), "ACHIEVEMENTS");
        contentPanel.add(createSection("CONTACT"), "CONTACT");
        
        add(contentPanel);
        
        // Input handling
        setupInputHandling();
        
        setFocusable(true);
        requestFocusInWindow();
    }
    
    private void setupInputHandling() {
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleInput(e);
            }
        });
        
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Navigate sections with mouse clicks
                if (e.getX() < getWidth() / 2) {
                    navigateLeft();
                } else {
                    navigateRight();
                }
            }
        });
        
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                // Add interactive particles at mouse position
                addParticleAtMouse(e.getX(), e.getY());
            }
        });
    }
    
    private JPanel createSection(String sectionName) {
        return new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                // Transparent background - main painting done in parent
                Graphics2D g2d = (Graphics2D) g;
                g2d.setComposite(AlphaComposite.Clear);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.setComposite(AlphaComposite.SrcOver);
            }
        };
    }
    
    private void paintMainContent(Graphics2D g2d) {
        setupRenderingHints(g2d);
        
        // Paint dynamic background
        paintQuantumBackground(g2d);
        
        // Paint particles
        paintParticles(g2d);
        
        // Paint 3D holographic effects
        paintHolographicEffects(g2d);
        
        // Paint section content
        paintCurrentSection(g2d);
        
        // Paint UI elements
        paintUI(g2d);
    }
    
    private void setupRenderingHints(Graphics2D g2d) {
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
    }
    
    private void paintQuantumBackground(Graphics2D g2d) {
        int width = getWidth();
        int height = getHeight();
        
        // Quantum field effect
        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 15; j++) {
                double x = i * width / 20.0;
                double y = j * height / 15.0;
                
                double waveX = Math.sin(time * 0.01 + x * 0.005) * 30;
                double waveY = Math.cos(time * 0.01 + y * 0.005) * 30;
                
                Color waveColor = neonColors[(i + j + (int)(time * 0.1)) % neonColors.length];
                g2d.setColor(new Color(waveColor.getRed(), waveColor.getGreen(), waveColor.getBlue(), 20));
                
                g2d.fillOval((int)(x + waveX), (int)(y + waveY), 10, 10);
            }
        }
        
        // Energy streams
        paintEnergyStreams(g2d);
    }
    
    private void paintEnergyStreams(Graphics2D g2d) {
        g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        
        for (int i = 0; i < 5; i++) {
            Path2D path = new Path2D.Double();
            double startX = getWidth() * (i / 5.0);
            double startY = 0;
            
            path.moveTo(startX, startY);
            
            for (int y = 0; y < getHeight(); y += 10) {
                double wave = Math.sin(time * 0.02 + y * 0.01 + i) * 100;
                path.lineTo(startX + wave, y);
            }
            
            Color streamColor = neonColors[i % neonColors.length];
            g2d.setColor(new Color(streamColor.getRed(), streamColor.getGreen(), streamColor.getBlue(), 100));
            g2d.draw(path);
        }
    }
    
    private void paintParticles(Graphics2D g2d) {
        for (Particle particle : particles) {
            particle.update();
            particle.render(g2d);
        }
        
        // Remove dead particles
        particles.removeIf(p -> !p.isAlive());
    }
    
    private void paintHolographicEffects(Graphics2D g2d) {
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        
        // Holographic rings
        for (int i = 0; i < 8; i++) {
            double radius = 50 + i * 30 + Math.sin(time * 0.02 + i) * 10;
            double alpha = 0.3 - i * 0.03;
            
            Color ringColor = neonColors[i % neonColors.length];
            g2d.setColor(new Color(ringColor.getRed(), ringColor.getGreen(), ringColor.getBlue(), (int)(alpha * 255)));
            g2d.setStroke(new BasicStroke(3));
            g2d.drawOval((int)(centerX - radius), (int)(centerY - radius), (int)(radius * 2), (int)(radius * 2));
        }
        
        // 3D rotating polyhedron
        paintRotatingPolyhedron(g2d, centerX - 200, centerY - 200);
        paintRotatingPolyhedron(g2d, centerX + 200, centerY + 200);
    }
    
    private void paintRotatingPolyhedron(Graphics2D g2d, int centerX, int centerY) {
        // Icosahedron vertices (simplified)
        double[][] vertices = generateIcosahedronVertices();
        int[][] projectedVertices = new int[vertices.length][2];
        
        // Apply 3D rotation
        for (int i = 0; i < vertices.length; i++) {
            double[] rotated = rotate3D(vertices[i], time * 0.01, time * 0.015, time * 0.008);
            projectedVertices[i][0] = centerX + (int)(rotated[0] * 50);
            projectedVertices[i][1] = centerY + (int)(rotated[1] * 50);
        }
        
        // Draw faces
        int[][] faces = getIcosahedronFaces();
        for (int i = 0; i < faces.length; i++) {
            int[] face = faces[i];
            
            // Calculate face normal for depth sorting
            double depth = (vertices[face[0]][2] + vertices[face[1]][2] + vertices[face[2]][2]) / 3;
            
            Color faceColor = neonColors[i % neonColors.length];
            int alpha = (int)(100 + depth * 50);
            alpha = Math.max(0, Math.min(255, alpha));
            
            g2d.setColor(new Color(faceColor.getRed(), faceColor.getGreen(), faceColor.getBlue(), alpha));
            
            int[] xPoints = {projectedVertices[face[0]][0], projectedVertices[face[1]][0], projectedVertices[face[2]][0]};
            int[] yPoints = {projectedVertices[face[0]][1], projectedVertices[face[1]][1], projectedVertices[face[2]][1]};
            
            g2d.fillPolygon(xPoints, yPoints, 3);
            
            g2d.setColor(Color.WHITE);
            g2d.setStroke(new BasicStroke(1));
            g2d.drawPolygon(xPoints, yPoints, 3);
        }
    }
    
    private double[][] generateIcosahedronVertices() {
        double phi = (1 + Math.sqrt(5)) / 2; // Golden ratio
        double[][] vertices = {
            {-1, phi, 0}, {1, phi, 0}, {-1, -phi, 0}, {1, -phi, 0},
            {0, -1, phi}, {0, 1, phi}, {0, -1, -phi}, {0, 1, -phi},
            {phi, 0, -1}, {phi, 0, 1}, {-phi, 0, -1}, {-phi, 0, 1}
        };
        return vertices;
    }
    
    private int[][] getIcosahedronFaces() {
        return new int[][] {
            {0, 11, 5}, {0, 5, 1}, {0, 1, 7}, {0, 7, 10}, {0, 10, 11},
            {1, 5, 9}, {5, 11, 4}, {11, 10, 2}, {10, 7, 6}, {7, 1, 8},
            {3, 9, 4}, {3, 4, 2}, {3, 2, 6}, {3, 6, 8}, {3, 8, 9},
            {4, 9, 5}, {2, 4, 11}, {6, 2, 10}, {8, 6, 7}, {9, 8, 1}
        };
    }
    
    private double[] rotate3D(double[] point, double angleX, double angleY, double angleZ) {
        double x = point[0], y = point[1], z = point[2];
        
        // Rotate around X axis
        double cosX = Math.cos(angleX), sinX = Math.sin(angleX);
        double newY = y * cosX - z * sinX;
        double newZ = y * sinX + z * cosX;
        y = newY; z = newZ;
        
        // Rotate around Y axis
        double cosY = Math.cos(angleY), sinY = Math.sin(angleY);
        double newX = x * cosY + z * sinY;
        newZ = -x * sinY + z * cosY;
        x = newX; z = newZ;
        
        // Rotate around Z axis
        double cosZ = Math.cos(angleZ), sinZ = Math.sin(angleZ);
        newX = x * cosZ - y * sinZ;
        newY = x * sinZ + y * cosZ;
        
        return new double[]{newX, newY, z};
    }
    
    private void paintCurrentSection(Graphics2D g2d) {
        String[] sections = {"HOME", "ABOUT", "SKILLS", "PROJECTS", "EXPERIENCE", "ACHIEVEMENTS", "CONTACT"};
        String currentSectionName = sections[currentSection];
        
        switch (currentSectionName) {
            case "HOME": paintHomeSection(g2d); break;
            case "ABOUT": paintAboutSection(g2d); break;
            case "SKILLS": paintSkillsSection(g2d); break;
            case "PROJECTS": paintProjectsSection(g2d); break;
            case "EXPERIENCE": paintExperienceSection(g2d); break;
            case "ACHIEVEMENTS": paintAchievementsSection(g2d); break;
            case "CONTACT": paintContactSection(g2d); break;
        }
    }
    
    private void paintHomeSection(Graphics2D g2d) {
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        
        // Holographic title
        paintHolographicText(g2d, "ANURAG TIWARI", centerX, centerY - 80, 64, true);
        paintHolographicText(g2d, "Electronics Engineer & Web Developer", centerX, centerY - 20, 24, false);
        paintHolographicText(g2d, "Innovating the Future with Technology", centerX, centerY + 20, 18, false);
        
        // Animated tech symbols
        paintTechSymbols(g2d, centerY + 100);
    }
    
    private void paintAboutSection(Graphics2D g2d) {
        int centerX = getWidth() / 2;
        int startY = getHeight() / 6;
        
        paintHolographicText(g2d, "ABOUT ME", centerX, startY, 48, true);
        
        String[] aboutLines = {
            "Electronics & Telecommunication Engineering Student",
            "IET DAVV, Indore (2023-2027)",
            "",
            "🎓 Passionate Tech Enthusiast",
            "🚀 Hackathon Winner & Team Leader", 
            "💻 Full-Stack Developer",
            "🔧 Electronics & Embedded Systems Expert",
            "🌐 Web3 & Blockchain Innovator"
        };
        
        for (int i = 0; i < aboutLines.length; i++) {
            int y = startY + 100 + i * 40;
            paintHolographicText(g2d, aboutLines[i], centerX, y, 20, false);
        }
    }
    
    private void paintSkillsSection(Graphics2D g2d) {
        int centerX = getWidth() / 2;
        int startY = getHeight() / 8;
        
        paintHolographicText(g2d, "SKILLS MATRIX", centerX, startY, 48, true);
        
        String[][] skillCategories = {
            {"Web Development", "React", "Next.js", "JavaScript", "HTML/CSS"},
            {"Mobile Development", "Flutter", "Android Studio", "API Integration"},
            {"Electronics", "Arduino", "ESP32", "PCB Design", "Embedded C"},
            {"Programming", "Python", "Java", "C/C++", "VS Code"},
            {"AI & ML", "Model Training", "Prompt Engineering", "Data Analysis"},
            {"Leadership", "Team Management", "Communication", "Project Planning"}
        };
        
        int cols = 3;
        int rows = 2;
        int cellWidth = getWidth() / cols;
        int cellHeight = (getHeight() - startY - 100) / rows;
        
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                int index = row * cols + col;
                if (index < skillCategories.length) {
                    int x = col * cellWidth + cellWidth / 2;
                    int y = startY + 100 + row * cellHeight + cellHeight / 2;
                    
                    paintSkillCategory(g2d, skillCategories[index], x, y);
                }
            }
        }
    }
    
    private void paintSkillCategory(Graphics2D g2d, String[] skills, int centerX, int centerY) {
        // Category title
        paintHolographicText(g2d, skills[0], centerX, centerY - 60, 22, true);
        
        // Skills in circular arrangement
        int radius = 80;
        for (int i = 1; i < skills.length; i++) {
            double angle = (i - 1) * 2 * Math.PI / (skills.length - 1) + time * 0.005;
            int x = centerX + (int)(Math.cos(angle) * radius);
            int y = centerY + (int)(Math.sin(angle) * radius);
            
            paintHolographicText(g2d, skills[i], x, y, 16, false);
        }
    }
    
    private void paintProjectsSection(Graphics2D g2d) {
        int centerX = getWidth() / 2;
        int startY = getHeight() / 8;
        
        paintHolographicText(g2d, "FEATURED PROJECTS", centerX, startY, 48, true);
        
        String[] projects = {
            "🌐 Antim Sewa - Funeral Services Platform",
            "🤖 AgentSync - Multi-Agent AI Dashboard", 
            "💰 Stock Saarthi - Web3 Tokenization (Winner)",
            "📡 RSSI Signal Detection - ML Classification",
            "🚗 E-Clutch System - Automotive Innovation (Winner)",
            "📍 Geolocation Attendance - Flutter App"
        };
        
        for (int i = 0; i < projects.length; i++) {
            int y = startY + 120 + i * 60;
            paintHolographicText(g2d, projects[i], centerX, y, 22, false);
        }
    }
    
    private void paintExperienceSection(Graphics2D g2d) {
        int centerX = getWidth() / 2;
        int startY = getHeight() / 8;
        
        paintHolographicText(g2d, "EXPERIENCE", centerX, startY, 48, true);
        
        String[] experiences = {
            "⚡ Team Spark Ignited - PR & Electronics Lead",
            "🎓 DUAA DAVV - Website Designer & Team Member",
            "🎉 DAVV Youth Fest - Core Organizing Team",
            "👨‍💻 GDGOC IET DAVV - Tech Team & Social Media Lead",
            "🏆 I Love Hackathon - Team Leader (Winner)"
        };
        
        for (int i = 0; i < experiences.length; i++) {
            int y = startY + 120 + i * 70;
            paintHolographicText(g2d, experiences[i], centerX, y, 24, false);
        }
    }
    
    private void paintAchievementsSection(Graphics2D g2d) {
        int centerX = getWidth() / 2;
        int startY = getHeight() / 8;
        
        paintHolographicText(g2d, "ACHIEVEMENTS", centerX, startY, 48, true);
        
        String[] achievements = {
            "🏆 Winner - I Love Hackathon",
            "🥇 Winner - Volvo Eicher Soil Symposium",
            "🥈 2nd Prize - ETWDC 2025",
            "🥉 3rd Rank - GDSC Code Series",
            "🎯 Finalist - IIT Chennai Hackathon",
            "📝 Selected - Pariksha Pe Charcha 2020"
        };
        
        for (int i = 0; i < achievements.length; i++) {
            int y = startY + 120 + i * 60;
            paintHolographicText(g2d, achievements[i], centerX, y, 26, false);
        }
    }
    
    private void paintContactSection(Graphics2D g2d) {
        int centerX = getWidth() / 2;
        int startY = getHeight() / 6;
        
        paintHolographicText(g2d, "GET IN TOUCH", centerX, startY, 48, true);
        
        String[] contacts = {
            "📧 tiwarianurag342409@gmail.com",
            "📱 +91-6261072872",
            "📍 Indore, India",
            "💼 github.com/AnuragTiwari1508",
            "🔗 linkedin.com/in/anurag-tiwari-4b664627b",
            "🌐 anurag-tiwari-portfolio.vercel.app"
        };
        
        for (int i = 0; i < contacts.length; i++) {
            int y = startY + 120 + i * 60;
            paintHolographicText(g2d, contacts[i], centerX, y, 24, false);
        }
    }
    
    private void paintHolographicText(Graphics2D g2d, String text, int x, int y, int fontSize, boolean glow) {
        g2d.setFont(new Font("Arial", Font.BOLD, fontSize));
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        
        if (glow) {
            // Multi-layer glow effect
            for (int i = 8; i >= 0; i--) {
                int alpha = 30 - i * 3;
                Color glowColor = neonColors[(int)(time * 0.1) % neonColors.length];
                g2d.setColor(new Color(glowColor.getRed(), glowColor.getGreen(), glowColor.getBlue(), alpha));
                g2d.drawString(text, x - textWidth/2 + i, y + i);
            }
        }
        
        // Main text with color cycling
        Color textColor = neonColors[(int)(time * 0.05) % neonColors.length];
        g2d.setColor(textColor);
        g2d.drawString(text, x - textWidth/2, y);
        
        // Scanline effect for extra futuristic look
        if (glow) {
            g2d.setColor(new Color(255, 255, 255, 50));
            g2d.drawLine(x - textWidth/2, y + 2, x + textWidth/2, y + 2);
        }
    }
    
    private void paintTechSymbols(Graphics2D g2d, int y) {
        String[] symbols = {"⚡", "🚀", "💻", "🔧", "🌐"};
        int spacing = getWidth() / (symbols.length + 1);
        
        g2d.setFont(new Font("Arial", Font.BOLD, 48));
        
        for (int i = 0; i < symbols.length; i++) {
            int x = spacing * (i + 1);
            double bounce = Math.sin(time * 0.02 + i) * 20;
            
            paintHolographicText(g2d, symbols[i], x, (int)(y + bounce), 48, true);
        }
    }
    
    private void paintUI(Graphics2D g2d) {
        // Section indicator
        String[] sections = {"HOME", "ABOUT", "SKILLS", "PROJECTS", "EXPERIENCE", "ACHIEVEMENTS", "CONTACT"};
        
        int indicatorY = getHeight() - 50;
        int totalWidth = sections.length * 100;
        int startX = (getWidth() - totalWidth) / 2;
        
        for (int i = 0; i < sections.length; i++) {
            int x = startX + i * 100;
            boolean isCurrent = (i == currentSection);
            
            if (isCurrent) {
                g2d.setColor(neonColors[i % neonColors.length]);
                g2d.fillOval(x - 20, indicatorY - 10, 40, 20);
            } else {
                g2d.setColor(new Color(100, 100, 100, 100));
                g2d.drawOval(x - 15, indicatorY - 8, 30, 16);
            }
        }
        
        // Instructions
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.PLAIN, 14));
        String instructions = "← → Arrow Keys or Mouse Click to Navigate | ESC to Exit";
        FontMetrics fm = g2d.getFontMetrics();
        g2d.drawString(instructions, (getWidth() - fm.stringWidth(instructions)) / 2, getHeight() - 20);
    }
    
    private void handleInput(KeyEvent e) {
        String[] sections = {"HOME", "ABOUT", "SKILLS", "PROJECTS", "EXPERIENCE", "ACHIEVEMENTS", "CONTACT"};
        
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                navigateLeft();
                break;
            case KeyEvent.VK_RIGHT:
                navigateRight();
                break;
            case KeyEvent.VK_ESCAPE:
                System.exit(0);
                break;
        }
    }
    
    private void navigateLeft() {
        String[] sections = {"HOME", "ABOUT", "SKILLS", "PROJECTS", "EXPERIENCE", "ACHIEVEMENTS", "CONTACT"};
        currentSection = (currentSection - 1 + sections.length) % sections.length;
        cardLayout.show(contentPanel, sections[currentSection]);
    }
    
    private void navigateRight() {
        String[] sections = {"HOME", "ABOUT", "SKILLS", "PROJECTS", "EXPERIENCE", "ACHIEVEMENTS", "CONTACT"};
        currentSection = (currentSection + 1) % sections.length;
        cardLayout.show(contentPanel, sections[currentSection]);
    }
    
    private void initializeParticles() {
        for (int i = 0; i < 100; i++) {
            particles.add(new Particle(
                random.nextDouble() * getWidth(),
                random.nextDouble() * getHeight(),
                neonColors[random.nextInt(neonColors.length)]
            ));
        }
    }
    
    private void addParticleAtMouse(int x, int y) {
        if (particles.size() < 200) {
            particles.add(new Particle(x, y, neonColors[random.nextInt(neonColors.length)]));
        }
    }
    
    private void startAnimations() {
        animationTimer = new javax.swing.Timer(16, e -> {
            time += 1;
            
            // Add random particles
            if (random.nextInt(10) == 0) {
                particles.add(new Particle(
                    random.nextDouble() * getWidth(),
                    random.nextDouble() * getHeight(),
                    neonColors[random.nextInt(neonColors.length)]
                ));
            }
            
            repaint();
        });
        animationTimer.start();
    }
    
    private class Particle {
        double x, y, vx, vy;
        Color color;
        int life, maxLife;
        double size;
        
        public Particle(double x, double y, Color color) {
            this.x = x;
            this.y = y;
            this.color = color;
            this.vx = (random.nextDouble() - 0.5) * 2;
            this.vy = (random.nextDouble() - 0.5) * 2;
            this.life = this.maxLife = 60 + random.nextInt(120);
            this.size = 2 + random.nextDouble() * 4;
        }
        
        public void update() {
            x += vx;
            y += vy;
            life--;
            
            // Gravity effect
            vy += 0.02;
            
            // Bounce off edges
            if (x <= 0 || x >= getWidth()) vx *= -0.8;
            if (y <= 0 || y >= getHeight()) vy *= -0.8;
            
            // Keep in bounds
            x = Math.max(0, Math.min(getWidth(), x));
            y = Math.max(0, Math.min(getHeight(), y));
        }
        
        public void render(Graphics2D g2d) {
            double alpha = (double) life / maxLife;
            int alphaInt = (int)(alpha * 255);
            
            g2d.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), alphaInt));
            g2d.fillOval((int)(x - size/2), (int)(y - size/2), (int)size, (int)size);
            
            // Tail effect
            g2d.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), alphaInt/3));
            g2d.fillOval((int)(x - vx*2 - size/4), (int)(y - vy*2 - size/4), (int)(size/2), (int)(size/2));
        }
        
        public boolean isAlive() {
            return life > 0;
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeel());
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            new AnuragResume3DEnhanced().setVisible(true);
        });
    }
}
