package com.github.rzabini.config

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.logging.Logger

/**
 * implementing classes will be able to auto load their configuration according to a predefined concevtion
 * <p>
 *     The object searches for a file named <group>.groovy in a directory 'config', searching upward in the path
 * </p>
 *
 */
trait AutoConfig {

    String group
    String env
    ConfigObject config
    Path workingDir
    Path configDir
    private static final String DEFAULT_CONFIG_NAME = 'config'
    //final static Logger LOG = Logger.getLogger(AutoConfig)

    Path getWorkingDir() {
        workingDir ?: Paths.get(System.getProperty('user.dir'))
    }

    void setWorkingDir(Path workingDir) {
        this.workingDir = workingDir
    }

    Path findConfigDir(Path baseDir) {
        //LOG.info "searching in $baseDir"
        if (!baseDir) {
            return null
        }
        Path candidate= baseDir.resolve(DEFAULT_CONFIG_NAME)

        if (Files.isDirectory(candidate)) {
            return candidate
        }

        findConfigDir(baseDir.parent)

    }

    void readBaseConfig() {
        configDir = configDir ?: this.findConfigDir(workingDir)
        assert configDir != null, 'cannot find config dir'
        //LOG.info "found config dir: $configDir"
        Path configFile = configDir.resolve("${group}.groovy")
        assert Files.exists(configFile), "cannot find $configFile"
        this.config = new ConfigSlurper(env).parse(configFile.text)
    }

    void setGroup(String group) {
        this.group = group
    }

    void setEnv(String env) {
        this.env = env
    }

    ConfigObject getConfig() {
        if (!config) {
            readBaseConfig()
        }
        config
    }

    Object propertyMissing(String property) {
        return getConfig().get(property)
    }
}
