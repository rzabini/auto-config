package com.github.rzabini.config

import com.github.rzabini.config.AutoConfig
import com.google.common.jimfs.Configuration
import com.google.common.jimfs.Jimfs
import org.junit.Rule
import org.slf4j.Logger
import spock.lang.Specification

import java.nio.file.FileSystem
import java.nio.file.Files
import java.nio.file.Path

class AutoConfigSpecification extends Specification {

    FileSystem fs = Jimfs.newFileSystem(Configuration.unix())
    Path root

    private Logger logger = Mock(Logger.class)
    @Rule ReplaceSlf4jLogger replaceSlf4jLogger = new ReplaceSlf4jLogger(TestAutoConfig, logger)



    def setup(){
        root = fs.getPath("/")
        Files.createDirectory(fs.getPath('/base'))
        Files.createDirectory(fs.getPath('/base/configParent'))
        Files.createDirectory(fs.getPath('/base/configParent/config'))
        Files.createDirectory(fs.getPath('/base/configParent/configSibling'))
        Files.createFile(fs.getPath('/base/configParent/config/mysystem.groovy')).text = 'foo="bar"'
    }

    def "throws exception if cannot find config directory"(){
        when:
        TestAutoConfig testAutoConfig=new TestAutoConfig(workingDir: root)
        testAutoConfig.config

        then:
        def ex = thrown(AssertionError)
        ex.message.startsWith 'cannot find config dir'
        1 * logger.info('searching in /')
    }

    def "can search its configuration"(){
        when:
        TestAutoConfig testAutoConfig=new TestAutoConfig(workingDir: fs.getPath('/base/configParent'))
        def val = testAutoConfig.config.foo

        then:
        val == 'bar'
        1 * logger.info('searching in /base/configParent')
        1 * logger.info('found config dir: /base/configParent/config')
    }

    def "can read a structured configuration"(){
        fs.getPath('/base/configParent/config/mysystem.groovy').text = 'ssh { host="host"}'

        when:
        TestAutoConfig testAutoConfig=new TestAutoConfig(workingDir: fs.getPath('/base/configParent'))
        def val = testAutoConfig.config.ssh.host

        then:
        val == 'host'
        1 * logger.info('searching in /base/configParent')
    }

    def "can read environment configuration"(){
        fs.getPath('/base/configParent/config/mysystem.groovy').text = '''
environments{ prod{
        ssh {
            host='production-host'
        }
    }
}'''
        given:
        TestAutoConfig prodAutoConfig=new TestAutoConfig(workingDir: fs.getPath('/base/configParent'), env:'prod')

        expect:
        prodAutoConfig.config.ssh.host == 'production-host'
    }

    def "property defaults to config"(){
        fs.getPath('/base/configParent/config/mysystem.groovy').text = 'ssh { host="host"}'
        given:
        TestAutoConfig testAutoConfig=new TestAutoConfig(workingDir: fs.getPath('/base/configParent'))

        expect:
        testAutoConfig.ssh.host== 'host'
    }

    def "undefined property is null"(){
        given:
        TestAutoConfig testAutoConfig=new TestAutoConfig(workingDir: fs.getPath('/base/configParent'))

        expect:
        testAutoConfig.undefined == null

    }


    def "can read configuration with non standard name"(){
        fs.getPath('/base/configParent/config/second.groovy').text = 'ssh { host="second"}'

        given:
        TestAutoConfig testAutoConfig=new TestAutoConfig(workingDir: fs.getPath('/base/configParent'), group: 'second')

        expect:
        testAutoConfig.ssh.host== 'second'

    }

}
