package com.github.rzabini.config

trait AutoConfig {

    String group
    String env
    def config
    File baseDir = new File(System.getProperty("user.dir"))
    File configDir

    File findConfigDir(File baseDir){
        if (baseDir==null)
            return null
        File candidate=new File(baseDir, 'config')

        if (candidate.isDirectory() )
            return candidate
        else
            findConfigDir(baseDir.parentFile)
    }

    def readBaseConfig(){
        if(!configDir)
            configDir = findConfigDir(baseDir)
        assert configDir != null, "cannot find config dir"
        def configFile = new File(configDir, "${group}.groovy")
        assert configFile.exists(), "cannot find $configFile"
        new ConfigSlurper(env).parse(
                configFile.toURI().toURL())
    }

    void setGroup(String group){
        this.group=group
        this.config=readBaseConfig()
    }

    void setEnv(String env){
        this.env=env
        this.config=readBaseConfig()
    }

    void setConfigDir(configDir){
        this.configDir= configDir instanceof File?configDir
                :new File(configDir)
        this.config=readBaseConfig()
    }


    def propertyMissing(String property){
        return config.get(property)
    }

}