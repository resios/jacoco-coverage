package org.jacoco.coverage.execution;

import com.intellij.execution.Executor;
import com.intellij.execution.configurations.JavaParameters;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.executors.DefaultRunExecutor;
import com.intellij.execution.junit.JUnitConfiguration;
import com.intellij.execution.runners.JavaProgramPatcher;
import com.intellij.util.PathUtil;
import com.theoryinpractice.testng.configuration.TestNGConfiguration;
import org.jacoco.coverage.config.CoverageConfig;
import org.jacoco.coverage.util.CoverageUtils;

import java.io.File;

public class JaCoCoCoveragePatcher extends JavaProgramPatcher {

    private boolean append = true;
    private static final String JACOCO_AGENT_PATH = "jacoco.agent.path";

    @Override
    public void patchJavaParameters(Executor executor, RunProfile configuration, JavaParameters javaParameters) {
        if (!executor.getId().equals(DefaultRunExecutor.EXECUTOR_ID)) {
            return;
        }

        if ((configuration instanceof RunConfiguration)) {
            if (!isTestConfiguration(configuration)) {
                return;
            }

            RunConfiguration runConfiguration = (RunConfiguration) configuration;

            CoverageConfig instance = CoverageConfig.getInstance(runConfiguration.getProject());
            if (instance != null && instance.getState() != null && instance.getState().isEnabled) {
                javaParameters.getVMParametersList().add(computeJaCoCoParams(runConfiguration));
            }
        }
    }

    private boolean isTestConfiguration(RunProfile configuration) {
        return (configuration instanceof JUnitConfiguration) || (configuration instanceof TestNGConfiguration);
    }

    private String computeJaCoCoParams(RunConfiguration runConfiguration) {
        return String.format(
                "-javaagent:%s=destfile=%s,append=%s",
                getAgentJarPath(),
                computeCoverageDataFile(runConfiguration),
                String.valueOf(append));
    }

    private String computeCoverageDataFile(RunConfiguration configuration) {
        return CoverageUtils.getDefaultCoverageFile(configuration.getProject());
    }

    private static String getAgentJarPath() {
        final String userDefined = System.getProperty(JACOCO_AGENT_PATH);
        if (userDefined != null && new File(userDefined).exists()) {
            return userDefined;
        }

        String agentPath = PathUtil.getJarPathForClass(JaCoCoCoveragePatcher.class);
        final File ourJar = new File(agentPath);
        final File pluginDir = ourJar.getParentFile();
        File pathInLib = new File(new File(pluginDir,"lib"), "jacocoagent.jar");
        File pathInJar = new File(pluginDir,"jacocoagent.jar");
        return pathInJar.exists() ? pathInJar.getPath() : pathInLib.getPath();
    }
}
