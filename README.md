## The Convex Hull
## 📘 Overview

This project provides:

* **Convex Hull computation** using the **Graham Scan algorithm** (`HullBuilder.java`)
* **2D point representation and geometry utilities** (`Point2d.java`)
* **Command-line tools** to:

  * Print the convex hull of a point set (`PrintHull.java`)
  * Test if a point lies within a convex hull (`TestContainment.java`)
* **Graphical viewer** to visualize, manipulate, and compute convex hulls interactively (`HullViewer225.java`)

---

## 🧩 Components

### 1. `Point2d.java`

Defines a 2D point with double-precision coordinates and basic geometric utilities:

* Compute distance between points
* Vector offsets and negation
* Determine chirality (left/right turn) among three points

### 2. `HullBuilder.java`

Implements the convex hull algorithm:

* Uses **Graham Scan** for efficient hull construction
* Handles duplicate and collinear points robustly
* Provides `isInsideHull()` to test point inclusion within the computed hull

### 3. `PrintHull.java`

Command-line tool to compute and print the convex hull:

```bash
java PrintHull <points_file>
```

* Reads a text file containing one `(x y)` pair per line
* Outputs convex hull points in order

### 4. `TestContainment.java`

Command-line tool to check if a given point lies within a convex hull:

```bash
java TestContainment <points_file> <x> <y>
```

* Reports whether the point `(x, y)` is inside or outside the convex hull

### 5. `HullViewer225.java`

A graphical Swing-based application to visualize and experiment with point sets:

* Add points interactively
* Compute and display the convex hull
* Zoom, pan, and reset views
* Save/load point sets from files

Run using:

```bash
java HullViewer225
```

or load an initial point set:

```bash
java HullViewer225 <points_file>
```

---

## 📂 Input File Format

Text files used for both CLI tools and the viewer should follow this format:

```
x1 y1
x2 y2
x3 y3
...
```

Each line represents a single 2D point.

---

## ⚙️ Compilation & Execution

### Compile all classes:

```bash
javac *.java
```

### Example usage:

```bash
# Compute convex hull
java PrintHull points.txt

# Test point containment
java TestContainment points.txt 3.5 2.2

# Launch viewer
java HullViewer225 points.txt
```

---

## 🧠 Algorithm Details

The convex hull is computed using the **Graham Scan** algorithm:

1. Sort points by x (and y) coordinate
2. Construct upper and lower hulls using orientation tests
3. Merge hulls into a single ordered vertex list

Point containment checks are performed via **edge orientation tests** to determine if a point lies inside the convex polygon.

---

## 🧾 License

All rights reserved by the respective authors and the University of Victoria.

---

Would you like me to format this README with Markdown enhancements (badges, table of contents, etc.) for a more polished GitHub presentation?
