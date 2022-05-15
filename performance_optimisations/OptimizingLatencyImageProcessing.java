package performance_optimisations;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class OptimizingLatencyImageProcessing {

    public static final String SOURCE_FILE = "";
    public static final String DEST_FILE = "./out/many-flowers.jpg";

    public static void main(String[] args) throws IOException {
        BufferedImage originalImg = ImageIO.read(new File(SOURCE_FILE));
        BufferedImage resultImg = new BufferedImage(originalImg.getWidth(), originalImg.getHeight(), BufferedImage.TYPE_INT_RGB);

        // start of single-thread application
        long startTime = System.currentTimeMillis();
        // recolorSingleThreaded(originalImg, resultImg);
        recolorMultiThreaded(originalImg, resultImg, 4);
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        File outputFile = new File(DEST_FILE);
        ImageIO.write(resultImg, "jpg", outputFile);
        System.out.println(duration);
    }

    public static void recolorMultiThreaded(BufferedImage originalImage, BufferedImage resultImage, int numberOfThreads) {
        List<Thread> threads = new ArrayList<>();
        int width = originalImage.getWidth();
        int height = originalImage.getHeight() / numberOfThreads; // breaking the img based into n parts divided across height
        // based on the no of threads
        for (int i = 0; i < numberOfThreads; i++) {
            final int threadMultiplier = i;

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    int leftCorner = 0;
                    int topCorner = height * threadMultiplier;
                    recolorImage(originalImage, resultImage, leftCorner, topCorner, width, height);
                }
            });
            threads.add(thread);
        }

        for (Thread thread : threads) {
            thread.start();
        }

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void recolorSingleThreaded(BufferedImage originalImage, BufferedImage resultImage) {
        recolorImage(originalImage, resultImage, 0, 0, originalImage.getWidth(), originalImage.getHeight());
    }

    public static void recolorImage(BufferedImage originalImage, BufferedImage resultImage, int leftCorner, int topCorner, int width, int height) {
        for (int i = leftCorner; i < leftCorner + width && i < originalImage.getWidth(); i++) {
            for (int j = topCorner; j < topCorner + height && j < originalImage.getHeight(); j++) {
                recolorPixel(originalImage, resultImage, i, j);
            }
        }
    }

    public static void recolorPixel(BufferedImage originalImage, BufferedImage resultImage, int x, int y) {
        int rgb = originalImage.getRGB(x, y);
        int red = getRed(rgb);
        int green = getGreen(rgb);
        int blue = getBlue(rgb);
        int newRed, newGreen, newBlue;
        if (isShadeOfGray(red, green, blue)) {
            // to get a purple color (a combination of red and blue) after the transformation of the pixel
            newRed = Math.min(red + 10, 255);
            newGreen = Math.max(green - 80, 0);
            newBlue = Math.max(0, blue - 20);
        } else {
            newRed = red;
            newGreen = green;
            newBlue = blue;
        }
        int newRGB = createRGBFromColors(newRed, newGreen, newBlue);
        setRGB(resultImage, x, y, newRGB);
    }

    public static void setRGB(BufferedImage image, int x, int y, int rgb) {
        image.getRaster().setDataElements(x, y, image.getColorModel().getDataElements(rgb, null));
    }

    public static boolean isShadeOfGray(int red, int green, int blue) {
        return Math.abs(red - green) < 30 && Math.abs(red - blue) < 30 && Math.abs(green - blue) < 30;
    }

    public static int createRGBFromColors(int red, int green, int blue) {
        int rgb = 0;
        rgb |= blue; // since it's the rightmost, we simply add it by applying logical OR btw the 2 values
        rgb |= green << 8; // shift the green to its position by shifting it to the left by 8 bits and add.
        rgb |= red << 16; // by 2 bytes to left
        rgb |= 0xFF000000; //we want the picture to be opaque, so we add a full 100% alpha value (255 or 0xFF000000)
        return rgb;
    }

    public static int getRed(int rgb) {
        return (rgb & 0x00FF0000) >> 16; // mask the alpha, green and blue and shift the red value by 2-bytes(16 bits) to right.
    }

    public static int getGreen(int rgb) {
        /* 0xFF = 255 in hexadecimal. green component is the second most from the right, so shift
        the value 8 bits to the right*/
        return (rgb & 0x0000FF00) >> 8;
    }

    public static int getBlue(int rgb) {
        /* getting the blue value out of the rgb pixel value by applying bit mask on the pixel,
        making all the components 0 except the right most byte (blue component)*/
        return rgb & 0x000000FF;
    }
}
