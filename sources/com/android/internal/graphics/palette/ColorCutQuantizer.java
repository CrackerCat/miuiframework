package com.android.internal.graphics.palette;

import android.graphics.Color;
import android.util.TimingLogger;
import com.android.internal.graphics.ColorUtils;
import com.android.internal.graphics.palette.Palette.Filter;
import com.android.internal.graphics.palette.Palette.Swatch;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

final class ColorCutQuantizer implements Quantizer {
    static final int COMPONENT_BLUE = -1;
    static final int COMPONENT_GREEN = -2;
    static final int COMPONENT_RED = -3;
    private static final String LOG_TAG = "ColorCutQuantizer";
    private static final boolean LOG_TIMINGS = false;
    private static final int QUANTIZE_WORD_MASK = 31;
    private static final int QUANTIZE_WORD_WIDTH = 5;
    private static final Comparator<Vbox> VBOX_COMPARATOR_VOLUME = new Comparator<Vbox>() {
        public int compare(Vbox lhs, Vbox rhs) {
            return rhs.getVolume() - lhs.getVolume();
        }
    };
    int[] mColors;
    Filter[] mFilters;
    int[] mHistogram;
    List<Swatch> mQuantizedColors;
    private final float[] mTempHsl = new float[3];
    TimingLogger mTimingLogger;

    private class Vbox {
        private int mLowerIndex;
        private int mMaxBlue;
        private int mMaxGreen;
        private int mMaxRed;
        private int mMinBlue;
        private int mMinGreen;
        private int mMinRed;
        private int mPopulation;
        private int mUpperIndex;

        Vbox(int lowerIndex, int upperIndex) {
            this.mLowerIndex = lowerIndex;
            this.mUpperIndex = upperIndex;
            fitBox();
        }

        /* Access modifiers changed, original: final */
        public final int getVolume() {
            return (((this.mMaxRed - this.mMinRed) + 1) * ((this.mMaxGreen - this.mMinGreen) + 1)) * ((this.mMaxBlue - this.mMinBlue) + 1);
        }

        /* Access modifiers changed, original: final */
        public final boolean canSplit() {
            return getColorCount() > 1;
        }

        /* Access modifiers changed, original: final */
        public final int getColorCount() {
            return (this.mUpperIndex + 1) - this.mLowerIndex;
        }

        /* Access modifiers changed, original: final */
        public final void fitBox() {
            int[] colors = ColorCutQuantizer.this.mColors;
            int[] hist = ColorCutQuantizer.this.mHistogram;
            int minRed = Integer.MAX_VALUE;
            int minBlue = Integer.MAX_VALUE;
            int minGreen = Integer.MAX_VALUE;
            int maxRed = Integer.MIN_VALUE;
            int maxBlue = Integer.MIN_VALUE;
            int maxGreen = Integer.MIN_VALUE;
            int count = 0;
            for (int i = this.mLowerIndex; i <= this.mUpperIndex; i++) {
                int color = colors[i];
                count += hist[color];
                int r = ColorCutQuantizer.quantizedRed(color);
                int g = ColorCutQuantizer.quantizedGreen(color);
                int b = ColorCutQuantizer.quantizedBlue(color);
                if (r > maxRed) {
                    maxRed = r;
                }
                if (r < minRed) {
                    minRed = r;
                }
                if (g > maxGreen) {
                    maxGreen = g;
                }
                if (g < minGreen) {
                    minGreen = g;
                }
                if (b > maxBlue) {
                    maxBlue = b;
                }
                if (b < minBlue) {
                    minBlue = b;
                }
            }
            this.mMinRed = minRed;
            this.mMaxRed = maxRed;
            this.mMinGreen = minGreen;
            this.mMaxGreen = maxGreen;
            this.mMinBlue = minBlue;
            this.mMaxBlue = maxBlue;
            this.mPopulation = count;
        }

        /* Access modifiers changed, original: final */
        public final Vbox splitBox() {
            if (canSplit()) {
                int splitPoint = findSplitPoint();
                Vbox newBox = new Vbox(splitPoint + 1, this.mUpperIndex);
                this.mUpperIndex = splitPoint;
                fitBox();
                return newBox;
            }
            throw new IllegalStateException("Can not split a box with only 1 color");
        }

