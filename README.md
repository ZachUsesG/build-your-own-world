# Build Your Own World (Procedural World Generation Engine)

A Java-based **2D procedural world generation game** developed for UC Berkeley’s CS 61BL course and later expanded into an independent project.  
This version introduces advanced algorithms for randomized terrain, biome generation, and world connectivity using recursive spatial logic, deterministic randomness, and interactive rendering.

---

## Overview

Each world is generated from a numeric seed, producing a unique yet reproducible layout of rooms, corridors, and natural biomes.  
Players can explore freely, interact with terrain, and save or reload worlds using seed-based replay for full reproducibility.

---

## Core Features

- **Procedural World Generation:** Deterministic pseudorandom engine that creates rooms, corridors, and environmental features.  
- **Biome System:** Six dynamic environments (forest, desert, tundra, sunflower field, cavern, and flower grove) each with distinct palettes and tiles.  
- **Connectivity Algorithms:** Combines minimum spanning tree logic, recursive backtracking, and corridor pruning for natural world structure.  
- **Persistent Save System:** Stores seed and movement history, enabling exact world state recovery.  
- **Seed Input Interface:** Allows players to generate, load, or quit sessions based on custom numeric seeds.  
- **Real-Time Rendering:** Smooth navigation, camera centering, and dynamic tile updates using a custom rendering engine.

---

## Technical Implementation

### Algorithms and Structures
- **Room Placement:** Randomized rejection sampling with overlap detection.  
- **Connectivity Enforcement:** Minimum spanning tree with L-shaped corridor generation.  
- **Path Realism:** Recursive dead-end removal and wall reconstruction.  
- **World Validation:** Breadth-first search ensures full connectivity and repairs isolated zones.  
- **Biome Texturing:** Weighted-probability cluster generation for varied terrain patterns.

### Rendering
- Custom 2D rendering engine built on **StdDraw**.  
- Centered viewport tracking player movement.  
- On-hover tile descriptions for interactive exploration.

### Code Architecture
- Modular package design (`core/`, `tileengine/`, `utils/`).  
- Object-oriented biome and color palette hierarchy for easy extensibility.  
- Data persistence via file I/O (`save.txt`) to store seeds and movement history.

---

## Tools and Technologies

| Category | Details |
|-----------|----------|
| **Language** | Java |
| **Libraries** | StdDraw, java.awt, edu.princeton.cs.algs4 |
| **IDE** | IntelliJ IDEA |
| **Version Control** | Git & GitHub |
| **Project Type** | Interactive Procedural Generation Game |

---

## How to Run

1. Clone the repository:
   ```bash
   git clone https://github.com/ZachUsesG/build-your-own-world.git
   cd build-your-own-world/proj3
Open the project in IntelliJ IDEA or another Java IDE.

Build and run Main.java in the byow/Core directory.

Enter a seed to generate your world, or load a saved session.

Explore and navigate the procedurally generated environment!

Example Use Case
Input Seed: 529384
→ Generates a fully connected world with mixed forest–tundra biomes and a central cavern network.
→ Saves session data to save.txt for future reloading.

Credits
Created by Zach Makari
Developed as part of UC Berkeley’s CS 61BL curriculum and extended as an independent exploration in procedural generation and spatial algorithms.
