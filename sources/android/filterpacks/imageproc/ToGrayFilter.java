package android.filterpacks.imageproc;

import android.app.slice.SliceItem;
import android.filterfw.core.FilterContext;
import android.filterfw.core.GenerateFieldPort;
import android.filterfw.core.MutableFrameFormat;
import android.filterfw.core.Program;
import android.filterfw.core.ShaderProgram;
import android.filterfw.format.ImageFormat;

public class ToGrayFilter extends SimpleImageFilter {
    private static final String mColorToGray4Shader = "precision mediump float;\nuniform sampler2D tex_sampler_0;\nvarying vec2 v_texcoord;\nvoid main() {\n  vec4 color = texture2D(tex_sampler_0, v_texcoord);\n  float y = dot(color, vec4(0.299, 0.587, 0.114, 0));\n  gl_FragColor = vec4(y, y, y, color.a);\n}\n";
    @GenerateFieldPort(hasDefault = true, name = "invertSource")
    private boolean mInvertSource = false;
    private MutableFrameFormat mOutputFormat;
    @GenerateFieldPort(hasDefault = true, name = "tile_size")
    private int mTileSize = 640;

    public ToGrayFilter(String name) {
        super(name, null);
    }

    public void setupPorts() {
        MutableFrameFormat create = ImageFormat.create(3, 3);
        String str = SliceItem.FORMAT_IMAGE;
        addMaskedInputPort(str, create);
        addOutputBasedOnInput(str, str);
    }

    /* Access modifiers changed, original: protected */
    public Program getNativeProgram(FilterContext context) {
        throw new RuntimeException("Native toGray not implemented yet!");
    }

    /* Access modifiers changed, original: protected */
    public Program getShaderProgram(FilterContext context) {
        int inputChannels = getInputFormat(SliceItem.FORMAT_IMAGE).getBytesPerSample();
        if (inputChannels == 4) {
            ShaderProgram program = new ShaderProgram(context, mColorToGray4Shader);
            program.setMaximumTileSize(this.mTileSize);
            if (this.mInvertSource) {
                program.setSourceRect(0.0f, 1.0f, 1.0f, -1.0f);
            }
            return program;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Unsupported GL input channels: ");
        stringBuilder.append(inputChannels);
        stringBuilder.append("! Channels must be 4!");
        throw new RuntimeException(stringBuilder.toString());
    }
}
