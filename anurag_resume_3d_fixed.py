import pygame
import math
import random
import sys
from typing import List, Tuple

# Initialize Pygame
pygame.init()

# Constants
SCREEN_WIDTH = 1920
SCREEN_HEIGHT = 1080
FPS = 60

# Colors (Neon theme)
NEON_COLORS = [
    (0, 255, 255),    # Cyan
    (255, 0, 255),    # Magenta
    (255, 255, 0),    # Yellow
    (0, 255, 0),      # Green
    (255, 100, 255),  # Pink
    (100, 255, 255),  # Light Blue
    (255, 150, 0),    # Orange
    (150, 0, 255),    # Purple
]

BLACK = (0, 0, 0)
WHITE = (255, 255, 255)
GRAY = (128, 128, 128)

class Particle:
    def __init__(self, x: float, y: float, color: Tuple[int, int, int]):
        self.x = x
        self.y = y
        self.vx = random.uniform(-2, 2)
        self.vy = random.uniform(-2, 2)
        self.color = color
        self.life = random.randint(60, 180)
        self.max_life = self.life
        self.size = random.uniform(2, 6)
        
    def update(self):
        self.x += self.vx
        self.y += self.vy
        self.life -= 1
        
        # Add some physics
        self.vy += 0.02  # Gravity
        
        # Bounce off edges
        if self.x <= 0 or self.x >= SCREEN_WIDTH:
            self.vx *= -0.8
        if self.y <= 0 or self.y >= SCREEN_HEIGHT:
            self.vy *= -0.8
            
        # Keep in bounds
        self.x = max(0, min(SCREEN_WIDTH, self.x))
        self.y = max(0, min(SCREEN_HEIGHT, self.y))
        
    def draw(self, screen):
        if self.life > 0:
            alpha = self.life / self.max_life
            
            # Create surface for alpha blending
            surf = pygame.Surface((self.size * 2, self.size * 2), pygame.SRCALPHA)
            color_with_alpha = (*self.color, int(alpha * 255))
            pygame.draw.circle(surf, color_with_alpha, (self.size, self.size), self.size)
            
            # Glow effect
            for i in range(3):
                glow_size = self.size + i * 2
                glow_alpha = int(alpha * 50 / (i + 1))
                glow_color = (*self.color, glow_alpha)
                glow_surf = pygame.Surface((glow_size * 2, glow_size * 2), pygame.SRCALPHA)
                pygame.draw.circle(glow_surf, glow_color, (glow_size, glow_size), glow_size)
                screen.blit(glow_surf, (self.x - glow_size, self.y - glow_size))
            
            screen.blit(surf, (self.x - self.size, self.y - self.size))
            
    def is_alive(self) -> bool:
        return self.life > 0

class Matrix3D:
    @staticmethod
    def multiply_matrix_vector(matrix, vector):
        """Multiply 3x3 matrix with 3D vector"""
        result = [0, 0, 0]
        for i in range(3):
            for j in range(3):
                result[i] += matrix[i][j] * vector[j]
        return result

    @staticmethod
    def rotation_x(angle):
        cos_a, sin_a = math.cos(angle), math.sin(angle)
        return [
            [1, 0, 0],
            [0, cos_a, -sin_a],
            [0, sin_a, cos_a]
        ]
    
    @staticmethod
    def rotation_y(angle):
        cos_a, sin_a = math.cos(angle), math.sin(angle)
        return [
            [cos_a, 0, sin_a],
            [0, 1, 0],
            [-sin_a, 0, cos_a]
        ]
    
    @staticmethod
    def rotation_z(angle):
        cos_a, sin_a = math.cos(angle), math.sin(angle)
        return [
            [cos_a, -sin_a, 0],
            [sin_a, cos_a, 0],
            [0, 0, 1]
        ]

    @staticmethod
    def multiply_matrices(a, b):
        """Multiply two 3x3 matrices"""
        result = [[0, 0, 0], [0, 0, 0], [0, 0, 0]]
        for i in range(3):
            for j in range(3):
                for k in range(3):
                    result[i][j] += a[i][k] * b[k][j]
        return result