        /* Access modifiers changed, original: final */
        public final int getLongestColorDimension() {
            int redLength = this.mMaxRed - this.mMinRed;
            int greenLength = this.mMaxGreen - this.mMinGreen;
            int blueLength = this.mMaxBlue - this.mMinBlue;
            if (redLength >= greenLength && redLength >= blueLength) {
                return -3;
            }
            if (greenLength < redLength || greenLength < blueLength) {
                return -1;
            }
            return -2;
        }

        /* Access modifiers changed, original: final */
        public final int findSplitPoint() {
            int longestDimension = getLongestColorDimension();
            int[] colors = ColorCutQuantizer.this.mColors;
            int[] hist = ColorCutQuantizer.this.mHistogram;
            ColorCutQuantizer.modifySignificantOctet(colors, longestDimension, this.mLowerIndex, this.mUpperIndex);
            Arrays.sort(colors, this.mLowerIndex, this.mUpperIndex + 1);
            ColorCutQuantizer.modifySignificantOctet(colors, longestDimension, this.mLowerIndex, this.mUpperIndex);
            int midPoint = this.mPopulation / 2;
            int i = this.mLowerIndex;
            int count = 0;
            while (true) {
                int i2 = this.mUpperIndex;
                if (i > i2) {
                    return this.mLowerIndex;
                }
                count += hist[colors[i]];
                if (count >= midPoint) {
                    return Math.min(i2 - 1, i);
                }
                i++;
            }
        }

        /* Access modifiers changed, original: final */
        public final Swatch getAverageColor() {
            int[] colors = ColorCutQuantizer.this.mColors;
            int[] hist = ColorCutQuantizer.this.mHistogram;
            int redSum = 0;
            int greenSum = 0;
            int blueSum = 0;
            int totalPopulation = 0;
            for (int i = this.mLowerIndex; i <= this.mUpperIndex; i++) {
                int color = colors[i];
                int colorPopulation = hist[color];
                totalPopulation += colorPopulation;
                redSum += ColorCutQuantizer.quantizedRed(color) * colorPopulation;
                greenSum += ColorCutQuantizer.quantizedGreen(color) * colorPopulation;
                blueSum += ColorCutQuantizer.quantizedBlue(color) * colorPopulation;
            }
            return new Swatch(ColorCutQuantizer.approximateToRgb888(Math.round(((float) redSum) / ((float) totalPopulation)), Math.round(((float) greenSum) / ((float) totalPopulation)), Math.round(((float) blueSum) / ((float) totalPopulation))), totalPopulation);
        }
    }

    ColorCutQuantizer() {
    }

    public void quantize(int[] pixels, int maxColors, Filter[] filters) {
        int i;
        int quantizedColor;
        int i2;
        int color;
        int distinctColorIndex;
        this.mTimingLogger = null;
        this.mFilters = filters;
        int[] hist = new int[32768];
        this.mHistogram = hist;
        for (i = 0; i < pixels.length; i++) {
            quantizedColor = quantizeFromRgb888(pixels[i]);
            pixels[i] = quantizedColor;
            hist[quantizedColor] = hist[quantizedColor] + 1;
        }
        i = 0;
        quantizedColor = 0;
        while (true) {
            i2 = 0;
            if (quantizedColor >= hist.length) {
                break;
            }
            if (hist[quantizedColor] > 0 && shouldIgnoreColor(quantizedColor)) {
                hist[quantizedColor] = 0;
            }
            if (hist[quantizedColor] > 0) {
                i++;
            }
            quantizedColor++;
        }
        int[] colors = new int[i];
        this.mColors = colors;
        int distinctColorIndex2 = 0;
        for (color = 0; color < hist.length; color++) {
            if (hist[color] > 0) {
                distinctColorIndex = distinctColorIndex2 + 1;
                colors[distinctColorIndex2] = color;
                distinctColorIndex2 = distinctColorIndex;
            }
        }
        if (i <= maxColors) {
            this.mQuantizedColors = new ArrayList();
            color = colors.length;
            while (i2 < color) {
                distinctColorIndex = colors[i2];
                this.mQuantizedColors.add(new Swatch(approximateToRgb888(distinctColorIndex), hist[distinctColorIndex]));
                i2++;
            }
            return;
        }
        this.mQuantizedColors = quantizePixels(maxColors);
    }

