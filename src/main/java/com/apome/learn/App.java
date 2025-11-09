package com.apome.learn;

import java.util.Set;
import java.util.HashSet;
import java.util.Objects;

class Pixel {
    public int xCord;
    public int yCord;

    public Pixel(int x, int y) {
        this.xCord = x;
        this.yCord = y;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Pixel other = (Pixel) obj;
        return xCord == other.xCord && yCord == other.yCord;
    }

    @Override
    public int hashCode() {
        return Objects.hash(xCord, yCord);
    }

    @Override
    public String toString() {
        return "[" + xCord + "," + yCord + "]";
    }
}

class World {
    public Set<Pixel> livingSet;

    public static void populate(Set<Pixel> world, int pixelCount) {
        for (int i = 0; i < pixelCount; i++) {
            world.add(new Pixel((int) (10 * Math.random()), (int) (10 * Math.random())));
        }
    }

    public Set<Pixel> livingNeighbourSet(Pixel pixel) {
        Set<Pixel> neighbours = new HashSet<>();
        neighbours.add(new Pixel(pixel.xCord + 1, pixel.yCord));
        neighbours.add(new Pixel(pixel.xCord - 1, pixel.yCord));
        neighbours.add(new Pixel(pixel.xCord, pixel.yCord + 1));
        neighbours.add(new Pixel(pixel.xCord, pixel.yCord - 1));
        neighbours.add(new Pixel(pixel.xCord + 1, pixel.yCord + 1));
        neighbours.add(new Pixel(pixel.xCord - 1, pixel.yCord - 1));
        neighbours.add(new Pixel(pixel.xCord + 1, pixel.yCord - 1));
        neighbours.add(new Pixel(pixel.xCord - 1, pixel.yCord + 1));

        Set<Pixel> livingNeighbours = new HashSet<>();
        for (Pixel p : neighbours) {
            if (livingSet.contains(p)) {
                livingNeighbours.add(p);
            }
        }
        return livingNeighbours;
    }

    public Set<Pixel> deadNeighbourSet(Pixel pixel) {
        Set<Pixel> neighbours = new HashSet<>();
        neighbours.add(new Pixel(pixel.xCord + 1, pixel.yCord));
        neighbours.add(new Pixel(pixel.xCord - 1, pixel.yCord));
        neighbours.add(new Pixel(pixel.xCord, pixel.yCord + 1));
        neighbours.add(new Pixel(pixel.xCord, pixel.yCord - 1));
        neighbours.add(new Pixel(pixel.xCord + 1, pixel.yCord + 1));
        neighbours.add(new Pixel(pixel.xCord - 1, pixel.yCord - 1));
        neighbours.add(new Pixel(pixel.xCord + 1, pixel.yCord - 1));
        neighbours.add(new Pixel(pixel.xCord - 1, pixel.yCord + 1));

        Set<Pixel> deadNeighbours = new HashSet<>();
        for (Pixel p : neighbours) {
            if (!livingSet.contains(p)) {
                deadNeighbours.add(p);
            }
        }
        return deadNeighbours;
    }

    @Override
    public String toString() {
        return "World " + livingSet.toString();
    }

    public void timeTick() {
        Set<Pixel> pixelsToRemove = new HashSet<>();
        Set<Pixel> pixelsToAdd = new HashSet<>();

        // Determine which living pixels will die
        for (Pixel pixel : livingSet) {
            int livingNeighbours = livingNeighbourSet(pixel).size();
            if (livingNeighbours < 2 || livingNeighbours > 3) {
                pixelsToRemove.add(pixel);
            }
        }

        // Consider births: check dead neighbours of all living pixels
        Set<Pixel> candidateDeadPixels = new HashSet<>();
        for (Pixel pixel : livingSet) {
            candidateDeadPixels.addAll(deadNeighbourSet(pixel));
        }

        for (Pixel candidate : candidateDeadPixels) {
            if (livingNeighbourSet(candidate).size() == 3) {
                pixelsToAdd.add(candidate);
            }
        }

        // Update the living set
        // System.out.println("pixelsToRemove: "+pixelsToRemove.toString());
        // System.out.println("pixelsToAdd: "+pixelsToAdd.toString());

        livingSet.removeAll(pixelsToRemove);
        livingSet.addAll(pixelsToAdd);
    }

    public void printASCIIView(int width, int height){
        char[] topRuler = new char[width * 3];
        for (int i = 0; i < width; i+=1) {
            topRuler[3*i+0] = ' ';
            topRuler[3*i+1] = ' ';
            topRuler[3*i+2] = Character.forDigit(i, width);
        }
        System.out.println(new String(topRuler));
        for (int i = 0; i < height; i++) {
            char[] asciiLine = new char[width * 3];
            
            System.out.print(Character.forDigit(i, width));
            for (int k = 0; k < asciiLine.length; k++) {
                asciiLine[k] = ' ';
            }
            for (int x = 0; x < width; x++) {
                if (livingSet.contains(new Pixel(x, i))) {
                    asciiLine[3*x + 1] = '@';
                } else {
                    asciiLine[3*x + 1] = '-';
                }
            }
            System.out.println(new String(asciiLine));
        }  
    }

    public World() {
        livingSet = new HashSet<>();
        populate(livingSet, 100);
    }
}

public class App {
    public static final String ANSI_CLS = "\u001b[2J";
    public static final String ANSI_HOME = "\u001b[H";

    public static void main(String[] args) {
        World world = new World();
        while (true) {
            // Clear the screen using ANSI escape codes
            System.out.print("\033c");
            System.out.flush();
                
            
            // Print the current state
            System.out.println("\033[31mState:\033[0m");
            world.printASCIIView(20, 20);
            
            // Advance one tick
            world.timeTick();
            
            // Wait for 500 milliseconds (2 ticks per second)
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}
