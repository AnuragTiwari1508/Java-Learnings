import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class AnuragResume3D extends JFrame {
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private Timer animationTimer;
    private float rotationAngle = 0;
    private float pulseScale = 1.0f;
    private boolean pulseDirection = true;
    private int currentSection = 0;
    private List<Color> gradientColors;
    
    // 3D Animation variables
    private double cameraX = 0, cameraY = 0, cameraZ = 200;
    private double rotX = 0, rotY = 0, rotZ = 0;
    
    public AnuragResume3D() {
        initializeColors();
        setupUI();
        startAnimations();
    }
    
    private void initializeColors() {
        gradientColors = new ArrayList<>();
        gradientColors.add(new Color(67, 56, 202));   // Deep Purple
        gradientColors.add(new Color(79, 70, 229));   // Indigo
        gradientColors.add(new Color(99, 102, 241));  // Blue Purple
        gradientColors.add(new Color(139, 92, 246));  // Purple
        gradientColors.add(new Color(168, 85, 247));  // Light Purple
    }
    
    private void setupUI() {
        setTitle("Anurag Tiwari - 3D Interactive Resume");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        
        // Create main layout
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setBackground(Color.BLACK);
        
        // Add sections
        mainPanel.add(createHomeSection(), "HOME");
        mainPanel.add(createAboutSection(), "ABOUT");
        mainPanel.add(createSkillsSection(), "SKILLS");
        mainPanel.add(createProjectsSection(), "PROJECTS");
        mainPanel.add(createExperienceSection(), "EXPERIENCE");
        mainPanel.add(createAchievementsSection(), "ACHIEVEMENTS");
        mainPanel.add(createContactSection(), "CONTACT");
        
        // Navigation
        JPanel navPanel = createNavigationPanel();
        
        setLayout(new BorderLayout());
        add(navPanel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);
        
        // Smooth transitions
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleNavigation(e);
            }
        });
        
        setFocusable(true);
        pack();
    }
    
    private JPanel createNavigationPanel() {
        JPanel nav = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        nav.setBackground(new Color(0, 0, 0, 180));
        
        String[] sections = {"HOME", "ABOUT", "SKILLS", "PROJECTS", "EXPERIENCE", "ACHIEVEMENTS", "CONTACT"};
        
        for (int i = 0; i < sections.length; i++) {
            final int index = i;
            final String section = sections[i];
            
            JButton btn = new StyledButton(section);
            btn.addActionListener(e -> {
                currentSection = index;
                cardLayout.show(mainPanel, section);
                animateTransition();
            });
            nav.add(btn);
        }
        
        return nav;
    }
    
    private JPanel createHomeSection() {
        return new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                
                // Enable anti-aliasing for smooth graphics
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                
                // Animated gradient background
                paintAnimatedBackground(g2d);
                
                // 3D floating elements
                paint3DElements(g2d);
                
                // Main content
                paintHomeContent(g2d);
                
                g2d.dispose();
            }
        };
    }
    
    private void paintAnimatedBackground(Graphics2D g2d) {
        int width = getWidth();
        int height = getHeight();
        
        // Create flowing gradient
        for (int i = 0; i < 5; i++) {
            Color color1 = gradientColors.get(i % gradientColors.size());
            Color color2 = gradientColors.get((i + 1) % gradientColors.size());
            
            float alpha = 0.3f + 0.1f * (float) Math.sin(rotationAngle + i);
            Color transparentColor1 = new Color(color1.getRed(), color1.getGreen(), color1.getBlue(), (int)(alpha * 255));
            Color transparentColor2 = new Color(color2.getRed(), color2.getGreen(), color2.getBlue(), (int)(alpha * 255));
            
            GradientPaint gradient = new GradientPaint(
                0, height * i / 5f + (float) Math.sin(rotationAngle + i) * 50,
                transparentColor1,
                width, height * (i + 1) / 5f + (float) Math.cos(rotationAngle + i) * 50,
                transparentColor2
            );
            
            g2d.setPaint(gradient);
            g2d.fillRect(0, (int)(height * i / 5f), width, height / 5 + 50);
        }
        
        // Floating particles
        paintParticles(g2d);
    }
    
    private void paintParticles(Graphics2D g2d) {
        g2d.setColor(new Color(255, 255, 255, 100));
        for (int i = 0; i < 50; i++) {
            double x = (Math.sin(rotationAngle + i * 0.1) * 100 + getWidth() / 2) % getWidth();
            double y = (Math.cos(rotationAngle + i * 0.15) * 150 + getHeight() / 2) % getHeight();
            double size = 2 + Math.sin(rotationAngle + i * 0.2) * 2;
            
            g2d.fillOval((int)x, (int)y, (int)size, (int)size);
        }
    }
    
    private void paint3DElements(Graphics2D g2d) {
        // 3D cube rotation
        int centerX = getWidth() / 4;
        int centerY = getHeight() / 4;
        
        // Draw rotating 3D cubes
        for (int i = 0; i < 3; i++) {
            draw3DCube(g2d, centerX + i * 100, centerY + i * 50, 30 + i * 10, rotationAngle + i);
        }
        
        // Floating geometric shapes
        drawFloatingShapes(g2d);
    }
    
    private void draw3DCube(Graphics2D g2d, int x, int y, int size, double rotation) {
        // Calculate 3D cube vertices
        double[][] vertices = {
            {-1, -1, -1}, {1, -1, -1}, {1, 1, -1}, {-1, 1, -1},
            {-1, -1, 1}, {1, -1, 1}, {1, 1, 1}, {-1, 1, 1}
        };
        
        // Transform vertices
        int[][] projectedVertices = new int[8][2];
        for (int i = 0; i < 8; i++) {
            double[] rotated = rotate3D(vertices[i], rotation, rotation * 0.7, rotation * 0.5);
            projectedVertices[i][0] = x + (int)(rotated[0] * size);
            projectedVertices[i][1] = y + (int)(rotated[1] * size);
        }
        
        // Draw cube faces with transparency
        g2d.setStroke(new BasicStroke(2));
        Color cubeColor = gradientColors.get((int)(rotation * 10) % gradientColors.size());
        g2d.setColor(new Color(cubeColor.getRed(), cubeColor.getGreen(), cubeColor.getBlue(), 150));
        
        // Draw edges
        int[][] edges = {
            {0,1}, {1,2}, {2,3}, {3,0}, {4,5}, {5,6}, {6,7}, {7,4},
            {0,4}, {1,5}, {2,6}, {3,7}
        };
        
        for (int[] edge : edges) {
            g2d.drawLine(
                projectedVertices[edge[0]][0], projectedVertices[edge[0]][1],
                projectedVertices[edge[1]][0], projectedVertices[edge[1]][1]
            );
        }
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
    
    private void drawFloatingShapes(Graphics2D g2d) {
        // Floating hexagons
        for (int i = 0; i < 5; i++) {
            int x = (int)(getWidth() * 0.7 + Math.sin(rotationAngle + i) * 100);
            int y = (int)(getHeight() * 0.3 + Math.cos(rotationAngle + i * 1.2) * 80);
            
            drawHexagon(g2d, x, y, 20 + i * 5, rotationAngle + i);
        }
    }
    
    private void drawHexagon(Graphics2D g2d, int centerX, int centerY, int radius, double rotation) {
        int[] xPoints = new int[6];
        int[] yPoints = new int[6];
        
        for (int i = 0; i < 6; i++) {
            double angle = rotation + i * Math.PI / 3;
            xPoints[i] = centerX + (int)(radius * Math.cos(angle));
            yPoints[i] = centerY + (int)(radius * Math.sin(angle));
        }
        
        Color hexColor = gradientColors.get((int)(rotation * 5) % gradientColors.size());
        g2d.setColor(new Color(hexColor.getRed(), hexColor.getGreen(), hexColor.getBlue(), 120));
        g2d.fillPolygon(xPoints, yPoints, 6);
        
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawPolygon(xPoints, yPoints, 6);
    }
    
    private void paintHomeContent(Graphics2D g2d) {
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        
        // Main title with 3D effect
        g2d.setFont(new Font("Arial", Font.BOLD, 72));
        String title = "Anurag Tiwari";
        
        // 3D text shadow effect
        for (int i = 5; i >= 0; i--) {
            int alpha = 255 - i * 40;
            g2d.setColor(new Color(0, 0, 0, alpha));
            drawCenteredString(g2d, title, centerX + i, centerY - 50 + i);
        }
        
        // Main title
        g2d.setColor(Color.WHITE);
        drawCenteredString(g2d, title, centerX, centerY - 50);
        
        // Subtitle with glow effect
        g2d.setFont(new Font("Arial", Font.PLAIN, 32));
        String subtitle = "Electronics Engineer & Web Developer";
        
        // Glow effect
        for (int i = 3; i >= 0; i--) {
            int alpha = 100 - i * 20;
            Color glowColor = gradientColors.get(currentSection % gradientColors.size());
            g2d.setColor(new Color(glowColor.getRed(), glowColor.getGreen(), glowColor.getBlue(), alpha));
            drawCenteredString(g2d, subtitle, centerX + i, centerY + 20 + i);
        }
        
        g2d.setColor(Color.WHITE);
        drawCenteredString(g2d, subtitle, centerX, centerY + 20);
        
        // Animated pulse circle
        int pulseRadius = (int)(50 * pulseScale);
        g2d.setColor(new Color(255, 255, 255, 100));
        g2d.fillOval(centerX - pulseRadius, centerY + 100 - pulseRadius, pulseRadius * 2, pulseRadius * 2);
        
        // Tech stack icons (simplified)
        paintTechStack(g2d, centerY + 200);
    }
    
    private void paintTechStack(Graphics2D g2d, int y) {
        String[] skills = {"Java", "React", "Python", "Arduino", "Web3"};
        int spacing = getWidth() / (skills.length + 1);
        
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        
        for (int i = 0; i < skills.length; i++) {
            int x = spacing * (i + 1);
            
            // Skill bubble with animation
            double scale = 1.0 + 0.2 * Math.sin(rotationAngle + i);
            int radius = (int)(30 * scale);
            
            Color skillColor = gradientColors.get(i % gradientColors.size());
            g2d.setColor(new Color(skillColor.getRed(), skillColor.getGreen(), skillColor.getBlue(), 200));
            g2d.fillOval(x - radius, y - radius, radius * 2, radius * 2);
            
            g2d.setColor(Color.WHITE);
            g2d.setStroke(new BasicStroke(2));
            g2d.drawOval(x - radius, y - radius, radius * 2, radius * 2);
            
            // Skill text
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(skills[i]);
            g2d.drawString(skills[i], x - textWidth/2, y + 5);
        }
    }
    
    private JPanel createAboutSection() {
        return new AnimatedSection("About Me", 
            "Electronics and Telecommunication Engineering student from IET DAVV, Indore.\n" +
            "Passionate about web development, embedded systems, and leading technical teams.\n\n" +
            "🎓 Education: B.Tech in Electronics & Telecommunication (2023-2027)\n" +
            "📍 Location: Indore, India\n" +
            "📧 Email: tiwarianurag342409@gmail.com\n" +
            "📱 Phone: +91-6261072872");
    }
    
    private JPanel createSkillsSection() {
        return new AnimatedSection("My Skills",
            "💻 Web Development: HTML/CSS, JavaScript, React, Next.js\n" +
            "📱 Mobile: Flutter, Android Studio\n" +
            "🔧 Electronics: Embedded C, PCB Design, Arduino, ESP32\n" +
            "🐍 Programming: Python, C/C++, VS Code\n" +
            "🤖 AI & ML: Prompt Engineering, ML Model Training\n" +
            "👥 Soft Skills: Leadership, Team Management, Communication");
    }
    
    private JPanel createProjectsSection() {
        return new AnimatedSection("Featured Projects",
            "🌐 Antim Sewa - Comprehensive funeral services website (Next.js, MongoDB)\n" +
            "🤖 AgentSync - Multi-agent AI platform for productivity\n" +
            "💰 Stock Saarthi - Web3 stock tokenization platform (Winner, I Love Hackathon)\n" +
            "📡 RSSI Signal Detection - ESP32-based activity classification\n" +
            "🚗 E-Clutch Project - Electronic clutch system (Winner, Volvo Eicher)\n" +
            "📍 Geolocation Attendance - Flutter app with API integration");
    }
    
    private JPanel createExperienceSection() {
        return new AnimatedSection("Work Experience",
            "⚡ Team Spark Ignited (IET DAVV) - PR & Electronics Team Lead (2024-Present)\n" +
            "🎓 DUAA DAVV - Team Member & Website Designer (2024-Present)\n" +
            "🎉 DAVV Youth Fest - Core Organizing Team Member (2024)\n" +
            "👨‍💻 GDGOC IET DAVV - Core Tech Team & Social Media Lead (2024-Present)\n" +
            "🏆 I Love Hackathon - Team Leader (Winner, 2024)");
    }
    
    private JPanel createAchievementsSection() {
        return new AnimatedSection("Achievements",
            "🏆 Winner - I Love Hackathon (Stock Saarthi project)\n" +
            "🥇 Winner - Volvo Eicher Soil Symposium 2024 (E-Clutch)\n" +
            "🥈 2nd Prize - ETWDC 2025 (Special Category, 7th overall)\n" +
            "🥉 3rd Rank - Code Series (GDSC IET DAVV)\n" +
            "🎯 Finalist - IIT Chennai Hackathon (Top 5)\n" +
            "📝 Selected - Pariksha Pe Charcha 2020");
    }
    
    private JPanel createContactSection() {
        return new AnimatedSection("Get In Touch",
            "📧 Email: tiwarianurag342409@gmail.com\n" +
            "📱 Phone: +91-6261072872\n" +
            "📍 Location: Indore, India\n" +
            "💼 GitHub: github.com/AnuragTiwari1508\n" +
            "🔗 LinkedIn: linkedin.com/in/anurag-tiwari-4b664627b\n" +
            "🌐 Portfolio: anurag-tiwari-portfolio.vercel.app");
    }
    
    private class AnimatedSection extends JPanel {
        private String title;
        private String content;
        
        public AnimatedSection(String title, String content) {
            this.title = title;
            this.content = content;
            setOpaque(false);
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            
            // Animated background
            paintAnimatedBackground(g2d);
            
            // Content with 3D effects
            paintSectionContent(g2d);
            
            g2d.dispose();
        }
        
        private void paintSectionContent(Graphics2D g2d) {
            int centerX = getWidth() / 2;
            int startY = getHeight() / 4;
            
            // Title with 3D effect
            g2d.setFont(new Font("Arial", Font.BOLD, 48));
            
            // 3D shadow
            for (int i = 3; i >= 0; i--) {
                g2d.setColor(new Color(0, 0, 0, 100 - i * 20));
                drawCenteredString(g2d, title, centerX + i, startY + i);
            }
            
            g2d.setColor(Color.WHITE);
            drawCenteredString(g2d, title, centerX, startY);
            
            // Content
            g2d.setFont(new Font("Arial", Font.PLAIN, 18));
            String[] lines = content.split("\n");
            
            for (int i = 0; i < lines.length; i++) {
                int y = startY + 100 + i * 30;
                
                // Animated text appearance
                double animationOffset = Math.sin(rotationAngle + i * 0.2) * 10;
                int alpha = (int)(255 * (0.7 + 0.3 * Math.sin(rotationAngle + i * 0.1)));
                
                g2d.setColor(new Color(255, 255, 255, alpha));
                drawCenteredString(g2d, lines[i], (int)(centerX + animationOffset), y);
            }
        }
    }
    
    private class StyledButton extends JButton {
        public StyledButton(String text) {
            super(text);
            setForeground(Color.WHITE);
            setBackground(new Color(67, 56, 202, 150));
            setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
            setFocusPainted(false);
            setFont(new Font("Arial", Font.BOLD, 14));
            
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    setBackground(new Color(99, 102, 241, 200));
                    setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                }
                
                @Override
                public void mouseExited(MouseEvent e) {
                    setBackground(new Color(67, 56, 202, 150));
                    setCursor(Cursor.getDefaultCursor());
                }
            });
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Rounded rectangle
            g2d.setColor(getBackground());
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
            
            // Text
            g2d.setColor(getForeground());
            FontMetrics fm = g2d.getFontMetrics();
            int x = (getWidth() - fm.stringWidth(getText())) / 2;
            int y = (getHeight() + fm.getAscent()) / 2 - 2;
            g2d.drawString(getText(), x, y);
            
            g2d.dispose();
        }
    }
    
    private void drawCenteredString(Graphics2D g2d, String text, int x, int y) {
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        g2d.drawString(text, x - textWidth/2, y);
    }
    
    private void handleNavigation(KeyEvent e) {
        String[] sections = {"HOME", "ABOUT", "SKILLS", "PROJECTS", "EXPERIENCE", "ACHIEVEMENTS", "CONTACT"};
        
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                currentSection = (currentSection - 1 + sections.length) % sections.length;
                break;
            case KeyEvent.VK_RIGHT:
                currentSection = (currentSection + 1) % sections.length;
                break;
            default:
                return;
        }
        
        cardLayout.show(mainPanel, sections[currentSection]);
        animateTransition();
    }
    
    private void animateTransition() {
        // Smooth transition animation
        javax.swing.Timer transitionTimer = new javax.swing.Timer(50, null);
        transitionTimer.addActionListener(e -> {
            repaint();
            if (((javax.swing.Timer)e.getSource()).getDelay() > 200) {
                ((javax.swing.Timer)e.getSource()).stop();
            }
        });
        transitionTimer.start();
    }
    
    private void startAnimations() {
        animationTimer = new Timer();
        animationTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                rotationAngle += 0.02f;
                
                // Pulse animation
                if (pulseDirection) {
                    pulseScale += 0.01f;
                    if (pulseScale > 1.3f) pulseDirection = false;
                } else {
                    pulseScale -= 0.01f;
                    if (pulseScale < 0.8f) pulseDirection = true;
                }
                
                SwingUtilities.invokeLater(() -> repaint());
            }
        }, 0, 16); // ~60 FPS
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeel());
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            new AnuragResume3D().setVisible(true);
        });
    }
}