class Shape3D:
    def __init__(self, vertices, edges, center=(0, 0, 0)):
        self.vertices = vertices
        self.edges = edges
        self.center = list(center)
        self.rotation = [0, 0, 0]
        
    def update(self, dt):
        self.rotation[0] += dt * 0.5
        self.rotation[1] += dt * 0.7
        self.rotation[2] += dt * 0.3
        
    def project(self, vertex, screen_center, scale=100):
        # Apply rotation
        rot_x = Matrix3D.rotation_x(self.rotation[0])
        rot_y = Matrix3D.rotation_y(self.rotation[1])
        rot_z = Matrix3D.rotation_z(self.rotation[2])
        
        # Combine rotations
        temp = Matrix3D.multiply_matrices(rot_y, rot_x)
        rotation_matrix = Matrix3D.multiply_matrices(rot_z, temp)
        
        rotated = Matrix3D.multiply_matrix_vector(rotation_matrix, vertex)
        
        # Simple perspective projection
        distance = 200
        factor = distance / (distance + rotated[2])
        
        x = int(screen_center[0] + rotated[0] * scale * factor)
        y = int(screen_center[1] + rotated[1] * scale * factor)
        
        return (x, y), rotated[2]
        
    def draw(self, screen, center, color, scale=100):
        projected_vertices = []
        depths = []
        
        for vertex in self.vertices:
            proj, depth = self.project(vertex, center, scale)
            projected_vertices.append(proj)
            depths.append(depth)
            
        # Draw edges with depth-based alpha
        for edge in self.edges:
            start_idx, end_idx = edge
            start_pos = projected_vertices[start_idx]
            end_pos = projected_vertices[end_idx]
            
            # Calculate average depth for this edge
            avg_depth = (depths[start_idx] + depths[end_idx]) / 2
            alpha = max(50, min(255, int(150 + avg_depth * 2)))
            
            try:
                pygame.draw.line(screen, color, start_pos, end_pos, 2)
            except:
                pass  # Skip invalid coordinates

def create_cube():
    vertices = [
        [-1, -1, -1], [1, -1, -1], [1, 1, -1], [-1, 1, -1],  # Front face
        [-1, -1, 1], [1, -1, 1], [1, 1, 1], [-1, 1, 1]       # Back face
    ]
    
    edges = [
        [0, 1], [1, 2], [2, 3], [3, 0],  # Front face
        [4, 5], [5, 6], [6, 7], [7, 4],  # Back face
        [0, 4], [1, 5], [2, 6], [3, 7]   # Connecting edges
    ]
    
    return Shape3D(vertices, edges)

def create_pyramid():
    vertices = [
        [-1, -1, -1], [1, -1, -1], [1, -1, 1], [-1, -1, 1],  # Base
        [0, 1, 0]  # Apex
    ]
    
    edges = [
        [0, 1], [1, 2], [2, 3], [3, 0],  # Base
        [0, 4], [1, 4], [2, 4], [3, 4]   # To apex
    ]
    
    return Shape3D(vertices, edges)

