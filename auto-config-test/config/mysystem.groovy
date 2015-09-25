foo='bar'

ssh {
    host='host'
}

environments{
    prod{
        ssh {
            host='production-host'
        }
    }
}