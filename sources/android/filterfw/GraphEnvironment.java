package android.filterfw;

import android.annotation.UnsupportedAppUsage;
import android.content.Context;
import android.filterfw.core.AsyncRunner;
import android.filterfw.core.FilterContext;
import android.filterfw.core.FilterGraph;
import android.filterfw.core.FrameManager;
import android.filterfw.core.GraphRunner;
import android.filterfw.core.RoundRobinScheduler;
import android.filterfw.core.SyncRunner;
import android.filterfw.io.GraphIOException;
import android.filterfw.io.GraphReader;
import android.filterfw.io.TextGraphReader;
import java.util.ArrayList;

public class GraphEnvironment extends MffEnvironment {
    public static final int MODE_ASYNCHRONOUS = 1;
    public static final int MODE_SYNCHRONOUS = 2;
    private GraphReader mGraphReader;
    private ArrayList<GraphHandle> mGraphs = new ArrayList();

    private class GraphHandle {
        private AsyncRunner mAsyncRunner;
        private FilterGraph mGraph;
        private SyncRunner mSyncRunner;

        public GraphHandle(FilterGraph graph) {
            this.mGraph = graph;
        }

        public FilterGraph getGraph() {
            return this.mGraph;
        }

        public AsyncRunner getAsyncRunner(FilterContext environment) {
            if (this.mAsyncRunner == null) {
                this.mAsyncRunner = new AsyncRunner(environment, RoundRobinScheduler.class);
                this.mAsyncRunner.setGraph(this.mGraph);
            }
            return this.mAsyncRunner;
        }

        public GraphRunner getSyncRunner(FilterContext environment) {
            if (this.mSyncRunner == null) {
                this.mSyncRunner = new SyncRunner(environment, this.mGraph, RoundRobinScheduler.class);
            }
            return this.mSyncRunner;
        }
    }

    @UnsupportedAppUsage
    public GraphEnvironment() {
        super(null);
    }

    public GraphEnvironment(FrameManager frameManager, GraphReader reader) {
        super(frameManager);
        this.mGraphReader = reader;
    }

    public GraphReader getGraphReader() {
        if (this.mGraphReader == null) {
            this.mGraphReader = new TextGraphReader();
        }
        return this.mGraphReader;
    }

    @UnsupportedAppUsage
    public void addReferences(Object... references) {
        getGraphReader().addReferencesByKeysAndValues(references);
    }

    @UnsupportedAppUsage
    public int loadGraph(Context context, int resourceId) {
        try {
            return addGraph(getGraphReader().readGraphResource(context, resourceId));
        } catch (GraphIOException e) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Could not read graph: ");
            stringBuilder.append(e.getMessage());
            throw new RuntimeException(stringBuilder.toString());
        }
    }

    public int addGraph(FilterGraph graph) {
        this.mGraphs.add(new GraphHandle(graph));
        return this.mGraphs.size() - 1;
    }

    public FilterGraph getGraph(int graphId) {
        if (graphId >= 0 && graphId < this.mGraphs.size()) {
            return ((GraphHandle) this.mGraphs.get(graphId)).getGraph();
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Invalid graph ID ");
        stringBuilder.append(graphId);
        stringBuilder.append(" specified in runGraph()!");
        throw new IllegalArgumentException(stringBuilder.toString());
    }

    @UnsupportedAppUsage
    public GraphRunner getRunner(int graphId, int executionMode) {
        if (executionMode == 1) {
            return ((GraphHandle) this.mGraphs.get(graphId)).getAsyncRunner(getContext());
        }
        if (executionMode == 2) {
            return ((GraphHandle) this.mGraphs.get(graphId)).getSyncRunner(getContext());
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Invalid execution mode ");
        stringBuilder.append(executionMode);
        stringBuilder.append(" specified in getRunner()!");
        throw new RuntimeException(stringBuilder.toString());
    }
}
