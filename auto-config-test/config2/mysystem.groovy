foo='bar'

ssh {
    host='another'
}

environments{
    prod{
        ssh {
            host='production-host'
        }
    }
}