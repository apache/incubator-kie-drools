deployment(name:'sessionService') {
    groups 'rio'

    //resources id:'impl.jars', 'sparkplug_1.0-SNAPSHOT/lib/sparkplug-oar-1.0-SNAPSHOT-impl.jar'
    //resources id:'client.jars', 'sparkplug_1.0-SNAPSHOT/lib/sparkplug-oar-1.0-SNAPSHOT-dl.jar'

   
    service(name: 'SessionService') {
        interfaces {
            classes 'org.drools.vsm.rio.SessionService'
            //resources ref:'client.jars'
        }
        implementation(class:'org.drools.vsm.rio.service.SessionServiceImpl') {
            //resources ref:'impl.jars'
        }

        maintain 1
    }
    
    
}