    public List<Swatch> getQuantizedColors() {
        return this.mQuantizedColors;
    }

    private List<Swatch> quantizePixels(int maxColors) {
        PriorityQueue<Vbox> pq = new PriorityQueue(maxColors, VBOX_COMPARATOR_VOLUME);
        pq.offer(new Vbox(0, this.mColors.length - 1));
        splitBoxes(pq, maxColors);
        return generateAverageColors(pq);
    }

    private void splitBoxes(PriorityQueue<Vbox> queue, int maxSize) {
        while (queue.size() < maxSize) {
            Vbox vbox = (Vbox) queue.poll();
            if (vbox != null && vbox.canSplit()) {
                queue.offer(vbox.splitBox());
                queue.offer(vbox);
            } else {
                return;
            }
        }
    }

    private List<Swatch> generateAverageColors(Collection<Vbox> vboxes) {
        ArrayList<Swatch> colors = new ArrayList(vboxes.size());
        for (Vbox vbox : vboxes) {
            Swatch swatch = vbox.getAverageColor();
            if (!shouldIgnoreColor(swatch)) {
                colors.add(swatch);
            }
        }
        return colors;
    }

    static void modifySignificantOctet(int[] a, int dimension, int lower, int upper) {
        if (dimension == -3) {
            return;
        }
        int i;
        int color;
        if (dimension == -2) {
            for (i = lower; i <= upper; i++) {
                color = a[i];
                a[i] = ((quantizedGreen(color) << 10) | (quantizedRed(color) << 5)) | quantizedBlue(color);
            }
        } else if (dimension == -1) {
            for (i = lower; i <= upper; i++) {
                color = a[i];
                a[i] = ((quantizedBlue(color) << 10) | (quantizedGreen(color) << 5)) | quantizedRed(color);
            }
        }
    }

    private boolean shouldIgnoreColor(int color565) {
        int rgb = approximateToRgb888(color565);
        ColorUtils.colorToHSL(rgb, this.mTempHsl);
        return shouldIgnoreColor(rgb, this.mTempHsl);
    }

    private boolean shouldIgnoreColor(Swatch color) {
        return shouldIgnoreColor(color.getRgb(), color.getHsl());
    }

    private boolean shouldIgnoreColor(int rgb, float[] hsl) {
        int count = this.mFilters;
        if (count != 0 && count.length > 0) {
            count = count.length;
            for (int i = 0; i < count; i++) {
                if (!this.mFilters[i].isAllowed(rgb, hsl)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static int quantizeFromRgb888(int color) {
        int r = modifyWordWidth(Color.red(color), 8, 5);
        int g = modifyWordWidth(Color.green(color), 8, 5);
        return ((r << 10) | (g << 5)) | modifyWordWidth(Color.blue(color), 8, 5);
    }

    static int approximateToRgb888(int r, int g, int b) {
        return Color.rgb(modifyWordWidth(r, 5, 8), modifyWordWidth(g, 5, 8), modifyWordWidth(b, 5, 8));
    }

    private static int approximateToRgb888(int color) {
        return approximateToRgb888(quantizedRed(color), quantizedGreen(color), quantizedBlue(color));
    }

    static int quantizedRed(int color) {
        return (color >> 10) & 31;
    }

    static int quantizedGreen(int color) {
        return (color >> 5) & 31;
    }

    static int quantizedBlue(int color) {
        return color & 31;
    }

    private static int modifyWordWidth(int value, int currentWidth, int targetWidth) {
        int newValue;
        if (targetWidth > currentWidth) {
            newValue = value << (targetWidth - currentWidth);
        } else {
            newValue = value >> (currentWidth - targetWidth);
        }
        return newValue & ((1 << targetWidth) - 1);
    }
}
