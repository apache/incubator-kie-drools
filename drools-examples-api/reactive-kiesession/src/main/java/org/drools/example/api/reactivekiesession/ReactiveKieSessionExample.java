package org.drools.example.api.reactivekiesession;

import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import java.util.ArrayList;
import java.util.List;

public class ReactiveKieSessionExample {

    public static void main(String[] args) {
        new ReactiveKieSessionExample().go();
    }

    public void go() {
        KieServices ks = KieServices.Factory.get();
        KieContainer kContainer = ks.getKieClasspathContainer();

        KieSession ksession = kContainer.newKieSession();

        List<String> list = new ArrayList<>();
        ksession.setGlobal( "list", list );

        ksession.fireAllRules();

        System.out.println(list);

        list.clear();

        ksession.insert("Debbie");
        ksession.fireAllRules();

        System.out.println( list );
    }
}
