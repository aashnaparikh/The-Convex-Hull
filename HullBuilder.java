/* HullBuilder.java
   CSC 225 - Summer 2025
   Starter code for Convex Hull Builder. Do not change the signatures
   of any of the methods below (you may add other methods as needed).
   B. Bird - 2025-05-12
   
   Simple implementation focusing on correctness and good performance.
   Uses Graham scan for hull computation and linear scan for point-in-polygon.
   
   Aashna Parikh, V010157821, 15th June 2025
*/

import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class HullBuilder{
    
    private ArrayList<Point2d> points;
    
    /* Constructor */
    public HullBuilder() {
        points = new ArrayList<Point2d>(); //empty list of points to collect dots
    }
    
    /* addPoint(P)
       Add the point P to the internal point set for this object.
       Note that there is no facility to delete points (other than
       destroying the HullBuilder and creating a new one). 
    */
    public void addPoint(Point2d P){
        points.add(P); //adds new dot to the list
    }
    
    /* getHull()
       Return a java.util.LinkedList object containing the points
       in the convex hull, in order (such that iterating over the list
       will produce the same ordering of vertices as walking around the 
       polygon).
    */
    public LinkedList<Point2d> getHull(){
        if (points.size() == 0) {
            return new LinkedList<Point2d>(); 
        } //if no points were added, theres no shape, return an empty list
        
        if (points.size() == 1) {
            LinkedList<Point2d> result = new LinkedList<Point2d>();
            result.add(points.get(0));
            return result;
        } //if there is one point, return the single point as the "hull"
        
        if (points.size() == 2) {
            LinkedList<Point2d> result = new LinkedList<Point2d>();
            result.add(points.get(0));
            result.add(points.get(1));
            return result;
        } //if there are two points, return them both, its a striaght line
        
        return grahamScan(); 
    }
    
    /* isInsideHull(P)
       Given a point P, return true if P lies inside the convex hull
       of the current point set (note that P may not be part of the
       current point set). Return false otherwise.
     */
    public boolean isInsideHull(Point2d P){
        LinkedList<Point2d> hull = getHull(); 
        
        if (hull.size() < 3) { 
            if (hull.size() == 2) { //if the hul is just a line between two points, point P lies exactly on the line segment
                return isOnLineSegment(P, hull.get(0), hull.get(1));
            } else if (hull.size() == 1) { //if the hull is single dot, return the same point
                return samePoint(P, hull.get(0));
            }
            return false; //if no points exist, P can't be inside anything, return false
        }
        
        return isInsideConvexPolygon(P, hull); //if we have 3 or more points, check if point P is inside that polygon
    }
    
 
    private LinkedList<Point2d> grahamScan() {
        ArrayList<Point2d> uniquePoints = removeDuplicates(); //remove any duplicate points
        if (uniquePoints.size() < 3) { //if fewer than 3 points, we cant form a polygon
            return new LinkedList<Point2d>(uniquePoints);
        }
        
        Collections.sort(uniquePoints, new Comparator<Point2d>() {
            public int compare(Point2d a, Point2d b) {
                if (Math.abs(a.x - b.x) < 1e-9) {
                    return Double.compare(a.y, b.y);
                }
                return Double.compare(a.x, b.x);
            } //sorting the points happen first by x-coordinate, if x is the same, then by y-cordinate
        });
        
        // Upper Hull
        ArrayList<Point2d> upper = new ArrayList<Point2d>(); //list will hold the points that form the upper edge of the convex hull
        for (Point2d p : uniquePoints) { //go through sorted points from left to right
            while (upper.size() >= 2) { //last two points already in the upper hull to decide what happens when yout ry to add the new one
                Point2d a = upper.get(upper.size() - 2);
                Point2d b = upper.get(upper.size() - 1); //if it wasnt a right turn, it doesnt help the outer boundary
                if (Point2d.chirality(a, b, p) > 0) {
                    break; //chirality tells us the direction of the turn made by three points, if its right turn then its outward, so we break the loop and accept point
                }
                upper.remove(upper.size() - 1); //right turn doesnt help form the outer boundary
            }
            upper.add(p); //all unwanted middle points are removed, we add the current point to the hull
        }
        
        //Lower hull
        ArrayList<Point2d> lower = new ArrayList<Point2d>(); //holds points for the bottom edge of the convex hull
        for (int i = uniquePoints.size() - 1; i >= 0; i--) {
            Point2d p = uniquePoints.get(i); //loop through sorted points in reverse order
            while (lower.size() >= 2) {
                Point2d a = lower.get(lower.size() - 2);
                Point2d b = lower.get(lower.size() - 1); //last two points on the lower hull
                if (Point2d.chirality(a, b, p) > 0) {
                    break; // Right turn - keep point
                }
                lower.remove(lower.size() - 1); // Remove point that makes left turn
            }
            lower.add(p);
        }
        
        LinkedList<Point2d> hull = new LinkedList<Point2d>(); //all convex hull points
        for (int i = 0; i < upper.size() - 1; i++) {
            hull.add(upper.get(i)); //last point of the upper hull is the first point of the lower hull
        }
        for (int i = 0; i < lower.size() - 1; i++) {
            hull.add(lower.get(i)); //all points from the lower hull, excluding the last one
        }
        
        return hull;
    }
    
    
    private ArrayList<Point2d> removeDuplicates() {
        ArrayList<Point2d> sorted = new ArrayList<Point2d>(points); //make a copy of the list of points
        Collections.sort(sorted, new Comparator<Point2d>() {
            public int compare(Point2d a, Point2d b) {
                if (Math.abs(a.x - b.x) < 1e-9) {
                    return Double.compare(a.y, b.y); //sort x cordinates, if two x's are the same, use y
                }
                return Double.compare(a.x, b.x);
            }
        });
        
        ArrayList<Point2d> unique = new ArrayList<Point2d>(); //creating a new list called unique
        for (Point2d p : sorted) { //add each point if its not the same as the last one added
            if (unique.isEmpty() || !samePoint(unique.get(unique.size() - 1), p)) {  //compare points with rounding tolerance
                unique.add(p);
            }
        }
        return unique;
    }
    
   
    private boolean isInsideConvexPolygon(Point2d P, LinkedList<Point2d> hull) {
        Point2d[] vertices = hull.toArray(new Point2d[0]); //convert convex hull into a regular array to loop through it
        int n = vertices.length;
        
        for (int i = 0; i < n; i++) {
            Point2d current = vertices[i]; //current is the current corner
            Point2d next = vertices[(i + 1) % n]; //next is the next corner
            
            int turn = Point2d.chirality(current, next, P); //use chirality to determine if the point is on the left or right side of the edge
            if (turn < 0) { // P is to the left of this edge its outside the convex
                return false;
            }
        }
        return true;
    }
    
 
    private boolean samePoint(Point2d a, Point2d b) { //these points are close enough to be equal
        return Math.abs(a.x - b.x) < 1e-9 && Math.abs(a.y - b.y) < 1e-9; 
    }
    
    
    private boolean isOnLineSegment(Point2d P, Point2d a, Point2d b) { //using chiralityt o check if three points are in a straight line
        if (Point2d.chirality(a, b, P) != 0) {
            return false;
        }
 
        double minX = Math.min(a.x, b.x);
        double maxX = Math.max(a.x, b.x);
        double minY = Math.min(a.y, b.y);
        double maxY = Math.max(a.y, b.y); //making sure P is within the bounding box of a and b
        
        return P.x >= minX - 1e-9 && P.x <= maxX + 1e-9 && 
               P.y >= minY - 1e-9 && P.y <= maxY + 1e-9;
    }
}