package test.com.github.rzabini.config

import com.github.rzabini.config.AutoConfig
import spock.lang.Specification

class AutoConfigSpecification extends Specification {

    def "class can search its configuration"(){
        given:
        TestAutoConfig testAutoConfig=new TestAutoConfig()

        expect:
        testAutoConfig.config.foo == 'bar'
    }

    def "can read a structured configuration"(){
        given:
        TestAutoConfig testAutoConfig=new TestAutoConfig()

        expect:
        testAutoConfig.config.ssh.host == 'host'
    }

    def "can read environment configuration"(){
        given:
        TestAutoConfig prodAutoConfig=new TestAutoConfig(env:'prod')

        expect:
        prodAutoConfig.config.ssh.host == 'production-host'
    }

    def "property defaults to config"(){
        given:
        TestAutoConfig testAutoConfig=new TestAutoConfig()

        expect:
        testAutoConfig.ssh.host== 'host'

    }

    def "can read configuration with non standard name"(){
        given:
        TestAutoConfig testAutoConfig=new TestAutoConfig(name: 'second')

        expect:
        testAutoConfig.ssh.host== 'second'

    }

    def "can read configuration given the config dir"(){
        given:
        TestAutoConfig testAutoConfig=new TestAutoConfig(configDir: new File('config2'))

        expect:
        testAutoConfig.ssh.host== 'another'

    }

    def "can read configuration given the config dir as string"(){
        given:
        TestAutoConfig testAutoConfig=new TestAutoConfig(configDir: 'config2')

        expect:
        testAutoConfig.ssh.host== 'another'

    }


}