class Resume3D:
    def __init__(self):
        self.screen = pygame.display.set_mode((SCREEN_WIDTH, SCREEN_HEIGHT), pygame.FULLSCREEN)
        pygame.display.set_caption("Anurag Tiwari - Ultimate 3D Resume")
        self.clock = pygame.time.Clock()
        
        # Font setup
        try:
            self.font_large = pygame.font.Font(None, 72)
            self.font_medium = pygame.font.Font(None, 48)
            self.font_small = pygame.font.Font(None, 24)
            self.font_title = pygame.font.Font(None, 96)
        except:
            # Fallback to default font
            self.font_large = pygame.font.Font(None, 72)
            self.font_medium = pygame.font.Font(None, 48)
            self.font_small = pygame.font.Font(None, 24)
            self.font_title = pygame.font.Font(None, 96)
        
        # State
        self.current_section = 0
        self.sections = ["HOME", "ABOUT", "SKILLS", "PROJECTS", "EXPERIENCE", "ACHIEVEMENTS", "CONTACT"]
        self.time = 0
        
        # Particles
        self.particles: List[Particle] = []
        self.init_particles()
        
        # 3D Objects
        self.shapes = [
            create_cube(),
            create_pyramid(),
            create_cube()
        ]
        
        # Section content
        self.section_content = {
            "HOME": {
                "title": "ANURAG TIWARI",
                "subtitle": "Electronics Engineer & Web Developer",
                "description": "Innovating the Future with Technology"
            },
            "ABOUT": {
                "title": "ABOUT ME",
                "content": [
                    "Electronics & Telecommunication Engineering Student",
                    "IET DAVV, Indore (2023-2027)",
                    "",
                    "🎓 Passionate Tech Enthusiast",
                    "🚀 Hackathon Winner & Team Leader",
                    "💻 Full-Stack Developer",
                    "🔧 Electronics & Embedded Systems Expert",
                    "🌐 Web3 & Blockchain Innovator"
                ]
            },
            "SKILLS": {
                "title": "SKILLS MATRIX",
                "categories": {
                    "Web Development": ["React", "Next.js", "JavaScript", "HTML/CSS"],
                    "Mobile Development": ["Flutter", "Android Studio", "API Integration"],
                    "Electronics": ["Arduino", "ESP32", "PCB Design", "Embedded C"],
                    "Programming": ["Python", "Java", "C/C++", "VS Code"],
                    "AI & ML": ["Model Training", "Prompt Engineering", "Data Analysis"],
                    "Leadership": ["Team Management", "Communication", "Project Planning"]
                }
            },
            "PROJECTS": {
                "title": "FEATURED PROJECTS",
                "projects": [
                    "🌐 Antim Sewa - Funeral Services Platform",
                    "🤖 AgentSync - Multi-Agent AI Dashboard",
                    "💰 Stock Saarthi - Web3 Tokenization (Winner)",
                    "📡 RSSI Signal Detection - ML Classification",
                    "🚗 E-Clutch System - Automotive Innovation (Winner)",
                    "📍 Geolocation Attendance - Flutter App"
                ]
            },
            "EXPERIENCE": {
                "title": "EXPERIENCE",
                "experiences": [
                    "⚡ Team Spark Ignited - PR & Electronics Lead",
                    "🎓 DUAA DAVV - Website Designer & Team Member",
                    "🎉 DAVV Youth Fest - Core Organizing Team",
                    "👨‍💻 GDGOC IET DAVV - Tech Team & Social Media Lead",
                    "🏆 I Love Hackathon - Team Leader (Winner)"
                ]
            },
            "ACHIEVEMENTS": {
                "title": "ACHIEVEMENTS",
                "achievements": [
                    "🏆 Winner - I Love Hackathon",
                    "🥇 Winner - Volvo Eicher Soil Symposium",
                    "🥈 2nd Prize - ETWDC 2025",
                    "🥉 3rd Rank - GDSC Code Series",
                    "🎯 Finalist - IIT Chennai Hackathon",
                    "📝 Selected - Pariksha Pe Charcha 2020"
                ]
            },
            "CONTACT": {
                "title": "GET IN TOUCH",
                "contacts": [
                    "📧 tiwarianurag342409@gmail.com",
                    "📱 +91-6261072872",
                    "📍 Indore, India",
                    "💼 github.com/AnuragTiwari1508",
                    "🔗 linkedin.com/in/anurag-tiwari-4b664627b",
                    "🌐 anurag-tiwari-portfolio.vercel.app"
                ]
            }
        }
        
    def init_particles(self):
        for _ in range(150):
            x = random.uniform(0, SCREEN_WIDTH)
            y = random.uniform(0, SCREEN_HEIGHT)
            color = random.choice(NEON_COLORS)
            self.particles.append(Particle(x, y, color))
            
    def add_particles(self, count=5):
        for _ in range(count):
            x = random.uniform(0, SCREEN_WIDTH)
            y = random.uniform(0, SCREEN_HEIGHT)
            color = random.choice(NEON_COLORS)
            self.particles.append(Particle(x, y, color))
            
    def update_particles(self):
        # Update existing particles
        for particle in self.particles[:]:
            particle.update()
            if not particle.is_alive():
                self.particles.remove(particle)
                
        # Add new particles occasionally
        if len(self.particles) < 200 and random.random() < 0.1:
            self.add_particles(1)
            
    def draw_holographic_text(self, text, x, y, font, color, glow=True):
        if glow:
            # Multi-layer glow effect
            for i in range(8, 0, -1):
                alpha = max(20, 60 - i * 5)
                
                # Create glow surface
                glow_surf = font.render(text, True, color)
                glow_surf.set_alpha(alpha)
                
                # Draw offset glow layers
                glow_rect = glow_surf.get_rect(center=(x + i//2, y + i//2))
                self.screen.blit(glow_surf, glow_rect)
        
        # Main text
        text_surf = font.render(text, True, color)
        text_rect = text_surf.get_rect(center=(x, y))
        self.screen.blit(text_surf, text_rect)
        
        # Scanline effect
        if glow:
            pygame.draw.line(self.screen, WHITE, 
                           (text_rect.left, text_rect.centery + 2), 
                           (text_rect.right, text_rect.centery + 2), 1)
    
    def draw_quantum_background(self):
        # Quantum field effect
        for i in range(0, SCREEN_WIDTH, 80):
            for j in range(0, SCREEN_HEIGHT, 80):
                wave_x = math.sin(self.time * 0.01 + i * 0.005) * 30
                wave_y = math.cos(self.time * 0.01 + j * 0.005) * 30
                
                color_idx = int((i + j + self.time * 10) / 80) % len(NEON_COLORS)
                color = NEON_COLORS[color_idx]
                
                # Create surface for alpha blending
                surf = pygame.Surface((20, 20), pygame.SRCALPHA)
                alpha_color = (*color, 30)
                pygame.draw.circle(surf, alpha_color, (10, 10), 8)
                self.screen.blit(surf, (i + wave_x, j + wave_y))
        
        # Energy streams
        self.draw_energy_streams()
        
    def draw_energy_streams(self):
        for i in range(5):
            points = []
            start_x = SCREEN_WIDTH * (i / 5.0)
            
            for y in range(0, SCREEN_HEIGHT, 20):
                wave = math.sin(self.time * 0.02 + y * 0.01 + i) * 100
                points.append((start_x + wave, y))
            
            if len(points) > 1:
                color = NEON_COLORS[i % len(NEON_COLORS)]
                
                # Draw line segments
                for j in range(len(points) - 1):
                    try:
                        pygame.draw.line(self.screen, color, points[j], points[j + 1], 2)
                    except:
                        pass
    
    def draw_3d_objects(self):
        positions = [
            (SCREEN_WIDTH // 4, SCREEN_HEIGHT // 4),
            (3 * SCREEN_WIDTH // 4, SCREEN_HEIGHT // 4),
            (SCREEN_WIDTH // 2, 3 * SCREEN_HEIGHT // 4)
        ]
        
        for i, shape in enumerate(self.shapes):
            shape.update(0.016)  # 60 FPS delta time
            color = NEON_COLORS[i % len(NEON_COLORS)]
            shape.draw(self.screen, positions[i], color, 60)
    
    def draw_section_indicator(self):
        indicator_y = SCREEN_HEIGHT - 60
        total_width = len(self.sections) * 120
        start_x = (SCREEN_WIDTH - total_width) // 2
        
        for i, section in enumerate(self.sections):
            x = start_x + i * 120
            is_current = (i == self.current_section)
            
            if is_current:
                color = NEON_COLORS[i % len(NEON_COLORS)]
                pygame.draw.circle(self.screen, color, (x, indicator_y), 15)
                
                # Section name
                text_surf = self.font_small.render(section, True, WHITE)
                text_rect = text_surf.get_rect(center=(x, indicator_y - 30))
                self.screen.blit(text_surf, text_rect)
            else:
                pygame.draw.circle(self.screen, GRAY, (x, indicator_y), 10, 2)
    
    def draw_home_section(self):
        center_x = SCREEN_WIDTH // 2
        center_y = SCREEN_HEIGHT // 2
        
        content = self.section_content["HOME"]
        
        # Animated title
        title_color = NEON_COLORS[int(self.time * 0.05) % len(NEON_COLORS)]
        self.draw_holographic_text(content["title"], center_x, center_y - 100, 
                                 self.font_title, title_color, True)
        
        # Subtitle
        subtitle_color = NEON_COLORS[(int(self.time * 0.05) + 2) % len(NEON_COLORS)]
        self.draw_holographic_text(content["subtitle"], center_x, center_y - 20,
                                 self.font_medium, subtitle_color, False)
        
        # Description
        self.draw_holographic_text(content["description"], center_x, center_y + 40,
                                 self.font_small, WHITE, False)
        
        # Tech symbols
        symbols = ["⚡", "🚀", "💻", "🔧", "🌐"]
        symbol_y = center_y + 120
        spacing = SCREEN_WIDTH // (len(symbols) + 1)
        
        for i, symbol in enumerate(symbols):
            x = spacing * (i + 1)
            bounce = math.sin(self.time * 0.02 + i) * 20
            color = NEON_COLORS[i % len(NEON_COLORS)]
            
            self.draw_holographic_text(symbol, x, int(symbol_y + bounce),
                                     self.font_large, color, True)
    
    def draw_content_section(self, section_name):
        if section_name not in self.section_content:
            return
            
        content = self.section_content[section_name]
        center_x = SCREEN_WIDTH // 2
        start_y = SCREEN_HEIGHT // 6
        
        # Title
        title_color = NEON_COLORS[self.current_section % len(NEON_COLORS)]
        self.draw_holographic_text(content["title"], center_x, start_y,
                                 self.font_large, title_color, True)
        
        y_offset = start_y + 120
        
        if "content" in content:
            # Multi-line content
            for i, line in enumerate(content["content"]):
                if line:  # Skip empty lines
                    color = WHITE
                    animation_offset = math.sin(self.time * 0.02 + i * 0.2) * 10
                    self.draw_holographic_text(line, center_x + animation_offset, 
                                             y_offset + i * 40, self.font_small, color, False)
                else:
                    y_offset += 20
                    
        elif "projects" in content:
            for i, project in enumerate(content["projects"]):
                color = NEON_COLORS[i % len(NEON_COLORS)]
                self.draw_holographic_text(project, center_x, y_offset + i * 60,
                                         self.font_small, color, False)
                                         
        elif "experiences" in content:
            for i, exp in enumerate(content["experiences"]):
                color = NEON_COLORS[i % len(NEON_COLORS)]
                self.draw_holographic_text(exp, center_x, y_offset + i * 70,
                                         self.font_small, color, False)
                                         
        elif "achievements" in content:
            for i, achievement in enumerate(content["achievements"]):
                color = NEON_COLORS[i % len(NEON_COLORS)]
                self.draw_holographic_text(achievement, center_x, y_offset + i * 60,
                                         self.font_small, color, False)
                                         
        elif "contacts" in content:
            for i, contact in enumerate(content["contacts"]):
                color = NEON_COLORS[i % len(NEON_COLORS)]
                self.draw_holographic_text(contact, center_x, y_offset + i * 60,
                                         self.font_small, color, False)
                                         
        elif "categories" in content:
            # Skills section with circular arrangement
            categories = list(content["categories"].items())
            cols = 3
            rows = 2
            
            for row in range(rows):
                for col in range(cols):
                    idx = row * cols + col
                    if idx < len(categories):
                        cat_name, skills = categories[idx]
                        
                        cell_x = (col + 1) * SCREEN_WIDTH // (cols + 1)
                        cell_y = start_y + 150 + row * 200
                        
                        # Category title
                        cat_color = NEON_COLORS[idx % len(NEON_COLORS)]
                        self.draw_holographic_text(cat_name, cell_x, cell_y - 60,
                                                 self.font_medium, cat_color, True)
                        
                        # Skills in circle
                        radius = 80
                        for i, skill in enumerate(skills):
                            angle = i * 2 * math.pi / len(skills) + self.time * 0.005
                            skill_x = cell_x + int(math.cos(angle) * radius)
                            skill_y = cell_y + int(math.sin(angle) * radius)
                            
                            skill_color = NEON_COLORS[(idx + i) % len(NEON_COLORS)]
                            self.draw_holographic_text(skill, skill_x, skill_y,
                                                     self.font_small, skill_color, False)
    
    def handle_input(self):
        for event in pygame.event.get():
            if event.type == pygame.QUIT:
                return False
            elif event.type == pygame.KEYDOWN:
                if event.key == pygame.K_ESCAPE:
                    return False
                elif event.key == pygame.K_LEFT:
                    self.current_section = (self.current_section - 1) % len(self.sections)
                elif event.key == pygame.K_RIGHT:
                    self.current_section = (self.current_section + 1) % len(self.sections)
            elif event.type == pygame.MOUSEBUTTONDOWN:
                # Navigate with mouse clicks
                if event.pos[0] < SCREEN_WIDTH // 2:
                    self.current_section = (self.current_section - 1) % len(self.sections)
                else:
                    self.current_section = (self.current_section + 1) % len(self.sections)
            elif event.type == pygame.MOUSEMOTION:
                # Add particles at mouse position
                x, y = event.pos
                if len(self.particles) < 300:
                    color = random.choice(NEON_COLORS)
                    self.particles.append(Particle(x, y, color))
        
        return True
    
    def run(self):
        running = True
        
        while running:
            dt = self.clock.tick(FPS) / 1000.0
            self.time += 1
            
            running = self.handle_input()
            
            # Update
            self.update_particles()
            
            # Clear screen
            self.screen.fill(BLACK)
            
            # Draw background effects
            self.draw_quantum_background()
            
            # Draw particles
            for particle in self.particles:
                particle.draw(self.screen)
            
            # Draw 3D objects
            self.draw_3d_objects()
            
            # Draw current section
            current_section_name = self.sections[self.current_section]
            if current_section_name == "HOME":
                self.draw_home_section()
            else:
                self.draw_content_section(current_section_name)
            
            # Draw UI
            self.draw_section_indicator()
            
            # Instructions
            instructions = "← → Arrow Keys or Mouse Click to Navigate | ESC to Exit"
            text_surf = self.font_small.render(instructions, True, WHITE)
            text_rect = text_surf.get_rect(center=(SCREEN_WIDTH // 2, SCREEN_HEIGHT - 20))
            self.screen.blit(text_surf, text_rect)
            
            pygame.display.flip()
        
        pygame.quit()
        sys.exit()

if __name__ == "__main__":
    resume = Resume3D()
    resume.run()
