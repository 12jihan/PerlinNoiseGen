package core;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class PerlinNoise {

    private final int[] permutation = new int[512];
    Gson gson = new Gson();

    public PerlinNoise(boolean randomize) {
        List<Integer> tempList = new ArrayList<>();

        if (randomize == true) {
            for (int i = 0; i < 256; i++) {
                tempList.add(i); // Fill with values 0 to 255
            }
            Collections.shuffle(tempList); // Shuffle the list
            // Convert List to JSON
            String json = gson.toJson(tempList);
            try (FileWriter writer = new FileWriter("output.json")) {
                writer.write(json);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try (FileReader reader = new FileReader("output.json")) {
                // Define the type of the data in the JSON file
                Type listType = new TypeToken<Integer[]>() {}.getType();

                // Deserialize JSON to Array
                Integer[] dataArray = gson.fromJson(reader, listType);
                tempList = Arrays.asList(dataArray);
                // Output the array
                System.out.println(Arrays.toString(dataArray));
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        for (int i = 0; i < 256; i++) {
            permutation[i] = tempList.get(i); // Copy values to permutation array
            permutation[256 + i] = permutation[i]; // Repeat the permutation at the second half of the array
        }
    }

    public double noise(double x, double y, double z) {
        int X = (int) Math.floor(x) & 255;
        int Y = (int) Math.floor(y) & 255;
        int Z = (int) Math.floor(z) & 255;

        x -= Math.floor(x);
        y -= Math.floor(y);
        z -= Math.floor(z);

        double u = fade(x);
        double v = fade(y);
        double w = fade(z);

        int A = permutation[X] + Y, AA = permutation[A] + Z, AB = permutation[A + 1] + Z;
        int B = permutation[X + 1] + Y, BA = permutation[B] + Z, BB = permutation[B + 1] + Z;

        return lerp(w, lerp(v, lerp(u, grad(permutation[AA], x, y, z),
                grad(permutation[BA], x - 1, y, z)),
                lerp(u, grad(permutation[AB], x, y - 1, z),
                        grad(permutation[BB], x - 1, y - 1, z))),
                lerp(v, lerp(u, grad(permutation[AA + 1], x, y, z - 1),
                        grad(permutation[BA + 1], x - 1, y, z - 1)),
                        lerp(u, grad(permutation[AB + 1], x, y - 1, z - 1),
                                grad(permutation[BB + 1], x - 1, y - 1, z - 1))));
    }

    private static double fade(double t) {
        return t * t * t * (t * (t * 6 - 15) + 10);
    }

    private static double lerp(double t, double a, double b) {
        return a + t * (b - a);
    }

    private static double grad(int hash, double x, double y, double z) {
        int h = hash & 15;
        double u = h < 8 ? x : y;
        double v = h < 4 ? y : h == 12 || h == 14 ? x : z;
        return ((h & 1) == 0 ? u : -u) + ((h & 2) == 0 ? v : -v);
    }
}
