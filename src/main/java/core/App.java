package core;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Hello world!
 *
 */
public class App {
    private static PerlinNoise perlin = new PerlinNoise(false);
    private static int width = 512;
    private static int height = 512;

    // Multi-Octave Noise Parameters
    private static int octaves = 10;
    private static double persistence = 0.7;
    private static double frequency = 0.010;
    private static double amplitude = 0.01;

    public static void main(String[] args) {

        // Create buffered image
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        // Generate Perlin noise and write to image
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                double noiseValue = octaveNoise(x, y, 0, perlin, octaves, persistence, frequency, amplitude);
                int rgb = noiseToColor(noiseValue);
                image.setRGB(x, y, rgb);
            }
        }

        // Save image
        try {
            File outputFile = new File("perlin_noise.png");
            ImageIO.write(image, "png", outputFile);
        } catch (IOException e) {
            System.out.println("Error: " + e);
        }
    }

    private static int noiseToColor(double noiseValue) {
        // Normalize to [0, 1]
        double value = (noiseValue + 1) / 2;
        value = Math.min(Math.max(value, 0), 1); // Clamp value between 0 and 1

        // Example: Convert to a blue-to-red gradient
        int red = (int) (value * 255);
        int blue = 255 - red;
        int green = (int) (value * 200); // Adjust green for variation

        return (red << 16) | (green << 8) | blue;
    }

    private static double octaveNoise(int x, int y, int z, PerlinNoise perlin, int octaves, double persistence,
            double frequency, double amplitude) {
        double total = 0;
        double maxValue = 0; // Used for normalizing result
        double amp = amplitude;
        double freq = frequency;

        for (int i = 0; i < octaves; i++) {
            total += perlin.noise(x * freq, y * freq, z * freq) * amp;
            maxValue += amp;
            amp *= persistence;
            freq *= 2;
        }

        return total / maxValue;
    }
}
