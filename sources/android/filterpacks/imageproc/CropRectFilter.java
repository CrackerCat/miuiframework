package android.filterpacks.imageproc;

import android.app.slice.SliceItem;
import android.filterfw.core.Filter;
import android.filterfw.core.FilterContext;
import android.filterfw.core.Frame;
import android.filterfw.core.FrameFormat;
import android.filterfw.core.GenerateFieldPort;
import android.filterfw.core.MutableFrameFormat;
import android.filterfw.core.Program;
import android.filterfw.core.ShaderProgram;
import android.filterfw.format.ImageFormat;

public class CropRectFilter extends Filter {
    private int mHeight = 0;
    @GenerateFieldPort(name = "height")
    private int mOutputHeight;
    @GenerateFieldPort(name = "width")
    private int mOutputWidth;
    private Program mProgram;
    private int mTarget = 0;
    @GenerateFieldPort(hasDefault = true, name = "tile_size")
    private int mTileSize = 640;
    private int mWidth = 0;
    @GenerateFieldPort(name = "xorigin")
    private int mXorigin;
    @GenerateFieldPort(name = "yorigin")
    private int mYorigin;

    public CropRectFilter(String name) {
        super(name);
    }

    public void setupPorts() {
        MutableFrameFormat create = ImageFormat.create(3);
        String str = SliceItem.FORMAT_IMAGE;
        addMaskedInputPort(str, create);
        addOutputBasedOnInput(str, str);
    }

    public void initProgram(FilterContext context, int target) {
        if (target == 3) {
            ShaderProgram shaderProgram = ShaderProgram.createIdentity(context);
            shaderProgram.setMaximumTileSize(this.mTileSize);
            this.mProgram = shaderProgram;
            this.mTarget = target;
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Filter Sharpen does not support frames of target ");
        stringBuilder.append(target);
        stringBuilder.append("!");
        throw new RuntimeException(stringBuilder.toString());
    }

    public void fieldPortValueUpdated(String name, FilterContext context) {
        if (this.mProgram != null) {
            updateSourceRect(this.mWidth, this.mHeight);
        }
    }

    public void process(FilterContext context) {
        String str = SliceItem.FORMAT_IMAGE;
        Frame input = pullInput(str);
        FrameFormat inputFormat = input.getFormat();
        Frame output = context.getFrameManager().newFrame(ImageFormat.create(this.mOutputWidth, this.mOutputHeight, 3, 3));
        if (this.mProgram == null || inputFormat.getTarget() != this.mTarget) {
            initProgram(context, inputFormat.getTarget());
        }
        if (!(inputFormat.getWidth() == this.mWidth && inputFormat.getHeight() == this.mHeight)) {
            updateSourceRect(inputFormat.getWidth(), inputFormat.getHeight());
        }
        this.mProgram.process(input, output);
        pushOutput(str, output);
        output.release();
    }

    /* Access modifiers changed, original: 0000 */
    public void updateSourceRect(int width, int height) {
        this.mWidth = width;
        this.mHeight = height;
        ShaderProgram shaderProgram = (ShaderProgram) this.mProgram;
        float f = (float) this.mXorigin;
        int i = this.mWidth;
        f /= (float) i;
        float f2 = (float) this.mYorigin;
        int i2 = this.mHeight;
        shaderProgram.setSourceRect(f, f2 / ((float) i2), ((float) this.mOutputWidth) / ((float) i), ((float) this.mOutputHeight) / ((float) i2));
    }
}
