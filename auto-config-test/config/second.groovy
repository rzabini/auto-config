foo='bar'

ssh {
    host='second'
}

environments{
    prod{
        ssh {
            host='production-host'
        }
    }
}