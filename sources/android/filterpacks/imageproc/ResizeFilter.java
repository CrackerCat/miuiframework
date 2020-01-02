package android.filterpacks.imageproc;

import android.app.slice.SliceItem;
import android.filterfw.core.Filter;
import android.filterfw.core.FilterContext;
import android.filterfw.core.Frame;
import android.filterfw.core.FrameFormat;
import android.filterfw.core.GLFrame;
import android.filterfw.core.GenerateFieldPort;
import android.filterfw.core.MutableFrameFormat;
import android.filterfw.core.Program;
import android.filterfw.core.ShaderProgram;
import android.filterfw.format.ImageFormat;

public class ResizeFilter extends Filter {
    @GenerateFieldPort(hasDefault = true, name = "generateMipMap")
    private boolean mGenerateMipMap = false;
    private int mInputChannels;
    @GenerateFieldPort(hasDefault = true, name = "keepAspectRatio")
    private boolean mKeepAspectRatio = false;
    private FrameFormat mLastFormat = null;
    @GenerateFieldPort(name = "oheight")
    private int mOHeight;
    @GenerateFieldPort(name = "owidth")
    private int mOWidth;
    private MutableFrameFormat mOutputFormat;
    private Program mProgram;

    public ResizeFilter(String name) {
        super(name);
    }

    public void setupPorts() {
        MutableFrameFormat create = ImageFormat.create(3);
        String str = SliceItem.FORMAT_IMAGE;
        addMaskedInputPort(str, create);
        addOutputBasedOnInput(str, str);
    }

    public FrameFormat getOutputFormat(String portName, FrameFormat inputFormat) {
        return inputFormat;
    }

    /* Access modifiers changed, original: protected */
    public void createProgram(FilterContext context, FrameFormat format) {
        FrameFormat frameFormat = this.mLastFormat;
        if (frameFormat == null || frameFormat.getTarget() != format.getTarget()) {
            this.mLastFormat = format;
            int target = format.getTarget();
            if (target == 2) {
                throw new RuntimeException("Native ResizeFilter not implemented yet!");
            } else if (target == 3) {
                this.mProgram = ShaderProgram.createIdentity(context);
            } else {
                throw new RuntimeException("ResizeFilter could not create suitable program!");
            }
        }
    }

    public void process(FilterContext env) {
        String str = SliceItem.FORMAT_IMAGE;
        Frame input = pullInput(str);
        createProgram(env, input.getFormat());
        MutableFrameFormat outputFormat = input.getFormat().mutableCopy();
        if (this.mKeepAspectRatio) {
            FrameFormat inputFormat = input.getFormat();
            this.mOHeight = (this.mOWidth * inputFormat.getHeight()) / inputFormat.getWidth();
        }
        outputFormat.setDimensions(this.mOWidth, this.mOHeight);
        Frame output = env.getFrameManager().newFrame(outputFormat);
        if (this.mGenerateMipMap) {
            Frame mipmapped = (GLFrame) env.getFrameManager().newFrame(input.getFormat());
            mipmapped.setTextureParameter(10241, 9985);
            mipmapped.setDataFromFrame(input);
            mipmapped.generateMipMap();
            this.mProgram.process(mipmapped, output);
            mipmapped.release();
        } else {
            this.mProgram.process(input, output);
        }
        pushOutput(str, output);
        output.release();
    }
}
