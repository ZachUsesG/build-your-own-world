# Build Your Own World (Procedural World Generation Engine)

A Java-based 2D procedural world generation game developed for UC Berkeley’s CS61BL course and expanded into an independent project.  
This version implements advanced algorithms for randomized terrain, biome generation, and world connectivity using recursive spatial logic, deterministic randomness, and interactive rendering.

---

## Overview
Each world is generated from a numeric seed, producing a unique but reproducible layout of rooms, corridors, and natural biomes.  
Players can explore, move freely, and save or reload worlds using seed-based replay.

---

## Core Features
- Procedural world generation through a deterministic pseudorandom engine that creates rooms, corridors, and environmental features  
- Biome system with six dynamic environments (forest, desert, tundra, sunflower field, cavern, and flower grove), each with unique color palettes and tiles  
- Corridor and connectivity algorithms combining minimum spanning tree logic, recursive backtracking, and corridor pruning for realistic map structure  
- Persistent save system that stores seed and movement history, allowing players to resume exact world states  
- Seed input interface for world creation, loading, and quitting  
- Real-time rendering and camera centering with smooth player navigation  

---

## Technical Implementation
- **Algorithms and Structures:**  
  - Room placement through randomized rejection sampling with overlap checks  
  - Connectivity enforced using minimum spanning tree and L-shaped corridor generation  
  - Recursive dead-end resolution and wall reconstruction for path realism  
  - Breadth-first search used to validate connectivity and repair isolated areas  
  - Clustered biome texture generation using weighted probabilities  
- **Rendering:**  
  - Custom rendering engine built with StdDraw for real-time 2D display  
  - Centered viewport that tracks player position  
  - On-hover tile description overlay for contextual exploration  
- **Code Architecture:**  
  - Modular structure with separate packages for `core`, `tileengine`, and `utils`  
  - Object-oriented biome and palette design supporting extensibility  
  - Data persistence implemented through file I/O in `save.txt`  

---

## Tools and Technologies
- **Language:** Java  
- **Libraries:** StdDraw, java.awt, edu.princeton.cs.algs4  
- **IDE:** IntelliJ IDEA  
- **Version Control:** Git and GitHub  
- **Project Type:** Interactive procedural generation game  

---

## How to Run
1. Clone the repository:
   ```bash
   git clone https://github.com/ZachUsesG/build-your-own-world.git
   cd build-your-own-world/proj3
