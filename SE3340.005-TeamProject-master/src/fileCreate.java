import java.io.FileNotFoundException;
import java.util.Date;
import java.util.Random;
import java.io.PrintWriter;
import java.util.*;
import java.io.*;

public class fileCreate {
    public static void main(String[] args) throws IOException{
        PrintWriter writer = new PrintWriter("source.txt");
        Random random = new Random(new Date().getTime());
        
        for (int i = 0; i < 100; i++) {
            generateGrid(16,writer, random);
            writer.println("");
            // System.out.println("####################");
        }
        writer.close();

    }

    public static void generateGrid(int bound, PrintWriter writer, Random random) throws IOException {
        int[] grid = new int[28];
        for (int i = 0; i < 7; i++)
            grid[i] = random.nextInt(bound) + 1;
        
        int index = 7;
        for (int r = 6; r > 0; r--) {
            //for each row iterate over previous rows items
            int startingPrev = index - (r + 1);
            int ending = startingPrev + (r);
            for (int c = startingPrev; c < ending; c++) {
                int leftSide = grid[c];
                int rightSide = grid[c + 1];
                if (leftSide + rightSide >= 1000) {
                    writer.println("$$$$$$$$!!!!!!!!!!!!!**********");
                }
                grid[index] = leftSide + rightSide;
                index++;
            }
        }
        int counter = 1;
        index = grid.length - 1;
        int[][] vals = {{2, 7, 9, 13, 15, 20, 22},
                {7, 8, 12, 15, 16, 17, 18, 22, 26},
                {5, 7, 8, 12, 13, 14, 18, 23}};
        int[] chosen = vals[new Random().nextInt(3)];
        for (int i = 7; i > 0; i--) {
            for (int c = 0; c < counter; c++) {
                writer.print(grid[index]);
                if(grid[index] < 100){
                    writer.print(" ");
                }
                if(grid[index] < 10){
                    writer.print(" ");
                }
                index--;
            }
            //System.out.println("");
            counter++;
        }
        writer.println();
        counter = 1;
        index = grid.length - 1;
        for (int i = 7; i > 0; i--) {
            for (int c = 0; c < counter; c++) {
                if (contains(chosen, index)) {
                    writer.print("0  ");
                } else {
                    writer.print(grid[index]);
                    if(grid[index] < 100){
                        writer.print(" ");
                    }
                    if(grid[index] < 10){
                        writer.print(" ");
                    }
                }
                index--;
            }
            //System.out.println("");
            counter++;
        }
    }

    public static boolean contains(final int[] array, final int v) {

        boolean result = false;

        for (int i : array) {
            if (i == v) {
                result = true;
                break;
            }
        }

        return result;
    }
}
