package org.jacoco.coverage.config;

import com.google.common.base.Objects;
import com.intellij.openapi.components.*;
import com.intellij.openapi.project.Project;
import org.jacoco.coverage.util.CoverageUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(
        name = CoverageUtils.PLUGIN_TITLE,
        storages = {
                @Storage(id = "default", file = "$PROJECT_FILE$"),
                @Storage(id = "dir", file = "$PROJECT_CONFIG_DIR$/jacoco_coverage.xml", scheme = StorageScheme.DIRECTORY_BASED)
        }
)
public class CoverageConfig implements ProjectComponent, PersistentStateComponent<CoverageConfig.State> {

    public static class State {
        public boolean isEnabled = false;
        public String coveragePath;

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }

            if (!getClass().equals(obj.getClass())) {
                return false;
            }

            State that = (State) obj;

            return Objects.equal(this.isEnabled, that.isEnabled) &&
                    Objects.equal(this.coveragePath, that.coveragePath);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(isEnabled, coveragePath);
        }
    }

    private State currentState = new State();

    public CoverageConfig() {
    }

    @Override
    public void projectOpened() {
    }

    @Override
    public void projectClosed() {

    }

    @Override
    public void initComponent() {

    }

    @Override
    public void disposeComponent() {

    }

    @NotNull
    @Override
    public String getComponentName() {
        return CoverageConfig.class.getName();
    }

    public static CoverageConfig getInstance(Project project) {
        return project.getComponent(CoverageConfig.class);
    }

    public boolean isEnabled(){
        State state = getState();
        return state != null && state.isEnabled;
    }

    public @Nullable String getCoveragePath(){
        State state = getState();
        if(state == null){
            return null;
        } else {
            return state.coveragePath;
        }
    }

    @Nullable
    @Override
    public State getState() {
        return currentState;
    }

    @Override
    public void loadState(State state) {
        currentState = state;
    }

}
